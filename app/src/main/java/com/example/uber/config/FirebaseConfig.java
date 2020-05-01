package com.example.uber.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public
class FirebaseConfig {

    private static DatabaseReference databaseReference;
    private static FirebaseAuth auth;

        // return database instance
    public static DatabaseReference getFirebaseDatabase(){

        if (databaseReference == null){
            databaseReference= FirebaseDatabase.getInstance ().getReference ();
        }
        return databaseReference;
    }

    //return FirebaseAuth instance
    public static FirebaseAuth getFirebaseAuth() {

        if (auth == null){
        auth = FirebaseAuth.getInstance();

        }
        return auth;
}

}