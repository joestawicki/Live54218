/****************************************************
This class is the adapter for the history (detailed) view
it gets data from the SQLite database
****************************************************/

package edu.snc.live54218;

import java.util.ArrayList;
import java.util.Calendar;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CustomBaseAdapter extends BaseAdapter {
	//Variables
	private static ArrayList<liveEntry> liveEntryArray;	
	private LayoutInflater entryInflator;
	
	/*********************************************************************
	 Purpose: Constructor for the CustomBaseAdapter
	 Input: Nothing
	 Output: Nothing
	 *********************************************************************/
	public CustomBaseAdapter (Context context, ArrayList<liveEntry> liveEntries) {
		liveEntryArray = liveEntries;
		entryInflator = LayoutInflater.from(context);
	}
	
	/*********************************************************************
	 Purpose: Returns the size of the live entry array
	 Input: Nothing
	 Output: liveEntryArray size (Integer)
	 *********************************************************************/
	public int getCount() {
		return liveEntryArray.size();
	}
	
	/*********************************************************************
	 Purpose: Returns the live entry array at a given position
	 Input: Integer position
	 Output: The liveEntry at the given position
	 *********************************************************************/
	public Object getItem(int position) {
		return liveEntryArray.get(position);
	}
	
	/*********************************************************************
	 Purpose: This method returns the id of the item (which in this case
	 is the same as the position, needed to extend Base Adapter)
	 Input: position
	 Output: position (Integer)
	 *********************************************************************/
	public long getItemId(int position) {
		return position;
	}

	/*********************************************************************
	 Purpose: This method populates the history row items with data from 
	 the live entry array
	 Input: Nothing
	 Output: Nothing
	 *********************************************************************/
	//Populates the history row items with data from the live entry array
	public View getView(int position, View convertView, ViewGroup parent) {
		EntryViewHolder holder;
		String month;
		String day;
		String year;
		String weight;
		if (convertView == null) {
			convertView = entryInflator.inflate(R.layout.historyrowitem, null);
			holder = new EntryViewHolder();
			holder.txtDate = (TextView)convertView.findViewById(R.id.historyDateTV);
			holder.txtExercise = (TextView)convertView.findViewById(R.id.historyExerciseTV);
			holder.txtFruits = (TextView)convertView.findViewById(R.id.historyFruitsTV);
			holder.txtScreen = (TextView)convertView.findViewById(R.id.historyScreenTV);
			holder.txtSleep = (TextView)convertView.findViewById(R.id.historySleepTV);
			holder.txtWater = (TextView)convertView.findViewById(R.id.historyWaterTV);
			holder.txtWeight = (TextView)convertView.findViewById(R.id.historyWeightTV);
			convertView.setTag(holder);
		} else {
			holder = (EntryViewHolder)convertView.getTag();
		}
		
		//No data present
		if (Float.toString(liveEntryArray.get(position).getExerciseTime()) == Float.toString(0) &&
				Float.toString(liveEntryArray.get(position).getFruitsAndVeggies()) == Float.toString(0) &&
				Float.toString(liveEntryArray.get(position).getScreenTime()) == Float.toString(0) && 
				Float.toString(liveEntryArray.get(position).getSleepTime()) == Float.toString(0) &&
				Float.toString(liveEntryArray.get(position).getWater()) == Float.toString(0) &&
				(Integer.toString(liveEntryArray.get(position).getWeight()) == Integer.toString(0) ||
						Integer.toString(liveEntryArray.get(position).getWeight()) == Integer.toString(-1))) {
			holder.txtDate.setText("No Data");
			holder.txtExercise.setText("");
			holder.txtFruits.setText("");
			holder.txtScreen.setText("");
			holder.txtSleep.setText("");
			holder.txtWater.setText("");
			holder.txtWeight.setText("");	
			
			//Data is present
		} else	{
			month = Integer.toString(liveEntryArray.get(position).getDate().get(Calendar.MONTH) + 1);
			day = Integer.toString(liveEntryArray.get(position).getDate().get(Calendar.DATE));
			year = Integer.toString(liveEntryArray.get(position).getDate().get(Calendar.YEAR));
			holder.txtDate.setText("Date: " + month + "/" + day + "/" + year);
			holder.txtExercise.setText("Exercise Time: " + Float.toString(liveEntryArray.get(position).getExerciseTime()));
			holder.txtFruits.setText("Fruits and Veggies: " + Float.toString(liveEntryArray.get(position).getFruitsAndVeggies()));
			holder.txtScreen.setText("Screen Time: " + Float.toString(liveEntryArray.get(position).getScreenTime()));
			holder.txtSleep.setText("Sleep Time: " + Float.toString(liveEntryArray.get(position).getSleepTime()));
			holder.txtWater.setText("Bottles of Water: " + Float.toString(liveEntryArray.get(position).getWater()));
			if (liveEntryArray.get(position).getWeight() == -1)
				weight = "None entered";
			else
				weight = Integer.toString(liveEntryArray.get(position).getWeight());
			holder.txtWeight.setText("Weight: " + weight);	
			//Complete entry
			if (liveEntryArray.get(position).getFruitsAndVeggies() >= (float)5 && liveEntryArray.get(position).getWater() >= (float)4 &&
					liveEntryArray.get(position).getScreenTime() <= (float)2 && liveEntryArray.get(position).getExerciseTime() >= (float)1 &&
					liveEntryArray.get(position).getSleepTime() >= (float)8)
				convertView.setBackgroundColor(Color.rgb(153, 255, 153));
			else
				convertView.setBackgroundColor(Color.rgb(255, 153, 153));
		}
		return convertView;
	}
	
	/*********************************************************************
	 Purpose: This class holds the live entry data
	 Input: Nothing
	 Output: Nothing
	 *********************************************************************/
	static class EntryViewHolder {
		TextView txtDate;
		TextView txtFruits;
		TextView txtWater;
		TextView txtScreen;
		TextView txtExercise;
		TextView txtSleep;
		TextView txtWeight;
	}
}