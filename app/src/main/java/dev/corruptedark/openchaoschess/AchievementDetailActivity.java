package dev.corruptedark.openchaoschess;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class AchievementDetailActivity extends AppCompatActivity {

    TextView titleView;
    TextView descriptionView;

    LinearLayout achievementDetailLayout;

    File settingsFile;
    InputStream fileReader;
    OutputStream fileWriter;
    byte[] bytes;
    String[] contentArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievement_detail);

        titleView = findViewById(R.id.achievement_detail_title_view);
        descriptionView = findViewById(R.id.achievement_detail_description_view);
        achievementDetailLayout = findViewById(R.id.achievement_detail_activity);
        descriptionView.setMovementMethod(new ScrollingMovementMethod());

        settingsFile = new File(getApplicationContext().getFilesDir(),"settings.txt");
        if(settingsFile.exists()) {
            try{
                fileReader = new FileInputStream(settingsFile);
                bytes = new byte[(int)settingsFile.length()];
                fileReader.read(bytes);
                fileReader.close();
                String contents = new String(bytes);
                contentArray = contents.split(" ");
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        else {
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

        achievementDetailLayout.setBackgroundColor(Color.parseColor("#"+ contentArray[0]));

        String title = getIntent().getStringExtra("title");
        String description = getIntent().getStringExtra("description");

        titleView.setText(title);
        titleView.setTextColor(Color.parseColor("#"+contentArray[7]));
        descriptionView.setText(description);
        descriptionView.setTextColor(Color.parseColor("#"+contentArray[7]));
    }
}
