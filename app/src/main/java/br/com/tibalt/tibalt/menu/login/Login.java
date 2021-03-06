package br.com.tibalt.tibalt.menu.login;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;

import java.util.Arrays;
import java.util.Map;

import br.com.tibalt.tibalt.MainActivity;
import br.com.tibalt.tibalt.R;
import br.com.tibalt.tibalt.menu.MenuGeral;
import br.com.tibalt.tibalt.menu.login.registrar.Registrar_1_email_or_external;
import br.com.tibalt.tibalt.utilitarios.DbCollections;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Login extends AppCompatActivity {

    @BindView(R.id.bt_facebook)
    LoginButton bt_facebook;
    @BindView(R.id.matricula)
    EditText matricula;
    @BindView(R.id.senha)
    EditText senha;

    CallbackManager callbackManager;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        setFbAction();
    }

    private void setFbAction() {
        callbackManager = CallbackManager.Factory.create();
        bt_facebook.setReadPermissions("email", "public_profile");
        bt_facebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(Login.this, "Erro ao logar.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(Login.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(Login.this, "Conta não registrada.", Toast.LENGTH_SHORT).show();
                            findViewById(R.id.pBar).setVisibility(View.GONE);
                            facebook_logout();
                        }

                    }
                });
    }

    private void validate() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(DbCollections.COLLECTIONS_LOGIN).document("registration")
                .get().addOnSuccessListener((result) -> {
                    Map<String, Object> map = result.getData();
                    String email = (String) map.get(matricula.getText().toString());
                    if (email != null) {
                        loginEmail(email);
                    } else {
                        matricula.setError("Matrícula não registrada.");
                        findViewById(R.id.pBar).setVisibility(View.GONE);
                        senha.setText("");
                    }
        }).addOnFailureListener((listener) -> {
            Toast.makeText(this, "Erro de conexão", Toast.LENGTH_SHORT).show();
            findViewById(R.id.pBar).setVisibility(View.GONE);
            senha.setText("");
        });

    }

    private void loginEmail(String email) {
        String password = senha.getText().toString();

        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(Login.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(Login.this, "Senha e/ou Login incorretos.", Toast.LENGTH_SHORT).show();
                            findViewById(R.id.pBar).setVisibility(View.GONE);
                            facebook_logout();
                            senha.setText("");
                        }
                    }
                });
    }

    @OnClick(R.id.login)
    public void login() {
        boolean error = false;

        if (matricula.getText().toString().length() < 8 || !validateMatricula()) {
            matricula.setError("Matrícula inválida");
            error = true;
        }

        if (!validatePass()) {
            senha.setError("Senha inválida");
            error = true;
        }

        if (!error) {
            findViewById(R.id.pBar).setVisibility(View.VISIBLE);
            validate();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick(R.id.naoAluno)
    public void naoAluno() {

    }

    @OnClick(R.id.registrar)
    public void registrar() {
        Intent intent = new Intent(this, Registrar_1_email_or_external.class);
        startActivity(intent);
    }

    private boolean validateMatricula() {
        return true;
    }

    private boolean validatePass() {
        return !senha.getText().toString().isEmpty();
    }

    private void facebook_logout(){
        if (AccessToken.getCurrentAccessToken() != null) {
            new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, (graphResponse) -> {
                LoginManager.getInstance().logOut();
            }).executeAsync();
        }
    }
}
