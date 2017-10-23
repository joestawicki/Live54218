/****************************************************
Adapted from Lauri Nevala's example at:
http://caughtinthemobileweb.wordpress.com/2011/06/20/how-to-implement-calendarview-in-android/
This class is the history calendar display for the live entries
****************************************************/

package edu.snc.live54218;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

public class historyCalendar extends Activity {
	
	//Variables
	private Calendar month = Calendar.getInstance();
	private CalendarAdapter adapter;
    private liveEntryOpenHelper dbAdapter;
    private String theDate;
	private int result;
	private Calendar calT;
	private String alertMessage = "";
	private String alertTitle = "";
    
    //Constants
    private final Context ct = this;
	private static final String colfruits = "fruitsAndVeggies";
	private static final String colwater = "waterBottles";
	private static final String colscreen = "screenTime";
	private static final String colexercise = "exerciseTime";
	private static final String colsleep = "sleepTime";
	private static final String colweight = "weight";
	private static final int EDITDIALOG = 100;
	private final int DELETEDIALOG = 101;
    private final int todayBtnId = 1;
    private final int group1Id = 2;
	
	/***************************************************************
	 Purpose: Called when the historyCalendar activity is created
	 Input: Nothing
	 Output: Boolean variable
	 **************************************************************/
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.historycalendar);
	    //set month, alert message, and alert title to before orientation change
	    DataHolder dh = new DataHolder();
	    dh = (DataHolder)getLastNonConfigurationInstance();
	    if (dh != null) {
	    	month = dh.calData;
		    alertMessage = dh.messageData;
		    alertTitle = dh.titleData;	
		    calT = dh.calT;
		    theDate = dh.theDate;
	    }
 	    
	    if (month == null)
	    	month = Calendar.getInstance();
	    Cursor cur;
	    dbAdapter = new liveEntryOpenHelper(this);
	    cur = dbAdapter.getAllEntries();

	    adapter = new CalendarAdapter(this, month, cur);
	    
	    //sets the adapter of the gridview to a CalendarAdapter and sets the Column Width
	    GridView gridview = (GridView) findViewById(R.id.historyCalendarGridview);
	    gridview.setAdapter(adapter);
	    gridview.setColumnWidth(getWindowManager().getDefaultDisplay().getWidth()/7);
	    
	    //Sets the title of the Calendar to the current month selected
	    TextView title  = (TextView) findViewById(R.id.HistoryCalendarTitle);
	    title.setText(android.text.format.DateFormat.format("MMMM yyyy", month));
	    	    
	    //used for the "previous calendar month
	    TextView previous  = (TextView) findViewById(R.id.historyCalendarPrevious);
	    previous.setOnClickListener(new OnClickListener() {			
			public void onClick(View v) {
				if(month.get(Calendar.MONTH) == month.getActualMinimum(Calendar.MONTH)) {				
					month.set((month.get(Calendar.YEAR) - 1), month.getActualMaximum(Calendar.MONTH), 1);
				} else {
					month.set(Calendar.MONTH, month.get(Calendar.MONTH) - 1);
				}
				refreshCalendar();
			}
		});
	    
	    //Used for the "next" calendar month event
	    TextView next  = (TextView) findViewById(R.id.historyCalendarNext);
	    next.setOnClickListener(new OnClickListener() {			
			public void onClick(View v) {
				if(month.get(Calendar.MONTH) == month.getActualMaximum(Calendar.MONTH)) {				
					month.set((month.get(Calendar.YEAR) + 1),month.getActualMinimum(Calendar.MONTH), 1);
				} else {
					month.set(Calendar.MONTH, month.get(Calendar.MONTH) + 1);
				}
				refreshCalendar();
				
			}
		});
	    
	    //Occurs when calendar is clicked on, ability to add data or edit data based
	    //on the day that is clicked
		gridview.setOnItemClickListener(new OnItemClickListener() {
		    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		    	TextView date = (TextView)v.findViewById(R.id.historyCalendarItemTextView);
		        if(date instanceof TextView && !date.getText().equals("")) {
		        	String day = date.getText().toString();
		        	calT = (Calendar)month.clone();
		        	theDate = Integer.toString(month.get(Calendar.MONTH) + 1) + "/" + 
	    		    		day + "/" + month.get(Calendar.YEAR);
		        	calT.set(Calendar.DATE, Integer.parseInt(day));
		        	//No data for that date, user will be able to add a new entry
		        	if (!dbAdapter.dataExistsByDate(calT)) 
		        	{
		        		alertTitle = "Add Entry - " + theDate;
		        		alertMessage = "Are you sure you want to add an entry on " + theDate + "?";
		        	}
		        	else //data exists for the date selected, able to edit data
		        	{
			        	Cursor data;
			        	data = dbAdapter.getEntryByDate(calT);
			        	alertMessage = "Fruits and Veggies: " + data.getString(data.getColumnIndex(colfruits)) + "\n";
			        	alertMessage += "Bottle of Water: " + data.getString(data.getColumnIndex(colwater)) + "\n";
			        	alertMessage += "Hours of Screen Time: " + data.getString(data.getColumnIndex(colscreen)) + "\n";
			        	alertMessage += "Hours of Exercise: " + data.getString(data.getColumnIndex(colexercise)) + "\n";
			        	alertMessage += "Hours of Sleep: " + data.getString(data.getColumnIndex(colsleep)) + "\n";
			        	if (data.getInt(data.getColumnIndex(colweight)) != -1)
			        		alertMessage += "Weight: " + data.getString(data.getColumnIndex(colweight)) + "\n";
			        	else
			        		alertMessage += "Weight: None entered\n";
		        		alertTitle = "Edit Entry - " + theDate;
		        		alertMessage += "Are you sure you want to edit the entry on " + theDate + "?";
		        	}
	        		//Show edit item Alert Dialog
	        		showDialog(EDITDIALOG);
		        	return;
		        }		        
		    }
		});
		
		 //listener for long item click (delete item), if no entry on the day clicked,
		//the user is notified that there is no entry to delete
        gridview.setOnItemLongClickListener(new OnItemLongClickListener() {
        	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        		TextView date = (TextView)view.findViewById(R.id.historyCalendarItemTextView);
		        if(date instanceof TextView && !date.getText().equals("")) {
		        	String day = date.getText().toString();
		        	calT = (Calendar)month.clone();
		        	theDate = Integer.toString(month.get(Calendar.MONTH) + 1) + "/" + 
	    		    		day + "/" + month.get(Calendar.YEAR);
		        	calT.set(Calendar.DATE, Integer.parseInt(day));
		        	//No entry to delete
		        	if (!dbAdapter.dataExistsByDate(calT))
		        	{
		        		Toast.makeText(ct, "No entry to delete on " + theDate + ".", Toast.LENGTH_SHORT).show();
		        		return true;
		        	}
		        	else //entry summary shown
		        	{
		        		Cursor data;
			        	data = dbAdapter.getEntryByDate(calT);
			        	alertMessage = "Fruits and Veggies: " + data.getString(data.getColumnIndex(colfruits)) + "\n";
			        	alertMessage += "Bottle of Water: " + data.getString(data.getColumnIndex(colwater)) + "\n";
			        	alertMessage += "Hours of Screen Time: " + data.getString(data.getColumnIndex(colscreen)) + "\n";
			        	alertMessage += "Hours of Exercise: " + data.getString(data.getColumnIndex(colexercise)) + "\n";
			        	alertMessage += "Hours of Sleep: " + data.getString(data.getColumnIndex(colsleep)) + "\n";
			        	if (data.getInt(data.getColumnIndex(colweight)) != -1)
			        		alertMessage += "Weight: " + data.getString(data.getColumnIndex(colweight)) + "\n";
			        	else
			        		alertMessage += "Weight: None entered\n";
		        		alertTitle = "Delete Entry - " + theDate;
		        		alertMessage = "Are you sure you want to delete the entry on " + theDate + "?";
		        	}
		        	//Show delete item Alert Dialog
		        	showDialog(DELETEDIALOG);
		        }
	        	return true;
        	}
        });
	}
	/***************************************************************
	 Purpose: Refreshes the calendar after data has been edited
	 Input: Nothing
	 Output: Nothing
	 **************************************************************/
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			refreshCalendar();
		}
	}
	/***************************************************************
	 Purpose: Creates the Options Menu with one item ("Go to Today")
	 Input: Nothing
	 Output: Boolean Variable
	 **************************************************************/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(group1Id, todayBtnId, todayBtnId, "Go To Today");
		return super.onCreateOptionsMenu(menu);
	}
	
	/***************************************************************
	 Purpose: Called when user selects a menu option (Go to Today)
	 Input: Nothing
	 Output: Boolean variable
	 **************************************************************/
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == todayBtnId){
			month = Calendar.getInstance();
			this.onCreate(null);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/***************************************************************
	 Purpose: Retains the current month selected on a rotation change
	 Input: Nothing
	 Output: Calendar month
	 **************************************************************/
	@Override
	public Object onRetainNonConfigurationInstance() {
		DataHolder dh = new DataHolder();
		dh.calData = month;
		dh.messageData = alertMessage;
		dh.titleData = alertTitle;
		dh.calT = calT;
		dh.theDate = theDate;
		return dh;
	}

	/***************************************************************
	 Purpose: Closes the dbAdapter onStop()
	 Input: Nothing
	 Output: Nothing
	 **************************************************************/
	@Override
	protected void onStop() {
		if (dbAdapter != null)
			dbAdapter.close();
		super.onStop();
	}

	/****************************************************************
	 Purpose: Refreshes the days based on month and sets the title to 
	 the month now selected
	 Returns: Nothing
	 Input: Nothing
	 ****************************************************************/
	public void refreshCalendar()
	{
		Cursor cur;
	    dbAdapter = new liveEntryOpenHelper(this);
	    cur = dbAdapter.getAllEntries();
	    adapter = new CalendarAdapter(this, month, cur);
	    GridView gridview = (GridView) findViewById(R.id.historyCalendarGridview);
	    gridview.setAdapter(adapter);
	    gridview.setColumnWidth(getWindowManager().getDefaultDisplay().getWidth()/7);
	    TextView title  = (TextView) findViewById(R.id.HistoryCalendarTitle);
		adapter.refreshDays();
		adapter.notifyDataSetChanged();				
		title.setText(android.text.format.DateFormat.format("MMMM yyyy", month));
	}
	
	/****************************************************************
	 Purpose: Creates the dialog message to either edit data or delete
	 data
	 Returns: Dialog
	 Input: Dialog ID
	 ****************************************************************/
	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		AlertDialog.Builder builder;
		switch (id) {
		case DELETEDIALOG:
			builder = new AlertDialog.Builder(this);
			builder.setMessage(alertMessage);
			builder.setTitle(alertTitle);
			builder.setIcon(R.drawable.delete);
			builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {			
        		public void onClick(DialogInterface dialog, int which) {
        			dbAdapter = new liveEntryOpenHelper(getApplicationContext());
        			if (dbAdapter.deleteDay(calT) == 1) {
        				Toast.makeText(ct, "Successfully deleted entry!", Toast.LENGTH_SHORT).show();
        				refreshCalendar();
        			}
        			else {
        				Toast.makeText(ct, "Unsuccessful delete, please try again...", Toast.LENGTH_SHORT).show();
        			}
        			removeDialog(DELETEDIALOG);	
        		} });
			builder.setNegativeButton("No", new DialogInterface.OnClickListener() {			
    			public void onClick(DialogInterface dialog, int which) {
    				removeDialog(DELETEDIALOG);			
			} });
			dialog = builder.create();
			break;
		case EDITDIALOG:
			builder = new AlertDialog.Builder(this);
			builder.setMessage(alertMessage);
			builder.setTitle(alertTitle);
			builder.setIcon(R.drawable.edit);
			builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {			
    			public void onClick(DialogInterface dialog, int which) {	    				
    				Intent i = new Intent(ct, entry.class);
                	i.putExtra("date", theDate);
                	startActivityForResult(i, result);
                	removeDialog(EDITDIALOG);
			} });
    		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {			
    			public void onClick(DialogInterface dialog, int which) {
    				removeDialog(EDITDIALOG);
			} });
			dialog = builder.create();
			break;
			default:
				dialog = null;
		}
		return dialog;
	}
	
	/*********************************************************************
	 Purpose: This class holds Alert Dialog message and title as well
	 as the current calendar, it is used during orientation changes to
	 save the data.
	 Input: Nothing
	 Output: Nothing
	 *********************************************************************/
	static class DataHolder {
		Calendar calData;
		Calendar calT;
		String messageData;
		String titleData;
		String theDate;
	}
}