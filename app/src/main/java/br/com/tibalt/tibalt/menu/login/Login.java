package br.com.tibalt.tibalt.menu.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;

import java.util.Map;

import br.com.tibalt.tibalt.MainActivity;
import br.com.tibalt.tibalt.R;
import br.com.tibalt.tibalt.menu.login.registrar.Register_1;
import br.com.tibalt.tibalt.utilitarios.DbCollections;
import br.com.tibalt.tibalt.utilitarios.Validate;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Login extends AppCompatActivity {

    @BindView(R.id.bt_facebook)
    LoginButton bt_facebook;
    @BindView(R.id.login)
    EditText login;
    @BindView(R.id.senha)
    EditText senha;

    CallbackManager callbackManager;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        facebook_logout();

        mAuth = FirebaseAuth.getInstance();

        setFbAction();
    }

    private void setFbAction() {
        callbackManager = CallbackManager.Factory.create();
        bt_facebook.setReadPermissions("email", "public_profile");
        bt_facebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(), (object, response) -> {
                            try {
                                String id = object.getString("id");
                                String email = object.getString("email");
                                String name = object.getString("name");
                                String picture = "https://graph.facebook.com/" + id + "/picture?type=large";

                                mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                                        if (task.isSuccessful()) {
                                            if (task.getResult().getSignInMethods().isEmpty()) {
                                                findViewById(R.id.pBar).setVisibility(View.GONE);
                                                Bundle bundle = new Bundle();
                                                bundle.putBoolean("facebook", true);
                                                bundle.putString("email", email);
                                                bundle.putString("name", name);
                                                bundle.putString("picture", picture);
                                                Intent intent = new Intent(Login.this, Register_1.class);
                                                intent.putExtras(bundle);
                                                startActivity(intent);
                                            } else {
                                                if (task.getResult().getSignInMethods().contains("facebook.com")){
                                                    handleFacebookAccessToken(loginResult.getAccessToken());
                                                } else {
                                                    facebook_logout();
                                                    Toast.makeText(Login.this, "Este Facebook não esta conectado à conta com este email.", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }
                                    }
                                });

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, email, name");
                request.setParameters(parameters);
                request.executeAsync();
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
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, (task) -> {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(Login.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(Login.this, "Conta não registrada.", Toast.LENGTH_SHORT).show();
                            findViewById(R.id.pBar).setVisibility(View.GONE);
                            facebook_logout();
                        }

                });
    }

    private void validate() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(DbCollections.COLLECTIONS_LOGIN).document("registration")
                .get().addOnSuccessListener((result) -> {
                    Map<String, Object> map = result.getData();
            String email = (String) map.get(login.getText().toString());
                    if (email != null) {
                        loginEmail(email);
                    } else {
                        login.setError("Matrícula não registrada.");
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

    @OnClick(R.id.bt_login)
    public void login() {
        boolean error = true;

        String login_text = login.getText().toString();
        int system_login = 0;

        if (Validate.isValidEmailAddress(login_text)) {
            error = false;
            system_login = 1;
        } else if (Validate.isMatricula(login_text)) {
            error = false;
            system_login = 2;
        } else {
            login.setError("Login inválido.");
        }

        if (!validatePass()) {
            senha.setError("Senha inválida.");
            error = true;
        }

        if (!error) {
            findViewById(R.id.pBar).setVisibility(View.VISIBLE);
            switch (system_login) {
                case 1:
                    loginEmail(login_text);
                    break;
                case 2:
                    validate();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick(R.id.registrar)
    public void registrar() {
        Intent intent = new Intent(this, Register_1.class);
        startActivity(intent);
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
