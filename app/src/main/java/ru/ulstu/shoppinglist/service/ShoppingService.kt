package ru.ulstu.shoppinglist.service

import android.app.*
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import ru.ulstu.shoppinglist.MainActivity
import ru.ulstu.shoppinglist.R
import ru.ulstu.shoppinglist.domain.model.ShoppingItem
import ru.ulstu.shoppinglist.domain.repository.ShoppingRepository
import javax.inject.Inject

@AndroidEntryPoint
class ShoppingService : Service() {

    @Inject
    lateinit var repository: ShoppingRepository

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var job: Job? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == "ACTION_STOP") {
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
            return START_NOT_STICKY
        }

        startForegroundService()
        return START_STICKY
    }

    private fun startForegroundService() {
        job?.cancel()
        job = serviceScope.launch {
            repository.getItems().collect { items ->
                val nextItem = items.firstOrNull { !it.isCrossedOut }
                updateNotification(nextItem)
            }
        }
    }

    private fun updateNotification(item: ShoppingItem?) {
        val contentText = item?.name ?: getString(R.string.all_items_done)
        
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.shopping_list))
            .setContentText(contentText)
            .setSmallIcon(android.R.drawable.ic_menu_agenda)
            .setOngoing(true)
            .setContentIntent(
                PendingIntent.getActivity(
                    this, 0, Intent(this, MainActivity::class.java),
                    PendingIntent.FLAG_IMMUTABLE
                )
            )

        if (item != null) {
            val crossOutIntent = Intent(this, NotificationReceiver::class.java).apply {
                action = "ACTION_CROSS_OUT"
                putExtra("ITEM_ID", item.id)
            }
            val crossOutPendingIntent = PendingIntent.getBroadcast(
                this, item.id.toInt(), crossOutIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            notificationBuilder.addAction(
                android.R.drawable.ic_menu_edit,
                getString(R.string.cross_out),
                crossOutPendingIntent
            )
        }

        notificationBuilder.addAction(
            android.R.drawable.ic_menu_close_clear_cancel,
            getString(R.string.stop_shopping),
            PendingIntent.getService(
                this, 100, Intent(this, ShoppingService::class.java).apply { action = "ACTION_STOP" },
                PendingIntent.FLAG_IMMUTABLE
            )
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(NOTIFICATION_ID, notificationBuilder.build(), ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            startForeground(NOTIFICATION_ID, notificationBuilder.build())
        }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Shopping Mode",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    companion object {
        const val CHANNEL_ID = "shopping_channel"
        const val NOTIFICATION_ID = 1
    }
}
