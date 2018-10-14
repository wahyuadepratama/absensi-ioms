package com.github.wahyuadepratama.absensi;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.wahyuadepratama.absensi.apihelper.BaseApiService;
import com.github.wahyuadepratama.absensi.apihelper.UtilsApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    public static final String MY_SHARED_PREFERENCES = "my_shared_preferences";
    public static final String SESSION_STATUS = "session_status";
    public final static String TAG_USERNAME = "username";
    public final static String TAG_ID = "id";

    public final static String NAMA="nama";
    public final static String EMAIL="email";
    public final static String NO_ANGGOTA="no_anggota";
    public final static String TEMPAT_LAHIR="tempat_lahir";
    public final static String TANGGAL_LAHIR="tanggal_lahir";
    public final static String ALAMAT="alamat";
    public final static String AVATAR="avatar";
    public final static String NO_HANDPHONE="no_handphone";
    public final static String KUTIPAN="kutipan";
    public final static String UPDATED_AT="updated_at";
    public final static String ID_ROLE="id_role";

    EditText etEmail;
    EditText etPassword;
    Button btnLogin;
    Button btnRegister;
    ProgressDialog loading;

    Context mContext;
    BaseApiService mApiService;
    SharedPreferences sharedpreferences;

    Boolean session = false;
    String id, nim, nama, email, no_anggota, tempat_lahir, tanggal_lahir, alamat, avatar, no_handphone, kutipan, updated_at, id_role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Cek session login jika TRUE maka langsung buka HomeActivity
        sharedpreferences = getSharedPreferences(MY_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        session     = sharedpreferences.getBoolean(SESSION_STATUS, false);
        id          = sharedpreferences.getString(TAG_ID, null);
        nim         = sharedpreferences.getString(TAG_USERNAME, null);
        nama        = sharedpreferences.getString(NAMA, null);
        email       = sharedpreferences.getString(EMAIL, null);
        no_anggota  = sharedpreferences.getString(NO_ANGGOTA, null);
        tempat_lahir = sharedpreferences.getString(TEMPAT_LAHIR, null);
        tanggal_lahir = sharedpreferences.getString(TANGGAL_LAHIR, null);
        alamat      = sharedpreferences.getString(ALAMAT, null);
        avatar      = sharedpreferences.getString(AVATAR, null);
        no_handphone = sharedpreferences.getString(NO_HANDPHONE, null);
        kutipan     = sharedpreferences.getString(KUTIPAN,null);
        updated_at  = sharedpreferences.getString(UPDATED_AT, null);
        id_role     = sharedpreferences.getString(ID_ROLE, null);

        if (session) {
            Intent redirect = new Intent(this, HomeActivity.class);
            redirect.putExtra(TAG_ID, id);
            redirect.putExtra(TAG_USERNAME, nim);
            redirect.putExtra(NAMA, nama);
            redirect.putExtra(EMAIL, email);
            redirect.putExtra(NO_ANGGOTA, no_anggota);
            redirect.putExtra(TEMPAT_LAHIR, tempat_lahir);
            redirect.putExtra(TANGGAL_LAHIR, tanggal_lahir);
            redirect.putExtra(ALAMAT, alamat);
            redirect.putExtra(AVATAR, avatar);
            redirect.putExtra(NO_HANDPHONE, no_handphone);
            redirect.putExtra(KUTIPAN, kutipan);
            redirect.putExtra(UPDATED_AT, updated_at);
            redirect.putExtra(ID_ROLE,id_role);
            finish();
            startActivity(redirect);
        }

        mContext = this;
        mApiService = UtilsApi.getAPIService(); // meng-init yang ada di package apihelper
        initComponents();
    }

    private void initComponents() {
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
//        btnRegister = (Button) findViewById(R.id.btnRegister);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading = ProgressDialog.show(mContext, null, "Harap Tunggu...", true, false);
                requestLogin();
            }
        });

//        btnRegister.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent regist = new Intent(MainActivity.this, RegisterActivity.class);
//                finish();
//                startActivity(regist);
//            }
//        });
    }

    private void requestLogin(){

        mApiService.login(etEmail.getText().toString(), etPassword.getText().toString()).enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    loading.dismiss();
                    try {
                        JSONObject jsonRESULTS = new JSONObject(response.body().string());
                        if (jsonRESULTS.getString("error").equals("false")){

                            String id           = jsonRESULTS.getJSONObject("user").getString("id");
                            String nim          = jsonRESULTS.getJSONObject("user").getString("nim");
                            String no_anggota   = jsonRESULTS.getJSONObject("user").getString("no_anggota");
                            String nama         = jsonRESULTS.getJSONObject("user").getString("nama");
                            String email        = jsonRESULTS.getJSONObject("user").getString("email");
                            String tempat_lahir = jsonRESULTS.getJSONObject("user").getString("tempat_lahir");
                            String tanggal_lahir = jsonRESULTS.getJSONObject("user").getString("tanggal_lahir");
                            String no_handphone = jsonRESULTS.getJSONObject("user").getString("no_handphone");
                            String alamat       = jsonRESULTS.getJSONObject("user").getString("alamat");
                            String kutipan      = jsonRESULTS.getJSONObject("user").getString("kutipan");
                            String avatar       = jsonRESULTS.getJSONObject("user").getString("avatar");
                            String id_role      = jsonRESULTS.getJSONObject("user").getString("id_role");
                            String updated_at   = jsonRESULTS.getJSONObject("user").getString("updated_at");

                            // Menyimpan login ke session
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putBoolean(SESSION_STATUS, true);
                            editor.putString(TAG_ID, id);
                            editor.putString(TAG_USERNAME, nim);
                            editor.putString(EMAIL,email);
                            editor.putString(NAMA,nama);
                            editor.putString(NO_ANGGOTA,no_anggota);
                            editor.putString(TEMPAT_LAHIR,tempat_lahir);
                            editor.putString(TANGGAL_LAHIR,tanggal_lahir);
                            editor.putString(NO_HANDPHONE,no_handphone);
                            editor.putString(ALAMAT,alamat);
                            editor.putString(KUTIPAN,kutipan);
                            editor.putString(AVATAR,avatar);
                            editor.putString(ID_ROLE,id_role);
                            editor.putString(UPDATED_AT,updated_at);
                            editor.commit();

                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            intent.putExtra(TAG_ID, id);
                            intent.putExtra(TAG_USERNAME, nim);
                            intent.putExtra(EMAIL,email);
                            intent.putExtra(NAMA,nama);
                            intent.putExtra(NO_ANGGOTA,no_anggota);
                            intent.putExtra(TEMPAT_LAHIR,tempat_lahir);
                            intent.putExtra(TANGGAL_LAHIR,tanggal_lahir);
                            intent.putExtra(NO_HANDPHONE,no_handphone);
                            intent.putExtra(ALAMAT,alamat);
                            intent.putExtra(KUTIPAN,kutipan);
                            intent.putExtra(AVATAR,avatar);
                            intent.putExtra(ID_ROLE,id_role);
                            intent.putExtra(UPDATED_AT,updated_at);
                            finish();

                            Toast.makeText(mContext, "Berhasil Login!", Toast.LENGTH_SHORT).show();
                            startActivity(intent);

                        } else {
                            // Jika login gagal
                            String error_message = jsonRESULTS.getString("error_msg");
                            Toast.makeText(mContext, error_message, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(mContext, "Gagal Login!", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    } catch (IOException e) {
                        Toast.makeText(mContext, "Masalah Koneksi Jaringan!", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(mContext, "Server Sedang Maintenance!", Toast.LENGTH_SHORT).show();
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

}
