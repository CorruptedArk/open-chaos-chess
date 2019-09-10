package com.openchaoschess.openchaoschess;

/**
 * Created by CorruptedArk
 */

import android.graphics.Color;


import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameActivity;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class SinglePlayerBoard extends BaseGameActivity{

    int boardSize, squareSize, xPosition, yPosition;
    Square[][] board;
    Square defaultSquare;
    Square selected;
    Mover mover;
    SingleGame singleGame;
    ViewGroup boardMain;
    SinglePlayerBoard context;
    MoveThread moveThread;
    RelativeLayout boardLayout;

    Toolbar toolbar;

    File settingsFile;
    FileInputStream fileReader;
    FileOutputStream fileWriter;
    byte[] bytes;
    String[] contentArray;
    int boardColor1;
    int boardColor2;
    int selectColor;
    int pieceColor;

    TextView wonLabel, lostLabel, tieLabel,cantMoveThatLabel, notYourTurnLabel, gameOverLabel, thatSucksLabel, noiceLabel, playerPointLabel, computerPointLabel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        boardMain = (ViewGroup) findViewById(R.id.board_layout);
        boardLayout = (RelativeLayout)findViewById(R.id.board_layout);
        wonLabel = (TextView) findViewById(R.id.won_label);
        lostLabel = (TextView) findViewById(R.id.lost_label);
        cantMoveThatLabel = (TextView) findViewById(R.id.cant_move_that_label);
        notYourTurnLabel = (TextView) findViewById(R.id.not_your_turn_label);
        gameOverLabel = (TextView) findViewById(R.id.game_over_label);
        thatSucksLabel = (TextView) findViewById(R.id.that_sucks_label);
        noiceLabel = (TextView) findViewById(R.id.noice_label);
        playerPointLabel = (TextView) findViewById(R.id.player_points);
        computerPointLabel = (TextView) findViewById(R.id.computer_points);
        tieLabel = (TextView) findViewById(R.id.tie_label);

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

        boardColor1 = Color.parseColor("#"+contentArray[3]);
        boardColor2 = Color.parseColor("#"+contentArray[4]);
        selectColor = Color.parseColor("#"+contentArray[6]);
        pieceColor = Color.parseColor("#"+contentArray[5]);

        context = this;
        defaultSquare = new Square(this,pieceColor);
        selected = defaultSquare;
        selected.setPiece(" ");
        boardSize = 8;

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        squareSize = (width-convertDpToPx(30))/8;
        xPosition = convertDpToPx(15);
        yPosition = convertDpToPx(160);

        mover = new Mover(this);
        singleGame = SingleGame.getInstance();

        if(singleGame.hasBoard()){
            board = singleGame.restoreBoard();
            createSquares(boardSize);
            for(int i = 0; i < boardSize; i++)
                for (int j = 0; j < boardSize; j++){
                    board[i][ j].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            moveThread = new MoveThread(view,context,boardColor1,boardColor2,selectColor);
                            moveThread.start();
                        }
                    });
                    board[i][j].setPieceColor(pieceColor);
                    boardMain.addView(board[i][j]);
                }
        }
        else{
            board = new Square[boardSize][ boardSize];
            for (int i = 0; i < boardSize; i++)
                for (int j = 0; j < boardSize; j++)
                    board[i][ j] = new Square(this,pieceColor);
            startNewGame(getIntent().getBooleanExtra("knightsOnly",false));
            singleGame.newGame();
        }

        playerPointLabel.setText(getResources().getText(R.string.player_points).toString()+ " " + singleGame.getPlayerPoints());
        computerPointLabel.setText(getResources().getText(R.string.computer_points).toString() + " " + singleGame.getComputerPoints());

        wonLabel.bringToFront();
        lostLabel.bringToFront();
        tieLabel.bringToFront();
        cantMoveThatLabel.bringToFront();
        notYourTurnLabel.bringToFront();
        gameOverLabel.bringToFront();
        thatSucksLabel.bringToFront();
        boardMain.invalidate();
        if(mHelper.getApiClient() != null && mHelper.getApiClient().isConnected()) {
            Games.Achievements.unlock(mHelper.getApiClient(), getResources().getString(R.string.achievement_started_game));
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

        RelativeLayout.LayoutParams playerPointParams = new RelativeLayout.LayoutParams(2*iconWidth, (int)(height*.03));
        playerPointParams.addRule(RelativeLayout.BELOW, R.id.toolbar);
        playerPointParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        playerPointParams.setMargins(buttonGap, buttonGap,0,0);
        playerPointLabel.setLayoutParams(playerPointParams);
        playerPointLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX,(int)(height*.025));
        playerPointLabel.setGravity(Gravity.LEFT);

        RelativeLayout.LayoutParams computerPointParams = new RelativeLayout.LayoutParams(2*iconWidth, (int)(height*.03));
        computerPointParams.addRule(RelativeLayout.BELOW, R.id.toolbar);
        computerPointParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        computerPointParams.setMargins(0,buttonGap,buttonGap,0);
        computerPointLabel.setLayoutParams(computerPointParams);
        computerPointLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX,(int)(height*.025));
        computerPointLabel.setGravity(Gravity.RIGHT);

        RelativeLayout.LayoutParams wonParams = new RelativeLayout.LayoutParams(3*iconWidth, (int)(height*.03));
        wonParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        wonParams.addRule(RelativeLayout.CENTER_VERTICAL);
        wonParams.setMargins(0, 0,0,0);
        wonLabel.setLayoutParams(wonParams);
        wonLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX,(int)(height*.025));
        wonLabel.setGravity(Gravity.CENTER);

        RelativeLayout.LayoutParams lostParams = new RelativeLayout.LayoutParams(3*iconWidth, (int)(height*.03));
        lostParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lostParams.addRule(RelativeLayout.CENTER_VERTICAL);
        lostParams.setMargins(0, 0,0,0);
        lostLabel.setLayoutParams(lostParams);
        lostLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX,(int)(height*.025));
        lostLabel.setGravity(Gravity.CENTER);

        RelativeLayout.LayoutParams tieParams = new RelativeLayout.LayoutParams(3*iconWidth, (int)(height*.03));
        tieParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        tieParams.addRule(RelativeLayout.CENTER_VERTICAL);
        tieParams.setMargins(0, 0,0,0);
        tieLabel.setLayoutParams(tieParams);
        tieLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX,(int)(height*.025));
        tieLabel.setGravity(Gravity.CENTER);

        RelativeLayout.LayoutParams cantMoveParams = new RelativeLayout.LayoutParams(3*iconWidth, (int)(height*.03));
        cantMoveParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        cantMoveParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        cantMoveParams.setMargins(0, 0,0,buttonGap);
        cantMoveThatLabel.setLayoutParams(cantMoveParams);
        cantMoveThatLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX,(int)(height*.025));
        cantMoveThatLabel.setGravity(Gravity.CENTER);

        RelativeLayout.LayoutParams noiceParams = new RelativeLayout.LayoutParams(iconWidth, (int)(height*.03));
        noiceParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        noiceParams.addRule(RelativeLayout.BELOW, R.id.toolbar);
        noiceParams.setMargins(0, 2*buttonGap,0,0);
        noiceLabel.setLayoutParams(noiceParams);
        noiceLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX,(int)(height*.025));
        noiceLabel.setGravity(Gravity.CENTER);

        RelativeLayout.LayoutParams notYourTurnParams = new RelativeLayout.LayoutParams(3*iconWidth, (int)(height*.03));
        notYourTurnParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        notYourTurnParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        notYourTurnParams.setMargins(0, 0,0,buttonGap);
        notYourTurnLabel.setLayoutParams(notYourTurnParams);
        notYourTurnLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX,(int)(height*.025));
        notYourTurnLabel.setGravity(Gravity.CENTER);

        RelativeLayout.LayoutParams gameOverParams = new RelativeLayout.LayoutParams(2*iconWidth, (int)(height*.03));
        gameOverParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        gameOverParams.addRule(RelativeLayout.ABOVE,R.id.lost_label);
        gameOverParams.setMargins(0, 0,0,buttonGap);
        gameOverLabel.setLayoutParams(gameOverParams);
        gameOverLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX,(int)(height*.025));
        gameOverLabel.setGravity(Gravity.CENTER);

        RelativeLayout.LayoutParams thatSucksParams = new RelativeLayout.LayoutParams(2*iconWidth, (int)(height*.03));
        thatSucksParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        thatSucksParams.addRule(RelativeLayout.BELOW, R.id.toolbar);
        thatSucksParams.setMargins(0, 2*buttonGap,0,0);
        thatSucksLabel.setLayoutParams(thatSucksParams);
        thatSucksLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX,(int)(height*.02));
        thatSucksLabel.setGravity(Gravity.CENTER);

        boardLayout.setBackgroundColor(Color.parseColor("#"+contentArray[0]));
        toolbar.setBackgroundColor(Color.parseColor("#"+contentArray[2]));
        toolbar.setTitleTextColor(Color.parseColor("#"+contentArray[7]));
        wonLabel.setTextColor(Color.parseColor("#"+contentArray[7]));
        lostLabel.setTextColor(Color.parseColor("#"+contentArray[7]));
        tieLabel.setTextColor(Color.parseColor("#"+contentArray[7]));
        playerPointLabel.setTextColor(Color.parseColor("#"+contentArray[7]));
        computerPointLabel.setTextColor(Color.parseColor("#"+contentArray[7]));
        cantMoveThatLabel.setTextColor(Color.parseColor("#"+contentArray[7]));
        noiceLabel.setTextColor(Color.parseColor("#"+contentArray[7]));
        notYourTurnLabel.setTextColor(Color.parseColor("#"+contentArray[7]));
        gameOverLabel.setTextColor(Color.parseColor("#"+contentArray[7]));
        thatSucksLabel.setTextColor(Color.parseColor("#"+contentArray[7]));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#"+contentArray[1]));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    public void onBackPressed() {
        singleGame.saveBoard(board);
        for(int i = 0; i < boardSize; i++)
            for(int j = 0; j < boardSize; j++)
                boardMain.removeView(board[i][j]);

        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_game:
                newGameButton_Click();
                return true;
            case android.R.id.home:
                singleGame.saveBoard(board);
                for(int i = 0; i < boardSize; i++)
                    for(int j = 0; j < boardSize; j++)
                    boardMain.removeView(board[i][j]);
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }


    private int convertDpToPx(int dp){
        return Math.round(dp*(getResources().getDisplayMetrics().xdpi/DisplayMetrics.DENSITY_DEFAULT));
    }


    private void newGameButton_Click()
    {
        tieLabel.setVisibility(View.INVISIBLE);
        wonLabel.setVisibility(View.INVISIBLE);
        lostLabel.setVisibility(View.INVISIBLE);
        cantMoveThatLabel.setVisibility(View.INVISIBLE);
        notYourTurnLabel.setVisibility(View.INVISIBLE);
        gameOverLabel.setVisibility(View.INVISIBLE);
        thatSucksLabel.setVisibility(View.INVISIBLE);
        noiceLabel.setVisibility(View.INVISIBLE);

        while(moveThread != null && moveThread.isAlive());

        selected = defaultSquare;
        clearPieces();
        singleGame.newGame();
        startNewGame(getIntent().getBooleanExtra("knightsOnly",false));
        playerPointLabel.setText(getResources().getText(R.string.player_points).toString()+ " " + singleGame.getPlayerPoints());
        computerPointLabel.setText(getResources().getText(R.string.computer_points).toString() + " " + singleGame.getComputerPoints());
        return;
    }

    void clearPieces()
    {
        for (int i = 0; i < boardSize; i++)
            for (int j = 0; j < boardSize; j++)
            {
                board[i][ j].setTeam(0);
                board[i][ j].setPiece(" ");
                board[i][ j].setPieceCount(0);
            }

        return;
    }

    void startNewGame(boolean knightsOnly)
    {
        drawBoard(knightsOnly ,boardSize);
        return;
    }

    public boolean bishopTie(){
        boolean bishopTie;
        List<Square> players = singleGame.movablePlayers(mover,board);
        List<Square> computers = singleGame.movableComputers(mover,board);

        boolean allPlayersAreBishops = true;
        boolean allComputersAreBishops = true;

        for(int i = 0; i < players.size(); i++)
            if(players.get(i).getPiece() != "B"){
                allPlayersAreBishops = false;
                break;
            }
        for(int i = 0; i < computers.size(); i++)
            if(computers.get(i).getPiece() != "B"){
                allComputersAreBishops = false;
                break;
            }

        if(allComputersAreBishops && allPlayersAreBishops && (players.size() == 1) && (computers.size() == 1) && (players.get(0).getColor() != computers.get(0).getColor()))
            bishopTie = true;
        else
            bishopTie = false;

        return bishopTie;
    }

    public synchronized void moveSelectedButton_Click(final View view)
    {
        if(mHelper.getApiClient() != null && mHelper.getApiClient().isConnected()) {
            Games.Achievements.unlock(mHelper.getApiClient(), getResources().getString(R.string.achievement_started_game));
        }
        thatSucksLabel.post(new Runnable() {
            @Override
            public void run() {
                thatSucksLabel.setVisibility(View.INVISIBLE);
            }
        });
        noiceLabel.post(new Runnable() {
            @Override
            public void run() {
                noiceLabel.setVisibility(View.INVISIBLE);
            }
        });
        int playerScore = singleGame.getPlayerPoints();
        if (singleGame.getTurn() == -1)
        {
            if(!singleGame.getCanComputerMove(mover,board) && !singleGame.getCanPlayerMove(mover,board)){
                cantMoveThatLabel.post(new Runnable() {
                    @Override
                    public void run() {
                        cantMoveThatLabel.setVisibility(View.VISIBLE);
                    }
                });
                gameOverLabel.post(new Runnable() {
                    @Override
                    public void run() {
                        gameOverLabel.setVisibility(View.VISIBLE);
                    }
                });
                singleGame.setTurn(0);
                if(singleGame.getComputerPoints()== singleGame.getPlayerPoints())
                    tieLabel.post(new Runnable() {
                        @Override
                        public void run() {
                            tieLabel.setVisibility(View.VISIBLE);
                            if(mHelper.getApiClient()!= null && mHelper.getApiClient().isConnected()) {
                                Games.Achievements.unlock(mHelper.getApiClient(), getString(R.string.achievement_breaking_even));
                                Games.Achievements.increment(mHelper.getApiClient(), getString(R.string.achievement_exceptionally_average), 1);
                                Games.Achievements.increment(mHelper.getApiClient(), getString(R.string.achievement_master_of_balance), 1);
                                Games.Achievements.increment(mHelper.getApiClient(), getString(R.string.achievement_hooked), 1);
                                Games.Achievements.increment(mHelper.getApiClient(), getString(R.string.achievement_chaos_junkie), 1);
                                Games.Achievements.increment(mHelper.getApiClient(), getString(R.string.achievement_get_help), 1);
                            }
                        }
                    });
                else if(singleGame.getComputerPoints()< singleGame.getPlayerPoints()) {
                    wonLabel.post(new Runnable() {
                        @Override
                        public void run() {
                            wonLabel.setVisibility(View.VISIBLE);
                            if(mHelper.getApiClient()!= null && mHelper.getApiClient().isConnected()) {
                                Games.Achievements.unlock(mHelper.getApiClient(), getResources().getString(R.string.achievement_noice_dude));
                                Games.Achievements.increment(mHelper.getApiClient(), getString(R.string.achievement_winner), 1);
                                Games.Achievements.increment(mHelper.getApiClient(), getString(R.string.achievement_master_of_chaos), 1);
                                Games.Achievements.increment(mHelper.getApiClient(), getString(R.string.achievement_hooked), 1);
                                Games.Achievements.increment(mHelper.getApiClient(), getString(R.string.achievement_chaos_junkie), 1);
                                Games.Achievements.increment(mHelper.getApiClient(), getString(R.string.achievement_get_help), 1);
                                if(singleGame.getComputerPoints() == 0)
                                    Games.Achievements.unlock(mHelper.getApiClient(),getString(R.string.achievement_untouchable));
                            }
                        }
                    });

                }
                else
                    lostLabel.post(new Runnable() {
                        @Override
                        public void run() {
                            lostLabel.setVisibility(View.VISIBLE);
                            if(mHelper.getApiClient()!= null && mHelper.getApiClient().isConnected()) {
                                Games.Achievements.unlock(mHelper.getApiClient(), getResources().getString(R.string.achievement_bruh_));
                                Games.Achievements.increment(mHelper.getApiClient(), getString(R.string.achievement_loser), 1);
                                Games.Achievements.increment(mHelper.getApiClient(), getString(R.string.achievement_master_of_failure), 1);
                                Games.Achievements.increment(mHelper.getApiClient(), getString(R.string.achievement_hooked), 1);
                                Games.Achievements.increment(mHelper.getApiClient(), getString(R.string.achievement_chaos_junkie), 1);
                                Games.Achievements.increment(mHelper.getApiClient(), getString(R.string.achievement_get_help), 1);
                                if(singleGame.getPlayerPoints() == 0)
                                    Games.Achievements.unlock(mHelper.getApiClient(),getString(R.string.achievement_slaughtered));
                            }
                        }
                    });
            }
            else if(bishopTie()){
                gameOverLabel.post(new Runnable() {
                    @Override
                    public void run() {
                        gameOverLabel.setVisibility(View.VISIBLE);
                    }
                });
                singleGame.setTurn(0);
                if(singleGame.getComputerPoints()== singleGame.getPlayerPoints())
                    tieLabel.post(new Runnable() {
                        @Override
                        public void run() {
                            tieLabel.setVisibility(View.VISIBLE);
                            if(mHelper.getApiClient()!= null && mHelper.getApiClient().isConnected()) {
                                Games.Achievements.unlock(mHelper.getApiClient(), getString(R.string.achievement_breaking_even));
                                Games.Achievements.increment(mHelper.getApiClient(), getString(R.string.achievement_exceptionally_average), 1);
                                Games.Achievements.increment(mHelper.getApiClient(), getString(R.string.achievement_master_of_balance), 1);
                                Games.Achievements.increment(mHelper.getApiClient(), getString(R.string.achievement_hooked), 1);
                                Games.Achievements.increment(mHelper.getApiClient(), getString(R.string.achievement_chaos_junkie), 1);
                                Games.Achievements.increment(mHelper.getApiClient(), getString(R.string.achievement_get_help), 1);
                            }
                        }
                    });
                else if(singleGame.getComputerPoints()< singleGame.getPlayerPoints()) {
                    wonLabel.post(new Runnable() {
                        @Override
                        public void run() {
                            wonLabel.setVisibility(View.VISIBLE);
                            if(mHelper.getApiClient()!= null && mHelper.getApiClient().isConnected()) {
                                Games.Achievements.unlock(mHelper.getApiClient(), getResources().getString(R.string.achievement_noice_dude));
                                Games.Achievements.increment(mHelper.getApiClient(), getString(R.string.achievement_winner), 1);
                                Games.Achievements.increment(mHelper.getApiClient(), getString(R.string.achievement_master_of_chaos), 1);
                                Games.Achievements.increment(mHelper.getApiClient(), getString(R.string.achievement_hooked), 1);
                                Games.Achievements.increment(mHelper.getApiClient(), getString(R.string.achievement_chaos_junkie), 1);
                                Games.Achievements.increment(mHelper.getApiClient(), getString(R.string.achievement_get_help), 1);
                                if(singleGame.getComputerPoints() == 0)
                                    Games.Achievements.unlock(mHelper.getApiClient(),getString(R.string.achievement_untouchable));
                            }
                        }
                    });

                }
                else
                    lostLabel.post(new Runnable() {
                        @Override
                        public void run() {
                            lostLabel.setVisibility(View.VISIBLE);
                            if(mHelper.getApiClient()!= null && mHelper.getApiClient().isConnected()) {
                                Games.Achievements.unlock(mHelper.getApiClient(), getResources().getString(R.string.achievement_bruh_));
                                Games.Achievements.increment(mHelper.getApiClient(), getString(R.string.achievement_loser), 1);
                                Games.Achievements.increment(mHelper.getApiClient(), getString(R.string.achievement_master_of_failure), 1);
                                Games.Achievements.increment(mHelper.getApiClient(), getString(R.string.achievement_hooked), 1);
                                Games.Achievements.increment(mHelper.getApiClient(), getString(R.string.achievement_chaos_junkie), 1);
                                Games.Achievements.increment(mHelper.getApiClient(), getString(R.string.achievement_get_help), 1);
                                if(singleGame.getPlayerPoints() == 0)
                                    Games.Achievements.unlock(mHelper.getApiClient(),getString(R.string.achievement_slaughtered));
                            }
                        }
                    });
            } else if(selected.getI() == -1) {
                // Nothing selected
                cantMoveThatLabel.post(new Runnable() {
                    @Override
                    public void run() {
                        cantMoveThatLabel.setVisibility(View.INVISIBLE);
                    }
                });
                notYourTurnLabel.post(new Runnable() {
                    @Override
                    public void run() {
                        notYourTurnLabel.setVisibility(View.INVISIBLE);
                    }
                });


            } else if (mover.movePiece(board, board[selected.getI()][ selected.getJ()], singleGame)) {
                selected.post(new Runnable() {
                    @Override
                    public void run() {
                        int color;

                        if(selected.getColor())
                            color = boardColor1;
                        else
                            color = boardColor2;

                        selected.setBackgroundColor(color);
                        selected = defaultSquare;
                    }
                });

                if(singleGame.getPlayerPoints() > playerScore){
                    noiceLabel.post(new Runnable() {
                        @Override
                        public void run() {
                            noiceLabel.setVisibility(View.VISIBLE);
                            thatSucksLabel.setVisibility(View.INVISIBLE);
                        }
                    });
                }
                else {
                    noiceLabel.post(new Runnable() {
                        @Override
                        public void run() {
                            noiceLabel.setVisibility(View.INVISIBLE);
                        }
                    });
                }

                cantMoveThatLabel.post(new Runnable() {
                    @Override
                    public void run() {
                        cantMoveThatLabel.setVisibility(View.INVISIBLE);
                    }
                });
                notYourTurnLabel.post(new Runnable() {
                    @Override
                    public void run() {
                        notYourTurnLabel.setVisibility(View.INVISIBLE);
                    }
                });
                singleGame.incrementMoveCount();
                singleGame.setTurn(1);


                playerPointLabel.post(new Runnable() {
                    @Override
                    public void run() {
                        playerPointLabel.setText(getResources().getText(R.string.player_points).toString() + " " + singleGame.getPlayerPoints());
                    }
                });

                do {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (singleGame.getCanComputerMove(mover, board))
                        moveComputer();
                    computerPointLabel.post(new Runnable() {
                        @Override
                        public void run() {
                            computerPointLabel.setText(getResources().getText(R.string.computer_points).toString() + " " + singleGame.getComputerPoints());
                        }
                    });
                }  while(!singleGame.getCanPlayerMove(mover,board) && singleGame.getCanComputerMove(mover,board) && singleGame.getPlayerCount() > 0);

                if (singleGame.getPlayerCount() == 0)
                {
                    lostLabel.post(new Runnable() {
                        @Override
                        public void run() {
                            lostLabel.setVisibility(View.VISIBLE);
                            if(mHelper.getApiClient()!= null && mHelper.getApiClient().isConnected()) {
                                Games.Achievements.unlock(mHelper.getApiClient(), getResources().getString(R.string.achievement_bruh_));
                                Games.Achievements.increment(mHelper.getApiClient(), getString(R.string.achievement_loser), 1);
                                Games.Achievements.increment(mHelper.getApiClient(), getString(R.string.achievement_master_of_failure), 1);
                                Games.Achievements.increment(mHelper.getApiClient(), getString(R.string.achievement_hooked), 1);
                                Games.Achievements.increment(mHelper.getApiClient(), getString(R.string.achievement_chaos_junkie), 1);
                                Games.Achievements.increment(mHelper.getApiClient(), getString(R.string.achievement_get_help), 1);
                                if(singleGame.getPlayerPoints() == 0)
                                    Games.Achievements.unlock(mHelper.getApiClient(),getString(R.string.achievement_slaughtered));
                            }
                        }
                    });

                    singleGame.setTurn(0);
                }
                else if (singleGame.getComputerCount() == 0)
                {
                    wonLabel.post(new Runnable() {
                        @Override
                        public void run() {
                            wonLabel.setVisibility(View.VISIBLE);
                            if(mHelper.getApiClient()!= null && mHelper.getApiClient().isConnected()) {
                                Games.Achievements.unlock(mHelper.getApiClient(), getResources().getString(R.string.achievement_noice_dude));
                                Games.Achievements.increment(mHelper.getApiClient(), getString(R.string.achievement_winner), 1);
                                Games.Achievements.increment(mHelper.getApiClient(), getString(R.string.achievement_master_of_chaos), 1);
                                Games.Achievements.increment(mHelper.getApiClient(), getString(R.string.achievement_hooked), 1);
                                Games.Achievements.increment(mHelper.getApiClient(), getString(R.string.achievement_chaos_junkie), 1);
                                Games.Achievements.increment(mHelper.getApiClient(), getString(R.string.achievement_get_help), 1);
                                if(singleGame.getComputerPoints() == 0)
                                    Games.Achievements.unlock(mHelper.getApiClient(),getString(R.string.achievement_untouchable));
                            }
                        }
                    });

                    singleGame.setTurn(0);

                }
            }
            else {
                cantMoveThatLabel.post(new Runnable() {
                    @Override
                    public void run() {
                        cantMoveThatLabel.setVisibility(View.VISIBLE);
                    }
                });
                notYourTurnLabel.post(new Runnable() {
                    @Override
                    public void run() {
                        notYourTurnLabel.setVisibility(View.INVISIBLE);
                    }
                });

            }


        }
        else if (singleGame.getTurn() == 1) {
            if(singleGame.getCanComputerMove(mover,board)) {
                cantMoveThatLabel.post(new Runnable() {
                    @Override
                    public void run() {
                        cantMoveThatLabel.setVisibility(View.VISIBLE);
                    }
                });
                notYourTurnLabel.post(new Runnable() {
                    @Override
                    public void run() {
                        notYourTurnLabel.setVisibility(View.VISIBLE);
                    }
                });
            }else{
                singleGame.setTurn(-1);
                moveSelectedButton_Click(view);
            }
        }
        else {
            gameOverLabel.post(new Runnable() {
                @Override
                public void run() {
                    gameOverLabel.setVisibility(View.VISIBLE);
                }
            });

        }


    }

    synchronized void moveComputer()
    {
        thatSucksLabel.post(new Runnable() {
            @Override
            public void run() {
                thatSucksLabel.setVisibility(View.INVISIBLE);
            }
        });
        noiceLabel.post(new Runnable() {
            @Override
            public void run() {
                noiceLabel.setVisibility(View.INVISIBLE);
            }
        });

        List<Square> computerPieces = new ArrayList<>();
        Square picked;
        Random rand = new Random();

        for (int i = 0; i < boardSize; i++)
            for (int j = 0; j < boardSize; j++)
                if (board[i][ j].getTeam() == 1)
        computerPieces.add(board[i][ j]);
        if (computerPieces.size() > 0)
        {
            int computerScore = singleGame.getComputerPoints();
            picked = computerPieces.get(rand.nextInt(computerPieces.size()));
            while (!mover.movePiece( board,  board[picked.getI()][ picked.getJ()], singleGame))
            {
                computerPieces.remove(picked);
                if (computerPieces.size() == 0)
                    break;
                picked = computerPieces.get(rand.nextInt(computerPieces.size()));
            }

            if(selected.getTeam() == 1)
            {
                selected.post(new Runnable() {
                    @Override
                    public void run() {
                        int color;

                        if(selected.getColor())
                            color = boardColor1;
                        else
                            color = boardColor2;

                        selected.setBackgroundColor(color);
                        selected = defaultSquare;
                    }
                });
            }

            if (singleGame.getComputerPoints() > computerScore)
                thatSucksLabel.post(new Runnable() {
                    @Override
                    public void run() {
                        thatSucksLabel.setVisibility(View.VISIBLE);
                        noiceLabel.setVisibility(View.INVISIBLE);
                    }
                });
            else
                thatSucksLabel.post(new Runnable() {
                    @Override
                    public void run() {
                        thatSucksLabel.setVisibility(View.INVISIBLE);
                    }
                });


            singleGame.setTurn(-1);

            notYourTurnLabel.post(new Runnable() {
                @Override
                public void run() {
                    notYourTurnLabel.setVisibility(View.INVISIBLE);
                }
            });
        }

    }

    void drawBoard(boolean knightsOnly, int size)
    {
        createSquares(size);

        startPieces(knightsOnly,size);

        return;
    }

    void createSquares(int size)
    {
        boolean colorPicker = false;
        int color;

        for (int i = 0; i < size; i++)
        {

            colorPicker = !colorPicker;
            for (int j = 0; j < size; j++)
            {

                if (colorPicker)
                    color = boardColor1;
                else
                    color = boardColor2;

                board[i][ j].setI(i);
                board[i][ j].setJ(j);
                board[i][ j].setColor(colorPicker);
                board[i][ j].setBackgroundColor(color);


                colorPicker = !colorPicker;
                board[i][ j].setX(xPosition + i * squareSize);
                board[i][ j].setY(yPosition + j * squareSize);
                board[i][ j].setLayoutParams(new RelativeLayout.LayoutParams(squareSize, squareSize));
                if (singleGame.getGameCount() == 0)
                {
                    board[i][ j].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            moveThread = new MoveThread(view,context,boardColor1,boardColor2,selectColor);
                            moveThread.start();
                        }
                    });
                    boardMain.addView(board[i][j]);
                }


            }
        }

        return;
    }

    void startPieces(boolean knightsOnly, int size)
    {
        if(knightsOnly)
        {
            // Set Teams and pieces
            for (int i = 0; i < size; i++) {
                board[i][0].setTeam(1);
                board[i][0].setPiece("Kn");
                board[i][1].setTeam(1);
                board[i][1].setPiece("Kn");
                board[i][6].setTeam(-1);
                board[i][6].setPiece("Kn");
                board[i][7].setTeam(-1);
                board[i][7].setPiece("Kn");
            }

        }
        else {
            // Set Teams
            for (int i = 0; i < size; i++) {
                board[i][0].setTeam(1);
                board[i][1].setTeam(1);
                board[i][6].setTeam(-1);
                board[i][7].setTeam(-1);
            }

            // Set Pawns
            for (int i = 0; i < size; i++)
                board[i][6].setPiece("P");

            for (int i = 0; i < size; i++)
                board[i][1].setPiece("P");

            // Set Rooks
            board[0][0].setPiece("R");
            board[7][0].setPiece("R");
            board[0][7].setPiece("R");
            board[7][7].setPiece("R");

            // Set Knights
            board[1][0].setPiece("Kn");
            board[6][0].setPiece("Kn");
            board[1][7].setPiece("Kn");
            board[6][7].setPiece("Kn");

            // Set Bishops
            board[2][0].setPiece("B");
            board[5][0].setPiece("B");
            board[2][7].setPiece("B");
            board[5][7].setPiece("B");

            // Set Kings
            board[3][0].setPiece("Ki");
            board[3][7].setPiece("Ki");

            // Set Queens
            board[4][0].setPiece("Q");
            board[4][7].setPiece("Q");
        }

        return;
    }


    @Override
    public void onSignInFailed() {

    }

    @Override
    public void onSignInSucceeded() {

    }
}
