package com.gworks.richedittext.markups

import android.text.style.LineHeightSpan
import android.text.Spanned
import android.graphics.Paint.FontMetricsInt
import android.text.Layout
import android.text.style.AlignmentSpan

class ParagraphSpan(val topSpacing: Int = 0, val bottomSpacing: Int = 0, align: Layout.Alignment = Layout.Alignment.ALIGN_NORMAL) :
        AlignmentSpan.Standard(align), LineHeightSpan {

    private var originalAscent = 0
    private var originalTop = 0

    override fun chooseHeight(text: CharSequence, start: Int, end: Int,
                              spanstartv: Int, v: Int, fm: FontMetricsInt) {
        val spanned = text as Spanned
        val st = spanned.getSpanStart(this)
        val en = spanned.getSpanEnd(this)
        if (start == st) {
            originalAscent = fm.ascent
            originalTop = fm.top
            fm.ascent -= topSpacing
            fm.top -= topSpacing
        } else {
            fm.ascent = originalAscent
            fm.top = originalTop
        }
        if (end == en || end == en + 1) { // paragraph may or may not include \n
            fm.descent += bottomSpacing
            fm.bottom += bottomSpacing
        }
    }

}