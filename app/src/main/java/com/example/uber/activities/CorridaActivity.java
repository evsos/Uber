package com.example.uber.activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;

import com.example.uber.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

public
class CorridaActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private LatLng localMotorista;


    @Override
    protected
    void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_corrida);

       inicializarComponentes();

    }
   public void aceitarCorrida(View view){

   }

    @Override
    public
    void onMapReady (GoogleMap googleMap) {

        mMap = googleMap;

        //recuperar localizacao do usuario
        getUserLocation ();
    }
    private
    void getUserLocation () {

        locationManager = (LocationManager) this.getSystemService (Context.LOCATION_SERVICE);

        locationListener = new LocationListener () {
            @Override
            public
            void onLocationChanged (Location location) {

                double latitude = location.getLatitude ();
                double longitude = location.getLongitude ();

                localMotorista = new LatLng ( latitude, longitude);
                mMap.clear ();
                mMap.addMarker (new MarkerOptions ().position (localMotorista).title ("my place").icon (BitmapDescriptorFactory.fromResource (R.drawable.carro)));
                mMap.moveCamera (CameraUpdateFactory.newLatLngZoom (localMotorista, 19));
            }

            @Override
            public
            void onStatusChanged (String provider, int status, Bundle extras) {

            }

            @Override
            public
            void onProviderEnabled (String provider) {

            }

            @Override
            public
            void onProviderDisabled (String provider) {

            }
        };

        if (ActivityCompat.checkSelfPermission (this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates (LocationManager.GPS_PROVIDER, 10000, 10, locationListener);
        }
    }
        private void inicializarComponentes() {
            Toolbar toolbar = findViewById (R.id.toolbar);
            setSupportActionBar (toolbar);
            getSupportActionBar ().setDisplayHomeAsUpEnabled (true);
            getSupportActionBar ().setTitle ("Iniciar corrida");

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager ().findFragmentById (R.id.map);
            mapFragment.getMapAsync (this);
        }

}
