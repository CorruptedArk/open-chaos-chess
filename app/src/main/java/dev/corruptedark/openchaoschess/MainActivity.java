package dev.corruptedark.openchaoschess;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Handler;
import android.os.Bundle;
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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by CorruptedArk
 */
public class MainActivity extends AppCompatActivity {
    public final int YOU = -1;
    public final int OPPONENT = 1;
    public final int NONE = 0;


    final int SELECT_PLAYERS = 420;
    final int REQUEST_ACHIEVEMENTS = 666;
    final int KNIGHT_TIME = 10000;
    final int CHECK_INBOX = 69;

    RelativeLayout mainLayout;
    TextView mainTitle;
    TextView mainSlogan;
    Button playButton;
    Button createGameButton;
    Button checkInvitesButton;
    Button aboutButton;
    Button quitButton;
    ImageButton settingsButton;
    ImageButton achievementsButton;
    ImageButton knightButton;
    ImageView mainImage;

    File settingsFile;
    InputStream fileReader;
    OutputStream fileWriter;
    byte[] bytes;
    String[] contentArray;
    Handler knightHandler;

    AchievementHandler achievementHandler;

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
        createGameButton = (Button)findViewById(R.id.create_game_button);
        checkInvitesButton = (Button)findViewById(R.id.check_invites_button);
        aboutButton = (Button)findViewById(R.id.about_button);
        quitButton = (Button)findViewById(R.id.quit_button);
        settingsButton = (ImageButton)findViewById(R.id.settings_button);
        achievementsButton = (ImageButton)findViewById(R.id.achievements_button);
        knightButton = (ImageButton)findViewById(R.id.knight_button);
        mainImage = (ImageView)findViewById(R.id.mainImage);

        mainLayout = (RelativeLayout)findViewById(R.id.activity_main);

        settingsFile = new File(getApplicationContext().getFilesDir(),getString(R.string.settings_file));
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

        knightHandler = new Handler();
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
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);


        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        int width = size.x;
        int height = size.y;

        int iconWidth = (int)(width * 0.3);
        int buttonHeight = (int)(height * .075);
        int buttonGap = (int)(height * .015);

        int textHeight = (int)(height * .03);

        RelativeLayout.LayoutParams mainTitleParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, buttonHeight);
        mainTitleParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        mainTitleParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        mainTitleParams.setMargins(0, 0,0,0);
        mainTitle.setLayoutParams(mainTitleParams);
        mainTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX,(int)(height*.05));

        RelativeLayout.LayoutParams mainSloganParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, (int)(height*.025));
        mainSloganParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        mainSloganParams.addRule(RelativeLayout.BELOW,R.id.main_title);
        mainSloganParams.setMargins(0, 0,0,0);
        mainSlogan.setLayoutParams(mainSloganParams);
        mainSlogan.setTextSize(TypedValue.COMPLEX_UNIT_PX,(int)(height*.02));
        mainSlogan.setGravity(Gravity.CENTER);

        RelativeLayout.LayoutParams iconParams = new RelativeLayout.LayoutParams(iconWidth,iconWidth);
        iconParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        iconParams.addRule(RelativeLayout.BELOW, R.id.main_slogan);
        iconParams.setMargins(0, buttonGap,0,0);
        mainImage.setLayoutParams(iconParams);

        playButton.setTextSize(TypedValue.COMPLEX_UNIT_PX,textHeight);
        RelativeLayout.LayoutParams playButtonParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        playButtonParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        playButtonParams.addRule(RelativeLayout.BELOW, R.id.mainImage);
        playButtonParams.setMargins(0, buttonGap,0,0);
        playButton.setLayoutParams(playButtonParams);

        createGameButton.setTextSize(TypedValue.COMPLEX_UNIT_PX,textHeight);
        RelativeLayout.LayoutParams createGameButtonParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        createGameButtonParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        createGameButtonParams.addRule(RelativeLayout.BELOW, R.id.play_button);
        createGameButtonParams.setMargins(0, buttonGap,0,0);
        createGameButton.setLayoutParams(createGameButtonParams);

        checkInvitesButton.setTextSize(TypedValue.COMPLEX_UNIT_PX,textHeight);
        RelativeLayout.LayoutParams checkInvitesButtonParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        checkInvitesButtonParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        checkInvitesButtonParams.addRule(RelativeLayout.BELOW, R.id.create_game_button);
        checkInvitesButtonParams.setMargins(0, buttonGap,0,0);
        checkInvitesButton.setLayoutParams(checkInvitesButtonParams);

        aboutButton.setTextSize(TypedValue.COMPLEX_UNIT_PX,textHeight);
        RelativeLayout.LayoutParams aboutButtonParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        aboutButtonParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        aboutButtonParams.addRule(RelativeLayout.BELOW, R.id.check_invites_button);
        aboutButtonParams.setMargins(0, buttonGap,0,0);
        aboutButton.setLayoutParams(aboutButtonParams);

        quitButton.setTextSize(TypedValue.COMPLEX_UNIT_PX,textHeight);
        RelativeLayout.LayoutParams quitButtonParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        quitButtonParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        quitButtonParams.addRule(RelativeLayout.BELOW, R.id.about_button);
        quitButtonParams.setMargins(0, buttonGap,0,0);
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


        knightButton.setBackgroundColor(Color.parseColor("#"+contentArray[0]));
        knightButton.getDrawable().setColorFilter(Color.parseColor("#"+contentArray[7]),PorterDuff.Mode.MULTIPLY);
        mainLayout.setBackgroundColor(Color.parseColor("#"+contentArray[0]));
        mainTitle.setTextColor(Color.parseColor("#"+contentArray[7]));
        mainSlogan.setTextColor(Color.parseColor("#"+contentArray[7]));
        playButton.setTextColor(Color.parseColor("#"+contentArray[7]));
        playButton.setBackgroundColor(Color.parseColor("#"+contentArray[3]));
        createGameButton.setTextColor(Color.parseColor("#"+contentArray[7]));
        createGameButton.setBackgroundColor(Color.parseColor("#"+contentArray[3]));
        checkInvitesButton.setTextColor(Color.parseColor("#"+contentArray[7]));
        checkInvitesButton.setBackgroundColor(Color.parseColor("#"+contentArray[3]));
        aboutButton.setTextColor(Color.parseColor("#"+contentArray[7]));
        aboutButton.setBackgroundColor(Color.parseColor("#"+contentArray[3]));
        quitButton.setTextColor(Color.parseColor("#"+contentArray[7]));
        quitButton.setBackgroundColor(Color.parseColor("#"+contentArray[3]));
        settingsButton.setBackgroundColor(Color.parseColor("#"+contentArray[3]));
        settingsButton.getDrawable().setColorFilter(Color.parseColor("#"+contentArray[7]),PorterDuff.Mode.MULTIPLY);
        achievementsButton.setBackgroundColor(Color.parseColor("#"+contentArray[3]));
        achievementsButton.getDrawable().setColorFilter(Color.parseColor("#"+contentArray[7]),PorterDuff.Mode.MULTIPLY);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#"+contentArray[1]));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        int width = size.x;
        int height = size.y;

        int iconWidth = (int)(width * 0.3);
        int buttonHeight = (int)(height * .075);
        int buttonGap = (int)(height * .015);

        int textHeight = (int)(height * .03);

        RelativeLayout.LayoutParams mainTitleParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, buttonHeight);
        mainTitleParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        mainTitleParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        mainTitleParams.setMargins(0, 0,0,0);
        mainTitle.setLayoutParams(mainTitleParams);
        mainTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX,(int)(height*.05));

        RelativeLayout.LayoutParams mainSloganParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, (int)(height*.025));
        mainSloganParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        mainSloganParams.addRule(RelativeLayout.BELOW,R.id.main_title);
        mainSloganParams.setMargins(0, 0,0,0);
        mainSlogan.setLayoutParams(mainSloganParams);
        mainSlogan.setTextSize(TypedValue.COMPLEX_UNIT_PX,(int)(height*.02));
        mainSlogan.setGravity(Gravity.CENTER);

        RelativeLayout.LayoutParams iconParams = new RelativeLayout.LayoutParams(iconWidth,iconWidth);
        iconParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        iconParams.addRule(RelativeLayout.BELOW, R.id.main_slogan);
        iconParams.setMargins(0, buttonGap,0,0);
        mainImage.setLayoutParams(iconParams);

        playButton.setTextSize(TypedValue.COMPLEX_UNIT_PX,textHeight);
        RelativeLayout.LayoutParams playButtonParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        playButtonParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        playButtonParams.addRule(RelativeLayout.BELOW, R.id.mainImage);
        playButtonParams.setMargins(0, buttonGap,0,0);
        playButton.setLayoutParams(playButtonParams);

        createGameButton.setTextSize(TypedValue.COMPLEX_UNIT_PX,textHeight);
        RelativeLayout.LayoutParams createGameButtonParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        createGameButtonParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        createGameButtonParams.addRule(RelativeLayout.BELOW, R.id.play_button);
        createGameButtonParams.setMargins(0, buttonGap,0,0);
        createGameButton.setLayoutParams(createGameButtonParams);

        checkInvitesButton.setTextSize(TypedValue.COMPLEX_UNIT_PX,textHeight);
        RelativeLayout.LayoutParams checkInvitesButtonParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        checkInvitesButtonParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        checkInvitesButtonParams.addRule(RelativeLayout.BELOW, R.id.create_game_button);
        checkInvitesButtonParams.setMargins(0, buttonGap,0,0);
        checkInvitesButton.setLayoutParams(checkInvitesButtonParams);

        aboutButton.setTextSize(TypedValue.COMPLEX_UNIT_PX,textHeight);
        RelativeLayout.LayoutParams aboutButtonParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        aboutButtonParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        aboutButtonParams.addRule(RelativeLayout.BELOW, R.id.check_invites_button);
        aboutButtonParams.setMargins(0, buttonGap,0,0);
        aboutButton.setLayoutParams(aboutButtonParams);

        quitButton.setTextSize(TypedValue.COMPLEX_UNIT_PX,textHeight);
        RelativeLayout.LayoutParams quitButtonParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        quitButtonParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        quitButtonParams.addRule(RelativeLayout.BELOW, R.id.about_button);
        quitButtonParams.setMargins(0, buttonGap,0,0);
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

        knightButton.setBackgroundColor(Color.parseColor("#"+contentArray[0]));
        knightButton.getDrawable().setColorFilter(Color.parseColor("#"+contentArray[7]),PorterDuff.Mode.MULTIPLY);
        mainLayout.setBackgroundColor(Color.parseColor("#"+contentArray[0]));
        mainTitle.setTextColor(Color.parseColor("#"+contentArray[7]));
        mainSlogan.setTextColor(Color.parseColor("#"+contentArray[7]));
        playButton.setTextColor(Color.parseColor("#"+contentArray[7]));
        playButton.setBackgroundColor(Color.parseColor("#"+contentArray[3]));
        createGameButton.setTextColor(Color.parseColor("#"+contentArray[7]));
        createGameButton.setBackgroundColor(Color.parseColor("#"+contentArray[3]));
        checkInvitesButton.setTextColor(Color.parseColor("#"+contentArray[7]));
        checkInvitesButton.setBackgroundColor(Color.parseColor("#"+contentArray[3]));
        aboutButton.setTextColor(Color.parseColor("#"+contentArray[7]));
        aboutButton.setBackgroundColor(Color.parseColor("#"+contentArray[3]));
        quitButton.setTextColor(Color.parseColor("#"+contentArray[7]));
        quitButton.setBackgroundColor(Color.parseColor("#"+contentArray[3]));
        settingsButton.setBackgroundColor(Color.parseColor("#"+contentArray[3]));
        settingsButton.getDrawable().setColorFilter(Color.parseColor("#"+contentArray[7]),PorterDuff.Mode.MULTIPLY);
        achievementsButton.setBackgroundColor(Color.parseColor("#"+contentArray[3]));
        achievementsButton.getDrawable().setColorFilter(Color.parseColor("#"+contentArray[7]),PorterDuff.Mode.MULTIPLY);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#"+contentArray[1]));
        }
    }


    public void settingsButtonClicked(View view) {
        startActivity(new Intent(MainActivity.this,SettingsActivity.class));
    }

    public void playButtonClicked(View view){
        Intent intent = new Intent(MainActivity.this, SinglePlayerBoard.class);
        startActivity(intent);
    }

    public void createGameButtonClicked(View view)
    {
        //TODO
    }

    public void checkInvitesButtonClicked(View view)
    {
        //TODO

    }

    public void aboutButtonClicked(View view){

        achievementHandler.incrementInMemory(AchievementHandler.OPENED_ABOUT);
        achievementHandler.saveValues();

        startActivity(new Intent(MainActivity.this,AboutActivity.class));
    }

    public void quitButtonClicked(View view){
        finish();
    }


    public void achievementsClicked(View view){
        startActivity(new Intent(MainActivity.this, AchievementsActivity.class));
    }

    public void knightButtonClicked(View view){
        Intent intent = new Intent(MainActivity.this, SinglePlayerBoard.class);
        intent.putExtra("knightsOnly",true);
        //SingleGame.getInstance().newGame();

        achievementHandler.incrementInMemory(AchievementHandler.HORSING_AROUND);

        startActivity(intent);
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

    }

}
