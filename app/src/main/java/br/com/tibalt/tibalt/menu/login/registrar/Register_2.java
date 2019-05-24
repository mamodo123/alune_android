package br.com.tibalt.tibalt.menu.login.registrar;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import br.com.tibalt.tibalt.R;
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

    boolean facebook;

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

    private void register(){

    }

    @OnClick(R.id.tv_terms)
    public void terms() {
        final Dialog termos = new Dialog(this);
        termos.setContentView(R.layout.terms);
        termos.setTitle(getString(R.string.terms_of_use));
        termos.findViewById(R.id.tv_ok).setOnClickListener((v) -> {
            termos.dismiss();
        });
        termos.show();

    }
}
