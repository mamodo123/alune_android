package br.com.tibalt.tibalt.menu.ofertaProcura;


import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import br.com.tibalt.tibalt.R;
import br.com.tibalt.tibalt.utilitarios.ViewPagerAdapter;
import br.com.tibalt.tibalt.menu.ofertaProcura.abas.FragOferta;
import br.com.tibalt.tibalt.menu.ofertaProcura.abas.FragProcura;

public class FragAbas extends Fragment {

    public FragAbas() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_frag_abas, container, false);

        ViewPager viewPager = rootView.findViewById(R.id.pager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());

        adapter.addFragment(new FragOferta(), "Ofertas");
        adapter.addFragment(new FragProcura(), "Procuras");

        viewPager.setAdapter(adapter);

        TabLayout tabLayout = rootView.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        getActivity().setTitle("Página inicial");

        return rootView;
    }

    @Override
    public void onResume(){
        super.onResume();

        NavigationView menuLateral = getActivity().findViewById(R.id.menuLateral);
        menuLateral.getMenu().getItem(0).setChecked(true);
    }

}
