package com.murtic.adis.techyz

import android.app.Activity
import android.os.Build
import android.os.Handler
import android.preference.PreferenceManager
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.webkit.WebSettings
import android.widget.EditText
import android.widget.ImageView
import com.google.android.gms.analytics.HitBuilders
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.pushbots.push.Pushbots
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.util.*


class Imtec_App constructor(val activity: Activity) {

    // TAG koji koristimo u Log porukama u konzoli
    private val TAG: String = "IMTEC APP"
    // App Postavke
    private val postavke = PreferenceManager.getDefaultSharedPreferences(activity)

    private val imtecUserApi by lazy {
        Imtec_User_Api.create()
    }
    private var disposable: Disposable? = null

    // Pokrece sve nase funkcije pravilnim redosljedom
    init {
        // Provjeri postavke za notifikacije
        val ukljuciNotifikacije = postavke.getBoolean("enable_notifications", true)
        // Provjeri da li je app registrovan
        val registrovano = postavke.getBoolean("registrovano", false)

        startGoogleAnalytics()
        if(ukljuciNotifikacije) startPushBots()
        setupSwipeToRefresh()
        setupWebview()
        setupNavigation()
        handleIntent()
        setupFloatingActionMenu()
    }

    // Funkcija pokrece Google Analytics
    private fun startGoogleAnalytics() {

        Log.v(TAG, "Starting Google Analytics ...")
        // [START shared_tracker]
        // Obtain the shared Tracker instance.
        val application = activity.application as Imtec_Services
        val mTracker = application.defaultTracker

        mTracker?.send(HitBuilders.EventBuilder()
                .setCategory("Action")
                .setAction("Share")
                .build())

    }

    // Funkcija dodaje podrsku za pushbots notifikacije
    private fun startPushBots() {

        Log.v(TAG, "Starting pushbots notifications ...")
        Pushbots.sharedInstance().init(activity)

    }

    private fun setupSwipeToRefresh() {

        Log.v(TAG, "Setting up Swipe to refresh support ...")
        // Ukljuci podrsku za swipe to refresh
        activity.swipe_to_refresh.setOnRefreshListener({
            activity.swipe_to_refresh.isRefreshing = true
            Handler().postDelayed({
                activity.swipe_to_refresh.isRefreshing = false
                activity.webview.reload()
            }, 1000)
        })
    }

    // Funkcija ispravno konfigurise WebView komponentu
    private fun setupWebview() {

        Log.v(TAG, "WebView setup in progress ...")

        // ukljuci javascriptu
        activity.webview.settings.javaScriptEnabled = true
        // ukljuci nase znakove za slova
        activity.webview.settings.defaultTextEncodingName = "utf-8"
        // mogucnost zumiranja texta unutar activity.webview-a i sakrivanje zoom dugmadi
        activity.webview.settings.builtInZoomControls = true
        activity.webview.settings.displayZoomControls = false
        // mogucnost zumiranja dokumenata unutar WebView komponente
        activity.webview.settings.setSupportZoom(true)
        activity.webview.settings.loadWithOverviewMode = true
        activity.webview.settings.allowContentAccess = true
        activity.webview.settings.setSupportMultipleWindows(true)

        // WebView Postavke (neke mogu ubrzati ili usporiti ucitavanje stranica)
        // Ukljuci automatsko ucitavanje slika
        activity.webview.settings.loadsImagesAutomatically = true

        // Iskljuci geolokaciju
        activity.webview.settings.setGeolocationEnabled(false)

        // Iskljuci postavljanje pocetnog fokusa
        activity.webview.settings.setNeedInitialFocus(false)

        // Dozvoli pristup fajlovima unutar WebView komponente
        activity.webview.settings.allowFileAccess = true

        // Ubrzaj rad activity.webview-a
        activity.webview.settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
        activity.webview.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        activity.webview.settings.domStorageEnabled = true
        activity.webview.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NARROW_COLUMNS
        activity.webview.settings.useWideViewPort = true

        // Iskljuci sacuvljavanje podataka u web formama
        activity.webview.settings.saveFormData = false
        activity.webview.clearCache(true)
        activity.webview.clearHistory()

        activity.webview.settings.cacheMode = WebSettings.LOAD_NO_CACHE
        if (Build.VERSION.SDK_INT >= 15) {
            activity.webview.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        }
        else {
            activity.webview.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        }
        // Iskljuci App Cache za brze otvaranje stranica
        activity.webview.settings.setAppCacheEnabled(false)
        // Pokreni Web View Client
        activity.webview.setWebViewClient(Imtec_WebViewClient(activity))
        activity.webview.setWebChromeClient(Imtec_WebChromeClient())

        Log.v(TAG, "WebView setup finished OK ...")

    }

    private fun setupNavigation() {

        Log.v(TAG, "Setting up navigation drawer ...")

        // ustimaj toggle za navigation drawer
        val toggle = ActionBarDrawerToggle(
                activity, activity.drawer, activity.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        activity.drawer.addDrawerListener(toggle)
        toggle.syncState()

        // postavi navigation item selected listener
        activity.navigationView.setNavigationItemSelectedListener(Imtec_Navigation(activity))

    }

    // Funkcija koja provjerava intent u Main Activity
    private fun handleIntent() {

        Log.v(TAG, "Checking for bundle in intent ...")

        var pushUrl: String? = null

        if (activity.intent.extras != null) {
            try {

                Log.v(TAG, "Unpacking bundle from intent ...")
                // otpakuj bundle i provjeri sadrzaj kako bi znali o cemu se radi
                val b = activity.intent.extras
                // push notifikacija
                if (b.containsKey("message") && b.containsKey("url")) {
                    Log.v(TAG, "Found message in bundle -> Unpacking URL")
                    pushUrl = b.getString("url")
                }
                // url iz skenera
                if (b.containsKey("scanned")) {
                    Log.v(TAG, "Found scanned URL in bundle -> Unpacking URL")
                    pushUrl = b.getString("scanned")
                }

                // Uhvati greske
            } catch (e: Exception) {
                e.printStackTrace()
            }

            Log.v(TAG, "WebView loading URL from bundle ...")
            // Ucitaj URL koji smo izvukli iz bundlea unutar intenta
            activity.webview.loadUrl(pushUrl)
        }

        Log.v(TAG, "No bundle found in intent -> WebView loading pocetna page ...")
        // Ukoliko je bundle u intentu prazan ucitaj pocetnu stranicu u WebView komponentu

        if (pushUrl == null) activity.webview.loadUrl(activity.getString(R.string.imtec_home_url))

    }

    // Funkcija koja konfigurise floating action menu
    private fun setupFloatingActionMenu() {

        Log.v(TAG, "Setting up Floating action menu ...")

        // Postavi onClickListenere za dugmice
        // Podrska
        activity.fab_podrska.setOnClickListener {
            activity.webview.loadUrl(activity.getString(R.string.imtec_fab_podrska_url))
            activity.fab_menu.close(true)
        }

        // Aktuelni katalog
        activity.fab_katalog.setOnClickListener {
            activity.webview.loadUrl("http://docs.google.com/viewer?url=${activity.getString(R.string.imtec_fab_katalog_url)}")
            activity.fab_menu.close(true)
        }

        // Poslovnice
        activity.fab_poslovnice.setOnClickListener {
            activity.webview.loadUrl(activity.getString(R.string.imtec_fab_poslovnice))
            activity.fab_menu.close(true)
        }

        // Provjeri da li je app vec registrovan
        val registrovano = this.postavke.getBoolean("registrovano", false)
        if(registrovano) activity.fab_registracija.labelText = "Registrovano"

        // Registracija
        activity.fab_registracija.setOnClickListener {

            if(registrovano) {
                val name = this.postavke.getString("name", "")
                val telefon = this.postavke.getString("telefon", "")
                val email = this.postavke.getString("email", "")
                val barcode = this.postavke.getString("barcode", "")

                val user = Imtec_User(name, telefon, email, barcode)
                this.showRegistrationQRCode(user)

            } else {
                this.showRegistrationPopUp()
            }
            activity.fab_menu.close(true)
        }
    }

    private fun showRegistrationPopUp() {

        val alertDialog = AlertDialog.Builder(activity)
        alertDialog.setTitle("Registracija")
        alertDialog.setMessage(activity.getString(R.string.register_message))

        val inflater = activity.layoutInflater
        val view = inflater.inflate(R.layout.register_popup,null)

        alertDialog.setView(view)
        alertDialog.setIcon(R.drawable.ic_account_circle_black_24dp)

        alertDialog.setNegativeButton("Odustani") { dialogInterface, _ ->
            dialogInterface.dismiss()
        }

        alertDialog.setPositiveButton("Potvrdi") { dialogInterface, _ ->

            // Preuzmi vrijednosti iz polja
            val name = view.findViewById(R.id.et_register_nsurname) as EditText
            val email = view.findViewById(R.id.et_register_email) as EditText
            val phone = view.findViewById(R.id.et_register_phone) as EditText
            val barcode = generateRandom().toString()

            val user = Imtec_User(
                    name.text.toString(), // ime i prezime
                    phone.text.toString(), // telefon
                    email.text.toString(), // email
                    barcode// generisani code
            )

            disposable = imtecUserApi.addUser(user)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { result -> showResult(result) },
                            { error -> showError(error.message) }
                    )

            // Provjeri da li su podatci uneseni
            if(!TextUtils.isEmpty(name.text.toString()) && !TextUtils.isEmpty(email.text.toString())) {
                dialogInterface.dismiss()

                // Sacuvaj podatke u preferences kako bi kasnije bili dostupni
                postavke.edit().putString("name", name.text.toString()).apply()
                postavke.edit().putString("telefon", phone.text.toString()).apply()
                postavke.edit().putString("email", email.text.toString()).apply()
                postavke.edit().putString("barcode", barcode).apply()
                postavke.edit().putBoolean("registrovano", true).apply()

                // Generisi i prikazi QRCode
                showRegistrationQRCode(user)
            }
        }
        alertDialog.show()
    }

    private fun showRegistrationQRCode(user: Imtec_User) {

        val alertDialog = AlertDialog.Builder(activity)

        val inflater = activity.layoutInflater
        val view = inflater.inflate(R.layout.qrcode_popup,null)

        alertDialog.setView(view)

        val multiFormatWriter = MultiFormatWriter()
        try {
            val bitMatrix = multiFormatWriter.encode(user.barcode + generateChecksumDigit(user.barcode), BarcodeFormat.EAN_13, 200, 200)
            val barcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.createBitmap(bitMatrix)
            val qrCodePreview = view.findViewById(R.id.qrcodePreview) as ImageView
            qrCodePreview.setImageBitmap(bitmap)
        } catch (e: WriterException) {
            e.printStackTrace()
        }

        alertDialog.setNegativeButton("Zatvori") { dialogInterface, _ ->
            dialogInterface.dismiss()
        }

        alertDialog.show()

    }

    private fun generateRandom(): Long {
        val random = Random()
        val sb = StringBuilder()

        // first not 0 digit
        sb.append(random.nextInt(9) + 1)

        // rest of 11 digits
        for (i in 0..10) {
            sb.append(random.nextInt(10))
        }

        return java.lang.Long.valueOf(sb.toString())!!.toLong()
    }

    private fun generateChecksumDigit(code: String): Int {
        var total_sum = 0
        for (i in 0 until code.length) {
            if (i % 2 != 0) {
                total_sum = total_sum + Integer.parseInt("" + code[i]) * 3
            } else {
                total_sum = total_sum + Integer.parseInt("" + code[i])
            }
        }
        return 10 - total_sum % 10
    }

    private fun showResult(result: String?) {

        Log.v("Rezultat", result)

    }

    private fun showError(message: String?) {
        Log.e("GRESKA", message)
    }

}
