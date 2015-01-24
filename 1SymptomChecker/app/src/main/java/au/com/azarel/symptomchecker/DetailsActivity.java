package au.com.azarel.symptomchecker;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import au.com.azarel.symptomcheckerservice.repository.Doctor;
import au.com.azarel.symptomcheckerservice.repository.Patient;

public class DetailsActivity extends Activity {
	// show patient or doctor details depending on type passed as extra
	Doctor mDoctor;
	Patient mPatient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);
		Intent intent = getIntent();
		String strType = intent.getStringExtra("type");
		StringBuilder sb = new StringBuilder();
		if (strType.equals("doctor")) {
			this.setTitle("Doctor Details");
			mDoctor = intent.getParcelableExtra("doctor");
			sb.append("Name: ");
			sb.append(mDoctor.getFirstName() + " " + mDoctor.getLastName());
			sb.append("\n");
			sb.append("Unique Id: ");
			sb.append(mDoctor.getDocUUID());
			TextView tv = (TextView)findViewById(R.id.txtDetails);
			tv.setText(sb.toString());
		} else {
			// strType = "patient"
			this.setTitle("Patient Details");
			mPatient = intent.getParcelableExtra("patient");
			sb.append("Name: ");
			sb.append(mPatient.getFirstName() + " " + mPatient.getLastName());
			sb.append("\n");
			sb.append("Date of Birth: ");
			SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
			Date dte  = new Date(mPatient.getDateOfBirth());
			sb.append(formatter.format(dte));
			sb.append("\n");
			sb.append("Unique Id: ");
			sb.append(mPatient.getPatUUID());
			sb.append("\n");
		}
		TextView tv = (TextView)findViewById(R.id.txtDetails);
		tv.setText(sb.toString());

		Button btnOk = (Button)findViewById(R.id.btnOK);
		btnOk.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
			
		});
	}
}
