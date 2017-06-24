package com.example.owner.superdiary.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.owner.superdiary.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PictureActivity extends AppCompatActivity {
    public static final int REQUEST_TAKE_PHOTO = 1;
    public static final int REQUEST_SELECT_PHOTO = 2;
    public static final int REQUEST_CROP_PHOTO = 3;

    private ImageView showImage;
    Button cameraBtn, selectBtn;

    private Uri imageUri;
    private String filename;
    private File outputImage;
    private SharedPreferences sharedPreferences;
    private String today;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);

        initView();

        today = getIntent().getExtras().getString("today");

        initEvent();
    }

    private void initEvent() {
        sharedPreferences = getSharedPreferences("PictureSrc", MODE_PRIVATE);
        getBitmapFromSharedPreferences();
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filename = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                outputImage = new File(path, filename + ".jpg");
                try {
                    if(outputImage.exists()) {
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                } catch(IOException e) {
                    e.printStackTrace();
                }
                imageUri = Uri.fromFile(outputImage);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent,REQUEST_TAKE_PHOTO);
            }
        });

        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filename = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                outputImage = new File(path, filename + ".jpg");
                try {
                    if(outputImage.exists()) {
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                } catch(IOException e) {
                    e.printStackTrace();
                }
                imageUri = Uri.fromFile(outputImage);
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_SELECT_PHOTO);
            }
        });
    }


    private void initView() {
        cameraBtn = (Button) findViewById(R.id.cameraBtn);
        selectBtn = (Button) findViewById(R.id.selectBtn);
        showImage = (ImageView) findViewById(R.id.showImage);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == PictureActivity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_TAKE_PHOTO:
                    startPhotoZoom(imageUri, 290, 250);
                    break;
                case REQUEST_SELECT_PHOTO:
                    startPhotoZoom(data.getData(), 290, 250);
                    break;
                case REQUEST_CROP_PHOTO:
                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        Bitmap photo = extras.getParcelable("data");
                        showImage.setImageBitmap(photo);
                        refreshAlbum();
                        saveImage(photo, outputImage);
                        saveBitmapToSharedPreferences(photo);
                    }
                    break;
            }
        }
    }

    public void startPhotoZoom(Uri uri, double width, double height) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("scale", true);
        intent.putExtra("aspectX", width/height);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", width);
        intent.putExtra("outputY", height);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        intent.putExtra("return-data", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, REQUEST_CROP_PHOTO);
    }

    public void refreshAlbum() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(imageUri);
        this.sendBroadcast(mediaScanIntent);
    }

    public void saveImage(Bitmap bitmap, File file) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            if (bitmap != null) {
                if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100,
                        fileOutputStream)) {
                    fileOutputStream.flush();
                }
            }
        } catch (FileNotFoundException e) {
            file.delete();
            e.printStackTrace();
        } catch (IOException e) {
            file.delete();
            e.printStackTrace();
        } finally {
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveBitmapToSharedPreferences(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String imageString = new String(Base64.encodeToString(byteArray, Base64.DEFAULT));
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(today, imageString);
        editor.commit();
    }

    private void getBitmapFromSharedPreferences(){
        String imageString = sharedPreferences.getString(today, null);
        if (imageString == null) return;
        byte[] byteArray = Base64.decode(imageString, Base64.DEFAULT);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
        Bitmap bitmap = BitmapFactory.decodeStream(byteArrayInputStream);
        showImage.setImageBitmap(bitmap);
    }
}
