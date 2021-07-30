package com.tapisdev.myapplication.fragment

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.Query
import com.synnapps.carouselview.CarouselView
import com.synnapps.carouselview.ImageListener
import com.tapisdev.cateringtenda.base.BaseFragment
import com.tapisdev.myapplication.R
import com.tapisdev.myapplication.activity.admin.ListPuskesmasActivity
import com.tapisdev.myapplication.adapter.AdapterPuskesmas
import com.tapisdev.myapplication.model.Puskesmas
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment : BaseFragment() {


    lateinit var animation_view_puskes : LottieAnimationView
    lateinit var rv_puskesmas : RecyclerView
    lateinit var btn_view_all : Button

    var TAG_GET = "getPuskes"
    lateinit var adapter: AdapterPuskesmas
    var listPuskesmas = ArrayList<Puskesmas>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_home, container, false)
        animation_view_puskes = root.findViewById(R.id.animation_view_puskes)
        rv_puskesmas = root.findViewById(R.id.rv_puskesmas)
        btn_view_all = root.findViewById(R.id.btn_view_all)

        adapter = AdapterPuskesmas(listPuskesmas)
        rv_puskesmas.setHasFixedSize(true)
        rv_puskesmas.layoutManager = LinearLayoutManager(requireContext()) as RecyclerView.LayoutManager?
        rv_puskesmas.adapter = adapter

        btn_view_all.setOnClickListener {
            val i = Intent(requireActivity(), ListPuskesmasActivity::class.java)
            startActivity(i)
        }

        getDataPuskes()
        return root
    }

    fun getDataPuskes(){
        /*val sdf = SimpleDateFormat("yyyy-MM-dd")
        val currentDate = sdf.format(Date())*/
        puskesRef.orderBy("created_at",Query.Direction.DESCENDING)
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


    companion object {
        fun newInstance(): HomeFragment{
            val fragment = HomeFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }


    override fun onResume() {
        super.onResume()
    }

}
