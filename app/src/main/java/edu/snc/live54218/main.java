/***************************************************************
 This class is the main activity created when the application is
 first created (contains options to enter data, view history, view
 about and summary that shows how many days are complete vs how
 many entries there are
 **************************************************************/
package edu.snc.live54218;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class main extends Activity {
	
	//Variables
	private TextView tv;
	private liveEntryOpenHelper dbAdapter; 
	private Cursor c;
	
	/*********************************************************************
	 Purpose: This is called when the main activity is created
	 Input: Nothing
	 Output: Nothing
	 *********************************************************************/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
    
	/*********************************************************************
	 Purpose: When activity is resumed, update the summary text on the screen
	 Input: Nothing
	 Output: Nothing
	 *********************************************************************/
    @Override
	protected void onResume() {
		super.onResume();
		dbAdapter = new liveEntryOpenHelper(this);
        c = dbAdapter.getAllEntries();
        int total, complete;
        //total - count of all entries in the array
        total = c.getCount();
        //complete - count of all completed entries
        complete = dbAdapter.getCompleteDays();
        
        if (complete == -1)
        	complete = 0;
        
        tv = (TextView)findViewById(R.id.mainTxtViewSummary);
        tv.setText("Summary: You have successfully completed " + Integer.toString(complete) + "/" + Integer.toString(total) + " days");
        
        //close cursor
        c.close();
	}

    /*********************************************************************
	 Purpose: When activity has stopped, close cursor and dbAdapter if not null
	 Input: Nothing
	 Output: Nothing
	 *********************************************************************/
	@Override
	protected void onStop() {
		super.onStop();
		if (c != null)
			c.close();
		if (dbAdapter != null)
			dbAdapter.close();
	}

	/******************************************************
	 * OnClick Listener
	On Enter data click, opens the date options class
	to enter data
	 *******************************************************/
	public void entryClick(View view){
    	//Starts dateOptions.java activity
    	Intent i = new Intent(this, dateOptions.class);
    	startActivity(i);
    }
    
	/******************************************************
	 * OnClick Listener
	On History click, opens the history calendar class
	 *******************************************************/
    public void historyClick(View view){
    	//Starts history.java activity
    	Intent i = new Intent(this, historyCalendar.class);
    	startActivity(i);
    }
    
	/******************************************************
	 * OnClick Listener
	On About click, opens the about class
	 *******************************************************/
    public void aboutClick(View view){
    	//Starts entry.java activity
    	Intent i = new Intent(this, about.class);
    	startActivity(i);
    }
}