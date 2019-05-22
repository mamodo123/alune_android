package br.com.tibalt.tibalt.menu.ofertaProcura.abas;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.com.tibalt.tibalt.R;

public abstract class OfertaProcura extends Fragment {

    //d

    FloatingActionButton fab;
    int cor;

    public OfertaProcura() {
        // Required empty public constructor
    }


//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_oferta_procura, container, false);
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_oferta_procura, container, false);
        fab = v.findViewById(R.id.fab);
        v.setBackgroundColor(cor);
        plus();
        return v;
    }


    public void plus(){
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = choose();
                startActivity(intent);
            }

        });
    }

    public abstract Intent choose();
}
