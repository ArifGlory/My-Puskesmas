package com.tapisdev.myapplication.activity.admin

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.tapisdev.cateringtenda.base.BaseActivity
import com.tapisdev.myapplication.R
import com.tapisdev.myapplication.model.Puskesmas
import com.tapisdev.myapplication.util.PermissionHelper
import com.tapisdev.mysteam.model.UserPreference
import kotlinx.android.synthetic.main.activity_edit_puskesmas.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class EditPuskesmasActivity : BaseActivity() ,PermissionHelper.PermissionListener{

    lateinit var i : Intent
    lateinit var puskesmas : Puskesmas

    val sdf = SimpleDateFormat("yyyy-MM-dd")
    val sdfTime = SimpleDateFormat("yyyy-MM-dd HH:mm")
    val currentDateTime = sdfTime.format(Date())

    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    var lat = 0.0
    var lon = 0.0

    lateinit var  permissionHelper : PermissionHelper
    var fotoBitmap : Bitmap? = null
    private var fileUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_puskesmas)
        mUserPref = UserPreference(this)
        storageReference = FirebaseStorage.getInstance().reference.child("images")

        i = intent
        puskesmas = i.getSerializableExtra("puskesmas") as Puskesmas

        permissionHelper = PermissionHelper(this)
        permissionHelper.setPermissionListener(this)

        updateUI()
    }

    fun updateUI(){
        edName.setText(puskesmas.nama_puskesmas)
        edAlamat.setText(puskesmas.alamat)
        Glide.with(this)
            .load(puskesmas.foto)
            .into(imagePuskesmas)
        textSelectImage.visibility = View.INVISIBLE

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if(data == null || data.data == null){
                return
            }

            filePath = data.data
            fileUri = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                fotoBitmap = bitmap
                imagePuskesmas.setImageBitmap(bitmap)
                textSelectImage.visibility = View.GONE
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun launchGallery() {
        var listPermissions: MutableList<String> = ArrayList()
        listPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        listPermissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        permissionHelper.checkAndRequestPermissions(listPermissions)
    }

    override fun onPermissionCheckDone() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }
}
