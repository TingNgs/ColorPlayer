package com.example.celestefyp.celestefyp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    int RESULT_LOAD_IMAGE = 0;
    private static final int MY_PERMISSIONS_REQUEST_STORAGE = 1;

    ImageView iv_image, iv_color, iv_color0, iv_color1, iv_color2;
    TextView tv_color,tv_color1;
    Button b_pick,b_photo;
    Spinner s_box;
    private final int requestCode = 20;
    SevenColor sc = new SevenColor();
    Bitmap bitmap ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iv_image = (ImageView) findViewById(R.id.iv_image);
        iv_color = (ImageView) findViewById(R.id.iv_color0);
        iv_color0 = (ImageView) findViewById(R.id.iv_color0);
        iv_color1 = (ImageView) findViewById(R.id.iv_color1);
        iv_color2 = (ImageView) findViewById(R.id.iv_color2);
        tv_color = (TextView) findViewById(R.id.tv_color);
        tv_color1 = (TextView) findViewById(R.id.tv_color1);
        //b_pick = (Button) findViewById(R.id.b_pick);
        b_photo = (Button) findViewById(R.id.b_photo);
        s_box = (Spinner) findViewById(R.id.s_box);


        iv_color0.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //iv_color = (ImageView) findViewById(R.id.iv_color0);
            }
        });
        iv_color1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //iv_color = (ImageView) findViewById(R.id.iv_color1);
            }
        });
        iv_color2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //iv_color = (ImageView) findViewById(R.id.iv_color2);
            }
        });

        b_photo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent photoCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(photoCaptureIntent,requestCode);

            }
        });

        ArrayAdapter<CharSequence> boxList = ArrayAdapter.createFromResource(MainActivity.this,
                R.array.ChooseBox,
                android.R.layout.simple_spinner_dropdown_item);
        s_box.setAdapter(boxList);

        /*b_pick.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

            }
        });

        iv_image.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {


                return true;
            }
        });*/
    }

    @Override
    public void onRequestPermissionsResult (int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case  MY_PERMISSIONS_REQUEST_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI) ;
                        startActivityForResult(i, RESULT_LOAD_IMAGE);
                    }
                }
                else {
                    Toast.makeText(MainActivity.this, "No permission granted!", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }
    private void updateBoxChose(){
        if(s_box.getSelectedItemPosition()== 0){
            iv_color = (ImageView) findViewById(R.id.iv_color0);
        }
        if(s_box.getSelectedItemPosition()== 1){
            iv_color = (ImageView) findViewById(R.id.iv_color1);
        }
        if(s_box.getSelectedItemPosition()== 2){
            iv_color = (ImageView) findViewById(R.id.iv_color2);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if(this.requestCode == requestCode && resultCode == RESULT_OK){
            Bitmap bitmap = (Bitmap)data.getExtras().get("data");
            iv_image.setImageBitmap(bitmap);
            if (!(iv_image.getDrawable() == null)) {
                if (iv_image.getDrawingCache() != null) {
                    bitmap.recycle();
                    bitmap = null;
                }
                iv_image.setDrawingCacheEnabled(true);
                iv_image.setDrawingCacheEnabled(true);
                iv_image.buildDrawingCache();
                bitmap = Bitmap.createBitmap(iv_image.getDrawingCache());
                iv_image.setDrawingCacheEnabled(false);
                int touchX = bitmap.getWidth()/2;
                int touchY = bitmap.getHeight()/2;
                if (touchX > 0 && touchY > 0 && touchX < bitmap.getWidth() && touchY < bitmap.getHeight()) {
                    int pixelColor = bitmap.getPixel(touchX, touchY);

                    int A = Color.alpha(pixelColor);
                    int R = Color.red(pixelColor);
                    int G = Color.green(pixelColor);
                    int B = Color.blue(pixelColor);
                    tv_color.setText("Alpha: " + A +"Color: "+ R + ", "+ G +", "+ B);
                    int tempColor = sc.getSevenColor(pixelColor);
                    int finalColor = sc.getColorValue(tempColor);
                    A = Color.alpha(finalColor);
                    R = Color.red(finalColor);
                    G = Color.green(finalColor);
                    B = Color.blue(finalColor);
                    tv_color1.setText("Alpha: " + A +"Color: "+ R + ", "+ G +", "+ B);
                    updateBoxChose();
                    iv_color.setBackgroundColor(finalColor);
                }
            }
        }
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            iv_image.setImageBitmap(BitmapFactory.decodeFile(picturePath));
        }
    }
}

