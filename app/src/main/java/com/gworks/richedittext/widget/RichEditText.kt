package com.gworks.richedittext.widget

import android.content.Context
import android.support.v7.widget.AppCompatEditText
import android.text.InputType
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.gworks.richedittext.RichEditTexter
import com.gworks.richedittext.addView
import com.gworks.richedittext.markups.Markup

class RichEditText : RelativeLayout {

    private val editText = AppCompatEditText(context)
    private val linearLayout = LinearLayout(context)
    internal val rtManager = RichEditTexter(editText)

    constructor(ctx: Context) : super(ctx) {
        editText.background = null
        editText.gravity = Gravity.TOP or Gravity.START
        addViews()
    }

    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs)

    private fun addViews() {
        val scrollView = HorizontalScrollView(context)
        addView(scrollView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, RelativeLayout.ALIGN_PARENT_BOTTOM, -1)
        scrollView.addView(linearLayout, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        addView(editText, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, ABOVE, scrollView.id)
    }

    fun onMarkupClicked(id: Class<out Markup>, value: Any?) {
        rtManager.onMarkupMenuClicked(id, value)
    }

    fun onParagraphMarkupClicked(id: Class<out Markup>, value: Any?) {
        rtManager.onParagraphMarkupMenuClicked(id, value)
    }

    fun getHtml(): String {
        return rtManager.getHtml(null)
    }

    fun setHtml(html: String) {
        rtManager.setHtml(html)
    }

    fun setPlainText(text: String) {
        rtManager.setPlainText(text)
    }

    fun getPlainText():String {
        return rtManager.getPlainText()
    }

    fun addMarkupView(view: View) {
        linearLayout.addView(view)
    }
}
