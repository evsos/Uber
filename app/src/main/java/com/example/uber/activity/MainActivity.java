package com.example.uber.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.uber.R;
import com.example.uber.helper.UsuarioFirebase;

import androidx.appcompat.app.AppCompatActivity;

public
class MainActivity extends AppCompatActivity {

    private ImageView ivUber;
    private RelativeLayout rLayout;
    private Button btnLogin;
    private Button btnRegister;

    @Override
    protected
    void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);

        getSupportActionBar ().hide ();

        ivUber = findViewById (R.id.ivUber);
        rLayout = findViewById (R.id.rLayout);
        btnLogin = findViewById (R.id.btnLogin);
        btnRegister = findViewById (R.id.btnRegister);

    }

    public void abrirTelaLogin(View view) {
        startActivity (new Intent(this, Login.class));
    }

    public void abrirTelaCadastro (View view){
        startActivity (new Intent(this, Register.class));
    }

    @Override
    protected
    void onStart () {
        super.onStart ();

        UsuarioFirebase.redirectLoggedInUser (MainActivity.this);
    }
}
