package com.example.runduo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginScreen extends AppCompatActivity {

    private Button LoginButton;
    private EditText LoginPW , LoginEmail;
    AwesomeValidation Valid;
    private FirebaseAuth fireAuthi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        LoginButton = findViewById(R.id.loginA);
        LoginEmail = findViewById(R.id.EmailA);
        LoginPW = findViewById(R.id.PwA);
        fireAuthi = FirebaseAuth.getInstance();

        Valid = new AwesomeValidation(ValidationStyle.BASIC);
        Valid.addValidation(this,R.id.EmailA, Patterns.EMAIL_ADDRESS,R.string.invalid_email);
        Valid.addValidation(this,R.id.PwA, ".{6,}",R.string.invalid_pw);

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Valid.validate()) {
                    CheckWithDatabase();
                }
                else {
                    Toast.makeText(LoginScreen.this,"Login Failed",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void CheckWithDatabase()
    {
        fireAuthi.signInWithEmailAndPassword(LoginEmail.getText().toString(), LoginPW.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginScreen.this,"Login Success",Toast.LENGTH_SHORT).show();
                            Intent ScreenNext = new Intent(LoginScreen.this,MainActivity.class);
                            startActivity(ScreenNext);
                            finish();
                        }
                    }
                });
    }
}
