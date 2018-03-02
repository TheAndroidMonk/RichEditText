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
import com.gworks.richedittext.converters.AttributeConverter
import com.gworks.richedittext.converters.MarkupConverter

open class ListItem(attributes: Attributes) : SingleSpanAttributedMarkup<ListItem.Attributes>(
        ListItemSpan(attributes.gapWidth, attributes.color, attributes.separator), attributes) {

    override val isSplittable: Boolean
        get() = false

    var bulletText: CharSequence?
        set(value) {
            (this.span as ListItemSpan).bulletText = value
        }
        get() {
            return (this.span as ListItemSpan).bulletText
        }

    constructor(converter: AttributeConverter<Any>, attr: Any) : this(converter.convertListItemAttribute(attr)!!)

//    internal fun reApply(text: Spannable, from: Int, to: Int) {
//        remove(text)
//        apply(text, from, to, 0)
//    }

    override fun convert(sb: StringBuilder, offset: Int, converter: MarkupConverter, begin: Boolean) {
        converter.convertMarkup(sb, offset, this, begin)
    }

    open class Attributes(val gapWidth: Int, val color: Int? = null, val separator: CharSequence? = ".")

}
