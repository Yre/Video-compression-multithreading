package com.example.yre.media_0723;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.GroupInfoListener;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alpha on 2015/7/13.
 */
public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    //private MyWiFiActivity mActivity;
    private MainActivity_mew mActivity;

    private static final String TAG = "MainActivity_mew";

    //WifiP2pManager.PeerListListener myPeerListListener;  //receive WIFI_P2P_PEERS_CHANGED_ACTION
    public static String mDeviceName;

    public List peers = new ArrayList();


    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel,
                                       MainActivity_mew activity) {
        super();
        this.mManager = manager;
        this.mChannel = channel;
        this.mActivity = activity;

    }



    private ConnectionInfoListener connectionInfoListener = new ConnectionInfoListener(){
                @Override
                public void onConnectionInfoAvailable(final WifiP2pInfo info) {

                    //InetAddress groupOwnerAddress = info.groupOwnerAddress;
                    //String serverIpAddress=groupOwnerAddress.getHostAddress();
                    //Toast.makeText(mActivity, "Server IP Address "+serverIpAddress, Toast.LENGTH_SHORT).show();
                    //System.out.println("Server IP Address "+serverIpAddress);

                    // After the group negotiation, we can determine the group owner.
                    if (info.groupFormed && info.isGroupOwner) {
                        // Do whatever tasks are specific to the group owner.
                        // One common case is creating a server thread and accepting
                        // incoming connections.
                        mActivity.isOwner = true;
                        TextView stateText2 = (TextView) mActivity.findViewById(R.id.stateText2);
                        stateText2.setText("Group Owner");

                    } else if (info.groupFormed) {
                        // The other device acts as the client. In this case,
                        // you'll want to create a client thread that connects to the group
                        // owner.
                        mActivity.isOwner = false;
                        TextView stateText2 = (TextView) mActivity.findViewById(R.id.stateText2);
                        stateText2.setText("Group Client");
                    }
                }
            };

    private GroupInfoListener groupInfoListener = new GroupInfoListener(){
        @Override
        public void onGroupInfoAvailable(WifiP2pGroup group) {

            if(group != null){
                // clients require these

                //String ssid = group.getNetworkName();
                //String passphrase = group.getPassphrase();

                /*
                for(int i=0;i<group.getClientList().size();i++){
                    WifiP2pDevice device = (WifiP2pDevice) group..get(i);
                    String deviceName=device.deviceName;
                    //String devicestatus=Integer.toString(device.status);

                    System.out.println(deviceName);

                    mActivity.peersname.add(deviceName);

                    //so on
                }
                */

                TextView commentText = (TextView) mActivity.findViewById(R.id.commentText);
                //commentText.setText("ssid: " + ssid + "\n" + "passphrase: " + passphrase + "\n" + mActivity.myDeviceName);
                commentText.setText(group.getClientList().toString());
            }
        }
    };



    private PeerListListener peerListListener = new PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {

            // Out with the old, in with the new.

            mActivity.peersname.clear();                   //peersname is the list of names with Adapter

            mActivity.peersshow.clear();                   //peersshow is the list of OBJECTS
            mActivity.peersshow.addAll(peerList.getDeviceList());


            peers = mActivity.peersshow;

            System.out.println(peers);                   //peers simply includes the name list


            for(int i=0;i<peers.size();i++){
                WifiP2pDevice device = (WifiP2pDevice) peers.get(i);
                String deviceName=device.deviceName;
                //String devicestatus=Integer.toString(device.status);

                System.out.println(deviceName);

                mActivity.peersname.add(deviceName);

                //so on
            }


            System.out.println("PeersList added successful");


            mActivity.madapter.notifyDataSetChanged();

            if (peers.size() == 0) {
                //stateText.setText("No devices found");
                //Toast.makeText(context, "No devices found", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Check to see if Wi-Fi is enabled and notify appropriate activity
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                //mActivity.setIsWifiP2pEnabled(true);
            } else {
                //mActivity.setIsWifiP2pEnabled(false);
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // The peer list has changed!  We should probably do something about that.

            if (mManager != null) {
                mManager.requestPeers(mChannel, peerListListener);

                Toast.makeText(context, "P2P peers changed", Toast.LENGTH_SHORT).show();

            }

        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // Respond to new connection or disconnections

            if (mManager == null) {
                return;
            }

            NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            //System.out.println("Network Info: " + networkInfo);

            if (networkInfo.isConnected()) {

                // We are connected with the other device, request connection
                // info to find group owner IP

                Toast.makeText(context, "networkInfo_isConnected", Toast.LENGTH_SHORT).show();
                mActivity.finishConnectTime.setText(mActivity.getTime());
                mActivity.stateText.setText("Connected");
                mManager.requestConnectionInfo(mChannel, connectionInfoListener);
            }

        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
            WifiP2pDevice device = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
            mDeviceName = device.deviceName;
            mActivity.idText.setText(mDeviceName);


            mManager.requestGroupInfo(mChannel, groupInfoListener);

            /*DeviceListFragment fragment = (DeviceListFragment) activity.getFragmentManager()
                    .findFragmentById(R.id.frag_list);
            fragment.updateThisDevice((WifiP2pDevice) intent.getParcelableExtra(
                    WifiP2pManager.EXTRA_WIFI_P2P_DEVICE));*/


        }
    }


}
