package com.gworks.richedittext.converters

import android.text.Editable
import android.text.Spanned
import com.gworks.richedittext.createMarkup
import com.gworks.richedittext.isAttributed
import com.gworks.richedittext.markups.Markup
import org.xml.sax.Attributes
import org.xml.sax.helpers.DefaultHandler

open class DefaultHtmlHandler(private val editable: Editable,
                              private val markupFactory: MarkupFactory,
                              private val unknownTagHandler: UnknownTagHandler?,
                              private val attributeConverter: AttributeConverter<Attributes>) : DefaultHandler(){

    override fun startElement(uri: String, localName: String, qName: String, attributes: Attributes) {
        val markupType = markupFactory.invoke(qName)
        if (markupType != null)
            editable.setSpan(if (!isAttributed(markupType)) markupType.newInstance()
            else createMarkup(markupType, attributeConverter, attributes), editable.length, editable.length, Spanned.SPAN_MARK_MARK)
        else
            unknownTagHandler?.handleStartTag(editable, qName, attributes)
    }

    override fun endElement(uri: String?, localName: String?, qName: String?) {
        val markupType = markupFactory.invoke(qName!!)
        if (markupType != null) {
            val spans = editable.getSpans(0, editable.length, markupType)
            if (spans.isNotEmpty()) {
                val span = spans[spans.lastIndex] // end is considered as the end of last applied span.
                if (editable.getSpanFlags(span) == Spanned.SPAN_MARK_MARK)
                    span.applyInternal(editable, editable.getSpanStart(span), editable.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        } else
            unknownTagHandler?.handleEndTag(editable, qName)
    }

    override fun characters(ch: CharArray?, start: Int, length: Int) {
        if (ch != null && length > 0)
            for (i in start until start + length)
                editable.append(ch[i])
    }

}
