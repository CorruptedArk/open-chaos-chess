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

import android.content.res.ColorStateList;
import android.graphics.Color;


import android.graphics.ColorFilter;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MarginLayoutParamsCompat;
import androidx.core.view.MenuItemCompat;
import androidx.core.widget.CompoundButtonCompat;
import androidx.core.widget.TextViewCompat;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class SinglePlayerBoard extends AppCompatActivity {

    public final int YOU = -1;
    public final int OPPONENT = 1;
    public final int NONE = 0;

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
    private Square animatedSquare;

    Toolbar toolbar;

    AchievementHandler achievementHandler;

    ColorManager colorManager;

    int boardColor1;
    int boardColor2;
    int selectColor;
    int pieceColor;

    private boolean bloodThirsty;
    private boolean bloodThirstQueued = false;

    TextView wonLabel, lostLabel, tieLabel, cantMoveThatLabel, notYourTurnLabel, gameOverLabel, thatSucksLabel, noiceLabel, playerPointLabel, computerPointLabel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        achievementHandler = AchievementHandler.getInstance(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        boardMain = (ViewGroup) findViewById(R.id.board_layout);
        boardLayout = (RelativeLayout) findViewById(R.id.board_layout);
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


        colorManager = ColorManager.getInstance(this);

        boardColor1 = colorManager.getColorFromFile(ColorManager.BOARD_COLOR_1);
        boardColor2 = colorManager.getColorFromFile(ColorManager.BOARD_COLOR_2);
        selectColor = colorManager.getColorFromFile(ColorManager.SELECTION_COLOR);
        pieceColor = colorManager.getColorFromFile(ColorManager.PIECE_COLOR);

        context = this;
        defaultSquare = new Square(this, pieceColor);
        selected = defaultSquare;
        selected.setPiece(Piece.NONE);
        boardSize = 8;

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        squareSize = (width - convertDpToPx(30)) / 8;
        xPosition = convertDpToPx(15);
        yPosition = convertDpToPx(160);

        mover = new Mover(this);
        singleGame = SingleGame.getInstance();

        if (singleGame.hasBoard()) {
            board = singleGame.restoreBoard();
            createSquares(boardSize);
            for (int i = 0; i < boardSize; i++)
                for (int j = 0; j < boardSize; j++) {
                    board[i][j].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            moveThread = new MoveThread(view, context, boardColor1, boardColor2, selectColor);
                            moveThread.start();
                        }
                    });
                    board[i][j].setPieceColor(pieceColor);
                    boardMain.addView(board[i][j]);
                }
        } else {
            board = new Square[boardSize][boardSize];
            for (int i = 0; i < boardSize; i++)
                for (int j = 0; j < boardSize; j++)
                    board[i][j] = new Square(this, pieceColor);
            startNewGame(getIntent().getBooleanExtra("knightsOnly", false));
            singleGame.newGame();
        }

        animatedSquare = new Square(this, pieceColor);
        animatedSquare.setPiece(Piece.NONE);
        animatedSquare.setVisibility(View.GONE);
        animatedSquare.setX(0);
        animatedSquare.setY(0);
        animatedSquare.setBackgroundColor(Color.TRANSPARENT);
        animatedSquare.setLayoutParams(new RelativeLayout.LayoutParams(squareSize, squareSize));

        singleGame.setAnimatedSquare(animatedSquare);

        boardMain.addView(animatedSquare);

        playerPointLabel.setText(getResources().getText(R.string.player_points).toString() + " " + singleGame.getPlayerPoints());
        computerPointLabel.setText(getResources().getText(R.string.computer_points).toString() + " " + singleGame.getComputerPoints());

        wonLabel.bringToFront();
        lostLabel.bringToFront();
        tieLabel.bringToFront();
        cantMoveThatLabel.bringToFront();
        notYourTurnLabel.bringToFront();
        gameOverLabel.bringToFront();
        thatSucksLabel.bringToFront();
        animatedSquare.bringToFront();
        boardMain.invalidate();

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        int width = size.x;
        int height = size.y;

        int iconWidth = (int) (width * 0.40);
        int buttonHeight = (int) (height * .075);
        int buttonGap = (int) (height * .03);

        RelativeLayout.LayoutParams playerPointParams = new RelativeLayout.LayoutParams(2 * iconWidth, (int) (height * .03));
        playerPointParams.addRule(RelativeLayout.BELOW, R.id.toolbar);
        playerPointParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        playerPointParams.setMargins(buttonGap, buttonGap, 0, 0);
        playerPointLabel.setLayoutParams(playerPointParams);
        playerPointLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (height * .025));
        playerPointLabel.setGravity(Gravity.LEFT);

        RelativeLayout.LayoutParams computerPointParams = new RelativeLayout.LayoutParams(2 * iconWidth, (int) (height * .03));
        computerPointParams.addRule(RelativeLayout.BELOW, R.id.toolbar);
        computerPointParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        computerPointParams.setMargins(0, buttonGap, buttonGap, 0);
        computerPointLabel.setLayoutParams(computerPointParams);
        computerPointLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (height * .025));
        computerPointLabel.setGravity(Gravity.RIGHT);

        RelativeLayout.LayoutParams wonParams = new RelativeLayout.LayoutParams(3 * iconWidth, (int) (height * .03));
        wonParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        wonParams.addRule(RelativeLayout.CENTER_VERTICAL);
        wonParams.setMargins(0, 0, 0, 0);
        wonLabel.setLayoutParams(wonParams);
        wonLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (height * .025));
        wonLabel.setGravity(Gravity.CENTER);

        RelativeLayout.LayoutParams lostParams = new RelativeLayout.LayoutParams(3 * iconWidth, (int) (height * .03));
        lostParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lostParams.addRule(RelativeLayout.CENTER_VERTICAL);
        lostParams.setMargins(0, 0, 0, 0);
        lostLabel.setLayoutParams(lostParams);
        lostLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (height * .025));
        lostLabel.setGravity(Gravity.CENTER);

        RelativeLayout.LayoutParams tieParams = new RelativeLayout.LayoutParams(3 * iconWidth, (int) (height * .03));
        tieParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        tieParams.addRule(RelativeLayout.CENTER_VERTICAL);
        tieParams.setMargins(0, 0, 0, 0);
        tieLabel.setLayoutParams(tieParams);
        tieLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (height * .025));
        tieLabel.setGravity(Gravity.CENTER);

        RelativeLayout.LayoutParams cantMoveParams = new RelativeLayout.LayoutParams(3 * iconWidth, (int) (height * .03));
        cantMoveParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        cantMoveParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        cantMoveParams.setMargins(0, 0, 0, buttonGap);
        cantMoveThatLabel.setLayoutParams(cantMoveParams);
        cantMoveThatLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (height * .025));
        cantMoveThatLabel.setGravity(Gravity.CENTER);

        RelativeLayout.LayoutParams noiceParams = new RelativeLayout.LayoutParams(iconWidth, (int) (height * .03));
        noiceParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        noiceParams.addRule(RelativeLayout.BELOW, R.id.toolbar);
        noiceParams.setMargins(0, 2 * buttonGap, 0, 0);
        noiceLabel.setLayoutParams(noiceParams);
        noiceLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (height * .025));
        noiceLabel.setGravity(Gravity.CENTER);

        RelativeLayout.LayoutParams notYourTurnParams = new RelativeLayout.LayoutParams(3 * iconWidth, (int) (height * .03));
        notYourTurnParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        notYourTurnParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        notYourTurnParams.setMargins(0, 0, 0, buttonGap);
        notYourTurnLabel.setLayoutParams(notYourTurnParams);
        notYourTurnLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (height * .025));
        notYourTurnLabel.setGravity(Gravity.CENTER);

        RelativeLayout.LayoutParams gameOverParams = new RelativeLayout.LayoutParams(2 * iconWidth, (int) (height * .03));
        gameOverParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        gameOverParams.addRule(RelativeLayout.ABOVE, R.id.lost_label);
        gameOverParams.setMargins(0, 0, 0, buttonGap);
        gameOverLabel.setLayoutParams(gameOverParams);
        gameOverLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (height * .025));
        gameOverLabel.setGravity(Gravity.CENTER);

        RelativeLayout.LayoutParams thatSucksParams = new RelativeLayout.LayoutParams(2 * iconWidth, (int) (height * .03));
        thatSucksParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        thatSucksParams.addRule(RelativeLayout.BELOW, R.id.toolbar);
        thatSucksParams.setMargins(0, 2 * buttonGap, 0, 0);
        thatSucksLabel.setLayoutParams(thatSucksParams);
        thatSucksLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (height * .02));
        thatSucksLabel.setGravity(Gravity.CENTER);

        boardLayout.setBackgroundColor(colorManager.getColorFromFile(ColorManager.BACKGROUND_COLOR));
        toolbar.setTitle(R.string.solo);
        toolbar.setBackgroundColor(colorManager.getColorFromFile(ColorManager.SECONDARY_COLOR));
        toolbar.setTitleTextColor(colorManager.getColorFromFile(ColorManager.TEXT_COLOR));
        wonLabel.setTextColor(colorManager.getColorFromFile(ColorManager.TEXT_COLOR));
        lostLabel.setTextColor(colorManager.getColorFromFile(ColorManager.TEXT_COLOR));
        tieLabel.setTextColor(colorManager.getColorFromFile(ColorManager.TEXT_COLOR));
        playerPointLabel.setTextColor(colorManager.getColorFromFile(ColorManager.TEXT_COLOR));
        computerPointLabel.setTextColor(colorManager.getColorFromFile(ColorManager.TEXT_COLOR));
        cantMoveThatLabel.setTextColor(colorManager.getColorFromFile(ColorManager.TEXT_COLOR));
        noiceLabel.setTextColor(colorManager.getColorFromFile(ColorManager.TEXT_COLOR));
        notYourTurnLabel.setTextColor(colorManager.getColorFromFile(ColorManager.TEXT_COLOR));
        gameOverLabel.setTextColor(colorManager.getColorFromFile(ColorManager.TEXT_COLOR));
        thatSucksLabel.setTextColor(colorManager.getColorFromFile(ColorManager.TEXT_COLOR));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(colorManager.getColorFromFile(ColorManager.BAR_COLOR));
        }

        bloodThirsty = GameplaySettingsManager.getInstance(this).getBloodThirstByDefault();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            SpannableString spannableString = new SpannableString(item.getTitle().toString());
            spannableString.setSpan(new ForegroundColorSpan(colorManager.getColorFromFile(ColorManager.TEXT_COLOR)), 0, spannableString.length(), 0);
            item.setTitle(spannableString);
        }

        int[][] states = {{android.R.attr.state_checked}, {}};
        int[] colors = {colorManager.getColorFromFile(ColorManager.BOARD_COLOR_1), colorManager.getColorFromFile(ColorManager.TEXT_COLOR)};
        final MenuItem bloodthirstToggle = menu.findItem(R.id.bloodthirst_toggle);

        AppCompatCheckBox bloodthirstToggleCheck = (AppCompatCheckBox) bloodthirstToggle.getActionView();
        bloodthirstToggleCheck.setChecked(bloodThirsty);

        bloodthirstToggleCheck.setText(R.string.bloodthirst);
        bloodthirstToggleCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bloodthirstToggle.setChecked(!bloodthirstToggle.isChecked());
                bloodThirstQueued = !bloodThirstQueued;
                if (bloodThirstQueued)
                    Toast.makeText(view.getContext(), R.string.bloodthirst_notification, Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(view.getContext(), R.string.bloodthirst_toggle_cancelled, Toast.LENGTH_SHORT).show();
            }
        });

        CompoundButtonCompat.setButtonTintList(bloodthirstToggleCheck, new ColorStateList(states, colors));

        return true;
    }

    @Override
    public void onBackPressed() {
        if (moveThread == null || !moveThread.isAlive()) {
            singleGame.saveBoard(board);
            for (int i = 0; i < boardSize; i++)
                for (int j = 0; j < boardSize; j++)
                    boardMain.removeView(board[i][j]);
            super.onBackPressed();
        }
        else {
            Toast.makeText(this, R.string.wait_for_move, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (moveThread == null || !moveThread.isAlive()) {
            switch (item.getItemId()) {
                case R.id.new_game:
                    newGameButton_Click();
                    return true;
                case R.id.bloodthirst_toggle:
                    item.setChecked(!item.isChecked());
                    bloodThirstQueued = !bloodThirstQueued;
                    if (bloodThirstQueued)
                        Toast.makeText(this, R.string.bloodthirst_notification, Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(this, R.string.bloodthirst_toggle_cancelled, Toast.LENGTH_SHORT).show();
                    return true;
                case android.R.id.home:
                    singleGame.saveBoard(board);
                    for (int i = 0; i < boardSize; i++)
                        for (int j = 0; j < boardSize; j++)
                            boardMain.removeView(board[i][j]);
                    this.finish();
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        } else {
            Toast.makeText(this, R.string.wait_for_move, Toast.LENGTH_SHORT).show();
            return super.onOptionsItemSelected(item);
        }
    }


    private int convertDpToPx(int dp) {
        return Math.round(dp * (getResources().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }


    private void newGameButton_Click() {
        tieLabel.setVisibility(View.INVISIBLE);
        wonLabel.setVisibility(View.INVISIBLE);
        lostLabel.setVisibility(View.INVISIBLE);
        cantMoveThatLabel.setVisibility(View.INVISIBLE);
        notYourTurnLabel.setVisibility(View.INVISIBLE);
        gameOverLabel.setVisibility(View.INVISIBLE);
        thatSucksLabel.setVisibility(View.INVISIBLE);
        noiceLabel.setVisibility(View.INVISIBLE);

        if (bloodThirstQueued) {
            bloodThirsty = !bloodThirsty;
            bloodThirstQueued = false;
        }

        selected = defaultSquare;
        while(moveThread != null && moveThread.isAlive())
        {
            moveThread.interrupt();
        }
        clearPieces();
        singleGame.newGame();
        startNewGame(getIntent().getBooleanExtra("knightsOnly", false));
        playerPointLabel.setText(getResources().getText(R.string.player_points).toString() + " " + singleGame.getPlayerPoints());
        computerPointLabel.setText(getResources().getText(R.string.computer_points).toString() + " " + singleGame.getComputerPoints());
        return;
    }

    void clearPieces() {
        for (int i = 0; i < boardSize; i++)
            for (int j = 0; j < boardSize; j++) {
                board[i][j].setTeam(NONE);
                board[i][j].setPiece(Piece.NONE);
                board[i][j].setPieceCount(0);
            }

        return;
    }

    void startNewGame(boolean knightsOnly) {
        drawBoard(knightsOnly, boardSize);
        return;
    }

    public boolean bishopTie() {
        boolean bishopTie;
        List<Square> players = singleGame.movablePlayers(mover, board);
        List<Square> computers = singleGame.movableComputers(mover, board);

        boolean allPlayersAreBishops = true;
        boolean allComputersAreBishops = true;

        for (int i = 0; i < players.size(); i++)
            if (players.get(i).getPiece() != Piece.BISHOP) {
                allPlayersAreBishops = false;
                break;
            }
        for (int i = 0; i < computers.size(); i++)
            if (computers.get(i).getPiece() != Piece.BISHOP) {
                allComputersAreBishops = false;
                break;
            }

        if (allComputersAreBishops && allPlayersAreBishops && (players.size() == 1) && (computers.size() == 1) && (players.get(0).getColor() != computers.get(0).getColor()))
            bishopTie = true;
        else
            bishopTie = false;

        return bishopTie;
    }

    public synchronized void moveSelectedButton_Click(final View view) {
        achievementHandler.incrementInMemory(AchievementHandler.STARTED_GAME);
        achievementHandler.saveValues();
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
        if (singleGame.getTurn() == YOU) {
            if (!singleGame.getCanComputerMove(mover, board) && !singleGame.getCanPlayerMove(mover, board)) {
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
                singleGame.setTurn(NONE);
                if (singleGame.getComputerPoints() == singleGame.getPlayerPoints())
                    tieLabel.post(new Runnable() {
                        @Override
                        public void run() {
                            tieLabel.setVisibility(View.VISIBLE);
                            achievementHandler.incrementInMemory(AchievementHandler.TIED_A_GAME);
                            achievementHandler.incrementInMemory(AchievementHandler.TIED_10_GAMES);
                            achievementHandler.incrementInMemory(AchievementHandler.TIED_50_GAMES);
                            achievementHandler.incrementInMemory(AchievementHandler.PLAYED_10_GAMES);
                            achievementHandler.incrementInMemory(AchievementHandler.PLAYED_50_GAMES);
                            achievementHandler.incrementInMemory(AchievementHandler.PLAYED_100_GAMES);
                            achievementHandler.saveValues();
                        }
                    });
                else if (singleGame.getComputerPoints() < singleGame.getPlayerPoints()) {
                    wonLabel.post(new Runnable() {
                        @Override
                        public void run() {
                            wonLabel.setVisibility(View.VISIBLE);

                            achievementHandler.incrementInMemory(AchievementHandler.WON_A_GAME);
                            achievementHandler.incrementInMemory(AchievementHandler.WON_10_GAMES);
                            achievementHandler.incrementInMemory(AchievementHandler.WON_50_GAMES);
                            achievementHandler.incrementInMemory(AchievementHandler.PLAYED_10_GAMES);
                            achievementHandler.incrementInMemory(AchievementHandler.PLAYED_50_GAMES);
                            achievementHandler.incrementInMemory(AchievementHandler.PLAYED_100_GAMES);

                            if (singleGame.getComputerPoints() == 0) {
                                achievementHandler.incrementInMemory(AchievementHandler.UNTOUCHABLE);
                            }
                            achievementHandler.saveValues();
                        }
                    });

                } else
                    lostLabel.post(new Runnable() {
                        @Override
                        public void run() {
                            lostLabel.setVisibility(View.VISIBLE);

                            achievementHandler.incrementInMemory(AchievementHandler.LOST_A_GAME);
                            achievementHandler.incrementInMemory(AchievementHandler.LOST_10_GAMES);
                            achievementHandler.incrementInMemory(AchievementHandler.LOST_50_GAMES);
                            achievementHandler.incrementInMemory(AchievementHandler.PLAYED_10_GAMES);
                            achievementHandler.incrementInMemory(AchievementHandler.PLAYED_50_GAMES);
                            achievementHandler.incrementInMemory(AchievementHandler.PLAYED_100_GAMES);


                            if (singleGame.getPlayerPoints() == 0) {
                                achievementHandler.incrementInMemory(AchievementHandler.SLAUGHTERED);
                            }

                            achievementHandler.saveValues();
                        }
                    });
            } else if (bishopTie()) {
                gameOverLabel.post(new Runnable() {
                    @Override
                    public void run() {
                        gameOverLabel.setVisibility(View.VISIBLE);
                    }
                });
                singleGame.setTurn(NONE);
                if (singleGame.getComputerPoints() == singleGame.getPlayerPoints())
                    tieLabel.post(new Runnable() {
                        @Override
                        public void run() {
                            tieLabel.setVisibility(View.VISIBLE);
                            achievementHandler.incrementInMemory(AchievementHandler.TIED_A_GAME);
                            achievementHandler.incrementInMemory(AchievementHandler.TIED_10_GAMES);
                            achievementHandler.incrementInMemory(AchievementHandler.TIED_50_GAMES);
                            achievementHandler.incrementInMemory(AchievementHandler.PLAYED_10_GAMES);
                            achievementHandler.incrementInMemory(AchievementHandler.PLAYED_50_GAMES);
                            achievementHandler.incrementInMemory(AchievementHandler.PLAYED_100_GAMES);
                            achievementHandler.saveValues();
                        }
                    });
                else if (singleGame.getComputerPoints() < singleGame.getPlayerPoints()) {
                    wonLabel.post(new Runnable() {
                        @Override
                        public void run() {
                            wonLabel.setVisibility(View.VISIBLE);

                            achievementHandler.incrementInMemory(AchievementHandler.WON_A_GAME);
                            achievementHandler.incrementInMemory(AchievementHandler.WON_10_GAMES);
                            achievementHandler.incrementInMemory(AchievementHandler.WON_50_GAMES);
                            achievementHandler.incrementInMemory(AchievementHandler.PLAYED_10_GAMES);
                            achievementHandler.incrementInMemory(AchievementHandler.PLAYED_50_GAMES);
                            achievementHandler.incrementInMemory(AchievementHandler.PLAYED_100_GAMES);

                            if (singleGame.getComputerPoints() == 0) {
                                achievementHandler.incrementInMemory(AchievementHandler.UNTOUCHABLE);
                            }
                            achievementHandler.saveValues();
                        }
                    });

                } else
                    lostLabel.post(new Runnable() {
                        @Override
                        public void run() {
                            lostLabel.setVisibility(View.VISIBLE);
                            achievementHandler.incrementInMemory(AchievementHandler.LOST_A_GAME);
                            achievementHandler.incrementInMemory(AchievementHandler.LOST_10_GAMES);
                            achievementHandler.incrementInMemory(AchievementHandler.LOST_50_GAMES);
                            achievementHandler.incrementInMemory(AchievementHandler.PLAYED_10_GAMES);
                            achievementHandler.incrementInMemory(AchievementHandler.PLAYED_50_GAMES);
                            achievementHandler.incrementInMemory(AchievementHandler.PLAYED_100_GAMES);


                            if (singleGame.getPlayerPoints() == 0) {
                                achievementHandler.incrementInMemory(AchievementHandler.SLAUGHTERED);
                            }

                            achievementHandler.saveValues();
                        }
                    });
            } else if (selected.getI() == -1) {
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


            } else if (mover.movePiece(board, board[selected.getI()][selected.getJ()], singleGame, bloodThirsty)) {
                selected.post(new Runnable() {
                    @Override
                    public void run() {
                        int color;

                        if (selected.getColor())
                            color = boardColor1;
                        else
                            color = boardColor2;

                        selected.setBackgroundColor(color);
                        selected = defaultSquare;
                    }
                });

                if (singleGame.getPlayerPoints() > playerScore) {
                    noiceLabel.post(new Runnable() {
                        @Override
                        public void run() {
                            noiceLabel.setVisibility(View.VISIBLE);
                            thatSucksLabel.setVisibility(View.INVISIBLE);
                        }
                    });
                } else {
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
                singleGame.setTurn(OPPONENT);


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
                } while (!singleGame.getCanPlayerMove(mover, board) && singleGame.getCanComputerMove(mover, board) && singleGame.getPlayerCount() > 0);

                if (singleGame.getPlayerCount() == 0) {
                    lostLabel.post(new Runnable() {
                        @Override
                        public void run() {
                            lostLabel.setVisibility(View.VISIBLE);
                            achievementHandler.incrementInMemory(AchievementHandler.LOST_A_GAME);
                            achievementHandler.incrementInMemory(AchievementHandler.LOST_10_GAMES);
                            achievementHandler.incrementInMemory(AchievementHandler.LOST_50_GAMES);
                            achievementHandler.incrementInMemory(AchievementHandler.PLAYED_10_GAMES);
                            achievementHandler.incrementInMemory(AchievementHandler.PLAYED_50_GAMES);
                            achievementHandler.incrementInMemory(AchievementHandler.PLAYED_100_GAMES);


                            if (singleGame.getPlayerPoints() == 0) {
                                achievementHandler.incrementInMemory(AchievementHandler.SLAUGHTERED);
                            }

                            achievementHandler.saveValues();
                        }
                    });

                    singleGame.setTurn(NONE);
                } else if (singleGame.getComputerCount() == 0) {
                    wonLabel.post(new Runnable() {
                        @Override
                        public void run() {
                            wonLabel.setVisibility(View.VISIBLE);
                            achievementHandler.incrementInMemory(AchievementHandler.WON_A_GAME);
                            achievementHandler.incrementInMemory(AchievementHandler.WON_10_GAMES);
                            achievementHandler.incrementInMemory(AchievementHandler.WON_50_GAMES);
                            achievementHandler.incrementInMemory(AchievementHandler.PLAYED_10_GAMES);
                            achievementHandler.incrementInMemory(AchievementHandler.PLAYED_50_GAMES);
                            achievementHandler.incrementInMemory(AchievementHandler.PLAYED_100_GAMES);

                            if (singleGame.getComputerPoints() == 0) {
                                achievementHandler.incrementInMemory(AchievementHandler.UNTOUCHABLE);
                            }
                            achievementHandler.saveValues();
                        }
                    });

                    singleGame.setTurn(NONE);

                }
            } else {
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


        } else if (singleGame.getTurn() == OPPONENT) {
            if (singleGame.getCanComputerMove(mover, board)) {
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
            } else {
                singleGame.setTurn(YOU);
                moveSelectedButton_Click(view);
            }
        } else {
            gameOverLabel.post(new Runnable() {
                @Override
                public void run() {
                    gameOverLabel.setVisibility(View.VISIBLE);
                }
            });

        }


    }

    synchronized void moveComputer() {
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
                if (board[i][j].getTeam() == OPPONENT)
                    computerPieces.add(board[i][j]);
        if (computerPieces.size() > 0) {
            int computerScore = singleGame.getComputerPoints();
            picked = computerPieces.get(rand.nextInt(computerPieces.size()));
            while (!mover.movePiece(board, board[picked.getI()][picked.getJ()], singleGame, bloodThirsty)) {
                computerPieces.remove(picked);
                if (computerPieces.size() == 0)
                    break;
                picked = computerPieces.get(rand.nextInt(computerPieces.size()));
            }

            if (selected.getTeam() == OPPONENT) {
                selected.post(new Runnable() {
                    @Override
                    public void run() {
                        int color;

                        if (selected.getColor())
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


            singleGame.setTurn(YOU);

            notYourTurnLabel.post(new Runnable() {
                @Override
                public void run() {
                    notYourTurnLabel.setVisibility(View.INVISIBLE);
                }
            });
        }

    }

    void drawBoard(boolean knightsOnly, int size) {
        createSquares(size);

        startPieces(knightsOnly, size);

        return;
    }

    void createSquares(int size) {
        boolean colorPicker = false;
        int color;

        for (int i = 0; i < size; i++) {

            colorPicker = !colorPicker;
            for (int j = 0; j < size; j++) {

                if (colorPicker)
                    color = boardColor1;
                else
                    color = boardColor2;

                board[i][j].setI(i);
                board[i][j].setJ(j);
                board[i][j].setColor(colorPicker);
                board[i][j].setBackgroundColor(color);


                colorPicker = !colorPicker;
                board[i][j].setX(xPosition + i * squareSize);
                board[i][j].setY(yPosition + j * squareSize);
                board[i][j].setLayoutParams(new RelativeLayout.LayoutParams(squareSize, squareSize));
                if (singleGame.getGameCount() == 0) {
                    board[i][j].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (moveThread == null || !moveThread.isAlive()) {
                                moveThread = new MoveThread(view, context, boardColor1, boardColor2, selectColor);
                                moveThread.start();
                            }
                        }
                    });
                    boardMain.addView(board[i][j]);
                }


            }
        }

        return;
    }

    void startPieces(boolean knightsOnly, int size) {
        if (knightsOnly) {
            // Set Teams and pieces
            for (int i = 0; i < size; i++) {
                board[i][0].setTeam(OPPONENT);
                board[i][0].setPiece(Piece.KNIGHT);
                board[i][1].setTeam(OPPONENT);
                board[i][1].setPiece(Piece.KNIGHT);
                board[i][6].setTeam(YOU);
                board[i][6].setPiece(Piece.KNIGHT);
                board[i][7].setTeam(YOU);
                board[i][7].setPiece(Piece.KNIGHT);
            }

        } else {
            // Set Teams
            for (int i = 0; i < size; i++) {
                board[i][0].setTeam(OPPONENT);
                board[i][1].setTeam(OPPONENT);
                board[i][6].setTeam(YOU);
                board[i][7].setTeam(YOU);
            }

            // Set Pawns
            for (int i = 0; i < size; i++)
                board[i][6].setPiece(Piece.PAWN);

            for (int i = 0; i < size; i++)
                board[i][1].setPiece(Piece.PAWN);

            // Set Rooks
            board[0][0].setPiece(Piece.ROOK);
            board[7][0].setPiece(Piece.ROOK);
            board[0][7].setPiece(Piece.ROOK);
            board[7][7].setPiece(Piece.ROOK);

            // Set Knights
            board[1][0].setPiece(Piece.KNIGHT);
            board[6][0].setPiece(Piece.KNIGHT);
            board[1][7].setPiece(Piece.KNIGHT);
            board[6][7].setPiece(Piece.KNIGHT);

            // Set Bishops
            board[2][0].setPiece(Piece.BISHOP);
            board[5][0].setPiece(Piece.BISHOP);
            board[2][7].setPiece(Piece.BISHOP);
            board[5][7].setPiece(Piece.BISHOP);

            // Set Kings
            board[3][0].setPiece(Piece.KING);
            board[3][7].setPiece(Piece.KING);

            // Set Queens
            board[4][0].setPiece(Piece.QUEEN);
            board[4][7].setPiece(Piece.QUEEN);
        }

        return;
    }


}
