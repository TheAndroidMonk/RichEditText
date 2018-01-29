package com.gworks.richedittext

import android.text.Spannable
import android.view.inputmethod.BaseInputConnection
import com.gworks.richedittext.markups.AttributedMarkup
import com.gworks.richedittext.markups.Markup

fun updateSpanFlags(text: Spannable, span: Any?, flags: Int) {
    text.setSpan(span, text.getSpanStart(span), text.getSpanEnd(span), flags)
}

fun CharSequence.indexOf(char: Char, start: Int = 0, limit: Int = this.length, ignoreCase: Boolean = false) : Int {
    val result = indexOf(char, start, ignoreCase)
    return if (result >= 0) minOf(result, limit) else limit
}

fun CharSequence.leftIndexOf(char: Char, start: Int = this.length, limit: Int = 0) : Int {
    var st = 0
    for (i in minOf(this.length - 1, start) downTo limit) {
        if (this[i] == char) {
            st = i
            break
        }
    }
    return st
}

fun Appendable.appendPlain(text: CharSequence, start: Int = 0, end: Int = start + text.length) {
    for (i in start until end) this.append(text[i])
}

/**
 * Returns true if a and b are equal, including if they are both null up to a certain range.
 * @param a first CharSequence to check
 * @param b second CharSequence to check
 * @return true if a and b are equal
 */
fun equalsInRange(a: CharSequence?,b: CharSequence?, length: Int, offsetA: Int = 0,  offsetB: Int = 0): Boolean {
    return (a === b) || matchingLength(a, b, length, offsetA, offsetB) == length
}

fun matchingLength(a: CharSequence?, b: CharSequence?, limit: Int, offsetA: Int = 0, offsetB: Int = 0): Int {
    if (a != null && b != null) {
        val lim = minOf(limit, minOf(a.length, b.length))
        for (i in 0 until lim) {
            if (a[offsetA + i] != b[offsetB + i]) return i + 1
        }
        return lim
    }
    return -1
}

fun getComposingLength(spanned: Spannable) = getComposingEnd(spanned) - getComposingStart(spanned)

fun getComposingStart(spanned: Spannable) = BaseInputConnection.getComposingSpanStart(spanned)

fun getComposingEnd(spanned: Spannable) = BaseInputConnection.getComposingSpanEnd(spanned)

fun composingRegionChanged(spanned: Spannable, start: Int, end: Int)= getComposingStart(spanned) != start || getComposingEnd(spanned) != end

fun isAttributed(markupType: Class<out Markup>) = AttributedMarkup::class.javaObjectType.isAssignableFrom(markupType)

fun inside(rangeSt: Int, rangeEn: Int, from: Int, to: Int) = from >= rangeSt && to <= rangeEn
