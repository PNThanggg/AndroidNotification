package com.pnt.local_notifications.notification.helpers

import android.content.Context


object ResourcesHelper {
    fun getStringResourceByKey(context: Context, resourceKey: String): String {
        val resId = context.resources.getIdentifier(resourceKey, "string", context.packageName)
        return context.resources.getString(resId)
    }
}