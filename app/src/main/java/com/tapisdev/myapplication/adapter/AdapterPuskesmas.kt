package com.tapisdev.myapplication.adapter

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
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
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.tapisdev.myapplication.R
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

    override fun onBindViewHolder(holder: Holder, position: Int) {

        val nf = NumberFormat.getNumberInstance(Locale.GERMAN)
        val df = nf as DecimalFormat

        holder.view.tvNamaPuskes.text = list?.get(position)?.nama_puskesmas
        holder.view.tvAlamat.text =list?.get(position)?.alamat


        Glide.with(holder.view.ivPuskesmas.context)
            .load(list?.get(position)?.foto)
            .into(holder.view.ivPuskesmas)

        holder.view.linePuskesmas.setOnClickListener {
            Log.d("adapterIsi",""+list.get(position).toString())
           /* val i = Intent(holder.view.linePuskesmas.context, DetailSteamActivity::class.java)
            i.putExtra("steam",list.get(position) as Serializable)
            holder.view.linePuskesmas.context.startActivity(i)*/
        }

    }


    class Holder(val view: View) : RecyclerView.ViewHolder(view)

}