package com.weezlabs.foursquarelib;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.weezlabs.forsquarelib.CheckParams;
import com.weezlabs.forsquarelib.LocationService;

public class MainActivity extends AppCompatActivity {
	//credentials for forsquarelib, registered on avolkova@ruswizards.com
	public static final String FOURSQUARE_CLIENT_ID = "YTZUJEICNYYBYWL4PRZWV13AXPVBUFLDOLTDXZP333HGVHJY";
	public static final String FOURSQUARE_CLIENT_SECRET = "JHJY0FPUZCZQMADBNV2TQ4M0MIVDQJW0QXJVXCXVR4QP4SD5";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
	}


	public void onStartClick(View clickedView){
		Intent intent = new Intent(MainActivity.this, LocationService.class);
		CheckParams checkParams = new CheckParams(FOURSQUARE_CLIENT_ID, FOURSQUARE_CLIENT_SECRET);
		checkParams.setWaitForOrderMs(15000);
		checkParams.setLocationUpdatePeriodMs(10000);
		checkParams.setWaitForStayMs(15000);
		checkParams.setWaitForStopEatingMs(20000);
		intent.putExtra(LocationService.CHECKER_EXTRA, checkParams);
		startService(intent);
		startReceiver();
	}

	public void onStopClick(View clickedView){
		if(LocationService.isStarted()) {
			Intent intent = new Intent(MainActivity.this, LocationService.class);
			stopService(intent);
			stopReceiver();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
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

	public void startReceiver(){
		// Switch On Broadcast Receiver
		PackageManager pm = MainActivity.this.getPackageManager();
		ComponentName componentName = new ComponentName(
				MainActivity.this, BroadcastManager.class);
		pm.setComponentEnabledSetting(componentName,
				PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
				PackageManager.DONT_KILL_APP);
		Toast.makeText(getApplicationContext(),
				"Broadcast Receiver Started", Toast.LENGTH_LONG)
				.show();
	}

	public void stopReceiver(){
		// Switch Off Broadcast Receiver
		PackageManager pm = MainActivity.this.getPackageManager();
		ComponentName componentName = new ComponentName(
				MainActivity.this, BroadcastManager.class);
		pm.setComponentEnabledSetting(componentName,
				PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
				PackageManager.DONT_KILL_APP);
		Toast.makeText(getApplicationContext(),
				"Broadcast Receiver Stopped", Toast.LENGTH_LONG)
				.show();
	}
}
