package com.gworks.richedittext

import android.text.Spanned
import android.widget.EditText
import android.widget.TextView
import com.gworks.richedittext.converters.*
import com.gworks.richedittext.markups.AttributedMarkup
import com.gworks.richedittext.markups.Markup
import java.util.*

/**
 * Tells whether the given markup type is applied in the given text view.
 */
fun isApplied(richTextView: TextView, markupClass: Class<Markup>): Boolean {
    return isAppliedInRange(richTextView, markupClass, 0, richTextView.length())
}

/**
 * Tells whether the given markup type is applied in the given text view's current selection.
 */
fun isAppliedInSelection(richTextView: TextView, markupClass: Class<Markup>): Boolean {
    return isAppliedInRange(richTextView, markupClass, richTextView.selectionStart, richTextView.selectionEnd)
}

/**
 * Tells whether the given markup type is applied in the given text view in the given range [from, to).
 */
fun isAppliedInRange(richTextView: TextView, markupClass: Class<Markup>, from: Int, to: Int): Boolean {
    val text = richTextView.text
    return text is Spanned && text.getSpans<Markup>(from, to, markupClass).isNotEmpty()
}

/**
 * Tells whether the given markup is applied in the given text view.
 */
fun isApplied(richTextView: TextView, markup: Markup): Boolean {
    val text = richTextView.text
    return text is Spanned && text.getSpanStart(markup) >= 0
}

/**
 * Tells whether the given markup is applied in the given text view's current selection.
 */
fun isAppliedInSelection(richTextView: TextView, markup: Markup): Boolean {
    return isAppliedInRange(richTextView, markup, richTextView.selectionStart, richTextView.selectionEnd)
}

/**
 * Tells whether the given markup is applied in the given text view in the given range [from, to).
 */
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
 * Returns all the markups applied **strictly** inside the given text view's current selection.
 */
fun getAppliedMarkupsInSelection(richTextView: TextView): List<Markup> {
    return getAppliedMarkupsInRange(richTextView, richTextView.selectionStart, richTextView.selectionEnd)
}

/**
 * Returns all the markups applied in given text view.
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
 * Returns all the markups applied **strictly** inside the given range [from, to) in the text view.
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

/**
 * Sets the given plain text to the text view.
 */
fun setPlainText(richTextView: TextView, text: String) {
    richTextView.text = text
}

/**
 * Returns the html equivalent of the rich text in given text view.
 *
 * @param unknownMarkupHandler the handler to handle any unknown markups
 */
@JvmOverloads
fun getHtml(richTextView: TextView, unknownMarkupHandler: MarkupConverter.UnknownMarkupHandler? = null): String {
    return if (richTextView.text !is Spanned) getPlainText(richTextView)
    else toHtml(richTextView.text as Spanned, unknownMarkupHandler)
}

/**
 * Sets the rich text equivalent of the given html to the text view.
 *
 * @param markupFactory a factory which creates a markup for given html tag.
 * @param unknownTagHandler a handler to handle any unknown html tag.
 */
@JvmOverloads
fun setHtml(richTextView: TextView, html: String, markupFactory: (String) -> Class<out Markup>? = defaultMarkupFactory, unknownTagHandler: UnknownTagHandler? = null){
    richTextView.text = fromHtml(html, markupFactory, unknownTagHandler, enableContinuousEditing = false)
}

/**
 * Applies a markup of given markup type to the text view's current selection.
 *
 * @param value a value if the markup is AttributedMarkup, pass null otherwise.
 */
fun applyInSelection(richTextView: EditText, markupType: Class<out Markup>, value: Any?, enableContinuousEditing: Boolean) {
    applyInRange(richTextView, createMarkup(markupType, value), richTextView.selectionStart, richTextView.selectionEnd, enableContinuousEditing)
}

/**
 * Applies a markup of given markup type to the text view in given range [from, to).
 *
 * @param value a value if the markup is AttributedMarkup, pass null otherwise.
 */
fun applyInRange(richTextView: EditText, markupType: Class<out Markup>, value: Any?, from: Int, to: Int, enableContinuousEditing: Boolean) {
    applyInRange(richTextView, createMarkup(markupType, value), from, to, enableContinuousEditing)
}


/**
 * Applies the given markup to the text view's current selection.
 */
fun applyInSelection(richTextView: EditText, markup: Markup, enableContinuousEditing: Boolean) {
    applyInRange(richTextView, markup, richTextView.selectionStart, richTextView.selectionEnd, enableContinuousEditing)
}

/**
 * Applies the given markup to the text view in given range [from, to).
 */
fun applyInRange(richTextView: EditText, markup: Markup, from: Int, to: Int, flags: Int) {
//        if (!enableContinuousEditing || to > from)
    markup.applyInternal(richTextView.text, from, to, flags)
}

/**
 * Applies the given markup to the text view in given range [from, to).
 */
fun applyInRange(richTextView: EditText, markup: Markup, from: Int, to: Int, enableContinuousEditing: Boolean) {
    applyInRange(richTextView, markup, from, to, getSpanFlag(from, to, enableContinuousEditing))
}

fun getSpanFlag(from: Int, to: Int, enableContinuousEditing: Boolean): Int {
    if (!enableContinuousEditing) return Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    return if (from == to) Spanned.SPAN_MARK_MARK else Spanned.SPAN_EXCLUSIVE_INCLUSIVE
}

/**
 * Removes all markups of given markup type in the text view's current selection.
 */
fun removeInSelection(richTextView: EditText, markupType: Class<out Markup>, enableContinuousEditing: Boolean) {
    removeInRange(richTextView, markupType, richTextView.selectionStart, richTextView.selectionEnd, enableContinuousEditing)
}

/**
 * Removes all markups of given markup type from the text view in given range [from, to).
 */
fun removeInRange(richTextView: EditText, markupType: Class<out Markup>, from: Int, to: Int, enableContinuousEditing: Boolean) {
    getAppliedMarkupsInRange(richTextView, from, to).forEach {
        if (it.javaClass == markupType)
            removeInternal(richTextView, it, from, to, enableContinuousEditing)
    }
}

/**
 * Removes all the markups from the text view in given range [from, to).
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
                var toggled = false
                val selectionIsZero = from == to
                val alreadyThere = to <= end

                if (start < from) {
                    val flags: Int
                    if (selectionIsZero && alreadyThere) {
                        if (to == end)
                            flags = toggleEnd(oldFlag)
                        else
                            flags = if (isStartInclusive(oldFlag)) Spanned.SPAN_INCLUSIVE_EXCLUSIVE else Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        toggled = true // store we toggled the start or not to decide whether to toggle the end or not.
                    } else
                        flags = getSpanFlag(start, from, enableContinuousEditing)

                    // The removed markup is reused here.
                    applyInRange(richTextView, markup, start, from, flags)
                    reused = true
                }

                if (end > to) {
                    val flags: Int
                    if (!toggled && selectionIsZero && alreadyThere) {
                        if (start == from)
                            flags = toggleStart(oldFlag)
                        else
                            flags = if (isEndInclusive(oldFlag)) Spanned.SPAN_INCLUSIVE_INCLUSIVE else Spanned.SPAN_INCLUSIVE_EXCLUSIVE
                    } else
                        flags = getSpanFlag(to, end, enableContinuousEditing)

                    // If not reused above reuse here.
                    val anotherMarkup = if (reused) createMarkup(markup.javaClass, (markup as? AttributedMarkup<*>)?.attributes) else markup
                    applyInRange(richTextView, anotherMarkup, to, end, flags)
                }
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

fun isStartExclusive(flags: Int) = (flags == Spanned.SPAN_EXCLUSIVE_EXCLUSIVE || flags == Spanned.SPAN_EXCLUSIVE_INCLUSIVE)

fun isStartInclusive(flags: Int) = (flags == Spanned.SPAN_INCLUSIVE_EXCLUSIVE || flags == Spanned.SPAN_INCLUSIVE_INCLUSIVE)

fun isEndExclusive(flags: Int) = (flags == Spanned.SPAN_EXCLUSIVE_EXCLUSIVE || flags == Spanned.SPAN_INCLUSIVE_EXCLUSIVE)

fun isEndInclusive(flags: Int) = (flags == Spanned.SPAN_EXCLUSIVE_INCLUSIVE || flags == Spanned.SPAN_INCLUSIVE_INCLUSIVE)

fun toggleStart(flags: Int) : Int {
    if (isStartExclusive(flags)) {
        if (isEndExclusive(flags)) return Spanned.SPAN_INCLUSIVE_EXCLUSIVE
        else if (isEndInclusive(flags)) return Spanned.SPAN_INCLUSIVE_INCLUSIVE
    } else if (isStartInclusive(flags)) {
        if (isEndExclusive(flags)) return Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        else if (isEndInclusive(flags)) return Spanned.SPAN_EXCLUSIVE_INCLUSIVE
    }
    return flags
}

fun toggleEnd(flags: Int) : Int {
    if (isEndExclusive(flags)) {
        if (isStartExclusive(flags)) return Spanned.SPAN_EXCLUSIVE_INCLUSIVE
        else if (isStartInclusive(flags)) return Spanned.SPAN_INCLUSIVE_INCLUSIVE
    } else if (isEndInclusive(flags)) {
        if (isStartExclusive(flags)) return Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        else if (isStartInclusive(flags)) return Spanned.SPAN_INCLUSIVE_EXCLUSIVE
    }
    return flags
}