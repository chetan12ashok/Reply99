package com.appdroid.reply99.whatsappAutoReply;

import com.appdroid.reply99.whatsappAutoReply.imageAndButtonsTempletForWA.CustomModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface IApiInterface {

    @POST("{phoneNoId}/messages")
    Call<MassageObj> sendMessage(@Path("phoneNoId") String phoneNoId,
                                 @Body RequastModel requastModel);

    @POST("{phoneNoId}/messages")
    Call<Object> sendCustomMassage(@Path("phoneNoId") String phoneNoId,
                                 @Body CustomModel customModel);

    @POST("{phoneNoId}/messages")
    Call<Object> sendCustomMassageWithVideo(@Path("phoneNoId") String phoneNoId,
                                   @Body com.appdroid.reply99.whatsappAutoReply.VideoTemplate.CustomModel customModel);


}
