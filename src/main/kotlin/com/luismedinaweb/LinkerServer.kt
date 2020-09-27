package com.luismedinaweb

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.awt.MenuItem
import java.awt.PopupMenu
import java.awt.SystemTray
import java.awt.TrayIcon
import java.io.File
import java.net.ServerSocket
import javax.imageio.ImageIO
import javax.swing.UIManager

/**
 *
 * @author Luis
 */
fun main(){
    LinkerServer.start()
}


object LinkerServer {

    private var portNumber = LinkerProtocol.portStart

    fun start() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
        } catch (ex: Exception) {
        }
        var started = false
        while (!started && portNumber < LinkerProtocol.portEnd) {
            try {
                ServerSocket(portNumber).use { serverSocket ->
                    initTray()
                    started = true
                    while (true) {
                        val socket = serverSocket.accept()
                        CoroutineScope(Dispatchers.IO).launch {
                            ServerThread(socket).run()
                        }
                    }
                }
            } catch (ex: Exception) {
                println("Could not listen on port $portNumber")
                portNumber++
            }
        }
    }

    private fun initTray() {
        try {
            val tray = SystemTray.getSystemTray()
            val exitListener = ExitListener()
            val image = ImageIO.read(ClassLoader.getSystemResource("Linker.png"))
            val trayIcon = TrayIcon(image, "Linker Server - Listening on port $portNumber")
            val hideIconListener = HideListener(tray, trayIcon)
            val popup = PopupMenu()
            val showItem = MenuItem("Hide icon")
            showItem.addActionListener(hideIconListener)
            popup.add(showItem)
            val exitItem = MenuItem("Exit")
            exitItem.addActionListener(exitListener)
            popup.add(exitItem)
            trayIcon.popupMenu = popup
            trayIcon.isImageAutoSize = true
            tray.add(trayIcon)
            trayIcon.displayMessage(
                    "Linker Server is running",
                    "Listening on port $portNumber",
                    TrayIcon.MessageType.INFO
            )
        } catch (ex: Exception) {
            println(ex.message)
        }
    }
}