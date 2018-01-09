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

class HtmlConverter(_unknownMarkupHandler: MarkupConverter.UnknownMarkupHandler?) : MarkupConverter {

    override val unknownMarkupHandler: MarkupConverter.UnknownMarkupHandler?

    init {
        unknownMarkupHandler = _unknownMarkupHandler;
    }

    override fun convertMarkup(sb: StringBuilder, boldMarkup: Bold, begin: Boolean): Boolean {
        sb.append(makeTag(BOLD, begin))
        return true
    }

    override fun convertMarkup(sb: StringBuilder, italicMarkup: Italic, begin: Boolean): Boolean {
        sb.append(makeTag(ITALIC, begin))
        return true
    }

    override fun convertMarkup(sb: StringBuilder, underlineMarkup: Underline, begin: Boolean): Boolean {
        sb.append(makeTag(UNDERLINE, begin))
        return true
    }

    override fun convertMarkup(sb: StringBuilder, linkMarkup: Link, begin: Boolean): Boolean {
        sb.append(if (begin) LT else _LT)
        sb.append(LINK)
        if (begin)
            sb.append(" " + ATTR_URL + "=" + linkMarkup.attributes)
        sb.append(GT)
        return true
    }

    companion object {

        val BOLD = "b"
        val ITALIC = "i"
        val UNDERLINE = "u"
        val LINK = "a"
        val H1 = "h1"
        val H2 = "h2"
        val H3 = "h3"
        val H4 = "h4"

        val ATTR_URL = "href"
        val ATTR_SRC = "src"

        val LT = "<"
        val _LT = "</"
        val GT = ">"
        val _GT = "/>"

        private fun makeTag(name: String, begin: Boolean): String {
            return (if (begin) LT else _LT) + name + GT
        }
    }
}
