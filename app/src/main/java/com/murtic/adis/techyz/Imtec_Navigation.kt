package com.murtic.adis.techyz

import android.app.Activity
import android.content.Intent
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class Imtec_Navigation constructor(val activity: Activity): NavigationView.OnNavigationItemSelectedListener {

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        // Sva navigacija stima se ovdje
        val id = item.itemId

        when(id) {
            R.id.imtec_home -> activity.webview.loadUrl(activity.getString(R.string.imtec_home_url))
            R.id.qr_code_scanner -> activity.startActivity(Intent(activity, Imtec_Scanner::class.java))
            R.id.imtec_akcije -> activity.webview.loadUrl(activity.getString(R.string.imtec_akcije_url))
            R.id.imtec_video_recenzije -> activity.webview.loadUrl(activity.getString(R.string.imtec_video_recenzije_url))
            R.id.imtec_poslovnice -> activity.webview.loadUrl(activity.getString(R.string.imtec_poslovnice_url))
            R.id.imtec_garancija -> activity.webview.loadUrl(activity.getString(R.string.imtec_garancija_url))
            R.id.imtec_servis -> activity.webview.loadUrl(activity.getString(R.string.imtec_servis_url))
            R.id.imtec_placanje -> activity.webview.loadUrl(activity.getString(R.string.imtec_placanje_url))
            R.id.imtec_narudzba -> activity.webview.loadUrl(activity.getString(R.string.imtec_narudzba_url))
            R.id.imtec_prijava -> activity.webview.loadUrl(activity.getString(R.string.imtec_prijava_url))
            R.id.imtec_korpa -> activity.webview.loadUrl(activity.getString(R.string.imtec_korpa_url))
            R.id.imtec_zamjena -> activity.webview.loadUrl(activity.getString(R.string.imtec_zamjena_url))
            R.id.imtec_reklamacija -> activity.webview.loadUrl(activity.getString(R.string.imtec_reklamacija_url))
            R.id.imtec_kontakti -> activity.webview.loadUrl(activity.getString(R.string.imtec_kontakti_url))
            R.id.imtec_postavke -> activity.startActivity(Intent(activity, Imtec_Settings::class.java))
        }

        activity.drawer.closeDrawer(GravityCompat.START)
        return true

    }

}
