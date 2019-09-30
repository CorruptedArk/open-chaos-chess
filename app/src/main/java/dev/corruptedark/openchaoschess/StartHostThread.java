package dev.corruptedark.openchaoschess;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.attribute.AttributeView;
import java.util.UUID;

public class StartHostThread extends Thread {

    private final BluetoothServerSocket serverSocket;
    private final String NAME = "Chaos Chess";
    private final String TAG = "Start Host Thread";
    private Activity callingActivity;
    private final boolean knightsOnly;

    private void manageConnectedSocket(BluetoothSocket socket)
    {
        MultiPlayerService multiPlayerService = new MultiPlayerService(socket);

        /*while(!multiPlayerService.hasNewMessage())
        {
            multiPlayerService.sendData("knightsOnly:" + String.valueOf(knightsOnly));
            try
            {
                Thread.sleep(500);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        }
        multiPlayerService.getMostRecentData();*/

        multiPlayerService.sendData("knightsOnly:" + String.valueOf(knightsOnly));

        GameConnectionHandler.setMultiPlayerService(multiPlayerService, callingActivity, knightsOnly, true);
    }

    public StartHostThread(Activity callingActivity, BluetoothAdapter adapter, boolean knightsOnly)
    {
        this.knightsOnly = knightsOnly;

        this.callingActivity = callingActivity;

        Resources resources = callingActivity.getResources();

        String uuidString = resources.getString(R.string.BT_UUID);
        String cleanUuidString = uuidString.replace("–","");
        UUID uuid = new UUID(
                new BigInteger(cleanUuidString.substring(0, 16), 16).longValue(),
                new BigInteger(cleanUuidString.substring(16), 16).longValue());

        BluetoothServerSocket tempSocket = null;
        try {
            tempSocket = adapter.listenUsingRfcommWithServiceRecord(NAME, uuid);
        }
        catch (IOException e)
        {
            Log.e(TAG, "Listen failed", e);
        }

        serverSocket = tempSocket;
    }

    public void run() {
        BluetoothSocket socket = null;

        while(true) {
            try {
                socket = serverSocket.accept();
            }
            catch (IOException e) {
                Log.e(TAG,"Accept failed", e);
                break;
            }

            if (socket != null) {
                manageConnectedSocket(socket);
                cancel();
                break;
            }
        }
    }

    public void cancel() {
        try {
           serverSocket.close();
        }
        catch (IOException e) {
            Log.e(TAG, "Close socket failed", e);
        }
    }
}
