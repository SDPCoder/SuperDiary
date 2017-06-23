package com.example.owner.superdiary.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.owner.superdiary.Utils.MD5Utils;
import com.example.owner.superdiary.R;

public class CheckPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_password);

        Button okBtn = (Button) findViewById(R.id.okBtn3);
        Button returnBtn = (Button) findViewById(R.id.returnBtn3);
        final EditText checkPwd = (EditText) findViewById(R.id.checkPwd);
        final Bundle bundle = getIntent().getExtras();
        final String curPwd = bundle.getString("curPwd");
        final int fab = bundle.getInt("fab");

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String checkPwdStr = checkPwd.getText().toString();
                if (MD5Utils.encode(checkPwdStr).equals(curPwd)) {
                    finish();
                    if (fab == 1) {
                        Intent intent = new Intent(CheckPasswordActivity.this, EditActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(CheckPasswordActivity.this, PictureActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(CheckPasswordActivity.this, "密码错误，请重新输入", Toast.LENGTH_SHORT).show();
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