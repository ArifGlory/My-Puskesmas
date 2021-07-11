package com.tapisdev.myapplication.activity.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tapisdev.cateringtenda.base.BaseActivity
import com.tapisdev.myapplication.MainActivity
import com.tapisdev.myapplication.R
import kotlinx.android.synthetic.main.activity_home_admin.*

class HomeAdminActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_admin)

        lineLogout.setOnClickListener {
            logout()
            auth.signOut()

            startActivity(Intent(this, MainActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
        }
        lineAddPuskes.setOnClickListener {
           startActivity(Intent(this, AddPuskesmasActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
        }
        lineProfilAdmin.setOnClickListener {

        }
        lineSemuaPuskes.setOnClickListener {

        }
    }
}
