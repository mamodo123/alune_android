package br.com.tibalt.tibalt.menu.ofertaProcura.abas;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import br.com.tibalt.tibalt.menu.ofertaProcura.abas.publicar.Ofertar;

public class FragOferta extends OfertaProcura {
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View v = inflater.inflate(R.layout.fragment_oferta_procura, container, false);
//        fab = v.findViewById(R.id.fab);
//        v.setBackgroundColor(Color.BLUE);
//        plus();
//        return v;
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        cor = Color.BLUE;
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public Intent choose(){
        return new Intent(getContext(), Ofertar.class);
    }
}
