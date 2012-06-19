package com.agribot.dashboard;

import android.os.Bundle;

import com.agribot.dashboard.R;


public class AboutActivity extends DashboardActivity 
{



protected void onCreate(Bundle savedInstanceState) 
{
    super.onCreate(savedInstanceState);

    setContentView (R.layout.activity_about);
    setTitleFromActivityLabel (R.id.title_text);
}
    
} // end class
