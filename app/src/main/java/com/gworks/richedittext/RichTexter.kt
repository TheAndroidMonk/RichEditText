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

import android.widget.TextView
import com.gworks.richedittext.converters.*
import com.gworks.richedittext.markups.*

open class RichTexter(// The text view which acts as rich text view.
        open val richTextView: TextView) {

    fun isApplied(markupClass: Class<Markup>): Boolean {
        return isApplied(richTextView, markupClass)
    }

    fun isAppliedInSelection(markupClass: Class<Markup>): Boolean {
        return isAppliedInSelection(richTextView, markupClass)
    }

    fun isAppliedInRange(markupClass: Class<Markup>, from: Int, to: Int): Boolean {
        return isAppliedInRange(richTextView, markupClass, from, to)
    }

    fun isApplied(markup: Markup): Boolean {
        return isApplied(richTextView, markup)
    }

    fun isAppliedInSelection(markup: Markup): Boolean {
        return isAppliedInSelection(richTextView, markup)
    }

    fun isAppliedInRange(markup: Markup, from: Int, to: Int): Boolean {
        return isAppliedInRange(richTextView, markup, from, to)
    }

    /**
     * Returns all the markups applied strictly inside the current selection.
     */
    fun getAppliedMarkupsInSelection(): List<Markup> {
        return getAppliedMarkupsInSelection(richTextView)
    }

    /**
     * Returns all the markups applied in this whole text.
     */
    fun getAppliedMarkups(): List<Markup> {
        return getAppliedMarkups(richTextView)
    }

    fun getMarkupFromSelection(markupClass: Class<out Markup>): Markup? {
        return getMarkupFromSelection(richTextView, markupClass)
    }

    /**
     * Returns all the markups applied strictly inside the given range [from, to).
     *
     * @param from from inclusive
     * @param to to exclusive
     */
    fun getAppliedMarkupsInRange(from: Int, to: Int): List<Markup> {
        return getAppliedMarkupsInRange(richTextView, from, to)
    }

    /**
     * Returns the rich text in the text view as plain text (i.e. String).
     */
    fun getPlainText(): String {
        return getPlainText(richTextView)
    }

    fun setPlainText(text: String) {
        setPlainText(richTextView, text)
    }

    @JvmOverloads
    fun getHtml(unknownMarkupHandler: MarkupConverter.UnknownMarkupHandler? = null): String {
        return getHtml(richTextView, unknownMarkupHandler);
    }

    @JvmOverloads
    open fun setHtml(html: String, markupFactory: (String) -> Class<out Markup>? = defaultMarkupFactory, unknownTagHandler: UnknownTagHandler? = null){
        setHtml(richTextView, html, markupFactory, unknownTagHandler)
    }
}