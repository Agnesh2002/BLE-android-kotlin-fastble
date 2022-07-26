package com.example.fastbletrial

import android.content.Context
import androidx.appcompat.app.AlertDialog

object AlertWindow {

    private lateinit var alertDialog: AlertDialog
    lateinit var builder: AlertDialog.Builder

    fun buildWindow(context: Context, title: String, msg: String, drawable: Int)
    {
        builder = AlertDialog.Builder(context)
        builder.setCancelable(false)
            .setTitle(title)
            .setMessage(msg)
            .setIcon(drawable)
    }
    fun showWindow()
    {
        alertDialog= builder.create()
        alertDialog.show()
    }
    fun dismissWindow()
    {
        alertDialog.dismiss()
    }
}