package com.marakaba.yamba;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.TwitterException;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class StatusActivity extends Activity implements OnClickListener, TextWatcher, OnSharedPreferenceChangeListener {
	private static final String TAG = "StatusActivity";
	EditText editText;
	Button updateButton;
	Twitter twitter;
	TextView textCount;
	SharedPreferences prefs;
	
    /** Called when the activity is first created. */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.status);
        
        // Find views
        editText = (EditText) findViewById(R.id.editText);
        updateButton = (Button) findViewById(R.id.buttonUpdate);
        updateButton.setOnClickListener(this);
        
        textCount = (TextView) findViewById(R.id.textCount);
        textCount.setText(Integer.toString(140));
        textCount.setTextColor(Color.GREEN);
        editText.addTextChangedListener(this);
        
        twitter = new Twitter("student", "password");
        twitter.setAPIRootUrl("http://yamba.marakana.com/api");
        
        // Setup preferences
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);
    }
	
	@SuppressWarnings("deprecation")
	private Twitter getTwitter() {
		if (twitter == null) { // Only if twitter is null (i.e., undefined), we create it.
			String username, password, apiRoot;
			username = prefs.getString("username", "");
			password = prefs.getString("password", "");
			apiRoot = prefs.getString("apiRoot", "htp://yamba.marakana.com/api");
			
			// Connect to twitter.com
			twitter = new Twitter(username, password); // We log into the Twitter service with user-defined preferences.
			twitter.setAPIRootUrl(apiRoot); // Remember that we need to update the actual service that we are using by updating the API root URL for that service.
		}
		return twitter;
	}
    
    // Called when button is clicked
    public void onClick(View v) {
    	// Update twitter status
    	try {
    		getTwitter().setStatus(editText.getText().toString());
    	}
    	catch (TwitterException e) {
    		Log.d(TAG, "Twitter setStatus failed: " + e);
    	}
    	
    	/*
    	String status = editText.getText().toString();
    	new PostToTwitter().execute(status);
    	Log.d(TAG, "onClicked");
    	*/
    }
    
    // Called first time user clicks on the menu button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater(); // We get the MenuInflater object from the context.
    	inflater.inflate(R.menu.menu, menu); // Use the inflater to inflate the menu from the XML resource.
    	return true; // We must return true for this menu to be displayed.
    }
    
    // Called when an options item is clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) { // Since the same method is called regardless of which item the user clicks, we need to figure out the ID of that item,
    								// and based on that, switch to a specific case to handle each item. At this point we only have one menu item, but that 
    								// might change in the future. Switching an item ID is a very scalable approach and will adapt nicely as our application
    								// grows in complexity.
    	case R.id.itemPrefs:
    		startActivity(new Intent(this, PrefsActivity.class)); 	// The startActivity() method in context allows us to launch a new activity. In this case,
    																// we are creating a new intent that specifies starting the PrefsActivity class.
    		break;
    	}
    	return true;	// Return true to consume the event here.
    }
    
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
    	// invalidate twitter object
    	twitter = null;
    }
    
    // Asynchronously posts to twitter
    class PostToTwitter extends AsyncTask<String, Integer, String> {
    	
    	// Called to initiate the background activity
    	@Override
    	protected String doInBackground(String...statuses) {
    		try 
    		{
    			Twitter.Status status = twitter.updateStatus(statuses[0]);
    			return status.text;
    		}
    		catch (TwitterException e) {
    			Log.e(TAG, e.toString());
    			e.printStackTrace();
    			return "Failed to post";
    		}
    	}
    	
    	// Called when there's a status to be updated
    	protected void onProgressUpdate(Integer...values) {
    		super.onProgressUpdate(values);
    		// Not used in this case
    	}
    	
    	// Called once the background activity has completed
    	@Override
    	protected void onPostExecute(String result) {
    		Toast.makeText(StatusActivity.this, result, Toast.LENGTH_LONG).show();
    	}	
    }
    
    // TextWatcher methods
    public void afterTextChanged(Editable statusText) {
    	int count = 140 - statusText.length();
    	textCount.setText(Integer.toString(count));
    	textCount.setTextColor(Color.GREEN);
    	if (count < 10)
    		textCount.setTextColor(Color.YELLOW);
    	if (count < 5)
    		textCount.setTextColor(Color.RED);
    }
    
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
    
    public void onTextChanged(CharSequence s, int start, int before, int count) {}
}