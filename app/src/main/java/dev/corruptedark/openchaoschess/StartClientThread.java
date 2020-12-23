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
import android.content.res.Resources;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

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

        while (!multiPlayerService.hasNewMessage(callingActivity))
        {
            Log.v(TAG, "Waiting for message");

            try {
                Thread.sleep(500);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        Log.v(TAG, "Connected");
        //multiPlayerService.sendData("We good fam");

        String settingsString = multiPlayerService.getMostRecentData(callingActivity);

        String knightsOnlyString = settingsString.split(";")[0].split(":")[1];
        String bloodthirstyString = settingsString.split(";")[1].split(":")[1];

        boolean knightsOnly = Boolean.parseBoolean(knightsOnlyString);
        boolean bloodthirsty = Boolean.parseBoolean(bloodthirstyString);

        Toast.makeText(callingActivity, "Starting Service", Toast.LENGTH_LONG).show();

        GameConnectionHandler.getInstance().setMultiPlayerService(multiPlayerService, callingActivity, knightsOnly, bloodthirsty, false);
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
        Looper.prepare();
        adapter.cancelDiscovery();

        try {
            Log.v(TAG, "Trying to connect");
            socket.connect();
        }
        catch (IOException e) {
            e.printStackTrace();
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
