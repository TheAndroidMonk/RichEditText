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

package com.gworks.richedittext.widget

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.AppCompatEditText
import android.util.AttributeSet
import com.gworks.richedittext.RichEditTexter
import com.gworks.richedittext.markups.Markup
import com.gworks.richedittext.markups.OList
import com.gworks.richedittext.markups.UList


class RichEditText : AppCompatEditText {

    private lateinit var manager: RichEditTexter

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        manager = RichEditTexter(this)
    }

    fun onMarkupClicked(id: Class<out Markup>, value: Any?) {
        manager.onMarkupMenuClicked(id, value)
    }

    fun onParagraphMarkupClicked(id: Class<out Markup>, value: Any?) {
        manager.onParagraphMarkupMenuClicked(id, value)
    }

    fun getHtml(): String {
        return manager.getHtml(null)
    }

    fun setHtml(html: String) {
        manager.setHtml(html)
    }
}
