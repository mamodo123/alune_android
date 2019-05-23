package br.com.tibalt.tibalt.menu.login.registrar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import br.com.tibalt.tibalt.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class Register_2 extends AppCompatActivity {

    @BindView(R.id.iv_profile)
    ImageView iv_profile;
    @BindView(R.id.et_name)
    EditText et_registrar;
    @BindView(R.id.fl_picture)
    FrameLayout fl_picture;

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
            et_registrar.setText(name);
            fl_picture.setBackgroundResource(R.drawable.photo_circle);
        }
    }
}
