package com.example.easy.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.easy.MainActivity;
import com.example.easy.MyAplication;
import com.example.easy.R;
import com.example.easy.tool.Globe;
import com.example.easy.ui.register.RegisterActivity;
import com.example.easy.ui.register.SubmitUser;

public class LoginActivity extends AppCompatActivity {
    private static String username = "";
    private static String password = "";
    private String result = "";
    private String loginUri = "http://114.55.64.152:3000/login";
    //private String login_uri = "http://192.168.1.103:3000/login";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyAplication.getInstance().addActivity(LoginActivity.this);
        setContentView(R.layout.activity_login);
        TextView loginTip = (TextView) findViewById(R.id.loginTip);
        String tip = "还没有账号？请" + "注册";
        Spannable spannable = Spannable.Factory.getInstance().newSpannable(tip);
        spannable.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        }, tip.length() - 2, tip.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        loginTip.setText(spannable);
        loginTip.setMovementMethod(LinkMovementMethod.getInstance());
        //加载结束

        EditText loginUsername = findViewById(R.id.loginUsername);
        EditText loginPassword = findViewById(R.id.loginPassword);
        loginUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                username = s.toString();
            }
        });
        loginPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                password = s.toString();
            }
        });

        Button loginSubmit = (Button)findViewById(R.id.loginSubmit);
        loginSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(username == "" || password == ""){
                    Toast.makeText(LoginActivity.this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
                }else {
                    try {
                        result = SubmitUser.Submit(username, password, loginUri);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                ResultReact(result);
            }
        });
    }

    public void ResultReact(String result){
        if(result.equals("\"success\"")){
            Globe.setLoginUser(username);
            Globe.setPath(getExternalCacheDir().getPath());
            Globe.setPhotoMsg(null);
            Globe.setPhotoMsgRank(null);
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }else if(result.equals("\"usernotexist\"")){
            final Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(LoginActivity.this, "用户名不存在", Toast.LENGTH_SHORT).show();
                    reStartActivity();
                }
            });
        }else if(result.equals("\"passwordwrong\"")){
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(LoginActivity.this, "用户名或者密码错误", Toast.LENGTH_SHORT).show();
                    reStartActivity();
                }
            });
        }
    }

    public void reStartActivity(){
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }
}

