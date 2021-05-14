package dev.corruptedark.openchaoschess;

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

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class AboutActivity extends AppCompatActivity {
    RelativeLayout aboutLayout;
    Toolbar toolbar;
    TextView aboutTitle;
    TextView aboutCredits;
    TextView aboutDescript;
    TextView aboutContact;

    ImageView aboutImageBoard1;
    ImageView aboutImageBoard2;
    ImageView aboutImagePiece;

    Context context;

    ColorManager colorManager;

    AchievementHandler achievementHandler = AchievementHandler.getInstance(this);

    private final int KNOCK = 13;
    private int knockCount = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        toolbar = (Toolbar) findViewById(R.id.about_toolbar);
        toolbar.setTitle(R.string.about);
        setSupportActionBar(toolbar);
        context = this;
        aboutLayout = (RelativeLayout) findViewById(R.id.about_layout);
        aboutTitle = (TextView)findViewById(R.id.about_title);
        aboutCredits = (TextView)findViewById(R.id.about_credits);
        aboutDescript = (TextView)findViewById(R.id.about_descript);
        aboutContact = (TextView)findViewById(R.id.about_contact);


        aboutImageBoard1 = (ImageView)findViewById(R.id.about_image_board1);
        aboutImageBoard2 = (ImageView)findViewById(R.id.about_image_board2);
        aboutImagePiece = (ImageView)findViewById(R.id.about_image_piece);



        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        aboutImagePiece.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(++knockCount == KNOCK){
                    Toast.makeText(context, "You have discovered the secret.", Toast.LENGTH_LONG).show();
                    achievementHandler.incrementInMemory(AchievementHandler.SECRET_KNOCK);
                    achievementHandler.saveValues();
                    Intent intent = new Intent(AboutActivity.this,SecretActivity.class);
                    knockCount = 0;
                    startActivity(intent);
                }
            }
        });

        colorManager = ColorManager.getInstance(this);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        Display display;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            display = getDisplay();
        }
        else {
            display = getWindowManager().getDefaultDisplay();
        }
        Point size = new Point();
        display.getSize(size);

        int width = size.x;
        int height = size.y;

        int iconWidth = (int)(width * 0.40);
        int buttonHeight = (int)(height * .075);
        int buttonGap = (int)(height * .03);

        RelativeLayout.LayoutParams iconParams = new RelativeLayout.LayoutParams(iconWidth,iconWidth);
        iconParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        iconParams.addRule(RelativeLayout.BELOW, R.id.about_toolbar);
        iconParams.setMargins(0, buttonGap,0,buttonGap);
        aboutImageBoard1.setLayoutParams(iconParams);
        aboutImageBoard2.setLayoutParams(iconParams);
        aboutImagePiece.setLayoutParams(iconParams);

        toolbar.setTitleTextColor(colorManager.getColorFromFile(ColorManager.TEXT_COLOR));
        toolbar.setBackgroundColor(colorManager.getColorFromFile(ColorManager.SECONDARY_COLOR));
        toolbar.getNavigationIcon().setColorFilter(colorManager.getColorFromFile(ColorManager.TEXT_COLOR), PorterDuff.Mode.MULTIPLY);
        aboutLayout.setBackgroundColor(colorManager.getColorFromFile(ColorManager.BACKGROUND_COLOR));
        aboutImageBoard1.getDrawable().setColorFilter(colorManager.getColorFromFile(ColorManager.BOARD_COLOR_1), PorterDuff.Mode.MULTIPLY);
        aboutImageBoard2.getDrawable().setColorFilter(colorManager.getColorFromFile(ColorManager.BOARD_COLOR_2), PorterDuff.Mode.MULTIPLY);
        aboutImagePiece.getDrawable().setColorFilter(colorManager.getColorFromFile(ColorManager.PIECE_COLOR), PorterDuff.Mode.MULTIPLY);
        aboutTitle.setTextColor(colorManager.getColorFromFile(ColorManager.TEXT_COLOR));
        aboutCredits.setTextColor(colorManager.getColorFromFile(ColorManager.TEXT_COLOR));
        aboutDescript.setTextColor(colorManager.getColorFromFile(ColorManager.TEXT_COLOR));
        aboutContact.setTextColor(colorManager.getColorFromFile(ColorManager.TEXT_COLOR));
        aboutContact.setLinkTextColor(colorManager.getColorFromFile(ColorManager.TEXT_COLOR));
        aboutContact.setHighlightColor(colorManager.getColorFromFile(ColorManager.BOARD_COLOR_1));

        aboutCredits.setText(getString(R.string.credits, BuildConfig.VERSION_NAME));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(colorManager.getColorFromFile(ColorManager.BAR_COLOR));
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
            finish();

        return super.onOptionsItemSelected(item);
    }

}
