package com.example.owner.superdiary.Activity;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.owner.superdiary.Utils.MD5Utils;
import com.example.owner.superdiary.R;

public class PasswordSettingActivityOne extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_setting_one);

        Button okBtn = (Button) findViewById(R.id.okBtn1);
        Button returnBtn = (Button) findViewById(R.id.returnBtn1);
        final EditText newPwd = (EditText) findViewById(R.id.newPwd);
        final EditText confirmPwd = (EditText) findViewById(R.id.confirmPwd);
        final SharedPreferences sharedPreferences = getSharedPreferences("DiaryPwd", MODE_PRIVATE);
        final String today = getIntent().getExtras().getString("today");

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newPwdStr = newPwd.getText().toString();
                String confirmPwdStr = confirmPwd.getText().toString();
                if (TextUtils.isEmpty(newPwdStr)) {
                    Toast.makeText(PasswordSettingActivityOne.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    if (newPwdStr.equals(confirmPwdStr)) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(today, MD5Utils.encode(newPwdStr));
                        editor.commit();
                        Toast.makeText(PasswordSettingActivityOne.this, "密码设置成功", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(PasswordSettingActivityOne.this, "两次输入的密码不同，请重新输入", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}