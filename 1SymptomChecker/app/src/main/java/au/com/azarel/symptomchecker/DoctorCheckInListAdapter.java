package au.com.azarel.symptomchecker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.TimePicker;
import au.com.azarel.symptomcheckerservice.repository.CheckIn;
import au.com.azarel.symptomcheckerservice.repository.Patient;
import au.com.azarel.symptomcheckerservice.repository.User;

public class DoctorCheckInListAdapter extends BaseAdapter {
	// list of a patient's checkins - used by their doctor

	ArrayList<CheckIn> mCheckInList;
	Context mContext;
	//Calendar mDateAndTime;
	View mView;
	String mServerPrefs;
	User mLoginUser;
	Patient mPatient;
	String mUserName;
	String mPassword;
	int mPosition;


	public DoctorCheckInListAdapter(Context c, ArrayList<CheckIn> mCheckIns) {
		this.mCheckInList = mCheckIns;
		mContext = c;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mCheckInList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mCheckInList.get(position);
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

	        grid =  mInflater.inflate(R.layout.doctor_check_in, null);
	    } else {
	        grid = convertView;
	    }
		
	    TextView tvCheckInDateTime = (TextView) grid.findViewById(R.id.tvCheckInDateTime);
	    TextView tvPain = (TextView) grid.findViewById(R.id.tvPain);
	    TextView tvEatingAffect = (TextView) grid.findViewById(R.id.tvEatingAffect);
	    CheckBox ckTookMeds  = (CheckBox)grid.findViewById(R.id.ckTookMedication);
	    
	    SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
	    tvCheckInDateTime.setText(formatter.format(new Date(mCheckInList.get(position).getCheckInTime())));
	    tvPain.setText("Pain: " + mCheckInList.get(position).getPain().name());
	    tvEatingAffect.setText("Eating Affect: " + mCheckInList.get(position).getEatingAffect().name());
	    ckTookMeds.setChecked(mCheckInList.get(position).getTookMedication());
	    
	    return grid;
	}

	public String getServerPrefs() {
		return mServerPrefs;
	}

	public void setServerPrefs(String mServerPrefs) {
		this.mServerPrefs = mServerPrefs;
	}

	public String getUserName() {
		return mUserName;
	}

	public void setUserName(String mUserName) {
		this.mUserName = mUserName;
	}

	public User getUser() {
		return mLoginUser;
	}

	public void setUser(User mLoginUser) {
		this.mLoginUser = mLoginUser;
	}

	public String getPassword() {
		return mPassword;
	}

	public void setPassword(String mPassword) {
		this.mPassword = mPassword;
	}

	public Patient getmPatient() {
		return mPatient;
	}

	public void setmPatient(Patient mPatient) {
		this.mPatient = mPatient;
	}


}
