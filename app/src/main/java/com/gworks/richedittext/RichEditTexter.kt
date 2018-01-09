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
import android.text.Spannable
import android.text.Spanned
import android.text.TextWatcher
import android.widget.EditText
import com.gworks.richedittext.markups.AttributedMarkup
import com.gworks.richedittext.markups.Markup

class RichEditTexter(editText: EditText) : RichTexter(editText) {

    private val textWatcher = object : TextWatcher {
        private var markupMarks: List<Markup>? = null
        private var replacedLength: Int = 0

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            //TODO need to remove the spans in text to be removed

            if (count == 0) {
                //Only 0 -length markups need to be replaced
                markupMarks = getAppliedMarkupsInRange(start, start + count)
                replacedLength = after
            }
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            //TODO need to handle the spans in newly added text
        }

        override fun afterTextChanged(s: Editable) {
            if (markupMarks != null) {
                for (markup in markupMarks!!) {
                    val spanStart = markup.getSpanStart(s)
                    s.removeSpan(markup)
                    s.setSpan(markup, spanStart, replacedLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                markupMarks = null
            }
        }
    }

    override val richTextView: EditText
        get() = super.richTextView as EditText

    init {
        editText.addTextChangedListener(textWatcher)
    }

    fun apply(markupType: Class<out Markup>, value: Any?) {
        apply(createMarkup(markupType, value))
    }

    /**
     * Applies the given markup in the given range.
     *
     * @param markup markup to apply
     * @param from inclusive
     * @param to exclusive
     */
    fun apply(markup: Markup, from: Int = richTextView.selectionStart, to: Int = richTextView.selectionEnd) {
        applyInternal(markup, from, to)
    }

    /**
     * Applies the given markup in the given range.
     *
     * @param markup markup to apply
     * @param from inclusive
     * @param to exclusive
     */
    private fun applyInternal(markup: Markup, from: Int, to: Int) {
        markup.applyInternal(richTextView.text, from, to,
                if (from == to) Spannable.SPAN_MARK_MARK else Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
//        addToSpanTransitions(markup, from, to)
    }

    fun remove(markupType: Class<out Markup>) {
        remove(markupType, richTextView.selectionStart, richTextView.selectionEnd)
    }

    fun remove(markupType: Class<out Markup>, from: Int, to: Int) {
        getAppliedMarkupsInRange(from, to).forEach({
            if (it.javaClass == markupType)
                removeInternal(it, from, to)
        })
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

                //First remove from the old range and reapply if splittable.
//                removeFromSpanTransitions(markup, start, end)
                markup.removeInternal(text)

                //If the markup is splittable apply in the outer region.
                if (markup.isSplittable) {
                    var reused = false
                    if (start < from) {
                        //The removed markup is reused here.
                        applyInternal(markup, start, from)
                        reused = true
                    }
                    if (end > to) {
                        val value = (markup as? AttributedMarkup<*>)?.attributes
                        //If not reused above reuse here.
                        applyInternal(if (reused) createMarkup(markup.javaClass, value) else markup, to, end)
                    }
                }
            }
        }
    }

    fun update(markupType: Class<out Markup>, value: Any?) {
        remove(markupType)
        apply(markupType, value)
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
            apply(markupType, value)
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

    companion object {

        private val TAG = "@RichEditTexter"
    }

}