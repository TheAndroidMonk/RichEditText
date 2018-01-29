package com.gworks.richedittext.markups

import android.text.Layout
import com.gworks.richedittext.converters.AttributeConverter
import com.gworks.richedittext.converters.MarkupConverter

class Paragraph(attributes: Attributes) : SingleSpanAttributedMarkup<Paragraph.Attributes> (
                ParagraphSpan(attributes.topSpacing, attributes.bottomSpacing, attributes.align), attributes) {

    override val isSplittable: Boolean
        get() = false

    constructor(converter: AttributeConverter<Any>, attr: Any) : this(converter.convertParagraphAttribute(attr)!!)

    override fun convert(sb: StringBuilder, offset: Int, converter: MarkupConverter, begin: Boolean) {
        converter.convertMarkup(sb, offset, this, begin)
    }

    open class Attributes(val topSpacing: Int? = null, val bottomSpacing: Int? = null, val align: Layout.Alignment? = null)

}
