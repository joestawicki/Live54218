/****************************************************
This class is used for the about screen
****************************************************/
package edu.snc.live54218;

import java.util.regex.Pattern;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.widget.TextView;

public class about extends Activity {
    /** Called when the activity is first created. */	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	//start the about activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        
        //jmt: pattern we want to match and turn into a clickable link
        Pattern pattern = Pattern.compile("www.live54218.org");
        //jmt: prefix our pattern with http://
        TextView aboutView;
        aboutView = (TextView) findViewById(R.id.aboutPart1TextView);
        Linkify.addLinks(aboutView, pattern, "http://");
        aboutView.setMovementMethod(LinkMovementMethod.getInstance());
    }
}