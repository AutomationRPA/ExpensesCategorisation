package com.example.scantext_gms;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    Button Login;
    EditText Name;
    EditText Password;
    TextView Info;
    //Button Login;
    int Counter = 3;
    TextView userRegistration;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        FirebaseUser user = firebaseAuth.getCurrentUser();
        setContentView(R.layout.activity_login);

        if (user != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            //finish();
        } else {
        }

        Name = (EditText) findViewById(R.id.etName1);
        Password = (EditText) findViewById(R.id.etPassword1);
        Info = (TextView) findViewById(R.id.tvInfo1);
        Login = (Button) findViewById(R.id.btnLogin1);
        userRegistration = (TextView) findViewById(R.id.tvRegister1);

        Info.setText("No.of attempts remaining: 3");

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate(Name.getText().toString(), Password.getText().toString());
            }
        });

        userRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
            }
        });
    }

    private void validate(String userName, String userPassword) {
        progressDialog.setMessage("verifying credentials");
        progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(userName, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    //finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                    Counter--;
                    Info.setText("No.of attempts remaining: " + Counter);
                    progressDialog.dismiss();
                    if (Counter == 0) {
                        Login.setEnabled(false);
                    }
                }
            }
        });
    }
}