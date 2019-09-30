package dev.corruptedark.openchaoschess;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class PairedDeviceAdapter extends ArrayAdapter {

    private ArrayList<BluetoothDevice> devices;
    private int listResourceId;
    private int backgroundColor;
    private int textColor;

    public PairedDeviceAdapter(@NonNull Context context, int resource, @NonNull ArrayList<BluetoothDevice> devices, int background, int textColor)
    {
        super(context, resource, devices);

        this.devices = devices;
        listResourceId = resource;
        backgroundColor = background;
        this.textColor = textColor;
    }

    @Override
    public int getCount()
    {
        return super.getCount();
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(listResourceId, null);
        TextView nameView = view.findViewById(R.id.device_name_label);
        TextView addressView = view.findViewById(R.id.device_address_label);
        nameView.setText(devices.get(position).getName());
        addressView.setText(devices.get(position).getAddress());

        view.setBackgroundColor(backgroundColor);
        nameView.setTextColor(textColor);
        addressView.setTextColor(textColor);

        return view;
    }

    @Nullable
    @Override
    public BluetoothDevice getItem(int position) {
        return devices.get(position);
    }

}
