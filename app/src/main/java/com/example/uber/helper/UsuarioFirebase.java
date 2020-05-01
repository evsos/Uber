package com.example.uber.helper;

import android.util.Log;

import com.example.uber.config.FirebaseConfig;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import androidx.annotation.NonNull;

public
class UsuarioFirebase {

    public static FirebaseUser getCurrentUser(){
        FirebaseAuth user = FirebaseConfig.getFirebaseAuth ();
        return user.getCurrentUser ();
    }

    public static
    boolean actualizarNomeUsuario (String nome) {

        try{
            FirebaseUser user = getCurrentUser ();
            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder ().setDisplayName (nome).build ();
            user.updateProfile (profileChangeRequest).addOnCompleteListener (new OnCompleteListener<Void> () {
                @Override
                public
                void onComplete (@NonNull Task<Void> task) {
                    if (!task.isSuccessful ()) {
                        Log.d("Perfil", "Erro ao actualizar nome de perfil");
                    }
                }

            });
            return true;
        }catch (Exception e) {
            e.printStackTrace ();
            return false;
        }

    }

}
