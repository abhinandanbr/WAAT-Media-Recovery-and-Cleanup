package com.abi.whatstrack;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdSize;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;
import uk.co.samuelwall.materialtaptargetprompt.extras.backgrounds.FullscreenPromptBackground;
import uk.co.samuelwall.materialtaptargetprompt.extras.focals.RectanglePromptFocal;

import static com.abi.whatstrack.Util.TAG;

public class MainFrag extends Fragment implements FragActivityListener{

    //UI
    private View view;
    private boolean helpCheck;
    private boolean doubleBackToExitPressedOnce = false;
    Menu menu;
    int height, width;
    double screenInches = 0;
    FolderList[] usageList;
    Long totalSize=0L;

    SwipeRefreshLayout swipeRefresh;
    TextView text_welcome;
    private RecyclerView usageView;
    private RecycleAdapter mRecycleAdapterUsageView;
    private RecyclerView.LayoutManager recycleLayoutManagerUsageView;
    CardView card1,card2;
    ProgressBar progressBar;
//    NestedScrollView nestedScrollView;
    TextView btn_donate,btn_review;
    private Animation cardAnimation;
    private boolean flag_popUp=false;


    //SharedPrefer
    private static SharedPreferences mPrefs;
    private static SharedPreferences settings1;
    private static String mPrefs_key;


    //Rating
    private static final String pref_logbool="logBool";
    private static final String pref_ad_app_open_times="ad_app_open_times";
    private static final String pref_rate_app_open_times="rateappopentimes";
    private static final String pref_review="review";
    private static final String pref_helpCheck="helpCheck";
    private static final String pref_rate_prev_date="rateprevdate";

    //Logs
    private static final String log_version="VERSION:";
    private static final String log_count="Count:";


    //Misc
    private boolean showAds=true;
    private boolean logBool=true;
    private boolean reviewCheck;


    public static MainFrag newInstance(String parameter,boolean ads) {

        Bundle args = new Bundle();
        args.putString("parameter", parameter);
        args.putBoolean("ads", ads);
        MainFrag fragment = new MainFrag();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mPrefs_key=getArguments().getString("parameter");
            showAds=getArguments().getBoolean("ads");
            mPrefs = getActivity().getSharedPreferences(mPrefs_key, Context.MODE_PRIVATE);

            reviewCheck = mPrefs.getBoolean(pref_review, false);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_main, container, false);
        interfacesInit();



        interfaces(getActivity());
        interfaceAnimations(getActivity());
        return view;
    }

    public void loadUsage(final Context context){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
//                final CustomListAdapter appListAdapter = new CustomListAdapter(MainActivity.this, gpsList());
                mRecycleAdapterUsageView = new RecycleAdapter(getActivity(),folderList());
                text_welcome.setText(""+totalSize+" MB");

                /*if(appUsageWrap!=null && !showAds) {
//                    Log.i(TAG, "DABASE " + 1);
                    gpsUsage();
                    if(checkAccessEnabled(MainActivity.this)){
                        mRecycleAdapterUsageView = new RecycleAdapter(MainActivity.this, appUsageWrap);
                    }else{
                        appUsageWrapNull= new AppList[1];
                        appUsageWrapNull[0]=new AppList(getString(R.string.usage_mode),null,null);
                        mRecycleAdapterUsageView = new RecycleAdapter(MainActivity.this, appUsageWrapNull);
                    }

                    appUsageView.setClickable(true);
                }else{
                    appUsageWrapNull= new AppList[1];
                    appUsageWrapNull[0]=new AppList(getString(R.string.donate_mode),null,null);
                    getmRecycleAdapter = new RecycleAdapter(MainActivity.this, appUsageWrapNull);
//                    appUsageView.setAdapter(getmRecycleAdapter);
                }*/

                getActivity().runOnUiThread(
                        new Runnable() {
                            @Override
                            public void run() {
                                usageView.setAdapter(mRecycleAdapterUsageView);

                                /*if(appUsageWrap!=null && !showAds && checkAccessEnabled(MainActivity.this)) {
                                    appUsageView.setAdapter(getmRecycleAdapter);

                                    appUsageView.addOnItemTouchListener(new RecyclerTouchListener(MainActivity.this,
                                            appUsageView, new ClickListener() {
                                        @Override
                                        public void onClick(View view, final int position) {
                                            //Values are passing to activity & to fragment as well
                                            try {
                                                Intent intent = new Intent(android.provider.SettingsFrag.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                intent.setData(Uri.parse("package:" + appUsageWrap[position].appInformation().packageName));
                                                startActivity(intent);
                                            } catch ( Exception e ) {
                                                Intent intent = new Intent(android.provider.SettingsFrag.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                                                startActivity(intent);

                                            }
                                        }
                                    }));
                                    appUsageView.setClickable(true);
                                }
                                else
                                {
                                    appUsageView.setAdapter(getmRecycleAdapter);
                                    RecycleAdapter listAdapter = getmRecycleAdapter;

                                    appUsageView.addOnItemTouchListener(new RecyclerTouchListener(MainActivity.this,
                                            appUsageView, new ClickListener() {
                                        @Override
                                        public void onClick(View view, final int position) {
                                            //Values are passing to activity & to fragment as well

                                        }
                                    }));

                                    appUsageView.setClickable(true);
                                }

//                                appListView.setAdapter(appListAdapter);
//                                appListView.setVisibility(View.GONE);
                                list_title.setText(String.format(getResources().getString(R.string.listAppsTitle) + " %d Apps", appListWrap.length));
                                */
                                usageView.setClickable(true);
                                progressBar.setVisibility(View.GONE);
                                swipeRefresh.setRefreshing(false);

                                /*if(appListWrap.length<4) {
                                    RecycleAdapter listAdapter = mRecycleAdapter;
                                    if (listAdapter == null) {
                                        // pre-condition
                                        return;
                                    } else {

                                        int totalHeight = 0;
                                        for (int i = 0; i < listAdapter.getItemCount(); i++) {

                                            View listItem = listAdapter.getView(appRecView);
                                            listItem.measure(0, 0);
                                            totalHeight += listItem.getMeasuredHeight();
                                        }
                                        ViewGroup.LayoutParams params = appRecView.getLayoutParams();
                                        if(screenInches>7f) {
                                            params.height = totalHeight + (lisDrawables[0].getIntrinsicHeight() * (listAdapter.getItemCount() - 1)) + 80;
                                            appRecView.setLayoutParams(params);
                                        }else {
                                            params.height = totalHeight + ( (listAdapter.getItemCount() - 1)) + 30;
                                            appRecView.setLayoutParams(params);
                                        }
                                    }

                                }*/
                                usageView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(),
                                        usageView, new ClickListener() {
                                    @Override
                                    public void onClick(View view, final int position) {

                                        if(usageList[position].folderName.contains("Database") && !flag_popUp) {
                                            popDelete(getActivity(), true, usageList[position].folder());
                                        }else if(!flag_popUp){
                                            popDelete(getActivity(), false, usageList[position].folder());
                                        }

                                    }
                                }));

                                new CountDownTimer(800, 10) {

                                    public void onTick(long millisUntilFinished) {

//                                        nestedScrollView.scrollTo(0, (int) (millisUntilFinished));
                                    }

                                    public void onFinish() {
                                        helpCheck = mPrefs.getBoolean(pref_helpCheck, true);

                                        if(helpCheck) {
                                            interfaceHelp(getActivity(), 1);
                                            mPrefs.edit().putBoolean(pref_helpCheck,false).apply();
                                        }
                                    }

                                }.start();

                            }
                        });
            }
        });
    }
    public static interface ClickListener{
        public void onClick(View view,int position);
    }
    class RecyclerTouchListener implements RecyclerView.OnItemTouchListener{

        private ClickListener clicklistener;
        private GestureDetector gestureDetector;

        public RecyclerTouchListener(Context context, final RecyclerView recycleView, final ClickListener clicklistener){

            this.clicklistener=clicklistener;
            gestureDetector=new GestureDetector(context,new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child=rv.findChildViewUnder(e.getX(),e.getY());
            if(child!=null && clicklistener!=null && gestureDetector.onTouchEvent(e)){
                clicklistener.onClick(child,rv.getChildAdapterPosition(child));
            }

            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }
    public FolderList[] folderList(){

        if(!showAds || true) {
        }

        int length;
        totalSize=0L;
        File f = new File(Environment.getExternalStorageDirectory().getPath()+"/WhatsApp");
        if(f.exists()) {

            List<Drawable> listDraw = new ArrayList<>();
            List<File> fileFill = new ArrayList<>();
            List<String> fileNames = new ArrayList<>();
            List<Long> fileSizes = new ArrayList<>();
            long currentSize=0;

            length=0;
            File custom = new File(Environment.getExternalStorageDirectory().getPath()+"/WhatsApp/Media/WhatsApp Images");
            if(custom.exists()) {
                fileFill.add(custom);
                listDraw.add(getResources().getDrawable(R.drawable.ic_images));
                fileNames.add(custom.getName());
                currentSize=getFolderSize(custom);
                fileSizes.add(currentSize);
                totalSize+=currentSize;
                length++;
            }


            custom = new File(Environment.getExternalStorageDirectory().getPath()+"/WhatsApp/Media/WhatsApp Documents");
            if(custom.exists()) {
                fileFill.add(custom);
                listDraw.add(getResources().getDrawable(R.drawable.ic_docs));
                fileNames.add(custom.getName());
                currentSize=getFolderSize(custom);
                fileSizes.add(currentSize);
                totalSize+=currentSize;
                length++;
            }

            custom = new File(Environment.getExternalStorageDirectory().getPath()+"/WhatsApp/Media/WhatsApp Video");
            if(custom.exists()) {
                fileFill.add(custom);
                listDraw.add(getResources().getDrawable(R.drawable.ic_video));
                fileNames.add(custom.getName());
                currentSize=getFolderSize(custom);
                fileSizes.add(currentSize);
                totalSize+=currentSize;
                length++;
            }

            custom = new File(Environment.getExternalStorageDirectory().getPath()+"/WhatsApp/Media/WhatsApp Audio");
            if(custom.exists()) {
                fileFill.add(custom);
                listDraw.add(getResources().getDrawable(R.drawable.ic_audio));
                fileNames.add(custom.getName());
                currentSize=getFolderSize(custom);
                fileSizes.add(currentSize);
                totalSize+=currentSize;
                length++;
            }

            custom = new File(Environment.getExternalStorageDirectory().getPath()+"/WhatsApp/Media/WhatsApp Animated Gifs");
            if(custom.exists()) {
                fileFill.add(custom);
                listDraw.add(getResources().getDrawable(R.drawable.ic_gif));
                fileNames.add(custom.getName());
                currentSize=getFolderSize(custom);
                fileSizes.add(currentSize);
                totalSize+=currentSize;
                length++;
            }

            custom = new File(Environment.getExternalStorageDirectory().getPath()+"/WhatsApp/Media/WallPaper");
            if(custom.exists()) {
                fileFill.add(custom);
                listDraw.add(getResources().getDrawable(R.drawable.ic_wallpaper));
                fileNames.add(custom.getName());
                currentSize=getFolderSize(custom);
                fileSizes.add(currentSize);
                totalSize+=currentSize;
                length++;
            }

            custom = new File(Environment.getExternalStorageDirectory().getPath()+"/WhatsApp/Media/WhatsApp Stickers");
            if(custom.exists()) {
                fileFill.add(custom);
                listDraw.add(getResources().getDrawable(R.drawable.ic_badge));
                fileNames.add(custom.getName());
                currentSize=getFolderSize(custom);
                fileSizes.add(currentSize);
                totalSize+=currentSize;
                length++;
            }

            custom = new File(Environment.getExternalStorageDirectory().getPath()+"/WhatsApp/Media/WhatsApp Documents");
            if(custom.exists()) {
                fileFill.add(custom);
                listDraw.add(getResources().getDrawable(R.drawable.ic_docs));
                fileNames.add(custom.getName());
                currentSize=getFolderSize(custom);
                fileSizes.add(currentSize);
                totalSize+=currentSize;
                length++;
            }

            custom = new File(Environment.getExternalStorageDirectory().getPath()+"/WhatsApp/Media/WhatsApp Profile Photos");
            if(custom.exists()) {
                fileFill.add(custom);
                listDraw.add(getResources().getDrawable(R.drawable.ic_profile));
                fileNames.add(custom.getName());
                currentSize=getFolderSize(custom);
                fileSizes.add(currentSize);
                totalSize+=currentSize;
                length++;
            }
            custom = new File(Environment.getExternalStorageDirectory().getPath()+"/WhatsApp/Media/WhatsApp Voice Notes");
            if(custom.exists()) {
                fileFill.add(custom);
                listDraw.add(getResources().getDrawable(R.drawable.ic_voice));
                fileNames.add(custom.getName());
                currentSize=getFolderSize(custom);
                fileSizes.add(currentSize);
                totalSize+=currentSize;
                length++;
            }

            custom = new File(Environment.getExternalStorageDirectory().getPath()+"/WhatsApp/Databases");
            if(custom.exists()) {
                fileFill.add(custom);
                listDraw.add(getResources().getDrawable(R.drawable.ic_database));
                fileNames.add(custom.getName());
                currentSize=getFolderSize(custom);
                fileSizes.add(currentSize);
                totalSize+=currentSize;
                length++;
            }

//            custom = new File(Environment.getExternalStorageDirectory().getPath()+"/WhatsApp/Media/WhatsApp Voice Notes");
//            File[] files = custom.listFiles();
//            for (File inFile : files) {
//                if (inFile.isDirectory() && inFile.getName().contains(".")) {
//                    fileFill.add(custom);
//                    listDraw.add(getResources().getDrawable(R.drawable.ic_database));
//                    fileNames.add(custom.getName());
//                    length++;
//                }
//            }

            usageList= new FolderList[length];
            for(int i=0;i<length;i++) {
                usageList[i] = new FolderList(fileFill.get(i),
                        fileNames.get(i), listDraw.get(i),fileSizes.get(i));
            }

//        Arrays.sort(usageList, new Comparator<FolderList>() {
//            @Override
//            public int compare(FolderList o1, FolderList o2) {
//                return o1.folderName.compareTo(o2.folderName);
//            }
//        });

            return usageList;
        }else{
            f.mkdir();
            usageList=new FolderList[1];
            usageList[0]=new FolderList(f,"No Whatsapp Folder",getResources().getDrawable(android.support.v4.R.drawable.notification_action_background),0L);
            return usageList;
        }
    }
    public static long getFolderSize(File dir) {
        long size = 0;
        try {
            for (File file : dir.listFiles()) {
                if (file.isFile()) {
                    // System.out.println(file.getName() + " " + file.length());
                    size += file.length();
                } else if (dir.getName().contains("Profile")) {
                    //size += getFolderSize(file);
                } else {
                    size += getFolderSize(file);
                }
            }
        }catch (Exception e){
            return 0;
        }
        return size/(1024*1024);
    }
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }


    public void interfaceHelp(Context context, int target){

        switch (target) {

            case 1:
                new MaterialTapTargetPrompt.Builder(getActivity())
                        .setPromptBackground(new FullscreenPromptBackground())
                        .setPromptFocal(new RectanglePromptFocal())
                        .setFocalColour(Color.TRANSPARENT)
                        .setBackgroundColour(getResources().getColor(R.color.main_page))
                        .setTarget(R.id.home_card2)
                        .setPrimaryText("Usage Info")
                        .setSecondaryText(getResources().getString(R.string.help_main1))
                        .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener() {
                            @Override
                            public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state) {
                                if (state == MaterialTapTargetPrompt.STATE_DISMISSED || state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED) {
                                    // User has pressed the prompt target
//                                nestedScrollView.smoothScrollTo(appRecView.getScrollX()+nestedScrollView.getBottom(),appRecView.getScrollY());

                                    new CountDownTimer(800, 10) {
                                        public void onTick(long millisUntilFinished) {
//                                            nestedScrollView.smoothScrollTo(0, usageView.getTop()-(int)millisUntilFinished+20);
                                        }
                                        public void onFinish() {
                                            interfaceHelp(getActivity(),4);
                                        }
                                    }.start();
                                }
                            }
                        })
                        .show();
                break;

            case 2:
//                new MaterialTapTargetPrompt.Builder(MainActivity.this)
//                        .setPromptBackground(new FullscreenPromptBackground())
//                        .setPromptFocal(new RectanglePromptFocal())
//                        .setFocalColour(Color.TRANSPARENT)
//                        .setBackgroundColour(getResources().getColor(R.color.main_page))
//                        .setTarget(R.id.app_recUsage)
//                        .setPrimaryText("Recent GPS Usage info")
//                        .setSecondaryText(getResources().getString(R.string.help_main2))
//                        .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener() {
//                            @Override
//                            public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state) {
//                                if (state == MaterialTapTargetPrompt.STATE_DISMISSED || state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED) {
//                                    // User has pressed the prompt target
//                                    new CountDownTimer(800, 10) {
//                                        public void onTick(long millisUntilFinished) {
//                                            nestedScrollView.smoothScrollTo(0, card2.getBottom()-(int)millisUntilFinished);
//                                        }
//                                        public void onFinish() {
//                                            interfaceHelp(MainActivity.this,3);
//                                        }
//                                    }.start();
//                                }
//                            }
//                        })
//                        .show();
                break;

            case 4:
                new MaterialTapTargetPrompt.Builder(getActivity())
                        .setTarget(R.id.home_card1)
                        .setPromptBackground(new FullscreenPromptBackground())
                        .setBackgroundColour(getResources().getColor(R.color.main_page))
                        .setPromptFocal(new RectanglePromptFocal())
                        .setFocalColour(Color.TRANSPARENT)
                        .setPrimaryText("Rate and Donate")
                        .setSecondaryText(getResources().getString(R.string.help_main4))
                        .show();

            case 5:
                new MaterialTapTargetPrompt.Builder(this)
                        .setTarget(R.id.action_settings)
                        .setBackgroundColour(getResources().getColor(R.color.main_page))
                        .setPrimaryText("Tap Here for SettingsFrag")
                        .setIcon(R.drawable.ic_settings)
                        .show();
                break;
        }

    }
    public void interfacesInit(){
        text_welcome = (TextView) view.findViewById(R.id.text_welcome);

        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);
        card1=(CardView) view.findViewById(R.id.home_card1);
        card2=(CardView) view.findViewById(R.id.home_card2);

        btn_donate=(TextView)view.findViewById(R.id.text_donate);
        btn_review=(TextView)view.findViewById(R.id.text_rate);

//        nestedScrollView=(NestedScrollView)view.findViewById(R.id.nestScroll);

        usageView=(RecyclerView) view.findViewById(R.id.usageView);
        progressBar=(ProgressBar) view.findViewById(R.id.progressBar);

    }
    public void interfaces(final Context context) {

        TypedValue outValue = new TypedValue();
        getResources().getValue(R.fraction.screen_size, outValue, true);
        float screenLimit = outValue.getFloat();

        if (logBool) Log.i(TAG, "SizeInitial:" + "" + screenInches);
        screenInches = getScreenSizeInches(getActivity());
        if (logBool) Log.i(TAG, "SizeFinal:" + "" + screenInches);

        if (screenInches < screenLimit) {
        }

        swipeRefresh.setRefreshing(true);
        swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.splash_color),
                Color.RED, Color.GREEN,getResources().getColor(R.color.about_color));
        if(showAds) {
            swipeRefresh.setPadding(0, 0, 0, AdSize.SMART_BANNER.getHeightInPixels(getActivity()));
        }
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.main_color), PorterDuff.Mode.SRC_IN);
        progressBar.setAlpha(0.7f);

        recycleLayoutManagerUsageView = new LinearLayoutManager(getActivity());
        usageView.setLayoutManager(recycleLayoutManagerUsageView);
        usageView.setClickable(false);
        loadUsage(getActivity());

//        nestedScrollView.setSmoothScrollingEnabled(true);

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                loadUsage(getActivity());

                new Handler().postDelayed(new Runnable() {
                    @Override public void run() {
                        swipeRefresh.setRefreshing(false);
                    }

                }, 3000);
            }
        });


        btn_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPrefs.edit().putBoolean(pref_review,true).apply();
                final String appPackageName = getActivity().getPackageName();
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.abi.whatstrack"));
                    startActivity(intent);
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
                getActivity().overridePendingTransition(R.anim.layout_frombottom,R.anim.layout_tobottom);
            }
        });

        if(!showAds) {
            btn_donate.setEnabled(false);
            btn_donate.setVisibility(View.GONE);
        }
        btn_donate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).billingAccount(getActivity());
            }
        });
    }
    public void interfaceAnimations(Context context){

        cardAnimation= AnimationUtils.loadAnimation(getActivity(),R.anim.about_anim);
        card1.setAnimation(cardAnimation);
        card2.setAnimation(cardAnimation);
    }
    public void popDelete(Context context,boolean database,final File file){
        flag_popUp=true;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose Action");
        builder.setMessage("Do you want to delete all files?");
        builder.setCancelable(false);
        if(!database) {
            builder.setPositiveButton("Delete All",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            deleteDir(file);
                            dialog.cancel();
                        }
                    });
            builder.setNegativeButton("No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
        }else {
            builder.setPositiveButton("Delete All",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            deleteDir(file);
                            dialog.cancel();
                        }
                    });
            builder.setNegativeButton("Keep only latest",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            if (file.isDirectory()) {
                                String[] children = file.list();
                                File temp1,temp2;
                                temp1 = new File(file, children[0]);
                                for (int i=1; i<children.length; i++) {
                                    temp2 = new File(file, children[i]);
                                    if(temp1.lastModified()<temp2.lastModified()){
                                        temp1.delete();
                                        temp1=new File(file, children[i]);
                                    }
                                    else
                                    {
                                        temp2.delete();
                                    }
                                }
                            }

                            dialog.cancel();
                        }
                    });
        }
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                flag_popUp=false;
                loadUsage(getActivity());
            }
        });
        AlertDialog alertDialog = builder.create();

        alertDialog.getWindow().setWindowAnimations(R.style.DialogAnim);
        alertDialog.show();
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
}
