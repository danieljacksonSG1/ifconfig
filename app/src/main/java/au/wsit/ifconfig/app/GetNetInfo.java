package au.wsit.ifconfig.app;

import android.media.MediaRouter;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.RouteInfo;
import android.util.Log;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by guyb on 8/02/15.
 */
public class GetNetInfo
{
    public String TAG = GetNetInfo.class.getSimpleName();
    public String intName;
    public int intMTU;
    public String IPAddress1;

    // Get our interface name
    public String getIntName()
    {
        try
        {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            NetworkInterface NI = interfaces.nextElement();
            intName = NI.getDisplayName();
            Log.i(TAG, intName);

            return intName;

        }
        catch(SocketException e)
        {
            Log.e(TAG, e.getMessage());
        }

        return null;
    }

    public int getMTU()
    {
        try
        {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            NetworkInterface NI = interfaces.nextElement();
            intMTU = NI.getMTU();
            Log.i(TAG, intMTU + "");

            return intMTU;

        }
        catch(SocketException e)
        {
            Log.e(TAG, e.getMessage());
        }

        return intMTU;
    }

    public String getIPAddresses()
    {
        try
        {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            NetworkInterface NI = interfaces.nextElement();
            List<InterfaceAddress> IPAddress = NI.getInterfaceAddresses();
            InterfaceAddress IP = IPAddress.get(0);
            IPAddress1 = IP.toString();

            Log.i(TAG, IPAddress1);

            return IPAddress1;

        }
        catch(SocketException e)
        {
            Log.e(TAG, e.getMessage());
        }

        return null;
    }

    public String getNext1()
    {
        try
        {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            NetworkInterface NI = interfaces.nextElement();
            Enumeration<InetAddress> IE = NI.getInetAddresses();
            InetAddress myIE = IE.nextElement();

            RouteInfo RI = RouteInfo(RouteInfo);

            return IPAddress1;

        }
        catch(SocketException e)
        {
            Log.e(TAG, e.getMessage());
        }

        return null;
    }




}
