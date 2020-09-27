package com.luismedinaweb

import java.awt.SystemTray
import java.awt.TrayIcon
import java.awt.event.ActionEvent
import java.awt.event.ActionListener

class HideListener(private val tray: SystemTray, private val trayIcon: TrayIcon) : ActionListener {
    override fun actionPerformed(e: ActionEvent?) {
        try {
            tray.remove(trayIcon)
        } catch (ex: Exception) {
        }
    }
}