package com.sokamn.trovami.data.network

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseClient @Inject constructor(){
    val auth: FirebaseAuth get() = FirebaseAuth.getInstance()
    val dbFirestore = Firebase.firestore
    val dbRealtime  = FirebaseDatabase.getInstance().reference
    val dbStorage = FirebaseStorage.getInstance().reference
}