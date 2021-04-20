package com.example.datn;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ConversationActivity extends AppCompatActivity {

    private static final String THREAD_ID = "thread_id";
    private static final String ADDRESS = "address";

    String thread_id, title;
    ArrayList<Message> list_message = new ArrayList<>();
    RecyclerView messageRecyclerView;
    MessageListAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        thread_id = getIntent().getStringExtra(THREAD_ID);
        title = getIntent().getStringExtra(ADDRESS);
        setTitle(title);
        load_message(thread_id);

        messageRecyclerView = findViewById(R.id.list_message);
        adapter = new MessageListAdapter(getApplicationContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setReverseLayout(true);
        messageRecyclerView.setLayoutManager(linearLayoutManager);
        messageRecyclerView.setAdapter(adapter);
        adapter.updateListMessage(list_message);
    }

    private void load_message(String id) {
        Uri uri = Uri.parse("content://sms/");
        Cursor cursor = getContentResolver().query(uri, null,
                "thread_id = " + id, null, null);
        while (cursor.moveToNext()) {
            String _id = cursor.getString(cursor.getColumnIndex("_id"));
            String thread_id = cursor.getString(cursor.getColumnIndex("thread_id"));
            String date = cursor.getString(cursor.getColumnIndex("date"));
            String date_sent = cursor.getString(cursor.getColumnIndex("date_sent"));
            String read = cursor.getString(cursor.getColumnIndex("read"));
            String status = cursor.getString(cursor.getColumnIndex("status"));
            String type = cursor.getString(cursor.getColumnIndex("type"));
            String reply_path_present = cursor.getString(
                    cursor.getColumnIndex("reply_path_present"));
            String subject = cursor.getString(cursor.getColumnIndex("subject"));
            String body = cursor.getString(cursor.getColumnIndex("body"));
            String service_center = cursor.getString(
                    cursor.getColumnIndex("service_center"));
            String locked = cursor.getString(cursor.getColumnIndex("locked"));
            String error_code = cursor.getString(cursor.getColumnIndex("error_code"));
            String seen = cursor.getString(cursor.getColumnIndex("seen"));
            String priority = cursor.getString(cursor.getColumnIndex("priority"));

            Message message = new Message(_id, thread_id, date, date_sent, read, status, type,
                    reply_path_present, subject, body, service_center, locked, error_code, seen,
                    priority);

            list_message.add(message);

//            for (int i = 0; i < cursor.getColumnCount(); i++) {
//                try {
//                    Log.d(i + "", cursor.getColumnName(i) + ": "
//                            + cursor.getString(cursor.getColumnIndex(cursor.getColumnName(i))));
//                } catch (Exception e) {
//                }
//            }
        }
    }
}
