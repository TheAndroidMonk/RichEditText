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

import android.text.Spanned
import android.widget.TextView
import com.gworks.richedittext.converters.*
import com.gworks.richedittext.markups.*
import java.util.*

open class RichTexter(// The text view which acts as rich text view.
        open val richTextView: TextView) {

    fun isApplied(markupClass: Class<Markup>): Boolean {
        return isAppliedInRange(markupClass, 0, richTextView.length())
    }

    fun isAppliedInSelection(markupClass: Class<Markup>): Boolean {
        return isAppliedInRange(markupClass, richTextView.selectionStart, richTextView.selectionEnd)
    }

    fun isAppliedInRange(markupClass: Class<Markup>, from: Int, to: Int): Boolean {
        val text = richTextView.text
        return text is Spanned && text.getSpans<Markup>(from, to, markupClass).isNotEmpty()
    }

    fun isApplied(markup: Markup): Boolean {
        val text = richTextView.text
        return text is Spanned && text.getSpanStart(markup) >= 0
    }

    fun isAppliedInSelection(markup: Markup): Boolean {
        return isAppliedInRange(markup, richTextView.selectionStart, richTextView.selectionEnd)
    }

    fun isAppliedInRange(markup: Markup, from: Int, to: Int): Boolean {
        val text = richTextView.text
        if (text is Spanned) {
            val start = text.getSpanStart(markup)
            val end = text.getSpanEnd(markup)
            return start > 0 && start >= from && start < to && end > 0 && end > from && end <= to
        }
        return false
    }

    /**
     * Returns all the markups applied strictly inside the current selection.
     */
    fun getAppliedMarkupsInSelection(): List<Markup> {
        return getAppliedMarkupsInRange(richTextView.selectionStart, richTextView.selectionEnd)
    }

    /**
     * Returns all the markups applied in this whole text.
     */
    fun getAppliedMarkups(): List<Markup> {
        return getAppliedMarkupsInRange(0, richTextView.length())
    }

    /**
     * Returns all the markups applied strictly inside the given range [from, to).
     *
     * @param from from inclusive
     * @param to to exclusive
     */
    fun getAppliedMarkupsInRange(from: Int, to: Int): List<Markup> {
        val text = richTextView.text
        return if (text !is Spanned) emptyList()
        else Arrays.asList(*text.getSpans(from, to, Markup::class.java))
    }

    /**
     * Returns the rich text in the text view as plain text (i.e. String).
     */
    fun getPlainText(): String {
        return richTextView.text.toString()
    }

    fun setPlainText(text: String) {
        richTextView.text = text
    }

    fun getHtml(unknownMarkupHandler: MarkupConverter.UnknownMarkupHandler? = null): String {
        return if (richTextView.text !is Spanned) getPlainText()
        else toHtml(richTextView.text as Spanned, unknownMarkupHandler)
    }

    open fun setHtml(html: String, markupFactory: MarkupFactory = defaultMarkupFactory, unknownTagHandler: UnknownTagHandler? = null){
        richTextView.text = fromHtml(html, markupFactory, unknownTagHandler, enableContinuousEditing = false)
    }
}