package au.com.azarel.symptomchecker;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import au.com.azarel.symptomcheckerservice.repository.CheckInMedication;

public class CheckInMedicationAdapter extends BaseAdapter {
	
	// listview adapter for check in medications
	private List<CheckInMedication> mCheckInMedications;
	private Context mContext;
	private View mView;
	
	public CheckInMedicationAdapter(Context c, List<CheckInMedication> items) {
		this.mCheckInMedications = items;
		mContext = c;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mCheckInMedications.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mCheckInMedications.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
	    View grid;
	    if (convertView == null) {
	        LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	        grid =  mInflater.inflate(R.layout.medication_check_in, null);
	    } else {
	        grid = convertView;
	    }

	    // name of medication
	    TextView tvMedication = (TextView) grid.findViewById(R.id.tvMedication);
        CheckBox ckTookIt = (CheckBox) grid.findViewById(R.id.ckMedication);
	    
	    // default date and time taken to current date and time
	    TextView tvDate = (TextView) grid.findViewById(R.id.tvDate);
	    TextView tvTime = (TextView) grid.findViewById(R.id.tvTime);
	    tvMedication.setText(mCheckInMedications.get(position).getPatientMedication().getMedication());
        ckTookIt.setChecked(mCheckInMedications.get(position).getTookIt());
        final int pos2 = position;
/*
        ckTookIt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mCheckInMedications.get(pos2).setTookIt(b);
                notifyDataSetChanged();
            }
        });
*/

        ckTookIt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mCheckInMedications.get(pos2).setTookIt(((CheckBox)view).isChecked());
            }
        } );

	    Date dateNow = new Date(mCheckInMedications.get(position).getTookItTime());
	    SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
	    String strDate = formatter.format(dateNow);
	    tvDate.setText(strDate);
	    
	    // set up date picker dialog
	    tvDate.setOnClickListener(new OnClickListener(){
	    	public void onClick(View v){            		
	    		mView = v;
                final View viewTime = ((View)v.getParent()).findViewById(R.id.tvTime);
	    	    DatePickerDialog.OnDateSetListener dt=new DatePickerDialog.OnDateSetListener() {
	    	    	public void onDateSet(DatePicker view, int year,
	    			                          int month, int day) {
	    			      updateDateView(mView, year, month, day);
                        updateCheckInDateAndTime(mView, viewTime, pos2);
	    			}
	    		};
	    		TextView tv = (TextView)v;
	    		String strDate = (String)(tv.getText());
	    	    SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy",Locale.getDefault());
	    	    Calendar date2 = Calendar.getInstance();
				try {
					date2.setTime(formatter.parse(strDate));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            new DatePickerDialog(v.getContext(),
	                    dt,
	                    date2.get(Calendar.YEAR),
	                    date2.get(Calendar.MONTH),
	                    date2.get(Calendar.DAY_OF_MONTH)).show();

	    	}
	    });

	    SimpleDateFormat formatterTime = new SimpleDateFormat("hh:mm",Locale.getDefault());
	    String strTime = formatterTime.format(dateNow);

	    tvTime.setText(strTime);
	    
	    // set up TimePicker dialog
	    tvTime.setOnClickListener(new OnClickListener(){
	    	public void onClick(View v){            		
	    		mView = v;
                final View viewDate = ((View)v.getParent()).findViewById(R.id.tvDate);
	    	    TimePickerDialog.OnTimeSetListener t=new TimePickerDialog.OnTimeSetListener() {
	    	    	public void onTimeSet(TimePicker view, int hourOfDay,
	    			                          int minute) {
	    			      updateTimeView(mView,hourOfDay, minute);
                        updateCheckInDateAndTime(viewDate, mView, pos2);
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

	    return grid;
	}

	//update the time and date fields after pickers used
	
	public void updateTimeView(View v, int hour, int minute){
	    TextView tx = (TextView)v;
	    DecimalFormat numFormat = new DecimalFormat("00");
	    tx.setText(numFormat.format(hour) + ":" + numFormat.format(minute));		
	}

	public void updateDateView(View v, int year, int month, int day){
	    TextView tx = (TextView)v;
	    SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
	    Calendar retDate = Calendar.getInstance();
	    retDate.set(year,month,day);
	    String strRetDate = formatter.format(retDate.getTime());
	    tx.setText(strRetDate);
	}

    public void updateCheckInDateAndTime(View vDate, View vTime, int position) {
        TextView tvDate = (TextView) vDate;
        TextView tvTime = (TextView) vTime;
        String strDateTimeMed = tvDate.getText() + " " + tvTime.getText() + ":00";
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
        Date datMed = new Date();
        try {
            datMed = formatter.parse(strDateTimeMed);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            //default to current date/time as set before try/catch block, but print message on exception
            System.out.println("Parse exception on CheckInMedication date " + strDateTimeMed);
        }
        mCheckInMedications.get(position).setTookItTime(datMed.getTime());
        notifyDataSetChanged();
    }
	

	public Context getContext() {
		return mContext;
	}

	public void setContext(Context mContext) {
		this.mContext = mContext;
	}

}
