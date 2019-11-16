package com.example.easy.ui.register;

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
import com.example.easy.R;
import com.example.easy.tool.Globe;
import com.example.easy.ui.login.LoginActivity;

public class RegisterActivity extends AppCompatActivity {
    private static String username = "";
    private static String password = "";
    private static String result = "";
    private String registerUri = "http://114.55.64.152:3000/register";
    //private String register_uri = "http://192.168.1.103:3000/register";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //底部的提示加载
        TextView registerTip = (TextView)findViewById(R.id.registerTip);
        String tip = "已有账号，请" + "登录";
        Spannable spannable = Spannable.Factory.getInstance().newSpannable(tip);
        spannable.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        }, tip.length() - 2, tip.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        registerTip.setText(spannable);
        registerTip.setMovementMethod(LinkMovementMethod.getInstance());
        //加载结束
        EditText registerUsername = findViewById(R.id.registerUsername);
        EditText registerPassword = findViewById(R.id.registerPassword);
        registerUsername.addTextChangedListener(new TextWatcher() {
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
        registerPassword.addTextChangedListener(new TextWatcher() {
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

        Button registerSubmit = (Button)findViewById(R.id.registerSubmit);
        registerSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(username == "" || password == ""){
                    Toast.makeText(RegisterActivity.this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
                }else {
                    try {
                        result = SubmitUser.Submit(username, password, registerUri);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                ResultReact(result);
            }
        });
    }

    public void ResultReact(String result){
        if(result.equals("\"userexist\"")){
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(RegisterActivity.this, "用户名已经被注册过了", Toast.LENGTH_SHORT).show();
                    reStartActivity();
                }
            });
        }else if(result.equals("\"somethingwrong\"")){
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(RegisterActivity.this, "未知错误，请重试或退出", Toast.LENGTH_SHORT).show();
                    reStartActivity();
                }
            });
        }else if(result.equals("\"success\"")){
            Globe.setLoginUser(username);
            Globe.setPath(getExternalCacheDir().getPath());
            Globe.setPhotoMsg(null);
            Globe.setPhotoMsgRank(null);
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
    //刷新界面
    public void reStartActivity(){
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }
}
