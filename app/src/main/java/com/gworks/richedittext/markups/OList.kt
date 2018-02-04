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

import com.gworks.richedittext.converters.AttributeConverter
import com.gworks.richedittext.converters.MarkupConverter

class OList(attributes: Attributes) : List<OList.Attributes>(attributes) {

    constructor(converter: AttributeConverter<Any>, attr: Any) : this(converter.convertOListAttribute(attr)!!)

    override fun convert(sb: StringBuilder, offset: Int, converter: MarkupConverter, begin: Boolean) {
        converter.convertMarkup(sb, offset, this, begin)
    }

    override fun createListItem(index: Int): ListItem {
        return ListItem(attributes)
    }

    override fun setIndex(listItem: ListItem, index: Int) {
        listItem.bulletText = (index + attributes.startWith).toString()
    }

    class Attributes(margin: Int, color: Int?, val startWith :Int = 1, separator: CharSequence? = ".") : ListItem.Attributes(margin, color, separator)
}

