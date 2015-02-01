package au.com.azarel.symptomchecker;

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
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import au.com.azarel.symptomcheckerservice.repository.CheckInMedication;

public class CheckInMedicationReadOnlyAdapter extends CheckInMedicationAdapter {
	// read only version of the CheckInMedication adapter, used for doctor's view
	// of a patient's check ins

	public CheckInMedicationReadOnlyAdapter(Context c,
			List<CheckInMedication> items) {
		super(c, items);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
	    View grid;
	    if (convertView == null) {
	        LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	        grid =  mInflater.inflate(R.layout.medication_check_in, null);
	    } else {
	        grid = convertView;
	    }

	    // fill list sub fields from List passed in via constructor
	    TextView tvMedication = (TextView) grid.findViewById(R.id.tvMedication);    
	    TextView tvDate = (TextView) grid.findViewById(R.id.tvDate);
	    TextView tvTime = (TextView) grid.findViewById(R.id.tvTime);
	    CheckBox ckTookIt = (CheckBox)grid.findViewById(R.id.ckMedication);

	    tvMedication.setText(((CheckInMedication)getItem(position)).getPatientMedication().getMedication());
	    ckTookIt.setChecked(((CheckInMedication)getItem(position)).getTookIt());
        ckTookIt.setEnabled(false);
	    
	    SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
	    String strDate = formatter.format(((CheckInMedication)getItem(position)).getTookItTime());
	    tvDate.setText(strDate);	    

	    SimpleDateFormat formatterTime = new SimpleDateFormat("hh:mm",Locale.getDefault());
	    String strTime = formatterTime.format(((CheckInMedication)getItem(position)).getTookItTime());
	    tvTime.setText(strTime);

	    return grid;
	}

}
