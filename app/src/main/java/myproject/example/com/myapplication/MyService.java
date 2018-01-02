package myproject.example.com.myapplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.View;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class MyService extends Service implements Runnable {

    private static final int NOTIFICATION_ID = 1;
    private static final String TAG = "SERVICE";

    private boolean mRunning = false;
    private Thread mThread;

    private Socket mSocket;
    private InputStream mInputStream;
    private DataInputStream dis;
    private static final String host = "192.168.43.208";
    private static final int port = 13030;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //setNotificationMessage("Service created");

        if (mThread == null) {
            mRunning = true;

            mThread = new Thread(this);
            mThread.start();
        }
    }

    @Override
    public void run() {
        try {
            while (mRunning) {
                try {
                    //setNotificationMessage("Connecting");
                    Log.d(TAG,"Connecting");
                    mSocket = new Socket();
                    mSocket.connect(new InetSocketAddress(host, port));
                    dis = new DataInputStream(mSocket.getInputStream());
                    Log.d(TAG,"Connected");
                    String receive = "";
                    while (true)
                    {
                        receive = dis.readUTF();
                        if(receive != null)
                            break;
                    }

                    setNotificationMessage(receive);
                    Log.d(TAG,"Receive from server " + receive);
                    receive = null;

                } catch (UnknownHostException ignored) {
                    //setNotificationMessage("Unknown host");
                    Log.d(TAG,"Unknown host");
                    ignored.printStackTrace();
                } catch (IOException ignored) {
                    //setNotificationMessage("Disconnected");
                    Log.d(TAG,"Disconnected");
                    ignored.printStackTrace();
                    close();
                }

                try {
                    // Reconnect delay
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {
                    Log.d(TAG,"InterruptedException");
                    ignored.printStackTrace();
                }
            }
        } finally {
            // Will eventually call onDestroy()
            Log.d(TAG,"onDestroy");
            stopSelf();
        }
    }

    private void setNotificationMessage(CharSequence message) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.rpismallicon);
        builder.setContentTitle("Kapı çaldı!");
        builder.setContentText(message);
        NotificationManagerCompat nm = NotificationManagerCompat.from(this);
        nm.notify(NOTIFICATION_ID, builder.build());
    }


    private void close() {
        if (mInputStream != null) {
            try {
                mInputStream.close();
                mInputStream = null;
            } catch (IOException ignored) {
                Log.d(TAG,"IOException Inputstream.close()");
                ignored.printStackTrace();
            }
        }

        if (mSocket != null) {
            try {
                mSocket.close();
                mSocket = null;
            } catch (IOException ignored) {
                Log.d(TAG,"IOException socket.close()");
                ignored.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        if (mThread != null) {
            mRunning = false;

            close();

            while (true) {
                try {
                    mThread.interrupt();
                    mThread.join();
                    mThread = null;
                    break;
                } catch (InterruptedException ignored) {
                    Log.d(TAG,"InterruptedException Thread.interrupt(), Thread.join()");
                    ignored.printStackTrace();
                }
            }
        }

        //setNotificationMessage("Service destroyed");
        Log.d(TAG,"Service destroyed");
        super.onDestroy();
    }
}