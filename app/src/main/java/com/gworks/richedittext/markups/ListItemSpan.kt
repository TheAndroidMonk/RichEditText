package com.gworks.richedittext.markups

import android.graphics.Canvas
import android.graphics.Paint
import android.os.Parcel
import android.text.Layout
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.BulletSpan

open class ListItemSpan(private val margin: Int = List.DEFAULT_MARGIN,
                        private val color: Int? = null,
                        private val separator: CharSequence? = null) :
                        BulletSpan(0, color ?: 0) {

    private val bulletTextWithSeparator = SpannableStringBuilder()

    internal var bulletText: CharSequence? = null
        set(value) {
            bulletTextWithSeparator.clear()
            bulletTextWithSeparator.append(value)
            if (separator != null)
                bulletTextWithSeparator.append(separator)
        }

    constructor(src: Parcel) : this(src.readInt(), if (src.readInt() == 0) null else src.readInt())

    override fun getLeadingMargin(first: Boolean): Int {
        return margin
    }

    override fun drawLeadingMargin(c: Canvas, p: Paint, x: Int, dir: Int,
                                   top: Int, baseline: Int, bottom: Int,
                                   text: CharSequence, start: Int, end: Int,
                                   first: Boolean, l: Layout) {
        if (bulletTextWithSeparator.isNotEmpty()) {
            if ((text as Spanned).getSpanStart(this) == start) {
                val style = p.style
                var oldColor = 0
                if (color != null) {
                    oldColor = p.color
                    p.color = color
                }
                p.style = Paint.Style.FILL
                c.drawText(bulletTextWithSeparator, 0, bulletTextWithSeparator.length, x.toFloat(), baseline.toFloat(), p)
                if (color != null) {
                    p.color = oldColor
                }
                p.style = style
            }
        } else super.drawLeadingMargin(c, p, x, dir, top, baseline, bottom, text, start, end, first, l)
    }

}