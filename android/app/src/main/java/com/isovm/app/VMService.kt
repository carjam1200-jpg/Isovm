package com.isovm.app

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import androidx.core.app.NotificationCompat
import java.io.File

class VMService : Service() {

    companion object {
        private const val ACTION_START = "com.isovm.app.action.START_VM"
        private const val ACTION_STOP = "com.isovm.app.action.STOP_VM"
        private const val EXTRA_ISO_PATH = "extra_iso_path"
        private const val NOTIF_CHANNEL = "isovm_channel"
        private const val NOTIF_ID = 1001

        fun createStartIntent(context: Context, isoPath: String): Intent {
            return Intent(context, VMService::class.java).apply {
                action = ACTION_START
                putExtra(EXTRA_ISO_PATH, isoPath)
            }
        }

        fun createStopIntent(context: Context): Intent {
            return Intent(context, VMService::class.java).apply { action = ACTION_STOP }
        }
    }

    private var qemuProcess: Process? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val isoPath = intent.getStringExtra(EXTRA_ISO_PATH) ?: return START_NOT_STICKY
                startForegroundServiceWithNotif()
                startQemu(isoPath)
            }
            ACTION_STOP -> {
                stopQemu()
                stopForeground(true)
                stopSelf()
            }
        }
        return START_STICKY
    }

    private fun startForegroundServiceWithNotif() {
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(NOTIF_CHANNEL, "Isovm VM", NotificationManager.IMPORTANCE_LOW)
            nm.createNotificationChannel(channel)
        }
        val notif: Notification = NotificationCompat.Builder(this, NOTIF_CHANNEL)
            .setContentTitle("Isovm")
            .setContentText("VM running")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .build()
        startForeground(NOTIF_ID, notif)
    }

    private fun startQemu(isoPath: String) {
        // qemu binary is expected at filesDir/qemu-system-x86_64 and be executable. You must provide a suitable aarch64 build.
        val qemuFile = File(filesDir, "qemu-system-x86_64")
        if (!qemuFile.exists()) {
            // Log and stop
            stopSelf()
            return
        }
        qemuFile.setExecutable(true)

        // Example QEMU arguments: emulate an x86_64 system with CD-ROM from imported ISO and VNC on :1 (5901)
        val args = listOf(
            qemuFile.absolutePath,
            "-m", "1024",
            "-cdrom", isoPath,
            "-boot", "d",
            "-enable-kvm", // will likely fail on Android but harmless to try
            "-vnc", "127.0.0.1:1",
            "-net", "nic",
            "-net", "user"
        )

        try {
            val pb = ProcessBuilder(args)
            pb.redirectErrorStream(true)
            qemuProcess = pb.start()

            // Simple watcher that stops service if the process exits
            Thread {
                try {
                    qemuProcess?.waitFor()
                } catch (e: InterruptedException) {
                }
                stopForeground(true)
                stopSelf()
            }.start()

        } catch (e: Exception) {
            e.printStackTrace()
            stopSelf()
        }
    }

    private fun stopQemu() {
        try {
            qemuProcess?.destroy()
            qemuProcess = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        stopQemu()
        super.onDestroy()
    }
}
