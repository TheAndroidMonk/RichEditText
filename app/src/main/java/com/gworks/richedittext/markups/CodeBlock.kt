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

import android.graphics.Typeface
import android.text.Spannable
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import com.gworks.richedittext.converters.AttributeConverter
import com.gworks.richedittext.converters.MarkupConverter
import com.gworks.richedittext.updateSpanFlags

class CodeBlock(attributes: Attributes) : List<CodeBlock.Attributes>(attributes) {

    private val typefaceSpan = CustomTypefaceSpan(Typeface.MONOSPACE)
    private val colorSpan = if (attributes.textColor != null) ForegroundColorSpan(attributes.textColor) else null
    private val backgroundSpan = if (attributes.backgroundColor != null) BackgroundColorSpan(attributes.backgroundColor) else null

    constructor(converter: AttributeConverter<Any>, attr: Any) : this(converter.convertCodeBlockAttribute(attr)!!)

    override fun convert(sb: StringBuilder, offset: Int, converter: MarkupConverter, begin: Boolean) {
        converter.convertMarkup(sb, offset, this, begin)
    }

    override fun createListItem(index: Int): ListItem {
        // Override the ListItem not to apply itself to the text.
        return object : ListItem(attributes) {

            override fun applyInternal(text: Spannable, from: Int, to: Int, flags: Int) {
                apply(text, from, to, flags)
            }

            override fun removeInternal(text: Spannable) {
                remove(text)
            }

            override fun updateSpanFlagsInternal(text: Spannable, flags: Int) {
                updateSpanFlags(text, flags)
            }
        }
    }

    override fun setIndex(listItem: ListItem, index: Int) {
        listItem.bulletText = if (attributes.needLineNos) (index + attributes.startWith).toString() else ""
    }

    override fun apply(text: Spannable, from: Int, to: Int, flags: Int) {
        super.apply(text, from, to, flags)
        text.setSpan(typefaceSpan, from, to, flags)
        if (colorSpan != null) text.setSpan(colorSpan, from, to, flags)
        if (backgroundSpan != null) text.setSpan(backgroundSpan, from, to, flags)
    }

    override fun remove(text: Spannable) {
        super.remove(text)
        text.removeSpan(typefaceSpan)
        if (colorSpan != null) text.removeSpan(colorSpan)
        if (backgroundSpan != null) text.removeSpan(backgroundSpan)
    }

    override fun updateSpanFlags(text: Spannable, flags: Int) {
        super.updateSpanFlags(text, flags)
        updateSpanFlags(text, typefaceSpan, flags)
        if (colorSpan != null) updateSpanFlags(text, colorSpan, flags)
        if (backgroundSpan != null) updateSpanFlags(text, backgroundSpan, flags)
    }

    class Attributes(margin: Int, val needLineNos: Boolean = true, val startWith: Int = 1, val textColor: Int? = null, val backgroundColor: Int? =null) :
            ListItem.Attributes(margin, color = null, separator = null)
}

