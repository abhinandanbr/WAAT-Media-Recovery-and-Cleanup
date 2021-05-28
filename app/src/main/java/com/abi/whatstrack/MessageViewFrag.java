package com.abi.whatstrack;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdSize;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.abi.whatstrack.Util.TAG;

public class MessageViewFrag extends Fragment implements IOBackPressed {

    View view;

    //UI
    private boolean helpCheck;
    private boolean doubleBackToExitPressedOnce = false;
    Menu menu;
    int height, width;
    double screenInches = 0;
    ChatsViewList[] chatsViewLists;
    PopupWindow mpopup;

    SwipeRefreshLayout swipeRefresh;
    private RecyclerView usageView;
    private RecycleAdapterChats mRecycleAdapterUsageView;
    private RecyclerView.LayoutManager recycleLayoutManagerUsageView;
    ProgressBar progressBar;
    private Animation cardAnimation;
    private boolean flag_popUp=false;
    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    private static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";


    //SharedPrefer
    private static SharedPreferences mPrefs;
    private static SharedPreferences settings1;
    private static String mPrefs_key;

    //Misc
    private boolean showAds=true;
    private boolean logBool=true;
    private boolean reviewCheck;
    private String contactSelect=null;

    public static MessageViewFrag newInstance(String parameter,boolean ads,String contactSelect) {

        Bundle args = new Bundle();
        args.putString("parameter", parameter);
        args.putBoolean("ads", ads);
        args.putString("contactSelect",contactSelect);
        MessageViewFrag fragment = new MessageViewFrag();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mPrefs_key=getArguments().getString("parameter");
            showAds=getArguments().getBoolean("ads");
            contactSelect=getArguments().getString("contactSelect");
            mPrefs = getActivity().getSharedPreferences(mPrefs_key, Context.MODE_PRIVATE);

//            reviewCheck = mPrefs.getBoolean(pref_review, false);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.chat_message_view, container, false);

        interfacesInit();
        interfaces(getActivity());
        interfaceAnimations(getActivity());

        return view;
    }


    //UI
    public void interfacesInit(){

        if(contactSelect!=null)
            ((MainActivity) getActivity()).setActionBarTitle(contactSelect);

        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);

        usageView=(RecyclerView) view.findViewById(R.id.usageView);
        progressBar=(ProgressBar) view.findViewById(R.id.progressBar);

    }
    public void interfaces(final Context context) {

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

    }
    public void interfaceAnimations(Context context){
        cardAnimation= AnimationUtils.loadAnimation(getActivity(),R.anim.about_anim);

    }


    //Interfaces
    public void loadUsage(final Context context){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                mRecycleAdapterUsageView = new RecycleAdapterChats(getActivity(),chatLists());

                getActivity().runOnUiThread(
                        new Runnable() {
                            @Override
                            public void run() {
                                usageView.setAdapter(mRecycleAdapterUsageView);
                                usageView.setClickable(true);
                                progressBar.setVisibility(View.GONE);
                                swipeRefresh.setRefreshing(false);

                            }
                        });
            }
        });
    }
    public ChatsViewList[] chatLists(){

        int length;
        File f = new File(Environment.getExternalStorageDirectory().getPath()+"/WhatsApp");
        if(f.exists()) {

            List<String> listMessage = new ArrayList<>();
            List<String> listContact = new ArrayList<>();
            List<String> listTime = new ArrayList<>();
            List<Bitmap> listIcon = new ArrayList<>();

            length=0;

            try {
                Table1Db table1Db = new Table1Db(getContext());
                SQLiteDatabase db = table1Db.getReadableDatabase();
                Long timeLong=0L;

                String sortOrder =
                        Table1.Table1Entry._ID+ " DESC";

                Cursor cursor = db.query(Table1.Table1Entry.TABLE_NAME, null, null,
                        null, null, null, sortOrder);

                while (cursor.moveToNext()) {
                    long itemId = cursor.getLong(cursor.getColumnIndexOrThrow(Table1.Table1Entry._ID));
                    String chats = cursor.getString(cursor.getColumnIndexOrThrow(Table1.Table1Entry.COLUMN_CHATS));
                    String time = cursor.getString(cursor.getColumnIndexOrThrow(Table1.Table1Entry.COLUMN_TIME));
                    String contactNm = cursor.getString(cursor.getColumnIndexOrThrow(Table1.Table1Entry.COLUMN_CONTACT));
//                    byte[] drawable = cursor.getBlob(cursor.getColumnIndexOrThrow(Table1.Table1Entry.COLUMN_IMAGE));
//                    Bitmap  bitmap = BitmapFactory.decodeByteArray(drawable, 0, drawable.length);
//                    Log.i(TAG, "DABASE " + item+" "+itemId);
                    if(!listContact.contains(contactNm) && contactNm.equals(contactSelect)) {
                        listMessage.add(chats);
                        timeLong=Long.parseLong(time);
                        final Calendar cal = Calendar.getInstance();
                        cal.setTimeInMillis(timeLong);
                        if(DateFormat.is24HourFormat(getContext())){
                            time = new SimpleDateFormat("dd/MM/YY HH:mm").format(cal.getTime());
                        }else {
                            time = new SimpleDateFormat("dd/MM/YY hh:mm a").format(cal.getTime());
                        }
                        listTime.add(time);
                        length++;
                    }
                }
                cursor.close();
                db.close();

            } catch (Exception e) {
                Log.i(TAG,"tableChatView frag");
            }

            chatsViewLists= new ChatsViewList[length];
            for(int i=0;i<length;i++) {
                chatsViewLists[i] = new ChatsViewList(listMessage.get(i),
                        listTime.get(i),null);
            }

            return chatsViewLists;
        }else{
            chatsViewLists=new ChatsViewList[1];
            chatsViewLists[0]=new ChatsViewList("No Chats","NA",null);
            return chatsViewLists;
        }
    }
    @Override
    public boolean onBackPressed() {
        getFragmentManager().popBackStack("NotiFrag",FragmentManager.POP_BACK_STACK_INCLUSIVE);

        return true;
    }
}
