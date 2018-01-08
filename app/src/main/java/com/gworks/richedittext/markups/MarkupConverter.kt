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

interface MarkupConverter {

    val unknownMarkupHandler: UnknownMarkupHandler?

    fun convertMarkup(sb: StringBuilder, boldMarkup: Bold, begin: Boolean): Boolean {
        return false
    }

    fun convertMarkup(sb: StringBuilder, italicMarkup: Italic, begin: Boolean): Boolean {
        return false
    }

    fun convertMarkup(sb: StringBuilder, underlineMarkup: Underline, begin: Boolean): Boolean {
        return false
    }

    fun convertMarkup(sb: StringBuilder, fontMarkup: Font, begin: Boolean): Boolean {
        return false
    }

    fun convertMarkup(sb: StringBuilder, linkMarkup: Link, begin: Boolean): Boolean {
        return false
    }

    fun convertMarkup(sb: StringBuilder, markup: Markup, begin: Boolean): Boolean {
        return unknownMarkupHandler?.handleMarkup(sb, markup, begin) ?: false
    }

    interface UnknownMarkupHandler {
        fun handleMarkup(sb: StringBuilder, markup: Markup, begin: Boolean): Boolean
    }
}
