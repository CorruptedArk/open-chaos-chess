package com.openchaoschess.openchaoschess;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pes.androidmaterialcolorpickerdialog.ColorPicker;
import com.pes.androidmaterialcolorpickerdialog.ColorPickerCallback;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class SettingsActivity extends AppCompatActivity {
    TextView backgroundColorLabel;
    TextView barColorLabel;
    TextView secondaryColorLabel;
    TextView boardColor1Label;
    TextView boardColor2Label;
    TextView pieceColorLabel;
    TextView selectionColorLabel;
    TextView textColorLabel;
    ImageButton backgroundColorButton;
    ImageButton barColorButton;
    ImageButton secondaryColorButton;
    ImageButton boardColor1Button;
    ImageButton boardColor2Button;
    ImageButton pieceColorButton;
    ImageButton selectionColorButton;
    ImageButton textColorButton;

    Button setDefaultsButton;
    Button saveColorButton;
    ColorPicker colorPicker;
    RelativeLayout layout;

    File settingsFile;
    InputStream fileReader;
    OutputStream fileWriter;
    byte[] bytes;
    String[] contentArray;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        backgroundColorLabel = (TextView)findViewById(R.id.background_color_label);
        barColorLabel = (TextView)findViewById(R.id.bar_color_label);
        secondaryColorLabel = (TextView)findViewById(R.id.secondary_color_label);
        boardColor1Label = (TextView)findViewById(R.id.board_color1_label);
        boardColor2Label = (TextView)findViewById(R.id.board_color2_label);
        pieceColorLabel = (TextView)findViewById(R.id.piece_color_label);
        selectionColorLabel = (TextView)findViewById(R.id.selection_color_label);
        textColorLabel = (TextView)findViewById(R.id.text_color_label);

        backgroundColorButton = (ImageButton)findViewById(R.id.background_color_button);
        barColorButton = (ImageButton)findViewById(R.id.bar_color_button);
        secondaryColorButton = (ImageButton)findViewById(R.id.secondary_color_button);
        boardColor1Button = (ImageButton)findViewById(R.id.board_color1_button);
        boardColor2Button = (ImageButton)findViewById(R.id.board_color2_button);
        pieceColorButton = (ImageButton)findViewById(R.id.piece_color_button);
        selectionColorButton = (ImageButton)findViewById(R.id.selection_color_button);
        textColorButton = (ImageButton)findViewById(R.id.text_color_button);

        setDefaultsButton = (Button)findViewById(R.id.set_defaults_button);
        saveColorButton = (Button)findViewById(R.id.save_color_button);
        layout = (RelativeLayout) findViewById(R.id.settings_layout);


        settingsFile = new File(getApplicationContext().getFilesDir(),"settings.txt");
        if(settingsFile.exists()) {
            //Toast.makeText(this,"File does exist",Toast.LENGTH_SHORT).show();
            try{
                fileReader = new FileInputStream(settingsFile);
                bytes= new byte[(int)settingsFile.length()];
                fileReader.read(bytes);
                fileReader.close();
                String contents = new String(bytes);
                //Toast.makeText(this,contents,Toast.LENGTH_LONG).show();
                contentArray = contents.split(" ");
                backgroundColorButton.setBackgroundColor(Color.parseColor("#"+contentArray[0]));
                barColorButton.setBackgroundColor(Color.parseColor("#"+contentArray[1]));
                secondaryColorButton.setBackgroundColor(Color.parseColor("#"+contentArray[2]));
                boardColor1Button.setBackgroundColor(Color.parseColor("#"+contentArray[3]));
                boardColor2Button.setBackgroundColor(Color.parseColor("#"+contentArray[4]));
                pieceColorButton.setBackgroundColor(Color.parseColor("#"+contentArray[5]));
                selectionColorButton.setBackgroundColor(Color.parseColor("#"+contentArray[6]));
                textColorButton.setBackgroundColor(Color.parseColor("#"+contentArray[7]));
            }
            catch (Exception e){
                e.printStackTrace();
            }

        }
        else {
            //Toast.makeText(this,"File did not exist",Toast.LENGTH_SHORT).show();
            try {
                fileWriter = new FileOutputStream(settingsFile,false);
                String contents = "FF303030 FF454545 FF696969 FF800000 FF000000 FFFFFFFF FF888888 FFFFFFFF";
                contentArray = contents.split(" ");
                fileWriter.write(contents.getBytes());
                fileWriter.close();
            }
            catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        layout.setBackgroundColor(Color.parseColor("#"+contentArray[0]));
        backgroundColorLabel.setTextColor(Color.parseColor("#"+contentArray[7]));
        barColorLabel.setTextColor(Color.parseColor("#"+contentArray[7]));
        secondaryColorLabel.setTextColor(Color.parseColor("#"+contentArray[7]));
        boardColor1Label.setTextColor(Color.parseColor("#"+contentArray[7]));
        boardColor2Label.setTextColor(Color.parseColor("#"+contentArray[7]));
        pieceColorLabel.setTextColor(Color.parseColor("#"+contentArray[7]));
        selectionColorLabel.setTextColor(Color.parseColor("#"+contentArray[7]));
        textColorLabel.setTextColor(Color.parseColor("#"+contentArray[7]));

        setDefaultsButton.setTextColor(Color.parseColor("#"+contentArray[7]));
        setDefaultsButton.setBackgroundColor(Color.parseColor("#"+contentArray[3]));
        saveColorButton.setTextColor(Color.parseColor("#"+contentArray[7]));
        saveColorButton.setBackgroundColor(Color.parseColor("#"+contentArray[3]));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#"+contentArray[1]));
        }
    }

    public void colorBoxClicked(View view) {
        final ImageButton selected = (ImageButton)view;
        int startColor = ((ColorDrawable)selected.getBackground()).getColor();
        colorPicker = new ColorPicker(SettingsActivity.this, Color.red(startColor),Color.green(startColor),Color.blue(startColor));
        colorPicker.setCallback(new ColorPickerCallback() {
            @Override
            public void onColorChosen(@ColorInt int color) {
                selected.setBackgroundColor(color);
                colorPicker.dismiss();
            }
        });
        colorPicker.show();
    }

    public void setDefaultsButtonClicked(View view) {
        backgroundColorButton.setBackgroundColor(0xFF303030);
        barColorButton.setBackgroundColor(0xFF454545);
        secondaryColorButton.setBackgroundColor(0xFF696969);
        boardColor1Button.setBackgroundColor(0xFF800000);
        boardColor2Button.setBackgroundColor(0xFF000000);
        pieceColorButton.setBackgroundColor(0xFFFFFFFF);
        selectionColorButton.setBackgroundColor(0xFF888888);
        textColorButton.setBackgroundColor(0xFFFFFFFF);
    }

    public void saveColorsButtonClicked(View view) {
        String contents = String.format("%s %s %s %s %s %s %s %s",getColorString(backgroundColorButton), getColorString(barColorButton), getColorString(secondaryColorButton), getColorString(boardColor1Button), getColorString(boardColor2Button), getColorString(pieceColorButton), getColorString(selectionColorButton), getColorString(textColorButton));
        contentArray = contents.split(" ");
        try{
            fileWriter = new FileOutputStream(settingsFile,false);
            fileWriter.write(contents.getBytes());
            fileWriter.close();
            Toast.makeText(this,"Colors saved",Toast.LENGTH_SHORT).show();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        layout.setBackgroundColor(Color.parseColor("#"+contentArray[0]));
        backgroundColorLabel.setTextColor(Color.parseColor("#"+contentArray[7]));
        barColorLabel.setTextColor(Color.parseColor("#"+contentArray[7]));
        secondaryColorLabel.setTextColor(Color.parseColor("#"+contentArray[7]));
        boardColor1Label.setTextColor(Color.parseColor("#"+contentArray[7]));
        boardColor2Label.setTextColor(Color.parseColor("#"+contentArray[7]));
        pieceColorLabel.setTextColor(Color.parseColor("#"+contentArray[7]));
        selectionColorLabel.setTextColor(Color.parseColor("#"+contentArray[7]));
        textColorLabel.setTextColor(Color.parseColor("#"+contentArray[7]));
        setDefaultsButton.setTextColor(Color.parseColor("#"+contentArray[7]));
        setDefaultsButton.setBackgroundColor(Color.parseColor("#"+contentArray[3]));
        saveColorButton.setTextColor(Color.parseColor("#"+contentArray[7]));
        saveColorButton.setBackgroundColor(Color.parseColor("#"+contentArray[3]));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#"+contentArray[1]));
        }
        layout.invalidate();
    }

    public String getColorString(View view){
        return Integer.toHexString(((ColorDrawable)view.getBackground()).getColor());
    }

    /*public int getColorInt(View view) {
       return ((ColorDrawable)view.getBackground()).getColor();
    }*/
}

