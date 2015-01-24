package au.com.azarel.symptomchecker;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;

import android.app.TimePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

public class TimeListAdapter extends BaseAdapter {
	// list adapter for patient's reminder times
public static final int MIN_TIME_SLOTS = 4;
List<String> mTimeList;
Context mContext;
Calendar mDateAndTime;
View mView;


public TimeListAdapter(Context c, List<String> myitem) {
	this.mTimeList =myitem;
	mContext = c;
}

@Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

    View grid;
    if (convertView == null) {
        LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        grid =  mInflater.inflate(R.layout.time_check_in, null);
    } else {
        grid = convertView;
    }

    TextView tv = (TextView) grid.findViewById(R.id.tvTime);    
    tv.setText(mTimeList.get(position));
	
    //set up time picker dialog
    tv.setOnClickListener(new OnClickListener(){
    	public void onClick(View v){            		
    		mView = v;
    	    TimePickerDialog.OnTimeSetListener t=new TimePickerDialog.OnTimeSetListener() {
    	    	public void onTimeSet(TimePicker view, int hourOfDay,
    			                          int minute) {
//    			      mDateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
//    			      mDateAndTime.set(Calendar.MINUTE, minute);
    			      updateView(mView,hourOfDay, minute);
    			}
    		};
    		TextView tv = (TextView)v;
    		String strTime = (String)(tv.getText());
            new TimePickerDialog(v.getContext(),
                    t,
                    Integer.parseInt(strTime.substring(0, 2)),
                    Integer.parseInt(strTime.substring(3, 5)),
                    true).show();

    	}
    });
    
    // delete button - only show if this in fifth or greater item in list
    Button btnDelete = (Button)grid.findViewById(R.id.btnTimeDelete);
    if (position < MIN_TIME_SLOTS){
    	btnDelete.setEnabled(false);
    	btnDelete.setVisibility(View.INVISIBLE);
    } else {
    	btnDelete.setEnabled(true);
    	btnDelete.setVisibility(View.VISIBLE);
    	btnDelete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mTimeList.remove(position);
				notifyDataSetChanged();
			}
    		
    	});
    }
    return grid;
}

public void updateView(View v, int hour, int minute){
	// update time after TimePickerDialog employed
    TextView tx = (TextView)v;
    DecimalFormat numFormat = new DecimalFormat("00");
//    tx.setText(mDateAndTime.toString());;
    tx.setText(numFormat.format(hour) + ":" + numFormat.format(minute));
	
}

@Override
public int getCount() {
    // TODO Auto-generated method stub
    return mTimeList.size();
}

@Override
public Object getItem(int position) {
    // TODO Auto-generated method stub
    return mTimeList.get(position);
}

@Override
public long getItemId(int position) {
    // TODO Auto-generated method stub
    return position;
}

public List<String> mycheckeditem() {
    return mTimeList;
}
}
