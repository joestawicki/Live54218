/****************************************************
Adapted from Lauri Nevala's example
This class is used as an adapter for the gridView in 
the historyCalendar.java file
****************************************************/
package edu.snc.live54218;

import java.util.Calendar;
import java.util.HashMap;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CalendarAdapter extends BaseAdapter {

	//Variables
	private Context mContext;
    private Calendar month;
    private String[] days;
    private int height;
    private Cursor eData;
    private HashMap<String, Integer> entryHash; 
    private String tempCal;
    
    //Constants
    private static final String coldate = "date";  
    private static final String colcomplete = "complete";
    
    /*********************************************************************
	 Purpose: Constructor for the CalendarAdapter
	 Input: Nothing
	 Output: Nothing
	 *********************************************************************/
    public CalendarAdapter(Context context, Calendar monthCalendar, Cursor entryData) {
    	month = monthCalendar;
    	mContext = context;
        month.set(Calendar.DAY_OF_MONTH, 1);
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        height = metrics.heightPixels;
        eData = entryData;
        refreshDays();
        refreshData();
    }

    /*********************************************************************
	 Purpose: Returns the length of the days array
	 Input: Nothing
	 Output: length of days array (Integer)
	 *********************************************************************/
    public int getCount() {
        return days.length;
    }

    /*********************************************************************
	 Purpose: No real purpose (have to implement to extend Base Adapter)
	 Input: Integer position
	 Output: null
	 *********************************************************************/
    public Object getItem(int position) {
        return null;
    }

    /*********************************************************************
	 Purpose: No real purpose (have to implement to extend Base Adapter)
	 Input: Integer position
	 Output: 0
	 *********************************************************************/
    public long getItemId(int position) {
        return 0;
    }

    /*********************************************************************
	 Purpose: Creates a new view for each item referenced by the Adapter
	 Input: Nothing
	 Output: Nothing
	 *********************************************************************/
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
    	TextView dayView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
        	LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.historycalendaritem, null);        	
        }
        dayView = (TextView)v.findViewById(R.id.historyCalendarItemTextView);
        
        //The seven days of the week headings (Sun, Mon, Tues, Wed...)
        if(position <= 6) {
        	dayView.setHeight(height/16);
        	dayView.setBackgroundColor(Color.rgb(49, 128, 187));
        	dayView.setTextColor(Color.rgb(0, 0, 0));
        	dayView.setText(days[position]);
        	dayView.setGravity(Gravity.CENTER);
        	dayView.setPadding(0, 0, 0, 0);
        }
        //Day is today, then text is blue
        else if(month.get(Calendar.MONTH) == Calendar.getInstance().get(Calendar.MONTH) && 
        		days[position] == Integer.toString(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) &&
        		month.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR)) {
        	dayView.setText(days[position]);
        	dayView.setBackgroundColor(Color.WHITE);
        	dayView.setHeight(height/9);
        	dayView.setTextColor(Color.BLUE);
        }
        //Days are grey if not part of current month
        else if(days[position] == "") {
        	dayView.setClickable(false);
        	dayView.setText(days[position]);
        	dayView.setBackgroundColor(Color.GRAY);
        	dayView.setHeight(height/9);
        }
        //Nothing special
        else {
        	dayView.setText(days[position]);
        	dayView.setBackgroundColor(Color.WHITE);
        	dayView.setHeight(height/9);
        	dayView.setTextColor(Color.BLACK);
        }
        //If day in the HashMap, the background of the day is updated
        //with green or red based on if that day is complete or not
        if (days[position] != "" && isNumeric(days[position])) {
            tempCal = Integer.toString(month.get(Calendar.MONTH)) + "/" + days[position] + 
            		"/" + Integer.toString(month.get(Calendar.YEAR));
            if (entryHash.containsKey(tempCal))
            {
            	if (entryHash.get(tempCal) == 1) {
            		dayView.setBackgroundResource(R.color.green);
            	}
            	else {
            		dayView.setBackgroundResource(R.color.red);
            	}
            }
        }        
        return v;
    }
    
    /****************************************************
     Purpose: Refreshes the days array based on the month selected
     Input: Nothing
     Output: Nothing
     ****************************************************/
    public void refreshDays()
    {
    	int lastDay = month.getActualMaximum(Calendar.DAY_OF_MONTH);
        int firstDay = month.get(Calendar.DAY_OF_WEEK);
       	days = new String[49];
        
    	//Set days
       	days[0] = "Sun";
       	days[1] = "Mon";
       	days[2] = "Tues";
       	days[3] = "Wed";
       	days[4] = "Thurs";
       	days[5] = "Fri";
       	days[6] = "Sat";
       	
        int j = 7;
        // populate empty days before first real day
        if(firstDay > 1) {
	        for(j = 7; j < firstDay + 7; j++) {
	        	days[j] = "";
	        }
        }
	    else {
	    	days[7] = "";
	    	j = 8;
	    }
        
        // populate days
        int dayNumber = 1;
        int i;
        for(i = j - 1; i < lastDay + j - 1; i++) {
        	days[i] = Integer.toString(dayNumber);
        	dayNumber++;
        }
        
        //ending blank days
        for(j = i; j < days.length; j++) {
        	days[j] = "";
        }       
    }
    
    /****************************************************
    Purpose: Populates all of the data in the HashMap with
    data from the SQLite database
    Input: Nothing
    Output: Nothing
    ****************************************************/
    public void refreshData() {
    	entryHash = new HashMap<String, Integer>();
    	String cal;
    	int bit;
    	if (eData.getCount() > 0) {
			eData.moveToFirst();
			while (!eData.isAfterLast()) {
				cal = eData.getString(eData.getColumnIndex(coldate));
				bit = eData.getInt(eData.getColumnIndex(colcomplete));
				entryHash.put(cal, bit);
				eData.moveToNext();
			}
    	}
    }
    
    /****************************************************
    Purpose: Returns whether a string is numeric or not
    Input: String
    Output: Boolean variable whether or not string is numeric
    or not
    ****************************************************/
    public static boolean isNumeric(String str)  
    {  
      try  
      {  
        @SuppressWarnings("unused")
		double d = Float.parseFloat(str);  
      }  
      catch(NumberFormatException nfe)  
      {  
        return false;  
      }  
      return true;  
    }
 
}