/*
 * Copyright 2018 TheAndroidMonk
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.gworks.richedittext

import android.text.*
import android.widget.EditText
import com.gworks.richedittext.converters.MarkupFactory
import com.gworks.richedittext.converters.UnknownTagHandler
import com.gworks.richedittext.converters.fromHtml
import com.gworks.richedittext.markups.AttributedMarkup
import com.gworks.richedittext.markups.Markup

class RichEditTexter(override val richTextView: EditText,
                     val enableContinuousEditing: Boolean = true) : RichTexter(richTextView) {

    init {
        richTextView.addTextChangedListener(MyWatcher(this))
    }

    override fun setHtml(html: String, markupFactory: MarkupFactory, unknownTagHandler: UnknownTagHandler?) {
        richTextView.setText(fromHtml(html, markupFactory, unknownTagHandler, enableContinuousEditing = enableContinuousEditing))
    }

    fun applyInSelection(markupType: Class<out Markup>, value: Any?) {
        applyInRange(createMarkup(markupType, value), richTextView.selectionStart, richTextView.selectionEnd)
    }

    fun applyInRange(markupType: Class<out Markup>, value: Any?, from: Int, to: Int) {
        applyInRange(createMarkup(markupType, value), from, to)
    }

    /**
     * Applies the given markup in the current selection.
     *
     * @param markup markup to apply
     */
    fun applyInSelection(markup: Markup) {
        applyInRange(markup, richTextView.selectionStart, richTextView.selectionEnd)
    }

    /**
     * Applies the given markup in the given range.
     *
     * @param markup markup to apply
     * @param from inclusive
     * @param to exclusive
     */
    fun applyInRange(markup: Markup, from: Int, to: Int,
                     flags: Int = getSpanFlag(from, to)) {
//        if (!enableContinuousEditing || to > from)
            markup.applyInternal(richTextView.text, from, to, flags)
    }


    internal fun getSpanFlag(from: Int, to: Int) : Int{
        if (!enableContinuousEditing) return Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        return if (from == to) Spanned.SPAN_MARK_MARK else Spanned.SPAN_EXCLUSIVE_INCLUSIVE
    }

    fun removeInSelection(markupType: Class<out Markup>) {
        removeInRange(markupType, richTextView.selectionStart, richTextView.selectionEnd)
    }

    fun removeInRange(markupType: Class<out Markup>, from: Int, to: Int) {
        getAppliedMarkupsInRange(from, to).forEach {
            if (it.javaClass == markupType)
                removeInternal(it, from, to)
        }
    }

    /**
     * Removes all the markups from the given range or current selection if no range is given.
     *
     * @param from inclusive
     * @param to exclusive
     */
    fun removeAllInRange(from: Int, to: Int) {
        getAppliedMarkupsInRange(from, to).forEach {
            removeInternal(it, from, to)
        }
    }

    /**
     * Removes the given markup from the given range. If the markup spans outside the
     * given range the markup is retained in the outer region if the markup is splittable.
     * Otherwise the markup is removed entirely.
     *
     * @param markup markup to removeInSelection
     * @param from inclusive
     * @param to exclusive
     */
    private fun removeInternal(markup: Markup?, from: Int, to: Int) {

        if (markup != null) {
            val text = richTextView.text
            val start = markup.getSpanStart(text)
            val end = markup.getSpanEnd(text)

            // If the markup is really applied in the text.
            if (start >= 0) {

                // Capture the flag before removing to "toggle" if necessary.
                val oldFlag = text.getSpanFlags(markup)

                // First removeInSelection and reapply if splittable.
                markup.removeInternal(text)

                // If the markup is splittable apply in the outer region.
                if (markup.isSplittable) {

                    var reused = false
                    if (start < from) {

                        val selectionIsZero = from == to
                        val alreadyThere = to <= end
                        // The removed markup is reused in if and else.
                        if (selectionIsZero && alreadyThere) // If the selection is zero then "toggle" the flags.
                            applyInRange(markup, start, from, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                        else
                            applyInRange(markup, start, from)
                        reused = true
                    }
                    if (end > to)
                    // If not reused above reuse here.
                        applyInRange(
                                if (reused) createMarkup(markup.javaClass, (markup as? AttributedMarkup<*>)?.attributes)
                                else markup,
                                to, end
                        )
                }
            }
        }
    }

    /**
     * Call this when a markup menu item is clicked. This method takes care of toggling the
     * markup, splitting the markup, updating the markup, etc.
     *
     * @param markupType
     * @param value
     */
    fun onMarkupMenuClicked(markupType: Class<out Markup>, value: Any?, start: Int, end: Int) {

        var toggled = false

        for (existing in getAppliedMarkupsInRange(start, end)) {
            if (!existing.canExistWith(markupType)) {
                removeInternal(existing, start, end)
                if (existing.javaClass == markupType)
                // If it can not exist with itself toggle.
                    toggled = true
            }
        }
        // Attributed markups are updated (reapplied) hence always check them.
        if (!toggled /*|| value != null && isAttributed(markupType)*/)
            applyInRange(markupType, value, start, end)
    }

    fun onMarkupMenuClicked(markupType: Class<out Markup>, value: Any?) {
        onMarkupMenuClicked(markupType, value, richTextView.selectionStart, richTextView.selectionEnd)
    }

    fun onParagraphMarkupMenuClicked(markupType: Class<out Markup>, value: Any?) {
        val text = richTextView.text
        var st = text.leftIndexOf('\n', richTextView.selectionStart)
        if (richTextView.selectionStart == richTextView.selectionEnd
                && richTextView.selectionEnd < text.length
                && text[richTextView.selectionEnd] == '\n')
            st = text.leftIndexOf('\n', richTextView.selectionStart - 1)
        val en = text.indexOf('\n', richTextView.selectionEnd)
        onMarkupMenuClicked(markupType, value,
                if (st == 0) 0 else st + 1,
                if (en == text.length) text.length else en + 1)
    }

    companion object {

        // Constants for edit operation in the EditText.
        private const val NONE = -1
        private const val INSERT = 0
        private const val REPLACE = 1
        private const val DELETE = 2

        private class MyWatcher(val richTexter: RichEditTexter) : TextWatcher {

            private var operation = NONE
            private var start = -1
            private var before = -1
            private var after = -1

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

                if (operation != NONE) return

                this.start = start
                this.before = count
                this.after = after

                operation = when {
                    count == 0 && after == 0 -> NONE
                    count == 0 -> INSERT
                    after == 0 -> DELETE
                    else -> REPLACE
                }
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                //TODO need to handle the spans in newly added text
            }

            override fun afterTextChanged(s: Editable) {

                if (operation == INSERT || operation == REPLACE) {

                    val spans = richTexter.getAppliedMarkupsInRange(start, start)
                    for(it in spans){
                        if (s.getSpanStart(it) == start && s.getSpanEnd(it) == start) {
                            it.removeInternal(s)
                            it.applyInternal(s, start, start + after, richTexter.getSpanFlag(start, start + after))
                        }
                    }

                    // Mark the operation as completed; along with the first stmt of
                    // beforeTextChanged() this prevents the infinite loop.
                    operation = NONE
                }
            }
        }

    }
}