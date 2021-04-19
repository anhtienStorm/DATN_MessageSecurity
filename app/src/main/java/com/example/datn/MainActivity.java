package com.example.datn;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;
import android.util.Log;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<Conversation> list_conversation = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Uri mainUri = Telephony.MmsSms.CONTENT_CONVERSATIONS_URI;

        Cursor mainCursor = getContentResolver().query(mainUri, null, null, null, null);
        while (mainCursor.moveToNext()) {
            for(int i=0;i<mainCursor.getColumnCount();i++){
                Log.d(mainCursor.getColumnName(i),mainCursor.getString(mainCursor.getColumnIndex(mainCursor.getColumnName(i))));

            }
        }

    }
}