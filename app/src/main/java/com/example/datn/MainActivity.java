package com.example.datn;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;
import android.util.Log;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<Conversation> list_conversation = new ArrayList<>();
    RecyclerView conversationRecyclerview;
    ConversationsListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Tin Nhắn");

        load_list_message();
        conversationRecyclerview = findViewById(R.id.list_conversation);
        adapter = new ConversationsListAdapter(getApplicationContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        conversationRecyclerview.setAdapter(adapter);
        conversationRecyclerview.setLayoutManager(linearLayoutManager);
        adapter.updateConversationList(list_conversation);

    }

    private void load_list_message() {
        Uri uri = Telephony.MmsSms.CONTENT_CONVERSATIONS_URI;
        Cursor cursor = getContentResolver().query(uri, null, null,
                null, null);
        while (cursor.moveToNext()) {
            String date = cursor.getString(
                    cursor.getColumnIndex("date"));
            String reply_path_present = cursor.getString(
                    cursor.getColumnIndex("reply_path_present"));
            String body = cursor.getString(
                    cursor.getColumnIndex("body"));
            String type = cursor.getString(
                    cursor.getColumnIndex("type"));
            String thread_id = cursor.getString(
                    cursor.getColumnIndex("thread_id"));
            String locked = cursor.getString(
                    cursor.getColumnIndex("locked"));
            String date_sent = cursor.getString(
                    cursor.getColumnIndex("date_sent"));
            String read = cursor.getString(
                    cursor.getColumnIndex("read"));
            String address = cursor.getString(
                    cursor.getColumnIndex("address"));
            String service_center = cursor.getString(
                    cursor.getColumnIndex("service_center"));
            String error_code = cursor.getString(
                    cursor.getColumnIndex("error_code"));
            String _id = cursor.getString(
                    cursor.getColumnIndex("_id"));
            String status = cursor.getString(
                    cursor.getColumnIndex("status"));

            Conversation conversation = new Conversation(date, reply_path_present, body, type,
                    thread_id, locked, date_sent, read, address, service_center,
                    error_code, _id, status);

            list_conversation.add(conversation);
        }
    }
}