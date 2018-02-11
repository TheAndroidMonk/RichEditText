package com.gworks.richedittext.widget

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.ImageButton
import com.gworks.richedittext.R
import com.gworks.richedittext.markups.*

class RichEditTextBuilder(private val context: Context) {

    private val rEdiText: RichEditText = RichEditText(context)
    private val dialogProvider = DialogProvider(context)

    fun createImageButton(resId: Int, clickListener: View.OnClickListener):ImageButton{

        val imgButton = ImageButton(context)
        imgButton.setImageResource(resId)
        imgButton.setOnClickListener(clickListener)
        imgButton.background = context.getDrawable(R.drawable.button_selector)
        imgButton.setPadding(dip(10),dip(10),dip(10),dip(10))
        return imgButton
    }

    private fun dip(value:Int):Int{
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,value.toFloat(),context.resources.displayMetrics).toInt()
    }

    fun withBold():RichEditTextBuilder {
        rEdiText.addMarkupView(
                createImageButton(R.drawable.ic_format_bold_black_18dp,
                        View.OnClickListener{rEdiText.onMarkupClicked(Bold::class.java, null)}
                ))
        return this
    }

    fun withItalics(): RichEditTextBuilder {

        rEdiText.addMarkupView(
                createImageButton(R.drawable.ic_format_italic_black_18dp,
                        View.OnClickListener { rEdiText.onMarkupClicked(Italic::class.java,null)})
        )
        return this
    }

    fun withUnderline(): RichEditTextBuilder{

        rEdiText.addMarkupView(
                createImageButton(R.drawable.ic_format_underlined_black_18dp,
                        View.OnClickListener { rEdiText.onMarkupClicked(Underline::class.java, null)})
        )
        return this
    }

    fun withSubscript(): RichEditTextBuilder {
        rEdiText.addMarkupView(
                createImageButton(R.mipmap.ic_launcher,
                        View.OnClickListener { rEdiText.onMarkupClicked(Subscript::class.java, null)})
        )
        return this
    }

    fun withSuperscript(): RichEditTextBuilder {
        rEdiText.addMarkupView(
                createImageButton(R.mipmap.ic_launcher,
                        View.OnClickListener { rEdiText.onMarkupClicked(Superscript::class.java,null)})
        )
        return this
    }

    fun withStrikeThrough(): RichEditTextBuilder {
        rEdiText.addMarkupView(
                createImageButton(R.drawable.ic_format_strikethrough_black_18dp,
                        View.OnClickListener { rEdiText.onMarkupClicked(Strikethrough::class.java, null)})
        )
        return this
    }

    fun withCode(): RichEditTextBuilder {
        rEdiText.addMarkupView(
                createImageButton(R.mipmap.ic_launcher,
                        View.OnClickListener { rEdiText.onMarkupClicked(Code::class.java, Code.Attributes())})
        )
        return this
    }

    fun withCodeBlock(): RichEditTextBuilder {
        rEdiText.addMarkupView(
                createImageButton(R.mipmap.ic_launcher,
                        View.OnClickListener { rEdiText.onParagraphMarkupClicked(CodeBlock::class.java, CodeBlock.Attributes(50))})
        )
        return this
    }

    fun withHyperLink(): RichEditTextBuilder {

        val imageButton =  createImageButton(R.drawable.ic_insert_link_black_18dp,View.OnClickListener{dialogProvider.handleLink(rEdiText.rtManager)})
        imageButton.setOnLongClickListener({dialogProvider.handleLink(rEdiText.rtManager,true)})
        rEdiText.addMarkupView(imageButton)
        return this
    }

    fun build(): RichEditText {
        return rEdiText
    }
}