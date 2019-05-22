package br.com.tibalt.tibalt.menu.login.registrar;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.util.Map;

import br.com.tibalt.tibalt.R;
import br.com.tibalt.tibalt.Services.FacebookExitService;
import br.com.tibalt.tibalt.utilitarios.DbCollections;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Registrar_2_matricula_pass extends AppCompatActivity {

    @BindView(R.id.et_matricula)
    EditText et_matricula;
    @BindView(R.id.et_senha)
    EditText et_senha;
    @BindView(R.id.et_senha2)
    EditText et_senha2;

    private final int PICK_PDF_CODE = 1234;
    String pdf = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_matricula_pass);

        Intent intent = new Intent(this, FacebookExitService.class);
        startService(intent);

        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Registrar");
    }

    private void validateAll() {
        boolean error = false;
        if (et_matricula.getText().toString().length() < 8) {
            error = true;
            et_matricula.setError("Matrícula inválida");
        }
        if (et_senha.getText().toString().length() < 6) {
            error = true;
            et_senha.setError("Senha muito pequena. Tamanho mínimo: 6.");
        } else if (!et_senha.getText().toString().equals(et_senha2.getText().toString())) {
            error = true;
            et_senha2.setError("As senhas não são idênticas!");
        }
        if (pdf == null) {
            error = true;
            Toast.makeText(this, "Você não selecionou um PDF.", Toast.LENGTH_SHORT).show();
        }

        if (!error) {
            findViewById(R.id.pBar).setVisibility(View.VISIBLE);
            next();
        }
    }

    private void next() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(DbCollections.COLLECTIONS_LOGIN).document("registration")
                .get().addOnSuccessListener((result) -> {
            Map<String, Object> map = result.getData();

            if (!map.containsKey(et_matricula.getText().toString())) {

                findViewById(R.id.pBar).setVisibility(View.GONE);

                Bundle bundle = getIntent().getExtras();
                bundle.putString("matricula", et_matricula.getText().toString());
                bundle.putString("senha", et_senha.getText().toString());
                bundle.putString("pdf", pdf);

                Intent intent = new Intent(Registrar_2_matricula_pass.this, Registrar_3_dados_pessoais.class);
                intent.putExtras(bundle);
                startActivity(intent);
            } else {
                findViewById(R.id.pBar).setVisibility(View.GONE);
                et_matricula.setError("Matrícula já cadastrada.");
                et_matricula.requestFocus();
            }
        });
    }

    @OnClick(R.id.bt_pdf)
    public void getPdf() {
        Intent intentPDF = new Intent(Intent.ACTION_GET_CONTENT);
        intentPDF.setType("application/pdf");
        intentPDF.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intentPDF, "Select Picture"), PICK_PDF_CODE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_PDF_CODE) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                if (uri != null) {
                    String uriString = uri.toString();
                    pdf = uriString;
                    File myFile = new File(uriString);
                    String path = myFile.getAbsolutePath();
                    String displayName = null;
                    TextView tv_pdf = findViewById(R.id.tv_pdf);
                    if (uriString.startsWith("content://")) {
                        Cursor cursor = null;
                        try {
                            cursor = this.getContentResolver().query(uri, null, null, null, null);
                            if (cursor != null && cursor.moveToFirst()) {
                                displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                                if (displayName.length() > 35) {
                                    displayName = displayName.substring(0, 35) + ("...");
                                }
                                tv_pdf.setText(displayName);
                            }
                        } finally {
                            cursor.close();
                        }
                    } else if (uriString.startsWith("file://")) {
                        displayName = myFile.getName();
                        if (displayName.length() > 35) {
                            displayName = displayName.substring(0, 35) + ("...");
                        }
                        tv_pdf.setText(displayName);
                    }
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        findViewById(R.id.pBar).setVisibility(View.VISIBLE);
        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, (graphResponse) -> {
            LoginManager.getInstance().logOut();
            super.onBackPressed();
        }).executeAsync();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_save:
                validateAll();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_menu, menu);
        return true;
    }
}
