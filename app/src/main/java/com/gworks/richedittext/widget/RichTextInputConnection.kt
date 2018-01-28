package com.gworks.richedittext.widget

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextUtils
import android.view.inputmethod.*
import android.widget.EditText
import com.gworks.richedittext.*
import com.gworks.richedittext.markups.Markup

class RichTextInputConnection(private val richTexter: RichEditTexter,
                              private val editText: EditText,
                              wrapped: InputConnection, mutable: Boolean = true) :
                              InputConnectionWrapper(wrapped, mutable) {

    private val tempEditable = SpannableStringBuilder()

    override fun setComposingRegion(start: Int, end: Int): Boolean {
        return if (!composingRegionChanged(editText.text, start, end))
            super.setComposingRegion(start, end)
        else {
            if (start != end)
                captureVulnerableSpans(start, end)
            super.setComposingRegion(start, end)
            if (start != end)
                restoreCapturedSpans()
            true
        }
    }

    private fun captureVulnerableSpans(start: Int, end: Int) {
        tempEditable.clear()
        tempEditable.appendPlain(editText.text, start, end)
        moveMarkups(editText.text, start, end, tempEditable)
    }

    private fun restoreCapturedSpans(composingStart: Int = getComposingStart(editText.text),
                                     composingLength: Int = getComposingLength(editText.text)) {
        val matchingLength = matchingLength(editText.text, tempEditable,
                composingLength, composingStart, 0)
        moveMarkups(tempEditable, 0, matchingLength, editText.text,
                composingStart, composingLength - matchingLength)
    }

    override fun setComposingText(text: CharSequence?, newCursorPosition: Int): Boolean {
        val start = getComposingStart(editText.text)
        val end = getComposingEnd(editText.text)
        captureVulnerableSpans(start, end)
        val result = super.setComposingText(text, newCursorPosition)
        restoreCapturedSpans()
        return result
    }

    override fun finishComposingText(): Boolean {
        val start = getComposingStart(editText.text)
        val end = getComposingEnd(editText.text)
        if (start >= 0 && end >= 0)
            captureVulnerableSpans(start, end)
        val result = super.finishComposingText()
        if (start >= 0 && end >= 0)
            restoreCapturedSpans(start, end)
        return result
    }

    override fun commitText(text: CharSequence?, newCursorPosition: Int): Boolean {
        val result = super.commitText(text, newCursorPosition)
        if (!TextUtils.isEmpty(text)) {
            val s = editText.text
            val st = editText.selectionStart
            val en = editText.selectionEnd
            val spans = s.getSpans(st + text!!.length, en + text.length, Markup::class.java)
            for (it in spans) {
                if (s.getSpanFlags(it) == Spanned.SPAN_MARK_MARK) {
                    it.removeInternal(s)
                    it.applyInternal(s, st, st + text.length, richTexter.getSpanFlag(st, st + text.length))
                }
            }
        }
        return result
    }

    /**
     * Moves the markups from the region `start...end` in `source` to the region
     * `destOff...destOff+end-start` in `dest`. Markups in `source` that begin
     * before `start` or end after `end` but overlap this range are trimmed
     * as if they began at `start` or ended at `end`.
     */
    private fun moveMarkups(source: Spannable, start: Int, end: Int, dest: Spannable, destOff: Int = 0,
                            extendInclusiveSpansBy: Int = 0) {
        val spans = source.getSpans(start, end, Markup::class.java)
        for (it in spans) {
            val st = source.getSpanStart(it)
            var en = source.getSpanEnd(it)
            if (inside(start, end, st, en)) {
                var fl = source.getSpanFlags(it)
                if (fl == Spanned.SPAN_EXCLUSIVE_INCLUSIVE && en == end)
                    en += extendInclusiveSpansBy
                if (fl == Spanned.SPAN_MARK_MARK && st == end) {
                    System.out.println("span " + it)
                    fl = Spanned.SPAN_EXCLUSIVE_INCLUSIVE
                    en += extendInclusiveSpansBy
                }
                it.removeInternal(source)
                it.applyInternal(dest, st - start + destOff, en - start + destOff, fl)
            }
        }
    }
}
