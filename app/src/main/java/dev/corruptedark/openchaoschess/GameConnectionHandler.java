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


    public final String RUNNING = "running";
    public final String NOT_RUNNING = "not running";

    private static MultiPlayerService multiPlayerService = null;

    private StartHostThread startHostThread;
    private StartClientThread startClientThread;

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
        if(startClientThread == null || !startClientThread.isAlive()) {
            startClientThread = new StartClientThread(device, callingActivity, adapter);
            startClientThread.start();
        }
        else
        {
            startClientThread.cancel();
            startClientThread = new StartClientThread(device, callingActivity, adapter);
            startClientThread.start();
        }
    }

    public void startHost(boolean knightsOnly)
    {
        if(startHostThread == null || !startHostThread.isAlive()) {
            startHostThread = new StartHostThread(callingActivity, adapter, knightsOnly);
            startHostThread.start();
        }
    }

    public void stopHost()
    {
        if(startHostThread != null)
        {
            startHostThread.cancel();
        }
    }

    public void stopClient()
    {
        if(startClientThread != null){
            startClientThread.cancel();
        }
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

    public String getHostStatus()
    {
        String status;

        if(startHostThread != null && startHostThread.isAlive())
        {
            status = RUNNING;
        }
        else
        {
            status = NOT_RUNNING;
        }

        return status;
    }


    public static MultiPlayerService getMultiPlayerService(Activity callingActivity)
    {
        multiPlayerService.hasNewMessage(callingActivity);
        return multiPlayerService;
    }



}
