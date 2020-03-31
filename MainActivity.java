/*
	lightsd-client is an Android app for communicating 
	with the lightd server running on an Arduino.

	Copyright (c) 2014 GB Tony Cabrera
*/

package com.RDFM.lightsd;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.util.Log;
import android.os.AsyncTask;

import java.io.*;
import java.net.*;
import java.util.*;

public class MainActivity extends Activity
{
//    private Handler myhandler = new Handler();
	
	private static final String APP_NAME = "lightsd";

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		
		TextView text_info = (TextView)findViewById(R.id.text_info);
		String ip = getLocalIpAddress();
		text_info.setText(ip);
    }
	
	private String getLocalIpAddress() {
		try {
			NetworkInterface net = NetworkInterface.getByName("wlan0");
			String if_name = net.getName();
			Log.d(APP_NAME, if_name);
			Enumeration<InetAddress> inetAddresses = net.getInetAddresses();
			String ip_address="";
			for (InetAddress inetAddress : Collections.list(inetAddresses)) {
				ip_address = inetAddress.getHostAddress();
				Log.d(APP_NAME, ip_address);
			}
			return if_name + " : " + ip_address;
		} catch (SocketException e) {
			Log.e(APP_NAME, e.toString());
		}
		return null;
	}

	public void lightsOn(View view){
		Thread cThread = new Thread(new ClientThread("1"));
		cThread.start();
	}

	public void lightsOff(View view){
		Thread cThread = new Thread(new ClientThread("0"));
		cThread.start();
	}

	public class ClientThread implements Runnable {
		private String state;

		public ClientThread(String s){
			this.state = s;
		}

		public void run() {
		
			String host = "192.168.2.110";
			int port = 80;
			Log.d(APP_NAME, "Running...");
			try{
				Socket s = new Socket(host, port);
				Log.d(APP_NAME, "Connected.");
				PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(s.getOutputStream())), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
				out.println(state);

				String reply = in.readLine();
				Log.d(APP_NAME, reply);
				s.close();
			} catch (UnknownHostException e) {
				String st = Log.getStackTraceString(e);
			} catch (IOException e) {
				String st = Log.getStackTraceString(e);
			}
		}
	}
}
