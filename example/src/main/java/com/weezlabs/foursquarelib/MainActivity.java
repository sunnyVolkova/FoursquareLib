package com.weezlabs.foursquarelib;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.weezlabs.forsquarelib.CheckParams;
import com.weezlabs.forsquarelib.LocationService;

public class MainActivity extends AppCompatActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
	}


	public void onStartClick(View clickedView){
		Intent intent = new Intent(MainActivity.this, LocationService.class);
		CheckParams checkParams = new CheckParams();
		checkParams.setWaitForOrderMs(15000);
		checkParams.setLocationUpdatePeriodMs(10000);
		checkParams.setWaitForStayMs(15000);
		checkParams.setWaitForStopEatingMs(20000);
		intent.putExtra(LocationService.CHECKER_EXTRA, checkParams);
		startService(intent);
	}

	public void onStopClick(View clickedView){
		if(LocationService.isStarted()) {
			Intent intent = new Intent(MainActivity.this, LocationService.class);
			stopService(intent);
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

}
