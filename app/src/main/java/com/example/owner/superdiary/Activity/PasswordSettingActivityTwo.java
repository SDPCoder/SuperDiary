package com.example.owner.superdiary.Activity;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.owner.superdiary.Utils.MD5Utils;
import com.example.owner.superdiary.R;

public class PasswordSettingActivityTwo extends AppCompatActivity {
    Button okBtn, returnBtn;
    EditText oldPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_setting_two);

        initView();
        initEvent();
    }

    private void initEvent() {
        final SharedPreferences sharedPreferences = getSharedPreferences("DiaryPwd", MODE_PRIVATE);
        final String today = getIntent().getExtras().getString("today");
        final String validPwd = sharedPreferences.getString(today, null);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String oldPwdStr = oldPwd.getText().toString();
                if (MD5Utils.encode(oldPwdStr).equals(validPwd)) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove(today);
                    editor.commit();
                    Toast.makeText(PasswordSettingActivityTwo.this, "密码取消成功", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(PasswordSettingActivityTwo.this, "密码错误，请重新输入", Toast.LENGTH_SHORT).show();
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

    private void initView() {
         okBtn = (Button) findViewById(R.id.okBtn2);
         returnBtn = (Button) findViewById(R.id.returnBtn2);
          oldPwd = (EditText) findViewById(R.id.oldPwd);
    }
}