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
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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
import java.util.ArrayList;

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

    Toolbar toolbar;


    ColorManager colorManager;

    private enum Requests{IMPORT, EXPORT}


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

        colorManager = ColorManager.getInstance(this);

        toolbar = (Toolbar) findViewById(R.id.settings_toolbar);
        toolbar.setTitle("Settings");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        colorManager = ColorManager.getInstance(this);

        toolbar.setTitleTextColor(colorManager.getColorFromFile(ColorManager.TEXT_COLOR));
        toolbar.setBackgroundColor(colorManager.getColorFromFile(ColorManager.SECONDARY_COLOR));

        backgroundColorButton.setBackgroundColor(colorManager.getColorFromFile(ColorManager.BACKGROUND_COLOR));
        barColorButton.setBackgroundColor(colorManager.getColorFromFile(ColorManager.BAR_COLOR));
        secondaryColorButton.setBackgroundColor(colorManager.getColorFromFile(ColorManager.SECONDARY_COLOR));
        boardColor1Button.setBackgroundColor(colorManager.getColorFromFile(ColorManager.BOARD_COLOR_1));
        boardColor2Button.setBackgroundColor(colorManager.getColorFromFile(ColorManager.BOARD_COLOR_2));
        pieceColorButton.setBackgroundColor(colorManager.getColorFromFile(ColorManager.PIECE_COLOR));
        selectionColorButton.setBackgroundColor(colorManager.getColorFromFile(ColorManager.SELECTION_COLOR));
        textColorButton.setBackgroundColor(colorManager.getColorFromFile(ColorManager.TEXT_COLOR));

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        layout.setBackgroundColor(colorManager.getColorFromFile(ColorManager.BACKGROUND_COLOR));
        backgroundColorLabel.setTextColor(colorManager.getColorFromFile(ColorManager.TEXT_COLOR));
        barColorLabel.setTextColor(colorManager.getColorFromFile(ColorManager.TEXT_COLOR));
        secondaryColorLabel.setTextColor(colorManager.getColorFromFile(ColorManager.TEXT_COLOR));
        boardColor1Label.setTextColor(colorManager.getColorFromFile(ColorManager.TEXT_COLOR));
        boardColor2Label.setTextColor(colorManager.getColorFromFile(ColorManager.TEXT_COLOR));
        pieceColorLabel.setTextColor(colorManager.getColorFromFile(ColorManager.TEXT_COLOR));
        selectionColorLabel.setTextColor(colorManager.getColorFromFile(ColorManager.TEXT_COLOR));
        textColorLabel.setTextColor(colorManager.getColorFromFile(ColorManager.TEXT_COLOR));
        setDefaultsButton.setTextColor(colorManager.getColorFromFile(ColorManager.TEXT_COLOR));
        setDefaultsButton.setBackgroundColor(colorManager.getColorFromFile(ColorManager.BOARD_COLOR_1));
        saveColorButton.setTextColor(colorManager.getColorFromFile(ColorManager.TEXT_COLOR));
        saveColorButton.setBackgroundColor(colorManager.getColorFromFile(ColorManager.BOARD_COLOR_1));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(colorManager.getColorFromFile(ColorManager.BAR_COLOR));
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
        colorManager.updateColor(ColorManager.BACKGROUND_COLOR, getColorInt(backgroundColorButton));
        colorManager.updateColor(ColorManager.BAR_COLOR, getColorInt(barColorButton));
        colorManager.updateColor(ColorManager.SECONDARY_COLOR, getColorInt(secondaryColorButton));
        colorManager.updateColor(ColorManager.BOARD_COLOR_1, getColorInt(boardColor1Button));
        colorManager.updateColor(ColorManager.BOARD_COLOR_2, getColorInt(boardColor2Button));
        colorManager.updateColor(ColorManager.PIECE_COLOR, getColorInt(pieceColorButton));
        colorManager.updateColor(ColorManager.TEXT_COLOR, getColorInt(textColorButton));

        if(colorManager.saveChangesToFile())
        {
            Toast.makeText(this, "Colors saved", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this, "Colors failed to save", Toast.LENGTH_SHORT).show();
        }

        layout.setBackgroundColor(colorManager.getColorFromFile(ColorManager.BACKGROUND_COLOR));
        backgroundColorLabel.setTextColor(colorManager.getColorFromFile(ColorManager.TEXT_COLOR));
        barColorLabel.setTextColor(colorManager.getColorFromFile(ColorManager.TEXT_COLOR));
        secondaryColorLabel.setTextColor(colorManager.getColorFromFile(ColorManager.TEXT_COLOR));
        boardColor1Label.setTextColor(colorManager.getColorFromFile(ColorManager.TEXT_COLOR));
        boardColor2Label.setTextColor(colorManager.getColorFromFile(ColorManager.TEXT_COLOR));
        pieceColorLabel.setTextColor(colorManager.getColorFromFile(ColorManager.TEXT_COLOR));
        selectionColorLabel.setTextColor(colorManager.getColorFromFile(ColorManager.TEXT_COLOR));
        textColorLabel.setTextColor(colorManager.getColorFromFile(ColorManager.TEXT_COLOR));
        setDefaultsButton.setTextColor(colorManager.getColorFromFile(ColorManager.TEXT_COLOR));
        setDefaultsButton.setBackgroundColor(colorManager.getColorFromFile(ColorManager.BOARD_COLOR_1));
        saveColorButton.setTextColor(colorManager.getColorFromFile(ColorManager.TEXT_COLOR));
        saveColorButton.setBackgroundColor(colorManager.getColorFromFile(ColorManager.BOARD_COLOR_1));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(colorManager.getColorFromFile(ColorManager.BAR_COLOR));
        }
        layout.invalidate();
    }

    public String getColorString(View view){
        return Integer.toHexString(((ColorDrawable)view.getBackground()).getColor());
    }

    public int getColorInt(View view) {
        return ((ColorDrawable)view.getBackground()).getColor();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.import_export_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return super.onOptionsItemSelected(item);
            case R.id.import_file:
                importSettings();
                return true;
            case R.id.export_file:
                exportSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void importSettings()
    {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");

        startActivityForResult(intent, Requests.IMPORT.ordinal());
    }

    private void exportSettings()
    {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TITLE, "colors.txt");


        startActivityForResult(intent, Requests.EXPORT.ordinal());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == Requests.IMPORT.ordinal() && resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            if (data != null) {
                uri = data.getData();
                colorManager.importColorsFromUri(uri);
            }
        }
        else if (requestCode == Requests.EXPORT.ordinal() && resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            if (data != null) {
                uri = data.getData();
                colorManager.exportColorsToDirectory(uri);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);

    }
}

