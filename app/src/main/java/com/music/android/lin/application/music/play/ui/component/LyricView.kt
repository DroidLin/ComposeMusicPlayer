package com.music.android.lin.application.music.play.ui.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDecay
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.animation.core.spring
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
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Constraints
import com.music.android.lin.application.music.play.model.LyricOutput
import com.music.android.lin.application.music.play.model.SimpleLineLyricOutput
import com.music.android.lin.application.music.play.ui.util.binarySearchLine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.util.concurrent.atomic.AtomicInteger

private val EmptyLyricMeasureResult = LyricTextMeasureResult(
    lyricOutput = LyricOutput,
    childConstraints = Constraints(),
    measureLyricTextResult = emptyList()
)

@Immutable
internal class LyricTextMeasureResult(
    val lyricOutput: LyricOutput,
    val childConstraints: Constraints,
    val measureLyricTextResult: List<LyricLineMeasureResult>
)

@Immutable
internal class LyricLineMeasureResult(
    val textLayoutResult: TextLayoutResult,
    val animatable: Animatable<Float, AnimationVector1D>
) {
    val textWidth = this.textLayoutResult.multiParagraph.width
    val textHeight get() = this.textLayoutResult.multiParagraph.height

    val scalePivot = Offset(0f, textLayoutResult.multiParagraph.height / 2)

    var lineYOffset by mutableFloatStateOf(0f)
    var realLineHeight by mutableFloatStateOf(textHeight)

    var viewTopBounds by mutableFloatStateOf(0f)
    var viewBottomBounds by mutableFloatStateOf(0f)
}

@Stable
class LyricViewState : DraggableState {

    private var lyricMaxYOffset by mutableFloatStateOf(0f)
    private var lyricViewYOffset by mutableFloatStateOf(0f)
    private var isDragging by mutableStateOf(false)

    private val flingInteger = AtomicInteger()

    private val dragMutex = MutatorMutex()
    private val dragScope = object : DragScope {
        override fun dragBy(pixels: Float) {
            dispatchRawDelta(pixels)
        }
    }

    internal val layoutInfoState =
        mutableStateOf<LyricTextMeasureResult>(value = EmptyLyricMeasureResult)

    var yOffset: Float
        get() = lyricViewYOffset
        private set(value) {
            lyricViewYOffset = value
        }

    override fun dispatchRawDelta(delta: Float) {
        yOffset += delta
    }

    override suspend fun drag(dragPriority: MutatePriority, block: suspend DragScope.() -> Unit) {
        isDragging = true
        dragMutex.mutateWith(dragScope, dragPriority, block)
        isDragging = false
    }

    internal fun applyMeasureResult(
        measureResult: LyricTextMeasureResult
    ) {
        layoutInfoState.value = measureResult
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

    fun DrawScope.drawSimpleLine(lyricOutput: LyricOutput) {

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
            lyricOutput = output,
            lyricViewState = lyricViewState,
        )

//        is SimpleLineLyricOutput -> LazyColumnLyricView(
//            currentPosition = currentPosition,
//            modifier = modifier,
//            lyricOutput = output,
//        )

        else -> Box(modifier = modifier)
    }
}

@Composable
private fun SimpleLineLyricView(
    currentPosition: State<Long>,
    lyricOutput: SimpleLineLyricOutput,
    lyricViewState: LyricViewState = rememberLyricViewState(),
    modifier: Modifier = Modifier,
) {
    val textMeasurer = rememberTextMeasurer()
    val contentColor = LocalContentColor.current
    val titleMedium = MaterialTheme.typography.titleMedium
    val headlineMedium = MaterialTheme.typography.headlineMedium

    val lastLine = remember { mutableIntStateOf(-1) }
    val currentLine = remember {
        derivedStateOf {
            lyricViewState.layoutInfoState.value
                .lyricOutput
                .entries
                .binarySearchLine(currentPosition.value)
        }
    }

    LaunchedEffect(currentLine.value) {
        val animSpec = spring<Float>(stiffness = Spring.StiffnessLow)
        lyricViewState.layoutInfoState.value.measureLyricTextResult.mapIndexed { index, lyricLineMeasureResult ->
            async {
                if (currentLine.value == index) {
                    val scaleUpValue = headlineMedium.fontSize.value / titleMedium.fontSize.value
                    lyricLineMeasureResult.animatable.animateTo(
                        targetValue = scaleUpValue,
                        animationSpec = animSpec
                    )
                } else if (lastLine.intValue == index) {
                    lyricLineMeasureResult.animatable.animateTo(1f, animSpec)
                } else lyricLineMeasureResult.animatable.snapTo(1f)
            }
        }.awaitAll()
        lastLine.intValue = currentLine.value
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
        measureLyrics(
            lyricOutput = lyricOutput,
            textMeasurer = textMeasurer,
            textStyle = titleMedium,
            lyricViewState = lyricViewState
        )
        layoutLyrics(
            lyricViewState = lyricViewState
        )
        drawLyrics(
            contentColor = contentColor,
            lyricViewState = lyricViewState
        )
    }
}

private fun DrawScope.measureLyrics(
    lyricOutput: LyricOutput,
    textMeasurer: TextMeasurer,
    textStyle: TextStyle,
    lyricViewState: LyricViewState,
) {
    val lyricMatch = lyricOutput == lyricViewState.layoutInfoState.value.lyricOutput
    if (lyricMatch) {
        return
    }
    val constraints = Constraints(
        maxWidth = this.size.width.toInt(),
        maxHeight = this.size.height.toInt()
    )
    val measureResults = lyricOutput.entries.map { line ->
        LyricLineMeasureResult(
            textLayoutResult = textMeasurer.measure(
                text = line.value,
                style = textStyle,
                constraints = constraints,
            ),
            animatable = Animatable(initialValue = 1f)
        )
    }
    val lyricMeasureResult = LyricTextMeasureResult(
        lyricOutput = lyricOutput,
        childConstraints = constraints,
        measureLyricTextResult = measureResults
    )
    lyricViewState.applyMeasureResult(lyricMeasureResult)
}

private fun DrawScope.layoutLyrics(
    lyricViewState: LyricViewState
) {
    var offset = 0f
    val lyricMeasureResult = lyricViewState.layoutInfoState.value
    lyricMeasureResult.measureLyricTextResult.forEach { lineMeasureResult ->
        val scaleValue = lineMeasureResult.animatable.value
        val scaleHeight = lineMeasureResult.textHeight * scaleValue
        val topOffset = (scaleHeight - lineMeasureResult.textHeight) / 2
        lineMeasureResult.lineYOffset = offset + topOffset
        lineMeasureResult.realLineHeight = scaleHeight

        lineMeasureResult.viewTopBounds = lyricViewState.yOffset + lineMeasureResult.lineYOffset
        lineMeasureResult.viewBottomBounds = lyricViewState.yOffset + lineMeasureResult.lineYOffset + lineMeasureResult.realLineHeight
        offset += scaleHeight
    }
}

private fun LyricLineMeasureResult.inDrawScreen(drawScope: DrawScope): Boolean {
    val topBounds = this.viewTopBounds
    val bottomBounds = this.viewBottomBounds
    return (topBounds <= 0 && bottomBounds > 0) ||
            (topBounds in (0f..drawScope.size.height) && bottomBounds in (0f..drawScope.size.height)) ||
            (topBounds < drawScope.size.height && bottomBounds >= drawScope.size.height)
}

private fun DrawScope.drawLyrics(
    contentColor: Color,
    lyricViewState: LyricViewState
) {
    val lyricMeasureResult = lyricViewState.layoutInfoState.value
    translate(
        left = 0f,
        top = lyricViewState.yOffset
    ) {
        for (lineMeasureResult in lyricMeasureResult.measureLyricTextResult) {
            translate(
                top = lineMeasureResult.lineYOffset
            ) {
                if (lineMeasureResult.inDrawScreen(this)) {
                    scale(
                        scale = lineMeasureResult.animatable.value,
                        pivot = lineMeasureResult.scalePivot
                    ) {
                        drawText(
                            textLayoutResult = lineMeasureResult.textLayoutResult,
                            color = contentColor
                        )
                    }
                }
            }
        }
    }
}