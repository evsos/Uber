package com.example.uber.model;

import com.example.uber.config.FirebaseConfig;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

public
class User {

    private String id;
    private String nome;
    private String email;
    private String senha;
    private String tipo;

    public
    User () {
    }

    public void salvar(){
        DatabaseReference firebaseRef = FirebaseConfig.getFirebaseDatabase ();
        DatabaseReference users = firebaseRef.child ("users" ).child (getId ());
        users.setValue (this);
    }

    public
    User (String id, String nome, String email, String senha, String tipo) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.tipo = tipo;
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
    String getNome () {
        return nome;
    }

    public
    void setNome (String nome) {
        this.nome = nome;
    }

    public
    String getEmail () {
        return email;
    }

    public
    void setEmail (String email) {
        this.email = email;
    }

    @Exclude
    public
    String getSenha () {
        return senha;
    }

    public
    void setSenha (String senha) {
        this.senha = senha;
    }

    public
    String getTipo () {
        return tipo;
    }

    public
    void setTipo (String tipo) {
        this.tipo = tipo;
    }
}
