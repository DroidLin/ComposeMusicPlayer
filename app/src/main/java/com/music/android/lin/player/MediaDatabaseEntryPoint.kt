package com.harvest.musicplayer

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * @author liuzhongao
 * @since 2024/4/6 11:44
 */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface MediaDatabaseEntryPoint {

    val mediaRepository: MediaRepository
}