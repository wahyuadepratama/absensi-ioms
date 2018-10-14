package com.github.wahyuadepratama.absensi.apihelper;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by wahyu on 04/10/18.
 */

public interface BaseApiService {

    // Fungsi ini untuk memanggil API http://domain/api/login
    @FormUrlEncoded
    @POST("login")
    Call<ResponseBody> login(@Field("username") String username,
                             @Field("password") String password);

    @FormUrlEncoded
    @POST("checkPiket")
    Call<ResponseBody> checkPiket(@Field("id_user") String id_user,
                                  @Field("today") String today);

    @FormUrlEncoded
    @POST("register.php")
    Call<ResponseBody> registerRequest(@Field("nama") String nama,
                                       @Field("email") String email,
                                       @Field("password") String password);
}
