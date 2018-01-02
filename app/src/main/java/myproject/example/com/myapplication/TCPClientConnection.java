package myproject.example.com.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.SystemClock;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;


public class TCPClientConnection implements Serializable {

    private static final String TAG = "TCPClientConnection";
    private static final long serialversionUID = 1L;

    private transient Socket socket;
    private boolean connected;
    private String host;
    private int port;
    private String command;
    private String receive;
    private ImageView imageView;
    private transient BufferedReader input;
    private transient DataOutputStream output;


    public TCPClientConnection(String host, int port, String command,ImageView imageView) {

        this.command = command;
        this.host = host;
        this.port = port;
        this.imageView = imageView;
        this.connected = false;
        new Thread(new ThreadConnection(this.socket,this.host, this.port, this.command)).start();

    }
    private class ThreadConnection implements Runnable{

        private transient Socket socketT;
        private String hostT;
        private int portT;
        private String commandT;


        private ThreadConnection(Socket socket, String host, int port,String command)
        {
            this.socketT = socket;
            this.hostT = host;
            this.portT = port;
            this.commandT = command;
        }


        @Override
        public void run(){

            try
            {

                Log.d(TAG, "Connecting for the '" + commandT + "'");
                socketT = new Socket(InetAddress.getByName(hostT), portT);

                socket = socketT;

                Log.d(TAG, "Trying to serialize the following object: " + socket + "\n");
                Serializer.serialize(socket);
                Log.d(TAG, "The " + socket + " object was successfully serialized!\n");

                output = new DataOutputStream(socketT.getOutputStream());
                Log.d(TAG, "Trying to serialize the following object: " + output + "\n");
                Serializer.serialize(output);
                Log.d(TAG, "The " + output + " object was successfully serialized!\n");

                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                Log.d(TAG, "Trying to serialize the following object: " + input + "\n");
                Serializer.serialize(input);
                Log.d(TAG, "The " + input + " object was successfully serialized!\n");

                Log.d(TAG, InetAddress.getByName(hostT)+":"+portT+" ---> Connected");
                Log.d(TAG, "The new command is " + commandT);


                sendCommandAndReceive(commandT);



            } catch (UnknownHostException e) {
                Log.e(TAG, " UnknownHostException Error", e);
                e.printStackTrace();
                connected = false;
            } catch (IOException e) {
                Log.e(TAG, "IOException Error", e);
                e.printStackTrace();
                connected = false;
            }catch (Exception e) {
                Log.e(TAG, "IOException Error", e);
                e.printStackTrace();
                connected = false;
            }

        }

    }
     // write to server
    private void write(DataOutputStream output, String message) {


        try {
            byte [] b = message.getBytes(Charset.forName("UTF-8"));
            Log.d(TAG, "LEN -> "+ b.length);
            output.write(b);
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "is sending command -> "+ message);
    }
    // read from server
    private String read(BufferedReader input, String message)
    {
        try {
            while((message = input.readLine()) == null);
            Log.d(TAG, "'"+message +"' is received");
        }catch (IOException e) {
            e.printStackTrace();
        }

        return message;

    }
    // send command and receive image or syncnaziation
    public void sendCommandAndReceive(String command)
    {
        if(command.equals("T")) {

            write(output,command);

            /*receive = read(input,receive);

            if(receive.equals("c"))
                connected = true;
            else
                connected = false;
            */

            connected = true;
        } else {
            if(command.equals("l") || command.equals("u") ||
               command.equals("e") || command.equals("i")) {

                write(output,command);
                if(command.equals("i"))
                    receiveImage(imageView);
            }


        }
        try {

            input.close();
            output.close();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    private void receiveImage(ImageView imageView)
    {

        Log.d(TAG, "receive image");
        try {
            DataInputStream dIn = new DataInputStream(socket.getInputStream());

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            byte[] message = new byte[dIn.readInt()];
            Log.d(TAG,String.valueOf(message.length));
            dIn.readFully(message);
            baos.write(message, 0, message.length);
            byte[] buffer = baos.toByteArray();
            Bitmap bitmap = BitmapFactory.decodeByteArray(buffer, 0, buffer.length);
            imageView.setImageBitmap(bitmap);
            imageView.invalidate();// attempt to refresh the canvas in image view
            SystemClock.sleep(2000);


        } catch (IOException e) {

            Log.e(TAG,"bytesRead = ois.read(picture, 0, picture.length); Error",e);

            e.printStackTrace();
        }



    }

    public void setCommand(String command)
    {
        this.command = command;
    }
    public ImageView getImageView()
    {
        return imageView;
    }

    public boolean isConnected()
    {
        return connected;
    }

    public void setConnected(boolean connected)
    {
        this.connected = connected;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String newHost) {
        this.host = newHost;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int newPort) {
        this.port = newPort;
    }
}