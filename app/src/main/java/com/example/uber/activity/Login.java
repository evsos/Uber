package com.example.uber.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.example.uber.R;

import androidx.appcompat.app.AppCompatActivity;

public
class Login extends AppCompatActivity {

    private EditText etMail;
    private EditText etPass;
    private Button btnLogin2;

    @Override
    protected
    void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_login);

        etMail = findViewById (R.id.etMail);
        etPass = findViewById (R.id.etPass);
        btnLogin2= findViewById (R.id.btnLogin2);

    }
}
