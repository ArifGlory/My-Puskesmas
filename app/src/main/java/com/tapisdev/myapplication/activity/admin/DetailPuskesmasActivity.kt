package com.tapisdev.myapplication.activity.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.tapisdev.cateringtenda.base.BaseActivity
import com.tapisdev.myapplication.R
import com.tapisdev.myapplication.activity.pengguna.LokasiPuskesmasActivity
import com.tapisdev.myapplication.model.Puskesmas
import com.tapisdev.mysteam.model.UserPreference
import kotlinx.android.synthetic.main.activity_detail_puskesmas.*
import java.io.Serializable

class DetailPuskesmasActivity : BaseActivity() {

    lateinit var i : Intent
    lateinit var puskesmas : Puskesmas


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_puskesmas)
        mUserPref = UserPreference(this)
        i = intent

        puskesmas = i.getSerializableExtra("puskesmas") as Puskesmas

        btnLokasi.setOnClickListener {
            val i = Intent(this,LokasiPuskesmasActivity::class.java)
            i.putExtra("puskesmas",puskesmas as Serializable)
            startActivity(i)
        }
        btnEditPuskesmas.setOnClickListener {

        }

        updateUI()
    }

    fun updateUI(){
        tvNamaPuskesmas.setText(puskesmas.nama_puskesmas)
        tvAlamat.setText(puskesmas.alamat)
        Glide.with(this)
            .load(puskesmas.foto)
            .into(ivFotoPuskesmas)

        if (mUserPref.getJenisUser() != null){
            if (mUserPref.getJenisUser().equals("admin")){
                btnEditPuskesmas.visibility = View.VISIBLE
            }
        }
    }
}
