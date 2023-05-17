package com.appdroid.reply99.whatsappAutoReply;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {
    private static Retrofit retrofit;

    private static String BASE_URL = "https://graph.facebook.com/";
    private static String VERSION = "v13.0/";


    public static Retrofit getRetrofitClint(){
        if (retrofit == null){
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL+VERSION)
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient().newBuilder().addInterceptor(new MyInterface()).build())
                .build();
        }
        return retrofit;

    }

}
