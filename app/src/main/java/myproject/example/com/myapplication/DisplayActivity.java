package myproject.example.com.myapplication;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.VideoView;


public class DisplayActivity extends AppCompatActivity implements OnClickListener{

    private static final String TAG = "DisplayActivity";
    private Button buttonVideoView;
    private Button buttonBack;
    private Button buttonExit;
    private TCPClientConnection controlled;
    private boolean connected;
    private ImageView imageView;
    private String host;
    private int port;
    private boolean isImageFitToScreen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        if (savedInstanceState == null) {
            PreferenceManager.setDefaultValues(getApplicationContext(), R.xml.settings, true);
        }

        // Bu bağlantı objesi bize lazım!
        controlled = (TCPClientConnection) getIntent().getExtras().getSerializable("Object");
        assert controlled != null;
        host = controlled.getHost();
        port = controlled.getPort();

        Log.d(TAG,"Bağlantı sağlandı --> " + controlled.isConnected());
        connected = controlled.isConnected();

        showToastMessage(host+":"+port,2000);

        buttonVideoView =  (Button)findViewById(R.id.video_viewButton);
        buttonBack =  (Button)findViewById(R.id.backButton);
        buttonExit =  (Button)findViewById(R.id.exitButton);
        imageView = (ImageView)findViewById(R.id.camera_preview);
        Switch mySwitch = (Switch) findViewById(R.id.mySwitch);

        buttonBack.setOnClickListener(this);
        buttonExit.setOnClickListener(this);
        buttonVideoView.setOnClickListener(this);

        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){

                   if(connected){

                       TCPClientConnection sendLock = new TCPClientConnection(host,port,"l",null);
                       Log.d(TAG,"'lock' gönderildi.");
                       showToastMessage("Kapı Kilitlendi.",1000);
                   }


                }else{

                    if(connected){

                        TCPClientConnection sendUnlock = new TCPClientConnection(host,port,"u",null);
                        Log.d(TAG,"'unlock' gönderildi.");
                        showToastMessage("Kapı Açıldı.",1000);
                    }
                }
            }
        });
    }
    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.camera_preview:

                if(isImageFitToScreen) {
                    isImageFitToScreen=false;
                    imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 0));
                    imageView.setAdjustViewBounds(true);
                }else{
                    isImageFitToScreen=true;
                    imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                }

                break;


            case R.id.video_viewButton:
                setDefaultColors();
                buttonVideoView.setBackgroundColor(getResources().getColor(R.color.clicked_color));
                String str = host+":"+port;
                showToastMessage(str,1000);

                // show video
                /*getWindow().setFormat(PixelFormat.UNKNOWN);
                String uriPath = "android.resource://myproject.example.com.myapplication/" + R.raw.video;
                Uri uri = Uri.parse(uriPath);
                videoView.setVideoURI(uri);
                videoView.requestFocus();
                videoView.start();*/
                // end show video


                //buttonVideoView.setEnabled(false);
                int picnum = 1;
                while(connected){


                        TCPClientConnection receiveImage = new TCPClientConnection(host,port,"i",imageView);
                        Log.d(TAG,"'image' alındı.");
                        imageView = (ImageView)receiveImage.getImageView();
                        SystemClock.sleep(2000);
                        picnum++;
                        if(picnum == 8)
                            break;

                }

                break;

            case R.id.backButton:  // click back button
                setDefaultColors();
                showToastMessage(getString(R.string.back_hit),1000);
                buttonBack.setBackgroundColor(getResources().getColor(R.color.clicked_color));
                if(connected){
                    TCPClientConnection exitBack = new TCPClientConnection(host,port,"e",null);
                    Log.d(TAG,"'exit' gönderildi.");
                    connected = false;
                    controlled.setConnected(false);
                }

                finish();
                startActivity(new Intent(DisplayActivity.this,MainActivity.class));

                break;

            case R.id.exitButton:  // click exit button
                setDefaultColors();
                showToastMessage(getString(R.string.exit_hit),1000);
                buttonExit.setBackgroundColor(getResources().getColor(R.color.clicked_color));
                if(connected){
                    TCPClientConnection exitExit = new TCPClientConnection(host,port,"e",null);
                    Log.d(TAG,"'exit' gönderildi.");
                    connected = false;
                    controlled.setConnected(false);
                }
                finish();
                System.exit(0);

                break;
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {

            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setDefaultColors()
    {
        buttonVideoView.setBackgroundColor(Color.WHITE);
        buttonExit.setBackgroundColor(Color.WHITE);
        buttonBack.setBackgroundColor(Color.WHITE);
    }



    private void showToastMessage(String text, int duration) {
        final Toast toast = Toast.makeText(DisplayActivity.this, text, Toast.LENGTH_SHORT);
        toast.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, duration);
    }

}

