package com.example.uber.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.uber.R;
import com.example.uber.helper.Permissoes;
import com.example.uber.helper.UsuarioFirebase;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public
class MainActivity extends AppCompatActivity {

    private ImageView ivUber;
    private RelativeLayout rLayout;
    private Button btnLogin;
    private Button btnRegister;
    private FirebaseAuth auth;
    private String[] permissoes = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};

    @Override
    protected
    void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);

        getSupportActionBar ().hide ();

        //validar permissoes de acesso a localizacao do usuario
        Permissoes.validarPermissoes (permissoes,MainActivity.this, 1);

        /*auth = FirebaseConfig.getFirebaseAuth ();
        auth.signOut ();*/

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

    @Override
    public
    void onRequestPermissionsResult (int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult (requestCode, permissions, grantResults);

        for(int permissaoResultado : grantResults){

            if (permissaoResultado == getPackageManager().PERMISSION_DENIED){
                    alertaValidacaoPermissao();
            }
        }
    }

    public void alertaValidacaoPermissao(){
        AlertDialog.Builder builder = new AlertDialog.Builder (this);
        builder.setTitle ("Permissoes negadas");
        builder.setMessage ("Tem de aceitar as permissoes de acesso à localizacão");
        builder.setCancelable (false);
        builder.setPositiveButton ("Confirmar", new DialogInterface.OnClickListener () {
            @Override
            public
            void onClick (DialogInterface dialog, int which) {
                finish ();
            }
        });

        AlertDialog dialog=builder.create ();
        dialog.show ();










    }
}
