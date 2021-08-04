package com.tapisdev.myapplication.adapter

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.opengl.GLDebugHelper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tapisdev.myapplication.R
import com.tapisdev.myapplication.activity.admin.DetailPuskesmasActivity
import com.tapisdev.myapplication.activity.admin.ListPuskesmasActivity
import com.tapisdev.myapplication.activity.pengguna.LokasiPuskesmasActivity
import com.tapisdev.myapplication.model.Puskesmas
import com.tapisdev.mysteam.model.UserModel
import com.tapisdev.mysteam.model.UserPreference
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.row_puskesmas.view.*
import java.io.Serializable
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class AdapterPuskesmas(private val list:ArrayList<Puskesmas>) : RecyclerView.Adapter<AdapterPuskesmas.Holder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.row_puskesmas,parent,false))
    }

    override fun getItemCount(): Int = list?.size
    lateinit var mUserPref : UserPreference
    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    val myDB = FirebaseFirestore.getInstance()
    val puskesRef = myDB.collection("puskesmas")
    lateinit var pDialogLoading : SweetAlertDialog
    lateinit var mContext : Context

    override fun onBindViewHolder(holder: Holder, position: Int) {

        val nf = NumberFormat.getNumberInstance(Locale.GERMAN)
        val df = nf as DecimalFormat
        pDialogLoading = SweetAlertDialog(holder.view.linePuskesmas.context, SweetAlertDialog.PROGRESS_TYPE)
        pDialogLoading.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialogLoading.setTitleText("Loading..")
        pDialogLoading.setCancelable(false)

        mUserPref = UserPreference(holder.view.linePuskesmas.context)
        mContext = holder.view.linePuskesmas.context

        holder.view.tvNamaPuskes.text = list?.get(position)?.nama_puskesmas
        holder.view.tvAlamat.text =list?.get(position)?.alamat


        Glide.with(holder.view.ivPuskesmas.context)
            .load(list?.get(position)?.foto)
            .into(holder.view.ivPuskesmas)

        holder.view.linePuskesmas.setOnClickListener {
            Log.d("adapterIsi",""+list.get(position).toString())
            val i = Intent(holder.view.linePuskesmas.context, DetailPuskesmasActivity::class.java)
            i.putExtra("puskesmas",list.get(position) as Serializable)
            holder.view.linePuskesmas.context.startActivity(i)
        }

    }


    class Holder(val view: View) : RecyclerView.ViewHolder(view)

}