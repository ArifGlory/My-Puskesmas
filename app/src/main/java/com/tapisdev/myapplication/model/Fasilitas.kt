package com.tapisdev.myapplication.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Fasilitas(
    var nama_fasilitas : String = "",
    var deskripsi_fasilitas : String = "",
    var id_puskesmas : String = "",
    var id_fasilitas : String = ""
) : Parcelable,java.io.Serializable