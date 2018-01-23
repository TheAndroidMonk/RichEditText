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

package com.gworks.richedittext.converters

import com.gworks.richedittext.markups.Font
import com.gworks.richedittext.markups.ListItem
import com.gworks.richedittext.markups.OList
import com.gworks.richedittext.markups.UList

interface AttributeConverter<T> {

    val unknownAttributeConverter: UnknownAttributeConverter<T>?

    fun convertLinkAttribute(attr: T): String? {
        return null
    }

    fun convertFontAttribute(attr: T): Font.Attributes? {
        return null
    }

    fun convertOListAttribute(attr: T): OList.Attributes? {
        return null
    }

    fun convertUListAttribute(attr: T): UList.Attributes? {
        return null
    }

    fun convertListItemAttribute(attr: T): ListItem.Attributes? {
        return null
    }

    fun convertAttribute(attr: T): Any? {
        return unknownAttributeConverter?.convertAttribute(attr)
    }

    interface UnknownAttributeConverter<T> {
        fun convertAttribute(attr: T): Any?
    }
}
