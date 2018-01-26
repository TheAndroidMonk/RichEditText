package com.gworks.richedittext.converters

import android.text.Editable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import com.gworks.richedittext.markups.*
import org.xml.sax.Attributes
import org.xml.sax.ContentHandler
import org.xml.sax.InputSource
import org.xml.sax.XMLReader
import org.xml.sax.helpers.XMLReaderFactory
import java.io.StringReader

fun updateSpanFlags(text: Spannable, span: Any?, flags: Int) {
    text.setSpan(span, text.getSpanStart(span), text.getSpanEnd(span), flags)
}

typealias MarkupFactory = (String) -> Class<out Markup>?

val defaultMarkupFactory: MarkupFactory = { tag ->

    when (tag) {
        HtmlConverter.BOLD -> Bold::class.java
        HtmlConverter.ITALIC -> Italic::class.java
        HtmlConverter.UNDERLINE -> Underline::class.java
        HtmlConverter.LINK -> Link::class.java
        HtmlConverter.SPAN -> Font::class.java
        else -> null
    }

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

fun toHtml(text: Spanned, unknownMarkupHandler: MarkupConverter.UnknownMarkupHandler? = null): String {

    val html = StringBuilder(text.length)
    val htmlConverter = HtmlConverter(unknownMarkupHandler)
    val spans = text.getSpans(0, text.length, Markup::class.java).toMutableList()
    var processed = -1

    while (processed < text.length) {

        // Get the next span transition.
        val transitionIndex = text.nextSpanTransition(processed, text.length, Markup::class.java)

        // If there are unprocessed text before transition add.
        if (transitionIndex > processed)
            html.append(text, maxOf(processed, 0), transitionIndex)

        val oldLen = html.length
        val iterator = spans.iterator()
        for (span in iterator) {

            val start = text.getSpanStart(span)
            if (start == transitionIndex)
                span.convert(html, html.length, htmlConverter, true)

            if (text.getSpanEnd(span) == transitionIndex) {
                span.convert(html, if (start == transitionIndex) html.length else oldLen, htmlConverter, false)
                iterator.remove()
            }
        }

        // The text and spans up to transitionIndex is processed.
        processed = transitionIndex
    }

    return html.toString()
}

fun fromHtml(html: String,
             markupFactory: MarkupFactory = defaultMarkupFactory,
             unknownTagHandler: UnknownTagHandler? = null,
             attributeConverter: AttributeConverter<Attributes> = HtmlAttributeConverter(),
             xmlReader: XMLReader = XMLReaderFactory.createXMLReader("org.ccil.cowan.tagsoup.Parser"),
             contentHandler: ContentHandler? = null): Spanned {
    val sb = SpannableStringBuilder()
    xmlReader.contentHandler = contentHandler ?: DefaultHtmlHandler(sb, markupFactory, unknownTagHandler, attributeConverter)
    xmlReader.parse(InputSource(StringReader(html)))
    return sb
}

interface UnknownTagHandler {

    fun handleStartTag(out: Editable, qName: String, attributes: Attributes?)

    fun handleEndTag(out: Editable, qName: String)

}
