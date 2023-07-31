package com.example.saviri.util

import android.content.Context
import android.widget.Toast




class ToastUtil {


    fun showMessage(message: String?,context: Context?){
        getToast(message,context).show()
    }


    private fun getToast(message:String?, context: Context?):Toast{
        return Toast.makeText(context,message,Toast.LENGTH_LONG)
    }
}