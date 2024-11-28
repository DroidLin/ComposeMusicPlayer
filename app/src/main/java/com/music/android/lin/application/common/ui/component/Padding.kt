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
private class PaddingScopeImpl : PaddingScope {

    override var left: Dp = Dp.Hairline
    override var top: Dp = Dp.Hairline
    override var right: Dp = Dp.Hairline
    override var bottom: Dp = Dp.Hairline
    override var isRTLAware: Boolean = true

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PaddingScopeImpl

        if (left != other.left) return false
        if (top != other.top) return false
        if (right != other.right) return false
        if (bottom != other.bottom) return false
        if (isRTLAware != other.isRTLAware) return false

        return true
    }

    override fun hashCode(): Int {
        var result = left.hashCode()
        result = 31 * result + top.hashCode()
        result = 31 * result + right.hashCode()
        result = 31 * result + bottom.hashCode()
        result = 31 * result + isRTLAware.hashCode()
        return result
    }
}

@Stable
fun Modifier.padding(block: PaddingScope.() -> Unit): Modifier {
    return this.then(PaddingScopeElement(block))
}

private class PaddingScopeElement(
    private val block: PaddingScope.() -> Unit,
) : ModifierNodeElement<PaddingScopeNode>() {

    override fun create(): PaddingScopeNode {
        return PaddingScopeNode(this.block)
    }

    override fun update(node: PaddingScopeNode) {
        node.block = this.block
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PaddingScopeElement

        return block == other.block
    }

    override fun hashCode(): Int {
        return block.hashCode()
    }

}

private class PaddingScopeNode(
    var block: PaddingScope.() -> Unit,
) : Modifier.Node(), LayoutModifierNode {

    private val paddingScope = PaddingScopeImpl()

    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints,
    ): MeasureResult {
        paddingScope.apply(block)

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