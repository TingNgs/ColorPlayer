package com.example.celestefyp.celestefyp;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;
import java.util.jar.Attributes;

public class MainActivity extends AppCompatActivity{
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    private final String DEVICE_ADDRESS="98:D3:31:FD:62:33";
    private BluetoothDevice device;
    private BluetoothSocket socket;
    private OutputStream outputStream;
    int RESULT_LOAD_IMAGE = 0;
    private static final int MY_PERMISSIONS_REQUEST_STORAGE = 1;
    int soundValue0=0,soundValue1=0,soundValue2=0;
    int selectingColor = 0;
    int playingSound;
    boolean soundPlayer = true;
    boolean deviceConnected = false;
    boolean resetting = false;
    ImageView iv_image, iv_color,iv_color0, iv_color1, iv_color2;
    ImageView iv_colorPicker0,iv_colorPicker1,iv_colorPicker2,iv_colorPicker;
    TextView tv_colorPicker0,tv_colorPicker1,tv_colorPicker2;
    TextView tv_color ,tv_fre_need,tv_fre_did;
    TextView tv_colorRGB;
    Button reset;
    ImageButton b_photo;
    //Spinner s_box;
    private final int requestCode = 20;
    SevenColor sc = new SevenColor();
    Bitmap bitmap ;
    private static final String FILE_NAME = "MainMicRecord";
    private static final int SAMPLE_RATE = 44100;//Hz，采样频率
    int FREQUENCY,recordedFREQUENCY;
    private static final long RECORD_TIME = 2000;
    private File mSampleFile;
    private int bufferSize=0;
    private AudioRecord mAudioRecord;
    private boolean recording = false;
    MyHandler mh;
    LinearLayout page1,page2,page3;
    MediaPlayer player;

    int []equal = new int [10];
    int count = 0;
    int failCount = 0;
    int resetCount = 0;
    int randomFailTime = 0;
    int randomRange = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}

        setContentView(R.layout.activity_main);
//set content view AFTER ABOVE sequence (to avoid crash)
        this.setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        super.onCreate(savedInstanceState);
        soundValue0=0;soundValue1=0;soundValue2=0;
        iv_colorPicker0 = (ImageView) findViewById(R.id.iv_colorPicker0);
        iv_colorPicker1 = (ImageView) findViewById(R.id.iv_colorPicker1);
        iv_colorPicker2 = (ImageView) findViewById(R.id.iv_colorPicker2);
        tv_colorPicker0 = (TextView) findViewById(R.id.tv_colorPicker0);
        tv_colorPicker1 = (TextView) findViewById(R.id.tv_colorPicker1);
        tv_colorPicker2 = (TextView) findViewById(R.id.tv_colorPicker2);
        iv_colorPicker = (ImageView) findViewById(R.id.iv_colorPicker0);
        iv_image = (ImageView) findViewById(R.id.iv_image);
        iv_color0 = (ImageView) findViewById(R.id.iv_color0);
        iv_color1 = (ImageView) findViewById(R.id.iv_color1);
        iv_color2 = (ImageView) findViewById(R.id.iv_color2);
        tv_color = (TextView) findViewById(R.id.tv_color);
        tv_fre_need = (TextView) findViewById(R.id.tv_Fre_need);
        tv_fre_did = (TextView) findViewById(R.id.tv_Fre_did);
        tv_colorRGB = (TextView) findViewById(R.id.tv_colorRGB);
        b_photo = (ImageButton) findViewById(R.id.b_photo);
        //s_box = (Spinner) findViewById(R.id.s_box);
        reset = (Button) findViewById(R.id.reset);
        page1 = (LinearLayout) findViewById(R.id.page1);
        page2 = (LinearLayout) findViewById(R.id.page2);
        page3 = (LinearLayout) findViewById(R.id.page3);

        mh=new MyHandler();

        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest();
        }


        iv_color0.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(resetting) return;
                String colorName = sc.getColorName(soundValue0);
                tv_color.setText(colorName);
                int temp_fre =  sc.getColorFre(soundValue0);
                FREQUENCY = temp_fre;

                tv_fre_need.setText("GOAL "+String.valueOf(FREQUENCY));
                if(temp_fre != 0 && recording == false){
                    startRecord();page2.setVisibility(View.GONE);
                    page3.setVisibility(View.VISIBLE);
                }
                if(soundPlayer) {
                    soundPlayer = false;
                    playSound(soundValue0);
                    playingSound = soundValue0;
                    soundPlayer = true;
                }
            }
        });
        iv_color1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(resetting) return;
                String colorName = sc.getColorName(soundValue1);
                tv_color.setText(colorName);
                int temp_fre =  sc.getColorFre(soundValue1);
                FREQUENCY = temp_fre;

                tv_fre_need.setText("GOAL "+String.valueOf(FREQUENCY));
                if(temp_fre != 0 && recording == false){startRecord();page2.setVisibility(View.GONE);
                    page3.setVisibility(View.VISIBLE);}
                if(soundPlayer) {
                    soundPlayer = false;
                    playSound(soundValue1);
                    playingSound = soundValue1;
                    soundPlayer = true;
                }
            }
        });
        iv_color2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(resetting) return;
                String colorName = sc.getColorName(soundValue2);
                tv_color.setText(colorName);
                int temp_fre =  sc.getColorFre(soundValue2);
                FREQUENCY = temp_fre;

                tv_fre_need.setText("GOAL "+String.valueOf(FREQUENCY));
                if(temp_fre != 0 && recording == false){startRecord();page2.setVisibility(View.GONE);
                    page3.setVisibility(View.VISIBLE);}
                if(soundPlayer) {
                    soundPlayer = false;
                    playSound(soundValue2);
                    playingSound = soundValue2;
                    soundPlayer = true;
                }
            }
        });
        iv_colorPicker0.setOnClickListener(colorPicker0_OnClick);
        iv_colorPicker1.setOnClickListener(colorPicker1_OnClick);
        iv_colorPicker2.setOnClickListener(colorPicker2_OnClick);
        tv_colorPicker2.setOnClickListener(colorPicker0_OnClick);
        tv_colorPicker2.setOnClickListener(colorPicker1_OnClick);
        tv_colorPicker2.setOnClickListener(colorPicker2_OnClick);
        b_photo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(recording) return;
                page1.setVisibility(View.GONE);
                Intent photoCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(photoCaptureIntent,requestCode);
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = getApplicationContext();
                page1.setVisibility(View.VISIBLE);
                page2.setVisibility(View.VISIBLE);
                page3.setVisibility(View.GONE);
                resetting = true;
                tv_color.setText("Resetting");
                iv_image.setImageBitmap(null);
                iv_color0.setBackgroundColor(Color.parseColor("#000000"));
                iv_color1.setBackgroundColor(Color.parseColor("#000000"));
                iv_color2.setBackgroundColor(Color.parseColor("#000000"));
                iv_color0.setImageDrawable(context.getResources().getDrawable(R.drawable.black));
                iv_color1.setImageDrawable(context.getResources().getDrawable(R.drawable.black));
                iv_color2.setImageDrawable(context.getResources().getDrawable(R.drawable.black));
                iv_colorPicker0.setBackgroundColor(Color.parseColor("#000000"));
                iv_colorPicker1.setBackgroundColor(Color.parseColor("#000000"));
                iv_colorPicker2.setBackgroundColor(Color.parseColor("#000000"));
                selectingColor = 0;
                tv_fre_need.setText("GOAL 000");
                tv_fre_did.setText("0");
                tv_colorRGB.setText("R:/ G:/ B:/");
                tv_color.setText("Reset");
                resetting = false;
                if(recording) stopRecording();
                recording = false;
            }
        });

        iv_image.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(resetting)  return true;
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
                    int touchX = (int) event.getX();
                    int touchY = (int) event.getY();
                    if (touchX > 0 && touchY > 0 && touchX < bitmap.getWidth() && touchY < bitmap.getHeight()) {
                        int pixelColor = bitmap.getPixel(touchX, touchY);

                        int A = Color.alpha(pixelColor);
                        int R = Color.red(pixelColor);
                        int G = Color.green(pixelColor);
                        int B = Color.blue(pixelColor);
                        tv_colorRGB.setText("R:"+R+" G:"+G+" B:"+B);
                        int tempColor = sc.getSevenColor(pixelColor);
                        int finalColor = sc.getColorValue(tempColor);
                        updateBoxChose(tempColor);
                        getColorImage(tempColor);
                        iv_color.setBackgroundColor(finalColor);
                        iv_colorPicker.setBackgroundColor(finalColor);
                    }
                }
                return true;
            }

        });
        tv_color.setText("BT not connected");
        if(BTinit()) {
            if (BTconnect()) {
                tv_color.setText("BT connected");
                deviceConnected = true;
            }
        }
    }
    View.OnClickListener colorPicker0_OnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            selectingColor = 0;
        }
    };
    View.OnClickListener colorPicker1_OnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            selectingColor = 1;
        }
    };
    View.OnClickListener colorPicker2_OnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            selectingColor = 2;
        }
    };
    private void getColorImage(int tempColor){
        Context context = getApplicationContext();
        iv_color.setImageDrawable(context.getResources().getDrawable(R.drawable.black));
        if (tempColor == 1) iv_color.setImageDrawable(context.getResources().getDrawable(R.drawable.red));
        else if (tempColor == 2) iv_color.setImageDrawable(context.getResources().getDrawable(R.drawable.yellow));
        else if (tempColor == 3) iv_color.setImageDrawable(context.getResources().getDrawable(R.drawable.blue));
        else if (tempColor == 4) iv_color.setImageDrawable(context.getResources().getDrawable(R.drawable.purple));
        else if (tempColor == 5) iv_color.setImageDrawable(context.getResources().getDrawable(R.drawable.orange));
        else if (tempColor == 6) iv_color.setImageDrawable(context.getResources().getDrawable(R.drawable.green));
        else if (tempColor == 7) iv_color.setImageDrawable(context.getResources().getDrawable(R.drawable.silver));
    }

    private static final int RECORD_REQUEST_CODE = 101;
    protected void makeRequest() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECORD_AUDIO},
                RECORD_REQUEST_CODE);
    }

    public boolean BTinit()
    {
        boolean found=false;
        BluetoothAdapter bluetoothAdapter= BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(),"Device doesn't Support Bluetooth",Toast.LENGTH_SHORT).show();
        }
        if(!bluetoothAdapter.isEnabled())
        {
            Intent enableAdapter = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableAdapter, 0);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
        if(bondedDevices.isEmpty())
        {
            Toast.makeText(getApplicationContext(),"Please Pair the Device first",Toast.LENGTH_SHORT).show();
        }
        else
        {

            for (BluetoothDevice iterator : bondedDevices)
            {
                if(iterator.getAddress().equals(DEVICE_ADDRESS))
                {
                    device=iterator;
                    found=true;
                    break;
                }
            }
        }
        return found;
    }
    public boolean BTconnect()
    {
        boolean connected=true;
        try {
            socket = device.createRfcommSocketToServiceRecord(PORT_UUID);
            socket.connect();
        } catch (IOException e) {
            e.printStackTrace();
            connected=false;
        }
        if(connected)
        {

            try {
                outputStream=socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        return connected;
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
    private void playSound(int soundValue){
        player = null;
        switch (soundValue){
            case 1:
                player = MediaPlayer.create(this, R.raw.a);
                break;
            case 2:
                player = MediaPlayer.create(this, R.raw.b);
                break;
            case 3:
                player = MediaPlayer.create(this, R.raw.c);
                break;
            case 4:
                player = MediaPlayer.create(this, R.raw.d);
                break;
            case 5:
                player = MediaPlayer.create(this, R.raw.e);
                break;
            case 6:
                player = MediaPlayer.create(this, R.raw.f);
                break;
            case 7:
                player = MediaPlayer.create(this, R.raw.g);
                break;
            default:
                player = MediaPlayer.create(this,R.raw.error);
                break;
        }
        if(player != null) {
            player.start();
        }
    player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            mp.stop();
            if(mp != null){
                mp.release();
            }
        }
    });
    }

    private void playMusic(int soundValue){
        player = null;
        switch (soundValue){
            case 1:
                player = MediaPlayer.create(this, R.raw.red);
                break;
            case 2:
                player = MediaPlayer.create(this, R.raw.yellow);
                break;
            case 3:
                player = MediaPlayer.create(this, R.raw.blue);
                break;
            case 4:
                player = MediaPlayer.create(this, R.raw.purple);
                break;
            case 5:
                player = MediaPlayer.create(this, R.raw.orange);
                break;
            case 6:
                player = MediaPlayer.create(this, R.raw.green);
                break;
            case 7:
                player = MediaPlayer.create(this, R.raw.silver);
                break;
            default:
                player = MediaPlayer.create(this,R.raw.error);
                break;
        }
        if(player != null) {
            player.start();
        }
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.stop();
                if(mp != null){
                    mp.release();
                }
            }
        });
    }
    private void updateBoxChose(int tempColor){

        if(selectingColor== 0){
            iv_color = (ImageView) findViewById(R.id.iv_color0);
            iv_colorPicker = (ImageView) findViewById(R.id.iv_colorPicker0);
            soundValue0 = tempColor;
        }
        if(selectingColor== 1){
            iv_color = (ImageView) findViewById(R.id.iv_color1);
            iv_colorPicker = (ImageView) findViewById(R.id.iv_colorPicker1);
            soundValue1 = tempColor;
        }
        if(selectingColor== 2){
            iv_color = (ImageView) findViewById(R.id.iv_color2);
            iv_colorPicker = (ImageView) findViewById(R.id.iv_colorPicker2);
            soundValue2 = tempColor;
        }
    }

    public void outPutToArduino(String colorName){
        colorName.concat("\n");
        try {
            outputStream.write(colorName.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(this.requestCode == requestCode && resultCode == RESULT_OK){
            bitmap = (Bitmap)data.getExtras().get("data");
            iv_image.setImageBitmap(bitmap);
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

    private void startRecord() {
        try {
            failCount = 0;
            randomFailTime = (int)(Math.random()*5) +3;
            recording = true;
            mSampleFile = new File(getFilesDir()+"/"+FILE_NAME);
            if(mSampleFile.exists()){
                if(!mSampleFile.delete()){
                    return;
                }
            }
            if(!mSampleFile.createNewFile()){
                return;
            }
        } catch(IOException e) {
            return;
        }
        //为了方便，这里只录制单声道
        //如果是双声道，得到的数据是一左一右，注意数据的保存和处理
        bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize);
        mAudioRecord.startRecording();
        new Thread(new Recording()).start();

    }
    int bufferSizeInBytes = 1024;
    short[] buffer = new short[bufferSizeInBytes];

    class Recording extends Thread {
        @Override
        public void run() {

                while (true) {

                    int bufferReadResult = mAudioRecord.read(buffer, 0, bufferSizeInBytes); // record data from mic into buffer
                    if (bufferReadResult > 0) {
                        calculate();
                    }
                }

        }
    }

    public void calculate() {
        double[] magnitude = new double[bufferSizeInBytes / 2];
        //Create Complex array for use in FFT
        Complex[] fftTempArray = new Complex[bufferSizeInBytes];
        for (int i = 0; i < bufferSizeInBytes; i++) {
            fftTempArray[i] = new Complex(buffer[i], 0);
        }

        //Obtain array of FFT data
        final Complex[] fftArray = FFT.fft(fftTempArray);
        // calculate power spectrum (magnitude) values from fft[]
        for (int i = 0; i < (bufferSizeInBytes / 2) - 1; ++i) {

            double real = fftArray[i].re();
            double imaginary = fftArray[i].im();
            magnitude[i] = Math.sqrt(real * real + imaginary * imaginary);

        }

        // find largest peak in power spectrum
        double max_magnitude = magnitude[0];
        int max_index = 0;
        for (int i = 0; i < magnitude.length; ++i) {
            if (magnitude[i] > max_magnitude) {
                max_magnitude = (int) magnitude[i];
                max_index = i;
            }
        }
        double freq = 44100 * max_index / bufferSizeInBytes;//here will get frequency in hz like(17000,18000..etc)
        equal[count] = (int)freq;
        count += 1;
        resetCount += 1;
        if(count>2){
            if(equal[count-1] != equal[0]){
                count = 0;
            }
        }
        if(count == 5){
            failCount += 1;
            Log.i("test","fre"+String.valueOf(equal[0]));
            count =0;
            resetCount=0;
            Message msg = new Message();
            Bundle b = new Bundle();
            b.putInt("fre",equal[0]);
            msg.setData(b);
            mh.sendMessage(msg);
        }
        if(resetCount== 50){
            Message msg = new Message();
            Bundle b = new Bundle();
            b.putInt("fre",0);
            msg.setData(b);
            mh.sendMessage(msg);
        }

    }

    class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            Bundle b = msg.getData();
            TextView tv = (TextView)findViewById(R.id.tv_Fre_did);
            TextView tv_color = (TextView)findViewById(R.id.tv_color);
            recordedFREQUENCY = b.getInt("fre");
            randomRange=(int)(Math.random()*31)-15;
            tv.setText(String.valueOf(b.getInt("fre")));
            if((FREQUENCY != 0 && Math.abs(recordedFREQUENCY-FREQUENCY)<=15) || failCount == randomFailTime){
                if (failCount == randomFailTime) {
                    tv.setText(String.valueOf(FREQUENCY + randomRange));
                }
                else{
                    tv.setText(String.valueOf(recordedFREQUENCY));
                }
                stopRecording();
                recording = false;
                String colorName = tv_color.getText().toString();
                colorName = colorName.substring(0,1);
                if(deviceConnected) {
                    outPutToArduino(colorName);
                    Toast.makeText(getApplicationContext(), "Send : " + colorName, Toast.LENGTH_LONG).show();
                }
                else
                    Toast.makeText(getApplicationContext(), "Pass : "+colorName, Toast.LENGTH_LONG).show();
                playMusic(playingSound);
            }
        }
    }

    //在这里stop的时候先不要release
    private void stopRecording() {
        mAudioRecord.stop();
        mAudioRecord.release();
    }

}
