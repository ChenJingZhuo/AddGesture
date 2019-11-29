package com.cjz.addgesture;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity {

    private GestureOverlayView mGov;
    private EditText mGestureName;
    private Gesture mGesture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mGov = (GestureOverlayView) findViewById(R.id.gov);
        mGov.setGestureColor(Color.BLUE);
        mGov.setGestureStrokeWidth(10);
        mGov.addOnGesturePerformedListener(new GestureOverlayView.OnGesturePerformedListener() {
            @Override
            public void onGesturePerformed(GestureOverlayView gestureOverlayView, Gesture gesture) {
                mGesture=gesture;
                final View dialog=getLayoutInflater().inflate(R.layout.save_gesture,null);
                ImageView imageView=dialog.findViewById(R.id.show);
                mGestureName=dialog.findViewById(R.id.gesture_name);
                Bitmap bitmap=gesture.toBitmap(128,128,10,0xff0000ff);
                imageView.setImageBitmap(bitmap);
                new AlertDialog.Builder(MainActivity.this).setView(dialog)
                        .setPositiveButton("保存", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);

                            }
                        }).setNegativeButton("取消",null).show();

            }
        });
    }

    public void saveFile(){
        if (mGesture!=null){
            GestureLibrary library= GestureLibraries.fromFile("/sdcard/mygestures");
            library.addGesture(mGestureName.getText().toString(),mGesture);
            boolean result=library.save();
            if (result){
                Toast.makeText(MainActivity.this,"保存成功",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(MainActivity.this,"保存失败",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==1){
            for (int i=0;i<permissions.length;i++){
                if (grantResults[i]== PackageManager.PERMISSION_GRANTED){
                    saveFile();
                }else {
                    Toast.makeText(this,"权限"+permissions[i]+"申请失败，不能保存手势文件",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
