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
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    private final String btDeviceName = "HC-05";
    private BluetoothDevice device;
    private BluetoothSocket socket;
    private OutputStream outputStream;
    int RESULT_LOAD_IMAGE = 0;
    private static final int MY_PERMISSIONS_REQUEST_STORAGE = 1;
    int soundValue0=0,soundValue1=0,soundValue2=0;
    boolean soundPlayer = true;
    boolean deviceConnected = false;
    boolean resetting = false;
    ImageView iv_image, iv_color,iv_color0, iv_color1, iv_color2;
    TextView tv_color ,tv_fre_need,tv_fre_did;
    TextView tv_colorRGB;
    Button b_photo,record,stop,reset;
    Spinner s_box;
    private final int requestCode = 20;
    SevenColor sc = new SevenColor();
    Bitmap bitmap ;
    File recordfile ;
    /*MediaRecorder audioRecorder;
    MediaPlayer mediaPlayer;
    boolean isPlaying = false;
    File recodeFile;*/
    private static final String FILE_NAME = "MainMicRecord";
    private static final int SAMPLE_RATE = 22050;//Hz，采样频率
    int FREQUENCY;
    private static final long RECORD_TIME = 2000;
    private File mSampleFile;
    private int bufferSize=0;
    private AudioRecord mAudioRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        soundValue0=0;soundValue1=0;soundValue2=0;
        iv_image = (ImageView) findViewById(R.id.iv_image);
        iv_color0 = (ImageView) findViewById(R.id.iv_color0);
        iv_color1 = (ImageView) findViewById(R.id.iv_color1);
        iv_color2 = (ImageView) findViewById(R.id.iv_color2);
        tv_color = (TextView) findViewById(R.id.tv_color);
        tv_fre_need = (TextView) findViewById(R.id.tv_Fre_need);
        tv_fre_did = (TextView) findViewById(R.id.tv_Fre_did);
        tv_colorRGB = (TextView) findViewById(R.id.tv_colorRGB);
        b_photo = (Button) findViewById(R.id.b_photo);
        s_box = (Spinner) findViewById(R.id.s_box);
        record = (Button) findViewById(R.id.record_button);
        reset = (Button) findViewById(R.id.reset);
        stop = (Button) findViewById(R.id.stop_button);
        stop.setEnabled(false);

        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(resetting) return;
                record.setEnabled(false);
                stop.setEnabled(true);
                startRecord();
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(resetting) return;
                stopRecording();
                frequencyAnalyse();
                record.setEnabled(true);
                stop.setEnabled(false);

            }
        });

        iv_color0.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(resetting) return;
                String colorName = sc.getColorName(soundValue0);
                tv_color.setText(colorName);
                int temp_fre =  sc.getColorFre(soundValue0);
                FREQUENCY = temp_fre;
                tv_fre_need.setText("Need:"+String.valueOf(FREQUENCY));
                if(soundPlayer) {
                    soundPlayer = false;
                    playSound(soundValue0);
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
                tv_fre_need.setText("Need:"+String.valueOf(FREQUENCY));
                if(soundPlayer) {
                    soundPlayer = false;
                    playSound(soundValue1);
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
                tv_fre_need.setText("Need:"+String.valueOf(FREQUENCY));
                if(soundPlayer) {
                    soundPlayer = false;
                    playSound(soundValue2);
                    soundPlayer = true;
                }
            }
        });

        b_photo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(resetting) return;
                Intent photoCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(photoCaptureIntent,requestCode);

            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetting = true;
                tv_color.setText("Resetting");
                iv_image.setImageBitmap(null);
                iv_color0.setBackgroundColor(Color.parseColor("#000000"));
                iv_color1.setBackgroundColor(Color.parseColor("#000000"));
                iv_color2.setBackgroundColor(Color.parseColor("#000000"));
                stop.setEnabled(false);
                record.setEnabled(true);
                tv_fre_need.setText("Need:");
                tv_fre_did.setText("Recorded:");
                tv_colorRGB.setText("R:/ G:/ B:/");
                s_box.setSelection(0);
                if(BTinit()) {
                    if (BTconnect()) {
                        tv_color.setText("BT connected");
                        deviceConnected = true;
                    }
                }
                if(!(tv_color.getText().toString().equals("BT connected")))tv_color.setText("BT not connected");
                resetting = false;
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
                        iv_color.setBackgroundColor(finalColor);
                    }
                }
                return true;
            }

        });
        ArrayAdapter<CharSequence> boxList = ArrayAdapter.createFromResource(MainActivity.this,
                R.array.ChooseBox,
                android.R.layout.simple_spinner_dropdown_item);
        s_box.setAdapter(boxList);
        tv_color.setText("BT not connected");
        if(BTinit()) {
            if (BTconnect()) {
                tv_color.setText("BT connected");
                deviceConnected = true;
            }
        }
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
                if(btDeviceName.equals(iterator.getName()))
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
        MediaPlayer player = null;
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
    private void updateBoxChose(int tempColor){
        if(s_box.getSelectedItemPosition()== 0){
            iv_color = (ImageView) findViewById(R.id.iv_color0);
            soundValue0 = tempColor;
        }
        if(s_box.getSelectedItemPosition()== 1){
            iv_color = (ImageView) findViewById(R.id.iv_color1);
            soundValue1 = tempColor;
        }
        if(s_box.getSelectedItemPosition()== 2){
            iv_color = (ImageView) findViewById(R.id.iv_color2);
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
            /*if (!(iv_image.getDrawable() == null)) {
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
                    tv_colorRGB.setText("R:"+R+" G:"+G+" B:"+B);
                    int tempColor = sc.getSevenColor(pixelColor);
                    int finalColor = sc.getColorValue(tempColor);

                    updateBoxChose(tempColor);
                    iv_color.setBackgroundColor(finalColor);
                }
            }*/
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
        new Thread(new AudioRecordThread()).start();
    }

    private class  AudioRecordThread implements Runnable{
        @Override
        public void run() {
            //将录音数据写入文件
            short[] audiodata = new short[bufferSize/2];
            DataOutputStream fos = null;
            try {
                fos = new DataOutputStream( new FileOutputStream(mSampleFile));
                int readSize;
                while (mAudioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING){
                    readSize = mAudioRecord.read(audiodata,0,audiodata.length);
                    if(AudioRecord.ERROR_INVALID_OPERATION != readSize){
                        for(int i = 0;i<readSize;i++){
                            fos.writeShort(audiodata[i]);
                            fos.flush();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if(fos!=null){
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                //在这里release
                mAudioRecord.release();
                mAudioRecord = null;
            }
        }
    };

    //在这里stop的时候先不要release
    private void stopRecording() {
        mAudioRecord.stop();
    }

    //对录音文件进行分析
    private void frequencyAnalyse(){
        if(mSampleFile == null){
            Toast.makeText(getApplicationContext(), "NULL", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            DataInputStream inputStream = new DataInputStream(new FileInputStream(mSampleFile));
            //16bit采样，因此用short[]
            //如果是8bit采样，这里直接用byte[]
            //从文件中读出一段数据，这里长度是SAMPLE_RATE，也就是1s采样的数据
            short[] buffer=new short[SAMPLE_RATE];
            for(int i = 0;i<buffer.length;i++){
                buffer[i] = inputStream.readShort();
            }
            short[] data = new short[FFT.FFT_N];

            //为了数据稳定，在这里FFT分析只取最后的FFT_N个数据
            System.arraycopy(buffer, buffer.length - FFT.FFT_N,
                    data, 0, FFT.FFT_N);

            //FFT分析得到频率
            int frequence = (int)FFT.GetFrequency(data);
            tv_fre_did.setText("Recorded:"+String.valueOf(frequence));
            int RESOLUTION = 100; //Hz，误差
            if(Math.abs(frequence - FREQUENCY)<RESOLUTION){
                //测试通过
                if(deviceConnected) {
                    String colorName = tv_color.getText().toString();
                    colorName=colorName.substring(0,1);
                    outPutToArduino(colorName);
                    Toast.makeText(getApplicationContext(), "Send : "+colorName, Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(), "Pass", Toast.LENGTH_LONG).show();
                }
            }else{
                //测试失败
                Toast.makeText(getApplicationContext(), "FAIL", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "At least need to record 1 second", Toast.LENGTH_SHORT).show();
        }
    }

}

