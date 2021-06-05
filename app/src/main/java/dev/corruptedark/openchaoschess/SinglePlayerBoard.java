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
import android.content.res.Resources;
import android.graphics.Color;


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

import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.CompoundButtonCompat;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

public class SinglePlayerBoard extends AppCompatActivity {

    public final int YOU = -1;
    public final int OPPONENT = 1;
    public final int NONE = 0;

    private final double RATIO_THRESHOLD = 0.2;

    public final int SLEEP_DURATION = 1000;

    int boardSize, squareSize, xPosition, yPosition;
    Square[][] board;
    Square defaultSquare;
    Square selected;
    Mover mover;
    SingleGame singleGame;
    ViewGroup boardMain;
    SinglePlayerBoard context;
    MoveThread moveThread;
    Thread computerStartThread;
    RelativeLayout boardLayout;
    private Square animatedSquare;

    private boolean squaresAdded;

    Toolbar toolbar;

    AchievementHandler achievementHandler;

    ColorManager colorManager;

    int boardColor1;
    int boardColor2;
    int selectColor;
    int pieceColor;

    private boolean bloodThirsty;
    private boolean bloodThirstQueued = false;

    private static volatile boolean aggressiveComputer;
    private static volatile boolean smartComputer;

    TextView wonLabel, lostLabel, tieLabel, cantMoveThatLabel, notYourTurnLabel, gameOverLabel, thatSucksLabel, noiceLabel, playerPointLabel, computerPointLabel, plusOneLabel;

    Random rand = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_player_board);

        achievementHandler = AchievementHandler.getInstance(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mover = new Mover(this);
        singleGame = SingleGame.getInstance();

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
        plusOneLabel = (TextView) findViewById(R.id.plus_one_label);

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

        Resources resources = context.getResources();
        int navBarHeight = 0;
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            navBarHeight = resources.getDimensionPixelSize(resourceId);
        }

        Display display;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            display = getDisplay();
        } else {
            display = getWindowManager().getDefaultDisplay();
        }
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = (int)(0.70 * size.y) - navBarHeight;

        if (Math.abs(((double) width)/size.y - 1.0) <= RATIO_THRESHOLD || width > size.y) { // ratio not long
            squareSize = height / 8;
            xPosition = (width - height) / 2;
        }
        else {
            squareSize = (width - convertDpToPx(30)) / 8;
            xPosition = convertDpToPx(15);
        }
        yPosition = size.y / 2 - 4 * squareSize;

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

        squaresAdded = false;
        if (singleGame.hasBoard()) {
            board = singleGame.restoreBoard();
            createSquares(boardSize);
        } else {
            aggressiveComputer = GameplaySettingsManager.getInstance(this).getAggressiveComputers();
            smartComputer = GameplaySettingsManager.getInstance(this).getSmartComputer();
            startNewGame(singleGame.isKnightsOnly());
        }

        wonLabel.bringToFront();
        lostLabel.bringToFront();
        tieLabel.bringToFront();
        cantMoveThatLabel.bringToFront();
        notYourTurnLabel.bringToFront();
        gameOverLabel.bringToFront();
        thatSucksLabel.bringToFront();
        animatedSquare.bringToFront();
        plusOneLabel.bringToFront();
        boardMain.invalidate();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        Display display;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            display = getDisplay();
        } else {
            display = getWindowManager().getDefaultDisplay();
        }
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

        RelativeLayout.LayoutParams plusOneParams = new RelativeLayout.LayoutParams(2 * iconWidth, (int) (height * .03));
        plusOneParams.addRule(RelativeLayout.ALIGN_BASELINE, R.id.player_points);
        plusOneLabel.setLayoutParams(plusOneParams);
        plusOneLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (height * .025));
        plusOneLabel.setGravity(Gravity.CENTER);

        boardLayout.setBackgroundColor(colorManager.getColorFromFile(ColorManager.BACKGROUND_COLOR));
        toolbar.setTitle(R.string.solo);
        toolbar.setBackgroundColor(colorManager.getColorFromFile(ColorManager.SECONDARY_COLOR));
        toolbar.setTitleTextColor(colorManager.getColorFromFile(ColorManager.TEXT_COLOR));
        toolbar.getNavigationIcon().setColorFilter(colorManager.getColorFromFile(ColorManager.TEXT_COLOR), PorterDuff.Mode.MULTIPLY);
        toolbar.getOverflowIcon().setColorFilter(colorManager.getColorFromFile(ColorManager.TEXT_COLOR), PorterDuff.Mode.MULTIPLY);
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
        plusOneLabel.setTextColor(colorManager.getColorFromFile(ColorManager.TEXT_COLOR));
        plusOneLabel.setVisibility(View.GONE);

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
        } else {
            Toast.makeText(this, R.string.wait_for_move, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if ((moveThread == null || !moveThread.isAlive()) && (computerStartThread == null || !computerStartThread.isAlive())) {
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
        if (computerStartThread != null && computerStartThread.isAlive()) {
            Toast.makeText(this, R.string.wait_for_move, Toast.LENGTH_SHORT).show();
        }
        else {
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

            aggressiveComputer = GameplaySettingsManager.getInstance(this).getAggressiveComputers();
            smartComputer = GameplaySettingsManager.getInstance(this).getSmartComputer();

            selected = defaultSquare;
            while (moveThread != null && moveThread.isAlive()) {
                moveThread.interrupt();
            }
            clearPieces();
            singleGame.newGame();
            startNewGame(singleGame.isKnightsOnly());
            playerPointLabel.setText(getResources().getText(R.string.player_points).toString() + " " + singleGame.getPlayerPoints());
            computerPointLabel.setText(getResources().getText(R.string.computer_points).toString() + " " + singleGame.getComputerPoints());
        }
    }

    void clearPieces() {
        for (int i = 0; i < boardSize; i++)
            for (int j = 0; j < boardSize; j++) {
                board[i][j].setTeam(NONE);
                board[i][j].setPiece(Piece.NONE);
                board[i][j].setPieceCount(0);
            }
    }

    void startNewGame(boolean knightsOnly) {
        drawBoard(knightsOnly, boardSize);
        if (GameplaySettingsManager.getInstance(this).getMoveSecond()) {
            singleGame.setTurn(OPPONENT);
            computerStartThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    moveComputer();
                }
            });
            computerStartThread.start();
        }
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
        boardColor1 = colorManager.getColorFromFile(ColorManager.BOARD_COLOR_1);
        boardColor2 = colorManager.getColorFromFile(ColorManager.BOARD_COLOR_2);
        selectColor = colorManager.getColorFromFile(ColorManager.SELECTION_COLOR);
        pieceColor = colorManager.getColorFromFile(ColorManager.PIECE_COLOR);
        achievementHandler.incrementInMemory(AchievementHandler.STARTED_GAME);
        achievementHandler.saveValues();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                thatSucksLabel.setVisibility(View.INVISIBLE);
            }
        });
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                noiceLabel.setVisibility(View.INVISIBLE);
            }
        });
        final int playerScore = singleGame.getPlayerPoints();
        if (singleGame.getTurn() == YOU) {
            if (!singleGame.getCanComputerMove(mover, board) && !singleGame.getCanPlayerMove(mover, board)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cantMoveThatLabel.setVisibility(View.VISIBLE);
                    }
                });
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        gameOverLabel.setVisibility(View.VISIBLE);
                    }
                });
                singleGame.setTurn(NONE);
                if (singleGame.getComputerPoints() == singleGame.getPlayerPoints())
                    runOnUiThread(new Runnable() {
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
                    runOnUiThread(new Runnable() {
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
                    runOnUiThread(new Runnable() {
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        gameOverLabel.setVisibility(View.VISIBLE);
                    }
                });
                singleGame.setTurn(NONE);
                if (singleGame.getComputerPoints() == singleGame.getPlayerPoints())
                    runOnUiThread(new Runnable() {
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
                    runOnUiThread(new Runnable() {
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
                    runOnUiThread(new Runnable() {
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cantMoveThatLabel.setVisibility(View.INVISIBLE);
                    }
                });
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notYourTurnLabel.setVisibility(View.INVISIBLE);
                    }
                });


            } else if (mover.movePiece(board, board[selected.getI()][selected.getJ()], singleGame, bloodThirsty)) {
                runOnUiThread(new Runnable() {
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

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cantMoveThatLabel.setVisibility(View.INVISIBLE);
                    }
                });
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notYourTurnLabel.setVisibility(View.INVISIBLE);
                    }
                });
                singleGame.incrementMoveCount();
                singleGame.setTurn(OPPONENT);
                Animation.AnimationListener translateListener = new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        plusOneLabel.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        plusOneLabel.setVisibility(View.INVISIBLE);
                        playerPointLabel.setText(getResources().getText(R.string.player_points).toString() + " " + singleGame.getPlayerPoints());
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                        //plusOneLabel.setVisibility(View.INVISIBLE);
                    }
                };
                final TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, convertDpToPx(10), convertDpToPx(-10));
                translateAnimation.setAnimationListener(translateListener);
                translateAnimation.setDuration(300);

                //plusOneLabel.setAnimation(translateAnimation);
                Runnable uiRunnable1 = new Runnable() {

                    @Override
                    public void run() {
                        if (singleGame.getPlayerPoints() > playerScore) {

                            plusOneLabel.startAnimation(translateAnimation);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    noiceLabel.setVisibility(View.VISIBLE);
                                    thatSucksLabel.setVisibility(View.INVISIBLE);
                                }
                            });

                        } else {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    noiceLabel.setVisibility(View.INVISIBLE);
                                }
                            });

                        }
                        //boardMain.invalidate();
                    }
                };

                RunnableFuture<Void> uiTask1 = new FutureTask<>(uiRunnable1, null);

                //uiTask1.run();
                runOnUiThread(uiTask1);
                //new Handler(Looper.getMainLooper()).post(uiTask1);

                try {
                    uiTask1.get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Runnable uiRunnable2 = new Runnable() {
                    @Override
                    public void run() {
                        computerPointLabel.setText(getResources().getText(R.string.computer_points).toString() + " " + singleGame.getComputerPoints());
                    }
                };

                RunnableFuture<Void> uiTask2 = null;
                do {
                    try {
                        if(uiTask1 != null) {
                            uiTask1.get();
                        }
                        if(uiTask2 != null) {
                            uiTask2.get();

                        }

                        if (singleGame.getCanComputerMove(mover, board)) {
                            Thread.sleep(SLEEP_DURATION);
                            moveComputer();
                        }


                        uiTask2 = new FutureTask<>(uiRunnable2, null);

                        runOnUiThread(uiTask2);
                        //uiTask2.run();
                    } catch (InterruptedException | ExecutionException | NullPointerException e) {
                        e.printStackTrace();
                    }

                } while (!singleGame.getCanPlayerMove(mover, board) && singleGame.getCanComputerMove(mover, board) && singleGame.getPlayerCount() > 0);

                if (singleGame.getPlayerCount() == 0) {
                    runOnUiThread(new Runnable() {
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
                    runOnUiThread(new Runnable() {
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cantMoveThatLabel.setVisibility(View.VISIBLE);
                    }
                });
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notYourTurnLabel.setVisibility(View.INVISIBLE);
                    }
                });

            }


        } else if (singleGame.getTurn() == OPPONENT) {
            if (singleGame.getCanComputerMove(mover, board)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cantMoveThatLabel.setVisibility(View.VISIBLE);
                    }
                });
                runOnUiThread(new Runnable() {
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
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    gameOverLabel.setVisibility(View.VISIBLE);
                }
            });

        }


    }

    void moveComputer() {
        //Looper.prepare();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                noiceLabel.setVisibility(View.INVISIBLE);
            }
        });
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                thatSucksLabel.setVisibility(View.INVISIBLE);
            }
        });

        // Generate pieces list
        List<Square> computerPieces = new ArrayList<>();
        List<Square> aggressivePieces = new ArrayList<>();
        for (int i = 0; i < boardSize; i++)
            for (int j = 0; j < boardSize; j++)
                if (board[i][j].getTeam() == OPPONENT)
                    computerPieces.add(board[i][j]);

        int computerPiecesSize = computerPieces.size();
        for (int i = computerPiecesSize - 1; i >= 0; i--) {
            if (!mover.canPieceMove(board, computerPieces.get(i), singleGame)) {
                computerPieces.remove(i);
            }
        }

        // Generate aggressive pieces list
        if (aggressiveComputer || smartComputer) {
            for (Square piece : computerPieces)
                //if (PieceCost.getAttackCost(mover, board, piece) != 0)
                if(mover.pieceHasEnemies(board, piece))
                    aggressivePieces.add(piece);
        }

        if (computerPieces.size() > 0) {

            Square picked;
            Random rand = new Random();

            int computerScore = singleGame.getComputerPoints();

            if(smartComputer) {
                List<Square> improvedAIPieces = new ArrayList<>();
                List<Integer> priority = new ArrayList<>();
                int maxPriority = -30;
                for (Square square : computerPieces) {
                    priority.add(   (mover.pieceInDanger(board, square) ? PieceCost.getPieceCost(square) : 0) +
                                    (PieceCost.getAttackCost(mover, board, square)) -
                                    (PieceCost.getDangerCostOfMove(mover, board, square, bloodThirsty)) +
                                    (aggressivePieces.contains(square) ? 1 : 0));
                }

                for (int pr : priority) {
                    if(pr > maxPriority)
                        maxPriority = pr;
                }

                for (int i = 0; i < computerPieces.size(); i++) {
                    if(priority.get(i) == maxPriority)
                        improvedAIPieces.add(computerPieces.get(i));
                }

                picked = improvedAIPieces.get(rand.nextInt(improvedAIPieces.size()));
            } else if(aggressiveComputer) {
                if (aggressivePieces.isEmpty())
                    picked = computerPieces.get(rand.nextInt(computerPieces.size()));
                else
                    picked = aggressivePieces.get(rand.nextInt(aggressivePieces.size()));
            } else {
                picked = computerPieces.get(rand.nextInt(computerPieces.size()));
            }

            mover.movePiece(board, board[picked.getI()][picked.getJ()], singleGame, bloodThirsty);

            if (selected.getTeam() == OPPONENT) {
                runOnUiThread(new Runnable() {
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        thatSucksLabel.setVisibility(View.VISIBLE);
                        noiceLabel.setVisibility(View.INVISIBLE);
                    }
                });
            else
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        thatSucksLabel.setVisibility(View.INVISIBLE);
                    }
                });


            singleGame.setTurn(YOU);

            runOnUiThread(new Runnable() {
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

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (moveThread == null || !moveThread.isAlive()) {
                    moveThread = new MoveThread(view, context/*, colorManager*/);
                    moveThread.start();
                }
            }
        };

        if (!squaresAdded) {
            if (!singleGame.hasBoard()) {

                board = new Square[boardSize][boardSize];
                for (int i = 0; i < boardSize; i++) {
                    board[i] = new Square[boardSize];
                    for (int j = 0; j < boardSize; j++) {
                        board[i][j] = new Square(context, pieceColor);
                        //board[i][j].setOnClickListener(onClickListener);
                        board[i][j].setPieceColor(pieceColor);
                        boardMain.addView(board[i][j]);
                    }
                }
            } else {
                for (int i = 0; i < boardSize; i++) {
                    for (int j = 0; j < boardSize; j++) {
                        board[i][j].setPieceColor(pieceColor);
                        boardMain.addView(board[i][j]);
                    }
                }
            }

            squaresAdded = true;
        }

        for (int i = 0; i < boardSize; i++)
            for (int j = 0; j < boardSize; j++)
                board[i][j].setOnClickListener(onClickListener);

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

        } else  {
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
                if(GameplaySettingsManager.getInstance(this).getHandicapQueensAttackEnabled())
                    board[i][1].setPiece(Piece.QUEEN);
                else
                    board[i][1].setPiece(Piece.PAWN);

            // Default positions
            int rook1 = 0;
            int rook2 = 7;
            int knight1 = 1;
            int knight2 = 6;
            int bishop1 = 2;
            int bishop2 = 5;
            int queen = 3;
            int king = 4;

            // Generate Chess960
            if(GameplaySettingsManager.getInstance(this).getChess960()) {
                List<Integer> freeSquares = new ArrayList<Integer>();
                for (int i = 0; i < 8; i++)
                    freeSquares.add(i);
                rook1 = rand.nextInt(6);
                rook2 = rand.nextInt(6 - rook1) + rook1 + 2;
                freeSquares.remove((Integer) rook1);
                freeSquares.remove((Integer) rook2);

                king = rook1 + 1 + rand.nextInt(rook2 - rook1 - 1);
                freeSquares.remove((Integer) king);

                bishop1 = (int) freeSquares.get(rand.nextInt(5));
                freeSquares.remove((Integer) bishop1);
                List<Integer> freeSquares4bishop2 = new ArrayList<Integer>();
                for(int i = 0; i < 4; i++)
                    if(freeSquares.get(i) % 2 != bishop1 % 2)
                        freeSquares4bishop2.add(freeSquares.get(i));
                bishop2 = (int) freeSquares4bishop2.get(rand.nextInt(freeSquares4bishop2.size()));
                freeSquares.remove((Integer) bishop2);

                knight1 = (int) freeSquares.get(rand.nextInt(3));
                freeSquares.remove((Integer) knight1);
                knight2 = (int) freeSquares.get(rand.nextInt(2));
                freeSquares.remove((Integer) knight2);

                queen = (int) freeSquares.get(0);
            }

            // Set Rooks
            board[rook1][0].setPiece(Piece.ROOK);
            board[rook2][0].setPiece(Piece.ROOK);
            if (GameplaySettingsManager.getInstance(this).getHandicapOnlyBishopsKnightsEnabled()) {
                board[rook1][7].setPiece(Piece.PAWN);
                board[rook2][7].setPiece(Piece.PAWN);

            } else {
                board[rook1][7].setPiece(Piece.ROOK);
                board[rook2][7].setPiece(Piece.ROOK);
            }

            // Set Kings
            board[king][0].setPiece(Piece.KING);
            board[king][7].setPiece(Piece.KING);


            // Set Bishops
            board[bishop1][0].setPiece(Piece.BISHOP);
            board[bishop2][0].setPiece(Piece.BISHOP);
            board[bishop1][7].setPiece(Piece.BISHOP);
            board[bishop2][7].setPiece(Piece.BISHOP);

            // Set Knights
            board[knight1][0].setPiece(Piece.KNIGHT);
            board[knight2][0].setPiece(Piece.KNIGHT);
            board[knight1][7].setPiece(Piece.KNIGHT);
            board[knight2][7].setPiece(Piece.KNIGHT);

            // Set Queens
            board[queen][0].setPiece(Piece.QUEEN);
            if (GameplaySettingsManager.getInstance(this).getHandicapOnlyBishopsKnightsEnabled()) {
                board[queen][7].setPiece(Piece.PAWN);
            } else {
                board[queen][7].setPiece(Piece.QUEEN);
            }


        }

        return;
    }


}
