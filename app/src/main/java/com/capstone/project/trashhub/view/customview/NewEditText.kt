package com.capstone.project.trashhub.view.customview

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.capstone.project.trashhub.R

class NewEditText : AppCompatEditText {

    constructor(context: Context) : super(context) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }


    private fun init(){
        addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty()){
                    error = if (validateEmail(s.toString())) null else context.getString(
                        R.string.email_is_invalid
                    )
                }
            }

            override fun afterTextChanged(s: Editable) {

            }
        })
    }

    private fun validateEmail(s:String): Boolean{
        return android.util.Patterns.EMAIL_ADDRESS.matcher(s).matches()
    }

}