package dev.corruptedark.openchaoschess;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class AchievementsActivity extends AppCompatActivity {

    LinearLayout achievementsLayout;
    ListView achievementListView;
    AchievementAdapter achievementAdapter;
    Toolbar toolbar;

    ColorManager colorManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievements);

        achievementsLayout = findViewById(R.id.achievements_activity);
        toolbar = (Toolbar) findViewById(R.id.achievement_toolbar);
        toolbar.setTitle("Achievements");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        colorManager = ColorManager.getInstance(this);

        achievementsLayout.setBackgroundColor(colorManager.getColorFromFile(ColorManager.BACKGROUND_COLOR));

        toolbar.setTitleTextColor(colorManager.getColorFromFile(ColorManager.TEXT_COLOR));
        toolbar.setBackgroundColor(colorManager.getColorFromFile(ColorManager.SECONDARY_COLOR));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(colorManager.getColorFromFile(ColorManager.BAR_COLOR));
        }

        achievementListView = findViewById(R.id.achievement_list_view);

        ArrayList<Achievement> achievementAdapterList = AchievementHandler.getInstance(this).getList();

        for(int i = achievementAdapterList.size() - 1; i >= 0; i--) {
            if(!AchievementHandler.getInstance(this).isUnlocked(achievementAdapterList.get(i))) {
                achievementAdapterList.remove(i);
            }
        }

        achievementAdapter = new AchievementAdapter(this,R.layout.achievement_item,achievementAdapterList,colorManager.getColorFromFile(ColorManager.BACKGROUND_COLOR),colorManager.getColorFromFile(ColorManager.TEXT_COLOR));
        achievementListView.setAdapter(achievementAdapter);
        achievementListView.setBackgroundColor(colorManager.getColorFromFile(ColorManager.BACKGROUND_COLOR));
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
            finish();

        return super.onOptionsItemSelected(item);
    }
}
