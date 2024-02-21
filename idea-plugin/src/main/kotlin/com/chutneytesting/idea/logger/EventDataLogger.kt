package com.chutneytesting.idea.logger

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationListener
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project

object EventDataLogger {
    private const val CHUTNEY_NOTIFICATIONS = "Chutney Notifications"

    fun logError(htmlMessage: String, project: Project, notificationListener: NotificationListener? = null) {
        val notif = NotificationGroupManager.getInstance()
            .getNotificationGroup(CHUTNEY_NOTIFICATIONS)
            .createNotification("Chutney error", htmlMessage, NotificationType.ERROR)

        notificationListener?.let { notif.setListener(it) }

        notif.notify(project)
    }

    fun logWarning(htmlMessage: String, project: Project, notificationListener: NotificationListener? = null) {
        val notif = NotificationGroupManager.getInstance()
            .getNotificationGroup(CHUTNEY_NOTIFICATIONS)
            .createNotification("Chutney warning", htmlMessage, NotificationType.WARNING)

        notificationListener?.let { notif.setListener(it) }

        notif.notify(project)
    }

    fun logInfo(htmlMessage: String, project: Project, notificationListener: NotificationListener? = null) {
        val notif = NotificationGroupManager.getInstance()
            .getNotificationGroup(CHUTNEY_NOTIFICATIONS)
            .createNotification("Chutney info", htmlMessage, NotificationType.INFORMATION)

        notificationListener?.let { notif.setListener(it) }

        notif.notify(project)
    }
}
