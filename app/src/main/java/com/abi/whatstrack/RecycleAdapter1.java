package com.abi.whatstrack;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class RecycleAdapter1 extends RecyclerView.Adapter<RecycleAdapter1.MyViewHolder> {

    private final ChatList[] lists;
    private Context context;
    private int height=0,width=0;
    private  MyViewHolder holder;
    private View.OnClickListener mClickListener;


    public View getView(ViewGroup parent) {
        View rowView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_items1, parent, false);
//        Animation animation = AnimationUtils
//                .loadAnimation(context, R.anim.fade_in);
//        v.startAnimation(animation);

        return rowView;

    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView contact,size;
        public ImageView imageView;

        public MyViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            contact = (TextView) view.findViewById(R.id.list_text);
            size = (TextView) view.findViewById(R.id.list_size);
            imageView = (ImageView) view.findViewById(R.id.list_icon);
        }

        @Override
        public void onClick(View v) {
            //
        }
    }


    public RecycleAdapter1(Context context, ChatList[] appLists) {
        this.context=context;
        this.lists=appLists;
        notifyItemRangeChanged(0,lists.length);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_items, parent, false);
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
            holder.contact.setText(lists[position].getContact());
            holder.size.setText(lists[position].getchatApp());
            holder.imageView.setImageBitmap(lists[position].appDrawable());
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
