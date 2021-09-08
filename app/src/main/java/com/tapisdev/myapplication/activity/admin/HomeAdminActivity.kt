package com.tapisdev.myapplication.activity.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.Query
import com.tapisdev.cateringtenda.base.BaseActivity
import com.tapisdev.myapplication.MainActivity
import com.tapisdev.myapplication.R
import com.tapisdev.myapplication.activity.PersebaranActivity
import com.tapisdev.myapplication.activity.pengguna.HomePenggunaActivity
import com.tapisdev.myapplication.activity.pengguna.LokasiPuskesmasActivity
import com.tapisdev.myapplication.adapter.AdapterPuskesmas
import com.tapisdev.myapplication.model.Puskesmas
import com.tapisdev.mysteam.model.UserPreference
import kotlinx.android.synthetic.main.activity_home_admin.*

class HomeAdminActivity : BaseActivity() {

    var TAG_GET = "getPuskes"
    lateinit var adapter: AdapterPuskesmas
    var listPuskesmas = ArrayList<Puskesmas>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_admin)
        mUserPref = UserPreference(this)

        adapter = AdapterPuskesmas(listPuskesmas)
        rvPuskesAdmin.setHasFixedSize(true)
        rvPuskesAdmin.layoutManager = LinearLayoutManager(this) as RecyclerView.LayoutManager?
        rvPuskesAdmin.adapter = adapter

        lineLogout.setOnClickListener {
            logout()
            auth.signOut()

            val i = Intent(this, HomePenggunaActivity::class.java)
            startActivity(i)
            finish()
        }
        lineAddPuskes.setOnClickListener {
           startActivity(Intent(this, AddPuskesmasActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
        }
        lineProfilAdmin.setOnClickListener {
            val i = Intent(this, ProfilAdminActivity::class.java)
            startActivity(i)
        }
        lineSemuaPuskes.setOnClickListener {
            startActivity(Intent(this, ListPuskesmasActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
        }
        linePersebaranPuskes.setOnClickListener {
            startActivity(Intent(this, PersebaranActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
        }

        getDataPuskes()
    }

    fun getDataPuskes(){
        /*val sdf = SimpleDateFormat("yyyy-MM-dd")
        val currentDate = sdf.format(Date())*/
        puskesRef.orderBy("created_at", Query.Direction.DESCENDING)
            .limit(5)
            .get().addOnSuccessListener { result ->
                listPuskesmas.clear()
                //Log.d(TAG_GET_Sparepart," datanya "+result.documents)
                for (document in result){
                    //Log.d(TAG_GET_Sparepart, "Datanya : "+document.data)
                    var puskes : Puskesmas = document.toObject(Puskesmas::class.java)
                    puskes.id_puskesmas = document.id
                    listPuskesmas.add(puskes)

                }
                if (listPuskesmas.size == 0){
                    animation_view_puskes.setAnimation(R.raw.empty_box)
                    animation_view_puskes.playAnimation()
                    animation_view_puskes.loop(false)
                }else{
                    animation_view_puskes.visibility = View.INVISIBLE
                }
                adapter.notifyDataSetChanged()

            }.addOnFailureListener { exception ->
                showErrorMessage("terjadi kesalahan : "+exception.message)
                Log.d(TAG_GET,"err : "+exception.message)
            }
    }
}
