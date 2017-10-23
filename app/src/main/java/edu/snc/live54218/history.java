/****************************************************
This class is the history detailed view (used to select
past data to update and delete)
****************************************************/
package edu.snc.live54218;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class history extends Activity {
	
	//Variables
	private ArrayList<liveEntry> liveEntryArray;
	private ListView liveEntryLV;
	private liveEntryOpenHelper dbAdapter;
	private int thePosition = 0;
	
	//Constants
	private static final String colfruits = "fruitsAndVeggies";
	private static final String colwater = "waterBottles";
	private static final String colscreen = "screenTime";
	private static final String colexercise = "exerciseTime";
	private static final String colsleep = "sleepTime";
	private static final String colweight = "weight";
	private static final String coldate = "date";  
	  
	/***************************************************************
	 Purpose: Called when the history activity is first created
	 Input: Nothing
	 Output: Nothing
	 **************************************************************/
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);
        liveEntryArray = new ArrayList<liveEntry>();
        final Context ct = this;
        
        getAllEntries();
        
        //Listener for item click (edit entry)
        liveEntryLV.setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        		Object temp = liveEntryLV.getItemAtPosition(position);
        		liveEntry selectedEntry = (liveEntry)temp;
        		Intent i = new Intent(ct, entry.class);
            	Calendar ci = selectedEntry.getDate();
            	String thisday= Integer.toString((ci.get(Calendar.MONTH) + 1)) + "/" + Integer.toString(ci.get(Calendar.DAY_OF_MONTH)) + "/" + Integer.toString(ci.get(Calendar.YEAR));
            	i.putExtra("date", thisday);
            	startActivity(i);
            	finish();
        	}
      	});
        
        //listener for long item click (delete item)
        liveEntryLV.setOnItemLongClickListener(new OnItemLongClickListener() {
        	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        		String theDate = Integer.toString(liveEntryArray.get(position).getDate().get(Calendar.MONTH) + 1) + "/" + 
    		    		liveEntryArray.get(position).getDate().get(Calendar.DATE) + "/" +
    		    		liveEntryArray.get(position).getDate().get(Calendar.YEAR);
        		thePosition = position;
        		//Show delete item Alert Dialog
        		AlertDialog deleteDialog = new AlertDialog.Builder(ct).create();
        		deleteDialog.setTitle("Delete Entry...");
        		deleteDialog.setMessage("Are you sure you want to delete the data on " + theDate + "?");
        		deleteDialog.setIcon(R.drawable.delete);
        		deleteDialog.setButton("Yes", new DialogInterface.OnClickListener() {			
        			public void onClick(DialogInterface dialog, int which) {
    				dbAdapter = new liveEntryOpenHelper(getApplicationContext());
    				if (dbAdapter.deleteDay(liveEntryArray.get(thePosition).getDate()) == 1) {
    					Toast.makeText(ct, "Successfully deleted entry!", Toast.LENGTH_SHORT).show();
    					//update entries
    					getAllEntries();
    					if (dbAdapter != null)
    						dbAdapter.close();
    				}
    				else {
    					Toast.makeText(ct, "Unsuccessful delete, please try again...", Toast.LENGTH_SHORT).show();
    				}
    			} });
        		deleteDialog.setButton2("Cancel", new DialogInterface.OnClickListener() {			
        			public void onClick(DialogInterface dialog, int which) {
    				//Do Nothing				
    			} });
        		deleteDialog.show();    
				return true;
        	}
        });
    }	

	/***************************************************************
	 Purpose: Called when the activity is stopped, it closes the
	 database adapter
	 Input: Nothing
	 Output: Nothing
	 **************************************************************/
	@Override
	protected void onStop() {
		super.onStop();
        if (dbAdapter != null)
        	dbAdapter.close();
	}
	
	/**********************************************************
	Purpose: gets all entries from the live entry array in order to
	get it to the adapter
	Input: Nothing
	Output: Nothing
	 ************************************************************/
	private void getAllEntries() {		
		liveEntry le = new liveEntry();
		liveEntryArray = new ArrayList<liveEntry>();
		Cursor myCursor = null;
		try {	
			dbAdapter = new liveEntryOpenHelper(this);
			myCursor = dbAdapter.getAllEntries();
			Calendar cal = Calendar.getInstance();
			cal.clear();
			String DBcal;
			String[] tempDates;
			
			if (myCursor.getCount() > 0) {
				myCursor.moveToFirst();
				//Run through cursor (each entry in database) and add that entry to the live entry array
				while (!myCursor.isAfterLast()) {
					le = new liveEntry();
					cal = Calendar.getInstance();
					cal.clear();
					DBcal = myCursor.getString(myCursor.getColumnIndex(coldate));
					tempDates = DBcal.split("/");
					cal.set(Calendar.MONTH, (Integer.parseInt(tempDates[0])));
					cal.set(Calendar.DATE, (Integer.parseInt(tempDates[1])));
					cal.set(Calendar.YEAR, (Integer.parseInt(tempDates[2])));
					le.setDate(cal);
					le.setFruitsAndVeggies(Float.parseFloat(myCursor.getString(myCursor.getColumnIndex(colfruits))));
					le.setWater(Float.parseFloat(myCursor.getString(myCursor.getColumnIndex(colwater))));
					le.setScreenTime(Float.parseFloat(myCursor.getString(myCursor.getColumnIndex(colscreen))));
					le.setExerciseTime(Float.parseFloat(myCursor.getString(myCursor.getColumnIndex(colexercise))));
					le.setSleepTime(Float.parseFloat(myCursor.getString(myCursor.getColumnIndex(colsleep))));
					le.setWeight(Integer.parseInt(myCursor.getString(myCursor.getColumnIndex(colweight))));
					liveEntryArray.add(le);
					myCursor.moveToNext();
				}
			} else {
				//add empty live entry
				liveEntryArray.add(le);
			}
		} catch (Exception e) {
			liveEntryArray.add(le);
			Log.e("historyGetAllEntries", e.toString());
			return;
		}
		liveEntryLV = (ListView)findViewById(R.id.historyListView);
		//set live entry array adapter to custombaseadapter
        liveEntryLV.setAdapter(new CustomBaseAdapter(this, liveEntryArray));
        
        //close cursor if open
        if (myCursor != null)
        	myCursor.close();
	}
}

