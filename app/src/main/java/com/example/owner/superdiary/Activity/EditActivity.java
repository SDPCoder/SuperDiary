package com.example.owner.superdiary.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.owner.superdiary.R;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class EditActivity extends AppCompatActivity {
    private String FILE_NAME;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Button saveBtn = (Button) findViewById(R.id.saveBtn);
        Button clearBtn = (Button) findViewById(R.id.clearBtn);
        editText = (EditText) findViewById(R.id.editText);
        Bundle bundle = getIntent().getExtras();
        String today = bundle.getString("today");
        FILE_NAME = today + "_diary.txt";

        load();

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try (FileOutputStream fileOutputStream = openFileOutput(FILE_NAME, MODE_PRIVATE)) {
                    String str = editText.getText().toString();
                    fileOutputStream.write(str.getBytes());
                    Toast.makeText(EditActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                } catch (IOException ex) {
                    Log.e("TAG", "Fail to save file.");
                }
            }
        });

        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setText("");
            }
        });
    }

    public void load() {
        try (FileInputStream fileInputStream = openFileInput(FILE_NAME)) {
            byte[] contents = new byte[fileInputStream.available()];
            fileInputStream.read(contents);
            editText.setText(new String(contents));
            editText.setSelection(editText.getText().length());
        } catch (IOException ex) {
            Log.e("TAG", "Fail to load file.");
        }
    }
}