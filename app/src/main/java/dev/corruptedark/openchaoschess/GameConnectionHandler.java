package dev.corruptedark.openchaoschess;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class GameConnectionHandler {

    public static String IS_HOST_KEY = "isHost";

    private BluetoothAdapter adapter;
    private boolean bluetoothSupported;
    public static final int REQUEST_ENABLE_BT = 69;
    private Activity callingActivity;

    private boolean isHost = false;

    private static MultiPlayerService multiPlayerService = null;

    /**
     * Opens a request to start bluetooth if bluetooth is supported.
     * @param callingActivity The activity to start Bluetooth from.
     *
     * Call getAndListPairedDevices() in the onActivityResult() in the same activity with requestCode == GameConnectionHandler.REQUEST_ENABLE_BT.
     * Otherwise, it will behave unpredictably.
     */
    public void startBluetooth(Activity callingActivity)
    {
        this.callingActivity = callingActivity;
        adapter = BluetoothAdapter.getDefaultAdapter();

        if (adapter == null)
        {
            Toast.makeText(callingActivity,"Sorry, your device doesn't support Bluetooth.",Toast.LENGTH_LONG).show();
            bluetoothSupported = false;
        }
        else
        {
            bluetoothSupported = true;
            if(!adapter.isEnabled())
            {
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                callingActivity.startActivityForResult(enableIntent,REQUEST_ENABLE_BT);
            }
        }

    }

    /**
     * Gets and returns a list of paired devices
     * @param callingActivity - The activity this method is called from.
     *
     * Should be called in the onActivityResult() of the activity that called startBluetooth()
     */
    public ArrayList<BluetoothDevice> getAndListPairedDevices(Activity callingActivity)
    {
        ArrayList<BluetoothDevice> devices = new ArrayList<>();

        if(adapter == null || !adapter.isEnabled())
        {
            startBluetooth(callingActivity);
        }
        else
        {
            Set<BluetoothDevice> pairedDevices = adapter.getBondedDevices();

            /*String uuidString = callingActivity.getString(R.string.BT_UUID);
            String cleanUuidString = uuidString.replace("–","");
            UUID uuid = new UUID(
                    new BigInteger(cleanUuidString.substring(0, 16), 16).longValue(),
                    new BigInteger(cleanUuidString.substring(16), 16).longValue());

            for(BluetoothDevice device:pairedDevices)
            {
                boolean hasUuid = false;


                for(int i = 0; i < device.getUuids().length && !hasUuid; i++)
                {
                   hasUuid = device.getUuids()[i].getUuid().equals(uuid);

                   if(hasUuid)
                       devices.add(device);
                }
            }*/

            devices.addAll(pairedDevices);
        }

        return devices;
    }

    public void connectToHost(BluetoothDevice device)
    {
        isHost = false;
        StartClientThread startClientThread = new StartClientThread(device, callingActivity, adapter);
        startClientThread.start();
    }

    public void startHost(boolean knightsOnly)
    {
        isHost = true;
        StartHostThread startHostThread = new StartHostThread(callingActivity, adapter, knightsOnly);
        startHostThread.start();
    }

    public static void setMultiPlayerService(MultiPlayerService service, Activity callingActivity, boolean knightsOnly, boolean isHost)
    {
        multiPlayerService = service;

        Intent intent = new Intent(callingActivity, MultiPlayerBoard.class);

        intent.putExtra("knightsOnly", knightsOnly);

        intent.putExtra("isHost", isHost);

        callingActivity.startActivity(intent);
    }

    public boolean bluetoothIsOn()
    {
        return adapter != null && adapter.isEnabled();
    }

    public static MultiPlayerService getMultiPlayerService()
    {
        return multiPlayerService;
    }

}
