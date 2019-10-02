package dev.corruptedark.openchaoschess;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class StartClientActivity extends AppCompatActivity {

    LinearLayout clientLayout;
    TextView clientInstructionsLabel;
    TextView pairedDevicesLabel;
    ListView pairedDevicesListView;

    PairedDeviceAdapter deviceAdapter;

    ColorManager colorManager;

    GameConnectionHandler gameConnectionHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_client);

        colorManager = ColorManager.getInstance(this);

        clientLayout = findViewById(R.id.client_layout);
        clientInstructionsLabel = findViewById(R.id.client_instructions);
        pairedDevicesLabel = findViewById(R.id.paired_devices_label);
        pairedDevicesListView = findViewById(R.id.paired_device_list_view);

        clientLayout.setBackgroundColor(colorManager.getColorFromFile(ColorManager.BACKGROUND_COLOR));
        clientInstructionsLabel.setTextColor(colorManager.getColorFromFile(ColorManager.TEXT_COLOR));
        pairedDevicesLabel.setTextColor(colorManager.getColorFromFile(ColorManager.TEXT_COLOR));
        pairedDevicesListView.setBackgroundColor(colorManager.getColorFromFile(ColorManager.BACKGROUND_COLOR));

        gameConnectionHandler = new GameConnectionHandler();
        gameConnectionHandler.startBluetooth(this);

        pairedDevicesListView.setSelector(new ColorDrawable(colorManager.getColorFromFile(ColorManager.SELECTION_COLOR)));
        if(gameConnectionHandler.bluetoothIsOn())
        {
            ArrayList<BluetoothDevice> bluetoothDevices = gameConnectionHandler.getAndListPairedDevices(this);
            deviceAdapter = new PairedDeviceAdapter(this,R.layout.device_list_item,bluetoothDevices,colorManager.getColorFromFile(ColorManager.BACKGROUND_COLOR), colorManager.getColorFromFile(ColorManager.TEXT_COLOR));
            pairedDevicesListView.setAdapter(deviceAdapter);
            pairedDevicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    BluetoothDevice device = deviceAdapter.getItem(position);

                    connectToHost(device);
                }
            });
        }
    }


    public void connectToHost(BluetoothDevice device) {
        Toast.makeText(this, "Starting connection to host", Toast.LENGTH_SHORT).show();
        gameConnectionHandler.connectToHost(device);
    }

    public void stopClient()
    {
        Toast.makeText(this, "Stopping client", Toast.LENGTH_SHORT).show();
        gameConnectionHandler.stopClient();
    }

    @Override
    public void onBackPressed() {

        gameConnectionHandler.stopClient();

        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case GameConnectionHandler.REQUEST_ENABLE_BT:
                if(resultCode == Activity.RESULT_OK)
                {
                    ArrayList<BluetoothDevice> bluetoothDevices = gameConnectionHandler.getAndListPairedDevices(this);
                    deviceAdapter = new PairedDeviceAdapter(this,R.layout.device_list_item,bluetoothDevices,colorManager.getColorFromFile(ColorManager.BACKGROUND_COLOR), colorManager.getColorFromFile(ColorManager.TEXT_COLOR));
                    pairedDevicesListView.setAdapter(deviceAdapter);
                    pairedDevicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            BluetoothDevice device = deviceAdapter.getItem(position);

                            connectToHost(device);
                        }
                    });
                }
                else if (resultCode == Activity.RESULT_CANCELED)
                {
                    onBackPressed();
                }
                break;
        }
    }
}
