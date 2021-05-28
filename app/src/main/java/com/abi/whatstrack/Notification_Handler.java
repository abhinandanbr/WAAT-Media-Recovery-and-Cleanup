package com.abi.whatstrack;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;


public class Notification_Handler extends Activity {


    public  static int GPS=1;

    public void Notification_Handler(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        NotificationManager mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if(extras.getInt("ID")==0) {
//
//            mGoogleApiClient = new GoogleApiClient.Builder(context)
//                    .addApi(LocationServices.API)
//                    .addConnectionCallbacks(this)
//                    .addOnConnectionFailedListener(this)
//                    .build();
//            mGoogleApiClient.connect();

            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS), 0);
            }else {
                startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
            }
            mNotificationManager.cancel(1);
        }
        else if(extras.getInt("ID")==1) {
            startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
            mNotificationManager.cancel(2);
        }else if(extras.getInt("ID")==2) {
        }
        else if(extras.getInt("ID")==3) {
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS), 0);
            }else {
                startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
            }
            mNotificationManager.cancel(4);
        }
        //startActivityForResult(new Intent(WifiManager.ACTION_REQUEST_SCAN_ALWAYS_AVAILABLE), 100);
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data){
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "User enabled Scan always available", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "User did not enable Scan always available", Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }
}
