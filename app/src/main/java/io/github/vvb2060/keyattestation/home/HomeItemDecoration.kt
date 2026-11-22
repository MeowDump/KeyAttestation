package io.github.vvb2060.keyattestation.home

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.Paint
import android.graphics.drawable.GradientDrawable
import android.view.View
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import io.github.vvb2060.keyattestation.R
import rikka.core.res.resolveColor

class HomeItemDecoration(context: Context) : ItemDecoration() {

    private val outlineColor: Int = ColorUtils.setAlphaComponent(
        context.theme.resolveColor(com.google.android.material.R.attr.colorOnSurface), 0x3D)
    private val cardMargin: Int = context.resources.getDimensionPixelSize(R.dimen.home_card_margin)
    private val cardPadding: Int = context.resources.getDimensionPixelSize(R.dimen.home_card_padding)
    private val rowInset: Int = context.resources.getDimensionPixelSize(R.dimen.home_row_inset)
    private val strokeWidth: Int = context.resources.getDimensionPixelSize(R.dimen.home_card_stroke)

    private val drawable = GradientDrawable().apply {
        cornerRadius = context.resources.getDimension(R.dimen.home_card_radius)
        setStroke(strokeWidth, outlineColor)
    }
    private val dividerPaint = Paint().apply {
        color = ColorUtils.setAlphaComponent(outlineColor, 0x24)
        style = Paint.Style.FILL
    }

    private fun hasTopMargin(@Suppress("UNUSED_PARAMETER") adapter: HomeAdapter, position: Int): Boolean {
        return position == 0
    }

    private fun hasBottomMargin(adapter: HomeAdapter, position: Int): Boolean {
        return position == adapter.itemCount - 1 || !(adapter.allowFrameAt(position) && adapter.allowFrameAt(position + 1) && !adapter.shouldCommitFrameAt(position))
    }

    private fun hasTopPadding(adapter: HomeAdapter, position: Int): Boolean {
        return adapter.allowFrameAt(position) && (position == 0 || adapter.shouldCommitFrameAt(position - 1) || !adapter.allowFrameAt(position - 1))
    }

    private fun hasBottomPadding(adapter: HomeAdapter, position: Int): Boolean {
        return adapter.shouldCommitFrameAt(position)// && (position == adapter.itemCount - 1 || adapter.shouldCommitFrameAt(position))
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val adapter = parent.adapter as HomeAdapter
        val position = parent.getChildAdapterPosition(view)

        if (hasTopMargin(adapter, position)) {
            outRect.top = cardMargin
        }
        if (hasTopPadding(adapter, position)) {
            outRect.top += cardPadding
        }

        if (hasBottomMargin(adapter, position)) {
            outRect.bottom = cardMargin
        }
        if (hasBottomPadding(adapter, position)) {
            outRect.bottom += cardPadding
        }

        outRect.left = cardMargin
        outRect.right = cardMargin
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if (parent.childCount == 0) {
            return
        }
        val adapter = parent.adapter as HomeAdapter
        var invalidatedPosition = true
        var left = 0
        var top = 0
        var right = 0
        var bottom: Int

        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            val position = parent.getChildAdapterPosition(child)

            if (!adapter.allowFrameAt(position)) {
                continue
            }

            if (adapter.allowDividerAfter(position)) {
                c.drawRect(
                    (child.left + rowInset).toFloat(),
                    (child.bottom - strokeWidth).toFloat(),
                    (child.right - rowInset).toFloat(),
                    child.bottom.toFloat(),
                    dividerPaint
                )
            }

            if (invalidatedPosition) {
                left = child.left
                top = child.top
                right = child.right
                invalidatedPosition = false
            }

            if ((i == parent.childCount - 1) || adapter.shouldCommitFrameAt(position)) {
                bottom = child.bottom

                drawable.setBounds(left, top - cardPadding, right, bottom + cardPadding)
                drawable.draw(c)

                invalidatedPosition = true
            } else {
                left = child.left.coerceAtLeast(left)
                right = child.right.coerceAtLeast(right)
            }
        }
    }
}
