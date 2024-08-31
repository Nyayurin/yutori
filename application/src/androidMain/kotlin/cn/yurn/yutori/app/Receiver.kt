package cn.yurn.yutori.app

import android.app.NotificationManager
import android.app.Service.NOTIFICATION_SERVICE
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.RemoteInput
import kotlinx.coroutines.runBlocking

class NotificationReplyReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val remoteInput = RemoteInput.getResultsFromIntent(intent!!)

        if (remoteInput != null) {
            runBlocking {
                val text = remoteInput.getCharSequence("reply").toString()
                val notificationId = intent.getIntExtra("notification_id", 0)
                val channelId = intent.getStringExtra("channel_id")
                viewModel.actions!!.message.create(
                    channel_id = channelId!!,
                    content = { text { text } }
                )
                val notificationManager =
                    context!!.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancel(notificationId)
            }
        }
    }
}