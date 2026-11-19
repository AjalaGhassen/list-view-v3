package com.example.listviewdecontacts

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.fragment.app.Fragment

class CarteContactFragment : Fragment() {

    private var latitude: Double? = null
    private var longitude: Double? = null
    private var nomContact: String = ""

    private lateinit var wvCarte: WebView
    private lateinit var tvErreurCarte: TextView

    companion object {
        private const val ARG_LAT = "lat"
        private const val ARG_LNG = "lng"
        private const val ARG_NOM = "nom"

        fun newInstance(lat: Double?, lng: Double?, nomContact: String): CarteContactFragment {
            val fragment = CarteContactFragment()
            val args = Bundle()
            if (lat != null) args.putDouble(ARG_LAT, lat)
            if (lng != null) args.putDouble(ARG_LNG, lng)
            args.putString(ARG_NOM, nomContact)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = arguments
        if (args != null && args.containsKey(ARG_LAT) && args.containsKey(ARG_LNG)) {
            latitude = args.getDouble(ARG_LAT)
            longitude = args.getDouble(ARG_LNG)
        }
        nomContact = args?.getString(ARG_NOM, "") ?: ""
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_carte_contact, container, false)

        wvCarte = view.findViewById(R.id.wvCarte)
        tvErreurCarte = view.findViewById(R.id.tvErreurCarte)

        val lat = latitude
        val lng = longitude

        if (lat == null || lng == null || lat !in -90.0..90.0 || lng !in -180.0..180.0) {
            tvErreurCarte.visibility = View.VISIBLE
            wvCarte.visibility = View.GONE
        } else {
            tvErreurCarte.visibility = View.GONE
            wvCarte.visibility = View.VISIBLE

            wvCarte.settings.javaScriptEnabled = true
            wvCarte.webViewClient = WebViewClient()

            val htmlContent = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"/>
                    <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
                    <style>
                        html, body, #map { height: 100%; margin: 0; padding: 0; }
                    </style>
                </head>
                <body>
                    <div id="map"></div>
                    <script>
                        var map = L.map('map').setView([$lat, $lng], 13);
                        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                            maxZoom: 19,
                            attribution: '© OpenStreetMap'
                        }).addTo(map);
                        var marker = L.marker([$lat, $lng]).addTo(map);
                        marker.bindPopup("<b>${nomContact.replace("\"", "\\\"")}</b>").openPopup();
                    </script>
                </body>
                </html>
            """.trimIndent()

            wvCarte.loadDataWithBaseURL("https://openstreetmap.org", htmlContent, "text/html", "UTF-8", null)
        }

        return view
    }
}