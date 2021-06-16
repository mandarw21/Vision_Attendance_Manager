package com.example.darkarmy.vision_login_01;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.jaeger.library.StatusBarUtil;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

//
    static final String TEACH_PREFS = "TeachPrefs";
    static final String IS_TEACHER_KEY = "isTeacher";

    private Button buttonSignin;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button btnSignup;
    //private ProgressDialog progressDialog;
    private Switch switchTeacher;
    private FirebaseAuth firebaseAuth;
    private String isTeacherChk;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        StatusBarUtil.setTransparent(LoginActivity.this);
        switchTeacher = (Switch)findViewById(R.id.teacher_switch);

        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() !=null)
        {
            //Profile activity here
            finish();
            SharedPreferences prefs = getSharedPreferences(LoginActivity.TEACH_PREFS, MODE_PRIVATE);
            isTeacherChk = prefs.getString(LoginActivity.IS_TEACHER_KEY,null);
            try{
            if (Objects.equals(isTeacherChk, "isTeacher")){
                startActivity(new Intent(getApplicationContext(), TeacherHome.class));
            }else {
                startActivity(new Intent(getApplicationContext(), StudentHome.class));
            }
            }catch (Exception e){
                showErrorDialog("There was a problem signing in ");

            }


        }
        editTextEmail = (EditText) findViewById(R.id.emailId);
        editTextPassword = (EditText) findViewById(R.id.password);
        buttonSignin = (Button) findViewById(R.id.sign_in_btn);
        btnSignup = (Button) findViewById(R.id.register_btn);

        buttonSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userLogin();
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(LoginActivity.this, Register.class));
            }
        });

   }
    private void userLogin()
    {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)||TextUtils.isEmpty(password)) {
            //Email or Password is empty
            Toast.makeText(this, "Please Enter Email and Password", Toast.LENGTH_LONG).show();
            return;
        }


//        progressDialog.setMessage("Signing In Please  Wait...");
//        progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //progressDialog.dismiss();

                       if (!task.isSuccessful()) {
                            Log.d("Vision", "Problem signing in: " + task.getException());
                            showErrorDialog("There was a problem signing in");
                        }
                        else {
                            //start the profile activity
                            finish();
                            if (switchTeacher.isChecked()){
                                teacherChecker();
                                startActivity(new Intent(getApplicationContext(), TeacherHome.class));
                            }else {
                                startActivity(new Intent(getApplicationContext(), StudentHome.class));
                            }
                        }
                    }
                });
    }

    private void teacherChecker() {
       String isTeacher ="isTeacher";
        SharedPreferences prefs = getSharedPreferences(TEACH_PREFS, 0);
        prefs.edit().putString(IS_TEACHER_KEY, isTeacher).apply();
    }

    // TODO: Show error on screen with an alert dialog
    private void showErrorDialog(String message) {

        new AlertDialog.Builder(this)
                .setTitle("Oops")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


}

