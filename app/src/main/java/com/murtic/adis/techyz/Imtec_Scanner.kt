package com.murtic.adis.techyz

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.AttributeSet
import android.util.TypedValue
import com.google.zxing.Result
import me.dm7.barcodescanner.core.IViewFinder
import me.dm7.barcodescanner.core.ViewFinderView
import me.dm7.barcodescanner.zxing.ZXingScannerView
import java.net.URI
import java.net.URISyntaxException


class Imtec_Scanner : Activity(), ZXingScannerView.ResultHandler {
    private var mScannerView: ZXingScannerView? = null

    public override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            askForCameraPermissions()
        }

        mScannerView = object : ZXingScannerView(this) {
            override fun createViewFinderView(context: Context): IViewFinder {
                return CustomViewFinderView(context)
            }
        }
        setContentView(mScannerView)                // Set the scanner view as the content view
    }

    private val REQUEST_CODE_ASK_PERMISSIONS = 123

    @TargetApi(Build.VERSION_CODES.M)
    private fun askForCameraPermissions() {

        val hasWriteCameraPermission = checkSelfPermission(Manifest.permission.CAMERA)
        if (hasWriteCameraPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CODE_ASK_PERMISSIONS)
        }
    }


    private class CustomViewFinderView : ViewFinderView {
        val PAINT = Paint()

        constructor(context: Context) : super(context) {
            init()
        }

        constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
            init()
        }

        private fun init() {
            PAINT.color = Color.WHITE
            PAINT.isAntiAlias = true
            val textPixelSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                    TRADE_MARK_TEXT_SIZE_SP.toFloat(), resources.displayMetrics)
            PAINT.textSize = textPixelSize
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            drawTradeMark(canvas)
        }

        private fun drawTradeMark(canvas: Canvas) {
            val framingRect = framingRect
            val tradeMarkTop: Float
            val tradeMarkLeft: Float
            if (framingRect != null) {
                tradeMarkTop = framingRect.top + PAINT.textSize - 90
                tradeMarkLeft = (framingRect.left - 5).toFloat()


            } else {
                tradeMarkTop = 10f
                tradeMarkLeft = canvas.height - PAINT.textSize - 10
            }
            canvas.drawText(TRADE_MARK_TEXT, tradeMarkLeft, tradeMarkTop, PAINT)
        }

        companion object {
            val TRADE_MARK_TEXT = "Postavi kod u centar kvadrata i skeniraj."
            val TRADE_MARK_TEXT_SIZE_SP = 15
        }
    }

    public override fun onResume() {
        super.onResume()
        mScannerView?.setResultHandler(this) // Register ourselves as a handler for scan results.
        mScannerView?.startCamera()         // Start camera on resume
    }

    public override fun onPause() {
        super.onPause()
        mScannerView?.stopCamera()           // Stop camera on pause
    }

    override fun handleResult(rawResult: Result) {
        // Do something with the result here

        val buildAlert = AlertDialog.Builder(this)
        buildAlert.setTitle(getString(R.string.imtec_scanner_rezultat_title))
        buildAlert.setMessage(rawResult.text + "\n" + rawResult.barcodeFormat.toString())
        buildAlert.setNegativeButton(getString(R.string.imtec_scanner_rezultat_odustani), { dialogInterface, _ ->
            dialogInterface.cancel()
            // If you would like to resume scanning, call this method below:
            mScannerView?.resumeCameraPreview(this)
        })


        buildAlert.setNeutralButton(getString(R.string.imtec_scanner_rezultat_kopiraj), { dialogInterface, _ ->
            // Gets a handle to the clipboard service.
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

            // Creates a new text clip to put on the clipboard
            val clip = ClipData.newPlainText("scanned_text", rawResult.text)

            // Set the clipboard's primary clip.
            clipboard.primaryClip = clip

            dialogInterface.cancel()

            // If you would like to resume scanning, call this method below:
            mScannerView?.resumeCameraPreview(this)
        })


        buildAlert.setPositiveButton(getString(R.string.imtec_scanner_posjeti_url), { _, _ ->
            val goToScanned = Intent(this@Imtec_Scanner, MainActivity::class.java)
            goToScanned.putExtra("scanned", rawResult.text)
            startActivity(goToScanned)
        })

        val alertDialog = buildAlert.create()
        alertDialog.show()

        // Provjeri da li je skenirani tekst zapravo URL ako jeste uljuci Posjeti URL dugmic
        // Ako nije url iskljucit ce dugmic
        val posjetiURL = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
        try {
            URI(rawResult.text)
            posjetiURL.isEnabled = true
        } catch (e: URISyntaxException) {
            e.printStackTrace()
            posjetiURL.isEnabled = false
        }
    }
}