package com.luismedinaweb

import com.google.gson.Gson
import java.awt.Desktop
import java.net.Socket
import java.net.URI
import java.net.URL
import java.nio.charset.StandardCharsets

/**
 * @author Luis
 */
class ServerThread(private val socket: Socket){

    companion object {
        private val gson: Gson = Gson()
    }

    fun run() {
        try {
            socket.use {
                // We'll take advantage of the fact that Url length probably won't be over 2100 and size this a bit above that
                val array = ByteArray(2400)
                socket.getInputStream().available()
                val readBytes = socket.getInputStream().buffered().read(array)
                val readString = array.copyOfRange(0, readBytes).toString(StandardCharsets.UTF_8)
                val packetReceived: LinkerPacket = gson.fromJson(readString, LinkerPacket::class.java)
                val result = processInput(packetReceived)
                socket.getOutputStream().write(gson.toJson(result).toByteArray())
            }
        } catch (ex: Exception) {
            println(ex.message)
        } finally {
            println("Socket closed: ${socket.isClosed}")
        }
    }

    private fun processInput(theInput: LinkerPacket): LinkerPacket? {
        return if (theInput.isValid) {
            when (theInput.type) {
                LinkerPacket.LINK -> handleLink(theInput)
                LinkerPacket.CLIENTHELLO -> handleClientHello()
                LinkerPacket.TERMINATE -> handleTerminate()
                else -> LinkerPacket(LinkerPacket.ACK, "Valid input, but no action taken.")
            }
        } else {
            LinkerPacket(LinkerPacket.NACK, "Invalid input.")
        }
    }

    private fun handleLink(packet: LinkerPacket): LinkerPacket {
        return packet.content?.let {
            try {
                displayInBrowser(packet.content!!)
                LinkerPacket(LinkerPacket.ACK, "Success")
            } catch (ex: Exception) {
                LinkerPacket(LinkerPacket.NACK, ex.message ?: "Error")
            }
        } ?: run {
            LinkerPacket(LinkerPacket.NACK, "Link not specified.")
        }
    }

    private fun handleClientHello(): LinkerPacket {
        return LinkerPacket(LinkerPacket.SERVERHELLO, "Success")
    }

    private fun handleTerminate(): LinkerPacket {
        return LinkerPacket(LinkerPacket.TERMINATE, "OK")
    }

    private fun displayInBrowser(searchURL: String) {
        val url = URL(searchURL)
        val uri = URI(url.protocol, url.host, url.path, url.query, null)
        Desktop.getDesktop().browse(uri)
    }
}