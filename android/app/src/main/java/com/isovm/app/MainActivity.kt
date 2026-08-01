package com.isovm.app

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.InputStream

class MainActivity : AppCompatActivity() {

    private lateinit var tvStatus: TextView
    private lateinit var btnImportIso: Button
    private lateinit var btnStartVm: Button
    private lateinit var btnStopVm: Button
    private lateinit var vncContainer: FrameLayout

    private var isoFile: File? = null

    private val importIsoLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        uri?.let { copyIsoToAppStorage(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvStatus = findViewById(R.id.tvStatus)
        btnImportIso = findViewById(R.id.btnImportIso)
        btnStartVm = findViewById(R.id.btnStartVm)
        btnStopVm = findViewById(R.id.btnStopVm)
        vncContainer = findViewById(R.id.vncContainer)

        btnImportIso.setOnClickListener {
            importIsoLauncher.launch(arrayOf("*/*"))
        }

        btnStartVm.setOnClickListener {
            isoFile?.let {
                val intent = VMService.createStartIntent(this, it.absolutePath)
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    startForegroundService(intent)
                } else {
                    startService(intent)
                }
                tvStatus.text = "VM starting..."
            } ?: run {
                tvStatus.text = "Please import an ISO first"
            }
        }

        btnStopVm.setOnClickListener {
            val intent = VMService.createStopIntent(this)
            startService(intent)
            tvStatus.text = "VM stopping..."
        }

        // TODO: Add a VNC client view and attach it to vncContainer
    }

    private fun copyIsoToAppStorage(uri: Uri) {
        try {
            val input: InputStream? = contentResolver.openInputStream(uri)
            val outFile = File(filesDir, "imported.iso")
            input?.use { inputStream ->
                outFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            isoFile = outFile
            tvStatus.text = "Imported ISO: ${outFile.name}"
        } catch (e: Exception) {
            e.printStackTrace()
            tvStatus.text = "Failed to import ISO: ${e.message}"
        }
    }
}
