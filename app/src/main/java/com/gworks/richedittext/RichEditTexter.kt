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

import android.text.Editable
import android.text.Spanned
import android.text.TextWatcher
import android.widget.EditText
import com.gworks.richedittext.markups.AttributedMarkup
import com.gworks.richedittext.markups.Markup

class RichEditTexter(override val richTextView: EditText) : RichTexter(richTextView) {

    private val textWatcher = object : TextWatcher {

        private val NONE = -1
        private val INSERT = 0
        private val REPLACE = 1
        private val DELETE = 2

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

                val spans = getAppliedMarkupsInRange(start, start)
                spans.forEach({
                    if (s.getSpanStart(it) == start && s.getSpanEnd(it) == start) {
                        it.removeInternal(s)
                        it.applyInternal(s, start, start + after, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
                    }
                })

                // Mark the operation as completed; along with the first stmt of
                // beforeTextChanged() this prevents the infinite loop.
                operation = NONE
            }
        }
    }

    init {
        richTextView.addTextChangedListener(textWatcher)
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
     * @param from inclusive
     * @param to exclusive
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
                     flags: Int = if (from == to) Spanned.SPAN_MARK_MARK else Spanned.SPAN_EXCLUSIVE_INCLUSIVE) {
        markup.applyInternal(richTextView.text, from, to, flags)
    }

    fun remove(markupType: Class<out Markup>) {
        remove(markupType, richTextView.selectionStart, richTextView.selectionEnd)
    }

    fun remove(markupType: Class<out Markup>, from: Int, to: Int) {
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
    fun removeAll(from: Int, to: Int) {
        getAppliedMarkupsInRange(from, to).forEach {
            removeInternal(it, from, to)
        }
    }

    /**
     * Removes the given markup from the given range. If the markup spans outside the
     * given range the markup is retained in the outer region if the markup is splittable.
     * Otherwise the markup is removed entirely.
     *
     * @param markup markup to remove
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

                // First remove and reapply if splittable.
                markup.removeInternal(text)

                // If the markup is splittable apply in the outer region.
                if (markup.isSplittable) {

                    var reused = false
                    if (start < from) {

                        // If the selection is zero then "toggle" the flags.
                        val flag = if (from < to || oldFlag == Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE else Spanned.SPAN_EXCLUSIVE_INCLUSIVE
                        // The removed markup is reused here.
                        applyInRange(markup, start, from, flag)
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

    fun update(markupType: Class<out Markup>, value: Any?) {
        remove(markupType)
        applyInSelection(markupType, value)
    }

    /**
     * Call this when a markup menu item is clicked. This method takes care of toggling the
     * markup, splitting the markup, updating the markup, etc.
     *
     * @param markupType
     * @param value
     */
    fun onMarkupMenuClicked(markupType: Class<out Markup>, value: Any?) {
        val start = richTextView.selectionStart
        val end = richTextView.selectionEnd
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
        if (AttributedMarkup::class.javaObjectType.isAssignableFrom(markupType) || !toggled)
            applyInRange(markupType, value, start, end)
    }

    private fun createMarkup(markupType: Class<out Markup>, value: Any?): Markup {
        try {
            //TODO Add reflection code to create an instance for attributed markups.
            return markupType.newInstance()
        } catch (e: InstantiationException) {
            e.printStackTrace()
            throw e
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
            throw e
        }
    }

}