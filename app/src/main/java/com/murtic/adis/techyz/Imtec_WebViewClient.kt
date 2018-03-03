package com.murtic.adis.techyz

import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.MailTo
import android.net.Uri
import android.provider.Settings
import android.support.v7.app.AlertDialog
import android.view.View
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.page_loader.*


class Imtec_WebViewClient constructor(val activity: Activity): WebViewClient() {

    @Suppress("OverridingDeprecatedMember")
    override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {

        // Handle the error
        if (errorCode == -2) view.loadData("", "", null)

        val builder = AlertDialog.Builder(activity)

        builder.setCancelable(false)
        builder.setTitle(activity.getString(R.string.imtec_alert_nc_title))
        builder.setMessage(activity.getString(R.string.imtec_alert_nc_text))
        builder.setPositiveButton(activity.getString(R.string.imtec_alert_nc_ok), { _, _ ->
            val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
            activity.startActivity(intent)
        })

        builder.setNegativeButton(activity.getString(R.string.imtec_alert_nc_cancel), { _, _ ->
            view.loadUrl(activity.getString(R.string.imtec_home_url))
        })

        val alert = builder.create()
        alert.show()
    }

    @TargetApi(android.os.Build.VERSION_CODES.M)
    override fun onReceivedError(view: WebView, req: WebResourceRequest, rerr: WebResourceError) {
        // Redirect to deprecated method, so you can use it in all SDK versions
        onReceivedError(view, rerr.errorCode, rerr.description.toString(), req.url.toString())
    }

    override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
        // prikazi loader dok se stranica ucitava
        activity.imtec_loader_bg.visibility = View.VISIBLE
        // sakrij WebView dok se stanica ucitava
        view.visibility = View.GONE
    }

    override fun onPageFinished(view: WebView, url: String) {
        // sakrij loader kada se stranica ucita u WebView
        activity.imtec_loader_bg.visibility = View.VISIBLE
        // prikazi WebView kada je stranica ucitana i spremna
        view.visibility = View.VISIBLE
    }

    @Suppress("OverridingDeprecatedMember")
    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {

        // Mogucnost poziva iz Webview-a
        if (url.startsWith("tel:")) {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse(url))
            activity.startActivity(intent)
            return true
            // Mogucnost slanja emaila iz Webview-a
        } else if (url.startsWith("mailto:")) {
            val mailTo = MailTo.parse(url)
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(mailTo.to))
            intent.putExtra(Intent.EXTRA_TEXT, mailTo.body)
            intent.putExtra(Intent.EXTRA_SUBJECT, mailTo.subject)
            intent.putExtra(Intent.EXTRA_CC, mailTo.cc)
            intent.type = "message/rfc822"
            activity.startActivity(intent)
            return true
        } else {
            return false
        }
    }

    // Ukloni suvisne elementa iz HTML strukture koristeci javascript
    override fun onLoadResource(view: WebView, url: String) {
        super.onLoadResource(view, url)

        view.loadUrl("javascript:" +
                "var sakrij_id=['chat_table','header_logo','top-dugme','PM_ASBlockOutput_19','authenticationAlign','header_search'];" +
                "for (i = 0; i < sakrij_id.length; i++){" +
                    "try{" +
                        "document.getElementById(sakrij_id[i]).style.display = 'none';" +
                    "}catch(e){}" +
                "} " +
                "var sakrij_class=['pt-spotlight1 clearfix','header-center','footer-container','header-top','header_quickmenu','backtotop','pt-spotlight10 clearfix','slideshow_container','header_bottom','breadcrumb clearfix'];" +
                "for (i = 0; i < sakrij_class.length; i++){" +
                    "try{" +
                        "document.getElementsByClassName(sakrij_class[i])[0].style.display = 'none';" +
                    "}catch(e){}" +
                "} void 0")

    }

}