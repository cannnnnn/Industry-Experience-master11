package com.iteration1.savingwildlife;


import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iteration1.savingwildlife.entities.Event;

import java.util.ArrayList;


public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.MyViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Event event);
    }

    private final ArrayList<Event> filterList;
    private final OnItemClickListener listener;
//    private final View.OnClickListener mOnClickListener = new MyOnClickListener();

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView type,location,time;

        public MyViewHolder(View view) {
            super(view);
            type = (TextView) view.findViewById(R.id.event_type);
            location = (TextView) view.findViewById(R.id.event_location);
            time = (TextView) view.findViewById(R.id.event_time);

        }

        public void bind(Event event, OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(event);
                }
            });
        }
    }

    public EventsAdapter(ArrayList<Event> filterList, OnItemClickListener listener) {
        this.filterList = filterList;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_list_row, parent, false);
//        itemView.setOnClickListener(mOnClickListener);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.bind(filterList.get(position), listener);
        Event event = filterList.get(position);
        String type = event.getEvent_type();
        String location = event.getEvent_location();
        String date = event.getEvent_date().replace("-","/") + " " + event.getEvent_start() + "-" + event.getEvent_end();
        holder.type.setText(type);
        holder.location.setText(location);
        holder.time.setText(date);
    }

    @Override
    public int getItemCount() {
        return filterList.size();
    }



}

