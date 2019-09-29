package dev.corruptedark.openchaoschess;

/**
 * Created by CorruptedArk
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
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

    ImageView aboutImage;

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


        aboutImage = (ImageView)findViewById(R.id.about_image);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        aboutImage.setOnClickListener(new View.OnClickListener() {
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

        Display display = getWindowManager().getDefaultDisplay();
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
        aboutImage.setLayoutParams(iconParams);

        toolbar.setTitleTextColor(colorManager.getColorFromFile(ColorManager.TEXT_COLOR));
        toolbar.setBackgroundColor(colorManager.getColorFromFile(ColorManager.SECONDARY_COLOR));
        aboutLayout.setBackgroundColor(colorManager.getColorFromFile(ColorManager.BACKGROUND_COLOR));
        aboutTitle.setTextColor(colorManager.getColorFromFile(ColorManager.TEXT_COLOR));
        aboutCredits.setTextColor(colorManager.getColorFromFile(ColorManager.TEXT_COLOR));
        aboutDescript.setTextColor(colorManager.getColorFromFile(ColorManager.TEXT_COLOR));
        aboutContact.setTextColor(colorManager.getColorFromFile(ColorManager.TEXT_COLOR));
        aboutContact.setLinkTextColor(colorManager.getColorFromFile(ColorManager.TEXT_COLOR));
        aboutContact.setHighlightColor(colorManager.getColorFromFile(ColorManager.BOARD_COLOR_1));

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
