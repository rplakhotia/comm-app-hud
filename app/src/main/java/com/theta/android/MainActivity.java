package com.theta.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private TextView messageTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        registerReceiver(mBroadcastReceiver, new IntentFilter("INTENT_FROM_SERVICE"));
        startService(new Intent(this, RecieveData.class));
        messageTextView = (TextView)findViewById(R.id.message);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (TextUtils.equals(intent.getAction(),"INTENT_FROM_SERVICE")){

                if(!TextUtils.isEmpty(intent.getStringExtra("MESSAGE"))){

                    String message = intent.getStringExtra("MESSAGE").toString();
                    System.out.println("intent.getStringExtra(\"MESSAGE\") = " + message);
                    messageTextView.setText(message);

                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }
}
