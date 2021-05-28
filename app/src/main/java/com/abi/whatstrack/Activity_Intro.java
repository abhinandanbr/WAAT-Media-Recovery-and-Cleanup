package com.abi.whatstrack;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.Calendar;

/**
 * Created by abhinandan on 11/2/18.
 */

public class Activity_Intro extends AppCompatActivity {

    private static SharedPreferences mPrefs;
    private static final String ANDROID_PRE_BASE = "pre_base";
    private static final String pref_intro="intro";

    boolean intro;
    long ratePrevDate;
    String versionString="";

    TextView version;
    Button btn_intro2,btn_intro5,btn_intro6;
    private int[] layouts;
    private ImageButton btnBack, btnNext;

    Animation fade_in,fade_out;

    boolean misc_perm=true;
    boolean battery_perm=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.view_intro);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        mPrefs = getSharedPreferences(FirebaseRemoteConfig.getInstance().getString(ANDROID_PRE_BASE), Context.MODE_PRIVATE);
        ratePrevDate= Calendar.getInstance().getTimeInMillis();
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putLong("rateprevdate",ratePrevDate);
        editor.apply();

        fade_in=AnimationUtils.loadAnimation(this,R.anim.fade_in);
        fade_out=AnimationUtils.loadAnimation(this,R.anim.fade_out);

        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

//        setContentView(R.layout.intro_main);

        changeStatusBarColor();

        Fragment fragment ;
        int save=0;
        fragment = new PagerFragIntro();

        Bundle args = new Bundle();
//        args.putInt(Home.ARG_TITLE_NUMBER, position);
        fragment.setArguments(args);

        FragmentTransaction trans = getSupportFragmentManager()
                .beginTransaction();
        trans.replace(R.id.content_frame,fragment);
        trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        trans.addToBackStack(null);

        trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        trans.commit();
    }

//    public void interfaces(final Context context){
//
//        changeStatusBarColor();
//
//        myViewPagerAdapter = new MyViewPagerAdapter();
//        viewPager.setAdapter(myViewPagerAdapter);
//        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
//        btnBack.setVisibility(View.GONE);
//
//        viewPager.setPageTransformer(true, new IntroAnimation());
//
//        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
//        tabLayout.setupWithViewPager(viewPager, true);
//
//
//        btnBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int current = getItem(-1);
//
//                viewPager.setCurrentItem(current);
//            }
//        });
//
//        btnNext.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                int current = getItem(+1);
//                if (current < layouts.length) {
//                    // move to next screen
//
//                        viewPager.setCurrentItem(current);
//                } else {
//                    SharedPreferences.Editor editor = mPrefs.edit();
//                    editor.putBoolean(pref_intro, false);
//                    editor.apply();
//                    startActivity(new Intent(Activity_Intro.this,MainActivity.class));
//                    overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
//                    finish();
//                }
//            }
//        });
//    }
//
//    private int getItem(int i) {
//        return viewPager.getCurrentItem() + i;
//    }
//
//    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {
//
//        @Override
//        public void onPageSelected(int position) {
////            addBottomDots(position);
//
//            if (position == 0) {
//                btnBack.setVisibility(View.GONE);
//            } else {
//                btnBack.setVisibility(View.VISIBLE);
//            }
//
//            if (position == layouts.length-1) {
//                try {
//                    PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
//                    versionString = getString(R.string.about_version)+pInfo.versionName;
//                } catch (PackageManager.NameNotFoundException e) {
////            e.printStackTrace();
//                }
//                version=(TextView )findViewById(R.id.intro_appVersion);
//                version.setText(versionString);
//                btnNext.setImageResource(R.drawable.arrow_done);
//            }
//            else {
//                btnNext.setImageResource(R.drawable.arrow_next);
//            }
//
//
//            if(Build.VERSION.SDK_INT>=23){
//                if(position == (layouts.length-3)) {
//                    btn_intro5 = (Button) findViewById(R.id.btn_intro5);
//                    btn_intro5.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
////                        Intent intent;
////                        intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
////                        startActivity(intent);
////                        overridePendingTransition(R.anim.layout_frombottom,R.anim.layout_tobottom);
////                        misc_perm=false;
//                            Intent intent;
////                    intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
//                                intent = new Intent(
//                                        Settings
//                                                .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
//                                startActivity(intent);
//                        }
//                    });
//                }
//
//            }
//
//            if(Build.VERSION.SDK_INT>=21){
//                if(position == (layouts.length-2)) {
//                    btn_intro6 = (Button) findViewById(R.id.btn_intro6);
//                    btn_intro6.setOnClickListener(new View.OnClickListener() {
//                        Intent intent;
//                        @Override
//                        public void onClick(View view) {
//                                intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
//                                startActivity(intent);
//                        }
//                    });
//                }
//
//            }
//
//            if(position == 1 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//
//                    btn_intro2 = (Button) findViewById(R.id.btn_intro2);
//
//                    btn_intro2.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//
//                                Intent intent = new Intent();
//                                String packageName = getBaseContext().getPackageName();
//                                PowerManager pm = (PowerManager) getBaseContext().getSystemService(Context.POWER_SERVICE);
//                                try {
//                                    if (pm.isIgnoringBatteryOptimizations(packageName)) {
//                                        intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
//                                        startActivity(intent);
//                                        overridePendingTransition(R.anim.layout_frombottom, R.anim.layout_tobottom);
//                                    } else {
//                                        intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
//                                        startActivity(intent);
//                                        overridePendingTransition(R.anim.layout_frombottom, R.anim.layout_tobottom);
//                                    }
//                                } catch (NullPointerException e) {
//                                }
//                            }
//                        }
//                    });
//                }
//                battery_perm=false;
//            }
//        }
//
//        @Override
//        public void onPageScrolled(int arg0, float arg1, int arg2) {
//
//        }
//
//        @Override
//        public void onPageScrollStateChanged(int arg0) {
//
//        }
//    };

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

//    public class MyViewPagerAdapter extends PagerAdapter {
//        private LayoutInflater layoutInflater;
//
//        public MyViewPagerAdapter() {
//        }
//
//        @Override
//        public Object instantiateItem(ViewGroup container, int position) {
//            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//            View view = layoutInflater.inflate(layouts[position], container, false);
//            container.addView(view);
//
//            return view;
//        }
//
//        @Override
//        public int getCount() {
//            return layouts.length;
//        }
//
//        @Override
//        public boolean isViewFromObject(View view, Object obj) {
//            return view == obj;
//        }
//
//
//        @Override
//        public void destroyItem(ViewGroup container, int position, Object object) {
//            View view = (View) object;
//            container.removeView(view);
//        }
//
//    }

    @Override
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(), "Complete Intro !", Toast.LENGTH_SHORT).show();
    }


}
