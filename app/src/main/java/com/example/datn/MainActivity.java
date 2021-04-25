package com.example.datn;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.loader.content.AsyncTaskLoader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.datn.encrypt.SmsSecure;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_SMS = 1;

    ArrayList<Conversation> list_conversation = new ArrayList<>();
    RecyclerView conversationRecyclerview;
    ConversationsListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Tin Nhắn");
        conversationRecyclerview = findViewById(R.id.list_conversation);
        adapter = new ConversationsListAdapter(getApplicationContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        conversationRecyclerview.setAdapter(adapter);
        conversationRecyclerview.setLayoutManager(linearLayoutManager);

        checkForSmsPermission();
    }

    @Override
    protected void onResume() {
        super.onResume();
        load_list_message();
        adapter.updateConversationList(list_conversation);
    }

    private void load_list_message() {
        list_conversation.clear();
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

    private void checkForSmsPermission() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            // Permission not yet granted. Use requestPermissions().
            // MY_PERMISSIONS_REQUEST_SEND_SMS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_SMS, Manifest.permission.SEND_SMS,
                            Manifest.permission.RECEIVE_SMS},
                    MY_PERMISSIONS_REQUEST_SMS);
        } else {
            // Permission already granted. Enable the SMS button.
            load_list_message();
            adapter.updateConversationList(list_conversation);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SMS: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    load_list_message();
                    adapter.updateConversationList(list_conversation);
                } else {
                    // Permission denied.
                    Toast.makeText(this, "failure_permission", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.encrypt_menu, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.encrypt_message:
                View inputPassEncryptLayout = getLayoutInflater().inflate(R.layout.input_password_layout, null);
                EditText inputPassEncrypt = inputPassEncryptLayout.findViewById(R.id.input_password);
                AlertDialog.Builder encryptDialogBuilder = new AlertDialog.Builder(this);
                encryptDialogBuilder.setTitle("Nhập vào mật khẩu")
                        .setView(inputPassEncryptLayout)
                        .setPositiveButton("Ok", (dialogInterface, i) -> {
                            encryptAllMessage(inputPassEncrypt.getText().toString());
                        })
                        .setNegativeButton("Huỷ", (dialogInterface, i) -> {}).create();
                encryptDialogBuilder.show();
                break;
            case R.id.decrypt_message:
                View inputPassDecryptLayout = getLayoutInflater().inflate(R.layout.input_password_layout, null);
                EditText inputPassDecrypt = inputPassDecryptLayout.findViewById(R.id.input_password);
                AlertDialog.Builder decryptDialogBuilder = new AlertDialog.Builder(this);
                decryptDialogBuilder.setTitle("Nhập vào mật khẩu")
                        .setView(inputPassDecryptLayout)
                        .setPositiveButton("Ok", (dialogInterface, i) -> {
                            decryptAllMessage(inputPassDecrypt.getText().toString());
                        })
                        .setNegativeButton("Huỷ", (dialogInterface, i) -> {}).create();
                decryptDialogBuilder.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void encryptAllMessage(String pass){
        Cursor cursor = getContentResolver().query(Telephony.Sms.CONTENT_URI, null,
                null, null, null);
        while (cursor.moveToNext()) {
            String body = cursor.getString(cursor.getColumnIndex(Telephony.Sms.BODY));
            ContentValues values = new ContentValues();
            values.put(Telephony.Sms.BODY, SmsSecure.encrypt(pass, "encrypted_by_AT"+body));
            int numRowsUpdated = getContentResolver().update(Telephony.Sms.CONTENT_URI, values,
                    Telephony.Sms._ID + "=?",
                    new String[]{String.valueOf(cursor.getString(cursor.getColumnIndex(Telephony.Sms._ID)))});
            Log.d("TienNAb", "updateMessage: "+numRowsUpdated);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void decryptAllMessage(String pass){
        Cursor cursor = getContentResolver().query(Telephony.Sms.CONTENT_URI, null,
                null, null, null);
        while (cursor.moveToNext()) {
            String body = cursor.getString(cursor.getColumnIndex(Telephony.Sms.BODY));
            ContentValues values = new ContentValues();
            values.put(Telephony.Sms.BODY, SmsSecure.decrypt(pass, "encrypted_by_AT"+body));
            int numRowsUpdated = getContentResolver().update(Telephony.Sms.CONTENT_URI, values,
                    Telephony.Sms._ID + "=?",
                    new String[]{String.valueOf(cursor.getString(cursor.getColumnIndex(Telephony.Sms._ID)))});
            Log.d("TienNAb", "updateMessage: "+numRowsUpdated);
        }
    }

}