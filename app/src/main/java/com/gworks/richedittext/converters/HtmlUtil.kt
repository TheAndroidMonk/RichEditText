package com.gworks.richedittext.converters

import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.Spanned
import com.gworks.richedittext.markups.*
import org.xml.sax.Attributes
import org.xml.sax.ContentHandler
import org.xml.sax.InputSource
import org.xml.sax.XMLReader
import org.xml.sax.helpers.XMLReaderFactory
import java.io.StringReader

val defaultMarkupFactory = { tag: String ->

    when (tag) {
        HtmlConverter.BOLD -> Bold::class.java
        HtmlConverter.ITALIC -> Italic::class.java
        HtmlConverter.UNDERLINE -> Underline::class.java
        HtmlConverter.LINK -> Link::class.java
        HtmlConverter.FONT -> Font::class.java
        HtmlConverter.STRIKE -> Strikethrough::class.java
        HtmlConverter.SUB -> Subscript::class.java
        HtmlConverter.SUP -> Superscript::class.java
        HtmlConverter.OL -> OList::class.java
        HtmlConverter.UL -> UList::class.java
        HtmlConverter.P -> Paragraph::class.java
        HtmlConverter.CODE -> Code::class.java
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
             markupFactory: (String) -> Class<out Markup>? = defaultMarkupFactory,
             unknownTagHandler: UnknownTagHandler? = null,
             attributeConverter: AttributeConverter<Attributes> = HtmlAttributeConverter(),
             xmlReader: XMLReader = XMLReaderFactory.createXMLReader("org.ccil.cowan.tagsoup.Parser"),
             contentHandler: ContentHandler? = null,
             enableContinuousEditing: Boolean = true): Spanned {
    val sb = SpannableStringBuilder()
    xmlReader.contentHandler = contentHandler ?:
            DefaultHtmlHandler(sb, markupFactory, unknownTagHandler, attributeConverter)
    xmlReader.parse(InputSource(StringReader(html)))
    if (enableContinuousEditing) {
        val spans = sb.getSpans(0, sb.length, Markup::class.java)
        spans.forEach { it.updateSpanFlagsInternal(sb, Spanned.SPAN_EXCLUSIVE_INCLUSIVE) }
    }
    return sb
}

fun Attributes.forEach(consumer: (key: String, value: String) -> Unit, filter: (key: String) -> Boolean = {true}) {
    for (it in 0 until length) {
        val name = getQName(it)
        if (filter(name)) consumer(name, this.getValue(it))
    }
}

interface UnknownTagHandler {

    fun handleStartTag(out: Editable, qName: String, attributes: Attributes?)

    fun handleEndTag(out: Editable, qName: String)

}
