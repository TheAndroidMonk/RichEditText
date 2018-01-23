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
import android.text.style.ForegroundColorSpan
import android.text.style.TypefaceSpan
import com.gworks.richedittext.converters.AttributeConverter
import com.gworks.richedittext.converters.MarkupConverter
import com.gworks.richedittext.converters.updateSpanFlags

class Font(attributes: Attributes) : AttributedMarkup<Font.Attributes>(attributes) {

    private val typefaceSpan: TypefaceSpan
    private val sizeSpan: AbsoluteSizeSpan
    private val colorSpan: ForegroundColorSpan

    override val isSplittable: Boolean
        get() = true

    constructor(converter: AttributeConverter<Any>, attr: Any) : this(converter.convertFontAttribute(attr)!!)

    init {
        typefaceSpan = TypefaceSpan(attributes.typeface)
        sizeSpan = AbsoluteSizeSpan(attributes.size, true)
        colorSpan = ForegroundColorSpan(attributes.color)
    }

    override fun convert(sb: StringBuilder, offset: Int, converter: MarkupConverter, begin: Boolean) {
        converter.convertMarkup(sb, offset, this, begin)
    }

    override fun apply(text: Spannable, from: Int, to: Int, flags: Int) {
        text.setSpan(typefaceSpan, from, to, flags)
        text.setSpan(sizeSpan, from, to, flags)
        text.setSpan(colorSpan, from, to, flags)
    }

    override fun remove(text: Spannable) {
        text.removeSpan(typefaceSpan)
        text.removeSpan(sizeSpan)
        text.removeSpan(colorSpan)
    }

    override fun updateSpanFlags(text: Spannable, flags: Int) {
        updateSpanFlags(text, typefaceSpan, flags)
        updateSpanFlags(text, sizeSpan, flags)
        updateSpanFlags(text, colorSpan, flags)
    }

    class Attributes(val typeface: String, val size: Int, val color: Int)
}
