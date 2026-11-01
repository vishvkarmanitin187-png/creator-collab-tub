package com.example.repository

import android.content.Context
import com.example.R
import com.example.model.*
import com.example.util.OperationType
import com.example.util.handleFirestoreError
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await

class FirestoreCreatorCollabRepository(private val db: FirebaseFirestore) : CreatorCollabRepository {

    constructor(context: Context) : this(
        try {
            val app = com.google.firebase.FirebaseApp.getInstance()
            val dbId = try {
                context.applicationContext.getString(R.string.firestore_database_id)
            } catch (e: Exception) { "" }

            if (dbId.isNotBlank()) {
                FirebaseFirestore.getInstance(app, dbId)
            } else {
                FirebaseFirestore.getInstance(app)
            }
        } catch (e: Exception) {