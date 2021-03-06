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
import com.gworks.richedittext.updateSpanFlags

abstract class SingleSpanAttributedMarkup<ATTR>(val span: Any, attributes: ATTR) : AttributedMarkup<ATTR>(attributes) {

    override fun apply(text: Spannable, from: Int, to: Int, flags: Int) {
        text.setSpan(span, from, to, flags)
    }

    override fun remove(text: Spannable) {
        text.removeSpan(span)
    }

    override fun updateSpanFlags(text: Spannable, flags: Int) {
        updateSpanFlags(text, span, flags)
    }
}