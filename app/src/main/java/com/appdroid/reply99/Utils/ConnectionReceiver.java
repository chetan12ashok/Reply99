package com.appdroid.reply99.Utils;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.appdroid.reply99.R;


public class ConnectionReceiver extends BroadcastReceiver {
    Context mContext;
    Dialog dialog;
    @Override
    public void onReceive(Context context, Intent intent) {


        if (isConnected(context)){
            if (dialog != null){
                dialog.dismiss();
            }
        }else {
            showDialog(context);
        }


    }

    public void showDialog(Context context){

        dialog = new Dialog(context);
        dialog.setContentView(R.layout.no_internet_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }


    private boolean isConnected(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return (networkInfo != null && networkInfo.isConnected());

        }catch (Exception e){
            return false;
        }
    }


}
