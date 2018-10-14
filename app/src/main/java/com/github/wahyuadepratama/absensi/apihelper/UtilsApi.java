package com.github.wahyuadepratama.absensi.apihelper;

/**
 * Created by wahyu on 04/10/18.
 */

public class UtilsApi {

    public static final String BASE_URL_API = "http://ioms.hmsiunand.com/api/";

    // Mendeklarasikan Interface BaseApiService
    public static BaseApiService getAPIService(){
        return RetrofitClient.getClient(BASE_URL_API).create(BaseApiService.class);
    }
}
