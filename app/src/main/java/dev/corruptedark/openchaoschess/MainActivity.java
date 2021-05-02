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
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Bundle;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    public final int YOU = -1;
    public final int OPPONENT = 1;
    public final int NONE = 0;

    private final double RATIO_THRESHOLD = 0.2;

    final int SELECT_PLAYERS = 420;
    final int REQUEST_ACHIEVEMENTS = 666;
    final int KNIGHT_TIME = 10000;
    final int CHECK_INBOX = 69;

    volatile boolean buttonsClickable = true;

    RelativeLayout mainLayout;
    TextView mainTitle;
    TextView mainSlogan;
    Button playButton;
    Button hostGameButton;
    Button joinGameButton;
    Button aboutButton;
    Button issuesButton;
    Button quitButton;
    ImageButton settingsButton;
    ImageButton achievementsButton;
    ImageButton knightButton;
    ImageView mainImage_board1;
    ImageView mainImage_board2;
    ImageView mainImage_piece;

    Handler knightHandler;

    AchievementHandler achievementHandler;

    ColorManager colorManager;

    Random rand = new Random(System.currentTimeMillis());

    Runnable knightChecker = new Runnable() {
        @Override
        public void run() {
            try{
                knightButton.setX(mainLayout.getWidth()*rand.nextFloat());
                knightButton.setY(mainLayout.getHeight()*rand.nextFloat());
            }
            finally {
                knightHandler.postDelayed(knightChecker,KNIGHT_TIME);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        achievementHandler = AchievementHandler.getInstance(this);

        mainTitle = (TextView)findViewById(R.id.main_title);
        mainSlogan = (TextView)findViewById(R.id.main_slogan);
        playButton = (Button)findViewById(R.id.play_button);
        hostGameButton = (Button)findViewById(R.id.host_game_button);
        joinGameButton = (Button)findViewById(R.id.join_game_button);
        aboutButton = (Button)findViewById(R.id.about_button);
        quitButton = (Button)findViewById(R.id.quit_button);
        issuesButton = (Button)findViewById(R.id.issues_button);
        settingsButton = (ImageButton)findViewById(R.id.settings_button);
        achievementsButton = (ImageButton)findViewById(R.id.achievements_button);
        knightButton = (ImageButton)findViewById(R.id.knight_button);
        mainImage_board1 = (ImageView)findViewById(R.id.mainImage_board1);
        mainImage_board2 = (ImageView)findViewById(R.id.mainImage_board2);
        mainImage_piece = (ImageView)findViewById(R.id.mainImage_piece);

        mainLayout = (RelativeLayout)findViewById(R.id.activity_main);

        colorManager = ColorManager.getInstance(this);

        knightHandler = new Handler(Looper.getMainLooper());
        startKnight();
    }

    void startKnight(){
        knightChecker.run();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        stopKnight();
    }

    void stopKnight(){
        knightHandler.removeCallbacks(knightChecker);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
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

        int iconWidth;
        int buttonHeight;
        int buttonGap;

        int titleHeight;
        int buttonText;

        int textHeight;
        int horizontalPadding = convertDpToPx(10);

        if (Math.abs(((double) width)/height - 1.0) <= RATIO_THRESHOLD || (double)width/height > 1) { // ratio not long
            double titleWeight = 0.1;
            double iconWeight = 0.4;
            double buttonWeight = 0.2;
            double gapWeight = 0.03;
            double textWeight = 0.05;

            double totalWeight = iconWeight + 6 * buttonWeight + 2 * textWeight + 8 * gapWeight + titleWeight;

            titleHeight = (int)(height * titleWeight / totalWeight);
            iconWidth = (int)(height * iconWeight / totalWeight);
            buttonHeight = (int)(height * buttonWeight / totalWeight);
            buttonText = (int)(height * 0.7 * buttonWeight / totalWeight);
            buttonGap = (int)(height * gapWeight / totalWeight);
            textHeight = (int)(height * textWeight / totalWeight);
        }
        else {
            iconWidth = (int)(width * 0.3);
            buttonHeight = (int)(height * .075);
            titleHeight = buttonHeight;
            buttonGap = (int)(height * .015);
            textHeight = (int)(height * .03);
            buttonText = textHeight;
            horizontalPadding = convertDpToPx(10);
        }

        RelativeLayout.LayoutParams mainTitleParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, titleHeight);
        mainTitleParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        mainTitleParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        mainTitleParams.setMargins(0, 0,0,0);
        mainTitle.setLayoutParams(mainTitleParams);
        mainTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int)(0.9 * titleHeight));

        RelativeLayout.LayoutParams mainSloganParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, buttonHeight);
        mainSloganParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        mainSloganParams.addRule(RelativeLayout.BELOW,R.id.main_title);
        mainSloganParams.setMargins(0, 0,0,0);
        mainSlogan.setLayoutParams(mainSloganParams);
        mainSlogan.setTextSize(TypedValue.COMPLEX_UNIT_PX, textHeight);
        mainSlogan.setGravity(Gravity.CENTER);

        RelativeLayout.LayoutParams iconParams = new RelativeLayout.LayoutParams(iconWidth,iconWidth);
        iconParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        iconParams.addRule(RelativeLayout.BELOW, R.id.main_slogan);
        iconParams.setMargins(0, buttonGap,0,0);
        mainImage_board1.setLayoutParams(iconParams);
        mainImage_board2.setLayoutParams(iconParams);
        mainImage_piece.setLayoutParams(iconParams);

        playButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, buttonText);
        RelativeLayout.LayoutParams playButtonParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, buttonHeight);
        playButtonParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        playButtonParams.addRule(RelativeLayout.BELOW, R.id.mainImage_board1);
        playButtonParams.setMargins(0, buttonGap,0,0);
        playButton.setPadding(horizontalPadding, 0, horizontalPadding, 0);
        playButton.setLayoutParams(playButtonParams);

        hostGameButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, buttonText);
        RelativeLayout.LayoutParams hostGameButtonParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, buttonHeight);
        hostGameButtonParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        hostGameButtonParams.addRule(RelativeLayout.BELOW, R.id.play_button);
        hostGameButtonParams.setMargins(0, buttonGap,0,0);
        hostGameButton.setPadding(horizontalPadding, 0, horizontalPadding, 0);
        hostGameButton.setLayoutParams(hostGameButtonParams);

        joinGameButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, buttonText);
        RelativeLayout.LayoutParams joinGameButtonParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, buttonHeight);
        joinGameButtonParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        joinGameButtonParams.addRule(RelativeLayout.BELOW, R.id.host_game_button);
        joinGameButtonParams.setMargins(0, buttonGap,0,0);
        joinGameButton.setPadding(horizontalPadding, 0, horizontalPadding, 0);
        joinGameButton.setLayoutParams(joinGameButtonParams);

        aboutButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, buttonText);
        RelativeLayout.LayoutParams aboutButtonParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, buttonHeight);
        aboutButtonParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        aboutButtonParams.addRule(RelativeLayout.BELOW, R.id.join_game_button);
        aboutButtonParams.setMargins(0, buttonGap,0,0);
        aboutButton.setPadding(horizontalPadding, 0, horizontalPadding, 0);
        aboutButton.setLayoutParams(aboutButtonParams);

        issuesButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, buttonText);
        RelativeLayout.LayoutParams issuesButtonParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, buttonHeight);
        issuesButtonParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        issuesButtonParams.addRule(RelativeLayout.BELOW, R.id.about_button);
        issuesButtonParams.setMargins(0, buttonGap,0,0);
        issuesButton.setPadding(horizontalPadding, 0, horizontalPadding, 0);
        issuesButton.setLayoutParams(issuesButtonParams);

        quitButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, buttonText);
        RelativeLayout.LayoutParams quitButtonParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, buttonHeight);
        quitButtonParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        quitButtonParams.addRule(RelativeLayout.BELOW, R.id.issues_button);
        quitButtonParams.setMargins(0, buttonGap,0,0);
        quitButton.setPadding(horizontalPadding, 0, horizontalPadding, 0);
        quitButton.setLayoutParams(quitButtonParams);

        RelativeLayout.LayoutParams settingsButtonParams = new RelativeLayout.LayoutParams(buttonHeight, buttonHeight);
        settingsButtonParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        settingsButtonParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        settingsButtonParams.setMargins(0, 0,0,0);
        settingsButton.setLayoutParams(settingsButtonParams);

        RelativeLayout.LayoutParams achievementButtonParams = new RelativeLayout.LayoutParams(buttonHeight, buttonHeight);
        achievementButtonParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        achievementButtonParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        achievementButtonParams.setMargins(0, 0,0,0);
        achievementsButton.setLayoutParams(achievementButtonParams);

        knightButton.getDrawable().setColorFilter(colorManager.getColorFromFile(ColorManager.PIECE_COLOR), PorterDuff.Mode.MULTIPLY);
        mainImage_piece.getDrawable().setColorFilter(colorManager.getColorFromFile(ColorManager.PIECE_COLOR), PorterDuff.Mode.MULTIPLY);
        mainImage_board1.getDrawable().setColorFilter(colorManager.getColorFromFile(ColorManager.BOARD_COLOR_1), PorterDuff.Mode.MULTIPLY);
        mainImage_board2.getDrawable().setColorFilter(colorManager.getColorFromFile(ColorManager.BOARD_COLOR_2), PorterDuff.Mode.MULTIPLY);
        mainLayout.setBackgroundColor(colorManager.getColorFromFile(ColorManager.BACKGROUND_COLOR));
        mainTitle.setTextColor(colorManager.getColorFromFile(ColorManager.TEXT_COLOR));
        mainSlogan.setTextColor(colorManager.getColorFromFile(ColorManager.TEXT_COLOR));
        playButton.setTextColor(colorManager.getColorFromFile(ColorManager.TEXT_COLOR));
        playButton.setBackgroundColor(colorManager.getColorFromFile(ColorManager.BOARD_COLOR_1));
        hostGameButton.setTextColor(colorManager.getColorFromFile(ColorManager.TEXT_COLOR));
        hostGameButton.setBackgroundColor(colorManager.getColorFromFile(ColorManager.BOARD_COLOR_1));
        joinGameButton.setTextColor(colorManager.getColorFromFile(ColorManager.TEXT_COLOR));
        joinGameButton.setBackgroundColor(colorManager.getColorFromFile(ColorManager.BOARD_COLOR_1));
        aboutButton.setTextColor(colorManager.getColorFromFile(ColorManager.TEXT_COLOR));
        aboutButton.setBackgroundColor(colorManager.getColorFromFile(ColorManager.BOARD_COLOR_1));
        issuesButton.setTextColor(colorManager.getColorFromFile(ColorManager.TEXT_COLOR));
        issuesButton.setBackgroundColor(colorManager.getColorFromFile(ColorManager.BOARD_COLOR_1));
        quitButton.setTextColor(colorManager.getColorFromFile(ColorManager.TEXT_COLOR));
        quitButton.setBackgroundColor(colorManager.getColorFromFile(ColorManager.BOARD_COLOR_1));
        settingsButton.setBackgroundColor(colorManager.getColorFromFile(ColorManager.BOARD_COLOR_1));
        settingsButton.getDrawable().setColorFilter(colorManager.getColorFromFile(ColorManager.TEXT_COLOR),PorterDuff.Mode.MULTIPLY);
        achievementsButton.setBackgroundColor(colorManager.getColorFromFile(ColorManager.BOARD_COLOR_1));
        achievementsButton.getDrawable().setColorFilter(colorManager.getColorFromFile(ColorManager.TEXT_COLOR),PorterDuff.Mode.MULTIPLY);

        knightButton.bringToFront();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(colorManager.getColorFromFile(ColorManager.BAR_COLOR));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

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
        int height = (int) (0.9 * size.y);

        int iconWidth;
        int buttonHeight;
        int buttonGap;

        int titleHeight;
        int buttonText;

        int textHeight;
        int horizontalPadding = convertDpToPx(10);

        if (Math.abs(((double) width)/height - 1.0) <= RATIO_THRESHOLD || (double)width/height > 1) { // ratio not long
            double titleWeight = 0.15;
            double iconWeight = 0.3;
            double buttonWeight = 0.1;
            double gapWeight = 0.02;
            double textWeight = 0.05;

            double totalWeight = iconWeight + 6 * buttonWeight + 2 * textWeight + 8 * gapWeight + titleWeight;

            titleHeight = (int)(height * titleWeight / totalWeight);
            iconWidth = (int)(height * iconWeight / totalWeight);
            buttonHeight = (int)(height * buttonWeight / totalWeight);
            buttonText = (int)(height * 0.7 * buttonWeight / totalWeight);
            buttonGap = (int)(height * gapWeight / totalWeight);
            textHeight = (int)(height * textWeight / totalWeight);
        }
        else {
            iconWidth = (int)(width * 0.3);
            buttonHeight = (int)(height * .075);
            titleHeight = buttonHeight;
            buttonGap = (int)(height * .015);
            textHeight = (int)(height * .03);
            buttonText = textHeight;
            horizontalPadding = convertDpToPx(10);
        }

        RelativeLayout.LayoutParams mainTitleParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, titleHeight);
        mainTitleParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        mainTitleParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        mainTitleParams.setMargins(0, 0,0,0);
        mainTitle.setLayoutParams(mainTitleParams);
        mainTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int)(0.8 * titleHeight));

        RelativeLayout.LayoutParams mainSloganParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 2 * textHeight);
        mainSloganParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        mainSloganParams.addRule(RelativeLayout.BELOW,R.id.main_title);
        mainSloganParams.setMargins(0, 0,0,0);
        mainSlogan.setLayoutParams(mainSloganParams);
        mainSlogan.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (0.89 * textHeight));
        mainSlogan.setGravity(Gravity.CENTER);

        RelativeLayout.LayoutParams iconParams = new RelativeLayout.LayoutParams(iconWidth,iconWidth);
        iconParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        iconParams.addRule(RelativeLayout.BELOW, R.id.main_slogan);
        iconParams.setMargins(0, buttonGap,0,0);
        mainImage_board1.setLayoutParams(iconParams);
        mainImage_board2.setLayoutParams(iconParams);
        mainImage_piece.setLayoutParams(iconParams);

        playButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, buttonText);
        RelativeLayout.LayoutParams playButtonParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, buttonHeight);
        playButtonParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        playButtonParams.addRule(RelativeLayout.BELOW, R.id.mainImage_board1);
        playButtonParams.setMargins(0, buttonGap,0,0);
        playButton.setPadding(horizontalPadding, 0, horizontalPadding, 0);
        playButton.setLayoutParams(playButtonParams);

        hostGameButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, buttonText);
        RelativeLayout.LayoutParams hostGameButtonParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, buttonHeight);
        hostGameButtonParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        hostGameButtonParams.addRule(RelativeLayout.BELOW, R.id.play_button);
        hostGameButtonParams.setMargins(0, buttonGap,0,0);
        hostGameButton.setPadding(horizontalPadding, 0, horizontalPadding, 0);
        hostGameButton.setLayoutParams(hostGameButtonParams);

        joinGameButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, buttonText);
        RelativeLayout.LayoutParams joinGameButtonParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, buttonHeight);
        joinGameButtonParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        joinGameButtonParams.addRule(RelativeLayout.BELOW, R.id.host_game_button);
        joinGameButtonParams.setMargins(0, buttonGap,0,0);
        joinGameButton.setPadding(horizontalPadding, 0, horizontalPadding, 0);
        joinGameButton.setLayoutParams(joinGameButtonParams);

        aboutButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, buttonText);
        RelativeLayout.LayoutParams aboutButtonParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, buttonHeight);
        aboutButtonParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        aboutButtonParams.addRule(RelativeLayout.BELOW, R.id.join_game_button);
        aboutButtonParams.setMargins(0, buttonGap,0,0);
        aboutButton.setPadding(horizontalPadding, 0, horizontalPadding, 0);
        aboutButton.setLayoutParams(aboutButtonParams);

        issuesButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, buttonText);
        RelativeLayout.LayoutParams issuesButtonParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, buttonHeight);
        issuesButtonParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        issuesButtonParams.addRule(RelativeLayout.BELOW, R.id.about_button);
        issuesButtonParams.setMargins(0, buttonGap,0,0);
        issuesButton.setPadding(horizontalPadding, 0, horizontalPadding, 0);
        issuesButton.setLayoutParams(issuesButtonParams);

        quitButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, buttonText);
        RelativeLayout.LayoutParams quitButtonParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, buttonHeight);
        quitButtonParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        quitButtonParams.addRule(RelativeLayout.BELOW, R.id.issues_button);
        quitButtonParams.setMargins(0, buttonGap,0,0);
        quitButton.setPadding(horizontalPadding, 0, horizontalPadding, 0);
        quitButton.setLayoutParams(quitButtonParams);

        RelativeLayout.LayoutParams settingsButtonParams = new RelativeLayout.LayoutParams(buttonHeight, buttonHeight);
        settingsButtonParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        settingsButtonParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        settingsButtonParams.setMargins(0, 0,0,0);
        settingsButton.setLayoutParams(settingsButtonParams);

        RelativeLayout.LayoutParams achievementButtonParams = new RelativeLayout.LayoutParams(buttonHeight, buttonHeight);
        achievementButtonParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        achievementButtonParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        achievementButtonParams.setMargins(0, 0,0,0);
        achievementsButton.setLayoutParams(achievementButtonParams);

        colorManager = ColorManager.getInstance(this);

        knightButton.getDrawable().setColorFilter(colorManager.getColorFromFile(ColorManager.PIECE_COLOR), PorterDuff.Mode.MULTIPLY);
        mainImage_piece.getDrawable().setColorFilter(colorManager.getColorFromFile(ColorManager.PIECE_COLOR), PorterDuff.Mode.MULTIPLY);
        mainImage_board1.getDrawable().setColorFilter(colorManager.getColorFromFile(ColorManager.BOARD_COLOR_1), PorterDuff.Mode.MULTIPLY);
        mainImage_board2.getDrawable().setColorFilter(colorManager.getColorFromFile(ColorManager.BOARD_COLOR_2), PorterDuff.Mode.MULTIPLY);
        mainLayout.setBackgroundColor(colorManager.getColorFromFile(ColorManager.BACKGROUND_COLOR));
        mainTitle.setTextColor(colorManager.getColorFromFile(ColorManager.TEXT_COLOR));
        mainSlogan.setTextColor(colorManager.getColorFromFile(ColorManager.TEXT_COLOR));
        playButton.setTextColor(colorManager.getColorFromFile(ColorManager.TEXT_COLOR));
        playButton.setBackgroundColor(colorManager.getColorFromFile(ColorManager.BOARD_COLOR_1));
        hostGameButton.setTextColor(colorManager.getColorFromFile(ColorManager.TEXT_COLOR));
        hostGameButton.setBackgroundColor(colorManager.getColorFromFile(ColorManager.BOARD_COLOR_1));
        joinGameButton.setTextColor(colorManager.getColorFromFile(ColorManager.TEXT_COLOR));
        joinGameButton.setBackgroundColor(colorManager.getColorFromFile(ColorManager.BOARD_COLOR_1));
        aboutButton.setTextColor(colorManager.getColorFromFile(ColorManager.TEXT_COLOR));
        aboutButton.setBackgroundColor(colorManager.getColorFromFile(ColorManager.BOARD_COLOR_1));
        issuesButton.setTextColor(colorManager.getColorFromFile(ColorManager.TEXT_COLOR));
        issuesButton.setBackgroundColor(colorManager.getColorFromFile(ColorManager.BOARD_COLOR_1));
        quitButton.setTextColor(colorManager.getColorFromFile(ColorManager.TEXT_COLOR));
        quitButton.setBackgroundColor(colorManager.getColorFromFile(ColorManager.BOARD_COLOR_1));
        settingsButton.setBackgroundColor(colorManager.getColorFromFile(ColorManager.BOARD_COLOR_1));
        settingsButton.getDrawable().setColorFilter(colorManager.getColorFromFile(ColorManager.TEXT_COLOR),PorterDuff.Mode.MULTIPLY);
        achievementsButton.setBackgroundColor(colorManager.getColorFromFile(ColorManager.BOARD_COLOR_1));
        achievementsButton.getDrawable().setColorFilter(colorManager.getColorFromFile(ColorManager.TEXT_COLOR),PorterDuff.Mode.MULTIPLY);

        knightButton.bringToFront();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(colorManager.getColorFromFile(ColorManager.BAR_COLOR));
        }
    }

    public void playButtonClicked(View view){
        if (buttonsClickable) {
            buttonsClickable = false;
            if(SingleGame.getInstance().isKnightsOnly()) {
                SingleGame.getInstance().newGame();
                SingleGame.getInstance().setKnightsOnly(false);
            }
            Intent intent = new Intent(MainActivity.this, SinglePlayerBoard.class);
            startActivity(intent);
        }
    }

    public void hostGameButtonClicked(View view)
    {
        if (buttonsClickable) {
            buttonsClickable = false;
            Intent intent = new Intent(MainActivity.this, StartHostActivity.class);
            startActivity(intent);
        }
    }

    public void joinGameButtonClicked(View view)
    {
        if (buttonsClickable) {
            buttonsClickable = false;
            Intent intent = new Intent(MainActivity.this, StartClientActivity.class);
            startActivity(intent);
        }
    }

    public void aboutButtonClicked(View view){
        if (buttonsClickable) {
            buttonsClickable = false;
            achievementHandler.incrementInMemory(AchievementHandler.OPENED_ABOUT);
            achievementHandler.saveValues();

            startActivity(new Intent(MainActivity.this, AboutActivity.class));
        }
    }

    public void issuesButtonClicked(View view)
    {
        if (buttonsClickable) {
            buttonsClickable = false;
            String url = getString(R.string.issues_url);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        }
    }


    public void quitButtonClicked(View view){
        finish();
    }

    public void settingsButtonClicked(View view) {
        if (buttonsClickable) {
            buttonsClickable = false;
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        }
    }

    public void achievementsClicked(View view){
        if (buttonsClickable) {
            buttonsClickable = false;
            startActivity(new Intent(MainActivity.this, AchievementsActivity.class));
        }
    }

    public void knightButtonClicked(View view){
        if (buttonsClickable) {
            buttonsClickable = false;
            Intent intent = new Intent(MainActivity.this, SinglePlayerBoard.class);

            if(!SingleGame.getInstance().isKnightsOnly()) {
                SingleGame.getInstance().newGame();
                SingleGame.getInstance().setKnightsOnly(true);
            }

            achievementHandler.incrementInMemory(AchievementHandler.HORSING_AROUND);

            startActivity(intent);
        }
    }

    private int convertDpToPx(int dp){
        return Math.round(dp*(getResources().getDisplayMetrics().xdpi/ DisplayMetrics.DENSITY_DEFAULT));

    }

    protected void initializeGameData(int request) {
        //TODO

        if (request == SELECT_PLAYERS) {
            MultiGame.getInstance().setTurn(YOU);
        } else {
            MultiGame.getInstance().setTurn(OPPONENT);
        }

    }

    protected void showTurnUI()
    {
        //TODO
        Intent intent = new Intent(MainActivity.this, MultiPlayerBoard.class);
        intent.putExtra("knightsOnly", false);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(final int request, int response, Intent data) {
        super.onActivityResult(request, response, data);
    }

    @Override
    protected void onPostResume() {
        buttonsClickable = true;
        super.onPostResume();
    }
}
