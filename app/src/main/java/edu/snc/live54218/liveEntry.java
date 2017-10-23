/****************************************************
This class is used to store the live entries throughout the application
****************************************************/
package edu.snc.live54218;

import java.util.Calendar;

public class liveEntry {

	//Variables
	private float fruits;
	private float water;
	private float screenTime;
	private float exerciseTime;
	private float sleepTime;
	private int weight;
	private Calendar date;
	
	//Constructor
	public liveEntry(float frveg, float wat, float screen, float exerc, float sleep, int wt, Calendar d) {
		this.fruits = frveg;
		this.water = wat;
		this.screenTime = screen;
		this.exerciseTime = exerc;
		this.sleepTime = sleep;
		this.weight = wt;
		this.date = d;
	}
	
	//Constructor
	public liveEntry(float frveg, float wat, float screen, float exerc, float sleep, Calendar d) {
		this.fruits = frveg;
		this.water = wat;
		this.screenTime = screen;
		this.exerciseTime = exerc;
		this.sleepTime = sleep;
		this.weight = -1;
		this.date = d;
	}
	
	//Constructor
	public liveEntry() {
		this.fruits = 0;
		this.water = 0;
		this.screenTime = 0;
		this.exerciseTime = 0;
		this.sleepTime = 0;
		this.weight = 0;
		this.date = Calendar.getInstance();
	}
		
	public float getFruitsAndVeggies()
	{
		return this.fruits;
	}	
	public void setFruitsAndVeggies(float frveg)
	{
		this.fruits = frveg;
	}
	
	public float getWater()
	{
		return this.water;
	}	
	public void setWater(float wat)
	{
		this.water = wat;
	}	
	
	public float getScreenTime()
	{
		return this.screenTime;
	}
	public void setScreenTime(float screen)
	{
		this.screenTime = screen;
	}
	
	public float getExerciseTime()
	{
		return this.exerciseTime;
	}
	public void setExerciseTime(float exerc)
	{
		this.exerciseTime = exerc;
	}
	
	public float getSleepTime()
	{
		return this.sleepTime;
	}
	public void setSleepTime(float sleep)
	{
		this.sleepTime = sleep;
	}
	
	public int getWeight()
	{
		return this.weight;
	}
	public void setWeight(int wt)
	{
		this.weight = wt;
	}
	
	public Calendar getDate()
	{
		return this.date;
	}
	public void setDate(Calendar d)
	{
		this.date = d;
	}	
}
