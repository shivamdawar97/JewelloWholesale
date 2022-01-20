package com.s3enterprises.jewellowholesale.print

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.s3enterprises.jewellowholesale.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.Charset
import java.util.*
import kotlin.experimental.or

class JewelloBluetoothSocket {

    //fun isConnected() = if (socket != null) socket!!.isConnected else false

    private var bluetoothDevice: BluetoothDevice? = null
    private var outputStream: OutputStream? = null
    private var inputStream: InputStream? = null
    private var readBuffer: ByteArray? = null
    private var readBufferPosition: Int = 0

    @Volatile
    private var stopWorker: Boolean = false

    private var socket: BluetoothSocket? = null
    private val defaultPrintFormat = byteArrayOf(27, 33, 0)
    private val bigPrintFormat = byteArrayOf(27, 33, 0).apply {
        this[2] = this[2]
            .or(0x8) // bold
            .or(0x10) // height
            .or(0x20) // width
    }
    private val boldPrintFormat = byteArrayOf(27, 33, 0).apply {
        this[2] = this[2]
            .or(0x12) // bold
    }

    suspend fun findDeviceAndConnect(context: Context) {
        if (Utils.printerName == "") return
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        try {
            if (!bluetoothAdapter.isEnabled) {
                val enableBluetooth = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                (context as Activity).startActivityForResult(enableBluetooth, 0)
                return
            }
            val pairedDevices = bluetoothAdapter.bondedDevices
            if (pairedDevices.size > 0) startConnection(pairedDevices, bluetoothAdapter)
            else Toast.makeText(context, "No Devices found", Toast.LENGTH_LONG).show()

        } catch (ex: Exception) {
            ex.printStackTrace()
        }

    }

    private suspend fun startConnection(
        pairedDevices: MutableSet<BluetoothDevice>,
        bluetoothAdapter: BluetoothAdapter
    ) {
        for (device in pairedDevices) if (device.name == Utils.printerName) {
            bluetoothDevice = device
            break
        }
        val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") //Standard SerialPortService ID
        if (socket == null) socket = withContext(Dispatchers.Default){
            bluetoothDevice!!.createRfcommSocketToServiceRecord(uuid)
        }

        bluetoothAdapter.cancelDiscovery()
        withContext(Dispatchers.Default){
            socket?.connect()
        }

        outputStream = socket!!.outputStream
        inputStream = socket!!.inputStream
        beginListenForData()
    }

    private fun beginListenForData() = try {
        val handler = Handler(Looper.getMainLooper())

        // this is the ASCII code for a newline character
        val delimiter: Byte = 10

        stopWorker = false
        readBufferPosition = 0
        readBuffer = ByteArray(1024)

        val workerThread = Thread {
            while (!Thread.currentThread().isInterrupted && !stopWorker)
                try {
                    val bytesAvailable = inputStream!!.available()
                    if (bytesAvailable > 0) {
                        val packetBytes = ByteArray(bytesAvailable)
                        inputStream!!.read(packetBytes)
                        for (i in 0 until bytesAvailable) {
                            val b = packetBytes[i]
                            if (b == delimiter) {
                                val encodedBytes = ByteArray(readBufferPosition)
                                System.arraycopy(
                                    readBuffer!!,
                                    0,
                                    encodedBytes,
                                    0,
                                    encodedBytes.size
                                )

                                // specify US-ASCII encoding
                                val data = String(encodedBytes, Charset.forName("US-ASCII"))
                                readBufferPosition = 0

                                // tell the user data were sent to bluetooth printer device
                                handler.post { Log.d("Bluetooth_Error", data) }

                            } else readBuffer!![readBufferPosition++] = b

                        }
                    }

                } catch (ex: IOException) {
                    stopWorker = true
                }
        }
        workerThread.start()
    } catch (e: Exception) {
        e.printStackTrace()
    }

    fun printData(text: String) = try {
        val buffer = text.toByteArray()
        outputStream?.let {
            it.write(defaultPrintFormat)
            it.write(buffer, 0, buffer.size)
        }
    } catch (ex: Exception) {
        ex.printStackTrace()
    }

    fun printBoldData(text: String) = try {
        val buffer = text.toByteArray()
        outputStream?.let {
            it.write(boldPrintFormat)
            it.write(buffer, 0, buffer.size)
        }
    } catch (ex: Exception) {
        ex.printStackTrace()
    }

    suspend fun disconnectBT() {
        try {
            delay(1000)
            stopWorker = true
            outputStream!!.close()
            inputStream!!.close()
            socket!!.close()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

    }

    fun printCustomData(bold: Int, width: Int, height: Int, text: String) = try {
        val customPrintFormat = byteArrayOf(27, 33, 0).apply {
            this[2] = this[2]
                .or(bold.toByte()) // bold
                .or(width.toByte()) // height
                .or(height.toByte()) // width
        }

        val buffer = text.toByteArray()
        outputStream?.let {
            it.write(customPrintFormat)
            it.write(buffer, 0, buffer.size)
        }
    } catch (ex: Exception) {
        ex.printStackTrace()
    }

}