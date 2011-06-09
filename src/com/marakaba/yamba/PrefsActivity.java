
package com.marakaba.yamba;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class PrefsActivity extends PreferenceActivity { // Unlike regular activities, PrefsActivity will subclass (i.e., extend) the PreferenceActivity class.

	@Override
	protected void onCreate(Bundle savedInstanceState) { // Just like any other activity, we override the onCreate() method to initialize the activity.
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.prefs); // Unlike regular activities that usually call setContentView(), our preference activity will set its content from 
	}											 // the prefs.xml file via a call to addPreferencesFromResources().

}
