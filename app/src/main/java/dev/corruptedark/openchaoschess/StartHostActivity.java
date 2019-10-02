package dev.corruptedark.openchaoschess;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.core.widget.CompoundButtonCompat;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class StartHostActivity extends AppCompatActivity {

    private LinearLayout hostLayout;
    private TextView hostInstructionLabel;
    private TextView hostStatusLabel;
    private AppCompatCheckBox knightsOnlyCheckBox;
    private Button startHostButton;
    private Button stopHostButton;

    private ColorManager colorManager;

    GameConnectionHandler gameConnectionHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_host);

        hostLayout = findViewById(R.id.host_layout);
        hostInstructionLabel = findViewById(R.id.host_instructions);
        hostStatusLabel = findViewById(R.id.host_status);
        knightsOnlyCheckBox = findViewById(R.id.knights_only_checkbox);
        startHostButton = findViewById(R.id.start_host_button);
        stopHostButton = findViewById(R.id.stop_host_button);

        colorManager = ColorManager.getInstance(this);

        hostLayout.setBackgroundColor(colorManager.getColorFromFile(ColorManager.BACKGROUND_COLOR));
        hostInstructionLabel.setTextColor(colorManager.getColorFromFile(ColorManager.TEXT_COLOR));
        hostStatusLabel.setTextColor(colorManager.getColorFromFile(ColorManager.TEXT_COLOR));
        knightsOnlyCheckBox.setTextColor(colorManager.getColorFromFile(ColorManager.TEXT_COLOR));

        ColorStateList colorStateList = new ColorStateList(
                new int[][] {
                        new int[] { -android.R.attr.state_checked }, // unchecked
                        new int[] {  android.R.attr.state_checked }  // checked
                },
                new int[] {
                        colorManager.getColorFromFile(ColorManager.TEXT_COLOR),
                        colorManager.getColorFromFile(ColorManager.TEXT_COLOR)
                }
        );

        CompoundButtonCompat.setButtonTintList(knightsOnlyCheckBox, colorStateList);

        startHostButton.setBackgroundColor(colorManager.getColorFromFile(ColorManager.BOARD_COLOR_1));
        stopHostButton.setTextColor(colorManager.getColorFromFile(ColorManager.TEXT_COLOR));
        stopHostButton.setBackgroundColor(colorManager.getColorFromFile(ColorManager.BOARD_COLOR_1));
        stopHostButton.setTextColor(colorManager.getColorFromFile(ColorManager.TEXT_COLOR));

        gameConnectionHandler = new GameConnectionHandler();
        gameConnectionHandler.startBluetooth(this);
        hostStatusLabel.setText(getString(R.string.host_status) + " " + gameConnectionHandler.getHostStatus());
    }

    public void startHosting(View view)
    {
        Toast.makeText(this,"Starting host", Toast.LENGTH_SHORT).show();
        gameConnectionHandler.startHost(knightsOnlyCheckBox.isChecked());
        hostStatusLabel.setText(getString(R.string.host_status) + " " + gameConnectionHandler.getHostStatus());
    }


    public void stopHosting(View view)
    {
        Toast.makeText(this,"Stopping host", Toast.LENGTH_SHORT).show();
        gameConnectionHandler.stopHost();
        hostStatusLabel.setText(getString(R.string.host_status) + " " + gameConnectionHandler.getHostStatus());
    }

    @Override
    protected void onResume() {
        super.onResume();

        hostStatusLabel.setText(getString(R.string.host_status) + " " + gameConnectionHandler.getHostStatus());
    }

    @Override
    public void onBackPressed() {
        stopHosting(hostLayout);

        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case GameConnectionHandler.REQUEST_ENABLE_BT:
                if(resultCode == Activity.RESULT_CANCELED)
                    onBackPressed();
                break;
        }
    }
}
