package com.tapisdev.myapplication.activity.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.tapisdev.cateringtenda.base.BaseActivity
import com.tapisdev.myapplication.R
import com.tapisdev.myapplication.activity.pengguna.LokasiPuskesmasActivity
import com.tapisdev.myapplication.model.Puskesmas
import com.tapisdev.mysteam.model.UserPreference
import es.dmoral.toasty.Toasty
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
            val i = Intent(this,EditPuskesmasActivity::class.java)
            i.putExtra("puskesmas",puskesmas as Serializable)
            startActivity(i)
        }
        btnHapusPuskesmas.setOnClickListener {
            SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Hapus puskesmas ini ?")
                .setContentText("Data yang dihapus tidak bisa dikembalikan")
                .setConfirmText("Ya")
                .setConfirmClickListener { sDialog ->
                    sDialog.dismissWithAnimation()
                    showLoading(this)

                    puskesRef.document(puskesmas.id_puskesmas).delete().addOnCompleteListener { task ->
                        dismissLoading()
                        if (task.isSuccessful){
                            Toasty.success(this, "Data berhasil Dihapus", Toast.LENGTH_SHORT, true).show()
                            onBackPressed()
                        }else{
                            Toasty.error(this, "terjadi kesalahan : "+task.exception, Toast.LENGTH_SHORT, true).show()
                            Log.d("deletepuskes","err : "+task.exception)
                        }
                    }

                }
                .setCancelButton(
                    "Tidak"
                ) { sDialog -> sDialog.dismissWithAnimation() }
                .show()
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
                btnHapusPuskesmas.visibility = View.VISIBLE
            }else{
                btnEditPuskesmas.visibility = View.GONE
                btnHapusPuskesmas.visibility = View.GONE
            }
        }
    }
}
