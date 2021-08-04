package com.jcy.letsgohiking.home.tab2.model

import android.os.Parcelable
import com.jcy.letsgohiking.home.tab2.model.LocationLatLngEntity
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SearchResultEntity(
    val fullAdress: String,
    val name: String,
    val locationLatLng: LocationLatLngEntity
): Parcelable
