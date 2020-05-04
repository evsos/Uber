package com.example.uber.helper;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.uber.activities.PassageiroActivity;
import com.example.uber.activities.RequestsActivity;
import com.example.uber.config.FirebaseConfig;
import com.example.uber.model.User;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;

public
class UsuarioFirebase {

    public static
    FirebaseUser getCurrentUser () {
        FirebaseAuth user = FirebaseConfig.getFirebaseAuth ();
        return user.getCurrentUser ();
    }

    public static User getDadosUsuarioLogado (){
        FirebaseUser firebaseUser = getCurrentUser ();
        User user = new User ();
        user.setId(firebaseUser.getUid ());
        user.setEmail (firebaseUser.getEmail ());
        user.setNome (firebaseUser.getDisplayName ());

        return user;
    }

    public static
    boolean actualizarNomeUsuario (String nome) {

        try {
            FirebaseUser user = getCurrentUser ();
            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder ().setDisplayName (nome).build ();
            user.updateProfile (profileChangeRequest).addOnCompleteListener (new OnCompleteListener<Void> () {
                @Override
                public
                void onComplete (@NonNull Task<Void> task) {
                    if (!task.isSuccessful ()) {
                        Log.d ("Perfil", "Erro ao actualizar nome de perfil");
                    }
                }
            });
            return true;
        } catch (Exception e) {
            e.printStackTrace ();
            return false;
        }

    }

    public static
    void redirectLoggedInUser (final Activity activity) {

        FirebaseUser user = getCurrentUser ();
        if (user != null) {

            DatabaseReference userRef = FirebaseConfig.getFirebaseDatabase ().child ("users").child (getCurrentUser ().getUid ());
            userRef.addListenerForSingleValueEvent (new ValueEventListener () {
                @Override
                public
                void onDataChange (@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue (User.class);
                    String userType = user.getTipo ();
                    if (userType.equals ("P")) {
                        activity.startActivity (new Intent (activity, PassageiroActivity.class));
                        Toast.makeText (activity, "Logged in sucessfully as a passenger", Toast.LENGTH_LONG).show ();

                    } else {
                        activity.startActivity (new Intent (activity, RequestsActivity.class));
                        Toast.makeText (activity, "Logged in sucessfully as a driver", Toast.LENGTH_LONG).show ();
                    }
                }

                @Override
                public
                void onCancelled (@NonNull DatabaseError databaseError) {

                }
            });


        }
    }

    public static void actualizarDadosLocalizacao(double lat, double lon){

        //define nó de local de usuario
        DatabaseReference localUsuario=FirebaseConfig.getFirebaseDatabase ().child ("local_usuario");
        GeoFire geofire = new GeoFire(localUsuario);

        //recupera dados do usuario logado
        User usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado ();

        //configura localizacao do usuario
        geofire.setLocation(usuarioLogado.getId (), new GeoLocation (lat, lon), new GeoFire.CompletionListener () {
            @Override
            public
            void onComplete (String key, DatabaseError error) {
                    if (error != null){
                        Log.d("Erro","Erro ao salvar localizacao");
                    }
            }
        });

    }
}
