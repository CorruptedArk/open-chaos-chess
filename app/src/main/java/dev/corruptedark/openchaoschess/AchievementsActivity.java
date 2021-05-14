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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;

public class AchievementsActivity extends AppCompatActivity {

    LinearLayout achievementsLayout;
    ListView achievementListView;
    AchievementAdapter achievementAdapter;
    Toolbar toolbar;

    ColorManager colorManager;

    private enum Requests{IMPORT, EXPORT}

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
        toolbar.getNavigationIcon().setColorFilter(colorManager.getColorFromFile(ColorManager.TEXT_COLOR), PorterDuff.Mode.MULTIPLY);
        toolbar.getOverflowIcon().setColorFilter(colorManager.getColorFromFile(ColorManager.TEXT_COLOR), PorterDuff.Mode.MULTIPLY);

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
                importAchievements();
                return true;
            case R.id.export_file:
                exportAchievements();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void importAchievements()
    {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");

        startActivityForResult(intent, Requests.IMPORT.ordinal());

    }

    private void exportAchievements()
    {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TITLE, "achievements.txt");


        startActivityForResult(intent, Requests.EXPORT.ordinal());

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        if (requestCode == Requests.IMPORT.ordinal() && resultCode == Activity.RESULT_OK)
        {
            Uri uri = null;
            if(data != null)
            {
                uri = data.getData();
                AchievementHandler.getInstance(this).importAchievementsFromUri(uri, this);

                ArrayList<Achievement> achievementAdapterList = AchievementHandler.getInstance(this).getList();

                for(int i = achievementAdapterList.size() - 1; i >= 0; i--) {
                    if(!AchievementHandler.getInstance(this).isUnlocked(achievementAdapterList.get(i))) {
                        achievementAdapterList.remove(i);
                    }
                }

                achievementAdapter = new AchievementAdapter(this,R.layout.achievement_item,achievementAdapterList,colorManager.getColorFromFile(ColorManager.BACKGROUND_COLOR),colorManager.getColorFromFile(ColorManager.TEXT_COLOR));
                achievementListView.setAdapter(achievementAdapter);
            }
        }
        else if (requestCode == Requests.EXPORT.ordinal() && resultCode == Activity.RESULT_OK)
        {
            Uri uri = null;
            if(data != null) {
                uri = data.getData();
                AchievementHandler.getInstance(this).exportAchievementsToDirectory(uri, this);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
