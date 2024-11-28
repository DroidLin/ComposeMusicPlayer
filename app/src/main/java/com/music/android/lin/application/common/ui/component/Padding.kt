package com.music.android.lin.application.common.ui.component

import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.offset

interface PaddingScope {
    var left: Dp
    var top: Dp
    var right: Dp
    var bottom: Dp

    var isRTLAware: Boolean
}

@Stable
private class PaddingScopeImpl(
    private val function: PaddingScope.() -> Unit,
) : PaddingScope {

    override var left: Dp = Dp.Hairline
    override var top: Dp = Dp.Hairline
    override var right: Dp = Dp.Hairline
    override var bottom: Dp = Dp.Hairline
    override var isRTLAware: Boolean = true

    fun receivePaddingValues() {
        this.function()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PaddingScopeImpl

        if (function != other.function) return false
        if (left != other.left) return false
        if (top != other.top) return false
        if (right != other.right) return false
        if (bottom != other.bottom) return false

        return true
    }

    override fun hashCode(): Int {
        var result = function.hashCode()
        result = 31 * result + left.hashCode()
        result = 31 * result + top.hashCode()
        result = 31 * result + right.hashCode()
        result = 31 * result + bottom.hashCode()
        return result
    }
}

@Stable
fun Modifier.padding(function: PaddingScope.() -> Unit): Modifier {
    return this.then(PaddingScopeElement(PaddingScopeImpl(function)))
}

private class PaddingScopeElement(
    private val paddingScope: PaddingScopeImpl,
) : ModifierNodeElement<PaddingScopeNode>() {

    override fun create(): PaddingScopeNode {
        return PaddingScopeNode(this.paddingScope)
    }

    override fun update(node: PaddingScopeNode) {
        node.paddingScope = this.paddingScope
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PaddingScopeElement

        return paddingScope == other.paddingScope
    }

    override fun hashCode(): Int {
        return paddingScope.hashCode()
    }
}

private class PaddingScopeNode(
    var paddingScope: PaddingScopeImpl,
) : Modifier.Node(), LayoutModifierNode {

    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints,
    ): MeasureResult {

        paddingScope.receivePaddingValues()

        val start = paddingScope.left
        val end = paddingScope.right
        val top = paddingScope.top
        val bottom = paddingScope.bottom
        val rtlAware = paddingScope.isRTLAware

        val horizontal = start.roundToPx() + end.roundToPx()
        val vertical = top.roundToPx() + bottom.roundToPx()

        val placeable = measurable.measure(constraints.offset(-horizontal, -vertical))

        val width = constraints.constrainWidth(placeable.width + horizontal)
        val height = constraints.constrainHeight(placeable.height + vertical)
        return layout(width, height) {
            if (rtlAware) {
                placeable.placeRelative(start.roundToPx(), top.roundToPx())
            } else {
                placeable.place(start.roundToPx(), top.roundToPx())
            }
        }
    }

    fun update(paddingScope: PaddingScope) {

    }
}