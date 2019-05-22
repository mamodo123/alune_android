package br.com.tibalt.tibalt.menu.ofertaProcura.abas.publicar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import br.com.tibalt.tibalt.R;
import br.com.tibalt.tibalt.menu.ofertaProcura.abas.publicar.SpinnerSelected.PrecosSpinner;
import br.com.tibalt.tibalt.menu.ofertaProcura.abas.serviços.Ofertas;

public class Ofertar extends AppCompatActivity {

    public static final int PICK_IMAGE = 1234;
    ImageView pressed;
    Bitmap[] imagens = new Bitmap[3];
    ArrayList<String> categories = new ArrayList<>();

    String before1 = "", before2 = "";

    EditText etPreco1, etPreco2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ofertar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setPrecos();

        etPreco1 = findViewById(R.id.etPrecoFixo);
        etPreco2 = findViewById(R.id.etPrecoFixo2);

        etPreco1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if(!editable.toString().equals(before1)){
                    String output = editable.toString().replace(",", "");
                    if(output.length() > 2){
                        output = output.substring(0, output.length()- 2) + "," + output.substring(output.length()-2);
                    }
                    before1 = output;
                    etPreco1.setText(output);
                    etPreco1.setSelection(output.length());
                }
            }
        });

        etPreco2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if(!editable.toString().equals(before2)){
                    String output = editable.toString().replace(",", "");
                    if(output.length() > 2){
                        output = output.substring(0, output.length()- 2) + "," + output.substring(output.length()-2);
                    }
                    before2 = output;
                    etPreco2.setText(output);
                    etPreco2.setSelection(output.length());
                }
            }
        });
    }


    public void getFoto(View v) {
        pressed = findViewById(v.getId());
        Intent i = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(i, "Selecione uma imagem"), PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_CANCELED) {
            if (requestCode == PICK_IMAGE) {
                Uri selectedImage = data.getData();
                Uri outputUri = Uri.fromFile(new File(getCacheDir(), "cropped"));
                Crop.of(selectedImage, outputUri).asSquare().start(this);
            } else if (requestCode == Crop.REQUEST_CROP) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Crop.getOutput(data));
                    pressed.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 100, 100, false));

                    switch (pressed.getId()) {
                        case R.id.ivImagem1:
                            imagens[0] = Bitmap.createScaledBitmap(bitmap, 300, 300, false);
                            break;
                        case R.id.ivImagem2:
                            imagens[1] = Bitmap.createScaledBitmap(bitmap, 300, 300, false);
                            break;
                        case R.id.ivImagem3:
                            imagens[2] = Bitmap.createScaledBitmap(bitmap, 300, 300, false);
                            break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setPrecos() {
        categories.add("Preço fixo");
        categories.add("Faixa de preço");
        categories.add("Preço a combinar");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = findViewById(R.id.spPreço);
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(new PrecosSpinner((EditText) findViewById(R.id.etPrecoFixo), (EditText) findViewById(R.id.etPrecoFixo2)));
    }

    public void enviar(View V) {
        Ofertas oferta = generate();
    }

    private Ofertas generate() {
        boolean ok = true;

        EditText etTitulo = findViewById(R.id.etOfertarNome);
        EditText etDescricao = findViewById(R.id.etOfertarDescrição);
        Spinner spTipoPreco = findViewById(R.id.spPreço);

        String titulo = etTitulo.getText().toString();
        if (titulo.isEmpty()) {
            etTitulo.setError("O título não pode ser vazio");
            ok = false;
        }
        String descricao = etDescricao.getText().toString();
        if (descricao.isEmpty()) {
            etDescricao.setError("A descrição não pode ser vazia");
            ok = false;
        }
        int tipoPreco = spTipoPreco.getSelectedItemPosition();
        float preco1 = 0;
        float preco2 = 0;
        switch (tipoPreco) {
            case 0:
                try {
                    preco1 = Float.parseFloat(etPreco1.getText().toString().replace(",", "."));
                    preco2 = preco1;
                } catch (NumberFormatException e) {
                    etPreco1.setError("Coloque um valor válido.");
                    ok = false;
                }
                break;
            case 1:
                try {
                    preco1 = Float.parseFloat(etPreco1.getText().toString().replace(",", "."));
                } catch (NumberFormatException e) {
                    etPreco1.setError("Coloque um valor válido.");
                    ok = false;
                }
                try {
                    preco2 = Float.parseFloat(etPreco2.getText().toString().replace(",", "."));
                } catch (NumberFormatException e) {
                    etPreco2.setError("Coloque um valor válido.");
                    ok = false;
                }

                break;
            case 3:
                preco1 = 0;
                preco2 = 0;
                break;
        }
        if (ok) {
            return new
                    Ofertas(titulo, descricao, tipoPreco, preco1, preco2, imagens);
        } else {
            return null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
