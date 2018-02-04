package com.gworks.richedittext.converters

import android.text.Layout
import com.gworks.richedittext.markups.Font
import com.gworks.richedittext.converters.AttributeConverter.UnknownAttributeConverter
import com.gworks.richedittext.markups.Paragraph

import org.xml.sax.Attributes

class HtmlAttributeConverter(override val unknownAttributeConverter: UnknownAttributeConverter<Attributes>? = null) : AttributeConverter<Attributes> {

    override fun convertLinkAttribute(attr: Attributes): String? {
        return attr.getValue(HtmlConverter.ATTR_HREF)
    }

    override fun convertFontAttribute(attr: Attributes): Font.Attributes? {
        return Font.Attributes(attr.getValue(HtmlConverter.ATTR_FACE),
                Integer.parseInt(attr.getValue(HtmlConverter.ATTR_SIZE)),
                Integer.parseInt(attr.getValue(HtmlConverter.ATTR_COLOR)))
    }

    override fun convertParagraphAttribute(attr: Attributes): Paragraph.Attributes? {
        return Paragraph.Attributes(align = when (attr.getValue(HtmlConverter.ATTR_ALIGN)) {
            HtmlConverter.VAL_LEFT -> Layout.Alignment.ALIGN_NORMAL
            HtmlConverter.VAL_RIGHT -> Layout.Alignment.ALIGN_OPPOSITE
            HtmlConverter.VAL_CENTER -> Layout.Alignment.ALIGN_CENTER
            else -> Layout.Alignment.ALIGN_NORMAL
        })
    }
}