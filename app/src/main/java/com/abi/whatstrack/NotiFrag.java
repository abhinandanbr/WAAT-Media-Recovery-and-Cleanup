package com.abi.whatstrack;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdSize;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.abi.whatstrack.Util.TAG;
import static com.abi.whatstrack.Util.broadcast_String;

public class NotiFrag extends Fragment implements FragActivityListener {

    //UI
    private View view;
    private boolean helpCheck;
    private boolean doubleBackToExitPressedOnce = false;
    Menu menu;
    int height, width;
    double screenInches = 0;
    ChatList[] chatlist;
    PopupWindow mpopup;

    SwipeRefreshLayout swipeRefresh;
    TextView text_welcome;
    private RecyclerView usageView;
    private RecycleAdapter1 mRecycleAdapterUsageView;
    private RecyclerView.LayoutManager recycleLayoutManagerUsageView;
    CardView card1,card2;
    ProgressBar progressBar;
    TextView btn_donate,btn_review;
    private Animation cardAnimation;
    private boolean flag_popUp=false;
    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    private static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";


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


    ImageChangeBroadcastReceiver imageChangeBroadcastReceiver;

    public static NotiFrag newInstance(String parameter,boolean ads) {

        Bundle args = new Bundle();
        args.putString("parameter", parameter);
        args.putBoolean("ads", ads);
        NotiFrag fragment = new NotiFrag();
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

        view = inflater.inflate(R.layout.fragment_noti, container, false);
        interfacesInit();

        interfaces(getActivity());
        interfaceAnimations(getActivity());

//        imageChangeBroadcastReceiver = new ImageChangeBroadcastReceiver();
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(broadcast_String);
//        getActivity().registerReceiver(imageChangeBroadcastReceiver,intentFilter);
        return view;
    }

    @Override
    public void onResume(){
        super.onResume();

        if(!isNotificationServiceEnabled()){
            text_welcome.setText("Notification Service Disabled!\nClick to Enable.");
            text_welcome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buildNotificationServiceAlertDialog();
                }
            });
        }else {
            text_welcome.setText("Click to Clear All Messages!");
            text_welcome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().deleteDatabase(Table1Db.DATABASE_NAME);
                    loadUsage(getActivity());
                }
            });
        }

    }
    @Override
    public void onDestroy(){
        super.onDestroy();
//        getActivity().unregisterReceiver(imageChangeBroadcastReceiver);
    }

    //UI
    public void interfacesInit(){

        ((MainActivity) getActivity()).setActionBarTitle(""+getString(R.string.app_name));

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

        if(!isNotificationServiceEnabled()){
            text_welcome.setText("Notification Service Disabled!\nClick to Enable.");
            text_welcome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buildNotificationServiceAlertDialog();
                }
            });
        }else {
            text_welcome.setText("Click to Clear All Messages!");
            text_welcome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.deleteDatabase(Table1Db.DATABASE_NAME);
                    loadUsage(getActivity());
                }
            });
        }

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
    private boolean isNotificationServiceEnabled(){
        String notificationListenerString = Settings.Secure.getString(getContext().getContentResolver(),ENABLED_NOTIFICATION_LISTENERS);
        //Check notifications access permission
        if (notificationListenerString == null || !notificationListenerString.contains(getActivity().getPackageName()))
        {
            return false;
            //The notification access has not acquired yet!
        }else{
            return true;
            //Your application has access to the notifications
        }
    }
    private void buildNotificationServiceAlertDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle("Enable Notification Listener Service");
        alertDialogBuilder.setMessage(R.string.notiMessage);
        alertDialogBuilder.setPositiveButton("Enable",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS));
                    }
                });
        alertDialogBuilder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // If you choose to not enable the notification listener
                        // the app. will not work as expected
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.getWindow().setWindowAnimations(R.style.DialogAnim);
        alertDialog.show();
    }


    public void loadUsage(final Context context){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
//                final CustomListAdapter appListAdapter = new CustomListAdapter(MainActivity.this, gpsList());
                mRecycleAdapterUsageView = new RecycleAdapter1(getActivity(),chatLists());
//                text_welcome.setText(""+totalSize+" MB");

                getActivity().runOnUiThread(
                        new Runnable() {
                            @Override
                            public void run() {
                                usageView.setAdapter(mRecycleAdapterUsageView);
                                usageView.setClickable(true);
                                progressBar.setVisibility(View.GONE);
                                swipeRefresh.setRefreshing(false);

                                usageView.addOnItemTouchListener(new NotiFrag.RecyclerTouchListener(getActivity(),
                                        usageView, new NotiFrag.ClickListener() {
                                    @Override
                                    public void onClick(View view, final int position) {

                                            popDelete(getActivity(),chatlist[position].getContact());
                                    }

                                    @Override
                                    public void onLongClick(View view, int position) {
//                                        Toast.makeText(getActivity(), "Long press on position :"+position,
//                                                Toast.LENGTH_SHORT).show();
                                    }
                                }));

                            }
                        });
            }
        });
    }
    public ChatList[] chatLists(){

        if(!showAds) {
        }

        int length;
        File f = new File(Environment.getExternalStorageDirectory().getPath()+"/WhatsApp");
        if(f.exists()) {

            List<String> listApp = new ArrayList<>();
            List<String> listContact = new ArrayList<>();
            List<Bitmap> listIcon = new ArrayList<>();
            long currentSize=0;

            length=0;

            try {
                Table1Db table1Db = new Table1Db(getContext());
                SQLiteDatabase db = table1Db.getReadableDatabase();

                String sortOrder =
                        Table1.Table1Entry._ID+ " DESC";

                Cursor cursor = db.query(Table1.Table1Entry.TABLE_NAME, null, null,
                        null, null, null, sortOrder);

                while (cursor.moveToNext()) {
                    long itemId = cursor.getLong(cursor.getColumnIndexOrThrow(Table1.Table1Entry._ID));
                    String appName = cursor.getString(cursor.getColumnIndexOrThrow(Table1.Table1Entry.COLUMN_NAME_TITLE));
                    String contactNm = cursor.getString(cursor.getColumnIndexOrThrow(Table1.Table1Entry.COLUMN_CONTACT));
                    byte[] drawable = cursor.getBlob(cursor.getColumnIndexOrThrow(Table1.Table1Entry.COLUMN_IMAGE));
                    Bitmap  bitmap = BitmapFactory.decodeByteArray(drawable, 0, drawable.length);
//                    Log.i(TAG, "DABASE " + item+" "+itemId);
                    if(!listContact.contains(contactNm)) {
                        final String packageName = appName;
                        PackageManager packageManager= getContext().getPackageManager();
                        appName = (String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA));
                        listApp.add(appName);
                        listContact.add(contactNm);
                        listIcon.add(bitmap);
                        length++;
                    }
                }
                cursor.close();
                db.close();

            } catch (Exception e) {
                Log.i(TAG,"table1 frag");
            }

            chatlist= new ChatList[length];
            for(int i=0;i<length;i++) {
                chatlist[i] = new ChatList(listApp.get(i),
                        listContact.get(i),listIcon.get(i));
            }

            return chatlist;
        }else{
            chatlist=new ChatList[1];
            chatlist[0]=new ChatList("No Chats","NA",null);
            return chatlist;
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
    class RecyclerTouchListener implements RecyclerView.OnItemTouchListener{

        private NotiFrag.ClickListener clicklistener;
        private GestureDetector gestureDetector;

        public RecyclerTouchListener(Context context, final RecyclerView recycleView, final ClickListener clicklistener){

            this.clicklistener=clicklistener;
            gestureDetector=new GestureDetector(context, new GestureDetector.OnGestureListener() {
                @Override
                public boolean onDown(MotionEvent e) {
                    return false;
                }

                @Override
                public void onShowPress(MotionEvent e) {

                }

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                    return false;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child=recycleView.findChildViewUnder(e.getX(),e.getY());
                    if(child!=null && clicklistener!=null){
                        clicklistener.onLongClick(child,recycleView.getChildAdapterPosition(child));
                    }
                }

                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                    return false;
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
    public static interface ClickListener{
        public void onClick(View view,int position);
        public void onLongClick(View view,int position);
    }


    //Interfaces
    public class ImageChangeBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int receivedNotificationCode = intent.getIntExtra("Notification Code",-1);
            Toast.makeText(getContext(),"Notify",Toast.LENGTH_SHORT).show();
        }
    }
    public void popDelete(Context context,String contact){
        Fragment fragment = MessageViewFrag.newInstance(mPrefs_key,showAds,contact);
        FragmentTransaction trans = getActivity().getSupportFragmentManager()
                .beginTransaction();
        trans.replace(R.id.content_frame,fragment);
        trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        trans.addToBackStack("NotiFrag");

        trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        trans.commit();


        /*View popUpView = getLayoutInflater().inflate(R.layout.chat_viewpager,
                null); // inflating popup layout
        if(Build.VERSION.SDK_INT< Build.VERSION_CODES.M)
        {
            mpopup = new PopupWindow(popUpView, ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT, false); // Creation of popup
        }else {
            mpopup = new PopupWindow(popUpView, ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT, true); // Creation of popup
        }
        mpopup.setAnimationStyle(android.R.style.Animation_Dialog);
        mpopup.showAtLocation(popUpView, Gravity.TOP, 0, 0);
        mpopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {


            }
        });
        mpopup.setOutsideTouchable(false);
        // Clear the default translucent background
        mpopup.setBackgroundDrawable(getResources().getDrawable(R.drawable.background_white));

        //UI

        ViewPager viewPager = popUpView.findViewById(R.id.pager);

        Adapter adapter = new Adapter(getChildFragmentManager());
        adapter.addFragment(MessageViewFrag.newInstance(mPrefs_key,showAds), "");

        viewPager.setAdapter(adapter);

        TabLayout tabs = (TabLayout) popUpView.findViewById(R.id.tab_layout);
        tabs.setupWithViewPager(viewPager);
        tabs.setTabGravity(TabLayout.GRAVITY_FILL);
        tabs.setTabMode(TabLayout.MODE_FIXED);
        tabs.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        tabs.getTabAt(0).setIcon(getResources().getDrawable(R.drawable.ic_chats));*/
    }
    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();
        private final List<Drawable> mFragmentDrawableList= new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
