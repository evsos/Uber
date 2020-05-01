package com.example.uber.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.uber.R;
import com.example.uber.config.FirebaseConfig;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public
class PassageiroActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FirebaseAuth auth;


    @Override
    protected
    void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_passageiro);
        Toolbar toolbar = findViewById (R.id.toolbar);
        toolbar.setTitle ("Iniciar uma viagem");
        setSupportActionBar (toolbar);

        auth = FirebaseConfig.getFirebaseAuth ();


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager ().findFragmentById (R.id.map);
        mapFragment.getMapAsync (this);

    }

    @Override
    public
    void onMapReady (GoogleMap googleMap) {

        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng (-34, 151);
        mMap.addMarker (new MarkerOptions ().position (sydney).title ("Marker in Sydney"));
        mMap.moveCamera (CameraUpdateFactory.newLatLng (sydney));
    }

    @Override
    public
    boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater ().inflate (R.menu.menu_main,menu);
        return true;
    }

    @Override
    public
    boolean onOptionsItemSelected (MenuItem item) {
        switch(item.getItemId ()){
            case R.id.menuSair:
                auth.signOut ();
                finish ();
                break;
        }
        return super.onOptionsItemSelected (item);
    }
}
