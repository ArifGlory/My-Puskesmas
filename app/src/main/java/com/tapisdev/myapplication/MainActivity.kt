package com.tapisdev.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.tapisdev.cateringtenda.base.BaseActivity
import com.tapisdev.myapplication.activity.admin.HomeAdminActivity
import com.tapisdev.mysteam.model.UserModel
import com.tapisdev.mysteam.model.UserPreference
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    var TAG_LOGIN = "login"
    var TAG_dETAIL = "detail"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mUserPref = UserPreference(this)

        btnLogin.setOnClickListener {
            checkValidation()
        }
    }

    fun checkValidation(){
        var getEmail = editText_email.text.toString()
        var getPass  = editText_password.text.toString()

        if (getEmail.equals("") || getEmail.length == 0){
            showErrorMessage("Email harus diisi")
        }else if (getPass.equals("") || getPass.length == 0){
            showErrorMessage("Password harus diisi")
        }else{
            signIn(getEmail,getPass)
        }
    }

    fun signIn(email : String,pass : String){
        showLoading(this)
        auth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(this, OnCompleteListener { task ->
            if(task.isSuccessful){
                var userId = auth.currentUser?.uid
                Log.d(TAG_LOGIN,"user ID : "+userId)

                userRef.document(auth.currentUser!!.uid).get().addOnCompleteListener{ task ->
                    if (task.isSuccessful){
                        val document = task.result
                        dismissLoading()
                        if (document != null) {
                            if (document.exists()) {
                                Log.d(TAG_dETAIL, "DocumentSnapshot data: " + document.data)
                                //convert doc to object
                                var userModel : UserModel = document.toObject(UserModel::class.java)!!
                                Log.d(TAG_dETAIL,"usermodel name : "+userModel.name)
                                setSession(userModel)

                                Log.d("userpref"," jenis user : "+mUserPref.getJenisUser())
                                if (mUserPref.getJenisUser() != null){
                                    if (mUserPref.getJenisUser().equals("admin")){
                                        val i = Intent(applicationContext, HomeAdminActivity::class.java)
                                        startActivity(i)
                                    }else{
                                        val i = Intent(applicationContext, MainActivity::class.java)
                                        startActivity(i)
                                    }
                                }else{
                                    val i = Intent(applicationContext, MainActivity::class.java)
                                    startActivity(i)
                                }
                            } else {
                                Log.d(TAG_dETAIL, "No such document")
                                auth.signOut()
                                logout()
                                val i = Intent(applicationContext, MainActivity::class.java)
                                startActivity(i)
                            }
                        }
                    }else{
                        dismissLoading()
                        showErrorMessage("Error saaat mencari di database")
                        Log.d(TAG_dETAIL,"err : "+task.exception)
                        auth.signOut()
                        logout()
                        val i = Intent(applicationContext, MainActivity::class.java)
                        startActivity(i)
                    }
                }


            }else{
                dismissLoading()
                showErrorMessage("Password / Email salah")
            }
        })
    }

    fun setSession(userModel: UserModel){
        mUserPref.saveName(userModel.name)
        mUserPref.saveEmail(userModel.email)
        mUserPref.saveFoto(userModel.foto)
        mUserPref.saveJenisUser(userModel.jenis)
        mUserPref.savePhone(userModel.phone)
    }
}
