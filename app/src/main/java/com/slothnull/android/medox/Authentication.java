package com.slothnull.android.medox;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.app.ProgressDialog;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.slothnull.android.medox.service.IndicatorsService;
import com.slothnull.android.medox.service.LocationService;

public class Authentication extends AppCompatActivity {

    private static final String TAG = "AuthenticationActivity";

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private EditText mEmailField;
    private EditText mPasswordField;

    private RadioGroup signInRadioGroup;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        //remove sign up button
        findViewById(R.id.signupButton).setVisibility(View.GONE);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        // Views
        mEmailField = (EditText) findViewById(R.id.fieldEmail);
        mPasswordField = (EditText) findViewById(R.id.fieldPassword);

        signInRadioGroup = (RadioGroup) findViewById(R.id.signInRadioGroup);
        sharedPreferences = this.getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Check auth on Activity start
        if (mAuth.getCurrentUser() != null) {
            onAuthSuccess(mAuth.getCurrentUser());
        }
    }

    public void signIn(View view) {
        Log.d(TAG, "signIn");
        if(!checkConnection()){//not connected
            return;
        }
        if (!validateForm()) {
            return;
        }

        showProgressDialog();
        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signIn:onComplete:" + task.isSuccessful());
                        hideProgressDialog();

                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
                        } else {
                            Toast.makeText(getApplicationContext(), "Sign In Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void signUp(View view) {
        Log.d(TAG, "signUp");
        if (!validateForm()) {
            return;
        }

        showProgressDialog();
        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUser:onComplete:" + task.isSuccessful());
                        hideProgressDialog();

                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
                        } else {
                            Toast.makeText(getApplicationContext(), "Sign Up Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void onAuthSuccess(FirebaseUser user) {
        //set app type in shared prefs.
        String type = appType();
        sharedPreferences.edit().putString("appType", type).apply();
        
        String username = usernameFromEmail(user.getEmail());

        // Write new user
        writeNewUser(user.getUid(), username, user.getEmail());

        // Go to MainActivity
        Intent intent = new Intent(Authentication.this, Splash.class);
        startActivity(intent);
        finish();
    }

    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }

    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(mEmailField.getText().toString())) {
            mEmailField.setError("Required");
            result = false;
        } else {
            mEmailField.setError(null);
        }

        if (TextUtils.isEmpty(mPasswordField.getText().toString())) {
            mPasswordField.setError("Required");
            result = false;
        } else {
            mPasswordField.setError(null);
        }

        return result;
    }

    // [START basic_write]
    private void writeNewUser(String userId, String name, String email) {
        //create a child or update if already exists
        mDatabase.child("users").child(userId).child("user").child("email").setValue(email);
        mDatabase.child("users").child(userId).child("user").child("username").setValue(name);
        updateToken(userId);
    }

    private void updateToken(String userId){
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.i(TAG, "Token =  " + token);
        String appType = sharedPreferences.getString("appType","");
        Log.i(TAG, "appType =  " + appType);
        if (appType.equals("care")){
            Log.i(TAG, "in care");
            mDatabase.child("users").child(userId).child("token").child("mobile").setValue(token);
        }else if( appType.equals("senior") ){
            Log.i(TAG, "in senior");
            mDatabase.child("users").child(userId).child("token").child("watch").setValue(token);
        }else{
            Log.e(TAG, "error Sending Token: undefined appType");
        }
    }
    // [END basic_write]

    private ProgressDialog mProgressDialog;

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

    public String appType(){
        String type = "";
        int selectedId = signInRadioGroup.getCheckedRadioButtonId();

        if (selectedId == R.id.careRadio){
            type = "care";
        }
        if (selectedId == R.id.seniorRadio){
            type = "senior";
        }
        return type;
    }

    public void signOut() {
        stopService(new Intent(this, IndicatorsService.class));
        stopService(new Intent(this, LocationService.class));
        FirebaseAuth firebaseAuth;
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();
    }
    private boolean checkConnection(){
        //check connection state
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if(cm.getActiveNetworkInfo() == null){//Not Connected
            String message = "You are offline, Please enable connection to be able to sign in";
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(message).setPositiveButton("OK", null);
            builder.show();
            return false;
        }
        return true;
    }
}
