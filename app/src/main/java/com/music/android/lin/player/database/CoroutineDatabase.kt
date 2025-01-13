package com.music.android.lin.player.database

import android.annotation.SuppressLint
import androidx.room.InvalidationTracker
import androidx.room.RoomDatabase
import androidx.room.getQueryDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

object CoroutineDatabase {

    private val RoomDatabase.transactionDispatcher: CoroutineDispatcher
        get() = backingFieldMap.getOrPut("TransactionDispatcher") {
            transactionExecutor.asCoroutineDispatcher()
        } as CoroutineDispatcher

    @SuppressLint("RestrictedApi")
    @JvmStatic
    fun <R> createFlow(
        db: RoomDatabase,
        inTransaction: Boolean,
        tableNames: Array<String>,
        suspendCall: suspend () -> R,
    ): Flow<@JvmSuppressWildcards R> = flow {
        coroutineScope {
            // Observer channel receives signals from the invalidation tracker to emit queries.
            val observerChannel = Channel<Unit>(Channel.CONFLATED)
            val observer = object : InvalidationTracker.Observer(tableNames) {
                override fun onInvalidated(tables: Set<String>) {
                    observerChannel.trySend(Unit)
                }
            }
            observerChannel.trySend(Unit) // Initial signal to perform first query.
            val queryContext =
                if (inTransaction) db.transactionDispatcher else db.getQueryDispatcher()
            val resultChannel = Channel<R>()
            launch(queryContext) {
                db.invalidationTracker.addObserver(observer)
                try {
                    // Iterate until cancelled, transforming observer signals to query results
                    // to be emitted to the flow.
                    for (signal in observerChannel) {
                        val result = suspendCall()
                        resultChannel.send(result)
                    }
                } finally {
                    db.invalidationTracker.removeObserver(observer)
                }
            }

            emitAll(resultChannel)
        }
    }
}