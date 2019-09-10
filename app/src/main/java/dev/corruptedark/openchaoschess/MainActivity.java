package com.openchaoschess.openchaoschess;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.games.TurnBasedMultiplayerClient;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatchConfig;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.example.games.basegameutils.BaseGameActivity;

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
public class MainActivity extends BaseGameActivity {
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
    SignInButton signInButton;
    Button signOutButton;
    ImageButton knightButton;
    ImageView mainImage;

    File settingsFile;
    InputStream fileReader;
    OutputStream fileWriter;
    byte[] bytes;
    String[] contentArray;
    Handler knightHandler;

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

        mainTitle = (TextView)findViewById(R.id.main_title);
        mainSlogan = (TextView)findViewById(R.id.main_slogan);
        playButton = (Button)findViewById(R.id.play_button);
        createGameButton = (Button)findViewById(R.id.create_game_button);
        checkInvitesButton = (Button)findViewById(R.id.check_invites_button);
        aboutButton = (Button)findViewById(R.id.about_button);
        quitButton = (Button)findViewById(R.id.quit_button);
        settingsButton = (ImageButton)findViewById(R.id.settings_button);
        achievementsButton = (ImageButton)findViewById(R.id.achievements_button);
        signInButton = (SignInButton)findViewById(R.id.sign_in_button);
        signOutButton = (Button)findViewById(R.id.sign_out_button);
        knightButton = (ImageButton)findViewById(R.id.knight_button);
        mainImage = (ImageView)findViewById(R.id.mainImage);

        mainLayout = (RelativeLayout)findViewById(R.id.activity_main);

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

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInButtonClicked(view);
            }
        });

        if(mHelper.isSignedIn()) {
            Toast.makeText(this,"You're already signed in to Play Games.",Toast.LENGTH_SHORT).show();
            signInButton.setVisibility(View.GONE);
            signOutButton.setVisibility(View.VISIBLE);
        }
        else{
            Toast.makeText(this,"You aren't logged in to Play Games.",Toast.LENGTH_SHORT).show();
            signInButton.setVisibility(View.VISIBLE);
            signOutButton.setVisibility(View.GONE);
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

        RelativeLayout.LayoutParams mainTitleParams = new RelativeLayout.LayoutParams(2*iconWidth, buttonHeight);
        mainTitleParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        mainTitleParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        mainTitleParams.setMargins(0, 0,0,0);
        mainTitle.setLayoutParams(mainTitleParams);
        mainTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX,(int)(height*.05));

        RelativeLayout.LayoutParams mainSloganParams = new RelativeLayout.LayoutParams(2*iconWidth, (int)(height*.025));
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

        RelativeLayout.LayoutParams signInButtonParams = new RelativeLayout.LayoutParams(iconWidth, buttonHeight);
        signInButtonParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        signInButtonParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        signInButtonParams.setMargins(0, 0,0,0);
        signInButton.setLayoutParams(signInButtonParams);

        RelativeLayout.LayoutParams signOutButtonParams = new RelativeLayout.LayoutParams(iconWidth, buttonHeight);
        signOutButtonParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        signOutButtonParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        signOutButtonParams.setMargins(0, 0,0,0);
        signOutButton.setLayoutParams(signOutButtonParams);
        signOutButton.setTextSize(TypedValue.COMPLEX_UNIT_PX,(int)(height*.02));
        signOutButton.setGravity(Gravity.CENTER);

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
        signOutButton.setBackgroundColor(Color.parseColor("#"+contentArray[3]));
        signOutButton.setTextColor(Color.parseColor("#"+contentArray[7]));
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

        RelativeLayout.LayoutParams mainTitleParams = new RelativeLayout.LayoutParams(2*iconWidth, buttonHeight);
        mainTitleParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        mainTitleParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        mainTitleParams.setMargins(0, 0,0,0);
        mainTitle.setLayoutParams(mainTitleParams);
        mainTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX,(int)(height*.05));

        RelativeLayout.LayoutParams mainSloganParams = new RelativeLayout.LayoutParams(2*iconWidth, (int)(height*.025));
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

        RelativeLayout.LayoutParams signInButtonParams = new RelativeLayout.LayoutParams(iconWidth, buttonHeight);
        signInButtonParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        signInButtonParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        signInButtonParams.setMargins(0, 0,0,0);
        signInButton.setLayoutParams(signInButtonParams);

        RelativeLayout.LayoutParams signOutButtonParams = new RelativeLayout.LayoutParams(iconWidth, buttonHeight);
        signOutButtonParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        signOutButtonParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        signOutButtonParams.setMargins(0, 0,0,0);
        signOutButton.setLayoutParams(signOutButtonParams);
        signOutButton.setTextSize(TypedValue.COMPLEX_UNIT_PX,(int)(height*.02));
        signOutButton.setGravity(Gravity.CENTER);

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
        signOutButton.setBackgroundColor(Color.parseColor("#"+contentArray[3]));
        signOutButton.setTextColor(Color.parseColor("#"+contentArray[7]));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#"+contentArray[1]));
        }
    }

    @Override
    public void onSignInFailed() {
        Toast.makeText(this,"Sign in failed. Achievements will not be saved.",Toast.LENGTH_SHORT).show();
        signInButton.setVisibility(View.VISIBLE);
        signOutButton.setVisibility(View.GONE);
    }

    @Override
    public void onSignInSucceeded() {
        Toast.makeText(this,"Sign in successful. Achievements will be saved.",Toast.LENGTH_SHORT).show();
        signInButton.setVisibility(View.GONE);
        signOutButton.setVisibility(View.VISIBLE);


    }

    public void settingsButtonClicked(View view) {
        startActivity(new Intent(MainActivity.this,SettingsActivity.class));
    }

    public void playButtonClicked(View view){
        SingleGame.saveHelper(mHelper);
        Intent intent = new Intent(MainActivity.this, SinglePlayerBoard.class);
        startActivity(intent);
    }

    public void createGameButtonClicked(View view)
    {
        boolean allowAutoMatch = true;
        Games.getTurnBasedMultiplayerClient(this,GoogleSignIn.getAccountForScopes(this, new Scope(Scopes.GAMES_LITE)))
                .getSelectOpponentsIntent(1,1, allowAutoMatch)
                .addOnSuccessListener(new OnSuccessListener<Intent>() {
                    @Override
                    public void onSuccess(Intent intent) {
                        startActivityForResult(intent, SELECT_PLAYERS);
                    }
                });
    }

    public void checkInvitesButtonClicked(View view)
    {
        //TODO
        Games.getTurnBasedMultiplayerClient(this,GoogleSignIn.getAccountForScopes(this, new Scope(Scopes.GAMES_LITE))).getInboxIntent()
                .addOnSuccessListener(new OnSuccessListener<Intent>() {
            @Override
            public void onSuccess(Intent intent) {
                startActivityForResult(intent, CHECK_INBOX);
            }
        });
    }

    public void aboutButtonClicked(View view){
        SingleGame.saveHelper(mHelper);
        if(mHelper.getApiClient() != null && mHelper.getApiClient().isConnected())
            Games.Achievements.unlock(mHelper.getApiClient(),getString(R.string.achievement_informed_player));
        startActivity(new Intent(MainActivity.this,AboutActivity.class));
    }

    public void quitButtonClicked(View view){
        finish();
    }

    public void signInButtonClicked(View view){
        Toast.makeText(this, "Attempting to sign in.", Toast.LENGTH_SHORT).show();
        mHelper.beginUserInitiatedSignIn();
        signInButton.setVisibility(View.GONE);
        signOutButton.setVisibility(View.VISIBLE);
    }

    public void signOutButtonClicked(View view){
        Toast.makeText(this, "Signing out.", Toast.LENGTH_SHORT).show();
        mHelper.signOut();
        signInButton.setVisibility(View.VISIBLE);
        signOutButton.setVisibility(View.GONE);
    }

    public void achievementsClicked(View view){
        if(mHelper.getApiClient() != null && mHelper.getApiClient().isConnected())
            startActivityForResult(Games.Achievements.getAchievementsIntent(mHelper.getApiClient()),
                    REQUEST_ACHIEVEMENTS);
        else
            Toast.makeText(this, "Achievements can't be accessed when Google Play Games Services isn't connected.",Toast.LENGTH_SHORT).show();
    }

    public void knightButtonClicked(View view){
        SingleGame.saveHelper(mHelper);
        Intent intent = new Intent(MainActivity.this, SinglePlayerBoard.class);
        intent.putExtra("knightsOnly",true);
        if(mHelper.getApiClient() != null && mHelper.getApiClient().isConnected())
            Games.Achievements.unlock(mHelper.getApiClient(),getString(R.string.achievement_horsing_around));
        SingleGame.getInstance().newGame();
        startActivity(intent);
    }

    private int convertDpToPx(int dp){
        return Math.round(dp*(getResources().getDisplayMetrics().xdpi/ DisplayMetrics.DENSITY_DEFAULT));

    }

    protected void initializeGameData(int request) {
        //TODO
        MultiGame.saveHelper(mHelper);

        if (request == SELECT_PLAYERS) {
            MultiGame.getInstance().setTurn(YOU);
        } else {
            MultiGame.getInstance().setTurn(OPPONENT);
        }

        MultiGame.getInstance().newGame();

    }

    protected void showTurnUI(TurnBasedMatch match)
    {
        //TODO
        Intent intent = new Intent(MainActivity.this, MultiPlayerBoard.class);
        intent.putExtra("knightsOnly", false);
        intent.putExtra("match", match);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(final int request, int response, Intent data) {
        if(request == REQUEST_ACHIEVEMENTS &&
                response == GamesActivityResultCodes.RESULT_RECONNECT_REQUIRED){
            mHelper.disconnect();
            signInButton.setVisibility(View.VISIBLE);
            signOutButton.setVisibility(View.GONE);
        }
        else if(request == SELECT_PLAYERS) {
            if(response != Activity.RESULT_OK)
            {
                Toast.makeText(this,"Multiplayer failed", Toast.LENGTH_SHORT);
                return;
            }
            ArrayList<String> invitees = data.getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);

            Bundle autoMatchCriteria = null;
            int minAutoPlayers = data.getIntExtra(Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS,0);
            int maxAutoPlayers = data.getIntExtra(Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 0);

            TurnBasedMatchConfig.Builder builder = TurnBasedMatchConfig.builder().addInvitedPlayers(invitees);

            if(minAutoPlayers > 0)
            {
                builder.setAutoMatchCriteria(RoomConfig.createAutoMatchCriteria(minAutoPlayers, maxAutoPlayers, 0));
            }
            Games.getTurnBasedMultiplayerClient(this, GoogleSignIn.getAccountForScopes(this, new Scope(Scopes.GAMES_LITE)))
                    .createMatch(builder.build()).addOnCompleteListener(new OnCompleteListener<TurnBasedMatch>() {
                @Override
                public void onComplete(@NonNull Task<TurnBasedMatch> task) {
                    if(task.isSuccessful())
                    {
                        TurnBasedMatch match = task.getResult();
                        if (match.getData() == null)
                        {
                            initializeGameData(request);
                        }

                        showTurnUI(match);
                    }
                    else
                    {
                        int status = CommonStatusCodes.DEVELOPER_ERROR;
                        Exception exception = task.getException();
                        if (exception instanceof ApiException)
                        {
                            status = ((ApiException) exception).getStatusCode();
                            exception.printStackTrace();
                        }
                        Toast.makeText(getApplicationContext(),"Send invite failed", Toast.LENGTH_SHORT);// handle error
                    }
                }
            });

        }
        else if (request == CHECK_INBOX) {
            Games.getTurnBasedMultiplayerClient(this, GoogleSignIn.getAccountForScopes(this, new Scope(Scopes.GAMES_LITE))).acceptInvitation(data.getExtras().getParcelable(Multiplayer.EXTRA_TURN_BASED_MATCH).toString()).addOnCompleteListener(
                    new OnCompleteListener<TurnBasedMatch>() {
                        @Override
                        public void onComplete(@NonNull Task<TurnBasedMatch> task) {
                            if(task.isSuccessful())
                            {
                                TurnBasedMatch match = task.getResult();
                                if (match.getData() == null)
                                {
                                    initializeGameData(request);
                                }

                                showTurnUI(match);
                            }
                            else
                            {
                                int status = CommonStatusCodes.DEVELOPER_ERROR;
                                Exception exception = task.getException();
                                if (exception instanceof ApiException)
                                {
                                    status = ((ApiException) exception).getStatusCode();
                                    exception.printStackTrace();
                                }
                                Toast.makeText(getApplicationContext(),"Recieve invite failed", Toast.LENGTH_SHORT);// handle error
                            }
                        }
                    }
            );
        }
        else{
            mHelper.onActivityResult(request,response,data);
        }


    }

}
