package com.music.android.lin.application.music.play.ui.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.animation.core.animateDecay
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.MutatorMutex
import androidx.compose.foundation.gestures.DragScope
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.music.android.lin.application.music.play.model.LyricOutput
import com.music.android.lin.application.music.play.model.SimpleLineLyricOutput
import com.music.android.lin.application.music.play.ui.util.binarySearchLine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.util.concurrent.atomic.AtomicInteger


class LyricViewState : DraggableState {

    private var lyricViewYOffset by mutableFloatStateOf(0f)
    private var isDragging by mutableStateOf(false)

    private val flingInteger = AtomicInteger()

    private val dragMutex = MutatorMutex()
    private val dragScope = object : DragScope {
        override fun dragBy(pixels: Float) {
            dispatchRawDelta(pixels)
        }
    }

    var yOffset: Float
        get() {
            return lyricViewYOffset
        }
        private set(value) {
            lyricViewYOffset = value
        }

    override fun dispatchRawDelta(delta: Float) {
        yOffset += delta
    }

    override suspend fun drag(dragPriority: MutatePriority, block: suspend DragScope.() -> Unit) {
        coroutineScope {
            isDragging = true
            dragMutex.mutateWith(dragScope, dragPriority, block)
            isDragging = false
        }
    }

    suspend fun onDragStarted(coroutineScope: CoroutineScope, startedOffset: Offset) {
        do {
            val value = flingInteger.get()
        } while (!flingInteger.compareAndSet(value, 0))
    }

    suspend fun onDragStopped(coroutineScope: CoroutineScope, velocity: Float) {
        coroutineScope {
            val token = flingInteger.incrementAndGet()
            val animationSpec = exponentialDecay<Float>(
                frictionMultiplier = 2f,
                absVelocityThreshold = 5f
            )
            AnimationState(
                initialValue = yOffset,
                initialVelocity = velocity,
            ).animateDecay(animationSpec) {
                val currentToken = flingInteger.get()
                if (token == currentToken) {
                    yOffset = this.value
                } else this.cancelAnimation()
            }
        }
    }

}

@Composable
fun rememberLyricViewState(): LyricViewState {
    return remember {
        LyricViewState()
    }
}


@Composable
fun LyricView(
    currentPosition: State<Long>,
    lyricOutput: State<LyricOutput?>,
    lyricViewState: LyricViewState = rememberLyricViewState(),
    modifier: Modifier = Modifier,
) {
    when (val output = lyricOutput.value) {
        is SimpleLineLyricOutput -> SimpleLineLyricView(
            currentPosition = currentPosition,
            modifier = modifier,
            lyricOutput = output
        )

        else -> Box(modifier = modifier)
    }
}

private val TextUnitConverter = TwoWayConverter<TextUnit, AnimationVector1D>(
    convertFromVector = { it.value.sp },
    convertToVector = { AnimationVector1D(it.value) }
)

@Composable
private fun SimpleLineLyricView(
    currentPosition: State<Long>,
    lyricOutput: SimpleLineLyricOutput,
    lyricViewState: LyricViewState = rememberLyricViewState(),
    modifier: Modifier = Modifier,
) {
    val textMeasurer = rememberTextMeasurer()
    val titleMedium = MaterialTheme.typography.titleMedium.copy(
        color = LocalContentColor.current,
    )
    val headlineMedium = MaterialTheme.typography.headlineMedium.copy(
        color = LocalContentColor.current,
    )

    val lyricLineAnimation = remember(lyricOutput) {
        lyricOutput.lyricEntities.map { line ->
            Animatable(initialValue = 1f)
        }.toMutableList()
    }

    val currentLine = remember {
        derivedStateOf {
            lyricOutput.lyricEntities.binarySearchLine(currentPosition.value)
        }
    }

    LaunchedEffect(currentLine.value) {
        val tweenSpec = tween<Float>(400)
        lyricLineAnimation.mapIndexed { index, animatable ->
            async {
                if (currentLine.value == index) {
                    animatable.animateTo(headlineMedium.fontSize.value / titleMedium.fontSize.value, tweenSpec)
                } else animatable.animateTo(1f, tweenSpec)
            }
        }.awaitAll()
    }

    val constraintState = remember { mutableStateOf<Constraints?>(null) }
    val textLayoutResultListState = remember {
        mutableStateOf<List<TextLayoutResult>?>(null)
    }

    Canvas(
        modifier = modifier
            .graphicsLayer { clip = true }
            .draggable(
                state = lyricViewState,
                orientation = Orientation.Vertical,
                onDragStarted = lyricViewState::onDragStarted,
                onDragStopped = lyricViewState::onDragStopped
            )
    ) {
        val constraints = constraintState.value ?:
            Constraints(maxWidth = this.size.width.toInt(), maxHeight = this.size.height.toInt())
                .also { constraintState.value = it }
        translate(left = 0f, top = lyricViewState.yOffset) {
            var offset = 0f
            val textLayoutResultList = textLayoutResultListState.value ?: lyricOutput.lyricEntities.map { lyricLine ->
                textMeasurer.measure(
                    text = lyricLine.lyricString,
                    style = titleMedium,
                    constraints = constraints,
                )
            }.also { textLayoutResultListState.value = it }
            textLayoutResultList.forEachIndexed { index, layoutResult ->
                val scaleValue = lyricLineAnimation[index].value
                val scaleHeight = layoutResult.multiParagraph.height * scaleValue
                val topOffset = (scaleHeight - layoutResult.multiParagraph.height) / 2
                translate(top = offset + topOffset) {
                    scale(
                        scale = scaleValue,
                        pivot = Offset(0f, layoutResult.multiParagraph.height / 2)
                    ) {
                        drawText(textLayoutResult = layoutResult)
                    }
                }
                offset += scaleHeight
            }
        }
    }
}
