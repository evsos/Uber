package com.example.uber.activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.uber.R;
import com.example.uber.config.FirebaseConfig;
import com.example.uber.model.Requisicao;
import com.example.uber.model.User;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

public
class CorridaActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Button btnAceitarCorrida;
    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private LatLng localMotorista;
    private LatLng localPassageiro;
    private User motorista;
    private User passageiro;
    private String idRequisicao;
    private Requisicao requisicao;
    private DatabaseReference firebaseRef;
    private Marker  marcadorMotorista;
    private Marker marcadorPassageiro;


    @Override
    protected
    void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_corrida);

       inicializarComponentes();

       //recupera dados do usuario
        if (getIntent().getExtras ().containsKey ("idRequisicao") && getIntent ().getExtras ().containsKey ("motorista")){
            Bundle extras = getIntent ().getExtras ();
            motorista = (User) extras.getSerializable ("motorista");
            idRequisicao =extras.getString(("idRequisicao"));

            verificaStatusRequisicao();
        }
    }

    private void verificaStatusRequisicao(){

        DatabaseReference requisicoes = firebaseRef.child ("requisicoes").child (idRequisicao);
        requisicoes.addValueEventListener (new ValueEventListener () {
            @Override
            public
            void onDataChange (@NonNull DataSnapshot dataSnapshot) {


                //Recupera requisicao
                requisicao = dataSnapshot.getValue (Requisicao.class);

                passageiro=requisicao.getPassageiro ();

                localPassageiro=new LatLng (Double.parseDouble (passageiro.getLatitude ()),Double.parseDouble (passageiro.getLongitude ()));

                switch(requisicao.getStatus ()){
                    case Requisicao.STATUS_AGUARDANDO: requisicaoAguardando();
                        break;
                    case Requisicao.STATUS_CAMINHO:requisicaoCaminho();
                        break;

                }
            }

            @Override
            public
            void onCancelled (@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void requisicaoAguardando(){
        btnAceitarCorrida.setText ("Aceitar corrida");
    }

    private void requisicaoCaminho(){
        btnAceitarCorrida.setText ("A caminho do passageiro");

        //exibir o motorista e o passageiro centrados na mesma tela

        //exibe marcador do motorista
        adicionarMarcadorMotorista(localMotorista,motorista.getNome ());

        // exibe marcador do passageiro

        adicionarMarcadorPassageiro (localPassageiro,passageiro.getNome ());

        //centralizar os dois marcadores

        centralizarDoisMarcadores(marcadorMotorista,marcadorPassageiro);
    }


    private void adicionarMarcadorMotorista(LatLng localizacao, String titulo){

        if(marcadorMotorista!=null)
            marcadorMotorista.remove ();


        marcadorMotorista = mMap.addMarker (new MarkerOptions ().position (localizacao).title (titulo).icon (BitmapDescriptorFactory.fromResource (R.drawable.carro)));


    }

    private void adicionarMarcadorPassageiro(LatLng localizacao,String titulo){

        if(marcadorPassageiro!=null)
            marcadorPassageiro.remove ();


        marcadorPassageiro = mMap.addMarker (new MarkerOptions ().position (localizacao).title (titulo).icon (BitmapDescriptorFactory.fromResource (R.drawable.usuario)));

    }

    private void centralizarDoisMarcadores(Marker marcador1, Marker marcador2){

        LatLngBounds.Builder builder = new LatLngBounds.Builder ();


        //poderia fazerse esta etapa com um for para usar varios marcadores
        builder.include (marcador1.getPosition ());
        builder.include (marcador2.getPosition ());

        LatLngBounds bounds = builder.build ();

        int largura = getResources ().getDisplayMetrics ().widthPixels;
        int altura = getResources ().getDisplayMetrics ().heightPixels;
        int espacoInterno = (int ) (largura*0.20);

        mMap.moveCamera (CameraUpdateFactory.newLatLngBounds (bounds,largura,altura,espacoInterno));

    }





   public void aceitarCorrida(View view){

        // configura requisicao
        requisicao = new Requisicao();
        requisicao.setId (idRequisicao);
        requisicao.setMotorista (motorista);
        requisicao.setStatus(Requisicao.STATUS_CAMINHO);

        requisicao.actualizar ();
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

             btnAceitarCorrida=findViewById (R.id.btnAceitarCorrida);
            Toolbar toolbar = findViewById (R.id.toolbar);
            setSupportActionBar (toolbar);
            getSupportActionBar ().setDisplayHomeAsUpEnabled (true);
            getSupportActionBar ().setTitle ("Iniciar corrida");

            //Configuracoes iniciais
            firebaseRef=FirebaseConfig.getFirebaseDatabase ();





            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager ().findFragmentById (R.id.map);
            mapFragment.getMapAsync (this);
        }

}
