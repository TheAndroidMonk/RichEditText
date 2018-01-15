package com.gworks.richedittext.converters

import com.gworks.richedittext.markups.Font
import com.gworks.richedittext.converters.AttributeConverter.UnknownAttributeConverter

import org.xml.sax.Attributes

/**
 * Created by durgadass on 13/1/18.
 */
class HtmlAttributeConverter(override val unknownAttributeConverter: UnknownAttributeConverter<Attributes>? = null) : AttributeConverter<Attributes> {

    override fun convertLinkAttribute(attr: Attributes): String? {
        return attr.getValue(HtmlConverter.ATTR_URL)
    }

    override fun convertFontAttribute(attr: Attributes): Font.Attributes? {
        return Font.Attributes(attr.getValue(HtmlConverter.ATTR_FONT),
                Integer.parseInt(attr.getValue(HtmlConverter.ATTR_SIZE)),
                Integer.parseInt(attr.getValue(HtmlConverter.ATTR_COLOR)))
    }

}