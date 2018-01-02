package myproject.example.com.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.Serializable;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
                                                               Serializable {

    private static final String TAG = "MainActivity";
    private static final long serialVersionUID = 1L;
    private String ip;
    private int port;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            PreferenceManager.setDefaultValues(getApplicationContext(), R.xml.settings, true);
        }

        Button startButton = (Button) findViewById(R.id.start);
        Button exitButton = (Button) findViewById(R.id.exit);

        startButton.setOnClickListener(this);
        exitButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            // first screen pressed the connect button
            case R.id.start:
                final AlertDialog.Builder builder_connection = new AlertDialog.Builder(MainActivity.this);
                @SuppressLint("InflateParams")
                View connectView = getLayoutInflater().inflate(R.layout.connect_dialog, null);
                final EditText ipEdit = (EditText) connectView.findViewById(R.id.ip_edit);
                final EditText portEdit = (EditText) connectView.findViewById(R.id.port_edit);
                final Button connectButton = (Button) connectView.findViewById(R.id.connect_to_rpi);
                Button exitButton = (Button) connectView.findViewById(R.id.exit_to_connection_label);


                builder_connection.setView(connectView);
                builder_connection.setCancelable(false);
                final AlertDialog alertDialog_connection = builder_connection.create();
                alertDialog_connection.show();

                // Connection button pressed
                connectButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        alertDialog_connection.dismiss();
                        ip = ipEdit.getText().toString();
                        port = Integer.parseInt(portEdit.getText().toString());

                        // if ip text and port text non empty
                        if (!ipEdit.getText().toString().isEmpty() && !portEdit.getText().toString().isEmpty()) {

                            showToastMessage(ip+":"+port +"  bağlanılıyor...",1500);

                            TCPClientConnection firstconnection = new TCPClientConnection(ip,port,"T",null);

                            // when it is true continue and work
                            while(!firstconnection.isConnected());

                            Log.d(TAG,"Bağlandı mı = " + firstconnection.isConnected());
                            showToastMessage("Bağlandı mı = " + firstconnection.isConnected(),1000);

                            // if connected for request
                            if(firstconnection.isConnected())
                            {
                                // start service
                                startService(new Intent(MainActivity.this,MyService.class));

                                showToastMessage(ip+":"+port +" Bağlandı...",1500);
                                Intent intent = new Intent(MainActivity.this, DisplayActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("Object", (Serializable) firstconnection);
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
                            else
                            {
                                showToastMessage(ip+":"+port +"Bağlantı Başarısız...",2000);
                                //finish();
                            }

                        }
                        // Connection Fail!
                        else {
                            showToastMessage("Bağlantı Başarısız!",1500);
                            final AlertDialog.Builder builder_connect_fail = new AlertDialog.Builder(MainActivity.this);
                            @SuppressLint("InflateParams")
                            View connectFail = getLayoutInflater().inflate(R.layout.connect_fail, null);
                            Button retry_btn = (Button) connectFail.findViewById(R.id.retry_connect);

                            builder_connect_fail.setView(connectFail);
                            builder_connect_fail.setCancelable(false);
                            final AlertDialog alertDialog_connection_fail = builder_connect_fail.create();
                            alertDialog_connection_fail.show();

                            // retry button pressed
                            retry_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    // close fail dialog and show connection screen
                                    alertDialog_connection_fail.dismiss();
                                    //alertDialog_connection.show();
                                }
                            });
                        }
                    }
                });
                exitButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog_connection.dismiss();
                    }
                });


                break;
            // first screes exit to app
            case R.id.exit:

                finish();
                System.exit(0);
                break;

        }

    }

    private void showToastMessage(String text, int duration){
        final Toast toast = Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT);
        toast.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, duration);
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


}
