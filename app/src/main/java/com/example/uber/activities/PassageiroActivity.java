package com.example.uber.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
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
    private LatLng localMotorista;
    private LinearLayout linearLayoutDestino;
    private Button btnCallUber;
    private boolean uberChamado = false;
    private DatabaseReference firebaseRef;
    private Requisicao requisicao;
    private User passageiro;
    private String statusRequisicao;
    private Destino destino;
    private Marker marcadorMotorista;
    private Marker marcadorPassageiro;
    private Marker marcadorDestino;
    private User motorista;


    @Override
    protected
    void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_passageiro);



        inicializarComponentes ();

        //adicionar listener para status da requisicao
        verificaStatusRequisicao();
    }


    private void adicionarMarcadorPassageiro(LatLng localizacao,String titulo){

        if(marcadorPassageiro!=null)
            marcadorPassageiro.remove ();

        marcadorPassageiro = mMap.addMarker (new MarkerOptions ().position (localizacao).title (titulo).icon (BitmapDescriptorFactory.fromResource (R.drawable.usuario)));
    }

    private void centralizarMarcador(LatLng local){
        mMap.moveCamera (CameraUpdateFactory.newLatLngZoom (localPassageiro,20));
    }

    private void adicionarMarcadorMotorista(LatLng localizacao, String titulo){

        if(marcadorMotorista!=null)
            marcadorMotorista.remove ();


        marcadorMotorista = mMap.addMarker (new MarkerOptions ().position (localizacao).title (titulo).icon (BitmapDescriptorFactory.fromResource (R.drawable.carro)));
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



    private void verificaStatusRequisicao(){
        User usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado ();
        DatabaseReference requisicoes = firebaseRef.child("requisicoes");
        final Query requisicaoPesquisa = requisicoes.orderByChild ("passageiro/id").equalTo (usuarioLogado.getId ());
        requisicaoPesquisa.addValueEventListener (new ValueEventListener () {
            @Override
            public
            void onDataChange (@NonNull DataSnapshot dataSnapshot) {

                List<Requisicao> lista = new ArrayList<> ();
                for(DataSnapshot ds:dataSnapshot.getChildren ()){
                   lista.add(ds.getValue (Requisicao.class));
                }
            Collections.reverse (lista);
                if (lista!=null && lista.size()>0){


                requisicao=lista.get (0);

                    if (requisicao!=null){
                        passageiro=requisicao.getPassageiro ();

                        localPassageiro=new LatLng (Double.parseDouble (passageiro.getLatitude ()),Double.parseDouble (passageiro.getLongitude ()));


                        statusRequisicao=requisicao.getStatus ();
                        destino = requisicao.getDestino ();

                        if(requisicao.getMotorista() != null){
                            motorista = requisicao.getMotorista ();
                            localMotorista= new LatLng (Double.parseDouble (motorista.getLatitude ()),Double.parseDouble (motorista.getLongitude ()));

                        }


                        alteraInterfaceConsoanteStatusRequisicao (statusRequisicao);
                    }
                }
            }

            @Override
            public
            void onCancelled (@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void alteraInterfaceConsoanteStatusRequisicao(String status) {
        switch (requisicao.getStatus ()) {
                case Requisicao.STATUS_AGUARDANDO:
                    requisicaoAguardando();
                    break;
                case Requisicao.STATUS_CAMINHO:
                    requisicaoCaminho();
                    break;
                case Requisicao.STATUS_VIAGEM:
                    requisicaoViagem ();
                    break;
                case Requisicao.STATUS_FINALIZADA:
                    requisicaoFinalizada ();
                    break;
            }
        }


    private void requisicaoAguardando () {
        linearLayoutDestino.setVisibility (View.GONE);
        btnCallUber.setText ("Cancelar Requisicao de Uber");
        uberChamado = true;

        //adicionar marcador passageiro
        adicionarMarcadorPassageiro (localPassageiro,passageiro.getNome ());
        centralizarMarcador (localPassageiro);
    }

    private void requisicaoCaminho () {
        linearLayoutDestino.setVisibility (View.GONE);
        btnCallUber.setText ("Motorista a caminho");
        uberChamado = true;

        //adicionar marcador de passageiro
        adicionarMarcadorPassageiro (localPassageiro,passageiro.getNome ());

        //adicionar marcador de motorista
        adicionarMarcadorMotorista (localMotorista,motorista.getNome ());

        //centralizar os 2 marcadores
    centralizarDoisMarcadores (marcadorMotorista,marcadorPassageiro);




    }


    private void requisicaoViagem () {
    }


    private void requisicaoFinalizada () {
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

                //actualizar geofire
                UsuarioFirebase.actualizarDadosLocalizacao (latitude, longitude);



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

        if(!uberChamado) {


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
                            uberChamado = true;
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
        }else {

            //cancelar requisicao
            uberChamado=false;

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

        linearLayoutDestino.setVisibility (View.GONE);
        btnCallUber.setText ("Cancelar requisicao Uber");
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

    //Configuracoes iniciais
    auth = FirebaseConfig.getFirebaseAuth ();
    firebaseRef=FirebaseConfig.getFirebaseDatabase ();


    Toolbar toolbar = findViewById (R.id.toolbar);
    toolbar.setTitle ("Iniciar uma viagem");
    toolbar.setTitleTextColor(Color.WHITE);
    setSupportActionBar (toolbar);

    //inicializar componentes

    etDestination =findViewById (R.id.etDestination);
    linearLayoutDestino=findViewById (R.id.linearLayoutDestino);
    btnCallUber=findViewById (R.id.btnCallUber);






    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager ().findFragmentById (R.id.map);
    mapFragment.getMapAsync (this);



}
}
