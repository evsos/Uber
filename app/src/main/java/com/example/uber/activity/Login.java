package com.example.uber.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.uber.R;
import com.example.uber.config.FirebaseConfig;
import com.example.uber.helper.UsuarioFirebase;
import com.example.uber.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public
class Login extends AppCompatActivity {

    private EditText etLoginMail;
    private EditText etLoginPass;
    private Button btnLogin2;
    private FirebaseAuth auth;

    @Override
    protected
    void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_login);

        etLoginMail = findViewById (R.id.etLoginMail);
        etLoginPass = findViewById (R.id.etLoginPass);
        btnLogin2= findViewById (R.id.btnLogin2);



    }

    public void validarLoginUsuario(View view){

        //recuperar textos dos campos
        String email = etLoginMail.getText ().toString ().trim ();
        String pass = etLoginPass.getText ().toString ().trim ();

        if (!email.isEmpty()){
            if (!pass.isEmpty()){

                User user = new User();
                user.setEmail (email);
                user.setSenha (pass);

                loginUser(user);

            }else{
                Toast.makeText (Login.this,"Preencha a password!",Toast.LENGTH_LONG).show ();
            }
        }else{
            Toast.makeText (Login.this,"Preencha o email!",Toast.LENGTH_LONG).show ();
        }
    }

    public void loginUser(User user){

        auth = FirebaseConfig.getFirebaseAuth ();
        auth.signInWithEmailAndPassword (user.getEmail (),user.getSenha ()).addOnCompleteListener (new OnCompleteListener<AuthResult> () {
            @Override
            public
            void onComplete (@NonNull Task<AuthResult> task) {
                if ( task.isSuccessful ()){
                    //Verificar o tipo de usuario logado e direccionar para a respectiva activity
                    UsuarioFirebase.redirectLoggedInUser(Login.this);

                }else{
                    String excecao="";
                    try {
                        throw task.getException ();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        excecao="Login failed, please type a stronger password";
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        excecao="Login failed, please insert a valid email";
                    } catch (Exception e) {
                        excecao="Error loging in"+ e.getMessage ();
                        e.printStackTrace ();
                    }
                    Toast.makeText (Login.this,excecao,Toast.LENGTH_LONG).show ();
                }
            }
        });

    }

}
