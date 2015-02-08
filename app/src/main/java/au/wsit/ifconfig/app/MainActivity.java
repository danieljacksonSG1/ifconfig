package au.wsit.ifconfig.app;
import android.content.Context;
import android.content.Intent;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.os.Handler;
import android.net.*;
import java.io.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.Enumeration;
import org.apache.http.conn.util.InetAddressUtils;



public class MainActivity extends ActionBarActivity {

    // TextView variables
    TextView IP_VIEW;
    TextView SUBNET_VIEW;
    TextView GATEWAY_VIEW;
    TextView WAN_VIEW;
    TextView NAT_VIEW;
    TextView DNS1_VIEW;
    TextView DNS2_VIEW;

    TextView DHCPSERVER_VIEW;
    TextView LEASE_VIEW;

    TextView SSID_VIEW;
    TextView RSSI_VIEW;
    TextView BSSID_VIEW;
    TextView LINKSPEED_VIEW;
    TextView LOCALMAC_VIEW;

    ProgressBar BITRATE_BAR;

    TextView CONSOLE_VIEW;
    int console_count = 0;

    // For Refreshing the GUI
    private Handler mHandler;

    String DEBUG_TAG = "EXP:";

    // Dynamic DNS URL
    String DYN_URL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Init the TextView Objects
        IP_VIEW = (TextView) findViewById(R.id.IP_SetTextVIew_ID);
        SUBNET_VIEW = (TextView) findViewById(R.id.SN_SetTextView_ID);
        GATEWAY_VIEW = (TextView) findViewById(R.id.GW_SetTextView_ID);
        WAN_VIEW = (TextView) findViewById(R.id.WAN_SetTextView_ID);
        NAT_VIEW = (TextView) findViewById(R.id.NAT_SetTextView_ID);
        DNS1_VIEW = (TextView) findViewById(R.id.DNS1_SetTextView_ID);
        DNS2_VIEW = (TextView) findViewById(R.id.DNS2_SetTextView_ID);

        DHCPSERVER_VIEW = (TextView) findViewById(R.id.DHCPServer_SetTextView_ID);
        LEASE_VIEW = (TextView) findViewById(R.id.Lease_SetTextView_ID);

        SSID_VIEW = (TextView) findViewById(R.id.SSID_SetTextView_ID);
        RSSI_VIEW = (TextView) findViewById(R.id.RSSI_SetTextView_ID);
        BSSID_VIEW = (TextView) findViewById(R.id.BSSID_SetTextView_ID);
        LINKSPEED_VIEW = (TextView) findViewById(R.id.LINKSPEED_SetTextView_ID);
        LOCALMAC_VIEW = (TextView) findViewById(R.id.LocalMac_SetTextView_ID);

        BITRATE_BAR = (ProgressBar) findViewById(R.id.BitRateBar_ID);
        CONSOLE_VIEW = (TextView) findViewById(R.id.ConsoleOutput_ID);

        // Refreshe's the RSSI and Mbps in the GUI
        mHandler = new Handler();
        mHandler.post(mUpdate);


        if (WriteFile.checkFileExists("URL.txt") == false)
        {
            DYN_URL = "http://checkip.dyndns.org\n";
            // We then write it to file
            WriteFile WRITE_URL = new WriteFile();
            WRITE_URL.writeData(DYN_URL, "URL.txt");
        }

        else if (WriteFile.checkFileExists("URL.txt") == true)
        {
            // Get the saved Dynamic DNS update method from file
            WriteFile GetIP_FROM_FILE = new WriteFile();
            DYN_URL = GetIP_FROM_FILE.readData("URL.txt");
            new DownloadWebpageTask().execute(DYN_URL);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        new DownloadWebpageTask().execute(DYN_URL);
    }

    // Click handler for refreshing the WAN IP text view
    public void RefreshWANip(View view)
    {
       new DownloadWebpageTask().execute(DYN_URL);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // This method calls the GetWifiData() in another thread then sends it back to the main UI thread to refresh the data
    private Runnable mUpdate = new Runnable() {
        public void run() {
            // Stuff

            GetIPinfo();
            GetWirelessInfo();
            getMobileIP();


            mHandler.postDelayed(this, 1000);
        }
    };


    // This method gets all the IP details
    public void GetIPinfo() {
        String IP; // Wireless LAN IP Address
        String GW; // Gateway
        String SN; // Subnet Mask
        String DNS1;
        String DNS2;
        String DHCP_SERVER_IP;
        String Lease;

        // Create an instance of the WiFiManager API
        WifiManager WiFiInfo = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        DhcpInfo DHCP_INFO = WiFiInfo.getDhcpInfo();
        IP = Formatter.formatIpAddress(DHCP_INFO.ipAddress);
        SN = Formatter.formatIpAddress(DHCP_INFO.netmask);
        GW = Formatter.formatIpAddress(DHCP_INFO.gateway);
        DNS1 = Formatter.formatIpAddress(DHCP_INFO.dns1);
        DNS2 = Formatter.formatIpAddress(DHCP_INFO.dns2);
        DHCP_SERVER_IP = Formatter.formatIpAddress(DHCP_INFO.serverAddress);
        Lease = Integer.toString(DHCP_INFO.leaseDuration / 60 / 60);

        // Push the data to the GUI
        if (NetState() == "WiFi")
        {
            IP_VIEW.setText(IP);
            SUBNET_VIEW.setText(SN);
            GATEWAY_VIEW.setText(GW);
            DNS1_VIEW.setText(DNS1);
            DNS2_VIEW.setText(DNS2);
            DHCPSERVER_VIEW.setText(DHCP_SERVER_IP);
            LEASE_VIEW.setText(Lease + "Hr");
        }
        else if (NetState() == "Mobile")
        {
            IP_VIEW.setText(getMobileIP());
            SUBNET_VIEW.setText("");
            GATEWAY_VIEW.setText("");
            DNS1_VIEW.setText("");
            DNS2_VIEW.setText("");
            DHCPSERVER_VIEW.setText("");
            LEASE_VIEW.setText("");

        }


    }

    public void GetWirelessInfo() {
        String SSID;
        String RSSI;
        String BSSID;
        String LinkSpeed;
        String LocalMac;

        WifiManager WifiStats = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo WIFI_INFO = WifiStats.getConnectionInfo();

        SSID = WIFI_INFO.getSSID();
        RSSI = Integer.toString(WIFI_INFO.getRssi());
        BSSID = WIFI_INFO.getBSSID();
        LinkSpeed = Integer.toString(WIFI_INFO.getLinkSpeed());
        LocalMac = WIFI_INFO.getMacAddress();

        if (NetState() == "WiFi")
        {
            SSID_VIEW.setText(SSID);
            RSSI_VIEW.setText(RSSI);
            BSSID_VIEW.setText(BSSID);
            LINKSPEED_VIEW.setText(LinkSpeed + "Mbps");
            BITRATE_BAR.setProgress(WIFI_INFO.getLinkSpeed());
            LOCALMAC_VIEW.setText(LocalMac);

        }
        else if(NetState() == "Mobile")
        {
            SSID_VIEW.setText("");
            RSSI_VIEW.setText("");
            BSSID_VIEW.setText("");
            LINKSPEED_VIEW.setText("");
            BITRATE_BAR.setProgress(0);
            LOCALMAC_VIEW.setText(LocalMac);
        }



    }

    private class DownloadWebpageTask extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Tap to Refresh";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result)
        {
            WAN_VIEW.setText(result);
            NAT_VIEW.setText(CheckNAT());
        }
    }



    public String downloadUrl(String myurl) throws IOException {
        String DEBUG_TAG = "HTTP LOG: ";

        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 500;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d(DEBUG_TAG, "The response is: " + response);
            is = conn.getInputStream();



            // Convert the InputStream into a string
            String contentAsString = readIt(is, len);
            contentAsString = Html.fromHtml(contentAsString).toString();
            // Split it up
            String WAN_IP_STRING[] = contentAsString.split("\\s");

          /*  if (WAN_IP_STRING[5] != IP_VIEW.getText())
            {
                NAT_VIEW.setText("NO");
            }
            else if(WAN_IP_STRING[5] == IP_VIEW.getText())
            {
                NAT_VIEW.setText("YES");
            } */
            // Return the IP in the array
            return WAN_IP_STRING[5];

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    // Reads an InputStream and converts it to a String.
    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

    public String getMobileIP()
    {
        String ipAddress = null;

        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && InetAddressUtils.isIPv4Address(ipAddress = inetAddress.getHostAddress()))
                    {
                        ipAddress = inetAddress.getHostAddress().toString();


                        return ipAddress;

                    }
                }
            }
        } catch (SocketException ex) {}
        return "done";
      }

    // Method to check if we're on 3G or WiFI
    public String NetState()
    {
        ConnectivityManager conMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        //mobile
        NetworkInfo.State mobile = conMan.getNetworkInfo(0).getState();

        //wifi
        NetworkInfo.State wifi = conMan.getNetworkInfo(1).getState();

        if (mobile == NetworkInfo.State.CONNECTED || mobile == NetworkInfo.State.CONNECTING)
        {
            //mobile
            //ToastMsg("Connect through 3G/4G");
            return "Mobile";
        }
        else if (wifi == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTING)
        {
            //wifi
           // ToastMsg("Connected through WiFi");
            return "WiFi";
        }

    return "done";
    }


    // Method for debugging purposes
    public void LogOutput(String msg)
    {
        if (console_count > 4)
        {
            // If the console messages get to big then clear the output log
            CONSOLE_VIEW.setText("");
        }

        msg = msg.toString();
        CONSOLE_VIEW.append(msg + "\n");
        console_count++;


    }
    // Returns YES or NO
    public String CheckNAT()
    {
        String WAN_IP = WAN_VIEW.getText().toString();
        String WIFI_IP = IP_VIEW.getText().toString();
        String MOBILE_IP = getMobileIP();
        if (WAN_IP == WIFI_IP || WAN_IP == MOBILE_IP)
        {
            return "NO";
        }
        else
        {
            return "YES";
        }
    }

    public void Exit(MenuItem Menu)
    {
        System.exit(0);
    }

    public void Settings(MenuItem Menu)
    {
        Intent ABOUT_INTENT = new Intent(this, Settings.class);
        startActivity(ABOUT_INTENT);
    }




}



