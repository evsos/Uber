package com.example.uber.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public
class Register extends AppCompatActivity {

    private EditText etName;
    private EditText etMail2;
    private EditText etPass2;
    private TextView tvPassenger;
    private TextView tvDriver;
    private Switch switch1;
    private Button btnRegister2;
    private FirebaseAuth mAuth;


    @Override
    protected
    void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_register);

        etName=findViewById (R.id.etName);
        etMail2=findViewById (R.id.etMail2);
        etPass2=findViewById (R.id.etPass2);
        tvPassenger=findViewById (R.id.tvPassenger);
        tvDriver=findViewById (R.id.tvDriver);
        switch1=findViewById (R.id.switch1);
        btnRegister2=findViewById (R.id.btnRegister2);
    }

    public void registarUsuario (View view) {

        //get several entered fields

        String name = etName.getText ().toString ().trim ();
        String mail = etMail2.getText ().toString ().trim ();
        String pass = etPass2.getText ().toString ().trim ();

        if (!name.isEmpty()) {
            //verifica o nome
            if (!mail.isEmpty()) {
                //verifica o email
                if (!pass.isEmpty()) {
                    //verifica a password

                    User user = new User();
                    user.setNome (name);
                    user.setEmail (mail);
                    user.setSenha (pass);
                    user.setTipo (verifyUserType());

                    registerUserInFirebase(user);


                }else{
                    Toast.makeText (this,"Preencha a password", Toast.LENGTH_LONG).show ();
                }

            }else{
                Toast.makeText (this,"Preencha o mail", Toast.LENGTH_LONG).show ();
            }

        }else{
            Toast.makeText (this,"Preencha o nome", Toast.LENGTH_LONG).show ();
        }

    }


    public String verifyUserType() {

            //ternary statement
        return switch1.isChecked () ? "D" : "P" ;  //Driver caso seja checked (mudou para driver), ou Passageiro caso nao tenha sido checkado

    }

    public void registerUserInFirebase (final User user){

            mAuth = FirebaseConfig.getFirebaseAuth ();
            mAuth.createUserWithEmailAndPassword (user.getEmail (),user.getSenha ()).addOnCompleteListener (new OnCompleteListener<AuthResult> () {
                @Override
                public
                void onComplete (@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful ()){
                       try {
                           String idUser = task.getResult ().getUser ().getUid ();
                           user.setId (idUser);
                           user.salvar ();

                           //actualizar nome no user profile
                           UsuarioFirebase.actualizarNomeUsuario(user.getNome ());


                           //redireccionar um user com base no seu tipo, se o user for um passageiro, direccionar logo para a maps activity, se nao, direccionar para
                           //a activity requisicoes

                           if ( verifyUserType() == "P"){
                               startActivity (new Intent (Register.this, MapsActivity.class));
                               finish ();
                               Toast.makeText (Register.this, "User registered successfully as a passenger",Toast.LENGTH_LONG).show ();
                           }else{
                               startActivity (new Intent (Register.this, RequestsActivity.class));
                               finish ();
                               Toast.makeText (Register.this, "User registered successfully as a driver",Toast.LENGTH_LONG).show ();
                           }

                       } catch (Exception e){e.printStackTrace ();}

                    }else{
                        String excecao="";
                        try {
                            throw task.getException ();
                        } catch (FirebaseAuthWeakPasswordException e) {
                            excecao="Register failed, please type a stronger password";
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            excecao="Please insert a valid email";
                        } catch (FirebaseAuthUserCollisionException e) {
                            excecao="Register failed, that account is already registered";
                        } catch (Exception e) {
                            excecao="Error registering user"+ e.getMessage ();
                            e.printStackTrace ();
                        }
                        Toast.makeText (Register.this,excecao,Toast.LENGTH_LONG).show ();


                    }
                }
            });

    }
}
