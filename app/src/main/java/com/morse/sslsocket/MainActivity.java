package com.morse.sslsocket;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.morse.socketopenssl.SSLSocket;


public class MainActivity extends AppCompatActivity {


    private TextView tvsend, tvread, tverror;
    private Button btn;
    private Button btn2;
    private Button btn3;
    private EditText et;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvsend = (TextView) findViewById(R.id.tv_send_textview);
        tvread = (TextView) findViewById(R.id.tv_read_textview);
        tverror = (TextView) findViewById(R.id.tv_error_textview);
        et = (EditText) findViewById(R.id.et_edittext01);
        btn = (Button) findViewById(R.id.btn_button01);
        btn2 = (Button) findViewById(R.id.btn_button02);
        btn3 = (Button) findViewById(R.id.btn_button03);

        final SSLSocket sslSocket = new SSLSocket(this, R.raw.test);
        sslSocket.setCallBack(new SSLSocket.CallBack() {
            @Override
            public void readResponse(String str) {
                tvread.setText(str);
            }

            @Override
            public void sendResponse(String str) {
                tvsend.setText(str);
            }

            @Override
            public void onFailure(String str) {
                tverror.setText(str);
            }
        });

        btn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                sslSocket.send("SSLSocket test");
            }
        });
        btn2.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                sslSocket.read();
            }
        });
    }


}
