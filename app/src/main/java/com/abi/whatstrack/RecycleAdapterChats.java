package com.abi.whatstrack;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class RecycleAdapterChats extends RecyclerView.Adapter<RecycleAdapterChats.MyViewHolder> {

    private final ChatsViewList[] lists;
    private  MyViewHolder holder;
    private Context context;
    private View.OnClickListener mClickListener;


    public View getView(ViewGroup parent) {
        View rowView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_items_chat_view, parent, false);
//        Animation animation = AnimationUtils
//                .loadAnimation(context, R.anim.fade_in);
//        v.startAnimation(animation);

        return rowView;

    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView message,time;

        public MyViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            message = (TextView) view.findViewById(R.id.list_text);
            time = (TextView) view.findViewById(R.id.list_time);
        }

        @Override
        public void onClick(View v) {
            //
        }
    }


    public RecycleAdapterChats(Context context, ChatsViewList[] appLists) {
        this.context=context;
        this.lists=appLists;
        notifyItemRangeChanged(0,lists.length);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_items_chat_view, parent, false);
        itemView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int width = itemView.getMeasuredWidth();
        int height = itemView.getMeasuredHeight();
        holder = new MyViewHolder(itemView);
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mClickListener.onClick(v);
//            }
//        });
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
//        AppList appList = lists.get(position);
        if(lists[position]!=null) {
            holder.time.setText(lists[position].gettime());
            holder.message.setText(lists[position].getchatMessage());
        }
//        holder.setIsRecyclable(true);
    }

    @Override
    public int getItemCount() {
        return lists.length;
    }

    public void setClickListener(View.OnClickListener callback) {
        mClickListener = callback;
    }
}
