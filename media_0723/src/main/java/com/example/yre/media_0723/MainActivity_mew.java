package com.example.yre.media_0723;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class MainActivity_mew extends AppCompatActivity {

    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    BroadcastReceiver mReceiver;

    private final IntentFilter mIntentFilter = new IntentFilter();

    public TextView stateText;         //text
    public TextView stateText2;        //text
    public TextView idText;
    public TextView commentText;

    private Button connectButton;
    private Button searchButton;
    private Button enableButton;
    private Button disConnectButton;

    public TextView startConnectTime;
    public TextView finishConnectTime;

    private Button serverButton;
    //private Button clientButton;
    private Button server2Button;
    //private Button client2Button;
    private Button fileButton;
    private Button imageButton;
    private Button testButton;


    List peersshow = new ArrayList();

    ArrayList<String> peersname = new ArrayList<String>(){};

    ArrayAdapter<String> madapter;


    public static boolean enablenum = false;
    public static int peerpick = 0;

    String myDeviceName;
    public static boolean isOwner = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_mew);

        //String id = System.getProperty("user.name");

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);


        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);


        connectButton = (Button) findViewById(R.id.connectButton);
        stateText = (TextView) findViewById(R.id.stateText);
        searchButton = (Button) findViewById(R.id.searchButton);
        enableButton = (Button) findViewById(R.id.enableButton);
        disConnectButton = (Button) findViewById(R.id.disConnectButton);
        startConnectTime = (TextView) findViewById(R.id.start_connect_time);
        finishConnectTime = (TextView) findViewById(R.id.finish_connect_time);
        serverButton = (Button) findViewById(R.id.serverButton);
        //clientButton = (Button) findViewById(R.id.clientButton);
        server2Button = (Button) findViewById(R.id.server2Button);
        //client2Button = (Button) findViewById(R.id.client2Button);
        fileButton = (Button) findViewById(R.id.fileButton);
        imageButton = (Button) findViewById(R.id.imageButton);
        testButton =(Button) findViewById(R.id.testButton);


        idText = (TextView) findViewById(R.id.idText);
        commentText = (TextView) findViewById(R.id.commentText);

        searchButton.setOnClickListener(searchButtonClick);
        connectButton.setOnClickListener(connectButtonClick);
        enableButton.setOnClickListener(enableButtonClick);
        disConnectButton.setOnClickListener(disConnectButtonClick);
        //serverButton.setOnClickListener(serverButtonClick);
        //clientButton.setOnClickListener(clientButtonClick);
        //server2Button.setOnClickListener(server2ButtonClick);
        //client2Button.setOnClickListener(client2ButtonClick);
        fileButton.setOnClickListener(fileButtonClick);
        //imageButton.setOnClickListener(imageButtonClick);
        testButton.setOnClickListener(testButtonClick);



        madapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, peersname);
        ListView peersListView = (ListView) findViewById(R.id.peersListView);
        peersListView.setAdapter(madapter);


        peersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                peerpick = position;
                stateText.setText(peersname.get(peerpick));

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /* register the broadcast receiver with the intent values to be matched */
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }

    /* unregister the broadcast receiver */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

//==================================================================================================//

    View.OnClickListener searchButtonClick = new View.OnClickListener() {   //-----searchButton

        public void onClick(View v){
            startConnectTime.setText(getTime());
            Toast.makeText(getBaseContext(), "Searching~", Toast.LENGTH_SHORT).show();
            stateText.setText("Search~");

            mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {

                @Override
                public void onSuccess() {

                    finishConnectTime.setText(getTime());
                }

                @Override
                public void onFailure(int reasonCode) {

                    Toast.makeText(getBaseContext(), Integer.toString(reasonCode), Toast.LENGTH_LONG).show();

                }
            });
        }
    };


    //@Override
    public void connect(){  //--------------------------------------------CONNECT


        stateText.setText("Trying Connect with: " + peersname.get(peerpick));

        WifiP2pDevice device = (WifiP2pDevice) peersshow.get(peerpick);   //modified
        WifiP2pConfig config = new WifiP2pConfig();

        config.deviceAddress = device.deviceAddress;
        final String deviceName = device.deviceName;
        config.wps.setup = WpsInfo.PBC;

        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                // WiFiDirectBroadcastReceiver will notify us. Ignore for now.
                Toast.makeText(MainActivity_mew.this, "Connection Init Successful!",
                        Toast.LENGTH_SHORT).show();

                stateText.setText("Connected with: " + deviceName);
                finishConnectTime.setText(getTime());
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(MainActivity_mew.this, "Connect failed. Retry.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    //@Override
    public void disconnect() {

        mManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {

            @Override
            public void onFailure(int reasonCode) {
                Toast.makeText(MainActivity_mew.this, "Disconnect failed. Reason :" + reasonCode, Toast.LENGTH_SHORT).show();
                finishConnectTime.setText(getTime());
                //stateText2.setText("");
            }

            @Override
            public void onSuccess() {
                Toast.makeText(MainActivity_mew.this, "Disconnect Success", Toast.LENGTH_SHORT).show();
                stateText.setText("Hello world!");
                finishConnectTime.setText(getTime());
                //stateText2.setText("");
            }

        });
    }


    public View.OnClickListener connectButtonClick = new View.OnClickListener() {  //-----connectButton

        public void onClick(View v){
            startConnectTime.setText(getTime());
            Toast.makeText(getBaseContext(), "Connecting~", Toast.LENGTH_SHORT).show();
            stateText.setText("Connection Init");

            try {

                connect();

            } catch (Exception ex) {
                Toast.makeText(getBaseContext(), "Connection Failed, PLZ try again", Toast.LENGTH_SHORT).show();
            }

        }
    };

    public View.OnClickListener enableButtonClick = new View.OnClickListener() {  //-----enableButton

        public void onClick(View v){

            if (!(enablenum)){
                Toast.makeText(getBaseContext(), "Enable Discovering~", Toast.LENGTH_SHORT).show();
                enableButton.setText("Enable");
                enablenum=true;
                stateText.setText("Enable or not~");
            } else{
                Toast.makeText(getBaseContext(), "UnEnable~", Toast.LENGTH_SHORT).show();
                enableButton.setText("UnEnable");
                enablenum=false;
                stateText.setText("Enable or not~");
            }

        }
    };

    View.OnClickListener disConnectButtonClick = new View.OnClickListener() {   //-----disConnectButton

        public void onClick(View v){
            startConnectTime.setText(getTime());
            Toast.makeText(getBaseContext(), "disConnecting~", Toast.LENGTH_SHORT).show();
            stateText.setText("DisConnect~");

            disconnect();
        }
    };

    /*
    View.OnClickListener clientButtonClick = new View.OnClickListener() {   //-----clientButton

        public void onClick(View v){

            myDeviceName = idText.getText().toString();
            Intent intent = new Intent(MainActivity.this, ClientActivity.class);
            intent.putExtra("devicename", myDeviceName);
            startActivity(intent);
        }
    };
    */
    /*
    View.OnClickListener client2ButtonClick = new View.OnClickListener() {

        public void onClick(View v){

            Intent intent = new Intent(MainActivity.this, ClientActivity_2.class);
            //intent.putExtra("devicename", myDeviceName);
            startActivity(intent);
        }
    };
    */

    View.OnClickListener fileButtonClick = new View.OnClickListener() {

        public void onClick(View v){

            if (MainActivity_mew.isOwner){
                Intent intent = new Intent(MainActivity_mew.this, FileServerActivity.class);
                //intent.putExtra("devicename", myDeviceName);
                String path = getIntent().getStringExtra("path");
                intent.putExtra("path",path);
                startActivity(intent);
            } else {
                Intent intent = new Intent(MainActivity_mew.this, FileClientActivity.class);
                //intent.putExtra("devicename", myDeviceName);
                String path = getIntent().getStringExtra("path");
                intent.putExtra("path",path);
                startActivity(intent);
            }

        }
    };


    View.OnClickListener testButtonClick = new View.OnClickListener() {

        public void onClick(View v){

            Toast.makeText(getBaseContext(),"Testing",Toast.LENGTH_SHORT).show();

            /*
            File foo = new File(Environment.getExternalStorageDirectory(), "JustTest.CLASS");

            Method method = foo.getClass().getMethod("pop", null);
            method.invoke(foo, null);
            */

            /*
            String packageName = "com.example.mypackage";
            String className = "com.example.mypackage.MyClass";

            String apkName = getPackageManager().getApplicationInfo(packageName, 0).sourceDir;
            PathClassLoader myClassLoader =
                    new dalvik.system.PathClassLoader(
                            apkName,
                            ClassLoader.getSystemClassLoader());
            Class<?> handler = Class.forName(className, true, myClassLoader);

            */



        }
    };




    public String getTime(){

        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH); // Note: zero based!
        int day = now.get(Calendar.DAY_OF_MONTH);
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);
        int second = now.get(Calendar.SECOND);
        int millis = now.get(Calendar.MILLISECOND);

        String timer = (Integer.toString(year) + "-" + Integer.toString(month+1) + "-"
                + Integer.toString(day) + " " + Integer.toString(hour) + ":"
                + Integer.toString(minute) + ":" + Integer.toString(second) + "." + Integer.toString(millis));

        return timer;

    }

}
