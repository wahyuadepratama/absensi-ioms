package com.github.wahyuadepratama.absensi;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.github.wahyuadepratama.absensi.apihelper.BaseApiService;
import com.github.wahyuadepratama.absensi.apihelper.UtilsApi;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.github.wahyuadepratama.absensi.MainActivity.ALAMAT;
import static com.github.wahyuadepratama.absensi.MainActivity.EMAIL;
import static com.github.wahyuadepratama.absensi.MainActivity.NAMA;
import static com.github.wahyuadepratama.absensi.MainActivity.NO_HANDPHONE;
import static com.github.wahyuadepratama.absensi.MainActivity.TAG_ID;
import static com.github.wahyuadepratama.absensi.MainActivity.TAG_USERNAME;

/**
 * Created by wahyu on 04/10/18.
 */

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    public ProgressDialog loading;
    public TextView homeNama, homeNim, homeEmail, homeNoHandphone, homeAlamat;
    public Button homeLogout;
    public Button homeAbsensi;
    public String id, nim, nama, no_anggota, email, no_handphone, alamat;
    public SharedPreferences sharedpreferences;
    private IntentIntegrator intentIntegrator;
    private DrawerLayout mDrawerLayout;

    Context mContext;
    BaseApiService mApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        navigationView();
        drawerLayout();

        //ambil data dari session
        sharedpreferences = getSharedPreferences(MainActivity.MY_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        id          = getIntent().getStringExtra(TAG_ID);
        nim         = getIntent().getStringExtra(TAG_USERNAME);
        email       = getIntent().getStringExtra(EMAIL);
        nama        = getIntent().getStringExtra(NAMA);
        no_handphone= getIntent().getStringExtra(NO_HANDPHONE);
        alamat      = getIntent().getStringExtra(ALAMAT);

        homeNama    = findViewById(R.id.homeNama);
        homeNim     = findViewById(R.id.homeNim);
        homeEmail   = findViewById(R.id.homeEmail);
        homeNoHandphone = findViewById(R.id.homeNoHandphone);
        homeAlamat  = findViewById(R.id.homeAlamat);

        homeNama.setText(nama);
        homeNim.setText(nim);
        homeAlamat.setText(alamat);
        homeNoHandphone.setText(no_handphone);
        homeEmail.setText(email);

        mContext = this;
        mApiService = UtilsApi.getAPIService(); // meng-init yang ada di package apihelper
    }

    private void drawerLayout() {
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerLayout.addDrawerListener(
            new DrawerLayout.DrawerListener() {
                @Override
                public void onDrawerSlide(View drawerView, float slideOffset) {
                    // Respond when the drawer's position changes
                }

                @Override
                public void onDrawerOpened(View drawerView) {
                    setNavigationViewListener();
                }

                @Override
                public void onDrawerClosed(View drawerView) {
                    // Respond when the drawer is closed
                }

                @Override
                public void onDrawerStateChanged(int newState) {
                    // Respond when the drawer motion state changes
                }
            }
        );
    }

    private void navigationView(){
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
            new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem menuItem) {
                    // set item as selected to persist highlight
                    menuItem.setChecked(true);
                    // close drawer when item is tapped
                    mDrawerLayout.closeDrawers();
                    // Add code here to update the UI based on the item selected
                    // For example, swap UI fragments here
                    return true;
                }
            });
    }

    private void setNavigationViewListener() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {

            case R.id.nav_logout: {
                loading = ProgressDialog.show(HomeActivity.this, null, "Harap Tunggu...", true, false);
                logout();
                break;
            }
            case R.id.nav_absensi: {
                scanMe();
                break;
            }
        }
        //close navigation drawer
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void scanMe() {
        // inisialisasi IntentIntegrator(scanQR)
        intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setPrompt("Silahkan Scan Barcode Absensi");
        intentIntegrator.setBeepEnabled(true);
        intentIntegrator.setBarcodeImageEnabled(true);
        intentIntegrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null){
            if (result.getContents() == null){
                Toast.makeText(this, "Hasil tidak ditemukan", Toast.LENGTH_SHORT).show();
            }else{
                // jika qrcode berisi data
                try{
                    // converting the data json
                    JSONObject object = new JSONObject(result.getContents());

                    String code = object.getString("Y29kZQ==");
                    code = decodeBase64(code);

                    String today = object.getString("dG9kYXk=");
                    today = decodeBase64(today);

                    loading = ProgressDialog.show(mContext, null, "Harap Tunggu...", true, false);
                    checkPiket(getIntent().getStringExtra(TAG_ID),today);

//                    Toast.makeText(this, "hm..! " + code + today, Toast.LENGTH_LONG).show();

                }catch (Exception e){
                    e.printStackTrace();
                    // jika format encoded tidak sesuai maka hasil
                    // ditampilkan ke toast
                    Toast.makeText(mContext, "Format error", Toast.LENGTH_SHORT).show();
                    Toast.makeText(this, result.getContents(), Toast.LENGTH_SHORT).show();
                }
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private String decodeBase64(String coded){
        byte[] valueDecoded = new byte[0];
        try {
            valueDecoded = Base64.decode(coded.getBytes("UTF-8"), Base64.DEFAULT);
        } catch (UnsupportedEncodingException e) {
        }
        return new String(valueDecoded);
    }

    private void checkPiket(final String id_user, final String today){
        mApiService.checkPiket(id_user, today).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    loading.dismiss();
                    try {
                        JSONObject jsonRESULTS = new JSONObject(response.body().string());

                            String error_msg = jsonRESULTS.getString("error_msg");

    //                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
    //                            finish();

                            Toast.makeText(mContext, error_msg, Toast.LENGTH_LONG).show();
//                            startActivity(intent);

                    } catch (JSONException e) {
                        Toast.makeText(mContext, "Gagal Memparsing Data!", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    } catch (IOException e) {
                        Toast.makeText(mContext, "Masalah Koneksi Jaringan!", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(mContext, "Response Fail!", Toast.LENGTH_SHORT).show();
                    loading.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(mContext, "Masalah Koneksi Jaringan!", Toast.LENGTH_SHORT).show();
                Log.e("debug", "onFailure: ERROR > " + t.toString());
                loading.dismiss();
            }
        });
    }

    private void logout() {
        // update login session ke FALSE dan mengosongkan nilai id dan username
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(MainActivity.SESSION_STATUS, false);
        editor.putString(TAG_ID, null);
        editor.putString(TAG_USERNAME, null);
        editor.commit();

        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
        finish();

        Toast.makeText(HomeActivity.this, "Berhasil Logout!", Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }

}
