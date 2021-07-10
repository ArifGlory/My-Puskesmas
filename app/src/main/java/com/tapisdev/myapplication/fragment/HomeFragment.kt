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
import com.synnapps.carouselview.CarouselView
import com.synnapps.carouselview.ImageListener
import com.tapisdev.cateringtenda.base.BaseFragment
import com.tapisdev.myapplication.R
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment : BaseFragment() {



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_home, container, false)

        return root
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
