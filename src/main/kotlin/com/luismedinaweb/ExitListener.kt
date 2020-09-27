package com.luismedinaweb

import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.JOptionPane
import kotlin.system.exitProcess

class ExitListener : ActionListener {
    override fun actionPerformed(e: ActionEvent?) {
        if (JOptionPane.showConfirmDialog(null, "Are you sure you want to exit?") == JOptionPane.YES_OPTION) {
            exitProcess(0)
        }
    }
}