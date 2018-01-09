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
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.gworks.richedittext.markups.Bold
import com.gworks.richedittext.markups.Italic
import com.gworks.richedittext.markups.Markup
import com.gworks.richedittext.widget.RichEditText


class MainActivity : AppCompatActivity() {

    private lateinit var editText: RichEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edt_layout)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        editText = findViewById(R.id.editText)
        val layout = findViewById<LinearLayout>(R.id.buttonLayout)
        layout.addView(newMarkupButton("Bold", Bold::class.java))
        layout.addView(newMarkupButton("Italics", Italic::class.java))

        val textView = findViewById<TextView>(R.id.textView)

        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener({textView.text = editText.getHtml()})
    }

    private fun newMarkupButton(label: String, mId: Class<out Markup>): Button {
        val b = Button(this)
        b.text = label
        b.tag = mId
        b.setOnClickListener { editText.onMarkupClicked(it.tag as Class<out Markup>,null) }
        return b
    }
}