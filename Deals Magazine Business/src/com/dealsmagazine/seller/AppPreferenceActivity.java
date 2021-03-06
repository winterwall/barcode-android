/*
 * 
 * � 2011 WhereYouShop.com  All Rights Reserved | http://www.whereyoushop.com 
 * Emergency 24    | The WhereYouShop Team  
 *
 */

package com.dealsmagazine.seller;

import com.dealsmagazine.seller.R;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class AppPreferenceActivity extends PreferenceActivity {
	
	/*
	 * Default Constructor
	 */
	public AppPreferenceActivity() {
	}

	/*
	 * Called when the activity is first created. Inflate the Preferences Screen XML declaration.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Inflate the XML declaration
		addPreferencesFromResource(R.xml.prefs);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
			Intent intent = new Intent();
			intent.setClass(AppPreferenceActivity.this, TabView.class);
			intent.putExtra(TabView.KEY_TAB_INDEX, 1);
			startActivity(intent);
			finish();
	}
}
