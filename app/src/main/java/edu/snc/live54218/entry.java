/****************************************************
This class shows an entry screen in which a user is able
to enter data for a day or update data.  The date
is read only with the other entry fields being editable.
The entry fields are populated with past data if past
entries exist for that day.
****************************************************/
package edu.snc.live54218;

import java.util.Calendar;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class entry extends Activity {

	/** Called when the activity is first created. */
	
	//Constants
	private static final String colfruits = "fruitsAndVeggies";
	private static final String colwater = "waterBottles";
	private static final String colscreen = "screenTime";
	private static final String colexercise = "exerciseTime";
	private static final String colsleep = "sleepTime";
	private static final String colweight = "weight";
    private final int clearBtnId = 1;
    private final int group1Id = 2;
	
	//Variables
	private liveEntry entry;
	private liveEntryOpenHelper entryDB;
	private EditText text;
	private EditText fruitView;
	private EditText waterView;
	private EditText screenView;
	private EditText exerciseView;
	private EditText sleepView;
	private TextView dateText;
	private boolean hasPastData = false;
	
	/*********************************************************************
	 Purpose: Called when activity is first created
	 Input: Nothing
	 Output: Nothing
	 *********************************************************************/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry);
        
        //add all the listeners for the Edit Texts
        addForFruit();
        addForWater();
        addForScreen();
        addForExercise();
        addForSleep();
        
        //create a bundle to get the data that was passed from the date options screen
        Bundle b = getIntent().getExtras();
        String date;
        if(b != null){
        	//set the date text to be whatever was passed
        	date = b.getString("date");
            dateText = (TextView)findViewById(R.id.editTextDate);
            dateText.setText(date);
            
            //Check if past data
            entryDB = new liveEntryOpenHelper(this);
            Calendar cal = Calendar.getInstance();
            String[] tempDates;
            cal.clear();
    		tempDates = date.split("/");
    		cal.set(Calendar.MONTH, (Integer.parseInt(tempDates[0]) - 1));
    		cal.set(Calendar.DATE, (Integer.parseInt(tempDates[1])));
    		cal.set(Calendar.YEAR, (Integer.parseInt(tempDates[2])));
            hasPastData = entryDB.dataExistsByDate(cal);
            if (hasPastData) //populate data if there is past data
            {
            	Cursor cur;
            	cur = entryDB.getEntryByDate(cal);
            	//fruits and veggies
            	text = (EditText)findViewById(R.id.editTextFruit);
            	text.setText(cur.getString(cur.getColumnIndex(colfruits)));
            	//water
            	text = (EditText)findViewById(R.id.editTextWater);
            	text.setText(cur.getString(cur.getColumnIndex(colwater)));
            	//screen
            	text = (EditText)findViewById(R.id.editTextScreen);
            	text.setText(cur.getString(cur.getColumnIndex(colscreen)));
            	//sleep
            	text = (EditText)findViewById(R.id.editTextSleep);
            	text.setText(cur.getString(cur.getColumnIndex(colsleep)));
            	//exercise
            	text = (EditText)findViewById(R.id.editTextExercise);
            	text.setText(cur.getString(cur.getColumnIndex(colexercise)));
            	//weight
            	text = (EditText)findViewById(R.id.editTextWeight);
            	if (Integer.parseInt(cur.getString(cur.getColumnIndex(colweight))) == -1)
            		text.setText("");
            	else
            		text.setText(cur.getString(cur.getColumnIndex(colweight)));
            	Button btn;
            	btn = (Button)findViewById(R.id.entrySaveButton);
            	btn.setText("Update54218");
            }
            else
            {
            	Button btn;
            	btn = (Button)findViewById(R.id.entrySaveButton);
            	btn.setText("Save54218");
            }
        }
        
    }
    
    /*********************************************************************
	 Purpose: Called when activity is stopped (closes the entry database
	 if it is open)
	 Input: Nothing
	 Output: Nothing
	 *********************************************************************/
    @Override
	protected void onStop() {
		super.onStop();
        if (entryDB != null)
        	entryDB.close();
	}
    
    /***************************************************************
	 Purpose: Creates the Options Menu with one item ("Clear All Fields")
	 Input: Nothing
	 Output: Boolean Variable
	 **************************************************************/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(group1Id, clearBtnId, clearBtnId, "Clear All Fields");
		return super.onCreateOptionsMenu(menu);
	}
	
	/***************************************************************
	 Purpose: Called when user selects a menu option (Go to Today)
	 Input: Nothing
	 Output: Boolean variable
	 **************************************************************/
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == clearBtnId){
			//water
        	text = (EditText)findViewById(R.id.editTextWater);
        	text.setText("");
        	//screen
        	text = (EditText)findViewById(R.id.editTextScreen);
        	text.setText("");
        	//sleep
        	text = (EditText)findViewById(R.id.editTextSleep);
        	text.setText("");
        	//exercise
        	text = (EditText)findViewById(R.id.editTextExercise);
        	text.setText("");
        	//weight
        	text = (EditText)findViewById(R.id.editTextWeight);
       		text.setText("");
       		//fruits and veggies
        	text = (EditText)findViewById(R.id.editTextFruit);
        	text.setText("");
        	text.requestFocus();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
    
    /*********************************************************************
	 Purpose: Called the user clicks the save data button, it either
	 saves the data or updates it depending on whether or not data
	 was already present for that day
	 Input: Nothing
	 Output: Nothing
	 *********************************************************************/
    public void saveClick(View view){
        Calendar date = Calendar.getInstance();
        Float fruit;
        Float water;
        Float screen;
        Float sleep;
        Float exercise;
        int weight;
        String temp;
        String[] tempSplit;
        
        if (!hasPastData)
        {
        	try {
        		// get date
        		dateText = (TextView)findViewById(R.id.editTextDate);
        		temp = dateText.getText().toString();
        		tempSplit = temp.split("/");
        		date = Calendar.getInstance();
        		date.clear();
        		date.set(Calendar.MONTH, Integer.parseInt(tempSplit[0]) - 1);
        		date.set(Calendar.DAY_OF_MONTH, Integer.parseInt(tempSplit[1]));
        		date.set(Calendar.YEAR, Integer.parseInt(tempSplit[2]));
        		//get fruits and veggies
        		text = (EditText)findViewById(R.id.editTextFruit);
        		if (text.getText().toString() == "")
        			fruit = (float)0;
        		else
        			fruit = Float.parseFloat(text.getText().toString());
        		//get water
        		text = (EditText)findViewById(R.id.editTextWater);
        		if (text.getText().toString() == "")
        			water = (float)0;
        		else
        			water = Float.parseFloat(text.getText().toString());
        		//get screen
        		text = (EditText)findViewById(R.id.editTextScreen);
        		if (text.getText().toString() == "")
        			screen = (float)0;
        		else        		
        			screen = Float.parseFloat(text.getText().toString());
        		//get sleep
        		text = (EditText)findViewById(R.id.editTextSleep);
        		if (text.getText().toString() == "")
        			sleep = (float)0;
        		else
        			sleep = Float.parseFloat(text.getText().toString());
        		//get exercise
        		text = (EditText)findViewById(R.id.editTextExercise);
        		if (text.getText().toString() == "")
        			exercise = (float)0;
        		else
        			exercise = Float.parseFloat(text.getText().toString());
        		//get weight
        		text = (EditText)findViewById(R.id.editTextWeight);
        		if (text.getText().toString() != "" && isNumeric(text.getText().toString()))
        			weight = Integer.parseInt(text.getText().toString());
        		else
        			weight = -1;
        		entry = new liveEntry(fruit, water, screen, exercise, sleep, weight, date);
        		if (entryDB.AddEntry(entry))
        		{
        			Toast toast = Toast.makeText(this, "Saved successfully!", Toast.LENGTH_SHORT);
        			toast.setGravity(Gravity.CENTER, 0, 0);
        			toast.show();
        		}
        		else
        		{
        			Toast toast = Toast.makeText(this, "Save Failed, please try again.", Toast.LENGTH_SHORT);
        			toast.setGravity(Gravity.CENTER, 0, 0);
        			toast.show();
        		}
        		this.setResult(Activity.RESULT_OK);
        	    this.finish();

        	} catch (Exception e) {
        		Toast toast = Toast.makeText(this, "Save Failed, please try again.", Toast.LENGTH_SHORT);
        		toast.setGravity(Gravity.CENTER, 0, 0);
        		toast.show();
        		return;
        	}
        }
        else //update data
        {
        	try {
        		// get date
        		dateText = (TextView)findViewById(R.id.editTextDate);
        		temp = dateText.getText().toString();
        		tempSplit = temp.split("/");
        		date = Calendar.getInstance();
        		date.clear();
        		date.set(Calendar.MONTH, Integer.parseInt(tempSplit[0]) - 1);
        		date.set(Calendar.DAY_OF_MONTH, Integer.parseInt(tempSplit[1]));
        		date.set(Calendar.YEAR, Integer.parseInt(tempSplit[2]));
        		//get fruits and veggies
        		text = (EditText)findViewById(R.id.editTextFruit);
        		if (text.getText().toString() == "")
        			fruit = (float)0;
        		else
        			fruit = Float.parseFloat(text.getText().toString());
        		//get water
        		text = (EditText)findViewById(R.id.editTextWater);
        		if (text.getText().toString() == "")
        			water = (float)0;
        		else
        			water = Float.parseFloat(text.getText().toString());
        		//get screen
        		text = (EditText)findViewById(R.id.editTextScreen);
        		if (text.getText().toString() == "")
        			screen = (float)0;
        		else        		
        			screen = Float.parseFloat(text.getText().toString());
        		//get sleep
        		text = (EditText)findViewById(R.id.editTextSleep);
        		if (text.getText().toString() == "")
        			sleep = (float)0;
        		else
        			sleep = Float.parseFloat(text.getText().toString());
        		//get exercise
        		text = (EditText)findViewById(R.id.editTextExercise);
        		if (text.getText().toString() == "")
        			exercise = (float)0;
        		else
        			exercise = Float.parseFloat(text.getText().toString());
        		//get weight
        		text = (EditText)findViewById(R.id.editTextWeight);
        		if (text.getText().toString() != "" && isNumeric(text.getText().toString()))
        			weight = Integer.parseInt(text.getText().toString());
        		else
        			weight = -1;
        		entry = new liveEntry(fruit, water, screen, exercise, sleep, weight, date);
        		if (entryDB.UpdateEntry(entry) > 0)
        		{
        			Toast toast = Toast.makeText(this, "Updated successfully!", Toast.LENGTH_SHORT);
        			toast.setGravity(Gravity.CENTER, 0, 0);
        			toast.show();
        		}
        		else
        		{
        			Toast toast = Toast.makeText(this, "Update Failed, please try again.", Toast.LENGTH_SHORT);
        			toast.setGravity(Gravity.CENTER, 0, 0);
        			toast.show();
        		}
        		this.setResult(Activity.RESULT_OK);
        	    this.finish();

        	} catch (Exception e) {
        		Toast toast = Toast.makeText(this, "Update Failed, please try again.", Toast.LENGTH_SHORT);
        		toast.setGravity(Gravity.CENTER, 0, 0);
        		toast.show();
        		return;
        	}
        }
    }
    
    /*********************************************************************
	 Purpose: Returns whether or not a given string is numeric or not
	 using the parseFloat method
	 Input: String str
	 Output: boolean variable whether or not the string is numeric
	 *********************************************************************/
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
 
    /*********************************************************************
	 Purpose: Called when the fruits entry field is changed, changes the 
	 background color of the view depending on whether or not the text entered
	 is complete or not
	 Input: Nothing
	 Output: Nothing
	 *********************************************************************/
    public void addForFruit(){    	
    	fruitView = (EditText)findViewById(R.id.editTextFruit);
        fruitView.addTextChangedListener(new TextWatcher(){
            public void afterTextChanged(Editable s) {
                
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){
            	
            }
            public void onTextChanged(CharSequence s, int start, int before, int count){
            	try {
            		LinearLayout fruitLayout;
            		fruitLayout = (LinearLayout)findViewById(R.id.fruitLinearLayout);
        		
            		if(count >0)
            		{
            			float fruitTemp = Float.parseFloat(s.toString());
            			if(fruitTemp >= 5)
            			{	            		
            				fruitLayout.setBackgroundColor(getResources().getColor(R.color.green));
            			}
            			else
            			{
            				fruitLayout.setBackgroundColor(getResources().getColor(R.color.red));
            			}
            		}
            		else
            		{
            			fruitLayout.setBackgroundColor(getResources().getColor(R.color.appBackground));
            		}
            	} catch (Exception ex) {
            		//do nothing
            	}
            }
        });
    }
    
    /*********************************************************************
	 Purpose: Called when the water entry field is changed, changes the 
	 background color of the view depending on whether or not the text entered
	 is complete or not
	 Input: Nothing
	 Output: Nothing
	 *********************************************************************/
    public void addForWater(){    	
    	waterView = (EditText)findViewById(R.id.editTextWater);
        waterView.addTextChangedListener(new TextWatcher(){
            public void afterTextChanged(Editable s) {
                
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){
            	
            }
            public void onTextChanged(CharSequence s, int start, int before, int count){
            	try {
            		LinearLayout waterLayout;
            		waterLayout = (LinearLayout)findViewById(R.id.waterLinearLayout);
        		
            		if(count >0)
            		{
            			float waterTemp = Float.parseFloat(s.toString());
            			if(waterTemp >= 4)
            			{	            		
            				waterLayout.setBackgroundColor(getResources().getColor(R.color.green));
            			}
            			else
            			{
            				waterLayout.setBackgroundColor(getResources().getColor(R.color.red));
            			}
            		}
            		else
            		{
            			waterLayout.setBackgroundColor(getResources().getColor(R.color.appBackground));
            		}
            	} catch (Exception ex) {
            		//do nothing
            	}
            }
        });
    }
        
    /*********************************************************************
	 Purpose: Called when the screen entry field is changed, changes the 
	 background color of the view depending on whether or not the text entered
	 is complete or not
	 Input: Nothing
	 Output: Nothing
	 *********************************************************************/
    public void addForScreen(){    	
    	screenView = (EditText)findViewById(R.id.editTextScreen);
        screenView.addTextChangedListener(new TextWatcher(){
            public void afterTextChanged(Editable s) {
                
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){
            	
            }
            public void onTextChanged(CharSequence s, int start, int before, int count){
            	try {
            		LinearLayout screenLayout;
            		screenLayout = (LinearLayout)findViewById(R.id.screenLinearLayout);
        		
            		if(count >0)
            		{
            			float screenTemp = Float.parseFloat(s.toString());
            			if(screenTemp <= 2)
            			{	            		
            				screenLayout.setBackgroundColor(getResources().getColor(R.color.green));
            			}
            			else
            			{
            				screenLayout.setBackgroundColor(getResources().getColor(R.color.red));
            			}
            		}
            		else
            		{
            			screenLayout.setBackgroundColor(getResources().getColor(R.color.appBackground));
            		}
            	} catch (Exception ex) {
            		//do nothing
            	}
            }
        });
    }
    
    /*********************************************************************
	 Purpose: Called when the exercise entry field is changed, changes the 
	 background color of the view depending on whether or not the text entered
	 is complete or not
	 Input: Nothing
	 Output: Nothing
	 *********************************************************************/
    public void addForExercise(){    	
    	exerciseView = (EditText)findViewById(R.id.editTextExercise);
    	exerciseView.addTextChangedListener(new TextWatcher(){
            public void afterTextChanged(Editable s) {
                
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){
            	
            }
            public void onTextChanged(CharSequence s, int start, int before, int count){
            	try {
            		LinearLayout exerciseLayout;
            		exerciseLayout = (LinearLayout)findViewById(R.id.exerciseLinearLayout);
        		
            		if(count >0)
            		{
            			float exerciseTemp = Float.parseFloat(s.toString());
            			if(exerciseTemp >= 1)
            			{	            		
            				exerciseLayout.setBackgroundColor(getResources().getColor(R.color.green));
            			}
            			else
            			{
            				exerciseLayout.setBackgroundColor(getResources().getColor(R.color.red));
            			}
            		}
            		else
            		{
            			exerciseLayout.setBackgroundColor(getResources().getColor(R.color.appBackground));
            		}
            	} catch (Exception ex) {
            		//do nothing
            	}
            }
        });
    }
 
    /*********************************************************************
	 Purpose: Called when the sleep entry field is changed, changes the 
	 background color of the view depending on whether or not the text entered
	 is complete or not
	 Input: Nothing
	 Output: Nothing
	 *********************************************************************/   
    public void addForSleep(){    	
    	sleepView = (EditText)findViewById(R.id.editTextSleep);
    	sleepView.addTextChangedListener(new TextWatcher(){
            public void afterTextChanged(Editable s) {
                
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){
            	
            }
            public void onTextChanged(CharSequence s, int start, int before, int count){
            	try {
            		LinearLayout sleepLayout;
            		sleepLayout = (LinearLayout)findViewById(R.id.sleepLinearLayout);
        		
            		if(count >0)
            		{
            			float sleepTemp = Float.parseFloat(s.toString());
            			if(sleepTemp >= 8)
            			{	            		
            				sleepLayout.setBackgroundColor(getResources().getColor(R.color.green));
            			}
            			else
            			{
            				sleepLayout.setBackgroundColor(getResources().getColor(R.color.red));
            			}
            		}
            		else
            		{
            			sleepLayout.setBackgroundColor(getResources().getColor(R.color.appBackground));
            		}
            	} catch(Exception ex) {
            		//do nothing
            	}
            }
        });
    }
}