package com.slothnull.android.medox;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.slothnull.android.medox.model.AbstractProfile;

public class EditMedicalProfile extends AppCompatActivity {

    private static final String TAG = "EditProfileActivity";

    private ProgressDialog mProgressDialog;

    private EditText name;
    private EditText sex;
    private EditText birth;
    private EditText height;
    private EditText weight;
    private EditText blood;
    private EditText address;
    private EditText phone;
    private EditText emergency;
    private EditText martial;
    private EditText diseases;
    private EditText allergic;

    AbstractProfile oldProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_medical_profile);
        //wait for getting old config
        showProgressDialog();


        name = (EditText) findViewById(R.id.name);
        sex = (EditText) findViewById(R.id.sex);
        birth = (EditText) findViewById(R.id.birth);
        height = (EditText) findViewById(R.id.height);
        weight = (EditText) findViewById(R.id.weight);
        blood = (EditText) findViewById(R.id.blood);
        address = (EditText) findViewById(R.id.address);
        phone = (EditText) findViewById(R.id.phone);
        emergency = (EditText) findViewById(R.id.emergency);
        martial = (EditText) findViewById(R.id.martial);
        diseases = (EditText) findViewById(R.id.diseases);
        allergic = (EditText) findViewById(R.id.allergic);

        getProfile();
    }

    public void save(View v){
        //strings to send to config class
        String mName = name.getText().toString();
        String mSex = sex.getText().toString();
        String mBirth = birth.getText().toString();
        String mHeight = height.getText().toString();
        String mWeight = weight.getText().toString();
        String mBlood = blood.getText().toString();
        String mAddress = address.getText().toString();
        String mPhone = phone.getText().toString();
        String mEmergency = emergency.getText().toString();
        String mMartial = martial.getText().toString();
        String mDiseases = diseases.getText().toString();
        String mAllergic = allergic.getText().toString();

        //if fields are empty save old config
        if (mName.isEmpty())
            mName = oldProfile.name;
        if (mSex.isEmpty())
            mSex = oldProfile.sex;
        if (mBirth.isEmpty())
            mBirth = oldProfile.birth;
        if (mHeight.isEmpty())
            mHeight = oldProfile.height;
        if (mWeight.isEmpty())
            mWeight = oldProfile.weight;
        if (mBlood.isEmpty())
            mBlood = oldProfile.blood;
        if (mAddress.isEmpty())
            mAddress = oldProfile.address;
        if (mPhone.isEmpty())
            mPhone = oldProfile.phone;
        if (mEmergency.isEmpty())
            mEmergency = oldProfile.emergency;
        if (mMartial.isEmpty())
            mMartial = oldProfile.martial;
        if (mDiseases.isEmpty())
            mDiseases = oldProfile.diseases;
        if (mAllergic.isEmpty())
            mAllergic = oldProfile.allergic;

        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //send notification to database to access it later in Notification Activity
        AbstractProfile profile = new AbstractProfile(
                mName,
                mSex,
                mBirth,
                mHeight,
                mWeight,
                mBlood,
                mAddress,
                mPhone,
                mEmergency,
                mMartial,
                mDiseases,
                mAllergic);


        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference()
                .child("users").child(UID).child("profile");
        mDatabase.setValue(profile);
        finish();
    }

    public void getProfile(){
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        final ValueEventListener profileListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                oldProfile = dataSnapshot.getValue(AbstractProfile.class);
                if(oldProfile.name != null)
                    name.setHint(name.getHint() + oldProfile.name);
                if(oldProfile.sex != null)
                    sex.setHint(sex.getHint() + oldProfile.sex);
                if(oldProfile.birth != null)
                    birth.setHint(birth.getHint() + oldProfile.birth);
                if(oldProfile.height != null)
                    height.setHint(height.getHint() + oldProfile.height);
                if(oldProfile.weight != null)
                    weight.setHint(weight.getHint() + oldProfile.weight);
                if(oldProfile.blood != null)
                    blood.setHint(blood.getHint() + oldProfile.blood);
                if(oldProfile.address != null)
                    address.setHint(address.getHint() + oldProfile.address);
                if(oldProfile.phone != null)
                    phone.setHint(phone.getHint() + oldProfile.phone);
                if(oldProfile.emergency != null)
                    emergency.setHint(emergency.getHint() + oldProfile.emergency);
                if(oldProfile.martial != null)
                    martial.setHint(martial.getHint() + oldProfile.martial);
                if(oldProfile.diseases != null)
                    diseases.setHint(diseases.getHint() + oldProfile.diseases);
                if(oldProfile.allergic != null)
                    allergic.setHint(allergic.getHint() + oldProfile.allergic);

                hideProgressDialog();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        mDatabase.child("users").child(UID).child("profile")
                .addValueEventListener(profileListener);
    }

    //progress dialog to wait for saved data
    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Loading...");
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}
