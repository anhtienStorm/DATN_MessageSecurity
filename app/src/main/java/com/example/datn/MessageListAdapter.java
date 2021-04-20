package com.example.datn;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MessageListAdapter extends RecyclerView.Adapter<MessageListViewHolder>{

    ArrayList<Message> list_message;
    Context context;

    public MessageListAdapter(Context context){
        this.context = context;
    }

    public void updateListMessage(ArrayList<Message> list){
        list_message = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MessageListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.message_list_item, parent, false);
        return new MessageListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageListViewHolder holder, int position) {
        holder.bindView(list_message.get(position));
        if ("2".equals(list_message.get(position).getType())){
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.content.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            holder.content.setLayoutParams(params);
        } else {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.content.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            holder.content.setLayoutParams(params);
        }
    }

    @Override
    public int getItemCount() {
        return list_message.size();
    }
}

class MessageListViewHolder extends RecyclerView.ViewHolder{

    TextView content, dateTime;

    public MessageListViewHolder(@NonNull View itemView) {
        super(itemView);

        content = itemView.findViewById(R.id.content_message);
        dateTime = itemView.findViewById(R.id.date_time);
    }

    void bindView(Message message){
        content.setText(message.getBody());
        dateTime.setText(message.getType());
    }
}
