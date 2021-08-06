package com.tapisdev.myapplication.activity.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import com.tapisdev.cateringtenda.base.BaseActivity
import com.tapisdev.myapplication.R
import com.tapisdev.myapplication.model.Fasilitas
import com.tapisdev.myapplication.model.Puskesmas
import com.tapisdev.mysteam.model.UserPreference
import kotlinx.android.synthetic.main.activity_add_fasilitas.*

class AddFasilitasActivity : BaseActivity() {

    lateinit var i : Intent
    lateinit var puskesmas : Puskesmas
    var TAG_SIMPAN = "simpanFasilitas"
    lateinit var fasilitas : Fasilitas

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_fasilitas)
        mUserPref = UserPreference(this)

        i = intent
        puskesmas = i.getSerializableExtra("puskesmas") as Puskesmas


        btnSimpanFasilitas.setOnClickListener {
            checkValidation()
        }
    }

    fun checkValidation(){
        var getName = edNamaFasilitas.text.toString()
        var getDeskripsi = edDeskripsi.text.toString()

        if (getName.equals("") || getName.length == 0){
            showErrorMessage("Nama Belum diisi")
        }else if (getDeskripsi.equals("") || getDeskripsi.length == 0){
            showErrorMessage("Deskripsi Belum diisi")
        }
        else {
            fasilitas = Fasilitas(getName,getDeskripsi,puskesmas.id_puskesmas,"")
            saveFasilitas()
        }
    }

    fun saveFasilitas(){
        showLoading(this)
        pDialogLoading.setTitleText("menyimpan data..")
        showInfoMessage("Sedang menyimpan ke database..")

        fasilitasRef.document().set(fasilitas).addOnCompleteListener {
                task ->
            if (task.isSuccessful){
                dismissLoading()
                showLongSuccessMessage("Tambah Fasilitas Berhasil")
                onBackPressed()
            }else{
                dismissLoading()
                showLongErrorMessage("Error, coba lagi nanti ")
                Log.d(TAG_SIMPAN,"err : "+task.exception)
            }
        }
    }
}
