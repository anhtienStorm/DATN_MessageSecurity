package com.example.datn;

import android.app.ActionBar;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datn.encrypt.SmsSecure;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class ConversationActivity extends AppCompatActivity {

    private static final String THREAD_ID = "thread_id";
    private static final String ADDRESS = "address";
    private static final String PASS = "171098";

    private String thread_id, title;
    private ArrayList<Message> list_message = new ArrayList<>();
    private RecyclerView messageRecyclerView;
    private MessageListAdapter adapter;
    private ImageView bt_send, bt_lock, bt_unlock;
    private EditText input;
    private TextView titleView;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        thread_id = getIntent().getStringExtra(THREAD_ID);
        title = getIntent().getStringExtra(ADDRESS);

        messageRecyclerView = findViewById(R.id.list_message);
        bt_send = findViewById(R.id.bt_send);
        input = findViewById(R.id.input_message);

        adapter = new MessageListAdapter(getApplicationContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setReverseLayout(true);
        messageRecyclerView.setLayoutManager(linearLayoutManager);
        messageRecyclerView.setAdapter(adapter);

        updateList();

        bt_send.setOnClickListener(v -> {
//            updateMessage(thread_id);
            if (!TextUtils.isEmpty(input.getText().toString())) {
                send_message(title, input);
            }
        });

        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(actionBar.getDisplayOptions()
                | androidx.appcompat.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        View actionbarView = getLayoutInflater().inflate(R.layout.actionbar_custom_layout,null);
        bt_lock = actionbarView.findViewById(R.id.lock);
        bt_unlock = actionbarView.findViewById(R.id.unlock);
        titleView = actionbarView.findViewById(R.id.title);
        titleView.setText(title);
        bt_unlock.setOnClickListener(v -> {
            unlock_thread(thread_id);
        });
        actionBar.setCustomView(actionbarView);

    }

    void updateList(){
        load_message(thread_id);
        adapter.updateListMessage(list_message);
    }

    private void load_message(String id) {
        list_message.clear();
        Cursor cursor = getContentResolver().query(Telephony.Sms.CONTENT_URI, null,
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
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void updateMessage(String id){
        Cursor cursor = getContentResolver().query(Telephony.Sms.CONTENT_URI, null,
                "thread_id = " + id, null, null);
        while (cursor.moveToNext()) {
            String body = cursor.getString(cursor.getColumnIndex(Telephony.Sms.BODY));
            ContentValues values = new ContentValues();
            try {
                values.put(Telephony.Sms.BODY, SmsSecure.decrypt(PASS, body));
            } catch (Exception e) {
                e.printStackTrace();
            }
            int numRowsUpdated = getContentResolver().update(Telephony.Sms.CONTENT_URI, values,
                    Telephony.Sms._ID + "=?",
                    new String[]{String.valueOf(cursor.getString(cursor.getColumnIndex(Telephony.Sms._ID)))});
            Log.d("TienNAb", "updateMessage: "+numRowsUpdated);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void unlock_thread(String id){
        list_message.clear();
        Cursor cursor = getContentResolver().query(Telephony.Sms.CONTENT_URI, null,
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
            String decryptBody = SmsSecure.decrypt("anhtien", body);
            decryptBody = decryptBody.replace("encrypted_by_AT","");
            String service_center = cursor.getString(
                    cursor.getColumnIndex("service_center"));
            String locked = cursor.getString(cursor.getColumnIndex("locked"));
            String error_code = cursor.getString(cursor.getColumnIndex("error_code"));
            String seen = cursor.getString(cursor.getColumnIndex("seen"));
            String priority = cursor.getString(cursor.getColumnIndex("priority"));

            Message message = new Message(_id, thread_id, date, date_sent, read, status, type,
                    reply_path_present, subject, decryptBody, service_center, locked, error_code, seen,
                    priority);

            list_message.add(message);
        }
        adapter.notifyDataSetChanged();
    }

    private void send_message(String destinationAddress, EditText smsEditText){
        // Set the service center address if needed, otherwise null.
        String scAddress = null;
        // Set pending intents to broadcast
        // when message sent and when delivered, or set to null.
        PendingIntent sentIntent = null, deliveryIntent = null;
        // Use SmsManager.
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(destinationAddress, scAddress, smsEditText.getText().toString(),
                        sentIntent, deliveryIntent);

//        Message message = new Message(null, thread_id, null, null, "1",
//                null, "2", null, null,
//                smsEditText.getText().toString(), null, null, null,
//                null, null);
//        list_message.add(message);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                updateList();
                smsEditText.setText("");
//            }
//        }, 5000);
    }
}
