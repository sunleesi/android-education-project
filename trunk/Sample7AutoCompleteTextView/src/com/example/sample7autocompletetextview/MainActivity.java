package com.example.sample7autocompletetextview;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;


public class MainActivity extends ActionBarActivity {

	AutoCompleteTextView textView;
//	ArrayAdapter<String> mAdapter;
	MyAdapter mAdapter;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (AutoCompleteTextView)findViewById(R.id.autoCompleteTextView1);
//        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        mAdapter = new MyAdapter();
        textView.setAdapter(mAdapter);
        
        initData();
    }
    
    private void initData() {
    	String[] array = getResources().getStringArray(R.array.items);
    	for (String s : array) {
    		mAdapter.add(s);
    	}
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
