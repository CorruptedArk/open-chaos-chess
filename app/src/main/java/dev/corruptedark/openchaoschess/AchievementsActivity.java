package dev.corruptedark.openchaoschess;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class AchievementsActivity extends AppCompatActivity {

    ListView achievementListView;
    AchievementAdapter achievementAdapter;
    Toolbar toolbar;

    File settingsFile;
    InputStream fileReader;
    OutputStream fileWriter;
    byte[] bytes;
    String[] contentArray;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievements);

        toolbar = (Toolbar) findViewById(R.id.achievement_toolbar);
        toolbar.setTitle("Achievements");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


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

        toolbar.setTitleTextColor(Color.parseColor("#" + contentArray[7]));
        toolbar.setBackgroundColor(Color.parseColor("#" + contentArray[2]));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#"+contentArray[1]));
        }

        achievementListView = findViewById(R.id.achievement_list_view);

        ArrayList<Achievement> achievementAdapterList = AchievementHandler.getInstance(this).getList();

        for(int i = achievementAdapterList.size() - 1; i >= 0; i--) {
            if(!AchievementHandler.getInstance(this).isUnlocked(achievementAdapterList.get(i))) {
                achievementAdapterList.remove(i);
            }
        }

        achievementAdapter = new AchievementAdapter(this,R.layout.achievement_item,achievementAdapterList,Color.parseColor("#"+contentArray[0]),Color.parseColor("#"+contentArray[7]));
        achievementListView.setAdapter(achievementAdapter);
        achievementListView.setBackgroundColor(Color.parseColor("#"+contentArray[0]));
        achievementListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Achievement achievement = achievementAdapter.getItem(position);

                Intent intent = new Intent(AchievementsActivity.this,AchievementDetailActivity.class);
                intent.putExtra("title",achievement.getTitle());
                intent.putExtra("description",achievement.getDescription());
                startActivity(intent);
            }
        });
    }
}
