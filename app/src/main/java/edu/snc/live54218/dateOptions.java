/****************************************************
This class shows the three different date options for
entering data (today, past data, or custom date)
****************************************************/

package edu.snc.live54218;

import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;

public class dateOptions extends Activity {
	
	//Variables
	private DatePickerDialog dialog;
	private int mMonth;
	private int mDay;
	private int mYear;
	private int result;
	
	/*********************************************************************
	 Purpose: Called when activity is first created
	 Input: Nothing
	 Output: Nothing
	 *********************************************************************/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 setContentView(R.layout.dateoptions);
	}
	
	/*********************************************************************
	 Purpose: If result of calling activities being finished is OK, then finish this activity
	 (go back to the main screen)
	 Input: Nothing
	 Output: Nothing
	 *********************************************************************/
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			this.finish();
		}
	}
	
	/*********************************************************************
	 Purpose: Enter today's data (starts entry activity with today's date)
	 Input: Nothing
	 Output: Nothing
	 *********************************************************************/
	public void currentDayClick(View view){
    	//Starts entry.java activity
    	Intent i = new Intent(this, entry.class);
    	Calendar ci = Calendar.getInstance();
    	String today = Integer.toString((ci.get(Calendar.MONTH) + 1)) + "/" + Integer.toString(ci.get(Calendar.DAY_OF_MONTH)) + "/" + Integer.toString(ci.get(Calendar.YEAR));
    	i.putExtra("date", today);
    	startActivityForResult(i, result);
    }
	
	/*********************************************************************
	 Purpose: Date Picker on date set listener for custom date entry (starts
	 entry activity with date picker's date)
	 Input: Nothing
	 Output: Nothing
	 *********************************************************************/
	private DatePickerDialog.OnDateSetListener PickDate =		
		    new DatePickerDialog.OnDateSetListener() {
				public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		    	mMonth = monthOfYear;
		    	mDay = dayOfMonth;
		    	mYear = year;   
		    	Intent i = new Intent(getApplicationContext(), entry.class);
	    		String date = Integer.toString(mMonth + 1) + "/" + Integer.toString(mDay) + "/" + Integer.toString(mYear);
	    		i.putExtra("date", date);
	    		startActivityForResult(i, result);
		    }
		};
		
	/*********************************************************************
	 Purpose: Able to update past data (opens the history activity so 
	 the user can select the date to update or delete)
	 Input: Nothing
	 Output: Nothing
	 *********************************************************************/
	public void pastDataClick(View view){
		//starts history.java activity
		Intent i = new Intent(this, history.class);
		startActivityForResult(i, result);
	}
	
	/*********************************************************************
	 Purpose: Opens up a date picker dialog so the user can select a date 
	 to add/update
	 Input: Nothing
	 Output: Nothing
	 *********************************************************************/
	public void customDateClick(View view){
		Calendar ci = Calendar.getInstance();
		//new dialog date picker to select custom date
		dialog = new DatePickerDialog(this, PickDate, ci.get(Calendar.YEAR), ci.get(Calendar.MONTH), ci.get(Calendar.DATE));
		dialog.show();
	}
}
