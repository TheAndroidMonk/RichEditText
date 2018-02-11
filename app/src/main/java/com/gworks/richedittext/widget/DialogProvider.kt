package com.gworks.richedittext.widget

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.EditText
import com.gworks.richedittext.R
import com.gworks.richedittext.RichEditTexter
import com.gworks.richedittext.markups.Link

class DialogProvider(private val context: Context){

    fun handleLink(richEditTexter: RichEditTexter, longClick: Boolean = false): Boolean {

        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_link,null)
        val markup = richEditTexter.getMarkupFromSelection(Link::class.java)
        val linkText = if(markup == null)"" else (markup as Link).attributes
        dialogView.findViewById<EditText>(R.id.edtLink).setText(linkText)
        if(linkText.isBlank() || longClick)
        AlertDialog.Builder(context).setView(dialogView)
                .setPositiveButton("APPLY", { _, _ -> richEditTexter.onMarkupMenuClicked(Link::class.java,linkText)})
                .setMessage("Enter a URL")
                .show()
        else
            richEditTexter.onMarkupMenuClicked(Link::class.java,linkText)
        return true
    }
}