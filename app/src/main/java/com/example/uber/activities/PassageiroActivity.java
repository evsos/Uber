package com.example.uber.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.example.uber.R;
import com.example.uber.config.FirebaseConfig;
import com.example.uber.helper.UsuarioFirebase;
import com.example.uber.model.Destino;
import com.example.uber.model.Requisicao;
import com.example.uber.model.User;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

public
class PassageiroActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FirebaseAuth auth;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private EditText etDestination;
    private LatLng localPassageiro;





    @Override
    protected
    void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_passageiro);

        inicializarComponentes ();
    }

    @Override
    public
    void onMapReady (GoogleMap googleMap) {

        mMap = googleMap;

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

                localPassageiro = new LatLng ( latitude, longitude);
                mMap.clear ();
                mMap.addMarker (new MarkerOptions ().position (localPassageiro).title ("my place").icon (BitmapDescriptorFactory.fromResource (R.drawable.usuario)));
                mMap.moveCamera (CameraUpdateFactory.newLatLngZoom (localPassageiro, 19));
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

    public void callUber (View view){

    String enderecoDestino = etDestination.getText ().toString ();
    if (!enderecoDestino.equals ("") || enderecoDestino != null){

        Address addressDestino = recuperarEndereco (enderecoDestino);

        if(addressDestino != null ){

            final Destino destino = new Destino();
            destino.setCidade (addressDestino.getAdminArea());
            destino.setCep(addressDestino.getPostalCode());
            destino.setBairro(addressDestino.getSubLocality ());
            destino.setRua (addressDestino.getThoroughfare ());
            destino.setNumero (addressDestino.getFeatureName ());
            destino.setLatitude (String.valueOf (addressDestino.getLatitude ()));
            destino.setLongitude (String.valueOf (addressDestino.getLongitude ()));

            StringBuilder mensagem = new StringBuilder ();
            mensagem.append ("Cidade:"+destino.getCidade ());
            mensagem.append ("\nRua:"+destino.getRua ());
            mensagem.append ("\nBairro:"+destino.getBairro());
            mensagem.append ("\nNúmero:"+destino.getNumero ());
            mensagem.append ("\nCep:"+destino.getCep ());

            AlertDialog.Builder builder = new AlertDialog.Builder (this);
            builder.setTitle ("Confirme seu endereco");
            builder.setMessage (mensagem);
            builder.setPositiveButton ("Confirmar", new DialogInterface.OnClickListener () {
                @Override
                public
                void onClick (DialogInterface dialog, int which) {

                    //salvar a requisicao
                    salvarRequisicao (destino);

                }
            });
            builder.setNegativeButton ("Cancelar", new DialogInterface.OnClickListener () {
                @Override
                public
                void onClick (DialogInterface dialog, int which) {

                }
            });
            AlertDialog dialog = builder.create ();
            dialog.show ();
        }

    }

    }

    private void salvarRequisicao(Destino destino){

        Requisicao requisicao = new Requisicao ();
        requisicao.setDestino (destino);

        User usuarioPassageiro = UsuarioFirebase.getDadosUsuarioLogado();

        usuarioPassageiro.setLatitude (String.valueOf (localPassageiro.latitude));
        usuarioPassageiro.setLongitude (String.valueOf (localPassageiro.longitude));

        requisicao.setPassageiro (usuarioPassageiro);
        requisicao.setStatus (Requisicao.STATUS_AGUARDANDO);

        requisicao.salvar();
    }


    private Address recuperarEndereco(String endereco){
        Geocoder geocoder = new Geocoder (this, Locale.getDefault ());
        try {
            List<Address> listaEnderecos = geocoder.getFromLocationName (endereco,1);
            if(listaEnderecos!=null && listaEnderecos.size ()>0){
                Address address = listaEnderecos.get(0);

                return address;

            }
        } catch (IOException e) {
            e.printStackTrace ();
        }

        return null;
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

private void inicializarComponentes(){
    Toolbar toolbar = findViewById (R.id.toolbar);
    toolbar.setTitle ("Iniciar uma viagem");
    setSupportActionBar (toolbar);

    //inicializar componentes

    etDestination =findViewById (R.id.etDestination);



    auth = FirebaseConfig.getFirebaseAuth ();


    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager ().findFragmentById (R.id.map);
    mapFragment.getMapAsync (this);



}
}
