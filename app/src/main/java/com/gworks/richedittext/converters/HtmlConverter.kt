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

import android.text.Layout
import com.gworks.richedittext.markups.*
import org.xml.sax.Attributes

class HtmlConverter(override val unknownMarkupHandler: MarkupConverter.UnknownMarkupHandler?) : MarkupConverter {

    override fun convertMarkup(out: StringBuilder, offset: Int, subscript: Subscript, begin: Boolean) {
        out.insert(offset, makeTag(SUB, begin, attributes = subscript.extra as? Attributes))
    }

    override fun convertMarkup(out: StringBuilder, offset: Int, superscript: Superscript, begin: Boolean) {
        out.insert(offset, makeTag(SUP, begin, attributes = superscript.extra as? Attributes))
    }

    override fun convertMarkup(out: StringBuilder, offset: Int, bold: Bold, begin: Boolean) {
        out.insert(offset, makeTag(BOLD, begin, attributes = bold.extra as? Attributes))
    }

    override fun convertMarkup(out: StringBuilder, offset: Int, italic: Italic, begin: Boolean) {
        out.insert(offset, makeTag(ITALIC, begin, attributes = italic.extra as? Attributes))
    }

    override fun convertMarkup(out: StringBuilder, offset: Int, underline: Underline, begin: Boolean) {
        out.insert(offset, makeTag(UNDERLINE, begin, attributes = underline.extra as? Attributes))
    }

    override fun convertMarkup(out: StringBuilder, offset: Int, strikethrough: Strikethrough, begin: Boolean) {
        out.insert(offset, makeTag(STRIKE, begin, attributes = strikethrough.extra as? Attributes))
    }

    override fun convertMarkup(out: StringBuilder, offset: Int, uList: UList, begin: Boolean) {
        out.insert(offset, makeTag(UL, begin, attributes = uList.extra as? Attributes))
    }

    override fun convertMarkup(out: StringBuilder, offset: Int, oList: OList, begin: Boolean) {
        out.insert(offset, makeTag(OL, begin, attributes = oList.extra as? Attributes))
    }

    override fun convertMarkup(out: StringBuilder, offset: Int, listItem: ListItem, begin: Boolean) {
        out.insert(offset, makeTag(LI, begin, attributes = listItem.extra as? Attributes))
    }

    override fun convertMarkup(out: StringBuilder, offset: Int, link: Link, begin: Boolean) {
        out.insert(offset, makeTag(LINK, begin, ATTR_HREF + "=" + link.attributes, link.extra as? Attributes, ATTR_HREF))
    }

    override fun convertMarkup(out: StringBuilder, offset: Int, paragraph: Paragraph, begin: Boolean) {
        out.insert(offset, makeTag(P, begin,
                when (paragraph.attributes.align) {
                    Layout.Alignment.ALIGN_NORMAL -> ATTR_ALIGN + "=" + VAL_LEFT
                    Layout.Alignment.ALIGN_OPPOSITE -> ATTR_ALIGN + "=" + VAL_RIGHT
                    Layout.Alignment.ALIGN_CENTER -> ATTR_ALIGN + "=" + VAL_CENTER
                    null -> ""
                }, paragraph.extra as? Attributes, ATTR_ALIGN))
    }

    override fun convertMarkup(out: StringBuilder, offset: Int, font: Font, begin: Boolean) {
        // TODO add impl
        super.convertMarkup(out, offset, font, begin)
    }

    companion object {

        fun makeTag(name: String, begin: Boolean, attrs: String = "", attributes: Attributes? = null, vararg supported: String): CharSequence {
            sb.setLength(0) // We are reusing the instance; reset it first.
            sb.append(if (begin) LT else _LT)
            sb.append(name)
            sb.append(if (begin && attrs.isNotEmpty()) (" " + attrs) else "")
            makeAttributes(sb, attributes, *supported)
            sb.append(GT)
            return sb
        }

        private fun makeAttributes(out: Appendable, attributes: Attributes?, vararg supported: String) {
            attributes?.forEach({ name, value -> out.append(name + "=" + value) }, { it !in supported })
        }

        private val sb = StringBuilder()

        const val H1 = "h1"
        const val H2 = "h2"
        const val H3 = "h3"
        const val H4 = "h4"
        const val BOLD = "b"
        const val ITALIC = "i"
        const val UNDERLINE = "u"
        const val LINK = "a"
        const val SPAN = "span"
        const val LI = "li"
        const val OL = "ol"
        const val UL = "ul"
        const val SUB = "sub"
        const val SUP = "sup"
        const val STRIKE = "strike"
        const val P = "p"

        const val ATTR_SRC = "src"
        const val ATTR_HREF = "href"
        const val ATTR_SIZE = "size"
        const val ATTR_FONT = "font"
        const val ATTR_COLOR = "color"
        const val ATTR_ALIGN = "align"

        const val VAL_LEFT = "left"
        const val VAL_RIGHT = "right"
        const val VAL_CENTER = "center"

        private const val LT = "<"
        private const val _LT = "</"
        private const val GT = ">"
        private const val _GT = "/>"
    }
}