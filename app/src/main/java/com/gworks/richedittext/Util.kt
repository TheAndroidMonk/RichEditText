package com.gworks.richedittext

import android.text.Spannable
import com.gworks.richedittext.markups.AttributedMarkup
import com.gworks.richedittext.markups.Markup

fun isAttributed(markupType: Class<out Markup>) : Boolean {
    return AttributedMarkup::class.javaObjectType.isAssignableFrom(markupType)
}

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