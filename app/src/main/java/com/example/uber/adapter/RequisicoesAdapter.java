package com.example.uber.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.uber.R;
import com.example.uber.model.Requisicao;
import com.example.uber.model.User;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RequisicoesAdapter extends RecyclerView.Adapter<RequisicoesAdapter.MyViewHolder> {

    private List<Requisicao> requisicoes;
    private Context context;
    private User motorista;

    public RequisicoesAdapter (List<Requisicao> requisicoes, Context context, User motorista) {
        this.requisicoes = requisicoes;
        this.context = context;
        this.motorista = motorista;
    }

    @NonNull
    @Override
    public
    MyViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext ()).inflate (R.layout.adapter_requisicoes,parent, false);


        return new MyViewHolder (item);
    }

    @Override
    public
    void onBindViewHolder (@NonNull MyViewHolder holder, int position) {

        Requisicao requisicao = requisicoes.get(position);
        User passageiro = requisicao.getPassageiro ();


        holder.nome.setText (passageiro.getNome ());
        holder.distancia.setText ("1 km - aproximadamente");

    }

    @Override
    public
    int getItemCount () {
        return requisicoes.size ();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView nome, distancia;

        public MyViewHolder(View itemView){
            super(itemView);

            nome=itemView.findViewById (R.id.tvRequisicaoNome);
            distancia=itemView.findViewById (R.id.tvRequisicaoDistancia);

        }
    }

}
