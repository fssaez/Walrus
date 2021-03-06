package com.navlog.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.navlog.calculator.R;
import com.navlog.models.AirplaneProfileModel;
import com.navlog.models.CruisePerformanceModel;



public class AirplaneProfileActivity extends Activity {
	private AirplaneProfileModel profile = new AirplaneProfileModel();
	public static final String cruisePerformanceKey = "cruisePerformance";
	private String performanceToEdit;
	private ListView lv;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_airplane_profile);
		loadPreviousActivityFlightData();
		populateList();		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.airplane_profile, menu);
		return true;
	}
	
	
	@Override 
	public void onActivityResult(int requestCode, int resultCode, Intent data) 
	{     
	  super.onActivityResult(requestCode, resultCode, data); 
	  switch(requestCode) { 
	    case (0) : { 
	      if (resultCode == Activity.RESULT_OK) 
	      { 
	    	
	    	Bundle b = data.getExtras();
	    	CruisePerformanceModel performance = new CruisePerformanceModel();
	  		performance = (CruisePerformanceModel) b.getSerializable(cruisePerformanceKey);
	  		String label = performance.getLabel();
	  		this.profile.removeCruisePerformanceParam(performanceToEdit);
	  		this.profile.addCruisePerformanceParam(label, performance);	  		
	  		this.populateList();
	      
	      } 
	      break; 
	    } 
	  } 
	}
	
	@Override
	public void onBackPressed()
	{
		final String items[] = {"Save and Exit", "Don't Save"};

		AlertDialog.Builder ab=new AlertDialog.Builder(this);
		ab.setTitle("Keep Changes?");
		ab.setItems(items, new NLExitClickListener());
		ab.show();
	
	}
	
	private void goBack()
	{
		super.onBackPressed();
	}
	
	public void launchLoadedCruisePerformanceActivity(String label)
    {
		this.performanceToEdit = label;
    	Intent intent = new Intent(this, CruisePerformanceActivity.class);
    	Bundle b = new Bundle();
    	CruisePerformanceModel performance = this.profile.getCruisePerformanceParam(label);
    	b.putSerializable(cruisePerformanceKey, performance);
    	intent.putExtras(b);
    	startActivityForResult(intent, 0);
    }
	
	public void launchCruisePerformanceActivity(View view)
	{
		Intent intent = new Intent(this, CruisePerformanceActivity.class);
		startActivityForResult(intent, 0);
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
	    switch (item.getItemId()) {
	        case R.id.save_profile:
	        	returnDataToPreviousActivity();
	        	 	
	        	break;
	        
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	    return true;
	}
	
	public void save(View view)
	{
		returnDataToPreviousActivity();
	}
	
	public void returnDataToPreviousActivity()
	{
		Intent resultIntent = new Intent();
		Bundle b = new Bundle();
    	
		profile.setAirplaneBrand(this.getAirplaneBrandField());
    	profile.setAirplaneModel(this.getAirplaneModelField());
    	profile.setAirplaneName(this.getAirplaneNameField());
    	profile.setAirplaneTailNumber(this.getAirplaneTailNumberField());
    	
    	b.putSerializable(AirplaneListActivity.airplaneProfileKey, profile);
    	resultIntent.putExtras(b);
		setResult(Activity.RESULT_OK, resultIntent);
		finish();

		
	}
	
	private void deletePerformance(String label)
	{
		this.profile.removeCruisePerformanceParam(label);
		populateList();
	}
	
	
	private void loadPreviousActivityFlightData()
	{
		if(getIntent().hasExtra(AirplaneListActivity.airplaneProfileKey))
		{
			Bundle b = new Bundle();
			b = getIntent().getExtras();
			profile = (AirplaneProfileModel) b.getSerializable(AirplaneListActivity.airplaneProfileKey);
			this.setAirplaneBrandField(profile.getAirplaneBrand());
			this.setAirplaneModelField(profile.getAirplaneModel());
			this.setAirplaneNameField(profile.getAirplaneName());
			this.setAirplaneTailNumberField(profile.getAirplaneTailNumber());
		}
			
		
	}
	
    public void populateList()
    {
    	String[] paramKeys  = profile.getAllLabels();
		lv = (ListView) findViewById(R.id.list);
		lv.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_activated_1, paramKeys));

		lv.setFastScrollEnabled(true);
		PerformanceParamsClickListener clickListener = new PerformanceParamsClickListener();
		lv.setOnItemClickListener(clickListener);
		setListViewSize();

    }
    
    public void setListViewSize()
    {
    	ListAdapter myListAdapter = lv.getAdapter();
    	if (myListAdapter == null) {
            //do nothing return null
            return;
        }
        //set listAdapter in loop for getting final size
        int totalHeight = 0;
        for (int size = 0; size < myListAdapter.getCount(); size++) {
            View listItem = myListAdapter.getView(size, null, lv);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
      //setting listview item in adapter
        ViewGroup.LayoutParams params = lv.getLayoutParams();
        params.height = totalHeight + (lv.getDividerHeight() * (myListAdapter.getCount() - 1));
        lv.setLayoutParams(params);
        // print height of adapter on log
        //Log.i("height of listItem:", String.valueOf(totalHeight));
    }
    	
    
	
	
	
	public String getAirplaneNameField()
	{
		EditText airplaneName = (EditText) findViewById(R.id.airplaneName);
    	String name = airplaneName.getText().toString();
    	return name;
    	
	}
	
	public void setAirplaneNameField(String name)
	{
		EditText airplaneName = (EditText) findViewById(R.id.airplaneName);
    	airplaneName.setText(name);
	}
    	
	
	public String getAirplaneBrandField()
	{
		EditText airplaneBrand = (EditText) findViewById(R.id.airplaneBrand);
    	String brand = airplaneBrand.getText().toString();
    	return brand;
	}
	
	public void setAirplaneBrandField(String brand)
	{
		EditText airplaneBrand = (EditText) findViewById(R.id.airplaneBrand);
    	airplaneBrand.setText(brand);
	}
	
	public String getAirplaneModelField()
	{
		EditText airplaneModel = (EditText) findViewById(R.id.airplaneModel);
    	String model = airplaneModel.getText().toString();
    	return model;
	}
	
	public void setAirplaneModelField(String model)
	{
		EditText airplaneModel = (EditText) findViewById(R.id.airplaneModel);
    	airplaneModel.setText(model);
	}
	
	public String getAirplaneTailNumberField()
	{
		EditText airplaneTail = (EditText) findViewById(R.id.airplaneTailNum);
    	String tail = airplaneTail.getText().toString();
    	return tail;
	}
	
	public void setAirplaneTailNumberField(String tailNum)
	{
		EditText airplaneTail = (EditText) findViewById(R.id.airplaneTailNum);
    	airplaneTail.setText(tailNum);
	}
	
	public class NLExitClickListener implements DialogInterface.OnClickListener
	{
		public void onClick(DialogInterface d, int choice) 
		{
			if(choice == 0)
			{
				returnDataToPreviousActivity();
			}
			else if(choice == 1)
			{
				goBack();
			}
		}
			
	}
	
	
	public class PerformanceParamsClickListener implements OnItemClickListener
	{

		public void onItemClick(AdapterView<?> parent, View view,
				int position, long id) {
			
			performanceToEdit = (String)((TextView) view).getText();
			final CharSequence[] items = {"Open", "Delete"};

			AlertDialog.Builder builder = new AlertDialog.Builder(AirplaneProfileActivity.this);
			builder.setTitle("Select an option");
			builder.setItems(items, new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int item) {
			        Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();

			        if(items[item].equals("Open"))
			        {
			        	launchLoadedCruisePerformanceActivity(performanceToEdit);
			        }
			        else if (items[item].equals("Delete"))
			        {
			        	deletePerformance(performanceToEdit);
			        }
			    }
			});
			AlertDialog alert = builder.create();
			alert.show();
			
		}
		
	}	
}
