package br.com.tibalt.tibalt.menu.login.registrar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import br.com.sapereaude.maskedEditText.MaskedEditText;
import br.com.tibalt.tibalt.MainActivity;
import br.com.tibalt.tibalt.R;
import br.com.tibalt.tibalt.utilitarios.FirebaseUpdate;
import br.com.tibalt.tibalt.utilitarios.Validate;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Registrar_3_dados_pessoais extends AppCompatActivity {

    public static final int PICK_IMAGE = 1;

    @BindView(R.id.riv_photo)
    ImageView riv_photo;
    @BindView(R.id.et_name)
    EditText et_name;
    @BindView(R.id.et_email)
    EditText et_email;
    @BindView(R.id.et_cpf)
    MaskedEditText et_cpf;
    @BindView(R.id.et_cel)
    MaskedEditText et_cel;
    @BindView(R.id.et_estrangeiro)
    EditText et_estrangeiro;
    @BindView(R.id.riv_mini_photo)
    ImageView riv_mini_photo;
    @BindView(R.id.cb_terms)
    CheckBox cb_terms;

    boolean brasileiro = true;
    Uri photo = null;
    boolean facebook;
    boolean face_foto = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_dados_pessoais);

        ButterKnife.bind(this);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Dados pessoais");

        Bundle bundle = getIntent().getExtras();

        facebook = bundle.getBoolean("facebook");

        if (facebook) {
            String id = bundle.getString("id");
            String email = bundle.getString("email");
            String name = bundle.getString("name");
            String picture = bundle.getString("picture");
            photo = Uri.parse(picture);
            face_foto = true;

            Glide.with(Registrar_3_dados_pessoais.this).load(picture).diskCacheStrategy(DiskCacheStrategy.RESOURCE).apply(RequestOptions.circleCropTransform()).into(riv_photo);
            et_name.setText(name);
            et_email.setText(email);
            if (!et_email.getText().toString().isEmpty() && !Validate.isValidEmailAddress(email)) {
                et_email.setError("Email inválido");
            }
        } else {
            String email = bundle.getString("email");
            et_email.setText(email);
        }
    }

    @OnClick(R.id.fl_photo)
    public void getPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/jpeg");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                CropImage.activity(data.getData())
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .setRequestedSize(200, 200, CropImageView.RequestSizeOptions.RESIZE_EXACT)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .start(this);
            }
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (result != null) {
                Uri uri = result.getUri();
                Glide.with(Registrar_3_dados_pessoais.this).load(uri).diskCacheStrategy(DiskCacheStrategy.RESOURCE).apply(RequestOptions.circleCropTransform()).into(riv_photo);
                photo = uri;
                face_foto = false;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                findViewById(R.id.pBar).setVisibility(View.VISIBLE);
                new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, (graphResponse) -> {
                    LoginManager.getInstance().logOut();
                    super.onBackPressed();
                }).executeAsync();
                return true;
            case R.id.action_save:
                validateAll();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void validateAll() {
        boolean error = false;

        if (et_name.getText().toString().isEmpty()) {
            error = true;
            et_name.setError("Nome inválido.");
        }

        String email = et_email.getText().toString();
        if (!Validate.isValidEmailAddress(email)) {
            error = true;
            et_email.setError("Email inválido.");
        }

        if (!Validate.isCPF(et_cpf.getRawText())) {
            error = true;
            et_cpf.setError("CPF inválido.");
        }
        if (brasileiro) {
            if (et_cel.getRawText().isEmpty()) {
                error = true;
                et_cel.setError("Telefone inválido.");
            }
        } else {
            if (et_estrangeiro.getText().toString().isEmpty()) {
                error = true;
                et_estrangeiro.setError("Telefone inválido.");
            }
        }

        if (!cb_terms.isChecked()) {
            error = true;
            cb_terms.setError("Aceite os termos antes de prosseguir.");
        }

        if (!error) {
            findViewById(R.id.pBar).setVisibility(View.VISIBLE);
            createUser(email);
        }

    }

    private void createUser(String email) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                if (task.getResult().getSignInMethods().isEmpty()) {
                    createWithEmail(email);
                } else {
                    et_email.setError("Email já cadastrado.");
                    et_email.requestFocus();
                    findViewById(R.id.pBar).setVisibility(View.GONE);
                }
            }
        });
    }

    private void createWithEmail(String email) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Bundle bundle = getIntent().getExtras();
        String password = bundle.getString("senha");

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String name = et_name.getText().toString();
                            String email = et_email.getText().toString();
                            String cpf = et_cpf.getRawText();
                            String cel;
                            if (brasileiro) {
                                cel = et_cel.getText().toString();
                            } else {
                                cel = et_estrangeiro.getText().toString();
                            }
                            String matricula = bundle.getString("matricula");
                            String pdf = bundle.getString("pdf");

                            if (facebook) {
                                AuthCredential credential = FacebookAuthProvider.getCredential(AccessToken.getCurrentAccessToken().getToken());
                                mAuth.getCurrentUser().linkWithCredential(credential)
                                        .addOnCompleteListener(Registrar_3_dados_pessoais.this, new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {
                                                    runOnUiThread(() -> updateuser(name, email, cpf, cel, matricula, pdf, db, mAuth));
                                                    Intent intent = new Intent(Registrar_3_dados_pessoais.this, MainActivity.class);
                                                    startActivity(intent);
                                                } else {
                                                    new AlertDialog.Builder(Registrar_3_dados_pessoais.this)
                                                            .setTitle("Atenção!")
                                                            .setMessage("Esta conta do Facebook está vinculada à outra conta. Sua conta foi vinculada à sua matrícula e email.\nCaso deseje vincular ao Facebook, você poderá em seu perfil.")
                                                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    dialog.dismiss();
                                                                    runOnUiThread(() -> updateuser(name, email, cpf, cel, matricula, pdf, db, mAuth));
                                                                    Intent intent = new Intent(Registrar_3_dados_pessoais.this, MainActivity.class);
                                                                    startActivity(intent);
                                                                }
                                                            })
                                                            .show();
                                                }
                                            }
                                        });
                                return;
                            }

                            runOnUiThread(() -> updateuser(name, email, cpf, cel, matricula, pdf, db, mAuth));
                            Intent intent = new Intent(Registrar_3_dados_pessoais.this, MainActivity.class);
                            startActivity(intent);
                        }
                    }
                });
    }

    private void updateuser(String name, String email, String cpf, String cel, String matricula, String pdf, FirebaseFirestore db, FirebaseAuth mAuth) {
        FirebaseUser user = mAuth.getCurrentUser();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storage.getReference().child("atestados/" + user.getUid()).putFile(Uri.parse(pdf));

        HashMap<Object, String> userInfo = new HashMap<>();
        userInfo.put("name", name);
        userInfo.put("email", email);
        userInfo.put("cpf", cpf);
        userInfo.put("cel", cel);
        userInfo.put("matricula", matricula);

        if (photo != null) {
            if (facebook && face_foto) {
                userInfo.put("photo", photo.toString());
                FirebaseUpdate.updateUser(userInfo);
            } else {
                storage.getReference().child("user_photo/" + user.getUid()).putFile(photo).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storage.getReference().child("user_photo/" + user.getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                userInfo.put("photo", uri.toString());
                                FirebaseUpdate.updateUser(userInfo);
                            }
                        });
                    }
                });
            }
        } else {
            userInfo.put("photo", null);
            FirebaseUpdate.updateUser(userInfo);
        }
    }

    @OnClick(R.id.tv_terms)
    public void terms() {
        final Dialog termos = new Dialog(this);
        termos.setContentView(R.layout.terms);
        termos.setTitle("Termos de uso");
        termos.findViewById(R.id.tv_ok).setOnClickListener((v) -> {
            termos.dismiss();
        });
        termos.show();

    }

    @OnClick(R.id.tv_estrangeiro)
    public void estrangeiro() {
        TextView texto = findViewById(R.id.tv_estrangeiro);
        if (brasileiro) {
            et_cel.setVisibility(View.GONE);
            et_estrangeiro.setVisibility(View.VISIBLE);
            texto.setText("Meu número é brasileiro");
        } else {
            et_cel.setVisibility(View.VISIBLE);
            et_estrangeiro.setVisibility(View.GONE);
            texto.setText("Meu número é estrangeiro");
        }
        brasileiro = !brasileiro;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_menu, menu);
        return true;
    }
}
