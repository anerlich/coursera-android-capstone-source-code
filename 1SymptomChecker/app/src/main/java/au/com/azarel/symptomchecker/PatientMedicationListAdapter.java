package au.com.azarel.symptomchecker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import au.com.azarel.symptomcheckerservice.repository.Patient;
import au.com.azarel.symptomcheckerservice.repository.PatientMedication;
import au.com.azarel.symptomcheckerservice.repository.User;

public class PatientMedicationListAdapter extends BaseAdapter {
	// list of patient medications used by doctor
	ArrayList<PatientMedication> mPatientMedicationList;
	Context mContext;
	//Calendar mDateAndTime;
	View mView;
	String mServerPrefs;
	User mLoginUser;
	Patient mPatient;
	String mUserName;
	String mPassword;
	int mPosition;


	public PatientMedicationListAdapter(Context c, ArrayList<PatientMedication> myitem) {
		this.mPatientMedicationList = myitem;
		mContext = c;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mPatientMedicationList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mPatientMedicationList.get(position);
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

	        grid =  mInflater.inflate(R.layout.medication_patient, null);
	    } else {
	        grid = convertView;
	    }
	    TextView tvMedication = (TextView) grid.findViewById(R.id.tvPatientMedication);    
	    TextView tvDate = (TextView) grid.findViewById(R.id.tvDate);
	    TextView tvTime = (TextView) grid.findViewById(R.id.tvTime);
	    tvMedication.setText(mPatientMedicationList.get(position).getMedication());

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
