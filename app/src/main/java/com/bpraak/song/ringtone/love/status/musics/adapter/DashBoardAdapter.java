package com.bpraak.song.ringtone.love.status.musics.adapter;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bpraak.song.ringtone.love.status.musics.R;
import com.bpraak.song.ringtone.love.status.musics.activity.MainActivity;

import java.util.ArrayList;


public class DashBoardAdapter extends RecyclerView.Adapter<DashBoardAdapter.MyViewHolder> {

    private ArrayList<String> dataSet;
    ArrayList<Integer> menuImage;
    Context mContext;

    public DashBoardAdapter(Context pContext, ArrayList<String> strArray, ArrayList<Integer> integers) {
        dataSet = strArray;
        menuImage = integers;
        mContext = pContext;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final ImageView iv_app;
        TextView tv_menu;
        ImageView iv_menu;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.tv_menu = (TextView) itemView.findViewById(R.id.tv_menu);
            this.iv_menu = (ImageView) itemView.findViewById(R.id.iv_menu);
            this.iv_app = (ImageView) itemView.findViewById(R.id.iv_app);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_item, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.tv_menu.setText(dataSet.get(position));

//        if (position==3){
//            holder.iv_app.setImageResource(menuImage.get(position));
//            holder.iv_app.setVisibility(View.VISIBLE);
//            holder.iv_menu.setVisibility(View.GONE);
//        }else {
            holder.iv_menu.setImageResource(menuImage.get(position));
            holder.iv_app.setVisibility(View.GONE);
            holder.iv_menu.setVisibility(View.VISIBLE);
//        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)(mContext)).onItemclick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}