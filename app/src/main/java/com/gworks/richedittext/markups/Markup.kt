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
import android.text.Spanned
import com.gworks.richedittext.converters.MarkupConverter
import com.gworks.richedittext.updateSpanFlags

abstract class Markup {

    /**
     * Tells whether this markup type is splittable.
     */
    abstract val isSplittable: Boolean

    internal var extra: Any? = null

    internal open fun applyInternal(text: Spannable, from: Int, to: Int, flags: Int) {
        text.setSpan(this, from, to, flags)
        apply(text, from, to, flags)
    }

    internal open fun removeInternal(text: Spannable) {
        text.removeSpan(this)
        remove(text)
    }

    internal open fun updateSpanFlagsInternal(text: Spannable, flags: Int) {
        updateSpanFlags(text, this, flags)
        updateSpanFlags(text, flags)
    }

    /**
     * Returns the starting index of this markup in the given text. Returns -1 if not applied.
     */
    fun getSpanStart(text: Spanned): Int {
        return text.getSpanStart(this)
    }

    /**
     * Returns the ending index of this markup in the given text. Returns -1 if not applied.
     */
    fun getSpanEnd(text: Spanned): Int {
        return text.getSpanEnd(this)
    }

    /**
     *
     * @param sb
     * @param converter
     * @param begin
     */
    abstract fun convert(sb: StringBuilder, offset: Int = sb.length, converter: MarkupConverter, begin: Boolean)

    /**
     * Tells whether this markup can exist with the given markup type.
     */
    abstract fun canExistWith(anotherType: Class<out Markup>): Boolean

    /**
     * Applies this markup to the given text in given range [from, to).
     * @param text
     * @param from from inclusive
     * @param to to exclusive
     * @param flags
     */
    protected abstract fun apply(text: Spannable, from: Int, to: Int, flags: Int)

    /**
     * Removes this markup from the given text.
     */
    protected abstract fun remove(text: Spannable)

    protected abstract fun updateSpanFlags(text: Spannable, flags: Int)
}

