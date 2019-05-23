package br.com.tibalt.tibalt.menu.login.registrar;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

import org.json.JSONException;

import br.com.tibalt.tibalt.R;
import br.com.tibalt.tibalt.menu.login.Login;
import br.com.tibalt.tibalt.utilitarios.Validate;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Register_1 extends AppCompatActivity {

    @BindView(R.id.ed_email)
    EditText ed_email;
    @BindView(R.id.ed_senha)
    EditText ed_senha;
    @BindView(R.id.ed_senha2)
    EditText ed_senha2;
    @BindView(R.id.bt_facebook)
    LoginButton bt_facebook;

    Boolean facebook;
    Bundle bundle;

    CallbackManager callbackManager;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_1);

        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Registrar");

        mAuth = FirebaseAuth.getInstance();

        bundle = getIntent().getExtras();

        facebook = bundle.getBoolean("facebook", false);

        if (facebook) {
            bt_facebook.setVisibility(View.GONE);
            findViewById(R.id.ou).setVisibility(View.GONE);
            ed_email.setText(bundle.getString("email", ""));
        } else {
            setFbAction();
        }

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
                                                bundle = new Bundle();
                                                bundle.putBoolean("facebook", true);
                                                bundle.putString("email", email);
                                                bundle.putString("name", name);
                                                bundle.putString("picture", picture);
                                                ed_email.setText(email);
                                                bt_facebook.setVisibility(View.GONE);
                                                findViewById(R.id.ou).setVisibility(View.GONE);
                                                facebook = true;
                                            } else {
                                                if (task.getResult().getSignInMethods().contains("facebook.com")){
                                                    facebook_logout();
                                                    Toast.makeText(Register_1.this, "Este facebook ja está ligado à outra conta.", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    ed_email.setError("Este email ja está ligado à outra conta.");
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
                Toast.makeText(Register_1.this, "Erro ao logar.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick(R.id.bt_registrar)
    public void registrar() {
        boolean error = false;

        if (!Validate.isValidEmailAddress(ed_email.getText().toString())) {
            error = true;
            ed_email.setError("Email inválido!");
        }

        if (ed_senha.getText().toString().length() < 6) {
            error = true;
            ed_senha.setError("Senha muito pequena! Mínimo é 6 letras.");
        }

        if (!ed_senha2.getText().toString().equals(ed_senha.getText().toString())) {
            error = true;
            ed_senha2.setError("As senhas não são iguais!");
        }

        if (!error) {
            validate();
        }

    }

    private void validate() {
        String email = ed_email.getText().toString();
        mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().getSignInMethods().isEmpty()) {
                         Intent intent = new Intent(Register_1.this, Register_2.class);
                         intent.putExtras(bundle);
                         startActivity(intent);
                    } else {
                       ed_email.setError("Este email já esta associado à uma conta!");
                    }
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                super.onOptionsItemSelected(item);
                return true;
        }
    }

    @Override
    public void onBackPressed() {
        facebook_logout();
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void facebook_logout() {
        if (AccessToken.getCurrentAccessToken() != null) {
            new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, (graphResponse) -> {
                LoginManager.getInstance().logOut();
            }).executeAsync();
        }
    }
}
