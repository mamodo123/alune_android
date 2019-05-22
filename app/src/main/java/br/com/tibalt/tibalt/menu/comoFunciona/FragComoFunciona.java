package br.com.tibalt.tibalt.menu.comoFunciona;


import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.com.tibalt.tibalt.R;

public class FragComoFunciona extends Fragment {

    public FragComoFunciona() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle("Como funciona");
        return inflater.inflate(R.layout.fragment_frag_como_funciona, container, false);
    }

    @Override
    public void onResume(){
        super.onResume();

        NavigationView menuLateral = getActivity().findViewById(R.id.menuLateral);
        menuLateral.getMenu().getItem(1).setChecked(true);
    }

}
