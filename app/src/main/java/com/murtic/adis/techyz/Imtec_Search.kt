package com.murtic.adis.techyz

import android.app.Activity
import android.support.v7.widget.SearchView
import android.view.MenuItem
import kotlinx.android.synthetic.main.content_main.*

class Imtec_Search constructor(val activity: Activity, val searchView: SearchView, val searchMenuItem: MenuItem): SearchView.OnQueryTextListener {

    // Sta se desava kada korisnik unese pojam za pretragu i klikne search
    override fun onQueryTextSubmit(query: String?): Boolean {
        // kreiraj link za pretragu i otvori u webview
        activity.webview.loadUrl("http://imtec.ba/search?search_query=$query&controller=search&orderby=position&orderway=desc")
        // close search view if its visible
        if (searchView.isShown) {
            searchMenuItem.collapseActionView()
            searchView.setQuery("", false)
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return false
    }

}
