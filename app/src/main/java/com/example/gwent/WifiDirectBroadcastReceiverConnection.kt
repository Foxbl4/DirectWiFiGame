package com.example.gwent

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.NetworkInfo
import android.net.wifi.p2p.WifiP2pManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.main_activity.*



class WifiDirectBroadcastReceiverConnection(
    private var mManager: WifiP2pManager,
    private var mChannel: WifiP2pManager.Channel,
    private var mActivity: ConnectionActivity
) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action: String = intent.action.toString()

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION == action){

            val state: Int = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED){
                Toast.makeText(context, "Wifi is ON", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Wifi is OFF", Toast.LENGTH_SHORT).show()
            }

        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION == action){

            if (ActivityCompat.checkSelfPermission(
                    context, Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED) {
                return
            }

            mManager.requestPeers(mChannel, mActivity.peerListListener)

        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION == action){

            val networkInfo: NetworkInfo? = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO)

            if (networkInfo?.isConnected!!){
                mManager.requestConnectionInfo(mChannel, mActivity.connectionInfoListener)
            } else {
                mActivity.txtConnect.text = "Device Disconnected"
            }

        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION == action){
           // val device: WifiP2pDevice? =
               // intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE)
           // val thisDeviceName = device!!.deviceName
        }
    }
}