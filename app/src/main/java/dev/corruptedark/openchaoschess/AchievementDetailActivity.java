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

    ColorManager colorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievement_detail);

        titleView = findViewById(R.id.achievement_detail_title_view);
        descriptionView = findViewById(R.id.achievement_detail_description_view);
        achievementDetailLayout = findViewById(R.id.achievement_detail_activity);
        descriptionView.setMovementMethod(new ScrollingMovementMethod());

        colorManager = ColorManager.getInstance(this);

        achievementDetailLayout.setBackgroundColor(colorManager.getColorFromFile(ColorManager.BACKGROUND_COLOR));

        String title = getIntent().getStringExtra("title");
        String description = getIntent().getStringExtra("description");

        titleView.setText(title);
        titleView.setTextColor(colorManager.getColorFromFile(ColorManager.TEXT_COLOR));
        descriptionView.setText(description);
        descriptionView.setTextColor(colorManager.getColorFromFile(ColorManager.TEXT_COLOR));
    }
}
