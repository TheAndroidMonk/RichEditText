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
import com.gworks.richedittext.indexOf
import java.util.*

abstract class List<ATTR>(attributes: ATTR) : AttributedMarkup<ATTR>(attributes) {

    override val isSplittable: Boolean
        get() = true

    private val listItems = ArrayList<ListItem>()

    override fun canExistWith(anotherType: Class<out Markup>): Boolean {
        return anotherType != OList::class.java
                && anotherType != UList::class.java
                && anotherType != CodeBlock::class.java
    }

    override fun applyInternal(text: Spannable, from: Int, to: Int, flags: Int) {
        super.applyInternal(text, from, to,
                if (to == text.length) Spannable.SPAN_INCLUSIVE_INCLUSIVE
                else Spannable.SPAN_INCLUSIVE_INCLUSIVE)
    }

    override fun apply(text: Spannable, from: Int, to: Int, flags: Int) {
        apply(text, from, to, flags, false)
    }

    private fun apply(text: Spannable, from: Int, to: Int, flags: Int, reapply: Boolean) {
        if (text.length == to || text[to - 1] == '\n') {
            var i = from
            var index = 0
            while (i < to) {
                val listItem: ListItem
                if (index < listItems.size)
                    listItem = listItems[index]
                else {
                    listItem = createListItem(index)
                    listItems.add(listItem)
                }
                setIndex(listItem, index)
                val spanTo = minOf(text.indexOf('\n', i, to), to)
//                if (reapply)
//                    listItem.reApply(text, i, spanTo)
//                else
                    listItem.applyInternal(text, i, spanTo,
                            if (spanTo == text.length) Spannable.SPAN_INCLUSIVE_INCLUSIVE
                            else Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                i = spanTo + 1
                index++
            }
        } else throw IllegalArgumentException("List markup should end with new line")
    }

    internal fun reApply(text: Spannable, from: Int, to: Int) {
        remove(text)
        apply(text, from, to, 0, true)
    }

    protected abstract fun createListItem(index: Int): ListItem

    protected open fun setIndex(listItem: ListItem, index: Int) {}

    override fun remove(text: Spannable) {
        listItems.forEach({ it.removeInternal(text) })
        listItems.clear()
    }

    override fun updateSpanFlags(text: Spannable, flags: Int) {
        // no-op; this markup itself is span and this has no span
    }

    companion object {
        const val DEFAULT_MARGIN = 50
    }
}