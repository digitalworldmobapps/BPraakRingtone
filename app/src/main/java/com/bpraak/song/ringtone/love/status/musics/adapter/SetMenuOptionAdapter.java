package com.bpraak.song.ringtone.love.status.musics.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.bpraak.song.ringtone.love.status.musics.R;
import com.bpraak.song.ringtone.love.status.musics.activity.RingActivity;

import java.util.ArrayList;


public class SetMenuOptionAdapter extends RecyclerView.Adapter<SetMenuOptionAdapter.MyViewHolder> {

    private ArrayList<String> dataSet;
    ArrayList<Integer> menuImage;
    Context mContext;

    public SetMenuOptionAdapter(Context pContext, ArrayList<String> strArray, ArrayList<Integer> integers) {
        dataSet = strArray;
        menuImage = integers;
        mContext = pContext;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_menu;
        ImageView iv_menu;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.tv_menu = (TextView) itemView.findViewById(R.id.tv_menu);
            this.iv_menu = (ImageView) itemView.findViewById(R.id.iv_menu);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.setmenu_item, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.tv_menu.setText(dataSet.get(position));
        holder.iv_menu.setImageResource(menuImage.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((RingActivity) (mContext)).onOptionItemclick(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}