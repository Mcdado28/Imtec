package com.murtic.adis.techyz

import android.webkit.WebChromeClient
import android.webkit.WebView

class Imtec_WebChromeClient constructor(): WebChromeClient() {
    override fun onProgressChanged(view: WebView, newProgress: Int) {
        super.onProgressChanged(view, newProgress)

        //progressBar.setProgress(newProgress)

    }
}
