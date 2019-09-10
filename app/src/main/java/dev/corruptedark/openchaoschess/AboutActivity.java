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

    File settingsFile;
    FileInputStream fileReader;
    FileOutputStream fileWriter;
    AchievementHandler achievementHandler = AchievementHandler.getInstance(this);

    private final int KNOCK = 13;
    private int knockCount = 0;

    byte[] bytes;
    String[] contentArray;

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

        settingsFile = new File(getApplicationContext().getFilesDir(), getString(R.string.settings_file));
        if(settingsFile.exists()) {
            try {
                fileReader = new FileInputStream(settingsFile);
                bytes = new byte[(int) settingsFile.length()];
                fileReader.read(bytes);
                fileReader.close();
                String contents = new String(bytes);
                //Toast.makeText(this,contents,Toast.LENGTH_LONG).show();
                contentArray = contents.split(" ");
            } catch (Exception e) {
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

        toolbar.setTitleTextColor(Color.parseColor("#" + contentArray[7]));
        toolbar.setBackgroundColor(Color.parseColor("#" + contentArray[2]));
        aboutLayout.setBackgroundColor(Color.parseColor("#"+contentArray[0]));
        aboutTitle.setTextColor(Color.parseColor("#" + contentArray[7]));
        aboutCredits.setTextColor(Color.parseColor("#" + contentArray[7]));
        aboutDescript.setTextColor(Color.parseColor("#" + contentArray[7]));
        aboutContact.setTextColor(Color.parseColor("#" + contentArray[7]));
        aboutContact.setLinkTextColor(Color.parseColor("#" + contentArray[7]));
        aboutContact.setHighlightColor(Color.parseColor("#55" + contentArray[3].substring(2,8)));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#"+contentArray[1]));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
            finish();

        return super.onOptionsItemSelected(item);
    }

}
