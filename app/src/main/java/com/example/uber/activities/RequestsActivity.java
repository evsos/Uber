package com.example.uber.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.uber.R;
import com.example.uber.config.FirebaseConfig;
import com.example.uber.model.Requisicao;
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
import androidx.recyclerview.widget.RecyclerView;

public
class RequestsActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference firebaseRef;
    private RecyclerView rvRequisicoes;
    private TextView tvAguardandoRequisicoes;
    private List<Requisicao> listaRequisicoes = new ArrayList<> ();

    @Override
    protected
    void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_requests);

        inicializarComponentes ();


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
        auth = FirebaseConfig.getFirebaseAuth ();
        firebaseRef = FirebaseConfig.getFirebaseDatabase ();

        getSupportActionBar ().setTitle ("Requisicoes");

        rvRequisicoes = findViewById (R.id.rvRequisicoes);
        tvAguardandoRequisicoes=findViewById (R.id.tvAguardandoRequisicoes);

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

                for(DataSnapshot ds: dataSnapshot.getChildren ()){
                    Requisicao requisicao = ds.getValue (Requisicao.class);
                    listaRequisicoes.add(requisicao);

                }
            }

            @Override
            public
            void onCancelled (@NonNull DatabaseError databaseError) {

            }
        })

    }
}
