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
import androidx.compose.ui.geometry.Size
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
import androidx.compose.ui.unit.dp
import com.music.android.lin.application.music.play.model.LyricLine
import com.music.android.lin.application.music.play.model.LyricOutput
import com.music.android.lin.application.music.play.model.SimpleLineLyricOutput
import com.music.android.lin.application.music.play.ui.util.binarySearchLine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicInteger

private val EmptyLyricMeasureResult = LyricTextMeasureResult(
    lyricOutput = LyricOutput,
    childConstraints = Constraints(),
    measureLyricTextResult = emptyList(),
    drawScopeSize = Size.Zero
)

@Immutable
internal class LyricTextMeasureResult(
    val lyricOutput: LyricOutput,
    val drawScopeSize: Size,
    val childConstraints: Constraints,
    val measureLyricTextResult: List<LyricLineMeasureResult>,
) {

    var lyricContentHeight by mutableFloatStateOf(0f)
}

@Immutable
internal class LyricLineMeasureResult(
    val lyricLine: LyricLine,
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

    internal val anchorLyricLayoutInfoState =
        mutableStateOf<LyricLineMeasureResult?>(null)

    var yOffset: Float
        get() = lyricViewYOffset
        private set(value) {
            lyricViewYOffset = value
        }

    override fun dispatchRawDelta(delta: Float) {
        scrollByInner(delta)
    }

    private fun scrollByInner(delta: Float): Boolean {
        val layoutInfo = layoutInfoState.value
        val drawScopeHeight = layoutInfo.drawScopeSize.height
        val topBounds = drawScopeHeight - layoutInfo.lyricContentHeight - (drawScopeHeight / 2f)
        val bottomBounds = drawScopeHeight / 2f

        val nextYOffset = (yOffset + delta).coerceIn(topBounds, bottomBounds)
        val cancel = nextYOffset == yOffset
        yOffset = nextYOffset
        return cancel
    }

    suspend fun snapToLine(lineIndex: Int) {
        scrollToLine(lineIndex = lineIndex, smooth = false)
    }

    suspend fun animateToLine(lineIndex: Int) {
        scrollToLine(lineIndex = lineIndex, smooth = true)
    }

    private suspend fun scrollToLine(lineIndex: Int, smooth: Boolean = false) {
        if (isDragging) return
        val lineMeasureResult = layoutInfoState.value.measureLyricTextResult.getOrNull(lineIndex) ?: return
        val drawScopeOffset = layoutInfoState.value.drawScopeSize.height / 4
        val targetValue = -lineMeasureResult.lineYOffset + drawScopeOffset
        if (!smooth) {
            scrollByInner(targetValue - yOffset)
        } else {
            coroutineScope {
                val token = flingInteger.incrementAndGet()
                var lastValue = yOffset
                Animatable(initialValue = yOffset).animateTo(
                    targetValue = targetValue,
                    animationSpec = spring(stiffness = Spring.StiffnessLow)
                ) {
                    val currentToken = flingInteger.get()
                    if (token == currentToken) {
                        val delta = this.value - lastValue
                        scrollByInner(delta)
                        lastValue = this.value
                    } else {
                        launch { stop() }
                    }
                }
            }
        }
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
        val token = flingInteger.incrementAndGet()
        val animationSpec = exponentialDecay<Float>(
            frictionMultiplier = 1f,
            absVelocityThreshold = 1f
        )
        var lastValue = yOffset
        AnimationState(
            initialValue = yOffset,
            initialVelocity = velocity,
        ).animateDecay(animationSpec) {
            val currentToken = flingInteger.get()
            if (token == currentToken) {
                val delta = this.value - lastValue
                scrollByInner(delta)
                lastValue = this.value
            } else this.cancelAnimation()
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
            lyricOutput = output,
            lyricViewState = lyricViewState,
        )

        else -> Box(modifier = modifier)
    }
}

private const val FontScale = 28f / 16f

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

    val lastLine = remember { mutableIntStateOf(-1) }
    val currentLine = remember {
        derivedStateOf {
            lyricViewState.layoutInfoState.value
                .lyricOutput
                .entries
                .binarySearchLine(currentPosition.value)
        }
    }

    val first = remember { mutableStateOf(true) }

    LaunchedEffect(currentLine.value) {
        if (lyricViewState.layoutInfoState.value.measureLyricTextResult.isEmpty()) {
            return@LaunchedEffect
        }
        val indexOfCurrentLine = currentLine.value
        val indexOfLastLine = lastLine.intValue
        if (first.value) {
            launch { lyricViewState.snapToLine(indexOfCurrentLine) }
            launch {
                lyricViewState.anchorLyricLayoutInfoState.value
                    ?.animatable
                    ?.snapTo(FontScale)
            }
            launch {
                lyricViewState.layoutInfoState.value
                    .measureLyricTextResult
                    .getOrNull(indexOfCurrentLine)
                    ?.animatable
                    ?.snapTo(FontScale)
            }
            launch {
                lyricViewState.layoutInfoState.value
                    .measureLyricTextResult
                    .getOrNull(indexOfLastLine)
                    ?.animatable
                    ?.snapTo(1f)
                lastLine.intValue = currentLine.value
            }
            first.value = false
        } else {
            launch { lyricViewState.animateToLine(currentLine.value) }
            val animSpec = spring<Float>(stiffness = Spring.StiffnessLow)
            launch {
                lyricViewState.anchorLyricLayoutInfoState.value
                    ?.animatable
                    ?.animateTo(FontScale, animSpec)
            }
            launch {
                lyricViewState.layoutInfoState.value
                    .measureLyricTextResult
                    .getOrNull(indexOfCurrentLine)
                    ?.animatable
                    ?.animateTo(FontScale, animSpec)
            }
            launch {
                lyricViewState.layoutInfoState.value
                    .measureLyricTextResult
                    .getOrNull(indexOfLastLine)
                    ?.animatable
                    ?.animateTo(1f, animSpec)
                lastLine.intValue = indexOfCurrentLine
            }
        }
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
            currentLine = currentLine,
            lyricOutput = lyricOutput,
            textMeasurer = textMeasurer,
            textStyle = titleMedium,
            lyricViewState = lyricViewState
        )
        layoutLyrics(
            currentLine = currentLine,
            lyricViewState = lyricViewState
        )
        drawLyrics(
            currentLine = currentLine,
            contentColor = contentColor,
            lyricViewState = lyricViewState
        )
    }
}

private fun DrawScope.measureLyrics(
    currentLine: State<Int>,
    lyricOutput: LyricOutput,
    textMeasurer: TextMeasurer,
    textStyle: TextStyle,
    lyricViewState: LyricViewState,
) {
    val indexOfCurrentLine = currentLine.value
    val cacheLyricLine = lyricViewState.anchorLyricLayoutInfoState.value?.lyricLine
    val sourceLyricLine = lyricOutput.entries.getOrNull(indexOfCurrentLine)
    if (sourceLyricLine != null && sourceLyricLine != cacheLyricLine) {
        val scaleConstraints = Constraints(
            maxWidth = (this.size.width / FontScale).toInt(),
            maxHeight = this.size.height.toInt()
        )
        val scaleLineMeasureResult = LyricLineMeasureResult(
            textLayoutResult = textMeasurer.measure(
                text = sourceLyricLine.value,
                style = textStyle,
                constraints = scaleConstraints,
            ),
            animatable = Animatable(initialValue = 1f),
            lyricLine = sourceLyricLine
        )
        lyricViewState.anchorLyricLayoutInfoState.value = scaleLineMeasureResult
    }

    val lyricMatch = lyricOutput == lyricViewState.layoutInfoState.value.lyricOutput
    val sizeMatch = this.size.width == lyricViewState.layoutInfoState.value.drawScopeSize.width &&
            this.size.height == lyricViewState.layoutInfoState.value.drawScopeSize.height
    if (lyricMatch && sizeMatch) {
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
            animatable = Animatable(initialValue = 1f),
            lyricLine = line
        )
    }
    val lyricMeasureResult = LyricTextMeasureResult(
        lyricOutput = lyricOutput,
        childConstraints = constraints,
        measureLyricTextResult = measureResults,
        drawScopeSize = this.size
    )
    lyricViewState.applyMeasureResult(lyricMeasureResult)
}

private fun DrawScope.layoutLyrics(
    currentLine: State<Int>,
    lyricViewState: LyricViewState
) {
    val lyricLineOffset = 16.dp.toPx()
    var offset = 0f
    val lyricMeasureResult = lyricViewState.layoutInfoState.value
    lyricMeasureResult.measureLyricTextResult.forEachIndexed { index, lineMeasureResult ->
        val measureResult = if (index == currentLine.value) {
            lyricViewState.anchorLyricLayoutInfoState.value ?: lineMeasureResult
        } else lineMeasureResult

        val scaleValue = measureResult.animatable.value
        val scaleHeight = measureResult.textHeight * scaleValue
        val topOffset = (scaleHeight - measureResult.textHeight) / 2
        if (index == currentLine.value) {
            val currentMeasureResult = lyricViewState.anchorLyricLayoutInfoState.value
            currentMeasureResult?.lineYOffset = offset + topOffset
            currentMeasureResult?.realLineHeight = scaleHeight
            currentMeasureResult?.viewTopBounds = lyricViewState.yOffset + measureResult.lineYOffset
            currentMeasureResult?.viewBottomBounds = lyricViewState.yOffset + measureResult.lineYOffset + measureResult.realLineHeight
        }
        lineMeasureResult.lineYOffset = offset + topOffset
        lineMeasureResult.realLineHeight = scaleHeight
        lineMeasureResult.viewTopBounds = lyricViewState.yOffset + measureResult.lineYOffset
        lineMeasureResult.viewBottomBounds = lyricViewState.yOffset + measureResult.lineYOffset + measureResult.realLineHeight
        offset += (scaleHeight + lyricLineOffset)
    }
    lyricMeasureResult.lyricContentHeight = offset
}

private fun LyricLineMeasureResult.inDrawScreen(drawScope: DrawScope): Boolean {
    val topBounds = this.viewTopBounds
    val bottomBounds = this.viewBottomBounds
    return (topBounds <= 0 && bottomBounds > 0) ||
            (topBounds in (0f..drawScope.size.height) && bottomBounds in (0f..drawScope.size.height)) ||
            (topBounds < drawScope.size.height && bottomBounds >= drawScope.size.height)
}

private fun DrawScope.drawLyrics(
    currentLine: State<Int>,
    contentColor: Color,
    lyricViewState: LyricViewState
) {
    val lyricMeasureResult = lyricViewState.layoutInfoState.value
    translate(
        left = 0f,
        top = lyricViewState.yOffset
    ) {
        lyricMeasureResult.measureLyricTextResult.forEachIndexed { index, lineMeasureResult ->
            val measureResult = if (index == currentLine.value) {
                lyricViewState.anchorLyricLayoutInfoState.value ?: lineMeasureResult
            } else lineMeasureResult
            translate(
                top = measureResult.lineYOffset
            ) {
                if (measureResult.inDrawScreen(this)) {
                    scale(
                        scale = measureResult.animatable.value,
                        pivot = measureResult.scalePivot
                    ) {
                        drawText(
                            textLayoutResult = measureResult.textLayoutResult,
                            color = contentColor
                        )
                    }
                }
            }
        }
    }
}