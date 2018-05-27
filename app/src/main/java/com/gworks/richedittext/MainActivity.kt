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

package com.gworks.richedittext

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import com.gworks.richedittext.widget.RichEditText
import com.gworks.richedittext.widget.RichEditTextBuilder

class MainActivity : AppCompatActivity() {

    private lateinit var tv: TextView

    private lateinit var richText: RichEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        richText = RichEditTextBuilder(this)
                .withBold()
                .withItalics()
                .withStrikeThrough()
                .withHyperLink()
                .withUnderline()
                .withUnorderedList()
                .withOrderedList()
                .build()
        richText.setPlainText("Some\nsample\ntext")
        val ll = LinearLayout(this)
        ll.orientation = LinearLayout.VERTICAL
        tv = TextView(this)
        ll.addView(tv)
        ll.addView(richText)
        setContentView(ll)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.add(0, 101, 0, "VIEW")?.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.itemId == 101) {
            tv.text = richText.getHtml()
        }
        return true
    }
}