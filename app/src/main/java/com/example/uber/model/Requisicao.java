package com.example.uber.model;

import com.example.uber.config.FirebaseConfig;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

public
class Requisicao {

    private String id;
    private String status;
    // para saber se essa requisicao ja esta sendo atendida por um motorista ou nao,
    // se o passageirro ja chamou o Uber e esta aguardando o motorista aceitar(status: AGUARDANDO),
    // se o motorista ja aceitou a viagem e esta a caminho(status: CAMINHO),
    // se o motorista e o passageiro já estao em viagem (status: VIAGEM
    //se a viagem ja foi concluida(status: FINALIZADA)
    private User passageiro;
    private User motorista;
    private Destino destino;

    public static final String STATUS_AGUARDANDO="aguardando";
    public static final String STATUS_CAMINHO="caminho";
    public static final String STATUS_VIAGEM="viagem";
    public static final String STATUS_FINALIZADA="finalizada";

    public
    Requisicao () {
    }

    public void salvar(){
        DatabaseReference firebaseRef = FirebaseConfig.getFirebaseDatabase ();
        DatabaseReference requisicoes = firebaseRef.child ("requisicoes");
        String idRequisicao = requisicoes.push().getKey ();
        setId (idRequisicao);

        requisicoes.child (getId ()).setValue(this);
    }

    public void actualizar(){
        DatabaseReference firebaseRef = FirebaseConfig.getFirebaseDatabase ();
        DatabaseReference requisicoes = firebaseRef.child ("requisicoes");

        DatabaseReference requisicao=requisicoes.child(getId ());
        Map objecto = new HashMap ();
        objecto.put("motorista", getMotorista ());
        objecto.put("status",getStatus ());

        requisicao.updateChildren (objecto);
    }

    public void actualizarLocalizacaoMotorista(){
        DatabaseReference firebaseRef = FirebaseConfig.getFirebaseDatabase ();
        DatabaseReference requisicoes = firebaseRef.child ("requisicoes");

        DatabaseReference requisicao=requisicoes.child(getId ()).child("motorista");
        Map objecto = new HashMap ();
        objecto.put("latitude", getMotorista ().getLatitude ());
        objecto.put("longitude",getMotorista ().getLongitude ());

        requisicao.updateChildren (objecto);
    }

    public void actualizarStatus(){
        DatabaseReference firebaseRef = FirebaseConfig.getFirebaseDatabase ();
        DatabaseReference requisicoes = firebaseRef.child ("requisicoes");

        DatabaseReference requisicao=requisicoes.child(getId ());
        Map objecto = new HashMap ();
        objecto.put("status",getStatus ());

        requisicao.updateChildren (objecto);
    }

    public
    String getId () {
        return id;
    }

    public
    void setId (String id) {
        this.id = id;
    }

    public
    String getStatus () {
        return status;
    }

    public
    void setStatus (String status) {
        this.status = status;
    }

    public
    User getPassageiro () {
        return passageiro;
    }

    public
    void setPassageiro (User passageiro) {
        this.passageiro = passageiro;
    }

    public
    User getMotorista () {
        return motorista;
    }

    public
    void setMotorista (User motorista) {
        this.motorista = motorista;
    }

    public
    Destino getDestino () {
        return destino;
    }

    public
    void setDestino (Destino destino) {
        this.destino = destino;
    }
}
