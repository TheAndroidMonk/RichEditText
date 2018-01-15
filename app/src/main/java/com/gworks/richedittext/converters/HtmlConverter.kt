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

import com.gworks.richedittext.markups.Bold
import com.gworks.richedittext.markups.Italic
import com.gworks.richedittext.markups.Link
import com.gworks.richedittext.markups.Underline

class HtmlConverter(override val unknownMarkupHandler: MarkupConverter.UnknownMarkupHandler?) : MarkupConverter {

    override fun convertMarkup(sb: StringBuilder, offset: Int, boldMarkup: Bold, begin: Boolean): Boolean {
        sb.insert(offset, makeTag(name = BOLD, begin = begin))
        return true
    }

    override fun convertMarkup(sb: StringBuilder, offset: Int, italicMarkup: Italic, begin: Boolean): Boolean {
        sb.insert(offset, makeTag(name = ITALIC, begin = begin))
        return true
    }

    override fun convertMarkup(sb: StringBuilder, offset: Int, underlineMarkup: Underline, begin: Boolean): Boolean {
        sb.insert(offset, makeTag(name = UNDERLINE, begin = begin))
        return true
    }

    override fun convertMarkup(sb: StringBuilder, offset: Int, linkMarkup: Link, begin: Boolean): Boolean {
        sb.insert(offset, makeTag(name = LINK, begin = begin, attrs = ATTR_URL + "=" + linkMarkup.attributes))
        return true
    }

    companion object {

        val BOLD = "b"
        val ITALIC = "i"
        val UNDERLINE = "u"
        val LINK = "a"
        val SPAN = "span"
        val H1 = "h1"
        val H2 = "h2"
        val H3 = "h3"
        val H4 = "h4"

        val ATTR_URL = "href"
        val ATTR_SRC = "src"
        val ATTR_SIZE = "size"
        val ATTR_FONT = "font"
        val ATTR_COLOR = "color"

        private val LT = "<"
        private val _LT = "</"
        private val GT = ">"
        private val _GT = "/>"

        private fun makeTag(name: String, begin: Boolean, attrs: String = ""): CharSequence {
            sb.setLength(0) // We are reusing the instance; reset it first.
            sb.append(if (begin) LT else _LT)
            sb.append(name)
            sb.append(if (begin && attrs.isNotEmpty()) (" " + attrs) else "")
            sb.append(GT)
            return sb
        }

        private val sb = StringBuilder()
    }
}