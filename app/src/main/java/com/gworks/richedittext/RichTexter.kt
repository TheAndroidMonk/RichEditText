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
import android.util.SparseArray
import android.widget.TextView
import com.gworks.richedittext.markups.HtmlConverter
import com.gworks.richedittext.markups.Markup
import com.gworks.richedittext.markups.MarkupConverter
import java.util.*

open class RichTexter(// The text view which acts as rich text view.
        open val richTextView: TextView) {

    // Mapping between the index and its span transitions in the text.
    private val spanTransitions =  SparseArray<SpanTransition>()

    /**
     * Returns the rich text in the text view as plain text (i.e. String).
     */
    val plainText: String
        get() = richTextView.text.toString()

    /**
     * Returns the html equivalent of the rich text in the text view.
     */
    val html: String
        get() = getHtml(null)

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
     *
     * @param from from inclusive
     * @param to to exclusive
     */
    fun getAppliedMarkupsInSelection(): List<Markup> {
        return getAppliedMarkupsInRange(richTextView.selectionStart, richTextView.selectionEnd)
    }

    /**
     * Returns all the markups applied in this whole text.
     *
     * @param from from inclusive
     * @param to to exclusive
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
     * Returns the markups starting at the given index.
     *
     * @param index start index
     * @return unmodifiable list of markups
     */
    fun getSpansStartingAt(index: Int): List<Markup> {
        val spans = spansStartingAt(index)
        return if (spans == null) emptyList() else Collections.unmodifiableList(spans)
    }

    /**
     * Returns the markups ending at the given index.
     *
     * @param index end index
     * @return unmodifiable list of markups
     */
    fun getSpansEndingAt(index: Int): List<Markup> {
        val spans = spansEndingAt(index)
        return if (spans == null) emptyList() else Collections.unmodifiableList(spans)
    }

    private fun spansStartingAt(index: Int): List<Markup>? {
        return spanTransitions.get(index)?.startingSpans
    }

    private fun spansEndingAt(index: Int): List<Markup>? {
        return spanTransitions.get(index)?.endingSpans
    }

    /**
     * Returns the html equivalent of the rich text in the text view.
     *
     * @param unknownMarkupHandler the handler to handle the unknown markups.
     */
    private fun getHtml(unknownMarkupHandler: MarkupConverter.UnknownMarkupHandler?): String {

        val text = richTextView.text
        if (text !is Spanned)
            return text.toString()

        val html = StringBuilder(text.length)
        val htmlConverter = HtmlConverter(unknownMarkupHandler!!)
        val openSpans = LinkedList<Markup>()

        var processed = 0
        val end = text.length
        while (processed < end) {

            // Get the next span transition.
            val transitionIndex = text.nextSpanTransition(processed, end, null)
            if (transitionIndex > processed)
                html.append(text.subSequence(processed, transitionIndex))

            val startingSpans = spansStartingAt(transitionIndex)
            if (startingSpans != null) {
                for (startingSpan in startingSpans) {
                    startingSpan.convert(html, htmlConverter, true)
                    // Consider the starting span as an opening span.
                    openSpans.add(startingSpan)
                }
            }

            // Iterate all ending spans at the transition index.
            val endingSpans = spansEndingAt(transitionIndex)
            if (endingSpans != null) {
                for (endingSpan in endingSpans) {
                    val iterator = openSpans.listIterator(openSpans.size)
                    while (iterator.hasPrevious()) {
                        val openSpan = iterator.previous()
                        // If an ending span has a matching open span, consider it as a closing
                        // span and remove the open span, and proceed to next ending span.
                        if (openSpan === endingSpan) {
                            iterator.remove()
                            endingSpan.convert(html, htmlConverter, false)
                            break
                        }
                    }
                }
            }

            processed = transitionIndex
        }
        return if (openSpans.isEmpty())
            html.toString()
        else
            throw IllegalStateException("Spans are not well formed")//TODO Will we really reach this?
    }

    internal fun removeFromSpanTransitions(markup: Markup, from: Int, to: Int) {
        spanTransitions.get(from)?.startingSpans?.remove(markup)
        spanTransitions.get(to)?.endingSpans?.remove(markup)
    }

    internal fun addToSpanTransitions(markup: Markup, from: Int, to: Int) {

        var transitionFrom = spanTransitions.get(from)
        if (transitionFrom == null) {
            transitionFrom = SpanTransition()
            spanTransitions.put(from, transitionFrom)
        }
        transitionFrom.startingSpans.add(markup)

        var transitionTo = spanTransitions.get(to)
        if (transitionTo == null) {
            transitionTo = SpanTransition();
            spanTransitions.put(to, transitionTo)
        }
        transitionTo.endingSpans.add(markup)
    }

    /**
     * Class representing a span transition in the text at a given index.
     */
    internal class SpanTransition {

        //spans starting at this span transition.
        val startingSpans = LinkedList<Markup>()

        //spans ending at this span transition.
        val endingSpans = LinkedList<Markup>()
    }

}
