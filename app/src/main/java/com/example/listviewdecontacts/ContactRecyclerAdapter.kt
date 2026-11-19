package com.example.listviewdecontacts

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ContactRecyclerAdapter(
    private var contacts: List<Contact>,
    private val onItemClick: (Contact) -> Unit
) : RecyclerView.Adapter<ContactRecyclerAdapter.ContactViewHolder>() {

    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivPhoto: ImageView = itemView.findViewById(R.id.ivPhotoItem)
        val tvNomPrenom: TextView = itemView.findViewById(R.id.tvNomPrenomItem)
        val tvTelephone: TextView = itemView.findViewById(R.id.tvTelephoneItem)
        val tvEmail: TextView = itemView.findViewById(R.id.tvEmailItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_contact, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contacts[position]
        holder.tvNomPrenom.text = "${contact.prenom} ${contact.nom}"
        holder.tvTelephone.text = contact.telephone
        holder.tvEmail.text = contact.email

        if (!contact.photoUri.isNullOrEmpty()) {
            try {
                holder.ivPhoto.setImageURI(Uri.parse(contact.photoUri))
            } catch (e: Exception) {
                holder.ivPhoto.setImageResource(contact.imageResId)
            }
        } else {
            holder.ivPhoto.setImageResource(contact.imageResId)
        }

        holder.itemView.setOnClickListener {
            onItemClick(contact)
        }
    }

    override fun getItemCount(): Int = contacts.size

    fun updateData(newContacts: List<Contact>) {
        contacts = newContacts
        notifyDataSetChanged()
    }
}