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
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.AccessToken;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import br.com.tibalt.tibalt.MainActivity;
import br.com.tibalt.tibalt.R;
import br.com.tibalt.tibalt.utilitarios.FirebaseUpdate;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Register_2 extends AppCompatActivity {

    @BindView(R.id.iv_profile)
    ImageView iv_profile;
    @BindView(R.id.et_name)
    EditText et_name;
    @BindView(R.id.fl_picture)
    FrameLayout fl_picture;
    @BindView(R.id.cb_terms)
    CheckBox cb_terms;
    @BindView(R.id.tv_terms)
    TextView tv_terms;
    @BindView(R.id.x)
    ImageView x;
    @BindView(R.id.pbar)
    ProgressBar pbar;

    boolean facebook;
    Uri photo = null;
    boolean face_foto = false;

    public static final int PICK_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_2);

        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Registrar");

        Bundle bundle = getIntent().getExtras();
        facebook = bundle.getBoolean("facebook", false);

        if (facebook) {
            String picture = bundle.getString("picture");
            Glide.with(this).load(picture).diskCacheStrategy(DiskCacheStrategy.RESOURCE).apply(RequestOptions.circleCropTransform()).into(iv_profile);
            String name = bundle.getString("name");
            et_name.setText(name);
            fl_picture.setBackgroundResource(R.drawable.photo_circle);
            face_foto = true;
            photo = Uri.parse(picture);
            x.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.registrar)
    public void validar(){
        boolean error = false;

        if (et_name.getText().toString().isEmpty()) {
            error = true;
            et_name.setError(getString(R.string.invalid_name));
        }

        if (!cb_terms.isChecked()) {
            error = true;
            tv_terms.setError(getString(R.string.acept_terms));
        }

        if (!error) {
            register();
        }
    }

    private void register() {
        pbar.setVisibility(View.VISIBLE);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Bundle bundle = getIntent().getExtras();
        String email = bundle.getString("email");
        String password = bundle.getString("senha");

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        pbar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            String name = et_name.getText().toString();
                            if (facebook) {
                                AuthCredential credential = FacebookAuthProvider.getCredential(AccessToken.getCurrentAccessToken().getToken());
                                mAuth.getCurrentUser().linkWithCredential(credential)
                                        .addOnCompleteListener(Register_2.this, new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {
                                                    runOnUiThread(() -> updateuser(name, email, db, mAuth));
                                                    Intent intent = new Intent(Register_2.this, MainActivity.class);
                                                    startActivity(intent);
                                                } else {
                                                    new AlertDialog.Builder(Register_2.this)
                                                            .setTitle("Atenção!")
                                                            .setMessage("Esta conta do Facebook está vinculada à outra conta. Sua conta foi vinculada ao seu email.\nCaso deseje vincular ao Facebook, você poderá em seu perfil.")
                                                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    dialog.dismiss();
                                                                    runOnUiThread(() -> updateuser(name, email, db, mAuth));
                                                                    Intent intent = new Intent(Register_2.this, MainActivity.class);
                                                                    startActivity(intent);
                                                                }
                                                            })
                                                            .show();
                                                }
                                            }

                                        });
                            } else {
                                runOnUiThread(() -> updateuser(name, email, db, mAuth));
                                Intent intent = new Intent(Register_2.this, MainActivity.class);
                                startActivity(intent);
                            }
                        }
                    }
                });
    }

    private void updateuser(String name, String email, FirebaseFirestore db, FirebaseAuth mAuth) {
        FirebaseUser user = mAuth.getCurrentUser();
        FirebaseStorage storage = FirebaseStorage.getInstance();

        HashMap<Object, String> userInfo = new HashMap<>();
        userInfo.put("name", name);
        userInfo.put("email", email);

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
        termos.setTitle(getString(R.string.terms_of_use));
        termos.findViewById(R.id.tv_ok).setOnClickListener((v) -> termos.dismiss());
        termos.show();

    }

    @OnClick(R.id.iv_camera)
    public void getPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/jpeg");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    @OnClick(R.id.x)
    public void x() {
        photo = null;
        face_foto = false;
        iv_profile.setImageResource(R.drawable.ic_default_user);
        fl_picture.setBackgroundResource(android.R.color.transparent);
        x.setVisibility(View.INVISIBLE);
    }

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
                Glide.with(Register_2.this).load(uri).diskCacheStrategy(DiskCacheStrategy.RESOURCE).apply(RequestOptions.circleCropTransform()).into(iv_profile);
                photo = uri;
                face_foto = false;
                fl_picture.setBackgroundResource(R.drawable.photo_circle);
                x.setVisibility(View.VISIBLE);
            }
        }
    }
}
