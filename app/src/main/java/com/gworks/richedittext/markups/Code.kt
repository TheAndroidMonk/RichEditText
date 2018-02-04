package com.gworks.richedittext.markups

import android.graphics.Typeface
import android.text.Spannable
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import com.gworks.richedittext.converters.AttributeConverter
import com.gworks.richedittext.converters.MarkupConverter
import com.gworks.richedittext.updateSpanFlags

class Code(attributes: Code.Attributes) : AttributedMarkup<Code.Attributes>(attributes)  {

    override val isSplittable: Boolean
        get() = true

    private val typefaceSpan = CustomTypefaceSpan(Typeface.MONOSPACE)
    private val colorSpan = if (attributes.color != null) ForegroundColorSpan(attributes.color) else null
    private val backgroundSpan = if (attributes.backgroundColor != null) BackgroundColorSpan(attributes.backgroundColor) else null

    constructor(converter: AttributeConverter<Any>, attr: Any) : this(converter.convertCodeAttribute(attr)!!)

    override fun convert(sb: StringBuilder, offset: Int, converter: MarkupConverter, begin: Boolean) {
        converter.convertMarkup(sb, offset, this, begin)
    }

    override fun apply(text: Spannable, from: Int, to: Int, flags: Int) {
        text.setSpan(typefaceSpan, from, to, flags)
        if (colorSpan != null) text.setSpan(colorSpan, from, to, flags)
        if (backgroundSpan != null) text.setSpan(backgroundSpan, from, to, flags)
    }

    override fun remove(text: Spannable) {
        text.removeSpan(typefaceSpan)
        if (colorSpan != null) text.removeSpan(colorSpan)
        if (backgroundSpan != null) text.removeSpan(backgroundSpan)
    }

    override fun updateSpanFlags(text: Spannable, flags: Int) {
        updateSpanFlags(text, typefaceSpan, flags)
        if (colorSpan != null) updateSpanFlags(text, colorSpan, flags)
        if (backgroundSpan != null) updateSpanFlags(text, backgroundSpan, flags)
    }

    class Attributes(val color: Int? = null, val backgroundColor: Int? = null)
}