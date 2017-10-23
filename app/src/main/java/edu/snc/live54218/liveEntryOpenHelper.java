/****************************************************
This class is used to read and write data from the
SQLite database
****************************************************/
package edu.snc.live54218;

import java.util.Calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
    
public class liveEntryOpenHelper extends SQLiteOpenHelper {
	
   //Constants
   private static final String DBName = "liveEntryDatabase";
   private static final String entryTable = "Entries";
   private static final String colID = "entryID";
   private static final String colfruits = "fruitsAndVeggies";
   private static final String colwater = "waterBottles";
   private static final String colscreen = "screenTime";
   private static final String colexercise = "exerciseTime";
   private static final String colsleep = "sleepTime";
   private static final String colweight = "weight";
   private static final String coldate = "date";   
   private static final String colcomplete = "complete";      
	
	/*********************************************************************
	 Purpose: This is the liveEntryOpenHelper constructor
	 Input: Nothing
	 Output: Nothing
	 *********************************************************************/
   liveEntryOpenHelper(Context context) {
	 super(context, DBName, null, 2);
   }
	  
	/*********************************************************************
	 Purpose: This method creates the SQLite Database
	 Input: SQLiteDatabase object
	 Output: Nothing
	 *********************************************************************/
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + entryTable + " (" + colID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				colfruits + " FLOAT, " + colwater + " FLOAT, " + colscreen + " FLOAT, " + colexercise + " FLOAT, " +
				colsleep + " FLOAT, " + colweight + " INT, " + coldate + " VARCHAR(30), " + colcomplete + " BIT)");
	}

	/*********************************************************************
	 Purpose: This method upgrades the SQLite Database (required to extend
	 SQLiteOpenHelper)
	 Input: SQLiteDatabase
	 Output: Nothing
	 *********************************************************************/
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w("liveEntryOpenHelper", "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
		try {
		db.execSQL("DROP TABLE IF EXISTS " + entryTable);
		onCreate(db);
		
		} catch (SQLException e) {
			Log.e("liveEntryOpenHelper", e.getLocalizedMessage().toString());
		}
	}
	
	/*********************************************************************
	 Purpose: This method adds an entry to the database
	 Input: live entry to add to database
	 Output: Boolean variables whether or not the entry has been added or not
	 *********************************************************************/
	public boolean AddEntry(liveEntry entry)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues cv = new ContentValues();
		cv.put(colfruits, entry.getFruitsAndVeggies());
		cv.put(colwater, entry.getWater());
		cv.put(colscreen, entry.getScreenTime());
		cv.put(colexercise, entry.getExerciseTime());
		cv.put(colsleep, entry.getSleepTime());
		cv.put(colweight, entry.getWeight());
		String theDate = Integer.toString(entry.getDate().get(Calendar.MONTH)) + "/" + Integer.toString(entry.getDate().get(Calendar.DATE)) + "/" +
				Integer.toString(entry.getDate().get(Calendar.YEAR));	   
		cv.put(coldate, theDate);
		
		//Complete Bit
		int complete;
		if (entry.getFruitsAndVeggies() >= (float)5 && entry.getWater() >= (float)4 &&
				entry.getScreenTime() <= (float)2 && entry.getExerciseTime() >= (float)1 &&
				entry.getSleepTime() >= (float)8)
			complete = 1;
		else
			complete = 0;
		cv.put(colcomplete, complete);
			
		
		try {
		db.insert(entryTable, colweight, cv);
		} catch (Exception e) {
			Log.e("liveEntryOpenHelper", e.getLocalizedMessage().toString());
			return false;
		}
		return true;
	}
	
	/*********************************************************************
	 Purpose: This method returns a cursor with all of the entries in the database
	 Input: Nothing
	 Output: Cursor with all entries in the SQLite database
	 *********************************************************************/
	public Cursor getAllEntries()
	{
		Cursor cur;
		SQLiteDatabase db;
		try {
		db = this.getReadableDatabase();
		
		cur = db.rawQuery("SELECT * FROM " + entryTable + " ORDER BY " + coldate + " ASC", null);
		
		} catch (Exception e) {
			Log.e("getAllEntries", e.getLocalizedMessage().toString());
			return null;
		}
		return cur;
	}
	
	/*********************************************************************
	 Purpose: This method updates an entry currently in the database
	 Input: live entry to update (since there can only be one entry per date,
	 an entries date will be unique)
	 Output: Integer which is the number of entries updates (hopefully it is 1)
	 *********************************************************************/
	public int UpdateEntry(liveEntry entry)
	{
		int rows;
		SQLiteDatabase db = null;
		try {
		db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(colfruits, entry.getFruitsAndVeggies());
		cv.put(colwater, entry.getWater());
		cv.put(colscreen, entry.getScreenTime());
		cv.put(colexercise, entry.getExerciseTime());
		cv.put(colsleep, entry.getSleepTime());
		cv.put(colweight, entry.getWeight());
		String theDate = Integer.toString(entry.getDate().get(Calendar.MONTH)) + "/" + Integer.toString(entry.getDate().get(Calendar.DATE)) + "/" +
				Integer.toString(entry.getDate().get(Calendar.YEAR));	   
		cv.put(coldate, theDate);
		
		
		//Complete Bit
		int complete;
		if (entry.getFruitsAndVeggies() >= (float)5 && entry.getWater() >= (float)4 &&
				entry.getScreenTime() <= (float)2 && entry.getExerciseTime() >= (float)1 &&
				entry.getSleepTime() >= (float)8)
			complete = 1;
		else
			complete = 0;
		cv.put(colcomplete, complete);
		
		//update row where date column like the entry date
		rows = db.update(entryTable, cv, coldate + " LIKE ? ", new String []{theDate});
		} catch (Exception e) {
			Log.e("updateEntryByID", e.getLocalizedMessage().toString());
			return 0;
		}
		return rows;
	}
	
	/*********************************************************************
	 Purpose: This method deletes an entry in the database by the entry date
	 (since each date is unique)
	 Input: live entry date to delete from database
	 Output: Number of rows deleted from database (hopefully will be 1)
	 *********************************************************************/
	public int deleteDay(Calendar date)
	{
		String theDate;
		SQLiteDatabase db = null;
		int count;
		try {
		db = this.getWritableDatabase();
		theDate = Integer.toString(date.get(Calendar.MONTH)) + "/" + Integer.toString(date.get(Calendar.DATE)) + "/" +
				Integer.toString(date.get(Calendar.YEAR));  
		count = db.delete(entryTable, coldate + " LIKE ? ", new String[] {theDate});
		
		} catch (Exception e) {
			Log.e("deleteDay", e.getLocalizedMessage().toString());
			return 0;
		}
		//return number of rows deleted
		return count;
	}
	
	/*********************************************************************
	 Purpose: This method returns the number of days marked as "complete" in the database
	 Input: Nothing
	 Output: Integer which is the number of days "complete" in the database
	 *********************************************************************/
	public int getCompleteDays()
	{
		Cursor cur;
		SQLiteDatabase db = null;
		int count;
		try {
		db = this.getReadableDatabase();
		cur = db.rawQuery("SELECT * FROM " + entryTable + " WHERE " + colcomplete + " = 1 ", null);

		cur.moveToFirst();
		} catch (Exception e) {
			Log.e("getCompleteDays", e.getLocalizedMessage().toString());
			return -1;
		}
		count = cur.getCount();
		//return count of the cursor
		return count;
	}

	/*********************************************************************
	 Purpose: This method checks to see whether or not a particular date
	 has data associated with it in the database
	 Input: Calendar date of the live entry
	 Output: Boolean variables whether or not the entry with that date 
	 is in the database
	 *********************************************************************/
	public boolean dataExistsByDate(Calendar date)
	{
		String theDate;
		Cursor cur;
		SQLiteDatabase db = null;
		int count;
		theDate = Integer.toString(date.get(Calendar.MONTH)) + "/" + Integer.toString(date.get(Calendar.DATE)) + "/" +
				Integer.toString(date.get(Calendar.YEAR));
		
		try {
		db = this.getReadableDatabase();
		cur = db.rawQuery("SELECT * FROM " + entryTable + " WHERE " + coldate + " LIKE " + theDate, null);
		cur.moveToFirst();
		count = cur.getCount();
		} catch (Exception e) {
			Log.e("getEntryIdByDate", e.getLocalizedMessage().toString());
			return false;
		}
		if (count > 0) //a row is returned, there is data for that date		
			return true;
		else //no data for that date
			return false;
	}
	
	/*********************************************************************
	 Purpose: This method returns a cursor with data of a particular date
	 Input: Calendar date of live entry
	 Output: Cursor which contains the live entry data of that date
	 *********************************************************************/
	public Cursor getEntryByDate(Calendar date)
	{
		String tempDate;
		Cursor cur;
		SQLiteDatabase db = null;
		tempDate = Integer.toString(date.get(Calendar.MONTH)) + "/" + Integer.toString(date.get(Calendar.DATE)) + "/" +
				Integer.toString(date.get(Calendar.YEAR));		
		try {
		db = this.getReadableDatabase();
		cur = db.rawQuery("SELECT * FROM " + entryTable + " WHERE " + coldate + " LIKE " + tempDate, null);
		cur.moveToFirst();
		} catch (Exception e) {
			Log.e("getEntryIdByDate", e.getLocalizedMessage().toString());
			return null;
		}
		return cur;
	}
	
}
