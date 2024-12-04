package com.music.android.lin.application.music.play.ui.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateDecay
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntSizeAsState
import androidx.compose.animation.core.animateValueAsState
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.drawscope.DrawScope
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

    var yOffset: Float
        get() = lyricViewYOffset
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
    val contentColor = LocalContentColor.current
    val titleMedium = MaterialTheme.typography.titleMedium
    val headlineMedium = MaterialTheme.typography.headlineMedium

    val lyricLineAnimation = remember(lyricOutput) {
        lyricOutput.lyricEntities.map { line ->
            Animatable(
                initialValue = 1f,
                visibilityThreshold = 0.0001f
            )
        }.toMutableList()
    }

    val lastLine = remember { mutableIntStateOf(-1) }
    val currentLine = remember {
        derivedStateOf {
            lyricOutput.lyricEntities.binarySearchLine(currentPosition.value)
        }
    }

    LaunchedEffect(currentLine.value) {
        val animSpec = spring<Float>(stiffness = Spring.StiffnessLow)
        lyricLineAnimation.mapIndexed { index, animatable ->
            async {
                if (currentLine.value == index) {
                    val scaleUpValue = headlineMedium.fontSize.value / titleMedium.fontSize.value
                    animatable.animateTo(
                        targetValue = scaleUpValue,
                        animationSpec = animSpec
                    )
                } else if (lastLine.intValue == index) {
                    animatable.animateTo(1f, animSpec)
                } else animatable.snapTo(1f)
            }
        }.awaitAll()
        lastLine.intValue = currentLine.value
    }

    val constraintState = remember(lyricOutput) { mutableStateOf<Constraints?>(null) }
    val textLayoutResultListState = remember(lyricOutput) {
        mutableStateOf<List<TextLayoutResult>?>(null)
    }

    val currentTextLayoutResultState = remember(lyricOutput) {
        mutableStateOf<Pair<Int, TextLayoutResult>?>(null)
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
                val textLayoutResult = if (currentLine.value == index) {
                    val currentTextLayoutResultPair = currentTextLayoutResultState.value
                    if (currentTextLayoutResultPair == null || currentTextLayoutResultPair.first != index || currentTextLayoutResultPair.second.layoutInput.text != layoutResult.layoutInput.text) {
                        val measureLayoutResult = textMeasurer.measure(
                            text = layoutResult.layoutInput.text,
                            constraints = Constraints(
                                maxWidth = (this.size.width / (headlineMedium.fontSize.value / titleMedium.fontSize.value)).toInt(),
                                maxHeight = this.size.height.toInt()
                            ),
                            style = layoutResult.layoutInput.style
                        )
                        currentTextLayoutResultState.value = index to measureLayoutResult
                        measureLayoutResult
                    } else currentTextLayoutResultPair.second
                } else layoutResult

                val scaleValue = lyricLineAnimation[index].value
                val scaleHeight = textLayoutResult.multiParagraph.height * scaleValue
                val topOffset = (scaleHeight - textLayoutResult.multiParagraph.height) / 2
                translate(top = offset + topOffset) {
                    scale(
                        scale = scaleValue,
                        pivot = Offset(0f, textLayoutResult.multiParagraph.height / 2)
                    ) {
                        drawText(
                            textLayoutResult = textLayoutResult,
                            color = contentColor
                        )
                    }
                }
                offset += scaleHeight
            }
        }
    }
}

@Composable
private fun LazyColumnLyricView(
    currentPosition: State<Long>,
    lyricOutput: SimpleLineLyricOutput,
    modifier: Modifier = Modifier,
) {
    val currentLine = remember {
        derivedStateOf {
            lyricOutput.lyricEntities.binarySearchLine(currentPosition.value)
        }
    }
    LazyColumn(
        modifier = modifier
    ) {
        itemsIndexed(
            items = lyricOutput.lyricEntities,
            key = { index, item -> item.hashCode() },
            contentType = { index, item -> item.javaClass.name }
        ) { index, line ->
            val fontSizeAnimate = animateValueAsState(
                targetValue = if (currentLine.value == index) {
                    MaterialTheme.typography.headlineMedium.fontSize
                } else MaterialTheme.typography.titleMedium.fontSize,
                typeConverter = TextUnitConverter,
                animationSpec = remember { spring(stiffness = Spring.StiffnessLow) }
            )
            Text(
                text = line.lyricString,
                modifier = Modifier.fillParentMaxWidth(),
                fontSize = fontSizeAnimate.value
            )
        }
    }
}

private fun DrawScope.drawSimpleLine() {}