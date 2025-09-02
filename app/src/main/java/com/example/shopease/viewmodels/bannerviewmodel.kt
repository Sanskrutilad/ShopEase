package com.example.shopease.viewmodels

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class BannerViewModel : ViewModel() {
    private val _bannerList = mutableStateListOf<String>()
    val bannerList: List<String> = _bannerList

    init {
        fetchBannersFromFirebase()
    }

    private fun fetchBannersFromFirebase() {
        val database = FirebaseDatabase.getInstance().getReference("banners")
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _bannerList.clear()
                for (child in snapshot.children) {
                    val link = child.child("link").getValue(String::class.java)
                    if (!link.isNullOrEmpty()) {
                        _bannerList.add(link)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("BannerViewModel", "Firebase error: ${error.message}")
            }
        })
    }



}
