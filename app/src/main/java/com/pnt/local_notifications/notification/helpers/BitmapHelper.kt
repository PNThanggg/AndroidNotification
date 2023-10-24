package com.pnt.local_notifications.notification.helpers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.os.StrictMode
import java.io.IOException
import java.net.URL


object BitmapHelper {
    fun getBitmapFromUrl(url: String): Bitmap? {
        val policy: StrictMode.ThreadPolicy =
            StrictMode.ThreadPolicy.Builder().permitNetwork().build()
        StrictMode.setThreadPolicy(policy)

        try {
            return BitmapFactory.decodeStream(URL(url).openConnection().getInputStream())
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }

    fun getBitmapFromRes(context: Context, res: Int): Bitmap? {
        return BitmapFactory.decodeResource(context.resources, res)
    }

    fun toCircleBitmap(bitmap: Bitmap): Bitmap {
        val dstBimap = if (bitmap.width > bitmap.height) {
            Bitmap.createBitmap(
                bitmap,
                (bitmap.width - bitmap.height) / 2,
                0,
                bitmap.height,
                bitmap.height
            )
        } else {
            Bitmap.createBitmap(
                bitmap,
                0,
                (bitmap.height - bitmap.width) / 2,
                bitmap.width,
                bitmap.width
            )
        }

        val output: Bitmap = Bitmap.createBitmap(
            dstBimap.width,
            dstBimap.height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(output)

        val color = Color.BLACK
        val paint = Paint()
        val rect = Rect(0, 0, dstBimap.width, dstBimap.height)
        val rectF = RectF(rect)

        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = color
        canvas.drawOval(rectF, paint)

        paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC_IN))
        canvas.drawBitmap(dstBimap, rect, rect, paint)

        return output
    }
}