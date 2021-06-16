package com.example.darkarmy.vision_login_01;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.jaeger.library.StatusBarUtil;

import java.util.Calendar;

public class Register extends AppCompatActivity {

    // Constants
    public static final String CHAT_PREFS = "UserPrefs";
    public static final String DISPLAY_NAME_KEY = "username";
    private static final String ENROLLMENT_NO_KEY = "enrollment";
    private static final String STUDENT_SEM_KEY = "studentSEM";

    private Button next;
    private EditText name,email,passwordIni,passwordFin,enrollmentNo;
    private Switch switch01 , switch02;
    private int switchChecker=0;

    // Firebase instance variables
    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        StatusBarUtil.setTransparent(Register.this);



        next =  findViewById(R.id.next_btn);
        name =  findViewById(R.id.nameET);
        email = findViewById(R.id.emailId);
        passwordFin =  findViewById(R.id.passwordFinal);
        passwordIni = findViewById(R.id.password);
        enrollmentNo = findViewById(R.id.enrollment_no);
        switch01 = findViewById(R.id.switch1);
        switch02 = findViewById(R.id.switch2);

       name.setOnEditorActionListener(new TextView.OnEditorActionListener() {
           @Override
           public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
               String enroll = enrollmentNo.getText().toString();
               checkEnrollment(enroll);
               return false;
           }
       });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerStudent();
            }
        });



        // TODO: Get hold of an instance of FirebaseAuth
        mAuth= FirebaseAuth.getInstance();

    }

    private void checkEnrollment(String enrollmentSt) {

        int studentYear = Integer.parseInt(enrollmentSt.substring(6, 8));

        // get current year、month and day
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        year = year % 100;
        int courseYear = year - studentYear;

        if (isEnrollmentValid(enrollmentSt)){
            if (courseYear==1){
                switch01.setText("I");
                switch02.setText("II");
            }
            if (courseYear==2){
                switch01.setText("III");
                switch02.setText("IV");
            }
            if (courseYear==3){
                switch01.setText("V");
                switch02.setText("VI");
            }
            if (courseYear==4){
                switch01.setText("VII");
                switch02.setText("VIII");
            }
        }

    }

    private void registerStudent() {
        passwordIni.setError(null);
        enrollmentNo.setError(null);
        email.setError(null);
        name.setError(null);


        String nameSt = name.getText().toString();
        String emailSt = email.getText().toString();
        String enrollmentSt = enrollmentNo.getText().toString();
        String password = passwordIni.getText().toString();



        boolean cancel = false;
        View focusView = null;
        //
        if (TextUtils.isEmpty(nameSt)){
            name.setError(getString(R.string.error_field_required));
            focusView = name;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            passwordIni.setError(getString(R.string.error_invalid_password));
            focusView = passwordIni;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(emailSt)) {
            email.setError(getString(R.string.error_field_required));
            focusView = email;
            cancel = true;
        } else if (!isEmailValid(emailSt)) {
            email.setError(getString(R.string.error_invalid_email));
            focusView = email;
            cancel = true;
        }
        //
        if (TextUtils.isEmpty(enrollmentSt)) {
            enrollmentNo.setError(getString(R.string.error_field_required));
            focusView = enrollmentNo;
            cancel = true;
        } else if (!isEnrollmentValid(enrollmentSt)) {
            enrollmentNo.setError(getString(R.string.error_invalid_enrollment_no));
            focusView = enrollmentNo;
            cancel = true;
        }
        //
        if (!switch01.isChecked() && !switch02.isChecked()) {
            showErrorDialog("Please select your current Semester");
            focusView = switch01;
            cancel = true;
        }
            //
            if (cancel) {
                // There was an error; don't attempt login and focus the first
                // form field with an error.
                focusView.requestFocus();
            } else {
                // TODO: Call create FirebaseUser() here
                createFirebaseUser();
            }
        }



        private void createFirebaseUser () {
            String emailStr = email.getText().toString();
            String passwordStr = passwordIni.getText().toString();

            mAuth.createUserWithEmailAndPassword(emailStr, passwordStr).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    Log.d("Vision", "createUser onComplete: " + task.isSuccessful());

                    if (!task.isSuccessful()) {
                        Log.d("Vision", "User creation failed");
                        showErrorDialog("Registration attempt failed");
                    } else {
                        saveDisplayDetails();
                        Intent intent = new Intent(Register.this, LoginActivity.class);
                        finish();
                        startActivity(intent);
                    }
                }
            });
        }


        private boolean isEnrollmentValid (String enrollmentSt){
            int chkFinal = 0;

            String clgCode = "0875";
            String subU = enrollmentSt.substring(4, 6).toUpperCase();

            Log.d("Vision",subU);
            int studentYear = Integer.parseInt(enrollmentSt.substring(6, 8));

            // get current year、month and day
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            year = year % 100;

            int length = enrollmentSt.length();

            if ( length== 12) {

                //College Code
                if (enrollmentSt.substring(0, 4).equals(clgCode))
                    chkFinal++;
                //Subject
                if (subU.equals("CS") || subU.equals("EC") || subU.equals("ME") || subU.equals("CE"))
                    chkFinal++;
                //Year
                if (studentYear <= year)
                    chkFinal++;

                return chkFinal == 3;
            }
            else return false;

        }


    private boolean isEmailValid(String emailString) {
        // You can add more checking logic here.
        return emailString.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Add own logic to check for a valid password (minimum 6 characters)
        String confirmPassword= passwordFin.getText().toString();

        return confirmPassword.equals(password)&&password.length()>6;
    }
    
    private void saveDisplayDetails() {


            String displayName = name.getText().toString();
            String enrollment_no = enrollmentNo.getText().toString();


            SharedPreferences preferences = getSharedPreferences(CHAT_PREFS,0);
            preferences.edit().putString(DISPLAY_NAME_KEY,displayName).apply();
            preferences.edit().putString(ENROLLMENT_NO_KEY,enrollment_no).apply();
            if(switchChecker==1)
                preferences.edit().putString(STUDENT_SEM_KEY,switch01.getText().toString()).apply();
            else
                preferences.edit().putString(STUDENT_SEM_KEY,switch02.getText().toString()).apply();


    }

    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Oops")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok,null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
