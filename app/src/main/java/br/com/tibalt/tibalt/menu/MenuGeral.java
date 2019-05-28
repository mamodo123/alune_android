package br.com.tibalt.tibalt.menu;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import br.com.tibalt.tibalt.MainActivity;
import br.com.tibalt.tibalt.Models.User;
import br.com.tibalt.tibalt.R;
import br.com.tibalt.tibalt.menu.comoFunciona.FragComoFunciona;
import br.com.tibalt.tibalt.menu.ofertaProcura.FragAbas;
import br.com.tibalt.tibalt.utilitarios.SharedPref;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MenuGeral extends AppCompatActivity {

    @BindView(R.id.drawerLayout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.menuLateral)
    NavigationView menuLateral;

    private ActionBarDrawerToggle mToggle;

    private static final int TIME_DELAY = 2000;
    private static long back_pressed;

    FirebaseUser user;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicial);

        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        Log.d("userUid", user.getUid());

        getUserNav();
        setMenu();
        setFragOnMenu();
        setInicial();
    }

    private void getUserNav() {
        //cache
        String photoPref = SharedPref.readString(getApplicationContext(), "alune", "photo");
        View header = menuLateral.getHeaderView(0);
        if (photoPref != null) {
            Glide.with(this).load(photoPref).diskCacheStrategy(DiskCacheStrategy.RESOURCE).apply(RequestOptions.circleCropTransform()).into((ImageView) header.findViewById(R.id.iv_profile));
            header.findViewById(R.id.fl_picture).setBackgroundResource(R.drawable.photo_circle);
        }
        String namePref = SharedPref.readString(getApplicationContext(), "alune", "name");
        String emailPref = SharedPref.readString(getApplicationContext(), "alune", "email");
        ((TextView)header.findViewById(R.id.tv_name)).setText(namePref);
        ((TextView)header.findViewById(R.id.tv_email)).setText(emailPref);
        //

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(user.getUid());
        docRef.get().addOnSuccessListener((documentSnapshot) -> {
            User us = documentSnapshot.toObject(User.class);

            String photo = us.getPhoto();
            String name = us.getName();
            String email = us.getEmail();

            if (photo != null) {
                Glide.with(this).load(photo).diskCacheStrategy(DiskCacheStrategy.RESOURCE).apply(RequestOptions.circleCropTransform()).into((ImageView) header.findViewById(R.id.iv_profile));
                header.findViewById(R.id.fl_picture).setBackgroundResource(R.drawable.photo_circle);
                SharedPref.save(getApplicationContext(), "alune", "photo", photo);
            }
            SharedPref.save(getApplicationContext(), "alune", "name", name);
            SharedPref.save(getApplicationContext(), "alune", "email", email);
            ((TextView)header.findViewById(R.id.tv_name)).setText(name);
            ((TextView)header.findViewById(R.id.tv_email)).setText(email);

        });
    }

    private void setMenu() {
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setInicial() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.relativeContainer, new FragAbas()).commit();
        //menuLateral.getMenu().getItem(0).setChecked(true);
        menuLateral.setCheckedItem(R.id.nav_pag_inicial);
    }

    private void setFragOnMenu() {
        NavigationView nv = findViewById(R.id.menuLateral);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment fr = null;

                switch (item.getItemId()) {
                    case R.id.nav_pag_inicial:
                        fr = new FragAbas();
                        break;

                    case R.id.nav_como_funciona:
                        fr = new FragComoFunciona();
                        break;

                    case R.id.nav_dados_pessoais:
                        fr = null;
                        break;

                    case R.id.nav_negociacoes:
                        fr = null;
                        break;

                    case R.id.nav_sobre:
                        fr = null;
                        break;

                    case R.id.nav_sair:
                        findViewById(R.id.pbar).setVisibility(View.VISIBLE);
                        if (AccessToken.getCurrentAccessToken() != null) {
                            new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, (graphResponse) -> {
                                logout();
                            }).executeAsync();
                            break;
                        }
                        logout();
                        break;

                    default:
                        break;
                }
                if (fr != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.relativeContainer, fr).commit();
                    item.setChecked(true);
                }

                mDrawerLayout.closeDrawers();

                return true;
            }
        });
    }

    private void logout() {
        //Facebook logout
        LoginManager.getInstance().logOut();
        mAuth.signOut();

        SharedPref.deleteName(getApplicationContext(), "alune");

        Intent intent = new Intent(MenuGeral.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (!(getSupportFragmentManager().findFragmentById(R.id.relativeContainer) instanceof FragAbas)) {
                getSupportFragmentManager().beginTransaction().replace(R.id.relativeContainer, new FragAbas()).commit();
                NavigationView menuLateral = findViewById(R.id.menuLateral);
                //menuLateral.getMenu().getItem(0).setChecked(true);
                menuLateral.setCheckedItem(R.id.nav_pag_inicial);
            } else {
                if (back_pressed + TIME_DELAY > System.currentTimeMillis()) {
                    super.onBackPressed();
                } else {
                    Toast.makeText(getBaseContext(), "Pressione novamente para sair", Toast.LENGTH_SHORT).show();
                    back_pressed = System.currentTimeMillis();
                }
            }

        }
    }
}
