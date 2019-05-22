package br.com.tibalt.tibalt.menu.login.registrar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;

import java.util.Arrays;

import br.com.tibalt.tibalt.R;
import br.com.tibalt.tibalt.utilitarios.Validate;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Registrar_1_email_or_external extends AppCompatActivity {

    @BindView(R.id.bt_facebook)
    LoginButton bt_facebook;
    @BindView(R.id.ed_email)
    EditText ed_email;

    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_email_or_external);

        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Registrar");

        setFbAction();
    }

    @OnClick(R.id.bt_registrar)
    public void registrar(){
        String email = ed_email.getText().toString();
        if (Validate.isValidEmailAddress(email)){
            Bundle bundle = new Bundle();
            bundle.putBoolean("facebook", false);
            bundle.putString("email", email);
            Intent intent = new Intent(Registrar_1_email_or_external.this, Registrar_2_matricula_pass.class);
            intent.putExtras(bundle);
            startActivity(intent);
        } else {
            ed_email.setError("Email inválido");
        }
    }

    private void setFbAction() {
        callbackManager = CallbackManager.Factory.create();
        bt_facebook.setReadPermissions(Arrays.asList("email"));
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

                                Bundle bundle = new Bundle();
                                bundle.putBoolean("facebook", true);
                                bundle.putString("email", email);
                                bundle.putString("name", name);
                                bundle.putString("picture", picture);
                                Intent intent = new Intent(Registrar_1_email_or_external.this, Registrar_2_matricula_pass.class);
                                intent.putExtras(bundle);
                                startActivity(intent);

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
                Toast.makeText(Registrar_1_email_or_external.this, "Erro ao registrar com Facebook.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
