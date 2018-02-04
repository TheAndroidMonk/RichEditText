package com.gworks.richedittext.markups

import android.text.style.LineHeightSpan
import android.text.Spanned
import android.graphics.Paint.FontMetricsInt
import android.text.Layout
import android.text.style.AlignmentSpan

class ParagraphSpan(val topSpacing: Int? = null, val bottomSpacing: Int? = null, align: Layout.Alignment? = null) :
        AlignmentSpan.Standard(align?:Layout.Alignment.ALIGN_NORMAL), LineHeightSpan {

    private var originalAscent = 0
    private var originalTop = 0

    override fun chooseHeight(text: CharSequence, start: Int, end: Int,
                              spanstartv: Int, v: Int, fm: FontMetricsInt) {
        val spanned = text as Spanned
        val st = spanned.getSpanStart(this)
        val en = spanned.getSpanEnd(this)
        if (topSpacing != null) {
            if (start == st) {
                originalAscent = fm.ascent
                originalTop = fm.top
                fm.ascent -= topSpacing
                fm.top -= topSpacing
            } else {
                fm.ascent = originalAscent
                fm.top = originalTop
            }
        }
        if ((end == en || end == en + 1) // paragraph may or may not include \n
                && bottomSpacing != null) {
            fm.descent += bottomSpacing
            fm.bottom += bottomSpacing
        }
    }

}