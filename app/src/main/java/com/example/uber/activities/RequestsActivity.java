package com.example.uber.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.example.uber.R;
import com.example.uber.adapter.RequisicoesAdapter;
import com.example.uber.config.FirebaseConfig;
import com.example.uber.helper.RecyclerItemClickListener;
import com.example.uber.helper.UsuarioFirebase;
import com.example.uber.model.Requisicao;
import com.example.uber.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public
class RequestsActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference firebaseRef;
    private RecyclerView rvRequisicoes;
    private TextView tvAguardandoRequisicoes;
    private List<Requisicao> listaRequisicoes = new ArrayList<> ();
    private RequisicoesAdapter adapter;
    private User motorista;
    private LocationManager locationManager;
    private LocationListener locationListener;

    @Override
    protected
    void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_requests);

        inicializarComponentes ();

        //recuperar localizacao do usuario
        getUserLocation ();


    }

    @Override
    public
    boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater ().inflate (R.menu.menu_main, menu);
        return true;
    }

    @Override
    public
    boolean onOptionsItemSelected (MenuItem item) {
        switch (item.getItemId ()) {
            case R.id.menuSair:
                auth.signOut ();
                finish ();
                break;
        }
        return super.onOptionsItemSelected (item);
    }

    private
    void inicializarComponentes () {

        //Configuracoes iniciais
        motorista = UsuarioFirebase.getDadosUsuarioLogado();

        getSupportActionBar ().setTitle ("Requisicões");

        rvRequisicoes = findViewById (R.id.rvRequisicoes);
        tvAguardandoRequisicoes=findViewById (R.id.tvAguardandoRequisicoes);

        auth = FirebaseConfig.getFirebaseAuth ();
        firebaseRef = FirebaseConfig.getFirebaseDatabase ();

        //configurar Recycler View
        adapter=new RequisicoesAdapter (listaRequisicoes,getApplicationContext (),motorista);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager (getApplicationContext ());
        rvRequisicoes.setLayoutManager (layoutManager);
        rvRequisicoes.setHasFixedSize (true);
        rvRequisicoes.setAdapter(adapter);

        //Adiciona evento de clique no recycler
        rvRequisicoes.addOnItemTouchListener (new RecyclerItemClickListener (getApplicationContext (), rvRequisicoes, new RecyclerItemClickListener.OnItemClickListener () {
            @Override
            public
            void onItemClick (View view, int position) {
                Requisicao requisicao = listaRequisicoes.get (position);
                Intent i = new Intent (RequestsActivity.this, CorridaActivity.class);
                i.putExtra ("idRequisicao",requisicao.getId ());
                i.putExtra ("motorista",motorista);
                startActivity (i);

            }

            @Override
            public
            void onLongItemClick (View view, int position) {

            }

            @Override
            public
            void onItemClick (AdapterView<?> parent, View view, int position, long id) {

            }
        }));

        recuperarRequisicoes();
    }


    private void recuperarRequisicoes(){
        DatabaseReference requisicoes = firebaseRef.child ("requisicoes");
        Query requisicaoPesquisa = requisicoes.orderByChild ("status").
                equalTo (Requisicao.STATUS_AGUARDANDO);
        requisicaoPesquisa.addValueEventListener (new ValueEventListener () {

            @Override
            public
            void onDataChange (@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount ()>0){
                    tvAguardandoRequisicoes.setVisibility (View.GONE);
                    rvRequisicoes.setVisibility (View.VISIBLE);
                }else{
                    tvAguardandoRequisicoes.setVisibility (View.VISIBLE);
                    rvRequisicoes.setVisibility (View.GONE);
                }

                listaRequisicoes.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren ()){
                    Requisicao requisicao = ds.getValue (Requisicao.class);
                    listaRequisicoes.add(requisicao);

                }
                adapter.notifyDataSetChanged ();
            }

            @Override
            public
            void onCancelled (@NonNull DatabaseError databaseError) {

            }
        });

    }

    private
    void getUserLocation () {

        locationManager = (LocationManager) this.getSystemService (Context.LOCATION_SERVICE);

        locationListener = new LocationListener () {
            @Override
            public
            void onLocationChanged (Location location) {

                String latitude = String.valueOf (location.getLatitude ());
                String longitude = String.valueOf (location.getLongitude ());

                if(!latitude.isEmpty () && !longitude.isEmpty ()){
                    motorista.setLatitude (latitude);
                    motorista.setLongitude (longitude);
                    locationManager.removeUpdates (locationListener);

                }
                adapter.notifyDataSetChanged ();

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
            locationManager.requestLocationUpdates (LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }

}
