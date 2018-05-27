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
import com.gworks.richedittext.converters.UnknownTagHandler
import com.gworks.richedittext.converters.fromHtml
import com.gworks.richedittext.markups.List
import com.gworks.richedittext.markups.Markup

class RichEditTexter(override val richTextView: EditText,
                     val enableContinuousEditing: Boolean = true) : RichTexter(richTextView) {

    init {
        richTextView.addTextChangedListener(MyWatcher(this))
        richTextView.inputType = InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
    }

    override fun setHtml(html: String, markupFactory: (String) -> Class<out Markup>?, unknownTagHandler: UnknownTagHandler?) {
        richTextView.setText(fromHtml(html, markupFactory, unknownTagHandler, enableContinuousEditing = enableContinuousEditing))
    }

    fun applyInSelection(markupType: Class<out Markup>, value: Any?) {
        applyInSelection(richTextView, markupType, value, enableContinuousEditing)
    }

    fun applyInRange(markupType: Class<out Markup>, value: Any?, from: Int, to: Int) {
        applyInRange(richTextView, markupType, value, from, to, enableContinuousEditing)
    }

    /**
     * Applies the given markup in the current selection.
     *
     * @param markup markup to apply
     */
    fun applyInSelection(markup: Markup) {
        applyInSelection(richTextView, markup, enableContinuousEditing)
    }

    /**
     * Applies the given markup in the given range.
     *
     * @param markup markup to apply
     * @param from inclusive
     * @param to exclusive
     */
    fun applyInRange(markup: Markup, from: Int, to: Int, flags: Int = getSpanFlag(from, to, enableContinuousEditing)) {
        applyInRange(richTextView, markup, from, to, flags)
    }

    fun removeInSelection(markupType: Class<out Markup>) {
        removeInSelection(richTextView, markupType, enableContinuousEditing)
    }

    fun removeInRange(markupType: Class<out Markup>, from: Int, to: Int) {
        removeInRange(richTextView, markupType, from, to, enableContinuousEditing)
    }

    /**
     * Removes all the markups from the given range or current selection if no range is given.
     *
     * @param from inclusive
     * @param to exclusive
     */
    fun removeAllInRange(from: Int, to: Int) {
        removeAllInRange(richTextView, from, to, enableContinuousEditing)
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
        removeInternal(richTextView, markup, from, to, enableContinuousEditing)
    }

    /**
     * Call this when a markup menu item is clicked. This method takes care of toggling the
     * markup, splitting the markup, updating the markup, etc.
     *
     * @param markupType
     * @param value
     */
    fun onMarkupMenuClicked(markupType: Class<out Markup>, value: Any?, start: Int, end: Int) {
        onMarkupMenuClicked(richTextView, markupType, value, start, end, enableContinuousEditing)
    }

    fun onMarkupMenuClicked(markupType: Class<out Markup>, value: Any?) {
        onMarkupMenuClicked(richTextView, markupType, value, enableContinuousEditing)
    }

    fun onParagraphMarkupMenuClicked(markupType: Class<out Markup>, value: Any?) {
        onParagraphMarkupMenuClicked(richTextView, markupType, value, enableContinuousEditing)
    }

    private companion object {

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

            private var newLineAffected = false

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

                // Removing one or more newline from the text sdf
                newLineAffected = s.indexOf('\n', start, start + count) in start..start + count
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // Adding one or more newline to the text
                newLineAffected = s.indexOf('\n', start, start + count) in start..start + count
            }

            override fun afterTextChanged(s: Editable) {

                if (operation != NONE) {

                    val spans = s.getSpans(start, start + 1, Markup::class.java)
                    for (it in spans) {
                        if (it is List<*> && newLineAffected)
                            it.reApply(s, s.getSpanStart(it), s.getSpanEnd(it))
                        if (operation != DELETE && s.getSpanStart(it) == start && s.getSpanEnd(it) == start) {
                            it.removeInternal(s)
                            it.applyInternal(s, start, start + after,
                                    getSpanFlag(start, start + after, richTexter.enableContinuousEditing))
                        }
                        if (operation == DELETE && s.getSpanStart(it) == start && s.getSpanEnd(it) == start) {
                            it.removeInternal(s)
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