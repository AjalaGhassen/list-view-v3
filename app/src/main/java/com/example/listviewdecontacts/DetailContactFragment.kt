package com.example.listviewdecontacts

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import java.io.File
import java.io.FileOutputStream

class DetailContactFragment : Fragment() {

    private val viewModel: ContactViewModel by activityViewModels()

    private var contactId: Int = -1
    private var currentContact: Contact? = null
    private var currentPhotoUri: String? = null

    private lateinit var ivPhoto: ImageView
    private lateinit var etNom: EditText
    private lateinit var etPrenom: EditText
    private lateinit var etTelephone: EditText
    private lateinit var etEmail: EditText
    private lateinit var etLatitude: EditText
    private lateinit var etLongitude: EditText

    private lateinit var btnGalerie: Button
    private lateinit var btnCamera: Button
    private lateinit var btnVoirCarte: Button
    private lateinit var btnAppeler: Button
    private lateinit var btnEmail: Button
    private lateinit var btnEnregistrer: Button
    private lateinit var btnAnnuler: Button

    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            currentPhotoUri = uri.toString()
            ivPhoto.setImageURI(uri)
        } else {
            Toast.makeText(requireContext(), "Sélection d'image annulée", Toast.LENGTH_SHORT).show()
        }
    }

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            val savedUri = saveBitmapToInternalStorage(bitmap)
            if (savedUri != null) {
                currentPhotoUri = savedUri.toString()
                ivPhoto.setImageBitmap(bitmap)
            }
        } else {
            Toast.makeText(requireContext(), "Prise de photo annulée", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val ARG_CONTACT_ID = "contact_id"

        fun newInstance(contactId: Int): DetailContactFragment {
            val fragment = DetailContactFragment()
            val args = Bundle()
            args.putInt(ARG_CONTACT_ID, contactId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        contactId = arguments?.getInt(ARG_CONTACT_ID, -1) ?: -1
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detail_contact, container, false)

        currentContact = viewModel.getContactById(contactId)
        if (currentContact == null) {
            parentFragmentManager.popBackStack()
            return view
        }

        val contact = currentContact!!

        ivPhoto = view.findViewById(R.id.ivDetailPhoto)
        etNom = view.findViewById(R.id.etNom)
        etPrenom = view.findViewById(R.id.etPrenom)
        etTelephone = view.findViewById(R.id.etTelephone)
        etEmail = view.findViewById(R.id.etEmail)
        etLatitude = view.findViewById(R.id.etLatitude)
        etLongitude = view.findViewById(R.id.etLongitude)

        btnGalerie = view.findViewById(R.id.btnGalerie)
        btnCamera = view.findViewById(R.id.btnCamera)
        btnVoirCarte = view.findViewById(R.id.btnVoirCarte)
        btnAppeler = view.findViewById(R.id.btnAppeler)
        btnEmail = view.findViewById(R.id.btnEmail)
        btnEnregistrer = view.findViewById(R.id.btnEnregistrer)
        btnAnnuler = view.findViewById(R.id.btnAnnuler)

        etNom.setText(contact.nom)
        etPrenom.setText(contact.prenom)
        etTelephone.setText(contact.telephone)
        etEmail.setText(contact.email)
        etLatitude.setText(contact.latitude?.toString() ?: "")
        etLongitude.setText(contact.longitude?.toString() ?: "")
        currentPhotoUri = contact.photoUri

        if (!currentPhotoUri.isNullOrEmpty()) {
            try {
                ivPhoto.setImageURI(Uri.parse(currentPhotoUri))
            } catch (e: Exception) {
                ivPhoto.setImageResource(contact.imageResId)
            }
        } else {
            ivPhoto.setImageResource(contact.imageResId)
        }

        btnGalerie.setOnClickListener { galleryLauncher.launch("image/*") }
        btnCamera.setOnClickListener { cameraLauncher.launch(null) }

        btnAppeler.setOnClickListener { lancerAppel() }
        btnEmail.setOnClickListener { lancerEmail() }
        btnVoirCarte.setOnClickListener { ouvrirCarte() }

        btnEnregistrer.setOnClickListener { enregistrerModifications() }
        btnAnnuler.setOnClickListener { parentFragmentManager.popBackStack() }

        return view
    }

    private fun ouvrirCarte() {
        val latStr = etLatitude.text.toString().trim()
        val lngStr = etLongitude.text.toString().trim()

        val lat = latStr.toDoubleOrNull()
        val lng = lngStr.toDoubleOrNull()

        if (lat == null || lng == null || lat !in -90.0..90.0 || lng !in -180.0..180.0) {
            Toast.makeText(requireContext(), R.string.msg_coordonnees_absentes, Toast.LENGTH_SHORT).show()
            return
        }

        val nomComplet = "${etPrenom.text} ${etNom.text}"
        val carteFragment = CarteContactFragment.newInstance(lat, lng, nomComplet)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, carteFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun lancerAppel() {
        val tel = etTelephone.text.toString().trim()
        if (tel.isEmpty()) {
            etTelephone.error = getString(R.string.err_champ_obligatoire)
            return
        }
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$tel"))
        try {
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), R.string.msg_aucune_app_telephone, Toast.LENGTH_SHORT).show()
        }
    }

    private fun lancerEmail() {
        val mail = etEmail.text.toString().trim()
        if (mail.isEmpty()) {
            etEmail.error = getString(R.string.err_champ_obligatoire)
            return
        }
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$mail"))
        try {
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), R.string.msg_aucune_app_email, Toast.LENGTH_SHORT).show()
        }
    }

    private fun enregistrerModifications() {
        val nom = etNom.text.toString().trim()
        val prenom = etPrenom.text.toString().trim()
        val telephone = etTelephone.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val latStr = etLatitude.text.toString().trim()
        val lngStr = etLongitude.text.toString().trim()

        var isValid = true

        if (nom.isEmpty()) {
            etNom.error = getString(R.string.err_champ_obligatoire)
            isValid = false
        }
        if (prenom.isEmpty()) {
            etPrenom.error = getString(R.string.err_champ_obligatoire)
            isValid = false
        }
        if (telephone.isEmpty()) {
            etTelephone.error = getString(R.string.err_champ_obligatoire)
            isValid = false
        }
        if (email.isEmpty()) {
            etEmail.error = getString(R.string.err_champ_obligatoire)
            isValid = false
        }

        val lat = if (latStr.isNotEmpty()) {
            val d = latStr.toDoubleOrNull()
            if (d == null || d !in -90.0..90.0) {
                etLatitude.error = getString(R.string.err_latitude_invalide)
                isValid = false
                null
            } else d
        } else null

        val lng = if (lngStr.isNotEmpty()) {
            val d = lngStr.toDoubleOrNull()
            if (d == null || d !in -180.0..180.0) {
                etLongitude.error = getString(R.string.err_longitude_invalide)
                isValid = false
                null
            } else d
        } else null

        if (!isValid) return

        val contactModifie = currentContact!!.copy(
            nom = nom,
            prenom = prenom,
            telephone = telephone,
            email = email,
            photoUri = currentPhotoUri,
            latitude = lat,
            longitude = lng
        )

        viewModel.updateContact(contactModifie)
        Toast.makeText(requireContext(), R.string.msg_contact_mis_a_jour, Toast.LENGTH_SHORT).show()
        parentFragmentManager.popBackStack()
    }

    private fun saveBitmapToInternalStorage(bitmap: Bitmap): Uri? {
        val filename = "contact_photo_${System.currentTimeMillis()}.jpg"
        val file = File(requireContext().cacheDir, filename)
        return try {
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
            stream.flush()
            stream.close()
            Uri.fromFile(file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}