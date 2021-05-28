package com.abi.whatstrack;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.RemoteException;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v14.preference.PreferenceFragment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.abi.whatstrack.util.IabHelper;
import com.abi.whatstrack.util.IabResult;
import com.abi.whatstrack.util.Security;
import com.android.vending.billing.IInAppBillingService;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import static com.abi.whatstrack.util.IabHelper.BILLING_RESPONSE_RESULT_OK;
import static com.abi.whatstrack.util.Util.scheduleJobService;

public class MainActivity extends AppCompatActivity {
    private static final String TAG =Util.TAG;

    //UI
    private boolean helpCheck;
    private boolean autoCleanDb;
    private boolean doubleBackToExitPressedOnce = false;
    Menu menu;
    int height, width;

    //SharedPrefer
    private static SharedPreferences mPrefs;
    private static SharedPreferences settings1;
    private static SharedPreferences sharedSettingPreferences;
    private static String mPrefs_key;
    private static final String pref_autoCleanDB="autoCleanDB";
    private static final String pref_tutorial="tutorial";


    //Rate Card
    private boolean reviewCheck;
    private long ratePrevDate, ratePresDate;
    private int rateDays;
    private int rateAppOpenTimes;

    private boolean logBool=true;


    //Rating
    private static final String pref_logbool="logBool";
    private static final String pref_ad_app_open_times="ad_app_open_times";
    private static final String pref_rate_app_open_times="rateappopentimes";
    private static final String pref_review="review";
    private static final String pref_helpCheck="helpCheck";
    private static final String pref_rate_prev_date="rateprevdate";


    //Firebase
    private String mCurrentversionName, mLatestVersionName = "";
    View layout;

    private static final String admobID = "ca-app-pub-4248687703418801~2345453708";
    private AdView adMobView;
    private boolean loadAd = true;
    private boolean showAds=true;
    int adAppOpenTimes;
    private InterstitialAd mInterstitialAd;
    private FirebaseAnalytics mFirebaseAnalytics;
    FirebaseRemoteConfig mFirebaseRemoteConfig;
    private static final String ANDROID_VERSION_CONFIG = "android_latest_version_code";
    private static final String ANDROID_BASE_KEY = "base";
    private static final String ANDROID_PRE_BASE = "pre_base";
    private static final int REQUEST_CODE_EMAIL = 1;
    private static final int REQUEST_FOR_VERIFY = 999;
    private static String baseApi="";
    private boolean update_fetch;
    private String playStoreVersionCode;

    //Billing
    private IInAppBillingService mService;
    private ServiceConnection mServiceConn;
    private IabHelper mHelper;
    private String mSmallDonatePrice,mMedDonatePrice,mLargeDonatePrice,mGenDonatePrice;
    private static String sku="large_donate";
    private static boolean inAppBool=false;
    private static boolean beenInDisable=false;
    private static final String billIntent="com.android.vending.billing.InAppBillingService.BIND";
    private static final String billVending="com.android.vending";
    private static String billMailID;
    private static String developerPayload;
    //Billing Account
    private String checkAccount;

    //Logs
    private static final String log_version="VERSION:";
    private static final String log_count="Count:";

    private ViewPager viewPager;
    private PagerAdapter mPagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_main);

        sharedSettingPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        Intent intent = new Intent(this,Activity_Intro.class);

        if(sharedSettingPreferences.getBoolean(pref_tutorial,true)) {
            startActivity(intent);
        }

        mCurrentversionName = BuildConfig.VERSION_NAME;

        mServiceConn = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
                mService = null;
            }

            @Override
            public void onServiceConnected(ComponentName name,
                                           IBinder service) {
                mService = IInAppBillingService.Stub.asInterface(service);
            }
        };

        Intent serviceIntent =
                new Intent(billIntent);
        serviceIntent.setPackage(billVending);
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);

        permission();

        settings1 =getSharedPreferences("WhatsTrackSettings",Context.MODE_PRIVATE);
        settings1.edit().putBoolean("app_exit",false).apply();

        try {
            adFireBase(savedInstanceState, this, true);
        }catch (Exception e){

        }

        startService(new Intent(MainActivity.this.getApplicationContext(), LowMem.class));

//        interfacesInit();

        logBool = mPrefs.getBoolean(pref_logbool, false);
        reviewCheck = mPrefs.getBoolean(pref_review, false);
        rateAppOpenTimes = mPrefs.getInt(pref_rate_app_open_times, 0);
        ratePrevDate = mPrefs.getLong(pref_rate_prev_date, 0);
        helpCheck = mPrefs.getBoolean(pref_helpCheck, true);

        //Ads
        adAppOpenTimes = mPrefs.getInt(pref_ad_app_open_times, 0);
        if(getResources().getBoolean(R.bool.adEnable) && showAds){
            adAppOpenTimes+=1;
        }


        //UI Loading
        ratePresDate = Calendar.getInstance().getTimeInMillis();
        rateDays = (int) (ratePresDate - ratePrevDate) / (int) DateUtils.DAY_IN_MILLIS;
        layout=findViewById(R.id.root);
        mCurrentversionName = BuildConfig.VERSION_NAME;
        rateDialog(this, reviewCheck);

        //Display Calculation
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;

        getSupportActionBar().setIcon(R.mipmap.ic_wc_round);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
//        getSupportActionBar().setTitle("Junk Cleaner");


        //UI


//        viewPager = (ViewPager) findViewById(R.id.pager);
//        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
//        setupViewPager(viewPager);
//        TabLayout tabs = (TabLayout) findViewById(R.id.tab_layout);
//        tabs.setupWithViewPager(viewPager);
//        tabs.setTabGravity(TabLayout.GRAVITY_FILL);
//        tabs.setTabMode(TabLayout.MODE_FIXED);
//        tabs.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
//        tabs.getTabAt(0).setIcon(getResources().getDrawable(R.drawable.ic_database));
//        tabs.getTabAt(1).setIcon(getResources().getDrawable(R.drawable.ic_voice));

        selectItem(0);
        interfaceInit(this);

        autoCleanDb=sharedSettingPreferences.getBoolean(pref_autoCleanDB,false);

//        interfaces(this);
//        interfaceAnimations(this);

//        optimize_check(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        this.menu=menu;

        if(!showAds) {
            MenuItem item = menu.findItem(R.id.action_donate);
            item.setEnabled(false);
        }

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (item.getItemId()) {
            //noinspection SimplifiableIfStatement
            case R.id.action_settings:
                selectItem(1);
//                startActivityForResult(new Intent(getApplicationContext(), Settings_activity.class), 0);
                overridePendingTransition(R.anim.layout_frombottom,R.anim.layout_tobottom);
                return true;
            case R.id.action_about:
                startActivity(new Intent(getApplicationContext(), Activity_About.class));
                overridePendingTransition(R.anim.layout_frombottom,R.anim.layout_tobottom);
                return true;
            case R.id.action_donate:
                billingAccount(this);
                return true;
            case R.id.action_rate:
                final String appPackageName = getPackageName();
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.abi.whatstrack"));
                    startActivity(intent);
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
                overridePendingTransition(R.anim.layout_frombottom,R.anim.layout_tobottom);
                return true;
            case R.id.exit_settings:
                finish();
                overridePendingTransition(R.anim.layout_frombottom,R.anim.layout_tobottom);
                return true;
            case R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                overridePendingTransition(R.anim.layout_frombottom,R.anim.layout_tobottom);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void selectItem(int position) {
        // update the main content by replacing fragments
//
//        switch(position) {
//            case 7:
//                finish();
//                break;
//        }

        Fragment fragment ;
        int save=0;
        switch(position) {
            default:
                fragment = PagerFragMain.newInstance(mPrefs_key,showAds);
                break;
            case 0:
                fragment = PagerFragMain.newInstance(mPrefs_key,showAds);
                break;
            case 1:
                fragment = SettingsFrag.newInstance(mPrefs_key,showAds);
                save=1;
                break;
        }
        Bundle args = new Bundle();
//        args.putInt(Home.ARG_TITLE_NUMBER, position);
//        fragment.setArguments(args);

        FragmentTransaction trans = getSupportFragmentManager()
                .beginTransaction();
        trans.replace(R.id.content_frame,fragment);
        trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        if(save==1) {
            trans.addToBackStack("Home");
        }
        else
        {
            trans.addToBackStack(null);
        }

        trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        trans.commit();

        // update selected item and title, then close the drawer
//        mDrawerList.setItemChecked(position, true);
//        setTitle(mtitles[position]);
//        mDrawerLayout.closeDrawer(mDrawerList);
    }

    public void permission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1);
            }
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        settings1.edit().putBoolean("app_exit",true).apply();

        if(mServiceConn!=null) {
            try {
                Log.i(TAG, "Bill Service DISAA");

                unbindService(mServiceConn);
            } catch (NullPointerException e) {
                Log.i(TAG, "Bill Service Exception");

            }
        }

        if(mHelper!=null){
            try {
                mHelper.dispose();
                mHelper=null;
            }catch ( Exception e){
                Log.i(TAG,"Bill Dispose Exception");
            }
        }


        if(getResources().getBoolean(R.bool.adEnable) && showAds){
            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putInt(pref_ad_app_open_times, adAppOpenTimes);
            editor.apply();

            if (adAppOpenTimes == getResources().getInteger(R.integer.ad_rate_min_times_on_destroy))
                mInterstitialAd.show();
        }

//        startService(new Intent(MainActivity.this.getApplicationContext(), LowMem.class));
        autoCleanDb=sharedSettingPreferences.getBoolean(pref_autoCleanDB,false);
        if(autoCleanDb) {
            if (Build.VERSION.SDK_INT >= 26) {
                scheduleJobService(getApplicationContext(),JobServiceJob.class);
//                startForegroundService(new Intent(MainActivity.this.getApplicationContext(), LowMem.class));
//                startForegroundService(new Intent(MainActivity.this.getApplicationContext(), NotiListenerService.class));
            }else
                startService(new Intent(MainActivity.this.getApplicationContext(), NotiListenerService.class));
        }
    }

    @Override
    public void onBackPressed() {

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (!(fragment instanceof IOBackPressed) || !((IOBackPressed) fragment).onBackPressed()) {

            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                overridePendingTransition(R.anim.layout_frombottom, R.anim.layout_tobottom);
                finish();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(getApplicationContext(), "Press BACK again to exit", Toast.LENGTH_SHORT).show();


            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }


    public static double getScreenSizeInches(Activity activity) {
        WindowManager windowManager = activity.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);

        // since SDK_INT = 1;
        int mWidthPixels = displayMetrics.widthPixels;
        int mHeightPixels = displayMetrics.heightPixels;

        // includes window decorations (statusbar bar/menu bar)
        if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17) {
            try {
                mWidthPixels = (Integer) Display.class.getMethod("getRawWidth").invoke(display);
                mHeightPixels = (Integer) Display.class.getMethod("getRawHeight").invoke(display);
            } catch (Exception ignored) {

            }
        }

        // includes window decorations (statusbar bar/menu bar)
        if (Build.VERSION.SDK_INT >= 17) {
            try {
                Point realSize = new Point();
                Display.class.getMethod("getRealSize", Point.class).invoke(display, realSize);
                mWidthPixels = realSize.x;
                mHeightPixels = realSize.y;
            } catch (Exception ignored) {

            }
        }

        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        double x = Math.pow(mWidthPixels / dm.xdpi, 2);
        double y = Math.pow(mHeightPixels / dm.ydpi, 2);
        return Math.sqrt(x + y);
    }

    public void rateDialog(Context context, boolean reviewCheck) {
        if (logBool) Log.i(TAG, "Rate" + rateAppOpenTimes + " " + rateDays);

        if (!reviewCheck) {
            if (rateAppOpenTimes >= getResources().getInteger(R.integer.rate_min_times) && rateDays >= getResources().getInteger(R.integer.rate_min_days)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.rateTitle);
                builder.setMessage(R.string.rateMessage);
                builder.setCancelable(false);
                builder.setPositiveButton("RATE NOW.",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.abi.whatstrack"));
                                startActivity(intent);
                                SharedPreferences.Editor editor = mPrefs.edit();
                                editor.putBoolean(pref_review, true);
                                editor.apply();
                                overridePendingTransition(R.anim.layout_frombottom,R.anim.layout_tobottom);
                            }
                        });
                builder.setNegativeButton("No, Thanks.",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                SharedPreferences.Editor editor = mPrefs.edit();
                                editor.putBoolean(pref_review, true);
                                editor.apply();
                            }
                        });
                builder.setNeutralButton("REMIND ME LATER.", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        rateAppOpenTimes = 0;
                        ratePrevDate = Calendar.getInstance().getTimeInMillis();
                        SharedPreferences.Editor editor = mPrefs.edit();
                        editor.putInt(pref_rate_app_open_times, rateAppOpenTimes);
                        editor.putLong(pref_rate_prev_date, ratePrevDate);
                        editor.apply();
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.getWindow().setWindowAnimations(R.style.DialogAnim);
                alertDialog.show();
            } else if (rateAppOpenTimes < getResources().getInteger(R.integer.rate_min_times)) {
                rateAppOpenTimes += 1;
                SharedPreferences.Editor editor = mPrefs.edit();
                editor.putInt(pref_rate_app_open_times, rateAppOpenTimes);
                editor.apply();
            }
        }
    }
    public void optimize_check(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            final Intent intent = new Intent();
            String packageName = getBaseContext().getPackageName();

            PowerManager pm = (PowerManager) getBaseContext().getSystemService(Context.POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Battery Optimization Enabled !!");
                builder.setMessage(R.string.optimize_checkPop);
                builder.setCancelable(false);
                builder.setPositiveButton("Disable",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                                startActivity(intent);
                            }
                        });
                builder.setNegativeButton("No, Thanks.",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.getWindow().setWindowAnimations(R.style.DialogAnim);

                alertDialog.show();
            }
            if (logBool) Log.i(TAG, "" + "Battery Optimization Request");
        }
    }

    //Billing
    class Task implements Runnable{

        @Override
        public void run() {
            inAppPurchaseSetup(MainActivity.this);

        }
    }
    private void inAppPurchaseSetup(Context context){


        mHelper= new IabHelper(this,baseApi);

        mHelper.enableDebugLogging(true);

        mHelper.startSetup(new IabHelper.
                OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result){
                if(!result.isSuccess()){

                    if(logBool){
                        Log.i(TAG,"Billing Error");
                    }
                    inAppBool=false;
                }else{
                    if(logBool){
                        Log.i(TAG,"Billing Success");
                    }

                    ArrayList<String> skuList = new ArrayList<String>();
                    skuList.add("small_donate");
                    skuList.add("medium_donate");
                    skuList.add("large_donate");
                    skuList.add("gen_donate");
                    Bundle querySkus = new Bundle();
                    querySkus.putStringArrayList("ITEM_ID_LIST",skuList);
                    try {
                        Bundle skuDetails = mService.getSkuDetails(3,getPackageName(),"inapp",querySkus);
                        int response =skuDetails.getInt("RESPONSE_CODE");
                        final int BILLING_RESPONSE_RESULT_OK = 0;

                        if(response==BILLING_RESPONSE_RESULT_OK){
                            ArrayList<String> responseList
                                    =skuDetails.getStringArrayList("DETAILS_LIST");
                            for(String thisResponse: responseList){
                                JSONObject object= new JSONObject(thisResponse);
                                String sku = object.getString("productId");
                                String price =object.getString("price");
                                if(sku.equals("small_donate"))
                                    mSmallDonatePrice=price;
                                else if(sku.equals("medium_donate"))
                                    mMedDonatePrice=price;
                                else if(sku.equals("large_donate"))
                                    mLargeDonatePrice=price;
                                else if(sku.equals("gen_donate"))
                                    mGenDonatePrice=price;
                            }
                            if(logBool){
                                Log.i(TAG,"Bill :"+mSmallDonatePrice+" "+mMedDonatePrice+" "+mLargeDonatePrice+" "+mGenDonatePrice);
                            }
                            inAppBool=true;
                            checkPurchases(MainActivity.this);
                        }

                    }catch (RemoteException | JSONException e){
                        Log.i(TAG,"Bill could not fetch in app products");
                    }
                }
            }
        });
    }
    private void checkPurchases(Context context){
        try {
            checkAccount=mPrefs.getString("accountVerify","null");

            Bundle ownedItems = mService.getPurchases(3, getPackageName(), "inapp", null);

            int response = ownedItems.getInt("RESPONSE_CODE");
            if (response == BILLING_RESPONSE_RESULT_OK) {
                ArrayList<String> ownedSkus =
                        ownedItems.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
                ArrayList<String>  purchaseDataList =
                        ownedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
                ArrayList<String>  signatureList =
                        ownedItems.getStringArrayList("INAPP_DATA_SIGNATURE_LIST");
                String continuationToken =
                        ownedItems.getString("INAPP_CONTINUATION_TOKEN");

                for (int i = 0; i < purchaseDataList.size(); ++i) {
                    String purchaseData = purchaseDataList.get(i);
                    String signature = signatureList.get(i);
                    String sku = ownedSkus.get(i);

                    boolean bought= Security.verifyPurchase(baseApi,purchaseData,signature);

                    if(bought){
                        Log.i(TAG,"Bill Bought");

                        if(Build.VERSION.SDK_INT>=23) {

//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        if (ContextCompat.checkSelfPermission(MainActivity.this,
//                                                Manifest.permission.GET_ACCOUNTS)
//                                                != PackageManager.PERMISSION_GRANTED) {
//                                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//                                            builder.setTitle("Permission");
//                                            builder.setMessage("Looks like you have purchased the app! Grant Access to Verify your purchase!");
//                                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                                                @Override
//                                                public void onClick(DialogInterface dialogInterface, int i) {
//
//                                                    ActivityCompat.requestPermissions(MainActivity.this,
//                                                            new String[]{Manifest.permission.GET_ACCOUNTS},
//                                                            10000);
//
//                                                }
//                                            });
//                                            AlertDialog alertDialog = builder.create();
//                                            alertDialog.getWindow().setWindowAnimations(R.style.DialogAnim);
//                                            alertDialog.show();
//                                        }
//
//                                    }
//                                })
                            try {
                                JSONObject jo = new JSONObject(purchaseData);
                                developerPayload = jo.getString("developerPayload");

                                if(!checkAccount.equals("null")){
                                    if (checkAccount.equals(developerPayload)) {

//                                            mPrefs.edit().putString("accountVerify", checkAccount).apply();

                                        Log.i(TAG, "Bill Bought Verify");

                                        billingDisable(MainActivity.this, true);
                                        timerToast(Toast.makeText(MainActivity.this, "Purchase Verified", Toast.LENGTH_LONG), 1000);
                                        break;

                                    } else {
                                        Log.i(TAG, "Bill Bought Verify Error");
                                        // Toast.makeText(MainActivity.this, "Purchase Verify Error", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else {

                                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                    builder.setTitle(getResources().getString(R.string.IABAccountVerify));
                                    builder.setMessage(getResources().getString(R.string.IABAccountDesVerify));
                                    builder.setCancelable(false);
                                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                            Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                                                    new String[] { GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE }, false, null, null, null, null);

                                            startActivityForResult(intent, REQUEST_FOR_VERIFY);

                                            dialogInterface.dismiss();
                                        }
                                    });

                                    AlertDialog alertDialog = builder.create();
                                    alertDialog.getWindow().setWindowAnimations(R.style.DialogAnim);
                                    alertDialog.show();

                                }

                            } catch (JSONException e) {
                                Log.i(TAG, "Bill: Purchases Retrieve JSON Exception");
                            }
                        }else{
                            try {
                                JSONObject jo = new JSONObject(purchaseData);
                                String developerPayload = jo.getString("developerPayload");

                                Account[] accounts = AccountManager.get(MainActivity.this).getAccountsByType("com.google");
                                for (Account account : accounts) {
                                    if(account.name.equals(developerPayload)){
                                        Log.i(TAG,"Bill Bought Verify");
                                        billingDisable(MainActivity.this,true);
                                        timerToast(Toast.makeText(MainActivity.this, "Purchase Verified", Toast.LENGTH_LONG),1000);

                                        break;
                                    }else{
                                        Log.i(TAG,"Bill Bought Verify Error");
                                        // Toast.makeText(MainActivity.this, "Purchase Verify Error", Toast.LENGTH_SHORT).show();
                                    }
                                }

                            }
                            catch (JSONException e) {
                                Log.i(TAG,"Bill: Purchases Retrieve JSON Exception");
                            }
                        }


                    }else{
                        billingDisable(context,false);
                        Toast.makeText(MainActivity.this, "Purchase Verify Error", Toast.LENGTH_SHORT).show();
                    }

                    // do something with this purchase information
                    // e.g. display the updated list of products owned by user
                }
                Log.i(TAG,"Bill: Purchases Retrieve Success");

                // if continuationToken != null, call getPurchases again
                // and pass in the token to retrieve more items
            }else{
                Log.i(TAG,"Bill: Purchases Retrieve Error");
            }

        }catch (Exception e){
            e.printStackTrace();
            Log.i(TAG,"Bill: Purchases Retrieve Error");
        }
    }
    private void inAppExecutePurchase(Context context){
        try {
            if(inAppBool) {

                if(billMailID!=null) {
                    Bundle buyIntentBundle = mService.getBuyIntent(3, getPackageName(),
                            sku, "inapp", billMailID);
                    PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
                    int REQUEST_CODE = 1001;
                    startIntentSenderForResult(pendingIntent.getIntentSender(),
                            REQUEST_CODE, new Intent(), Integer.valueOf(0), Integer.valueOf(0),
                            Integer.valueOf(0));
                }

            }else{
                timerToast(Toast.makeText(MainActivity.this, "Purchase Initiate Failed! ", Toast.LENGTH_SHORT),2500);
            }

        }catch (RemoteException | IntentSender.SendIntentException e){
            Log.i(TAG,"Bill Initiate Fail");
        }
    }
    private void billingDialog(Context context){
        String[] items={getResources().getString(R.string.IABsub1)+mSmallDonatePrice
                ,getResources().getString(R.string.IABsub2)+mMedDonatePrice
                ,getResources().getString(R.string.IABsub3)+mLargeDonatePrice
                ,getResources().getString(R.string.IABsub4)+mGenDonatePrice};
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(getResources().getString(R.string.IABTitle));
        builder.setSingleChoiceItems(items, 2, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i==0){
                    sku="small_donate";
                }else if(i==1){
                    sku="medium_donate";
                }else if(i==2){
                    sku="large_donate";
                }
                else if(i==3){
                    sku="gen_donate";
                }
            }
        });
        builder.setPositiveButton("Go", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                inAppExecutePurchase(MainActivity.this);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setWindowAnimations(R.style.DialogAnim);
        alertDialog.show();
    }
    public void billingAccount(Context context){
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(getResources().getString(R.string.IABAccount));
            builder.setMessage(getResources().getString(R.string.IABAccountDes));
            builder.setCancelable(false);
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                            new String[] { GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE }, false, null, null, null, null);

                    startActivityForResult(intent, REQUEST_CODE_EMAIL);

                    dialogInterface.dismiss();
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.getWindow().setWindowAnimations(R.style.DialogAnim);
            alertDialog.show();


        } catch (ActivityNotFoundException | NullPointerException e) {
            Log.i(TAG,"Bill Account Fail");
        }
    }
    private void billingDisable(Context context,boolean check){

        if(check) {

            try {

                MenuItem item = menu.findItem(R.id.action_donate);
                item.setEnabled(false);

            }catch (Exception e){


//                btn_donate.setVisibility(View.GONE);
//                btn_donate.setEnabled(false); TODO

                adMobView.setVisibility(View.GONE);
                mPrefs.edit().putBoolean("adCheck", false).apply();
                mPrefs.edit().putInt("random",101).apply();
                showAds = false;


                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if(checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED && !showAds) {
                        storage_check(MainActivity.this);
                    }
                }
            }
            try {
                SampleDb sampleDb = new SampleDb(MainActivity.this);
                SQLiteDatabase db = sampleDb.getWritableDatabase();

                String sortOrder =
                        SampleTable.SampleTableEntry._ID + " DESC" + " LIMIT 1";

                Cursor cursor = db.query(SampleTable.SampleTableEntry.TABLE_NAME, null, null,
                        null, null, null, sortOrder);

                ContentValues values= new ContentValues();

                values.put(SampleTable.SampleTableEntry.COLUMN_NAME_TITLE, "dummy");
                db.insertOrThrow(SampleTable.SampleTableEntry.TABLE_NAME, null, values);

                cursor.close();
                db.close();

            }catch (Exception e){
                Log.i(TAG,"Failed");
            }
//            btn_donate.setVisibility(View.GONE);
//            btn_donate.setEnabled(false); TODO

            adMobView.setVisibility(View.GONE);
            mPrefs.edit().putBoolean("adCheck", false).apply();
            mPrefs.edit().putInt("random",101).apply();
            showAds = false;



        }else{
            try {
                mPrefs.edit().putBoolean("adCheck", true).apply();
                mPrefs.edit().remove("random").apply();
                context.deleteDatabase(SampleDb.DATABASE_NAME);
                Log.i(TAG,"BUYER "+"NOT BOUGHT");
            }catch (Exception e){

            }
        }
        beenInDisable=true;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1001) {
            int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
            String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
            String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");

            if (resultCode == RESULT_OK) {
                try {
                    JSONObject jo = new JSONObject(purchaseData);
                    String sku = jo.getString("productId");
                    timerToast(Toast.makeText(MainActivity.this, "Purchase Success", Toast.LENGTH_SHORT),2500);
                }
                catch (JSONException e) {
                    timerToast(Toast.makeText(MainActivity.this, "Purchase Parse Failed! ", Toast.LENGTH_SHORT),2500);
                    Log.i(TAG,"Purchase Parse Failed");
                    e.printStackTrace();
                }
            }
        }else if (requestCode == REQUEST_CODE_EMAIL) {
            if(resultCode==RESULT_OK) {
                String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                billMailID = accountName;

                mPrefs.edit().putString("accountVerify", billMailID).apply();

                billingDialog(MainActivity.this);
            }else{
//                Log.i(TAG,"Bill NO ACCCCC");
            }
        }else if(requestCode == REQUEST_FOR_VERIFY){
            if(resultCode==RESULT_OK) {
                String account = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                if (account.equals(developerPayload)) {

                    mPrefs.edit().putString("accountVerify", account).commit();

                    Log.i(TAG, "Bill Bought Verify");

                    billingDisable(MainActivity.this, true);
                    timerToast(Toast.makeText(MainActivity.this, "Purchase Verified", Toast.LENGTH_LONG), 1000);


                } else {
                    Log.i(TAG, "Bill Bought Verify Error");
                    // Toast.makeText(MainActivity.this, "Purchase Verify Error", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    //Ads and Firebase
    public void adFireBase(Bundle bundle1, Context context, boolean fireBase) {

        adMobView = (AdView) findViewById(R.id.adView);

        if (fireBase) {
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "WhatsTrack");
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

            if (FirebaseApp.getApps(context).isEmpty()) {
                FirebaseApp.initializeApp(context, FirebaseOptions.fromResource(context));
            }
            mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

            FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder( )
                    .setDeveloperModeEnabled ( false )
                    .build();
            mFirebaseRemoteConfig.setConfigSettings ( configSettings );
            mFirebaseRemoteConfig.setDefaults ( R.xml.remote_config_defaults );

            long cacheExpiration = 3600; // 1 hour in seconds.
            // If in developer mode cacheExpiration is set to 0 so each fetch will retrieve values from
            // the server.
            if (mFirebaseRemoteConfig.getInfo( ).getConfigSettings( ).isDeveloperModeEnabled( )) {
                cacheExpiration = 0;
            }
            mPrefs_key=FirebaseRemoteConfig.getInstance().getString(ANDROID_PRE_BASE);
            mPrefs = getSharedPreferences(FirebaseRemoteConfig.getInstance().getString(ANDROID_PRE_BASE), Context.MODE_PRIVATE);
            showAds=!(mPrefs.getBoolean("adCheck",true) && ((((mPrefs.getInt("random",100)%2)%3))==1));
            showAds=!showAds;

            adsCheck(this);

            mFirebaseRemoteConfig.fetch ( cacheExpiration )
                    .addOnCompleteListener ( this, new OnCompleteListener<Void>( ) {
                        @Override
                        public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task ) {
                            if ( task.isSuccessful ( )) {
                                update_fetch=true;

                                mFirebaseRemoteConfig.activateFetched ( );
                            } else {

                                update_fetch=false;
                                beenInDisable=false;
                                if(!showAds && !beenInDisable){
                                    billingDisable(MainActivity.this,true);
                                }

                            }
                            final String playStoreVersion = FirebaseRemoteConfig.getInstance().getString(ANDROID_VERSION_CONFIG);
                            baseApi = FirebaseRemoteConfig.getInstance().getString(ANDROID_BASE_KEY);


                            final Handler handler = new Handler();

                            new Thread(new Task()).start();

                            handler.postDelayed(new Runnable() {

                                @Override

                                public void run() {

                                    check_update(playStoreVersion, MainActivity.this, false);

                                }

                            }, 1000L);
                            if (logBool) {

                                Log.i(TAG, log_version + "CHECK 1 "+playStoreVersionCode);

                            }
                        }
                    } );

        }

        if(getResources().getBoolean(R.bool.adEnable) && showAds) {
            MobileAds.initialize(this, admobID);
            mInterstitialAd = new InterstitialAd(this);
            mInterstitialAd.setAdUnitId(getString(R.string.adUnitID_Inter));

            mInterstitialAd.loadAd(new AdRequest.Builder().build());


            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {

                    if (mInterstitialAd.isLoaded() && showAds) {
                        if (loadAd && (adAppOpenTimes > getResources().getInteger(R.integer.ad_rate_min_times))) {
                            mInterstitialAd.show();
                            loadAd = false;
                            adAppOpenTimes = 0;
                        }
                    }

                }

                @Override
                public void onAdClosed() {
                    // Load the next interstitial.
                    mInterstitialAd.loadAd(new AdRequest.Builder().build());
                }

            });

            AdRequest adRequest = new AdRequest.Builder().build();
            adMobView.loadAd(adRequest);
            adMobView.refreshDrawableState();
        }
        else {
            adMobView.setVisibility(View.GONE);
        }
    }
    private void adsCheck(Context context){
        if(true) {
            try {
                showAds = true;
                SampleDb sampleDb = new SampleDb(context);
                SQLiteDatabase db = sampleDb.getReadableDatabase();

                String sortOrder =
                        SampleTable.SampleTableEntry._ID + " DESC" + " LIMIT 1";

                Cursor cursor = db.query(SampleTable.SampleTableEntry.TABLE_NAME, null, null,
                        null, null, null, sortOrder);

                ArrayList<String> check = new ArrayList<>();
                while (cursor.moveToNext()) {
                    long itemId = cursor.getLong(cursor.getColumnIndexOrThrow(SampleTable.SampleTableEntry._ID));
                    String item = cursor.getString(cursor.getColumnIndexOrThrow(SampleTable.SampleTableEntry.COLUMN_NAME_TITLE));
//                    Log.i(TAG, "DABASE " + item+" "+itemId);
                    check.add(item);
                    showAds = false;
                }
                cursor.close();
                db.close();

            } catch (Exception e) {
                showAds = true;
            }
        }
    }

    //UI and Misc
    private void interfaceInit(Context context){

    }
    private boolean isNetworkAvailable() {

        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }catch (NullPointerException e){
            return false;
        }
    }
    public void check_update(String newversion, Context context, boolean bool) {


        mLatestVersionName = newversion;
        if (logBool)
            Log.i(TAG, log_version + mLatestVersionName + " " + mCurrentversionName + " " + isNetworkAvailable());
        try {
            if (!mLatestVersionName.isEmpty() && !mLatestVersionName.isEmpty() && isNetworkAvailable()
                    && mCurrentversionName != null && mLatestVersionName != null && !mLatestVersionName.equals("null")) {
                if (mCurrentversionName.compareTo(mLatestVersionName) > 0 || mCurrentversionName.compareTo(mLatestVersionName) < 0) {
                    if (logBool) Log.i(TAG, log_version + "Inside true");

                    if (bool) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setTitle("Update Available!");
                                builder.setMessage("Update to v" + mLatestVersionName + " and enjoy.");
                                builder.setCancelable(false);
                                builder.setPositiveButton("Update.",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                final String appPackageName = getPackageName();

                                                try {
                                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.abi.whatstrack"));
                                                    startActivity(intent);
                                                } catch (android.content.ActivityNotFoundException anfe) {
                                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                                }
                                            }
                                        });
                                builder.setNegativeButton("No, Thanks.",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });
                                AlertDialog alertDialog = builder.create();

                                alertDialog.getWindow().setWindowAnimations(R.style.DialogAnim);
                                alertDialog.show();
                            }
                        });
                    } else {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                layout=findViewById(R.id.root);
//                                    showNotification_app(MainActivity.this, true, mLatestVersionName);
                                Snackbar.make(layout, "New Update Available: v" + mLatestVersionName, Snackbar.LENGTH_INDEFINITE)
                                        .setAction("Click Here!", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                final String appPackageName = getPackageName();

                                                try {
                                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.abi.whatstrack"));
                                                    startActivity(intent);
                                                } catch (android.content.ActivityNotFoundException anfe) {
                                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                                }
                                            }
                                        }).setActionTextColor(Color.parseColor("#FF51FFC5"))
                                        .show();
                            }
                        });
                    }
                }
            } else if (!isNetworkAvailable()) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        timerToast(Toast.makeText(MainActivity.this, "Update Check Failed! Connect to Internet.", Toast.LENGTH_LONG),1500);

                    }
                });
            }else{
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        timerToast(Toast.makeText(MainActivity.this, "Update Check Failed!", Toast.LENGTH_LONG),1500);

                    }
                });
            }
        } catch (RuntimeException e) {
            if (!isNetworkAvailable()) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        timerToast(Toast.makeText(MainActivity.this, "Update Check Failed! Connect to Internet.", Toast.LENGTH_LONG),1500);
                    }
                });
                if (logBool) {
                    Log.i(TAG, log_version + "CHECK FUNCTION FAIl");
                }
//                e.printStackTrace();
            }
        }

    }
    public void storage_check(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            final Intent intent = new Intent();
            String packageName = getBaseContext().getPackageName();

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Enable Storage Access !!");
            builder.setMessage(R.string.storage_checkPop);
            builder.setCancelable(false);
            builder.setPositiveButton("Enable",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE
                                    }, 1);
                        }
                    });
            builder.setNegativeButton("Later.",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.getWindow().setWindowAnimations(R.style.DialogAnim);

            alertDialog.show();
            if (logBool) Log.i(TAG, "" + "Storage Request");
        }
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
    public void setActionBarTitle(String title) {
//        getSupportActionBar().setTitle(title);
    }
}
