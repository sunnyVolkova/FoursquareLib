package com.weezlabs.foursquarelib;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.weezlabs.forsquarelib.LocationChecker;
import com.weezlabs.forsquarelib.LocationService;

public class MainActivity extends AppCompatActivity {
	private LocationService locationService_;
	private boolean isBound_;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(MainActivity.this, LocationService.class);
				bindService(intent, serviceConnection_, Context.BIND_AUTO_CREATE);
			}
		});
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(isBound_ && locationService_ != null){
			locationService_.stopMyLocationCheck();
		}
	}

	/**
	 * Defines callbacks for service binding, passed to bindService()
	 */
	private ServiceConnection serviceConnection_ = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className,
									   IBinder service) {
			Log.d("LOG", "LocationService onServiceConnected");
			// We've bound to LocalService, cast the IBinder and get LocalService instance
			LocationService.LocalBinder binder = (LocationService.LocalBinder) service;
			locationService_ = binder.getService();
			LocationChecker customChecker = new LocationChecker();
			customChecker.setWaitForOrderMs(15000);
			customChecker.setLocationUpdatePeriodMs(10000);
			customChecker.setWaitForStayMs(15000);
			customChecker.setWaitForStopEatingMs(20000);
			locationService_.startMyLocationCheck(customChecker);
			isBound_ = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			Log.d("LOG", "LocationService onServiceDisconnected");
			isBound_ = false;
		}
	};

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
}
