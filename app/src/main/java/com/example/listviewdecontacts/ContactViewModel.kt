package com.example.listviewdecontacts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ContactViewModel : ViewModel() {

    private val _contacts = MutableLiveData<MutableList<Contact>>()
    val contacts: LiveData<MutableList<Contact>> get() = _contacts

    init {
        _contacts.value = mutableListOf(
            Contact(1, "Ben Ali", "Ahmed", "+216 98 123 456", "ahmed.benali@example.com", latitude = 36.8065, longitude = 10.1815),
            Contact(2, "Trabelsi", "Sarra", "+216 22 987 654", "sarra.trabelsi@example.com", latitude = 35.8256, longitude = 10.6369),
            Contact(3, "Gharbi", "Mohamed", "+216 55 456 789", "mohamed.gharbi@example.com", latitude = 34.7406, longitude = 10.7603),
            Contact(4, "Jaziri", "Ines", "+216 20 321 654", "ines.jaziri@example.com", latitude = 36.8665, longitude = 10.3347),
            Contact(5, "Mansouri", "Youssef", "+216 97 654 321", "youssef.mansouri@example.com", latitude = 37.2746, longitude = 9.8739),
            Contact(6, "Chaabane", "Amira", "+216 50 111 222", "amira.chaabane@example.com", latitude = null, longitude = null)
        )
    }

    fun getContactById(id: Int): Contact? {
        return _contacts.value?.find { it.id == id }
    }

    fun updateContact(updatedContact: Contact) {
        val list = _contacts.value ?: return
        val index = list.indexOfFirst { it.id == updatedContact.id }
        if (index != -1) {
            list[index] = updatedContact
            _contacts.value = list
        }
    }
}