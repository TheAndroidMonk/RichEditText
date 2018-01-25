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
import com.gworks.richedittext.converters.indexOf
import java.util.*

abstract class List<ATTR>(attributes: ATTR) : AttributedMarkup<ATTR>(attributes) {

    override val isSplittable: Boolean
        get() = true

    private val listItems = LinkedList<ListItem>()

    override fun applyInternal(text: Spannable, from: Int, to: Int, flags: Int) {
        super.applyInternal(text, from, to,
                if(to == text.length) Spannable.SPAN_INCLUSIVE_INCLUSIVE
                else Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
    }

    override fun apply(text: Spannable, from: Int, to: Int, flags: Int) {
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
                val spanTo = minOf(text.indexOf('\n', i, to) + 1, to)
                listItem.applyInternal(text, i, spanTo,
                        if (spanTo == text.length) Spannable.SPAN_INCLUSIVE_INCLUSIVE
                        else Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
                i = spanTo
                index++
            }
        } else throw IllegalArgumentException("List markup should end with new line")
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

