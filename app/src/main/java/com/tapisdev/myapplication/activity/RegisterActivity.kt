package com.tapisdev.myapplication.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tapisdev.cateringtenda.base.BaseActivity
import com.tapisdev.myapplication.R

class RegisterActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
    }
}
