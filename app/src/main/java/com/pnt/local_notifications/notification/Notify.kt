package com.pnt.local_notifications.notification


import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.Resources.NotFoundException
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat
import com.pnt.local_notifications.notification.helpers.BitmapHelper.getBitmapFromRes
import com.pnt.local_notifications.notification.helpers.BitmapHelper.getBitmapFromUrl
import com.pnt.local_notifications.notification.helpers.BitmapHelper.toCircleBitmap
import com.pnt.local_notifications.notification.helpers.ResourcesHelper.getStringResourceByKey

@Suppress("DEPRECATION")
class Notify private constructor(private val context: Context) {
    enum class NotifyImportance {
        MIN,
        LOW,
        HIGH,
        MAX
    }

    interface ChannelData {
        companion object {
            const val ID = "notify_channel_id"
            const val NAME = "notify_channel_name"
            const val DESCRIPTION = "notify_channel_description"
        }
    }

    private val notificationBuilder: NotificationCompat.Builder
    private var channelId: String? = null
    private var channelName: String? = null
    private var channelDescription: String? = null
    private var title: CharSequence = ""
    private var content: CharSequence = ""
    private var id: Int
    private var smallIcon: Int
    private var oreoImportance = 0
    private var importance = 0
    private var color = -1
    private var largeIcon: Any
    private var picture: Any? = null
    private var action: Intent? = null
    private var vibrationPattern = longArrayOf(0, 250, 250, 250)
    private var autoCancel = false
    private var vibration = true
    private var circle = false

    init {
        val applicationInfo = context.applicationInfo

        id = System.currentTimeMillis().toInt()
        largeIcon = applicationInfo.icon
        smallIcon = applicationInfo.icon
        setDefaultPriority()
        channelId = try {
            getStringResourceByKey(context, ChannelData.ID)
        } catch (e: NotFoundException) {
            "NotifyAndroid"
        }
        channelName = try {
            getStringResourceByKey(context, ChannelData.NAME)
        } catch (e: NotFoundException) {
            "NotifyAndroidChannel"
        }
        channelDescription = try {
            getStringResourceByKey(context, ChannelData.DESCRIPTION)
        } catch (e: NotFoundException) {
            "Default notification android channel"
        }
        notificationBuilder = NotificationCompat.Builder(context, channelId!!)
    }

    fun show() {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationBuilder.setAutoCancel(autoCancel)
            .setDefaults(Notification.DEFAULT_SOUND)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(smallIcon)
            .setContentTitle(title)
            .setContentText(content)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))

        /*
         * Set large icon
         * */
        var largeIconBitmap: Bitmap?
        largeIconBitmap =
            if (largeIcon is String) getBitmapFromUrl(largeIcon.toString()) else getBitmapFromRes(
                context, largeIcon as Int
            )

        if (largeIconBitmap != null) {
            if (circle) largeIconBitmap = toCircleBitmap(largeIconBitmap)
            notificationBuilder.setLargeIcon(largeIconBitmap)
        }

        if (picture != null) {
            val pictureBitmap: Bitmap? =
                if (picture is String) getBitmapFromUrl(picture.toString()) else getBitmapFromRes(
                    context, picture as Int
                )
            if (pictureBitmap != null) {
                val bigPictureStyle = NotificationCompat.BigPictureStyle().bigPicture(pictureBitmap)
                    .setSummaryText(content)
                bigPictureStyle.bigLargeIcon(largeIconBitmap)
                notificationBuilder.setStyle(bigPictureStyle)
            }
        }

        val realColor: Int =
            if (VERSION.SDK_INT >= VERSION_CODES.M) if (color == -1) Color.BLACK else context.resources.getColor(
                color,
                null
            ) else if (color == -1) Color.BLACK else context.resources.getColor(color)

        notificationBuilder.setColor(realColor)

        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId, channelName, oreoImportance
            )
            notificationChannel.description = channelDescription
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = realColor
            notificationChannel.enableVibration(vibration)
            notificationChannel.vibrationPattern = vibrationPattern
            notificationManager.createNotificationChannel(notificationChannel)
        } else {
            notificationBuilder.setPriority(importance)
        }

        if (vibration) notificationBuilder.setVibrate(vibrationPattern) else notificationBuilder.setVibrate(
            longArrayOf(0)
        )


        if (action != null) {
            val pi: PendingIntent =
                if (VERSION.SDK_INT >= VERSION_CODES.M) PendingIntent.getActivity(
                    context, id,
                    action,
                    PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
                ) else PendingIntent.getActivity(
                    context, id,
                    action, PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            notificationBuilder.setContentIntent(pi)
        }

        notificationManager.notify(id, notificationBuilder.build())
    }

    fun setTitle(title: CharSequence): Notify {
        this.title = title
        return this
    }

    fun setContent(content: CharSequence): Notify {
        this.content = content
        return this
    }

    fun setChannelId(channelId: String): Notify {
        if (channelId.isNotEmpty()) {
            this.channelId = channelId
            notificationBuilder.setChannelId(channelId)
        }
        return this
    }

    fun setChannelName(channelName: String): Notify {
        if (channelName.isNotEmpty()) this.channelName = channelName
        return this
    }

    fun setChannelDescription(channelDescription: String): Notify {
        if (channelDescription.isNotEmpty()) this.channelDescription = channelDescription
        return this
    }

    fun setImportance(importance: NotifyImportance): Notify {
        if (VERSION.SDK_INT >= VERSION_CODES.N) {
            when (importance) {
                NotifyImportance.MIN -> {
                    this.importance = Notification.PRIORITY_LOW
                    oreoImportance = NotificationManager.IMPORTANCE_MIN
                }

                NotifyImportance.LOW -> {
                    this.importance = Notification.PRIORITY_LOW
                    oreoImportance = NotificationManager.IMPORTANCE_LOW
                }

                NotifyImportance.HIGH -> {
                    this.importance = Notification.PRIORITY_HIGH
                    oreoImportance = NotificationManager.IMPORTANCE_HIGH
                }

                NotifyImportance.MAX -> {
                    this.importance = Notification.PRIORITY_MAX
                    oreoImportance = NotificationManager.IMPORTANCE_MAX
                }
            }
        }
        return this
    }

    private fun setDefaultPriority() {
        importance = Notification.PRIORITY_DEFAULT
        if (VERSION.SDK_INT >= VERSION_CODES.N) oreoImportance =
            NotificationManager.IMPORTANCE_DEFAULT
    }

    fun enableVibration(vibration: Boolean): Notify {
        this.vibration = vibration
        return this
    }

    fun setAutoCancel(autoCancel: Boolean): Notify {
        this.autoCancel = autoCancel
        return this
    }

    fun largeCircularIcon(): Notify {
        circle = true
        return this
    }

    fun setVibrationPattern(vibrationPattern: LongArray): Notify {
        this.vibrationPattern = vibrationPattern
        return this
    }

    fun setColor(@ColorRes color: Int): Notify {
        this.color = color
        return this
    }

    fun setSmallIcon(@DrawableRes smallIcon: Int): Notify {
        this.smallIcon = smallIcon
        return this
    }

    fun setLargeIcon(@DrawableRes largeIcon: Int): Notify {
        this.largeIcon = largeIcon
        return this
    }

    fun setLargeIcon(largeIconUrl: String): Notify {
        largeIcon = largeIconUrl
        return this
    }

    fun setPicture(@DrawableRes pictureRes: Int): Notify {
        picture = pictureRes
        return this
    }

    fun setPicture(pictureUrl: String): Notify {
        picture = pictureUrl
        return this
    }

    fun setAction(action: Intent): Notify {
        this.action = action
        return this
    }

    fun setId(id: Int): Notify {
        this.id = id
        return this
    }

    companion object {
        fun build(context: Context): Notify {
            return Notify(context)
        }

        fun cancel(context: Context, id: Int) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(id)
        }

        fun cancelAll(context: Context) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancelAll()
        }
    }
}



