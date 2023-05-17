package com.appdroid.reply99.whatsappAutoReply;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class MyInterface implements Interceptor {
    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        Request newRequast = request.newBuilder()
                .header("Authorization","Bearer EAASpiqmUlLcBAG68JSOTSk3nnvvZBRZBZBjl5pjgycfb55h2jFyPbNmBdMU2xwtOmhI3xb3HcvzIHZCRw3NZBKHxuOPAZB67BZBsrvexkj0cOO4GfF03XI2eoD67yD7XjtJ7dibZAfbInGP192wEQABemY00ql1gsVUdK68VnjCMz1RKJr3QiGmK")
                .header("Content-Type","application/json")
                .build();
        return chain.proceed(newRequast);

    }
}
