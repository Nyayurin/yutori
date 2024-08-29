package cn.yurn.yutori.app

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import cn.yurn.yutori.Satori
import cn.yurn.yutori.channel
import cn.yurn.yutori.guild
import cn.yurn.yutori.message
import cn.yurn.yutori.toElements
import cn.yurn.yutori.user
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class SatoriService : Service() {
    private lateinit var notificationManager: NotificationManager
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)
    private var notifications = 0

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(
            NotificationChannel("foreground", "保活", NotificationManager.IMPORTANCE_HIGH)
        )
        notificationManager.createNotificationChannel(
            NotificationChannel("messaging", "消息通知", NotificationManager.IMPORTANCE_HIGH)
        )
        startForeground(1, NotificationCompat.Builder(this, "foreground").build())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        satori.client {
            listening {
                message.created {
                    if (event.user.id == event.self_id) return@created
                    if (ActivityCompat.checkSelfPermission(
                            applicationContext,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return@created
                    }
                    val pendingIntent: PendingIntent = PendingIntent.getActivity(
                        applicationContext,
                        0,
                        Intent().apply {
                            action = Intent.ACTION_VIEW
                            data = "yapp://chatting/${event.channel.id}".toUri()
                        },
                        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                    )
                    /*var remoteInput: RemoteInput = RemoteInput.Builder("reply")
                        .setLabel("回复")
                        .build()
                    val replyPendingIntent: PendingIntent = PendingIntent.getBroadcast(applicationContext,
                        event.id.toInt(),
                        getMessageReplyIntent(conversation.getConversationId()),
                        PendingIntent.FLAG_UPDATE_CURRENT)*/
                    val isPrivate = event.channel.id.startsWith("private:")
                    NotificationManagerCompat.from(applicationContext).notify(
                        notifications++,
                        NotificationCompat.Builder(applicationContext, "messaging")
                            .setSmallIcon(R.drawable.icon)
                            .setContentTitle(event.channel.name ?: if (isPrivate) event.user.nick
                                ?: event.user.name else event.guild?.name)
                            .setContentText(previewMessageContent(event.message.content.toElements(satori)))
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true)
                            .build()
                    )
                }
            }
        }
        scope.launch { satori.start() }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        lateinit var satori: Satori
    }
}