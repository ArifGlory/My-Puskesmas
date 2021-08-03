package com.tapisdev.myapplication.activity.admin

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.tapisdev.cateringtenda.base.BaseActivity
import com.tapisdev.myapplication.R
import com.tapisdev.myapplication.model.Puskesmas
import com.tapisdev.myapplication.model.SharedVariable
import com.tapisdev.myapplication.util.PermissionHelper
import com.tapisdev.mysteam.model.UserPreference
import kotlinx.android.synthetic.main.activity_add_puskesmas.*
import kotlinx.android.synthetic.main.activity_edit_puskesmas.*
import kotlinx.android.synthetic.main.activity_edit_puskesmas.btnLokasi
import kotlinx.android.synthetic.main.activity_edit_puskesmas.edAlamat
import kotlinx.android.synthetic.main.activity_edit_puskesmas.edName
import kotlinx.android.synthetic.main.activity_edit_puskesmas.imagePuskesmas
import kotlinx.android.synthetic.main.activity_edit_puskesmas.textSelectImage
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class EditPuskesmasActivity : BaseActivity() ,PermissionHelper.PermissionListener{

    lateinit var i : Intent
    lateinit var puskesmas : Puskesmas
    var TAG_EDIT = "editPuskes"

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

        btnLokasi.setOnClickListener {
            startActivity(Intent(this, SelectLokasiActivity::class.java))
            overridePendingTransition(R.anim.slide_in_left, android.R.anim.slide_out_right)
        }
        btnEditPuskesmas.setOnClickListener {
            checkValidation()
        }
        rlImageEdit.setOnClickListener {
            launchGallery()
        }
        updateUI()
    }

    fun checkValidation(){
        var getName = edName.text.toString()
        var getAlamat = edAlamat.text.toString()
        
        if (getName.equals("") || getName.length == 0){
            showErrorMessage("Nama Belum diisi")
        } else if (getAlamat.equals("") || getAlamat.length == 0){
            showErrorMessage("Alamat Belum diisi")
        } else if (lat == 0.0){
            showErrorMessage("Lokasi belum dpilih")
        }else if (fileUri == null){
            updateDataOnly(getName,getAlamat)
        }
        else {
            updateDataWithFoto(getName,getAlamat)
        }
    }
    
    fun updateDataOnly(getName : String,getAlamat : String){
        showLoading(this)
        puskesRef.document(puskesmas.id_puskesmas).update("nama_puskesmas",getName)
        puskesRef.document(puskesmas.id_puskesmas).update("alamat",getAlamat)
        puskesRef.document(puskesmas.id_puskesmas).update("lat",lat.toString())
        puskesRef.document(puskesmas.id_puskesmas).update("lon",lon.toString()).addOnCompleteListener { task ->
            dismissLoading()
            if (task.isSuccessful){
                showSuccessMessage("Ubah Data Berhasil")
                val i = Intent(this,ListPuskesmasActivity::class.java)
                startActivity(i)
                finish()
            }else{
                showErrorMessage("Terjadi kesalahan, coba lagi nanti")
            }
        }
    }
    
    fun updateDataWithFoto(getName: String,getAlamat: String){
        showLoading(this)

        if (fileUri != null){
            Log.d(TAG_EDIT,"uri :"+fileUri.toString())

            val baos = ByteArrayOutputStream()
            fotoBitmap?.compress(Bitmap.CompressFormat.JPEG,50,baos)
            val data: ByteArray = baos.toByteArray()

            val fileReference = storageReference!!.child(System.currentTimeMillis().toString())
            val uploadTask = fileReference.putBytes(data)

            uploadTask.addOnFailureListener {
                    exception -> Log.d(TAG_EDIT, exception.toString())
            }.addOnSuccessListener {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                showSuccessMessage("Image Berhasil di upload")
                uploadTask.continueWithTask { task ->
                    if (!task.isSuccessful) {
                    }

                    fileReference.downloadUrl
                }.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUri = task.result

                        //DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(mAu.getInstance().getCurrentUser().getUid());
                        val url = downloadUri!!.toString()
                        Log.d(TAG_EDIT,"download URL : "+ downloadUri.toString())// This is the one you should store
                        puskesmas.foto = url

                        puskesRef.document(puskesmas.id_puskesmas).update("foto",url)
                        puskesRef.document(puskesmas.id_puskesmas).update("nama_puskesmas",getName)
                        puskesRef.document(puskesmas.id_puskesmas).update("alamat",getAlamat)
                        puskesRef.document(puskesmas.id_puskesmas).update("lat",lat.toString())
                        puskesRef.document(puskesmas.id_puskesmas).update("lon",lon.toString()).addOnCompleteListener { task ->
                            dismissLoading()
                            if (task.isSuccessful){
                                showSuccessMessage("Ubah Data Berhasil")
                                val i = Intent(this,ListPuskesmasActivity::class.java)
                                startActivity(i)
                                finish()
                            }else{
                                showErrorMessage("Terjadi kesalahan, coba lagi nanti")
                            }
                        }

                    } else {
                        dismissLoading()
                        showErrorMessage("Terjadi kesalahan, coba lagi nanti")
                    }
                }
            }.addOnProgressListener { taskSnapshot ->
                val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
                pDialogLoading.setTitleText("Uploaded " + progress.toInt() + "%...")
            }


        }else{
            dismissLoading()
            showErrorMessage("Anda belum memilih file")
        }
    }

    fun updateUI(){
        edName.setText(puskesmas.nama_puskesmas)
        edAlamat.setText(puskesmas.alamat)
        Glide.with(this)
            .load(puskesmas.foto)
            .into(imagePuskesmas)
        textSelectImage.visibility = View.INVISIBLE
        
        lat = puskesmas.lat.toDouble()
        lon = puskesmas.lon.toDouble()
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

    override fun onResume() {
        super.onResume()
        if (SharedVariable.centerLatLon.latitude != 0.0){
            lat = SharedVariable.centerLatLon.latitude
            lon = SharedVariable.centerLatLon.longitude

            val img: Drawable = btnLokasi.context.resources.getDrawable(R.drawable.ic_check_black_24dp)
            btnLokasi.setText("Lokasi Telah dipilih")
            btnLokasi.setCompoundDrawables(img,null,null,null)

            /* var alamatLokasi = getCompleteAddress(lat,lon)
             edAlamat.setText(alamatLokasi)*/
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
