package br.com.tibalt.tibalt.menu.ofertaProcura.abas.publicar.SpinnerSelected;

import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;

public class PrecosSpinner implements AdapterView.OnItemSelectedListener  {

    private EditText etPrecoFixo;
    private EditText etPrecoFixo2;

    public PrecosSpinner(EditText et1, EditText et2){
        etPrecoFixo = et1;
        etPrecoFixo2 = et2;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch(position){
            case 0:
                etPrecoFixo.setVisibility(EditText.VISIBLE);
                etPrecoFixo.setHint("ex: 18,00");
                etPrecoFixo2.setVisibility(EditText.GONE);
                break;
            case 1:
                etPrecoFixo.setVisibility(EditText.VISIBLE);
                etPrecoFixo.setHint("De");
                etPrecoFixo2.setVisibility(EditText.VISIBLE);
                etPrecoFixo2.setHint("Até");
                break;
            case 2:
                etPrecoFixo.setVisibility(EditText.GONE);
                etPrecoFixo2.setVisibility(EditText.GONE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
