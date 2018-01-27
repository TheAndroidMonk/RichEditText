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

import com.gworks.richedittext.markups.*

interface MarkupConverter {

    val unknownMarkupHandler: UnknownMarkupHandler?

    fun convertMarkup(out: StringBuilder, offset: Int, bold: Bold, begin: Boolean) {}

    fun convertMarkup(out: StringBuilder, offset: Int, italic: Italic, begin: Boolean) {}

    fun convertMarkup(out: StringBuilder, offset: Int, underline: Underline, begin: Boolean) {}

    fun convertMarkup(out: StringBuilder, offset: Int, strikethrough: Strikethrough, begin: Boolean) {}

    fun convertMarkup(out: StringBuilder, offset: Int, font: Font, begin: Boolean) {}

    fun convertMarkup(out: StringBuilder, offset: Int, link: Link, begin: Boolean) {}

    fun convertMarkup(out: StringBuilder, offset: Int, subscriptMarkup: Subscript, begin: Boolean) {}

    fun convertMarkup(out: StringBuilder, offset: Int, superscript: Superscript, begin: Boolean) {}

    fun convertMarkup(out: StringBuilder, offset: Int, uList: UList, begin: Boolean) {}

    fun convertMarkup(out: StringBuilder, offset: Int, oList: OList, begin: Boolean) {}

    fun convertMarkup(out: StringBuilder, offset: Int, listItem: ListItem, begin: Boolean) {}

    fun convertMarkup(out: StringBuilder, offset: Int, paragraph: Paragraph, begin: Boolean) {}

    fun convertMarkup(out: StringBuilder, offset: Int, markup: Markup, begin: Boolean) {
        unknownMarkupHandler?.handleMarkup(out, offset, markup, begin)
    }

    interface UnknownMarkupHandler {
        fun handleMarkup(out: StringBuilder, offset: Int, markup: Markup, begin: Boolean)
    }
}
