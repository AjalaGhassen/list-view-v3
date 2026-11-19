package com.example.listviewdecontacts

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ListeContactsFragment : Fragment() {

    private val viewModel: ContactViewModel by activityViewModels()

    private lateinit var etRecherche: EditText
    private lateinit var tvAucunResultat: TextView
    private lateinit var rvContacts: RecyclerView
    private lateinit var adapter: ContactRecyclerAdapter

    private var allContactsList: List<Contact> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_liste_contacts, container, false)

        etRecherche = view.findViewById(R.id.etRecherche)
        tvAucunResultat = view.findViewById(R.id.tvAucunResultat)
        rvContacts = view.findViewById(R.id.rvContacts)

        rvContacts.layoutManager = LinearLayoutManager(requireContext())
        adapter = ContactRecyclerAdapter(emptyList()) { contact ->
            ouvrirDetailContact(contact.id)
        }
        rvContacts.adapter = adapter

        viewModel.contacts.observe(viewLifecycleOwner) { contactsList ->
            allContactsList = contactsList
            filtrerContacts(etRecherche.text.toString())
        }

        etRecherche.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filtrerContacts(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        return view
    }

    private fun filtrerContacts(query: String) {
        val filteredList = if (query.trim().isEmpty()) {
            allContactsList
        } else {
            val q = query.trim().lowercase()
            allContactsList.filter { contact ->
                contact.nom.lowercase().contains(q) ||
                contact.prenom.lowercase().contains(q) ||
                contact.telephone.lowercase().contains(q)
            }
        }

        adapter.updateData(filteredList)

        if (filteredList.isEmpty()) {
            tvAucunResultat.visibility = View.VISIBLE
            rvContacts.visibility = View.GONE
        } else {
            tvAucunResultat.visibility = View.GONE
            rvContacts.visibility = View.VISIBLE
        }
    }

    private fun ouvrirDetailContact(contactId: Int) {
        val fragment = DetailContactFragment.newInstance(contactId)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}