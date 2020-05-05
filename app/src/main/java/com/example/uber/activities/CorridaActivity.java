package com.example.uber.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.uber.R;
import com.example.uber.config.FirebaseConfig;
import com.example.uber.helper.UsuarioFirebase;
import com.example.uber.model.Requisicao;
import com.example.uber.model.User;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
    private String statusRequisicao;
    private Boolean requisicaoActiva;
    private FloatingActionButton fabRota;



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
            localMotorista= new LatLng (Double.parseDouble (motorista.getLatitude ()),Double.parseDouble (motorista.getLongitude ()));
            idRequisicao =extras.getString(("idRequisicao"));
            requisicaoActiva =extras.getBoolean(("requisicaoActiva"));
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

                if (requisicao!=null){
                passageiro=requisicao.getPassageiro ();

                localPassageiro=new LatLng (Double.parseDouble (passageiro.getLatitude ()),Double.parseDouble (passageiro.getLongitude ()));

                statusRequisicao=requisicao.getStatus ();
                alteraInterfaceConsoanteStatusRequisicao (statusRequisicao);
            }
            }

            @Override
            public
            void onCancelled (@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void alteraInterfaceConsoanteStatusRequisicao(String status){

        switch(status){
            case Requisicao.STATUS_AGUARDANDO:
                requisicaoAguardando();
                break;
            case Requisicao.STATUS_CAMINHO:
                requisicaoCaminho();
                break;

        }



    }

    private void requisicaoAguardando(){
        btnAceitarCorrida.setText ("Aceitar corrida");

        adicionarMarcadorMotorista(localMotorista,motorista.getNome ());

        mMap.moveCamera (CameraUpdateFactory.newLatLngZoom (localMotorista,20));

    }

    private void requisicaoCaminho(){
        btnAceitarCorrida.setText ("A caminho do passageiro");
        fabRota.setVisibility (View.VISIBLE);



        //exibe marcador do motorista
        adicionarMarcadorMotorista(localMotorista,motorista.getNome ());

        // exibe marcador do passageiro
        adicionarMarcadorPassageiro (localPassageiro,passageiro.getNome ());

        //centralizar os dois marcadores - exibe os 2 marcadores centrados na mesma tela
        centralizarDoisMarcadores(marcadorMotorista,marcadorPassageiro);

        //Inicia monitoramento do motorista/passageiro
        iniciarMonitoramentoCorrida (passageiro,motorista);

    }

    private void iniciarMonitoramentoCorrida(User p, User m){
        //inicializar Geofire
        DatabaseReference localUsuario = FirebaseConfig.getFirebaseDatabase ().child ("local_usuario");
        GeoFire geoFire=new GeoFire (localUsuario);

        //adicionar circulo no passageiro, quando o carro se aproximar do passageiro, seremos notificados
        final Circle circulo = mMap.addCircle (new CircleOptions ().center (localPassageiro).radius (50).fillColor(Color.argb (80, 255, 153, 0))
                                                 .strokeColor (Color.argb (190,255,152,0)));

        final GeoQuery geoQuery = geoFire.queryAtLocation (new GeoLocation (localPassageiro.latitude, localPassageiro.longitude), 0.05);
        geoQuery.addGeoQueryEventListener (new GeoQueryEventListener () {
            @Override
            public
            void onKeyEntered (String key, GeoLocation location) {
                //criterios para um marcador que entrou no circulo
                if (key.equals (motorista.getId ())) {
                   //altera status da requisicao
                    requisicao.setStatus (Requisicao.STATUS_VIAGEM);
                    requisicao.actualizarStatus ();

                    //remove listener
                    geoQuery.removeAllListeners ();
                    circulo.remove ();
                }
            }

            @Override
            public
            void onKeyExited (String key) {

            }

            @Override
            public
            void onKeyMoved (String key, GeoLocation location) {

            }

            @Override
            public
            void onGeoQueryReady () {

            }

            @Override
            public
            void onGeoQueryError (DatabaseError error) {

            }
        });

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

                UsuarioFirebase.actualizarDadosLocalizacao (latitude,longitude);

                alteraInterfaceConsoanteStatusRequisicao (statusRequisicao);

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

            //Adiciona evento de clique no (Floating Action Button) FabRota
            fabRota = findViewById (R.id.fabRota);
            fabRota.setOnClickListener (new View.OnClickListener () {
                @Override
                public
                void onClick (View v) {

                    String status = statusRequisicao;
                    if (status != null && !status.isEmpty ()){

                    String lat ="";
                    String lon="";


                    switch(status){
                        case Requisicao.STATUS_CAMINHO:
                            lat = String.valueOf (localPassageiro.latitude);
                            lon = String.valueOf (localPassageiro.longitude);
                            break;
                       case Requisicao.STATUS_VIAGEM:
                           // lat = String.valueOf (localDestino.latitude);
                           // lon = String.valueOf (localDestino.longitude);
                            break;
                    }
                        //se o status for a caminho gerar rota do local do motorista ate ao local do passageiro
                        String latlong = lat +","+lon;
                        Uri uri=Uri.parse ("google.navigation:q="+latlong+"&mode=d");
                        Intent i = new Intent(Intent.ACTION_VIEW, uri);
                        i.setPackage("com.google.android.apps.maps");
                        startActivity(i);


                    }
                    //se o status for em viagem, gerar rota do local do passageiro ate ao destino
                }
            });


        }


    @Override
    public
    boolean onSupportNavigateUp () {
        if (requisicaoActiva){
            Toast.makeText (CorridaActivity.this,"Necessario encerrar a requisicao actual!",Toast.LENGTH_LONG).show ();
        }else{
            Intent i = new Intent(CorridaActivity.this,RequestsActivity.class);
            startActivity (i);
        }

        return false;
    }
}
