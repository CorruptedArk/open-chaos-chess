package dev.corruptedark.openchaoschess;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import java.io.IOException;
import java.util.Calendar;
import java.util.UUID;

public class StartClientThread extends Thread {

    private final BluetoothSocket socket;
    private final BluetoothDevice hostDevice;
    private final String TAG = "Start Client Thread";
    private BluetoothAdapter adapter;
    private Activity callingActivity;

    private void manageConnectedSocket(BluetoothSocket socket)
    {
        MultiPlayerService multiPlayerService = new MultiPlayerService(socket);

        while (!multiPlayerService.hasNewMessage());

        String knightsOnlyString = multiPlayerService.getMostRecentData();

        String response = knightsOnlyString.split(":")[1];

        boolean knightsOnly = Boolean.parseBoolean(response);

        GameConnectionHandler.setMultiPlayerService(multiPlayerService, callingActivity, knightsOnly);
    }

    StartClientThread(BluetoothDevice device, Activity callingActivity, BluetoothAdapter adapter) {
        this.adapter = adapter;
        this.callingActivity = callingActivity;
        BluetoothSocket tempSocket = null;
        hostDevice = device;
        Resources resources = callingActivity.getResources();

        UUID uuid = UUID.fromString(resources.getString(R.string.BT_UUID));

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
