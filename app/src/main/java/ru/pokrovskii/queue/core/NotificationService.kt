package ru.pokrovskii.queue.core

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.pokrovskii.queue.R
import ru.pokrovskii.queue.data.api.QueueApi
import ru.pokrovskii.queue.data.dataBase.UserDAO
import ru.pokrovskii.queue.ui.MainActivity
import javax.inject.Inject
import kotlin.coroutines.EmptyCoroutineContext

@AndroidEntryPoint
class NotificationService : Service() {
    @Inject
    lateinit var userDAO: UserDAO

    @Inject
    lateinit var queueApi: QueueApi

    @Inject
    lateinit var auth: FirebaseAuth

    private val notificationChannelId = "Your_Channel_ID"
    private val notificationId = 1

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        val coroutineScope = CoroutineScope(EmptyCoroutineContext)
        coroutineScope.launch {
            val currUser = userDAO.getCurrUser()
            for (queue in currUser!!.queues) {
                queueApi.startListeningQueue(queue.id) { queue ->
                    if (queue != null && queue.users.size >= 2 && auth.currentUser!!.uid == queue.users[1].id) {
                        createNotification(queue.name)
                    }
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        val coroutineScope = CoroutineScope(EmptyCoroutineContext)
        coroutineScope.launch {
            val currUser = userDAO.getCurrUser()
            for (queue in currUser!!.queues) {
                queueApi.endListeningQueue(queue.id)
            }
        }
    }

    private fun createNotification(nameOfQueue: String) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                notificationChannelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, notificationChannelId)
            .setSmallIcon(R.drawable.ic_people).setContentTitle(nameOfQueue)
            .setContentText(applicationContext.getString(R.string.you_are_the_next))
            .setAutoCancel(true)
            .setContentIntent(createAppOpenIntent(context = this))

        notificationManager.notify(
            notificationId, notification.build()
        )
    }

    private fun createAppOpenIntent(context: Context): PendingIntent {
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

}
