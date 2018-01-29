/*
 * Copyright 2018 TheAndroidMonk
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.gworks.richedittext.markups

import android.text.Spannable
import android.text.style.AbsoluteSizeSpan
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.TypefaceSpan
import com.gworks.richedittext.converters.AttributeConverter
import com.gworks.richedittext.converters.MarkupConverter
import com.gworks.richedittext.updateSpanFlags

class Font(attributes: Attributes) : AttributedMarkup<Font.Attributes>(attributes) {

    private val typefaceSpan: TypefaceSpan?
    private val sizeSpan: AbsoluteSizeSpan?
    private val colorSpan: ForegroundColorSpan?
    private val backgroundSpan: BackgroundColorSpan?

    override val isSplittable: Boolean
        get() = true

    constructor(converter: AttributeConverter<Any>, attr: Any) : this(converter.convertFontAttribute(attr)!!)

    init {
        typefaceSpan = if (attributes.typeface != null) TypefaceSpan(attributes.typeface) else null
        sizeSpan = if (attributes.size != null) AbsoluteSizeSpan(attributes.size, true) else null
        colorSpan = if (attributes.color != null) ForegroundColorSpan(attributes.color) else null
        backgroundSpan = if (attributes.backgroundColor != null) BackgroundColorSpan(attributes.backgroundColor) else null
    }

    override fun convert(sb: StringBuilder, offset: Int, converter: MarkupConverter, begin: Boolean) {
        converter.convertMarkup(sb, offset, this, begin)
    }

    override fun apply(text: Spannable, from: Int, to: Int, flags: Int) {
        if (typefaceSpan != null) text.setSpan(typefaceSpan, from, to, flags)
        if (sizeSpan != null) text.setSpan(sizeSpan, from, to, flags)
        if (colorSpan != null) text.setSpan(colorSpan, from, to, flags)
        if (backgroundSpan != null) text.setSpan(backgroundSpan, from, to, flags)
    }

    override fun remove(text: Spannable) {
        if (typefaceSpan != null) text.removeSpan(typefaceSpan)
        if (sizeSpan != null) text.removeSpan(sizeSpan)
        if (colorSpan != null) text.removeSpan(colorSpan)
        if (backgroundSpan != null) text.removeSpan(backgroundSpan)
    }

    override fun updateSpanFlags(text: Spannable, flags: Int) {
        if (typefaceSpan != null) updateSpanFlags(text, typefaceSpan, flags)
        if (sizeSpan != null) updateSpanFlags(text, sizeSpan, flags)
        if (colorSpan != null) updateSpanFlags(text, colorSpan, flags)
        if (backgroundSpan != null) updateSpanFlags(text, backgroundSpan, flags)
    }

    class Attributes(val typeface: String? = null,
                     val size: Int? = null,
                     val color: Int? = null,
                     val backgroundColor: Int? = null)
}
