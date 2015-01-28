package au.com.azarel.symptomchecker;

import java.util.List;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import au.com.azarel.symptomcheckerservice.repository.Patient;
import au.com.azarel.symptomcheckerservice.repository.User;

public class DoctorPatientListAdapter extends BaseAdapter {
	// list of doctor's patients
	
	List<Patient> mPatientList;
	Context mContext;
	//Calendar mDateAndTime;
	View mView;
	String mServerPrefs;
	User mLoginUser;
	String mUserName;
	String mPassword;
	int mPosition;


	public DoctorPatientListAdapter(Context c, List<Patient> myitem) {
		this.mPatientList = myitem;
		mContext = c;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mPatientList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mPatientList.get(position);
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

	        grid =  mInflater.inflate(R.layout.doctor_patient, null);
	    } else {
	        grid = convertView;
	    }

	    TextView tv = (TextView) grid.findViewById(R.id.tvPatientName);
        // prefix patient name with exclamation if patient has a critical pain or eating issue
        String strPatientName = "";
        if (mPatientList.get(position).isHasCriticalIssue()) {
            strPatientName = "! ";
        }
        strPatientName += mPatientList.get(position).getFirstName() + " " + mPatientList.get(position).getLastName();
	    tv.setText(strPatientName);
	    final int pos2=position;
	    // when list item is clicked, open PatientGraphActivity for the selected patient
	    tv.setOnClickListener(new OnClickListener(){
	    	public void onClick(View v){            		
	    		mView = v;
	    		Intent graphIntent = new Intent(mContext,PatientGraphActivity.class);
	    		graphIntent.putExtra("serverPrefs", mServerPrefs);
	    		graphIntent.putExtra("userName", mUserName);
	    		graphIntent.putExtra("password", mPassword);
	    		graphIntent.putExtra("user", mLoginUser);
	    		graphIntent.putExtra("patient", mPatientList.get(pos2));
	    		mContext.startActivity(graphIntent);
	    	}
	    });
	    
	    
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


}
