package com.gworks.richedittext

import android.text.Spanned
import android.widget.EditText
import android.widget.TextView
import com.gworks.richedittext.converters.*
import com.gworks.richedittext.markups.AttributedMarkup
import com.gworks.richedittext.markups.Markup
import java.util.*

fun isApplied(richTextView: TextView, markupClass: Class<Markup>): Boolean {
    return isAppliedInRange(richTextView, markupClass, 0, richTextView.length())
}

fun isAppliedInSelection(richTextView: TextView, markupClass: Class<Markup>): Boolean {
    return isAppliedInRange(richTextView, markupClass, richTextView.selectionStart, richTextView.selectionEnd)
}

fun isAppliedInRange(richTextView: TextView, markupClass: Class<Markup>, from: Int, to: Int): Boolean {
    val text = richTextView.text
    return text is Spanned && text.getSpans<Markup>(from, to, markupClass).isNotEmpty()
}

fun isApplied(richTextView: TextView, markup: Markup): Boolean {
    val text = richTextView.text
    return text is Spanned && text.getSpanStart(markup) >= 0
}

fun isAppliedInSelection(richTextView: TextView, markup: Markup): Boolean {
    return isAppliedInRange(richTextView, markup, richTextView.selectionStart, richTextView.selectionEnd)
}

fun isAppliedInRange(richTextView: TextView, markup: Markup, from: Int, to: Int): Boolean {
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
fun getAppliedMarkupsInSelection(richTextView: TextView): List<Markup> {
    return getAppliedMarkupsInRange(richTextView, richTextView.selectionStart, richTextView.selectionEnd)
}

/**
 * Returns all the markups applied in this whole text.
 */
fun getAppliedMarkups(richTextView: TextView): List<Markup> {
    return getAppliedMarkupsInRange(richTextView, 0, richTextView.length())
}

//FIXME quick method
fun getMarkupFromSelection(richTextView: TextView, markupClass: Class<out Markup>): Markup? {

    val text = richTextView.text
    if(text is Spanned){
        val markups = text.getSpans(richTextView.selectionStart, richTextView.selectionEnd,markupClass)
        if(markups.isNotEmpty())
            return markups[0]
    }
    return null
}

/**
 * Returns all the markups applied strictly inside the given range [from, to).
 *
 * @param from from inclusive
 * @param to to exclusive
 */
fun getAppliedMarkupsInRange(richTextView: TextView, from: Int, to: Int): List<Markup> {
    val text = richTextView.text
    return if (text !is Spanned) emptyList()
    else Arrays.asList(*text.getSpans(from, to, Markup::class.java))
}

/**
 * Returns the rich text in the text view as plain text (i.e. String).
 */
fun getPlainText(richTextView: TextView): String {
    return richTextView.text.toString()
}

fun setPlainText(richTextView: TextView, text: String) {
    richTextView.text = text
}

@JvmOverloads
fun getHtml(richTextView: TextView, unknownMarkupHandler: MarkupConverter.UnknownMarkupHandler? = null): String {
    return if (richTextView.text !is Spanned) getPlainText(richTextView)
    else toHtml(richTextView.text as Spanned, unknownMarkupHandler)
}

@JvmOverloads
fun setHtml(richTextView: TextView, html: String, markupFactory: (String) -> Class<out Markup>? = defaultMarkupFactory, unknownTagHandler: UnknownTagHandler? = null){
    richTextView.text = fromHtml(html, markupFactory, unknownTagHandler, enableContinuousEditing = false)
}

fun applyInSelection(richTextView: EditText, markupType: Class<out Markup>, value: Any?, enableContinuousEditing: Boolean) {
    applyInRange(richTextView, createMarkup(markupType, value), richTextView.selectionStart, richTextView.selectionEnd, enableContinuousEditing)
}

fun applyInRange(richTextView: EditText, markupType: Class<out Markup>, value: Any?, from: Int, to: Int, enableContinuousEditing: Boolean) {
    applyInRange(richTextView, createMarkup(markupType, value), from, to, enableContinuousEditing)
}

/**
 * Applies the given markup in the current selection.
 *
 * @param markup markup to apply
 */
fun applyInSelection(richTextView: EditText, markup: Markup, enableContinuousEditing: Boolean) {
    applyInRange(richTextView, markup, richTextView.selectionStart, richTextView.selectionEnd, enableContinuousEditing)
}

/**
 * Applies the given markup in the given range.
 *
 * @param markup markup to apply
 * @param from inclusive
 * @param to exclusive
 */
fun applyInRange(richTextView: EditText, markup: Markup, from: Int, to: Int, flags: Int) {
//        if (!enableContinuousEditing || to > from)
    markup.applyInternal(richTextView.text, from, to, flags)
}

/**
 * Applies the given markup in the given range.
 *
 * @param markup markup to apply
 * @param from inclusive
 * @param to exclusive
 */
fun applyInRange(richTextView: EditText, markup: Markup, from: Int, to: Int, enableContinuousEditing: Boolean) {
    applyInRange(richTextView, markup, from, to, getSpanFlag(from, to, enableContinuousEditing))
}

fun getSpanFlag(from: Int, to: Int, enableContinuousEditing: Boolean): Int {
    if (!enableContinuousEditing) return Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    return if (from == to) Spanned.SPAN_MARK_MARK else Spanned.SPAN_EXCLUSIVE_INCLUSIVE
}

fun removeInSelection(richTextView: EditText, markupType: Class<out Markup>, enableContinuousEditing: Boolean) {
    removeInRange(richTextView, markupType, richTextView.selectionStart, richTextView.selectionEnd, enableContinuousEditing)
}

fun removeInRange(richTextView: EditText, markupType: Class<out Markup>, from: Int, to: Int, enableContinuousEditing: Boolean) {
    getAppliedMarkupsInRange(richTextView, from, to).forEach {
        if (it.javaClass == markupType)
            removeInternal(richTextView, it, from, to, enableContinuousEditing)
    }
}

/**
 * Removes all the markups from the given range or current selection if no range is given.
 *
 * @param from inclusive
 * @param to exclusive
 */
fun removeAllInRange(richTextView: EditText, from: Int, to: Int, enableContinuousEditing: Boolean) {
    getAppliedMarkupsInRange(richTextView, from, to).forEach {
        removeInternal(richTextView, it, from, to, enableContinuousEditing)
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
fun removeInternal(richTextView: EditText, markup: Markup?, from: Int, to: Int, enableContinuousEditing: Boolean) {

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
                        applyInRange(richTextView, markup, start, from, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    else
                        applyInRange(richTextView, markup, start, from, enableContinuousEditing)
                    reused = true
                }
                if (end > to)
                // If not reused above reuse here.
                    applyInRange(
                            richTextView,
                            if (reused) createMarkup(markup.javaClass, (markup as? AttributedMarkup<*>)?.attributes)
                            else markup,
                            to, end,
                            enableContinuousEditing
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
fun onMarkupMenuClicked(richTextView: EditText, markupType: Class<out Markup>, value: Any?, start: Int, end: Int, enableContinuousEditing: Boolean) {

    var toggled = false

    for (existing in getAppliedMarkupsInRange(richTextView, start, end)) {
        if (!existing.canExistWith(markupType)) {
            removeInternal(richTextView, existing, start, end, enableContinuousEditing)
            if (existing.javaClass == markupType)
            // If it can not exist with itself toggle.
                toggled = true
        }
    }
    // Attributed markups are updated (reapplied) hence always check them.
    if (!toggled /*|| value != null && isAttributed(markupType)*/)
        applyInRange(richTextView, markupType, value, start, end, enableContinuousEditing)
}

fun onMarkupMenuClicked(richTextView: EditText, markupType: Class<out Markup>, value: Any?, enableContinuousEditing: Boolean) {
    onMarkupMenuClicked(richTextView, markupType, value, richTextView.selectionStart, richTextView.selectionEnd, enableContinuousEditing)
}

fun onParagraphMarkupMenuClicked(richTextView: EditText, markupType: Class<out Markup>, value: Any?, enableContinuousEditing: Boolean) {
    val text = richTextView.text
    var st = text.leftIndexOf('\n', richTextView.selectionStart)
    if (richTextView.selectionStart == richTextView.selectionEnd
            && richTextView.selectionEnd < text.length
            && text[richTextView.selectionEnd] == '\n')
        st = text.leftIndexOf('\n', richTextView.selectionStart - 1)
    val en = text.indexOf('\n', richTextView.selectionEnd)
    onMarkupMenuClicked(richTextView, markupType, value,
            if (st == 0) 0 else st + 1,
            if (en == text.length) text.length else en + 1,
            enableContinuousEditing)
}
