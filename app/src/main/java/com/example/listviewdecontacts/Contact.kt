package com.example.listviewdecontacts

import java.io.Serializable

data class Contact(
    val id: Int,
    var nom: String,
    var prenom: String,
    var telephone: String,
    var email: String,
    var photoUri: String? = null,
    var imageResId: Int = R.drawable.ic_default_avatar,
    var latitude: Double? = null,
    var longitude: Double? = null
) : Serializable