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

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.gworks.richedittext.markups.*
import com.gworks.richedittext.widget.RichEditText


class MainActivity : AppCompatActivity() {

    private lateinit var editText: RichEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edt_layout)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        editText = findViewById(R.id.editText)
        val layout = findViewById<LinearLayout>(R.id.buttonLayout)

        layout.addView(newMarkupButton("Bold", { editText.onMarkupClicked(Bold::class.java, null) }))
        layout.addView(newMarkupButton("Italic", { editText.onMarkupClicked(Italic::class.java, null) }))
        layout.addView(newMarkupButton("Underline", { editText.onMarkupClicked(Underline::class.java, null) }))
        layout.addView(newMarkupButton("Link", { editText.onMarkupClicked(Link::class.java, "www.google.com") }))
        layout.addView(newMarkupButton("OList", { editText.onParagraphMarkupClicked(OList::class.java, OList.Attributes(50, Color.DKGRAY, ".")) }))
        layout.addView(newMarkupButton("UList", { editText.onParagraphMarkupClicked(UList::class.java, UList.Attributes(50, Color.DKGRAY)) }))
        layout.addView(newMarkupButton("Font", { editText.onMarkupClicked(Font::class.java, null) }))
        layout.addView(newMarkupButton("Sub", { editText.onMarkupClicked(Subscript::class.java, null) }))
        layout.addView(newMarkupButton("Sup", { editText.onMarkupClicked(Superscript::class.java, null) }))
        layout.addView(newMarkupButton("Strike", { editText.onMarkupClicked(Strikethrough::class.java, null) }))

        val textView = findViewById<TextView>(R.id.textView)

        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener({ textView.text = editText.getHtml() })
//        editText.setHtml("svf<b>efsf</b> fgfgf f<i>df<b>g</i>fg</b>\n<p align=\"center\">This is a new paragraph. hchcghv ghgv jhhb" +
//                "hjjhhb ygyghjb hjjhb bjb gvguih hgh</p>\nrtetgf" +
//                "fgfgf gtfdg fgdfg fgfghfg dfhfgh dghfghfg dgfg")

        editText.setHtml("svf<b><i>efsf</i></b>hj f<b>gfg</b>f f<i>df<b>g</i>fg</b>")
    }

    private fun newMarkupButton(label: String, listener: (View) -> (Unit)): Button {
        val b = Button(this)
        b.text = label
        b.setOnClickListener(listener)
        return b
    }
}