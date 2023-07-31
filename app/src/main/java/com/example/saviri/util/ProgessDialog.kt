package com.example.saviri.util

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import com.example.saviri.R

object ProgessDialog {
    @SuppressLint("InflateParams")
    fun progressDialog(context: Context):Dialog{
        val dialog = Dialog(context)
        val inflate = LayoutInflater.from(context).inflate(R.layout.loading,null)
        dialog.setContentView(inflate)
        dialog.setCancelable(false)
        dialog.window!!.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )
        return dialog
    }
}