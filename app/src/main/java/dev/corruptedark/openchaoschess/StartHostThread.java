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
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.res.Resources;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

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
    private final boolean bloodthirsty;

    private void manageConnectedSocket(BluetoothSocket socket)
    {
        MultiPlayerService multiPlayerService = new MultiPlayerService(socket);

        /*while(!multiPlayerService.hasNewMessage())
        {
            Log.v(TAG, "Waiting for message");
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

        multiPlayerService.sendData(callingActivity,"knightsOnly:" + knightsOnly + ";bloodthirsty:" + bloodthirsty);

        Toast.makeText(callingActivity, "Starting Service", Toast.LENGTH_LONG).show();

        GameConnectionHandler.getInstance().setMultiPlayerService(multiPlayerService, callingActivity, knightsOnly, bloodthirsty,true);
    }

    public StartHostThread(Activity callingActivity, BluetoothAdapter adapter, boolean knightsOnly, boolean bloodthirsty)
    {
        this.knightsOnly = knightsOnly;
        this.bloodthirsty = bloodthirsty;

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
        Looper.prepare();
        BluetoothSocket socket = null;

        while(true) {
            try {
                Log.v(TAG, "Trying to connect");
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
