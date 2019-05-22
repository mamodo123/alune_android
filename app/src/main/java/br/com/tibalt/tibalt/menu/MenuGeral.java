package br.com.tibalt.tibalt.menu;

import android.app.ProgressDialog;
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
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import br.com.tibalt.tibalt.MainActivity;
import br.com.tibalt.tibalt.R;
import br.com.tibalt.tibalt.menu.comoFunciona.FragComoFunciona;
import br.com.tibalt.tibalt.menu.login.Login;
import br.com.tibalt.tibalt.menu.ofertaProcura.FragAbas;

public class MenuGeral extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    private static final int TIME_DELAY = 2000;
    private static long back_pressed;

    FirebaseUser user;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicial);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        Log.d("userUid", user.getUid());

        setMenu();
        setFragOnMenu();
        setInicial();
    }

    private void setMenu() {
        mDrawerLayout = findViewById(R.id.drawerLayout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setInicial() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.relativeContainer, new FragAbas()).commit();
        NavigationView menuLateral = findViewById(R.id.menuLateral);
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

                    case R.id.nav_perfil:
                        fr = null;
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
