package com.gworks.richedittext.widget

import android.os.Bundle
import android.text.*
import android.view.KeyEvent
import android.view.inputmethod.*
import android.widget.EditText
import com.gworks.richedittext.*
import com.gworks.richedittext.markups.Markup
import java.nio.file.Files.delete
import android.view.inputmethod.BaseInputConnection.getComposingSpanEnd
import android.view.inputmethod.BaseInputConnection.getComposingSpanStart
import android.text.Selection



class RichTextInputConnection(private val richTexter: RichEditTexter,
                              private val editText: EditText,
                              wrapped: InputConnection, mutable: Boolean = true) :
                              InputConnectionWrapper(wrapped, mutable) {

    private val tempEditable = SpannableStringBuilder()

    override fun setComposingRegion(start: Int, end: Int): Boolean {
        captureVulnerableSpans(start, end)
        return super.setComposingRegion(start, end)
    }

    private fun captureVulnerableSpans(start: Int, end: Int) {
        tempEditable.clear()
        tempEditable.clearSpans()
        tempEditable.appendPlain(editText.text, start, end)
        shiftMarkups(editText.text, start, end, tempEditable, move = false)
    }

    override fun setComposingText(text: CharSequence?, newCursorPosition: Int): Boolean {
        captureModifications(text, newCursorPosition)
        val composingStart = getComposingStart(editText.text)
        val selectionSt = editText.selectionStart
        val selectionEn = editText.selectionEnd
        val result = super.setComposingText(text, newCursorPosition)
        shiftMarkups(tempEditable, 0, tempEditable.length, editText.text, getComposingStart(editText.text), move = false)
        return result
    }

    private fun captureModifications(text: CharSequence?, newCursorPosition: Int){

        val selectionStart = editText.selectionStart
        val selectionEnd = editText.selectionEnd

        if (selectionStart == selectionEnd) { // If insert or delete
            val diff = (text?.length ?: 0) - tempEditable.length
            var i = 0
            while (true) {
                if (text != null
                        && i < minOf(tempEditable.length, text.length)
                        && tempEditable[i] == text[i]) {
                    i++; continue
                }
                if (diff > 0) // If insert
                    tempEditable.insert(i, text, i, i + diff)
                else if (diff > -tempEditable.length) // If delete
                    tempEditable.delete(i, i - diff)
                break
            }
        } else { // If replace
            tempEditable.replace(selectionStart, selectionEnd, text)
        }
    }

    override fun finishComposingText(): Boolean {
        val composingStart = getComposingStart(editText.text)
        val result = super.finishComposingText()
        shiftMarkups(tempEditable, 0, tempEditable.length, editText.text, composingStart, move = false)
        tempEditable.clear()
        tempEditable.clearSpans()
        return result
    }

    override fun commitText(text: CharSequence?, newCursorPosition: Int): Boolean {
        captureModifications(text, newCursorPosition)

        val result = super.commitText(text, newCursorPosition)
        val composingStart = getComposingStart(editText.text)
        shiftMarkups(tempEditable, 0, tempEditable.length, editText.text, composingStart, move = false)
        tempEditable.clear()
        tempEditable.clearSpans()
        return result
    }

    /**
     * Moves the markups from the region `start...end` in `source` to the region
     * `destOff...destOff+end-start` in `dest`. Markups in `source` that begin
     * before `start` or end after `end` but overlap this range are trimmed
     * as if they began at `start` or ended at `end`.
     */
    private fun shiftMarkups(source: Spannable, start: Int, end: Int, dest: Spannable, destOff: Int = 0,
                             move: Boolean = true, extendInclusiveSpansBy: Int = 0) {
        val spans = source.getSpans(start, end, Markup::class.java)
        for (it in spans) {
            val st = source.getSpanStart(it)
            val en = source.getSpanEnd(it)
            if (inside(start, end, st, en)) {
                var fl = source.getSpanFlags(it)
//                if (fl == Spanned.SPAN_EXCLUSIVE_INCLUSIVE && en == end)
////                    en += extendInclusiveSpansBy
//                if (fl == Spanned.SPAN_MARK_MARK && st == end) {
//                    fl = Spanned.SPAN_EXCLUSIVE_INCLUSIVE
////                    en += extendInclusiveSpansBy
//                }
                if (move) it.removeInternal(source)
                it.applyInternal(dest, st - start + destOff, en - start + destOff, fl)
            }
        }
    }

    override fun performEditorAction(editorAction: Int): Boolean {
        return super.performEditorAction(editorAction)
    }

    override fun sendKeyEvent(event: KeyEvent?): Boolean {
        return super.sendKeyEvent(event)
    }

    override fun beginBatchEdit(): Boolean {
        return super.beginBatchEdit()
    }

    override fun clearMetaKeyStates(states: Int): Boolean {
        return super.clearMetaKeyStates(states)
    }

    override fun commitCompletion(text: CompletionInfo?): Boolean {
        return super.commitCompletion(text)
    }

    override fun commitContent(inputContentInfo: InputContentInfo?, flags: Int, opts: Bundle?): Boolean {
        return super.commitContent(inputContentInfo, flags, opts)
    }

    override fun commitCorrection(correctionInfo: CorrectionInfo?): Boolean {
        return super.commitCorrection(correctionInfo)
    }

    override fun endBatchEdit(): Boolean {
        return super.endBatchEdit()
    }

    override fun deleteSurroundingText(beforeLength: Int, afterLength: Int): Boolean {
        delete(beforeLength, afterLength)
        return super.deleteSurroundingText(beforeLength, afterLength)
    }

    override fun deleteSurroundingTextInCodePoints(beforeLength: Int, afterLength: Int): Boolean {
        return super.deleteSurroundingTextInCodePoints(beforeLength, afterLength)
    }

    private fun delete(beforeLength: Int, afterLength: Int) {
        var a = editText.selectionStart
        var b = editText.selectionEnd
        if (a == b) {
            val content = editText.text
            // ignore the composing text.
            var ca = getComposingStart(content)
            var cb = getComposingEnd(content)
            if (cb < ca) {
                val tmp = ca
                ca = cb
                cb = tmp
            }
            if (ca != -1 && cb != -1) {
                if (ca < a) a = ca
                if (cb > b) b = cb
            }
            var start = a
            var end = b
            if (beforeLength > 0)
                start = maxOf(a - beforeLength, 0)
            if (afterLength > 0)
                end = minOf(b + afterLength,content.length)
            if(start < end) {
                captureVulnerableSpans(start, end)
                content.delete(start, end)
            }
        }
    }
}
