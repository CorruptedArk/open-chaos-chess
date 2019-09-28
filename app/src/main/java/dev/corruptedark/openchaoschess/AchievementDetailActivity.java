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
