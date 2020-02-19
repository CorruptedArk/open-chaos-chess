/*
 * Open Chaos Chess is a free as in speech version of Chaos Chess
 * Chaos Chess is a chess game where you control the piece that moves, but not how it moves
 *     Copyright (C) 2019  Noah Stanford <noahstandingford@gmail.com>
 *
 *     Open Chaos Chess is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Open Chaos Chess is distributed in the hope that it will be fun,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package dev.corruptedark.openchaoschess;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MultiPlayerService {

    private interface Constants {
        public static final int READ = 0;
        public static final int WRITE = 1;
        public static final int ERROR = 2;
        public static final String ERROR_KEY = "error";
        public static final String SEND_FAILED = "send_failed";
    }

    private final String TAG = "Multi Player Service";
    private final ServiceHandler handler;
    private ConnectedThread connectedThread;
    private Activity lastCallingActivity;

    public MultiPlayerService(BluetoothSocket connectedSocket) {
        handler = new ServiceHandler();
        connectedThread = new ConnectedThread(connectedSocket);
        connectedThread.start();
    }

    public synchronized void sendData(Activity sendingActivity, String data)
    {
        lastCallingActivity = sendingActivity;
        byte[] bytes = data.getBytes();

        connectedThread.write(bytes);
    }

    public synchronized String getMostRecentData(Activity callingActivity)
    {
        lastCallingActivity = callingActivity;
        return handler.getLastReceived();
    }

    public synchronized boolean hasNewMessage(Activity callingActivity)
    {
        lastCallingActivity = callingActivity;
        return handler.hasNewMessage();
    }

    public synchronized String getLastSent() {return handler.getLastSent();}

    public synchronized boolean hasNewError()
    {
        return handler.hasNewError();
    }

    public synchronized void cancel(){
        connectedThread.cancel();
    }

    static class ServiceHandler extends Handler {
        private volatile String lastSent = null;
        private volatile String lastReceived = null;
        private volatile String lastError = null;
        private volatile boolean newMessage = false;
        private volatile boolean newError = false;

        public synchronized String getLastSent() {
            return lastSent;
        }

        public synchronized String getLastReceived() {
            newMessage = false;
            return lastReceived;
        }

        public synchronized String getLastError() {
            newError = false;
            return lastError;
        }

        public synchronized boolean hasNewMessage() {
            return newMessage;
        }

        public synchronized boolean hasNewError() {
            return newError;
        }

        @Override
        public void handleMessage(Message message) {

            switch (message.what) {
                case Constants.READ:
                    byte[] readBuffer = (byte[]) message.obj;

                    lastReceived = new String(readBuffer, 0, message.arg1);
                    newMessage = true;
                    break;
                case Constants.WRITE:
                    byte[] writeBuffer = (byte[]) message.obj;

                    if(writeBuffer != null)
                        lastSent = new String(writeBuffer);
                    break;
                case Constants.ERROR:
                    lastError = message.getData().getString(Constants.ERROR_KEY);
                    newError = true;
                    break;

            }

        }
    }

    private class ConnectedThread extends Thread {
        private final int BUFFER_SIZE = 1024;
        private final BluetoothSocket connectedSocket;
        private final InputStream inStream;
        private final OutputStream outStream;
        private volatile byte[] inBuffer;
        private volatile byte[] outBuffer;

        public ConnectedThread(BluetoothSocket socket) {
            connectedSocket = socket;
            InputStream tempInput = null;
            OutputStream tempOut = null;

            try {
                tempInput = socket.getInputStream();
            }
            catch (IOException e)
            {
                Log.e(TAG,"Failed to create input stream",e);
            }

            try {
                tempOut = socket.getOutputStream();
            }
            catch (IOException e) {
                Log.e(TAG, "Failed to create output stream",e);
            }

            inStream = tempInput;
            outStream = tempOut;
        }

        public void run() {
            Looper.prepare();
            inBuffer = new byte[BUFFER_SIZE];
            //outBuffer = new byte[BUFFER_SIZE];

            int inBytesReturned;
            //int outBytesReturned;

            while (true) {
                try {
                    inBytesReturned = inStream.read(inBuffer);

                    Message inMessage = handler.obtainMessage(Constants.READ, inBytesReturned, -1, inBuffer);
                    inMessage.sendToTarget();
                    handler.handleMessage(inMessage);
                }
                catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Input stream disconnected " + lastCallingActivity.getLocalClassName(), e);

                    lastCallingActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(lastCallingActivity.getApplicationContext(),"Connection ended", Toast.LENGTH_LONG).show();
                            if(lastCallingActivity instanceof MultiPlayerBoard)
                            {
                                lastCallingActivity.finish();
                            }
                        }
                    });

                    break;
                }
            }
        }

        public synchronized void write(byte[] bytes) {
            try {

                outStream.write(bytes);

                Message outMessage = handler.obtainMessage(Constants.WRITE, -1, -1, bytes);
                outMessage.sendToTarget();
                handler.handleMessage(outMessage);
            }
            catch (IOException e) {
                Log.e(TAG, "Sending data failed", e);

                Message errorMessage = handler.obtainMessage(Constants.ERROR);
                Bundle bundle = new Bundle();
                bundle.putString(Constants.ERROR_KEY, Constants.SEND_FAILED);
                errorMessage.setData(bundle);
                handler.sendMessage(errorMessage);

                lastCallingActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(lastCallingActivity.getApplicationContext(),"Connection ended", Toast.LENGTH_LONG).show();
                        if(lastCallingActivity instanceof MultiPlayerBoard)
                        {
                            lastCallingActivity.finish();
                        }
                    }
                });

            }
        }

        public void cancel() {
            try {
                connectedSocket.close();
            }
            catch (IOException e) {
                Log.e(TAG, "Failed to close socket", e);
            }
        }


    }

}
