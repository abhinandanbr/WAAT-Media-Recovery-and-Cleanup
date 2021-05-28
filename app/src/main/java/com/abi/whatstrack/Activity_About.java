package com.abi.whatstrack;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import static com.abi.whatstrack.MainActivity.getScreenSizeInches;

public class Activity_About extends AppCompatActivity {

    private static String TAG=Util.TAG;
    Button btn_email,btn_review;
    LinearLayout prof_back,layout_btn;
    ImageView image_inst,image_mail,image_gplus;
    ImageView image_app,image_share;
    CardView cardView;
    ImageButton btn_donate;

    Animation from_bottom;

    private static SharedPreferences mPrefs;

    private static final String pref_logbool="logBool";
    private static final String ANDROID_PRE_BASE = "pre_base";


    int height,width;

    int dataLogMode=0;
    boolean logBool=false;

    TextView aboutVersion,about_license,text_about,text_aboutTitle,text_email,dev_id;

    String version;


    int setDur=700;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        getSupportActionBar().setIcon(R.mipmap.ic_wc_round);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("About");

        mPrefs = getSharedPreferences(FirebaseRemoteConfig.getInstance().getString(ANDROID_PRE_BASE), Context.MODE_PRIVATE);

        logBool= mPrefs.getBoolean(pref_logbool, false);

        from_bottom= AnimationUtils.loadAnimation(this,R.anim.about_anim);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;

        prof_back=(LinearLayout)findViewById(R.id.profile_back);
        cardView=(CardView) findViewById(R.id.card1);

        aboutVersion=(TextView)findViewById(R.id.about_version);
//        about_license=(TextView)findViewById(R.id.about_license);
        text_about=(TextView)findViewById(R.id.text_about);
        text_email=(TextView)findViewById(R.id.text_email);
        dev_id=(TextView)findViewById(R.id.dev_id);

        image_app=(ImageView)findViewById(R.id.app_image);
        image_inst=(ImageView)findViewById(R.id.img_insta);
        image_mail=(ImageView)findViewById(R.id.img_mail);
        image_gplus=(ImageView)findViewById(R.id.img_gplus);
        image_share=(ImageView)findViewById(R.id.img_share);

        btn_donate=(ImageButton)findViewById(R.id.btn_donate);

/*
        ViewGroup.LayoutParams params = prof_back.getLayoutParams();

        params.height = height/3;*/
//        prof_back.setLayoutParams(params);

        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            version = getString(R.string.about_version)+pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
        }
        aboutVersion.setText(version);

        interfaces(this);

        cardView.setAnimation(from_bottom);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
//                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void interfaces(final Context context){

//        DisplayMetrics dm = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(dm);
//        double x = Math.pow(dm.widthPixels/dm.xdpi,2);
//        double y = Math.pow(dm.heightPixels/dm.ydpi,2);
        double screenInches=0;

        TypedValue outValue = new TypedValue();
        getResources().getValue(R.fraction.screen_size, outValue, true);
        float screenLimit = outValue.getFloat();

        if(logBool)Log.i(TAG,"SizeInitial:"+""+screenInches);
        screenInches=getScreenSizeInches(this);
        if(logBool)Log.i(TAG,"SizeFinal:"+""+screenInches);


        if(screenInches<screenLimit){
//
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//            params.setMargins(0,15,0,0);
//
//            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(96,96);
//            params1.setMargins((int)(width/2.7f),25,0,0);
//
//            LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//            params2.setMargins(10,10,10,0);

/*            float scale=getResources().getConfiguration().fontScale;

            if(scale>1) {
            text_about.setTextSize(12);
            text_aboutTitle.setTextSize(14);
            text_email.setTextSize(12);
            dev_id.setTextSize(14);

            aboutVersion.setTextSize(10);
            about_license.setTextSize(10);

            btn_email.setTextSize(10);
            btn_review.setTextSize(10);
            }*/

//            dev_id.setTextScaleX(0.85f);
//
//            btn_email.setScaleY(0.9f);
//            btn_review.setScaleY(.9f);
//
//            img_profile.setScaleX(0.85f);
//            img_profile.setScaleY(.85f);
//
////            prof_back.setScaleX(0.9f);
//            int h=prof_back.getLayoutParams().height;
//            prof_back.getLayoutParams().height=(int)(0.9f*h);
//
//            if(width<720 && height<1280){
////                text_about.setTextSize(12);
//                prof_back.getLayoutParams().height=(int)(0.8f*h);
//
//                dev_id.setLayoutParams(params);
//                dev_id.setGravity(Gravity.CENTER_HORIZONTAL);
////                dev_id.setTextSize(15);
//
//                img_profile.setScaleX(0.8f);
//                img_profile.setScaleY(0.8f);
//                img_profile.setLayoutParams(params1);
//
//                layout_btn.setLayoutParams(params2);
//                layout_btn.setGravity(Gravity.CENTER_HORIZONTAL);
//                btn_email.setScaleX(0.8f);
//                btn_review.setScaleX(.8f);
//            }
        }

        aboutVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(dataLogMode<3 && !logBool)
                {
                    dataLogMode+=1;
                    timerToast(Toast.makeText(Activity_About.this,"Press "+(3-dataLogMode+1)+" more times to enable Data Logs!",Toast.LENGTH_SHORT),setDur);
                }
                else
                {
                    if(!logBool) {

                        ActivityCompat.requestPermissions(Activity_About.this,
                                new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE
                                }, 1);
//
//                        if (ContextCompat.checkSelfPermission(Activity_About.this,
//                                Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                                != PackageManager.PERMISSION_GRANTED) {
//
//
//
//                                if (ActivityCompat.shouldShowRequestPermissionRationale(Activity_About.this,
//                                        Manifest.permission.WRITE_EXTERNAL_STORAGE)){
//
//                                }
//                        }
//                        if (ContextCompat.checkSelfPermission(Activity_About.this,
//                                Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                                != PackageManager.PERMISSION_GRANTED) {
//                            logBool=false;
//                            dataLogMode=0;
//                            timerToast(Toast.makeText(Activity_About.this, "Data Logs Disabled due to Insufficient Access!", Toast.LENGTH_SHORT),setDur);
//
//                        }else{
//                            logBool=true;
//                            timerToast(Toast.makeText(Activity_About.this, "Data Logs Enabled! Click again to disable", Toast.LENGTH_SHORT),setDur);
//                        }


                    }
                    else if(logBool)
                    {
                        timerToast(Toast.makeText(Activity_About.this, "Data Logs Disabled!", Toast.LENGTH_SHORT),setDur);
                        logBool=false;
                        dataLogMode=0;
                    }
                }
                SharedPreferences.Editor editor = mPrefs.edit();
                editor.putBoolean(pref_logbool, logBool);
                editor.apply();
            }
        });

        image_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String appPackageName = getPackageName();
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.abi.whatstrack"));
                    intent.setPackage("com.android.vending");

                    startActivity(intent);
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });
        image_gplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com/+AbhinandanBR")));

            }
        });
        image_inst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/abhinandan.photography")));
                overridePendingTransition(R.anim.layout_frombottom,R.anim.layout_tobottom);
            }
        });
        image_mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("plain/text");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[] { getResources().getString(R.string.string_email) });
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name)+" Feedback");
                startActivity(Intent.createChooser(intent, ""));
            }
        });
        image_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareCompat.IntentBuilder.from(Activity_About.this)
                        .setType("text/plain")
                        .setChooserTitle("Chooser title")
                        .setText("http://play.google.com/store/apps/details?id=" + Activity_About.this.getPackageName())
                        .startChooser();
            }
        });

        btn_donate.setVisibility(View.GONE);
        btn_donate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    public void timerToast(final Toast toast,int toastDurationInMilliSeconds){
        CountDownTimer toastCountDown = new CountDownTimer(toastDurationInMilliSeconds, 1000 /*Tick duration*/) {
            public void onTick(long millisUntilFinished) {
                toast.show();
            }
            public void onFinish() {
                toast.cancel();
            }
        };
        toast.show();
        toastCountDown.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.layout_frombottom,R.anim.layout_tobottom);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    logBool=true;
                    timerToast(Toast.makeText(Activity_About.this, "Data Logs Enabled! Click again to disable", Toast.LENGTH_SHORT),setDur);
                    SharedPreferences.Editor editor = mPrefs.edit();
                    editor.putBoolean(pref_logbool, logBool);
                    editor.apply();

                } else {

                    logBool=false;
                    dataLogMode=0;
                    timerToast(Toast.makeText(Activity_About.this, "Data Logs Disabled due to Insufficient Access!", Toast.LENGTH_SHORT),setDur);
                    SharedPreferences.Editor editor = mPrefs.edit();
                    editor.putBoolean(pref_logbool, logBool);
                    editor.apply();
                }
                return;
            }

            // other 'switch' lines to check for other
            // permissions this app might request
        }
    }
}
