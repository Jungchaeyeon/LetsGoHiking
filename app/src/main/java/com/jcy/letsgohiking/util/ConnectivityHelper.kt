package com.jcy.letsgohiking.util

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Handler
import android.telephony.TelephonyManager
import java.util.*

/**
 * Helper class to check connectivity of the device.
 */
class ConnectivityHelper
/**
 * Constructor.
 *
 * @param connectivityManager
 * @param telephonyManager
 */(
    private val mConnectivityManager: ConnectivityManager,
    private val mTelephonyManager: TelephonyManager
) {
    companion object {
        private val sFastDataTypes: MutableList<Connection> = ArrayList()
        private val sFast3GDataTypes: MutableList<Connection> = ArrayList()
        var mWifiKilled = false
        operator fun get(context: Context): ConnectivityHelper {
            val c = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val t = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            return ConnectivityHelper(c, t)
        }

        init {
            sFastDataTypes.add(Connection.WIFI)
            sFastDataTypes.add(Connection.LTE)
        }

        init {
            sFast3GDataTypes.add(Connection.HSDPA)
            sFast3GDataTypes.add(Connection.HSPAP)
            sFast3GDataTypes.add(Connection.HSUPA)
            sFast3GDataTypes.add(Connection.EVDO_B)
        }
    }

    /**
     * Check the device current connectivity state based on the active network.
     *
     * @return
     */
    fun hasNetworkConnection(): Boolean {
        val activeNetwork = mConnectivityManager.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting
    }// We need to check 2 methods for the type because they both can give a different
    // value for the same information we are checking.
    // Get network type from ConnectivityManager.
    // Get network type from TelephonyManager.
    /**
     * Get the current connection type.
     *
     * @return Long representation of the connection type.
     */
    val connectionType: Connection
        get() {
            val info = mConnectivityManager.activeNetworkInfo
            if (info == null || !info.isConnected) {
                return Connection.NO_CONNECTION
            }

            // We need to check 2 methods for the type because they both can give a different
            // value for the same information we are checking.
            // Get network type from ConnectivityManager.
            val networkTypeConnection = info.subtype
            // Get network type from TelephonyManager.
            @SuppressLint("MissingPermission") val networkTypeTelephony =
                mTelephonyManager.networkType
            if (info.type == ConnectivityManager.TYPE_WIFI) {
                return Connection.WIFI
            } else if (networkTypeConnection == TelephonyManager.NETWORK_TYPE_LTE || networkTypeTelephony == TelephonyManager.NETWORK_TYPE_LTE) {
                return Connection.LTE
            } else if (networkTypeConnection == TelephonyManager.NETWORK_TYPE_HSDPA || networkTypeTelephony == TelephonyManager.NETWORK_TYPE_HSDPA) {
                return Connection.HSDPA
            } else if (networkTypeConnection == TelephonyManager.NETWORK_TYPE_HSPAP || networkTypeTelephony == TelephonyManager.NETWORK_TYPE_HSPAP) {
                return Connection.HSPAP
            } else if (networkTypeConnection == TelephonyManager.NETWORK_TYPE_HSUPA || networkTypeTelephony == TelephonyManager.NETWORK_TYPE_HSUPA) {
                return Connection.HSUPA
            } else if (networkTypeConnection == TelephonyManager.NETWORK_TYPE_EVDO_B || networkTypeTelephony == TelephonyManager.NETWORK_TYPE_EVDO_B) {
                return Connection.EVDO_B
            }
            return Connection.SLOW
        }

    /**
     * Get the connection type as string. This is mainly used for the GA tracking.
     *
     * @return String representation of the connection type.
     */
    val connectionTypeString: String
        get() {
            val connectionString: String
            connectionString = when (connectionType) {
                Connection.WIFI -> Connection.WIFI.toString()
                Connection.LTE -> Connection.LTE.toString()
                Connection.HSDPA -> Connection.HSDPA.toString()
                Connection.HSPAP -> Connection.HSPAP.toString()
                Connection.HSUPA -> Connection.HSUPA.toString()
                Connection.EVDO_B -> Connection.EVDO_B.toString()
                else -> Connection.NO_CONNECTION.toString()
            }
            return connectionString
        }

    fun useWifi(context: Context, useWifi: Boolean) {
        val wifiManager =
            context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifiManager.isWifiEnabled = useWifi
    }

    /**
     * Sets a timer that runs until the timeout limit is reached. At each interval it
     * will check whether or not we succeeded in enabling LTE. If it did not succeed then
     * we will try to get Wifi on again.
     */
    fun waitForLTE(context: Context, timeout: Int, interval: Int) {
        val remainingTime = timeout - interval
        Handler().postDelayed({
            // Keep waiting until the remaining time is less then the interval.
            if (remainingTime > interval) {
                waitForLTE(context, remainingTime, interval)
            } else if (connectionType != Connection.LTE) {
                // Turn wifi back on if we don't succeed in connecting with LTE before the timeout.
                useWifi(context, true)
            }
        }, interval.toLong())
    }

    fun attemptUsingLTE(context: Context, timeout: Int) {
        if (connectionType == Connection.WIFI) {
            useWifi(context, false)
            mWifiKilled = true
            waitForLTE(context, timeout + timeout / 10, timeout / 10)
        }
    }

    enum class Connection(// ~ 5 Mbps
        var stringValue: String, var intValue: Int
    ) {
        NO_CONNECTION("Unknown", -1), SLOW("Slow", 0), WIFI("Wifi", 1), LTE("4G", 2), HSDPA(
            "HSDPA",
            3
        ),  // ~ 2-14 Mbps
        HSPAP("HSPAP", 4),  // ~ 10-20 Mbps
        HSUPA("HSUPA", 5),  // ~ 1-23 Mbps
        EVDO_B("EVDO_B", 6);

        override fun toString(): String {
            return stringValue
        }
    }
}