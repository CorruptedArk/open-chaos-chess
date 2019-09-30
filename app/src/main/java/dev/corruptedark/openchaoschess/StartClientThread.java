package dev.corruptedark.openchaoschess;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.UUID;

public class StartClientThread extends Thread {

    private final BluetoothSocket socket;
    private final String TAG = "Start Client Thread";
    private BluetoothAdapter adapter;
    private Activity callingActivity;

    private void manageConnectedSocket(BluetoothSocket socket)
    {
        MultiPlayerService multiPlayerService = new MultiPlayerService(socket);

        while (!multiPlayerService.hasNewMessage())
        {
            try {
                Thread.sleep(500);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        //multiPlayerService.sendData("We good fam");

        String knightsOnlyString = multiPlayerService.getMostRecentData();

        String response = knightsOnlyString.split(":")[1];

        boolean knightsOnly = Boolean.parseBoolean(response);

        GameConnectionHandler.setMultiPlayerService(multiPlayerService, callingActivity, knightsOnly, false);
    }

    StartClientThread(BluetoothDevice device, Activity callingActivity, BluetoothAdapter adapter) {
        this.adapter = adapter;
        this.callingActivity = callingActivity;
        BluetoothSocket tempSocket = null;

        Resources resources = callingActivity.getResources();

        String uuidString = resources.getString(R.string.BT_UUID);
        String cleanUuidString = uuidString.replace("–","");
        UUID uuid = new UUID(
                new BigInteger(cleanUuidString.substring(0, 16), 16).longValue(),
                new BigInteger(cleanUuidString.substring(16), 16).longValue());

        try {
            tempSocket = device.createRfcommSocketToServiceRecord(uuid);
        }
        catch (IOException e) {
            Log.e(TAG, "Create failed",e);
        }

        socket = tempSocket;
    }

    public void run() {
        adapter.cancelDiscovery();

        try {
            socket.connect();
        }
        catch (IOException e) {
            cancel();
            return;
        }

        manageConnectedSocket(socket);
    }

    public void cancel() {
        try {
            socket.close();
        }
        catch (IOException e) {
            Log.e(TAG, "Close socket failed",e);
        }
    }
}
