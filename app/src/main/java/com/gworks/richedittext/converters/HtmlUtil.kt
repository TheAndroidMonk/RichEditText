package com.gworks.richedittext.converters

import android.text.Editable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import com.gworks.richedittext.markups.*
import org.xml.sax.Attributes
import org.xml.sax.InputSource
import org.xml.sax.helpers.DefaultHandler
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
            html.append(text, kotlin.math.max(processed, 0), transitionIndex)

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

fun fromHtml(html: String, markupFactory: MarkupFactory = defaultMarkupFactory,
             unknownTagHandler: UnknownTagHandler? = null,
             attributeConverter: AttributeConverter<Attributes> = HtmlAttributeConverter()): Spanned {

    val sb = SpannableStringBuilder()

    val xmlReader = XMLReaderFactory.createXMLReader("org.ccil.cowan.tagsoup.Parser")
    xmlReader.contentHandler = object : DefaultHandler() {

        override fun startElement(uri: String, localName: String, qName: String, attributes: Attributes) {
            val markupType = markupFactory.invoke(qName)
            if (markupType != null)
                sb.setSpan(createMarkup(markupType, attributeConverter, attributes), sb.length, sb.length, Spanned.SPAN_MARK_MARK)
            else
                unknownTagHandler?.handleStartTag(sb, qName, attributes)
        }

        override fun endElement(uri: String?, localName: String?, qName: String?) {
            val markupType = markupFactory.invoke(qName!!)
            if (markupType != null) {
                val spans = sb.getSpans(0, sb.length, markupType)
                if (spans.isNotEmpty()) {
                    val span = spans[spans.lastIndex] // end is considered as the end of last applied span.
                    if (sb.getSpanFlags(span) == Spanned.SPAN_MARK_MARK)
                        span.applyInternal(sb, sb.getSpanStart(span), sb.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            } else
                unknownTagHandler?.handleEndTag(sb, qName)
        }

        override fun characters(ch: CharArray?, start: Int, length: Int) {
            if (ch != null && length > 0)
                for (i in start until start + length)
                    sb.append(ch[i])
        }

        override fun endDocument() {
            val spans = sb.getSpans(0, sb.length, Markup::class.java)
            spans.forEach { it.updateSpanFlags(sb, Spanned.SPAN_EXCLUSIVE_INCLUSIVE) }
        }
    }
    xmlReader.parse(InputSource(StringReader(html)))
    return sb
}


internal fun createMarkup(markupType: Class<out Markup>, value: Any?): Markup {
    // TODO Add reflection code to create an instance for attributed markups.
    return markupType.newInstance()
}

internal fun <T> createMarkup(markupType: Class<out Markup>, attributeConverter: AttributeConverter<T>? = null, attr: T? = null): Markup {
    // TODO Add reflection code to create an instance for attributed markups.
    return markupType.newInstance()
}

interface UnknownTagHandler {

    fun handleStartTag(out: Editable, qName: String, attributes: Attributes?)

    fun handleEndTag(out: Editable, qName: String)

}
