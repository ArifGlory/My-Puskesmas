package com.tapisdev.myapplication.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Puskesmas(
    var nama_puskesmas : String = "",
    var alamat : String = "",
    var foto : String = "",
    var lat : String = "",
    var lon : String = "",
    var created_at : String = "",
    var jam_kerja : String = "",
    var id_puskesmas : String = ""
) : Parcelable,java.io.Serializable