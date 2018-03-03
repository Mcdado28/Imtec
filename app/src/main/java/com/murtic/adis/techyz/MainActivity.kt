package com.murtic.adis.techyz

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Postavi toolbar
        setSupportActionBar(toolbar)

        // pokreni novu instancu ImtecApp
        Imtec_App(this)

    }

    override fun onBackPressed() {
        // Ako je navigation drawer otvoren zatvori ga
        if (drawer.isDrawerOpen(GravityCompat.START)) { drawer.closeDrawer(GravityCompat.START) }
        // Ako je navigation drawer zatvoren i ako u memoriji ima jos jedna stranica idi jednu stranicu nazad
        else if (!drawer.isDrawerOpen(GravityCompat.START) && webview.canGoBack()) { webview.goBack() }
        // Ako nista od navedenog nije istina napusti aplikaciju
        else { super.onBackPressed() }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchMenuItem = menu.findItem(R.id.searchbox)
        val searchView = menu.findItem(R.id.searchbox).actionView as android.support.v7.widget.SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.setOnQueryTextListener(Imtec_Search(this, searchView, searchMenuItem))

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        when(id) {
            R.id.imtec_menu_home -> webview.loadUrl(getString(R.string.imtec_home_url))
        }

        return super.onOptionsItemSelected(item)
    }

}
