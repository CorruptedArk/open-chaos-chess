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

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ViewAnimator;

import java.sql.SQLInvalidAuthorizationSpecException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

public class Mover {
    public final int YOU = -1;
    public final int OPPONENT = 1;
    public final int NONE = 0;

    public final int SLEEP_DURATION = 1000;

    private enum Direction {UP, BACK, LEFT, RIGHT, LEFTUP, RIGHTUP, LEFTBACK, RIGHTBACK, _2R1U, _1R2U, _1L2U, _2L1U, _2L1D, _1L2D, _1R2D, _2R1D}

    private enum KQMODE {ROOK, BISHOP}

    Random rand = new Random();
    Context context;

    private Square destination;

    Mover(Context context) {
        this.context = context;
    }

    private void animateMove(final Square start, final Square end, final SingleGame singleGame) {
        final Square animatedSquare = singleGame.getAnimatedSquare();

        final int team = start.getTeam();
        final String piece = start.getPiece();
        final int pieceCount = start.getPieceCount() + 1;

        double distance = Math.sqrt((end.getX() - start.getX()) * (end.getX() - start.getX()) + (end.getY() - start.getY()) * (end.getY() - start.getY()));
        double speed = 0.20 * convertDpToPx(1);
        long duration = (long) (distance / speed);

        final TranslateAnimation animation = new TranslateAnimation(0, end.getX() - start.getX(), 0, end.getY() - start.getY());
        animatedSquare.setPiece(piece);
        animatedSquare.setTeam(team);
        animatedSquare.setX(start.getX());
        animatedSquare.setY(start.getY());


        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

                animatedSquare.setVisibility(View.VISIBLE);
                start.setPieceCount(0);
                start.setTeam(NONE);
                start.setPiece(Piece.NONE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                end.setTeam(team);
                end.setPiece(piece);
                end.setPieceCount(pieceCount);

                animatedSquare.setVisibility(View.GONE);
                animatedSquare.setPiece(Piece.NONE);

                if (piece == Piece.PAWN && end.getJ() == 7 && end.getTeam() == OPPONENT) {
                    end.setPiece(Piece.QUEEN);
                } else if (piece == Piece.PAWN && end.getJ() == 0 && end.getTeam() == YOU) {
                    end.setPiece(Piece.QUEEN);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        animation.setDuration(duration);

        Runnable animationRunnable = new Runnable() {
            @Override
            public void run() {
                animatedSquare.startAnimation(animation);
            }
        };

        RunnableFuture<Void> animationTask = new FutureTask<>(animationRunnable, null);

        ((Activity)context).runOnUiThread(animationTask);
        //animationTask.run();

       /* try {
            animationTask.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        /*((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                animatedSquare.startAnimation(animation);
            }
        });*/


        while (!animation.hasEnded()) {
            //Log.v("Open Chaos Chess", "Animation still running.");
            try {
                Thread.sleep(SLEEP_DURATION);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public void animateMove(final Square start, final Square end, final MultiGame multiGame) {
        final Square animatedSquare = multiGame.getAnimatedSquare();

        final int team = start.getTeam();
        final String piece = start.getPiece();
        final int pieceCount = start.getPieceCount() + 1;

        double distance = Math.sqrt((end.getX() - start.getX()) * (end.getX() - start.getX()) + (end.getY() - start.getY()) * (end.getY() - start.getY()));
        double speed = 0.20 * convertDpToPx(1);
        long duration = (long) (distance / speed);

        final TranslateAnimation animation = new TranslateAnimation(0, end.getX() - start.getX(), 0, end.getY() - start.getY());

        animatedSquare.setPiece(piece);
        animatedSquare.setTeam(team);
        animatedSquare.setX(start.getX());
        animatedSquare.setY(start.getY());

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

                animatedSquare.setVisibility(View.VISIBLE);
                start.setPieceCount(0);
                start.setTeam(NONE);
                start.setPiece(Piece.NONE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                end.setTeam(team);
                end.setPiece(piece);
                end.setPieceCount(pieceCount);

                animatedSquare.setVisibility(View.GONE);
                animatedSquare.setPiece(Piece.NONE);

                if (piece == Piece.PAWN && end.getJ() == 7 && end.getTeam() == OPPONENT) {
                    end.setPiece(Piece.QUEEN);
                } else if (piece == Piece.PAWN && end.getJ() == 0 && end.getTeam() == YOU) {
                    end.setPiece(Piece.QUEEN);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        animation.setDuration(duration);
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                animatedSquare.startAnimation(animation);
            }
        });

        while (!animation.hasEnded()) {
            //Log.v("Open Chaos Chess", "Animation still running.");
            try {
                Thread.sleep(SLEEP_DURATION);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public synchronized Square getLastDestination() {
        return destination;
    }

    boolean pieceHasEnemies(Square[][] board, Square square) {
        boolean hasEnemies;
        switch (square.getPiece()) {
            case Piece.PAWN:
                hasEnemies = pawnHasEnemies(board, square);
                break;
            case Piece.ROOK:
                hasEnemies = rookHasEnemies(board, square);
                break;
            case Piece.KNIGHT:
                hasEnemies = knightHasEnemies(board, square);
                break;
            case Piece.BISHOP:
                hasEnemies = bishopHasEnemies(board, square);
                break;
            case Piece.KING:
                hasEnemies = kingHasEnemies(board, square);
                break;
            case Piece.QUEEN:
                hasEnemies = queenHasEnemies(board, square);
                break;
            default:
                hasEnemies = false;
                break;
        }

        return hasEnemies;
    }

    //Single game functions start
    public synchronized boolean movePiece(Square[][] board, Square square, SingleGame singleGame, boolean bloodThirsty) {
        boolean moveSuccess;
        switch (square.getPiece()) {
            case Piece.PAWN:
                moveSuccess = movePawn(board, square, singleGame, bloodThirsty);
                break;
            case Piece.ROOK:
                moveSuccess = moveRook(board, square, singleGame, bloodThirsty);
                break;
            case Piece.KNIGHT:
                moveSuccess = moveKnight(board, square, singleGame, bloodThirsty);
                break;
            case Piece.BISHOP:
                moveSuccess = moveBishop(board, square, singleGame, bloodThirsty);
                break;
            case Piece.KING:
                moveSuccess = moveKing(board, square, singleGame, bloodThirsty);
                break;
            case Piece.QUEEN:
                moveSuccess = moveQueen(board, square, singleGame, bloodThirsty);
                break;
            default:
                moveSuccess = false;
                break;
        }

        return moveSuccess;
    }

    boolean pawnHasEnemies(Square[][] board, Square square) {
        return enemyInWayLeftUp(board, square, 1) || enemyInWayRightUp(board, square, 1);
    }

    boolean movePawn(Square[][] board, Square square, SingleGame singleGame, boolean bloodThirsty) {
        List<Direction> options = new ArrayList<>();

        boolean moveSuccess = false;
        boolean forwardImpossible = false;
        boolean leftImpossible = false;
        boolean rightImpossible = false;
        destination = new Square(context, 0xffffffff);

        rand.nextInt();
        int count;
        int enemy = -square.getTeam();

        if (bloodThirsty) {
            List<Square> enemySquares = new ArrayList<>();

            try {
                Square left = board[square.getI() + square.getTeam()][square.getJ() + square.getTeam()];
                if (left.getTeam() == enemy) // left capture
                {
                    enemySquares.add(left);
                }
            } catch (Exception e) {
            }

            try {
                Square right = board[square.getI() - square.getTeam()][square.getJ() + square.getTeam()];
                if (right.getTeam() == enemy) // right capture
                {
                    enemySquares.add(right);
                }
            } catch (Exception e) {
            }

            if (!enemySquares.isEmpty()) {
                destination = enemySquares.get(rand.nextInt(enemySquares.size()));
                if (destination.getTeam() == OPPONENT)
                    singleGame.incrementPlayerPoints();
                else if (destination.getTeam() == YOU)
                    singleGame.incrementComputerPoints();
                animateMove(square, destination, singleGame);
                moveSuccess = true;
            }

        }

        while (!moveSuccess && (!forwardImpossible || !leftImpossible || !rightImpossible)) {
            options.clear();

            if (!forwardImpossible)
                options.add(Direction.UP);
            if (!leftImpossible)
                options.add(Direction.LEFT);
            if (!rightImpossible)
                options.add(Direction.RIGHT);


            switch (options.get(rand.nextInt(options.size()))) {
                case UP: // tries to move forward

                    if (square.getPieceCount() == 0)
                        count = rand.nextInt(2) + 1;
                    else
                        count = 1;

                    try {

                        while (!nothingInWayForward(board, square, count)) {
                            count--;
                            if (count == 0) {
                                forwardImpossible = true;
                                break;
                            }
                        }
                        if (count > 0) {
                            destination = board[square.getI()][square.getJ() + square.getTeam() * count];
                            animateMove(square, destination, singleGame);
                            moveSuccess = true;
                        }
                    } catch (Exception e) {
                        forwardImpossible = true;
                    }
                    break;
                case LEFT: // tries to capture diagonally to left
                    try {
                        destination = board[square.getI() + square.getTeam()][square.getJ() + square.getTeam()];
                        if (destination.getTeam() == -square.getTeam()) {
                            if (destination.getTeam() == OPPONENT)
                                singleGame.incrementPlayerPoints();
                            else if (destination.getTeam() == YOU)
                                singleGame.incrementComputerPoints();

                            animateMove(square, destination, singleGame);
                            moveSuccess = true;
                        } else {
                            leftImpossible = true;
                        }
                    } catch (Exception e) {
                        leftImpossible = true;
                    }
                    break;
                case RIGHT: // tries to capture diagonally to right
                    try {
                        destination = board[square.getI() - square.getTeam()][square.getJ() + square.getTeam()];
                        if (destination.getTeam() == -square.getTeam()) {
                            if (destination.getTeam() == OPPONENT)
                                singleGame.incrementPlayerPoints();
                            else if (destination.getTeam() == YOU)
                                singleGame.incrementComputerPoints();

                            animateMove(square, destination, singleGame);
                            moveSuccess = true;
                        } else {
                            rightImpossible = true;
                        }
                    } catch (Exception e) {
                        rightImpossible = true;
                    }
                    break;
            }
        }

        if (destination.getJ() == 7 && destination.getTeam() == OPPONENT && moveSuccess) {
            destination.setPiece(Piece.QUEEN);
        } else if (destination.getJ() == 0 && destination.getTeam() == YOU && moveSuccess) {
            destination.setPiece(Piece.QUEEN);
        }

        return moveSuccess;
    }

    ArrayList<Square> getEnemiesOfRook(Square[][] board, Square square, int maxDistance) {
        ArrayList<Square> enemies = new ArrayList<>();

        int distance = 0;
        while (maxDistance > distance && nothingInWayForward(board, square, distance)) {
            distance++;
        }
        if (enemyInWayForward(board, square, distance)) {
            enemies.add(board[square.getI()][square.getJ() + square.getTeam() * distance]);
        }

        distance = 0;
        while (maxDistance > distance && nothingInWayLeft(board, square, distance)) {
            distance++;
        }
        if (enemyInWayLeft(board, square, distance)) {
            enemies.add(board[square.getI() + square.getTeam() * distance][square.getJ()]);
        }

        distance = 0;
        while (maxDistance > distance && nothingInWayRight(board, square, distance)) {
            distance++;
        }
        if (enemyInWayRight(board, square, distance)) {
            enemies.add(board[square.getI() - square.getTeam() * distance][square.getJ()]);
        }

        distance = 0;
        while (maxDistance > distance && nothingInWayBack(board, square, distance)) {
            distance++;
        }
        if (enemyInWayBack(board, square, distance)) {
            enemies.add(board[square.getI()][square.getJ() - square.getTeam() * distance]);
        }

        return enemies;
    }

    ArrayList<Square> getEnemiesOfRook(Square[][] board, Square square) {
        return getEnemiesOfRook(board, square, SingleGame.BOARD_SIZE);
    }

    boolean rookHasEnemies(Square[][] board, Square square, int maxDistance) {
        return !getEnemiesOfRook(board, square, maxDistance).isEmpty();
    }

    boolean rookHasEnemies(Square[][] board, Square square) {
        return rookHasEnemies(board, square, SingleGame.BOARD_SIZE);
    }

    boolean moveRook(Square[][] board, Square square, SingleGame singleGame, int max, boolean bloodThirsty) {
        List<Direction> options = new ArrayList<>();

        boolean moveSuccess = false;
        boolean forwardImpossible = false;
        boolean leftImpossible = false;
        boolean rightImpossible = false;
        boolean backImpossible = false;
        int maxForward = max;
        int maxRight = max;
        int maxLeft = max;
        int maxBack = max;
        int count;

        rand.nextInt();

        if (bloodThirsty) {
            ArrayList<Square> enemySquares = getEnemiesOfRook(board, square, max);
            if (!enemySquares.isEmpty()) {
                destination = enemySquares.get(rand.nextInt(options.size()));
                if (destination.getTeam() == OPPONENT)
                    singleGame.incrementPlayerPoints();
                else if (destination.getTeam() == YOU)
                    singleGame.incrementComputerPoints();
                animateMove(square, destination, singleGame);
                moveSuccess = true;
            }
        }

        while (!moveSuccess && (!forwardImpossible || !leftImpossible || !rightImpossible || !backImpossible)) {

            options.clear();

            if (!forwardImpossible)
                options.add(Direction.UP);
            if (!leftImpossible)
                options.add(Direction.LEFT);
            if (!rightImpossible)
                options.add(Direction.RIGHT);
            if (!backImpossible)
                options.add(Direction.BACK);

            switch (options.get(rand.nextInt(options.size()))) {
                case UP: // Tries to move forward

                    count = rand.nextInt(maxForward) + 1;

                    try {

                        while (!nothingInWayForward(board, square, count)) {

                            if (enemyInWayForward(board, square, count)) {
                                while (!nothingInWayForward(board, square, count - 1))
                                    count--;
                                break;
                            }
                            count--;
                            if (count == 0) {
                                forwardImpossible = true;
                                break;
                            }

                        }
                        destination = board[square.getI()][square.getJ() + square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam()) {
                            if (destination.getTeam() == OPPONENT)
                                singleGame.incrementPlayerPoints();
                            else if (destination.getTeam() == YOU)
                                singleGame.incrementComputerPoints();

                            animateMove(square, destination, singleGame);
                            moveSuccess = true;
                        }
                    } catch (Exception e) {
                        maxForward--;
                        if (maxForward == 0)
                            forwardImpossible = true;
                    }
                    break;
                case LEFT: // Tries to move left

                    count = rand.nextInt(maxLeft) + 1;

                    try {

                        while (!nothingInWayLeft(board, square, count)) {
                            if (enemyInWayLeft(board, square, count)) {
                                while (!nothingInWayLeft(board, square, count - 1))
                                    count--;
                                break;
                            }
                            count--;

                            if (count == 0) {
                                leftImpossible = true;
                                break;
                            }

                        }
                        destination = board[square.getI() + square.getTeam() * count][square.getJ()];
                        if (count > 0 && destination.getTeam() != square.getTeam()) {
                            if (destination.getTeam() == OPPONENT)
                                singleGame.incrementPlayerPoints();
                            else if (destination.getTeam() == YOU)
                                singleGame.incrementComputerPoints();

                            animateMove(square, destination, singleGame);
                            moveSuccess = true;
                        }
                    } catch (Exception e) {
                        maxLeft--;
                        if (maxLeft == 0)
                            leftImpossible = true;
                    }
                    break;
                case RIGHT: // Tries to move right

                    count = rand.nextInt(maxRight) + 1;

                    try {

                        while (!nothingInWayRight(board, square, count)) {
                            if (enemyInWayRight(board, square, count)) {
                                while (!nothingInWayRight(board, square, count - 1))
                                    count--;
                                break;
                            }
                            count--;

                            if (count == 0) {
                                rightImpossible = true;
                                break;
                            }

                        }
                        destination = board[square.getI() - square.getTeam() * count][square.getJ()];
                        if (count > 0 && destination.getTeam() != square.getTeam()) {
                            if (destination.getTeam() == OPPONENT)
                                singleGame.incrementPlayerPoints();
                            else if (destination.getTeam() == YOU)
                                singleGame.incrementComputerPoints();

                            animateMove(square, destination, singleGame);
                            moveSuccess = true;
                        }
                    } catch (Exception e) {
                        maxRight--;
                        if (maxRight == 0)
                            rightImpossible = true;
                    }
                    break;
                case BACK: // Tries to move back

                    count = rand.nextInt(maxBack) + 1;

                    try {

                        while (!nothingInWayBack(board, square, count)) {
                            if (enemyInWayBack(board, square, count)) {
                                while (!nothingInWayBack(board, square, count - 1))
                                    count--;
                                break;
                            }
                            count--;

                            if (count == 0) {
                                backImpossible = true;
                                break;
                            }

                        }
                        destination = board[square.getI()][square.getJ() - square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam()) {
                            if (destination.getTeam() == OPPONENT)
                                singleGame.incrementPlayerPoints();
                            else if (destination.getTeam() == YOU)
                                singleGame.incrementComputerPoints();

                            animateMove(square, destination, singleGame);
                            moveSuccess = true;
                        }
                    } catch (Exception e) {
                        maxBack--;
                        if (maxBack == 0)
                            backImpossible = true;
                    }
                    break;
            }
        }

        return moveSuccess;
    }

    boolean moveRook(Square[][] board, Square square, SingleGame singleGame, boolean bloodThirsty) {
        List<Direction> options = new ArrayList<>();

        boolean moveSuccess = false;
        boolean forwardImpossible = false;
        boolean leftImpossible = false;
        boolean rightImpossible = false;
        boolean backImpossible = false;
        int maxForward = 8;
        int maxRight = 8;
        int maxLeft = 8;
        int maxBack = 8;
        int count;

        rand.nextInt();

        if (bloodThirsty) {
            ArrayList<Square> enemySquares = getEnemiesOfRook(board, square);
            if (!enemySquares.isEmpty()) {
                destination = enemySquares.get(rand.nextInt(enemySquares.size()));
                if (destination.getTeam() == OPPONENT)
                    singleGame.incrementPlayerPoints();
                else if (destination.getTeam() == YOU)
                    singleGame.incrementComputerPoints();
                animateMove(square, destination, singleGame);
                moveSuccess = true;
            }
        }

        while (!moveSuccess && (!forwardImpossible || !leftImpossible || !rightImpossible || !backImpossible)) {
            options.clear();


            if (!forwardImpossible)
                options.add(Direction.UP);
            if (!leftImpossible)
                options.add(Direction.LEFT);
            if (!rightImpossible)
                options.add(Direction.RIGHT);
            if (!backImpossible)
                options.add(Direction.BACK);

            switch (options.get(rand.nextInt(options.size()))) {
                case UP: // Tries to move forward

                    count = rand.nextInt(maxForward) + 1;

                    try {

                        while (!nothingInWayForward(board, square, count)) {

                            if (enemyInWayForward(board, square, count)) {
                                while (!nothingInWayForward(board, square, count - 1))
                                    count--;
                                break;
                            }
                            count--;
                            if (count == 0) {
                                forwardImpossible = true;
                                break;
                            }

                        }
                        destination = board[square.getI()][square.getJ() + square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam()) {
                            if (destination.getTeam() == OPPONENT)
                                singleGame.incrementPlayerPoints();
                            else if (destination.getTeam() == YOU)
                                singleGame.incrementComputerPoints();

                            animateMove(square, destination, singleGame);
                            moveSuccess = true;
                        }
                    } catch (Exception e) {
                        maxForward--;
                        if (maxForward == 0)
                            forwardImpossible = true;
                    }
                    break;
                case LEFT: // Tries to move left

                    count = rand.nextInt(maxLeft) + 1;

                    try {

                        while (!nothingInWayLeft(board, square, count)) {
                            if (enemyInWayLeft(board, square, count)) {
                                while (!nothingInWayLeft(board, square, count - 1))
                                    count--;
                                break;
                            }
                            count--;

                            if (count == 0) {
                                leftImpossible = true;
                                break;
                            }

                        }
                        destination = board[square.getI() + square.getTeam() * count][square.getJ()];
                        if (count > 0 && destination.getTeam() != square.getTeam()) {
                            if (destination.getTeam() == OPPONENT)
                                singleGame.incrementPlayerPoints();
                            else if (destination.getTeam() == YOU)
                                singleGame.incrementComputerPoints();

                            animateMove(square, destination, singleGame);
                            moveSuccess = true;
                        }
                    } catch (Exception e) {
                        maxLeft--;
                        if (maxLeft == 0)
                            leftImpossible = true;
                    }
                    break;
                case RIGHT: // Tries to move right

                    count = rand.nextInt(maxRight) + 1;

                    try {

                        while (!nothingInWayRight(board, square, count)) {
                            if (enemyInWayRight(board, square, count)) {
                                while (!nothingInWayRight(board, square, count - 1))
                                    count--;
                                break;
                            }
                            count--;

                            if (count == 0) {
                                rightImpossible = true;
                                break;
                            }

                        }
                        destination = board[square.getI() - square.getTeam() * count][square.getJ()];
                        if (count > 0 && destination.getTeam() != square.getTeam()) {
                            if (destination.getTeam() == OPPONENT)
                                singleGame.incrementPlayerPoints();
                            else if (destination.getTeam() == YOU)
                                singleGame.incrementComputerPoints();

                            animateMove(square, destination, singleGame);
                            moveSuccess = true;
                        }
                    } catch (Exception e) {
                        maxRight--;
                        if (maxRight == 0)
                            rightImpossible = true;
                    }
                    break;
                case BACK: // Tries to move back

                    count = rand.nextInt(maxBack) + 1;

                    try {

                        while (!nothingInWayBack(board, square, count)) {
                            if (enemyInWayBack(board, square, count)) {
                                while (!nothingInWayBack(board, square, count - 1))
                                    count--;
                                break;
                            }
                            count--;

                            if (count == 0) {
                                backImpossible = true;
                                break;
                            }

                        }
                        destination = board[square.getI()][square.getJ() - square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam()) {
                            if (destination.getTeam() == OPPONENT)
                                singleGame.incrementPlayerPoints();
                            else if (destination.getTeam() == YOU)
                                singleGame.incrementComputerPoints();

                            animateMove(square, destination, singleGame);
                            moveSuccess = true;
                        }
                    } catch (Exception e) {
                        maxBack--;
                        if (maxBack == 0)
                            backImpossible = true;
                    }
                    break;

            }
        }

        return moveSuccess;
    }

    ArrayList<Square> getKnightMoves(Square[][] board, Square square) {
        ArrayList<Square> moves = new ArrayList<>();

        int[] firstPermutation = {1, 2};
        int[] currentPermutation = firstPermutation.clone();
        int i = square.getI();
        int j = square.getJ();

        Square currentSquare;
        do {
            try {
                currentSquare = board[i + currentPermutation[0]][j + currentPermutation[1]];

                if (currentSquare.getTeam() != square.getTeam()) {
                    moves.add(currentSquare);
                }
            }
            catch (Exception e) {
            }

            currentPermutation[0] = -currentPermutation[0]; // Negate i

            try {
                currentSquare = board[i + currentPermutation[0]][j + currentPermutation[1]];

                if (currentSquare.getTeam() != square.getTeam()) {
                    moves.add(currentSquare);
                }
            }
            catch (Exception e) {
            }

            //swap i and j
            int temp = currentPermutation[0];
            currentPermutation[0] = currentPermutation[1];
            currentPermutation[1] = temp;

        } while (!Arrays.equals(currentPermutation, firstPermutation));

        return moves;
    }

    boolean knightHasEnemies(Square[][] board, Square square) {
        ArrayList<Square> moves = getKnightMoves(board, square);
        boolean hasEnemies = false;

        for(Square move : moves) {
            if (move.getTeam() == -square.getTeam()) {
                hasEnemies = true;
                break;
            }
        }

        return hasEnemies;
    }

    boolean moveKnight(Square[][] board, Square square, SingleGame singleGame, boolean bloodThirsty) {

        boolean moveSuccess = false;

        rand.nextInt();
        int enemy = -square.getTeam();

        ArrayList<Square> possibleMoves = getKnightMoves(board, square);

        if (!possibleMoves.isEmpty() && bloodThirsty) {
            ArrayList<Square> enemyMoves = new ArrayList<>();
            for (Square move : possibleMoves) {
                if (move.getTeam() == enemy) {
                    enemyMoves.add(move);
                }
            }

            if (!enemyMoves.isEmpty()) {
                destination = enemyMoves.get(rand.nextInt(enemyMoves.size()));

                if (destination.getTeam() == OPPONENT)
                    singleGame.incrementPlayerPoints();
                else if (destination.getTeam() == YOU)
                    singleGame.incrementComputerPoints();

                animateMove(square, destination, singleGame);
                moveSuccess = true;
            }
        }
        if (!possibleMoves.isEmpty() && !moveSuccess) {
            destination = possibleMoves.get(rand.nextInt(possibleMoves.size()));

            if (destination.getTeam() == OPPONENT)
                singleGame.incrementPlayerPoints();
            else if (destination.getTeam() == YOU)
                singleGame.incrementComputerPoints();

            animateMove(square, destination, singleGame);
            moveSuccess = true;
        }

        return moveSuccess;
    }

    ArrayList<Square> getEnemiesOfBishop(Square[][] board, Square square, int maxDistance) {
        ArrayList<Square> enemies = new ArrayList<>();

        int distance = 0;
        while (maxDistance > distance && nothingInWayRightUp(board, square, distance)) {
            distance++;
        }
        if (enemyInWayRightUp(board, square, distance)) {
            enemies.add(board[square.getI() - square.getTeam() * distance][square.getJ() + square.getTeam() * distance]);
        }

        distance = 0;
        while (maxDistance > distance && nothingInWayLeftUp(board, square, distance)) {
            distance++;
        }
        if (enemyInWayLeftUp(board, square, distance)) {
            enemies.add(board[square.getI() + square.getTeam() * distance][square.getJ() + square.getTeam() * distance]);
        }

        distance = 0;
        while (maxDistance > distance && nothingInWayLeftDown(board, square, distance)) {
            distance++;
        }
        if (enemyInWayLeftDown(board, square, distance)) {
            enemies.add(board[square.getI() + square.getTeam() * distance][square.getJ() - square.getTeam() * distance]);
        }

        distance = 0;
        while (maxDistance > distance && nothingInWayRightDown(board, square, distance)) {
            distance++;
        }
        if (enemyInWayRightDown(board, square, distance)) {
            enemies.add(board[square.getI() - square.getTeam() * distance][square.getJ() - square.getTeam() * distance]);
        }

        return enemies;
    }

    ArrayList<Square> getEnemiesOfBishop(Square[][] board, Square square) {
        return getEnemiesOfBishop(board, square, SingleGame.BOARD_SIZE);
    }

    boolean bishopHasEnemies(Square[][] board, Square square, int maxDistance) {
        return !getEnemiesOfBishop(board, square, maxDistance).isEmpty();
    }

    boolean bishopHasEnemies(Square[][] board, Square square) {
        return bishopHasEnemies(board, square, SingleGame.BOARD_SIZE);
    }

    boolean moveBishop(Square[][] board, Square square, SingleGame singleGame, int max, boolean bloodThirsty) {
        List<Direction> options = new ArrayList<>();

        boolean moveSuccess = false;
        boolean rightUpImpossible = false;
        boolean leftUpImpossible = false;
        boolean leftDownImpossible = false;
        boolean rightDownImpossible = false;
        int maxRightUp = max;
        int maxLeftUp = max;
        int maxLeftDown = max;
        int maxRightDown = max;

        rand.nextInt();

        if (bloodThirsty) {
            ArrayList<Square> enemySquares = getEnemiesOfBishop(board, square, max);
            if (!enemySquares.isEmpty()) {
                destination = enemySquares.get(rand.nextInt(options.size()));
                if (destination.getTeam() == OPPONENT)
                    singleGame.incrementPlayerPoints();
                else if (destination.getTeam() == YOU)
                    singleGame.incrementComputerPoints();
                animateMove(square, destination, singleGame);
                moveSuccess = true;
            }
        }

        int count;

        while (!moveSuccess && (!rightUpImpossible || !leftUpImpossible || !leftDownImpossible || !rightDownImpossible)) {

            options.clear();

            if (!rightUpImpossible)
                options.add(Direction.RIGHTUP);
            if (!leftUpImpossible)
                options.add(Direction.LEFTUP);
            if (!leftDownImpossible)
                options.add(Direction.LEFTBACK);
            if (!rightDownImpossible)
                options.add(Direction.RIGHTBACK);

            switch (options.get(rand.nextInt(options.size()))) {
                case RIGHTUP: // Tries to move right and up

                    count = rand.nextInt(maxRightUp) + 1;

                    try {
                        while (!nothingInWayRightUp(board, square, count)) {
                            if (enemyInWayRightUp(board, square, count)) {
                                while (!nothingInWayRightUp(board, square, count - 1))
                                    count--;
                                break;
                            }
                            count--;
                            if (count == 0) {
                                rightUpImpossible = true;
                                break;
                            }
                        }
                        destination = board[square.getI() - square.getTeam() * count][square.getJ() + square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam()) {
                            if (destination.getTeam() == OPPONENT)
                                singleGame.incrementPlayerPoints();
                            else if (destination.getTeam() == YOU)
                                singleGame.incrementComputerPoints();

                            animateMove(square, destination, singleGame);
                            moveSuccess = true;
                        }
                    } catch (Exception e) {
                        maxRightUp--;
                        if (maxRightUp == 0)
                            rightUpImpossible = true;
                    }
                    break;
                case LEFTUP: // Tries to move left and up

                    count = rand.nextInt(maxLeftUp) + 1;

                    try {

                        while (!nothingInWayLeftUp(board, square, count)) {
                            if (enemyInWayLeftUp(board, square, count)) {
                                while (!nothingInWayLeftUp(board, square, count - 1))
                                    count--;
                                break;
                            }
                            count--;
                            if (count == 0) {
                                leftUpImpossible = true;
                                break;
                            }
                        }
                        destination = board[square.getI() + square.getTeam() * count][square.getJ() + square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam()) {
                            if (destination.getTeam() == OPPONENT)
                                singleGame.incrementPlayerPoints();
                            else if (destination.getTeam() == YOU)
                                singleGame.incrementComputerPoints();

                            animateMove(square, destination, singleGame);
                            moveSuccess = true;
                        }
                    } catch (Exception e) {
                        maxLeftUp--;
                        if (maxLeftUp == 0)
                            leftUpImpossible = true;
                    }
                    break;
                case LEFTBACK: // Tries to move left and down


                    count = rand.nextInt(maxLeftDown) + 1;

                    try {

                        while (!nothingInWayLeftDown(board, square, count)) {
                            if (enemyInWayLeftDown(board, square, count)) {
                                while (!nothingInWayLeftDown(board, square, count - 1))
                                    count--;
                                break;
                            }
                            count--;
                            if (count == 0) {
                                leftDownImpossible = true;
                                break;
                            }
                        }
                        destination = board[square.getI() + square.getTeam() * count][square.getJ() - square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam()) {
                            if (destination.getTeam() == OPPONENT)
                                singleGame.incrementPlayerPoints();
                            else if (destination.getTeam() == YOU)
                                singleGame.incrementComputerPoints();

                            animateMove(square, destination, singleGame);
                            moveSuccess = true;
                        }
                    } catch (Exception e) {
                        maxLeftDown--;
                        if (maxLeftDown == 0)
                            leftDownImpossible = true;
                    }
                    break;
                case RIGHTBACK: // Tries to move right and down

                    count = rand.nextInt(maxRightDown) + 1;

                    try {

                        while (!nothingInWayRightDown(board, square, count)) {
                            if (enemyInWayRightDown(board, square, count)) {
                                while (!nothingInWayRightDown(board, square, count - 1))
                                    count--;
                                break;
                            }
                            count--;
                            if (count == 0) {
                                rightDownImpossible = true;
                                break;
                            }
                        }
                        destination = board[square.getI() - square.getTeam() * count][square.getJ() - square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam()) {
                            if (destination.getTeam() == OPPONENT)
                                singleGame.incrementPlayerPoints();
                            else if (destination.getTeam() == YOU)
                                singleGame.incrementComputerPoints();

                            animateMove(square, destination, singleGame);
                            moveSuccess = true;
                        }
                    } catch (Exception e) {
                        maxRightDown--;
                        if (maxRightDown == 0)
                            rightDownImpossible = true;
                    }
                    break;
            }

        }

        return moveSuccess;
    }

    boolean moveBishop(Square[][] board, Square square, SingleGame singleGame, boolean bloodThirsty) {
        List<Direction> options = new ArrayList<>();

        boolean moveSuccess = false;
        boolean rightUpImpossible = false;
        boolean leftUpImpossible = false;
        boolean leftDownImpossible = false;
        boolean rightDownImpossible = false;
        int maxRightUp = 8;
        int maxLeftUp = 8;
        int maxLeftDown = 8;
        int maxRightDown = 8;
        int count;

        rand.nextInt();

        if (bloodThirsty) {
            ArrayList<Square> enemySquares = getEnemiesOfBishop(board, square);
            if (!enemySquares.isEmpty()) {
                destination = enemySquares.get(rand.nextInt(enemySquares.size()));
                if (destination.getTeam() == OPPONENT)
                    singleGame.incrementPlayerPoints();
                else if (destination.getTeam() == YOU)
                    singleGame.incrementComputerPoints();
                animateMove(square, destination, singleGame);
                moveSuccess = true;
            }
        }

        while (!moveSuccess && (!rightUpImpossible || !leftUpImpossible || !leftDownImpossible || !rightDownImpossible)) {
            options.clear();

            if (!rightUpImpossible)
                options.add(Direction.RIGHTUP);
            if (!leftUpImpossible)
                options.add(Direction.LEFTUP);
            if (!leftDownImpossible)
                options.add(Direction.LEFTBACK);
            if (!rightDownImpossible)
                options.add(Direction.RIGHTBACK);

            switch (options.get(rand.nextInt(options.size()))) {
                case RIGHTUP: // Tries to move right and up
                    count = rand.nextInt(maxRightUp) + 1;

                    try {
                        while (!nothingInWayRightUp(board, square, count)) {
                            if (enemyInWayRightUp(board, square, count)) {
                                while (!nothingInWayRightUp(board, square, count - 1))
                                    count--;
                                break;
                            }
                            count--;
                            if (count == 0) {
                                rightUpImpossible = true;
                                break;
                            }
                        }
                        destination = board[square.getI() - square.getTeam() * count][square.getJ() + square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam()) {
                            if (destination.getTeam() == OPPONENT)
                                singleGame.incrementPlayerPoints();
                            else if (destination.getTeam() == YOU)
                                singleGame.incrementComputerPoints();

                            animateMove(square, destination, singleGame);
                            moveSuccess = true;
                        }
                    } catch (Exception e) {
                        maxRightUp--;
                        if (maxRightUp == 0)
                            rightUpImpossible = true;
                    }
                    break;
                case LEFTUP: // Tries to move left and up
                    count = rand.nextInt(maxLeftUp) + 1;

                    try {

                        while (!nothingInWayLeftUp(board, square, count)) {
                            if (enemyInWayLeftUp(board, square, count)) {
                                while (!nothingInWayLeftUp(board, square, count - 1))
                                    count--;
                                break;
                            }
                            count--;
                            if (count == 0) {
                                leftUpImpossible = true;
                                break;
                            }
                        }
                        destination = board[square.getI() + square.getTeam() * count][square.getJ() + square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam()) {
                            if (destination.getTeam() == OPPONENT)
                                singleGame.incrementPlayerPoints();
                            else if (destination.getTeam() == YOU)
                                singleGame.incrementComputerPoints();

                            animateMove(square, destination, singleGame);
                            moveSuccess = true;
                        }
                    } catch (Exception e) {
                        maxLeftUp--;
                        if (maxLeftUp == 0)
                            leftUpImpossible = true;
                    }
                    break;
                case LEFTBACK: // Tries to move left and down
                    count = rand.nextInt(maxLeftDown) + 1;

                    try {

                        while (!nothingInWayLeftDown(board, square, count)) {
                            if (enemyInWayLeftDown(board, square, count)) {
                                while (!nothingInWayLeftDown(board, square, count - 1))
                                    count--;
                                break;
                            }
                            count--;
                            if (count == 0) {
                                leftDownImpossible = true;
                                break;
                            }
                        }
                        destination = board[square.getI() + square.getTeam() * count][square.getJ() - square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam()) {
                            if (destination.getTeam() == OPPONENT)
                                singleGame.incrementPlayerPoints();
                            else if (destination.getTeam() == YOU)
                                singleGame.incrementComputerPoints();

                            animateMove(square, destination, singleGame);
                            moveSuccess = true;
                        }
                    } catch (Exception e) {
                        maxLeftDown--;
                        if (maxLeftDown == 0)
                            leftDownImpossible = true;
                    }
                    break;
                case RIGHTBACK: // Tries to move right and down

                    count = rand.nextInt(maxRightDown) + 1;

                    try {

                        while (!nothingInWayRightDown(board, square, count)) {
                            if (enemyInWayRightDown(board, square, count)) {
                                while (!nothingInWayRightDown(board, square, count - 1))
                                    count--;
                                break;
                            }
                            count--;
                            if (count == 0) {
                                rightDownImpossible = true;
                                break;
                            }
                        }
                        destination = board[square.getI() - square.getTeam() * count][square.getJ() - square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam()) {
                            if (destination.getTeam() == OPPONENT)
                                singleGame.incrementPlayerPoints();
                            else if (destination.getTeam() == YOU)
                                singleGame.incrementComputerPoints();

                            animateMove(square, destination, singleGame);
                            moveSuccess = true;
                        }
                    } catch (Exception e) {
                        maxRightDown--;
                        if (maxRightDown == 0)
                            rightDownImpossible = true;
                    }
                    break;
            }

        }

        return moveSuccess;
    }

    boolean kingHasEnemies(Square[][] board, Square square) {
        return rookHasEnemies(board, square, 1) || bishopHasEnemies(board, square, 1);
    }

    boolean moveKing(Square[][] board, Square square, SingleGame singleGame, boolean bloodThirsty) {
        List<KQMODE> options = new ArrayList<>();

        boolean moveSuccess = false;
        boolean bishopImpossible = false;
        boolean rookImpossible = false;

        if (bloodThirsty) {
            ArrayList<Square> enemySquares = getEnemiesOfBishop(board, square, 1);
            enemySquares.addAll(getEnemiesOfRook(board, square, 1));

            if (!enemySquares.isEmpty()) {
                destination = enemySquares.get(rand.nextInt(enemySquares.size()));
                if (destination.getTeam() == OPPONENT)
                    singleGame.incrementPlayerPoints();
                else if (destination.getTeam() == YOU)
                    singleGame.incrementComputerPoints();
                animateMove(square, destination, singleGame);
                moveSuccess = true;
            }
        }

        while (!moveSuccess && (!bishopImpossible || !rookImpossible)) {
            options.clear();
            if (!bishopImpossible)
                options.add(KQMODE.BISHOP);
            if (!rookImpossible)
                options.add(KQMODE.ROOK);

            switch (options.get(rand.nextInt(options.size()))) {
                case BISHOP:
                    moveSuccess = moveBishop(board, square, singleGame, 1, false);
                    bishopImpossible = !moveSuccess;
                    break;
                case ROOK:
                    moveSuccess = moveRook(board, square, singleGame, 1, false);
                    rookImpossible = !moveSuccess;
                    break;
            }
        }

        return moveSuccess;
    }

    boolean queenHasEnemies(Square[][] board, Square square){
        return rookHasEnemies(board, square) || bishopHasEnemies(board, square);
    }

    boolean moveQueen(Square[][] board, Square square, SingleGame singleGame, boolean bloodThirsty) {
        boolean moveSuccess = false;
        boolean bishopImpossible = false;
        boolean rookImpossible = false;

        if (bloodThirsty) {
            ArrayList<Square> enemySquares = getEnemiesOfBishop(board, square);
            enemySquares.addAll(getEnemiesOfRook(board, square));

            if (!enemySquares.isEmpty()) {
                destination = enemySquares.get(rand.nextInt(enemySquares.size()));
                if (destination.getTeam() == OPPONENT)
                    singleGame.incrementPlayerPoints();
                else if (destination.getTeam() == YOU)
                    singleGame.incrementComputerPoints();
                animateMove(square, destination, singleGame);
                moveSuccess = true;
            }
        }

        while (!moveSuccess && (!bishopImpossible || !rookImpossible)) {
            if (rand.nextInt(2) == 0 && !bishopImpossible) {
                moveSuccess = moveBishop(board, square, singleGame, false);
                bishopImpossible = !moveSuccess;
            } else if (!rookImpossible) {
                moveSuccess = moveRook(board, square, singleGame, false);
                rookImpossible = !moveSuccess;
            }
        }

        return moveSuccess;
    }

    public synchronized boolean canPieceMove(Square[][] board, Square square, SingleGame singleGame) {
        boolean moveSuccess;
        switch (square.getPiece()) {
            case Piece.PAWN:
                moveSuccess = canPawnMove(board, square, singleGame);
                break;
            case Piece.ROOK:
                moveSuccess = canRookMove(board, square, singleGame);
                break;
            case Piece.KNIGHT:
                moveSuccess = canKnightMove(board, square, singleGame);
                break;
            case Piece.BISHOP:
                moveSuccess = canBishopMove(board, square, singleGame);
                break;
            case Piece.KING:
                moveSuccess = canKingMove(board, square, singleGame);
                break;
            case Piece.QUEEN:
                moveSuccess = canQueenMove(board, square, singleGame);
                break;
            default:
                moveSuccess = false;
                break;
        }

        return moveSuccess;
    }

    boolean canPawnMove(Square[][] board, Square square, SingleGame singleGame) {
        List<Direction> options = new ArrayList<>();

        boolean moveSuccess = false;
        boolean forwardImpossible = false;
        boolean leftImpossible = false;
        boolean rightImpossible = false;
        int count;

        rand.nextInt();

        while (!moveSuccess && (!forwardImpossible || !leftImpossible || !rightImpossible)) {
            options.clear();

            if (!forwardImpossible)
                options.add(Direction.UP);
            if (!leftImpossible)
                options.add(Direction.LEFT);
            if (!rightImpossible)
                options.add(Direction.RIGHT);

            switch (options.get(rand.nextInt(options.size()))) {
                case UP: // tries to move forward


                    if (square.getPieceCount() == 0)
                        count = rand.nextInt(2) + 1;
                    else
                        count = 1;

                    try {

                        while (!nothingInWayForward(board, square, count)) {
                            count--;
                            if (count == 0) {
                                forwardImpossible = true;
                                break;
                            }
                        }
                        if (count > 0) {
                            moveSuccess = true;
                        }
                    } catch (Exception e) {
                        forwardImpossible = true;
                    }
                    break;
                case LEFT: // tries to capture diagonally to left
                    try {
                        destination = board[square.getI() + square.getTeam()][square.getJ() + square.getTeam()];
                        if (destination.getTeam() == -square.getTeam()) {
                            moveSuccess = true;
                        } else {
                            leftImpossible = true;
                        }
                    } catch (Exception e) {
                        leftImpossible = true;
                    }
                    break;
                case RIGHT: // tries to capture diagonally to right
                    try {
                        destination = board[square.getI() - square.getTeam()][square.getJ() + square.getTeam()];
                        if (destination.getTeam() == -square.getTeam()) {
                            moveSuccess = true;
                        } else {
                            rightImpossible = true;
                        }
                    } catch (Exception e) {
                        rightImpossible = true;
                    }
                    break;
            }

        }

        return moveSuccess;
    }

    boolean canRookMove(Square[][] board, Square square, SingleGame singleGame, int max) {
        List<Direction> options = new ArrayList<>();

        boolean moveSuccess = false;
        boolean forwardImpossible = false;
        boolean leftImpossible = false;
        boolean rightImpossible = false;
        boolean backImpossible = false;
        int maxForward = max;
        int maxRight = max;
        int maxLeft = max;
        int maxBack = max;
        int count;


        rand.nextInt();

        while (!moveSuccess && (!forwardImpossible || !leftImpossible || !rightImpossible || !backImpossible)) {
            options.clear();


            if (!forwardImpossible)
                options.add(Direction.UP);
            if (!leftImpossible)
                options.add(Direction.LEFT);
            if (!rightImpossible)
                options.add(Direction.RIGHT);
            if (!backImpossible)
                options.add(Direction.BACK);

            switch (options.get(rand.nextInt(options.size()))) {
                case UP: // Tries to move forward

                    count = rand.nextInt(maxForward) + 1;

                    try {

                        while (!nothingInWayForward(board, square, count)) {

                            if (enemyInWayForward(board, square, count)) {
                                while (!nothingInWayForward(board, square, count - 1))
                                    count--;
                                break;
                            }
                            count--;
                            if (count == 0) {
                                forwardImpossible = true;
                                break;
                            }

                        }
                        destination = board[square.getI()][square.getJ() + square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam()) {
                            moveSuccess = true;
                        }
                    } catch (Exception e) {
                        maxForward--;
                        if (maxForward == 0)
                            forwardImpossible = true;
                    }
                    break;
                case LEFT: // Tries to move left

                    count = rand.nextInt(maxLeft) + 1;

                    try {

                        while (!nothingInWayLeft(board, square, count)) {
                            if (enemyInWayLeft(board, square, count)) {
                                while (!nothingInWayLeft(board, square, count - 1))
                                    count--;
                                break;
                            }
                            count--;

                            if (count == 0) {
                                leftImpossible = true;
                                break;
                            }

                        }
                        destination = board[square.getI() + square.getTeam() * count][square.getJ()];
                        if (count > 0 && destination.getTeam() != square.getTeam()) {
                            moveSuccess = true;
                        }
                    } catch (Exception e) {
                        maxLeft--;
                        if (maxLeft == 0)
                            leftImpossible = true;
                    }
                    break;
                case RIGHT: // Tries to move right

                    count = rand.nextInt(maxRight) + 1;

                    try {

                        while (!nothingInWayRight(board, square, count)) {
                            if (enemyInWayRight(board, square, count)) {
                                while (!nothingInWayRight(board, square, count - 1))
                                    count--;
                                break;
                            }
                            count--;

                            if (count == 0) {
                                rightImpossible = true;
                                break;
                            }

                        }
                        destination = board[square.getI() - square.getTeam() * count][square.getJ()];
                        if (count > 0 && destination.getTeam() != square.getTeam()) {
                            moveSuccess = true;
                        }
                    } catch (Exception e) {
                        maxRight--;
                        if (maxRight == 0)
                            rightImpossible = true;
                    }
                    break;
                case BACK: // Tries to move back

                    count = rand.nextInt(maxBack) + 1;

                    try {

                        while (!nothingInWayBack(board, square, count)) {
                            if (enemyInWayBack(board, square, count)) {
                                while (!nothingInWayBack(board, square, count - 1))
                                    count--;
                                break;
                            }
                            count--;

                            if (count == 0) {
                                backImpossible = true;
                                break;
                            }

                        }
                        destination = board[square.getI()][square.getJ() - square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam()) {
                            moveSuccess = true;
                        }
                    } catch (Exception e) {
                        maxBack--;
                        if (maxBack == 0)
                            backImpossible = true;
                    }
                    break;

            }
        }

        return moveSuccess;
    }

    boolean canRookMove(Square[][] board, Square square, SingleGame singleGame) {
        List<Direction> options = new ArrayList<>();

        boolean moveSuccess = false;
        boolean forwardImpossible = false;
        boolean leftImpossible = false;
        boolean rightImpossible = false;
        boolean backImpossible = false;
        int maxForward = 8;
        int maxRight = 8;
        int maxLeft = 8;
        int maxBack = 8;
        int count;


        rand.nextInt();

        while (!moveSuccess && (!forwardImpossible || !leftImpossible || !rightImpossible || !backImpossible)) {
            options.clear();


            if (!forwardImpossible)
                options.add(Direction.UP);
            if (!leftImpossible)
                options.add(Direction.LEFT);
            if (!rightImpossible)
                options.add(Direction.RIGHT);
            if (!backImpossible)
                options.add(Direction.BACK);

            switch (options.get(rand.nextInt(options.size()))) {
                case UP: // Tries to move forward

                    count = rand.nextInt(maxForward) + 1;

                    try {

                        while (!nothingInWayForward(board, square, count)) {

                            if (enemyInWayForward(board, square, count)) {
                                while (!nothingInWayForward(board, square, count - 1))
                                    count--;
                                break;
                            }
                            count--;
                            if (count == 0) {
                                forwardImpossible = true;
                                break;
                            }

                        }
                        destination = board[square.getI()][square.getJ() + square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam()) {

                            moveSuccess = true;
                        }
                    } catch (Exception e) {
                        maxForward--;
                        if (maxForward == 0)
                            forwardImpossible = true;
                    }
                    break;
                case LEFT: // Tries to move left

                    count = rand.nextInt(maxLeft) + 1;

                    try {

                        while (!nothingInWayLeft(board, square, count)) {
                            if (enemyInWayLeft(board, square, count)) {
                                while (!nothingInWayLeft(board, square, count - 1))
                                    count--;
                                break;
                            }
                            count--;

                            if (count == 0) {
                                leftImpossible = true;
                                break;
                            }

                        }
                        destination = board[square.getI() + square.getTeam() * count][square.getJ()];
                        if (count > 0 && destination.getTeam() != square.getTeam()) {

                            moveSuccess = true;
                        }
                    } catch (Exception e) {
                        maxLeft--;
                        if (maxLeft == 0)
                            leftImpossible = true;
                    }
                    break;
                case RIGHT: // Tries to move right

                    count = rand.nextInt(maxRight) + 1;

                    try {

                        while (!nothingInWayRight(board, square, count)) {
                            if (enemyInWayRight(board, square, count)) {
                                while (!nothingInWayRight(board, square, count - 1))
                                    count--;
                                break;
                            }
                            count--;

                            if (count == 0) {
                                rightImpossible = true;
                                break;
                            }

                        }
                        destination = board[square.getI() - square.getTeam() * count][square.getJ()];
                        if (count > 0 && destination.getTeam() != square.getTeam()) {

                            moveSuccess = true;
                        }
                    } catch (Exception e) {
                        maxRight--;
                        if (maxRight == 0)
                            rightImpossible = true;
                    }
                    break;
                case BACK: // Tries to move back

                    count = rand.nextInt(maxBack) + 1;

                    try {

                        while (!nothingInWayBack(board, square, count)) {
                            if (enemyInWayBack(board, square, count)) {
                                while (!nothingInWayBack(board, square, count - 1))
                                    count--;
                                break;
                            }
                            count--;

                            if (count == 0) {
                                backImpossible = true;
                                break;
                            }

                        }
                        destination = board[square.getI()][square.getJ() - square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam()) {
                            moveSuccess = true;
                        }
                    } catch (Exception e) {
                        maxBack--;
                        if (maxBack == 0)
                            backImpossible = true;
                    }
                    break;

            }
        }


        return moveSuccess;
    }

    boolean canKnightMove(Square[][] board, Square square, SingleGame singleGame) {
        List<Direction> options = new ArrayList<>();

        boolean moveSuccess = false;
        boolean _2R1U, _1R2U, _1L2U, _2L1U, _2L1D, _1L2D, _1R2D, _2R1D; // true means move is impossible
        _2R1U = _1R2U = _1L2U = _2L1U = _2L1D = _1L2D = _1R2D = _2R1D = false;


        rand.nextInt();

        while (!moveSuccess && (!_2R1U || !_1R2U || !_1L2U || !_2L1U || !_2L1D || !_1L2D || !_1R2D || !_2R1D)) {
            options.clear();

            if (!_2R1U)
                options.add(Direction._2R1U);
            if (!_1R2U)
                options.add(Direction._1R2U);
            if (!_1L2U)
                options.add(Direction._1L2U);
            if (!_2L1U)
                options.add(Direction._2L1U);
            if (!_2L1D)
                options.add(Direction._2L1D);
            if (!_1L2D)
                options.add(Direction._1L2D);
            if (!_1R2D)
                options.add(Direction._1R2D);
            if (!_2R1D)
                options.add(Direction._2R1D);


            switch (options.get(rand.nextInt(options.size()))) {
                case _2R1U:
                    try {
                        destination = board[square.getI() - square.getTeam() * 2][square.getJ() + square.getTeam()];
                        if (destination.getTeam() != square.getTeam()) {
                            moveSuccess = true;
                        } else {
                            _2R1U = true;
                        }
                    } catch (Exception e) {
                        _2R1U = true;
                    }
                    break;
                case _1R2U:
                    try {
                        destination = board[square.getI() - square.getTeam()][square.getJ() + square.getTeam() * 2];
                        if (destination.getTeam() != square.getTeam()) {
                            moveSuccess = true;
                        } else {
                            _1R2U = true;
                        }
                    } catch (Exception e) {
                        _1R2U = true;
                    }
                    break;
                case _1L2U:
                    try {
                        destination = board[square.getI() + square.getTeam()][square.getJ() + square.getTeam() * 2];
                        if (destination.getTeam() != square.getTeam()) {
                            moveSuccess = true;
                        } else {
                            _1L2U = true;
                        }
                    } catch (Exception e) {
                        _1L2U = true;
                    }
                    break;
                case _2L1U:
                    try {
                        destination = board[square.getI() + square.getTeam() * 2][square.getJ() + square.getTeam()];
                        if (destination.getTeam() != square.getTeam()) {
                            moveSuccess = true;
                        } else {
                            _2L1U = true;
                        }
                    } catch (Exception e) {
                        _2L1U = true;
                    }
                    break;
                case _2L1D:
                    try {
                        destination = board[square.getI() + square.getTeam() * 2][square.getJ() - square.getTeam()];
                        if (destination.getTeam() != square.getTeam()) {
                            moveSuccess = true;
                        } else {
                            _2L1D = true;
                        }
                    } catch (Exception e) {
                        _2L1D = true;
                    }
                    break;
                case _1L2D:
                    try {
                        destination = board[square.getI() + square.getTeam()][square.getJ() - square.getTeam() * 2];
                        if (destination.getTeam() != square.getTeam()) {
                            moveSuccess = true;
                        } else {
                            _1L2D = true;
                        }
                    } catch (Exception e) {
                        _1L2D = true;
                    }
                    break;
                case _1R2D:
                    try {
                        destination = board[square.getI() - square.getTeam()][square.getJ() - square.getTeam() * 2];
                        if (destination.getTeam() != square.getTeam()) {
                            moveSuccess = true;
                        } else {
                            _1R2D = true;
                        }
                    } catch (Exception e) {
                        _1R2D = true;
                    }
                    break;
                case _2R1D:
                    try {
                        destination = board[square.getI() - square.getTeam() * 2][square.getJ() - square.getTeam()];
                        if (destination.getTeam() != square.getTeam()) {
                            moveSuccess = true;
                        } else {
                            _2R1D = true;
                        }
                    } catch (Exception e) {
                        _2R1D = true;
                    }
                    break;

            }


        }


        return moveSuccess;
    }

    boolean canBishopMove(Square[][] board, Square square, SingleGame singleGame, int max) {
        boolean moveSuccess = false;
        boolean rightUpImpossible = false;
        boolean leftUpImpossible = false;
        boolean leftDownImpossible = false;
        boolean rightDownImpossible = false;
        int maxRightUp = max;
        int maxLeftUp = max;
        int maxLeftDown = max;
        int maxRightDown = max;


        rand.nextInt();

        while (!moveSuccess && (!rightUpImpossible || !leftUpImpossible || !leftDownImpossible || !rightDownImpossible)) {
            List<Direction> options = new ArrayList<>();
            int count;

            if (!rightUpImpossible)
                options.add(Direction.RIGHTUP);
            if (!leftUpImpossible)
                options.add(Direction.LEFTUP);
            if (!leftDownImpossible)
                options.add(Direction.LEFTBACK);
            if (!rightDownImpossible)
                options.add(Direction.RIGHTBACK);

            switch (options.get(rand.nextInt(options.size()))) {
                case RIGHTUP: // Tries to move right and up

                    count = rand.nextInt(maxRightUp) + 1;

                    try {
                        while (!nothingInWayRightUp(board, square, count)) {
                            if (enemyInWayRightUp(board, square, count)) {
                                while (!nothingInWayRightUp(board, square, count - 1))
                                    count--;
                                break;
                            }
                            count--;
                            if (count == 0) {
                                rightUpImpossible = true;
                                break;
                            }
                        }
                        destination = board[square.getI() - square.getTeam() * count][square.getJ() + square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam()) {
                            moveSuccess = true;
                        }
                    } catch (Exception e) {
                        maxRightUp--;
                        if (maxRightUp == 0)
                            rightUpImpossible = true;
                    }
                    break;
                case LEFTUP: // Tries to move left and up

                    count = rand.nextInt(maxLeftUp) + 1;

                    try {

                        while (!nothingInWayLeftUp(board, square, count)) {
                            if (enemyInWayLeftUp(board, square, count)) {
                                while (!nothingInWayLeftUp(board, square, count - 1))
                                    count--;
                                break;
                            }
                            count--;
                            if (count == 0) {
                                leftUpImpossible = true;
                                break;
                            }
                        }
                        destination = board[square.getI() + square.getTeam() * count][square.getJ() + square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam()) {
                            moveSuccess = true;
                        }
                    } catch (Exception e) {
                        maxLeftUp--;
                        if (maxLeftUp == 0)
                            leftUpImpossible = true;
                    }
                    break;
                case LEFTBACK: // Tries to move left and down


                    count = rand.nextInt(maxLeftDown) + 1;

                    try {

                        while (!nothingInWayLeftDown(board, square, count)) {
                            if (enemyInWayLeftDown(board, square, count)) {
                                while (!nothingInWayLeftDown(board, square, count - 1))
                                    count--;
                                break;
                            }
                            count--;
                            if (count == 0) {
                                leftDownImpossible = true;
                                break;
                            }
                        }
                        destination = board[square.getI() + square.getTeam() * count][square.getJ() - square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam()) {
                            moveSuccess = true;
                        }
                    } catch (Exception e) {
                        maxLeftDown--;
                        if (maxLeftDown == 0)
                            leftDownImpossible = true;
                    }
                    break;
                case RIGHTBACK: // Tries to move right and down

                    count = rand.nextInt(maxRightDown) + 1;

                    try {

                        while (!nothingInWayRightDown(board, square, count)) {
                            if (enemyInWayRightDown(board, square, count)) {
                                while (!nothingInWayRightDown(board, square, count - 1))
                                    count--;
                                break;
                            }
                            count--;
                            if (count == 0) {
                                rightDownImpossible = true;
                                break;
                            }
                        }
                        destination = board[square.getI() - square.getTeam() * count][square.getJ() - square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam()) {
                            moveSuccess = true;
                        }
                    } catch (Exception e) {
                        maxRightDown--;
                        if (maxRightDown == 0)
                            rightDownImpossible = true;
                    }
                    break;
            }

        }

        return moveSuccess;
    }

    boolean canBishopMove(Square[][] board, Square square, SingleGame singleGame) {
        List<Direction> options = new ArrayList<>();

        boolean moveSuccess = false;
        boolean rightUpImpossible = false;
        boolean leftUpImpossible = false;
        boolean leftDownImpossible = false;
        boolean rightDownImpossible = false;
        int maxRightUp = 8;
        int maxLeftUp = 8;
        int maxLeftDown = 8;
        int maxRightDown = 8;
        int count;


        rand.nextInt();

        while (!moveSuccess && (!rightUpImpossible || !leftUpImpossible || !leftDownImpossible || !rightDownImpossible)) {
            options.clear();


            if (!rightUpImpossible)
                options.add(Direction.RIGHTUP);
            if (!leftUpImpossible)
                options.add(Direction.LEFTUP);
            if (!leftDownImpossible)
                options.add(Direction.LEFTBACK);
            if (!rightDownImpossible)
                options.add(Direction.RIGHTBACK);

            switch (options.get(rand.nextInt(options.size()))) {
                case RIGHTUP: // Tries to move right and up

                    count = rand.nextInt(maxRightUp) + 1;

                    try {
                        while (!nothingInWayRightUp(board, square, count)) {
                            if (enemyInWayRightUp(board, square, count)) {
                                while (!nothingInWayRightUp(board, square, count - 1))
                                    count--;
                                break;
                            }
                            count--;
                            if (count == 0) {
                                rightUpImpossible = true;
                                break;
                            }
                        }
                        destination = board[square.getI() - square.getTeam() * count][square.getJ() + square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam()) {
                            moveSuccess = true;
                        }
                    } catch (Exception e) {
                        maxRightUp--;
                        if (maxRightUp == 0)
                            rightUpImpossible = true;
                    }
                    break;
                case LEFTUP: // Tries to move left and up

                    count = rand.nextInt(maxLeftUp) + 1;

                    try {

                        while (!nothingInWayLeftUp(board, square, count)) {
                            if (enemyInWayLeftUp(board, square, count)) {
                                while (!nothingInWayLeftUp(board, square, count - 1))
                                    count--;
                                break;
                            }
                            count--;
                            if (count == 0) {
                                leftUpImpossible = true;
                                break;
                            }
                        }
                        destination = board[square.getI() + square.getTeam() * count][square.getJ() + square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam()) {
                            moveSuccess = true;
                        }
                    } catch (Exception e) {
                        maxLeftUp--;
                        if (maxLeftUp == 0)
                            leftUpImpossible = true;
                    }
                    break;
                case LEFTBACK: // Tries to move left and down


                    count = rand.nextInt(maxLeftDown) + 1;

                    try {

                        while (!nothingInWayLeftDown(board, square, count)) {
                            if (enemyInWayLeftDown(board, square, count)) {
                                while (!nothingInWayLeftDown(board, square, count - 1))
                                    count--;
                                break;
                            }
                            count--;
                            if (count == 0) {
                                leftDownImpossible = true;
                                break;
                            }
                        }
                        destination = board[square.getI() + square.getTeam() * count][square.getJ() - square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam()) {
                            moveSuccess = true;
                        }
                    } catch (Exception e) {
                        maxLeftDown--;
                        if (maxLeftDown == 0)
                            leftDownImpossible = true;
                    }
                    break;
                case RIGHTBACK: // Tries to move right and down

                    count = rand.nextInt(maxRightDown) + 1;

                    try {

                        while (!nothingInWayRightDown(board, square, count)) {
                            if (enemyInWayRightDown(board, square, count)) {
                                while (!nothingInWayRightDown(board, square, count - 1))
                                    count--;
                                break;
                            }
                            count--;
                            if (count == 0) {
                                rightDownImpossible = true;
                                break;
                            }
                        }
                        destination = board[square.getI() - square.getTeam() * count][square.getJ() - square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam()) {
                            moveSuccess = true;
                        }
                    } catch (Exception e) {
                        maxRightDown--;
                        if (maxRightDown == 0)
                            rightDownImpossible = true;
                    }
                    break;
            }

        }

        return moveSuccess;
    }

    boolean canKingMove(Square[][] board, Square square, SingleGame singleGame) {
        List<KQMODE> options = new ArrayList<>();

        boolean moveSuccess = false;
        boolean bishopImpossible = false;
        boolean rookImpossible = false;

        while (!moveSuccess && (!bishopImpossible || !rookImpossible)) {
            if (!bishopImpossible)
                options.add(KQMODE.BISHOP);
            if (!rookImpossible)
                options.add(KQMODE.ROOK);

            switch (options.get(rand.nextInt(options.size()))) {
                case BISHOP:
                    moveSuccess = canBishopMove(board, square, singleGame, 1);
                    bishopImpossible = !moveSuccess;
                    break;
                case ROOK:
                    moveSuccess = canRookMove(board, square, singleGame, 1);
                    rookImpossible = !moveSuccess;
                    break;
            }
        }

        return moveSuccess;
    }

    boolean canQueenMove(Square[][] board, Square square, SingleGame singleGame) {
        List<KQMODE> options = new ArrayList<>();

        boolean moveSuccess = false;
        boolean bishopImpossible = false;
        boolean rookImpossible = false;
        while (!moveSuccess && (!bishopImpossible || !rookImpossible)) {
            if (!bishopImpossible)
                options.add(KQMODE.BISHOP);
            if (!rookImpossible)
                options.add(KQMODE.ROOK);

            switch (options.get(rand.nextInt(options.size()))) {
                case BISHOP:
                    moveSuccess = canBishopMove(board, square, singleGame);
                    bishopImpossible = !moveSuccess;
                    break;
                case ROOK:
                    moveSuccess = canRookMove(board, square, singleGame);
                    rookImpossible = !moveSuccess;
                    break;
            }
        }

        return moveSuccess;
    }
    //Single game functions end

    //Multi game functions start

    public synchronized boolean movePiece(Square[][] board, Square square, MultiGame multiGame, boolean bloodThirsty) {
        boolean moveSuccess;
        switch (square.getPiece()) {
            case Piece.PAWN:
                moveSuccess = movePawn(board, square, multiGame, bloodThirsty);
                break;
            case Piece.ROOK:
                moveSuccess = moveRook(board, square, multiGame, bloodThirsty);
                break;
            case Piece.KNIGHT:
                moveSuccess = moveKnight(board, square, multiGame, bloodThirsty);
                break;
            case Piece.BISHOP:
                moveSuccess = moveBishop(board, square, multiGame, bloodThirsty);
                break;
            case Piece.KING:
                moveSuccess = moveKing(board, square, multiGame, bloodThirsty);
                break;
            case Piece.QUEEN:
                moveSuccess = moveQueen(board, square, multiGame, bloodThirsty);
                break;
            default:
                moveSuccess = false;
                break;
        }

        return moveSuccess;
    }

    boolean movePawn(Square[][] board, Square square, MultiGame multiGame, boolean bloodThirsty) {
        List<Direction> options = new ArrayList<>();

        boolean moveSuccess = false;
        boolean forwardImpossible = false;
        boolean leftImpossible = false;
        boolean rightImpossible = false;
        destination = new Square(context, 0xffffffff);

        rand.nextInt();
        int count;
        int enemy = -square.getTeam();

        if (bloodThirsty) {
            List<Square> enemySquares = new ArrayList<>();

            try {
                Square left = board[square.getI() + square.getTeam()][square.getJ() + square.getTeam()];
                if (left.getTeam() == enemy) // left capture
                {
                    enemySquares.add(left);
                }
            } catch (Exception e) {
            }

            try {
                Square right = board[square.getI() - square.getTeam()][square.getJ() + square.getTeam()];
                if (right.getTeam() == enemy) // right capture
                {
                    enemySquares.add(right);
                }
            } catch (Exception e) {
            }

            if (!enemySquares.isEmpty()) {
                destination = enemySquares.get(rand.nextInt(enemySquares.size()));
                if (destination.getTeam() == OPPONENT)
                    multiGame.incrementYourPoints();
                else if (destination.getTeam() == YOU)
                    multiGame.incrementOpponentPoints();
                animateMove(square, destination, multiGame);
                moveSuccess = true;
            }

        }

        while (!moveSuccess && (!forwardImpossible || !leftImpossible || !rightImpossible)) {
            options.clear();

            if (!forwardImpossible)
                options.add(Direction.UP);
            if (!leftImpossible)
                options.add(Direction.LEFT);
            if (!rightImpossible)
                options.add(Direction.RIGHT);


            switch (options.get(rand.nextInt(options.size()))) {
                case UP: // tries to move forward

                    if (square.getPieceCount() == 0)
                        count = rand.nextInt(2) + 1;
                    else
                        count = 1;

                    try {

                        while (!nothingInWayForward(board, square, count)) {
                            count--;
                            if (count == 0) {
                                forwardImpossible = true;
                                break;
                            }
                        }
                        if (count > 0) {
                            destination = board[square.getI()][square.getJ() + square.getTeam() * count];
                            destination.setTeam(square.getTeam());
                            destination.setPiece(square.getPiece());
                            destination.setPieceCount(square.getPieceCount() + 1);
                            square.setPieceCount(0);
                            square.setPiece(Piece.NONE);
                            square.setTeam(NONE);
                            moveSuccess = true;
                        }
                    } catch (Exception e) {
                        forwardImpossible = true;
                    }
                    break;
                case LEFT: // tries to capture diagonally to left
                    try {
                        destination = board[square.getI() + square.getTeam()][square.getJ() + square.getTeam()];
                        if (destination.getTeam() == -square.getTeam()) {
                            if (destination.getTeam() == OPPONENT)
                                multiGame.incrementYourPoints();
                            else if (destination.getTeam() == YOU)
                                multiGame.incrementOpponentPoints();

                            animateMove(square, destination, multiGame);
                            moveSuccess = true;
                        } else {
                            leftImpossible = true;
                        }
                    } catch (Exception e) {
                        leftImpossible = true;
                    }
                    break;
                case RIGHT: // tries to capture diagonally to right
                    try {
                        destination = board[square.getI() - square.getTeam()][square.getJ() + square.getTeam()];
                        if (destination.getTeam() == -square.getTeam()) {
                            if (destination.getTeam() == OPPONENT)
                                multiGame.incrementYourPoints();
                            else if (destination.getTeam() == YOU)
                                multiGame.incrementOpponentPoints();

                            animateMove(square, destination, multiGame);
                            moveSuccess = true;
                        } else {
                            rightImpossible = true;
                        }
                    } catch (Exception e) {
                        rightImpossible = true;
                    }
                    break;
            }
        }

        if (destination.getJ() == 7 && destination.getTeam() == OPPONENT && moveSuccess) {
            destination.setPiece(Piece.QUEEN);
        } else if (destination.getJ() == 0 && destination.getTeam() == YOU && moveSuccess) {
            destination.setPiece(Piece.QUEEN);
        }

        return moveSuccess;
    }

    boolean moveRook(Square[][] board, Square square, MultiGame multiGame, int max, boolean bloodThirsty) {
        List<Direction> options = new ArrayList<>();

        boolean moveSuccess = false;
        boolean forwardImpossible = false;
        boolean leftImpossible = false;
        boolean rightImpossible = false;
        boolean backImpossible = false;
        int maxForward = max;
        int maxRight = max;
        int maxLeft = max;
        int maxBack = max;
        int count;

        rand.nextInt();

        if (bloodThirsty) {
            ArrayList<Square> enemySquares = getEnemiesOfRook(board, square);
            if (!enemySquares.isEmpty()) {
                destination = enemySquares.get(rand.nextInt(enemySquares.size()));
                if (destination.getTeam() == OPPONENT)
                    multiGame.incrementYourPoints();
                else if (destination.getTeam() == YOU)
                    multiGame.incrementOpponentPoints();
                animateMove(square, destination, multiGame);
                moveSuccess = true;
            }
        }

        while (!moveSuccess && (!forwardImpossible || !leftImpossible || !rightImpossible || !backImpossible)) {

            options.clear();

            if (!forwardImpossible)
                options.add(Direction.UP);
            if (!leftImpossible)
                options.add(Direction.LEFT);
            if (!rightImpossible)
                options.add(Direction.RIGHT);
            if (!backImpossible)
                options.add(Direction.BACK);

            switch (options.get(rand.nextInt(options.size()))) {
                case UP: // Tries to move forward

                    count = rand.nextInt(maxForward) + 1;

                    try {

                        while (!nothingInWayForward(board, square, count)) {

                            if (enemyInWayForward(board, square, count)) {
                                while (!nothingInWayForward(board, square, count - 1))
                                    count--;
                                break;
                            }
                            count--;
                            if (count == 0) {
                                forwardImpossible = true;
                                break;
                            }

                        }
                        destination = board[square.getI()][square.getJ() + square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam()) {
                            if (destination.getTeam() == OPPONENT)
                                multiGame.incrementYourPoints();
                            else if (destination.getTeam() == YOU)
                                multiGame.incrementOpponentPoints();

                            destination.setTeam(square.getTeam());
                            destination.setPiece(square.getPiece());
                            destination.setPieceCount(square.getPieceCount() + 1);
                            square.setPieceCount(0);
                            square.setTeam(NONE);
                            square.setPiece(Piece.NONE);
                            moveSuccess = true;
                        }
                    } catch (Exception e) {
                        maxForward--;
                        if (maxForward == 0)
                            forwardImpossible = true;
                    }
                    break;
                case LEFT: // Tries to move left

                    count = rand.nextInt(maxLeft) + 1;

                    try {

                        while (!nothingInWayLeft(board, square, count)) {
                            if (enemyInWayLeft(board, square, count)) {
                                while (!nothingInWayLeft(board, square, count - 1))
                                    count--;
                                break;
                            }
                            count--;

                            if (count == 0) {
                                leftImpossible = true;
                                break;
                            }

                        }
                        destination = board[square.getI() + square.getTeam() * count][square.getJ()];
                        if (count > 0 && destination.getTeam() != square.getTeam()) {
                            if (destination.getTeam() == OPPONENT)
                                multiGame.incrementYourPoints();
                            else if (destination.getTeam() == YOU)
                                multiGame.incrementOpponentPoints();

                            destination.setTeam(square.getTeam());
                            destination.setPiece(square.getPiece());
                            destination.setPieceCount(square.getPieceCount() + 1);
                            square.setPieceCount(0);
                            square.setTeam(NONE);
                            square.setPiece(Piece.NONE);
                            moveSuccess = true;
                        }
                    } catch (Exception e) {
                        maxLeft--;
                        if (maxLeft == 0)
                            leftImpossible = true;
                    }
                    break;
                case RIGHT: // Tries to move right

                    count = rand.nextInt(maxRight) + 1;

                    try {

                        while (!nothingInWayRight(board, square, count)) {
                            if (enemyInWayRight(board, square, count)) {
                                while (!nothingInWayRight(board, square, count - 1))
                                    count--;
                                break;
                            }
                            count--;

                            if (count == 0) {
                                rightImpossible = true;
                                break;
                            }

                        }
                        destination = board[square.getI() - square.getTeam() * count][square.getJ()];
                        if (count > 0 && destination.getTeam() != square.getTeam()) {
                            if (destination.getTeam() == OPPONENT)
                                multiGame.incrementYourPoints();
                            else if (destination.getTeam() == YOU)
                                multiGame.incrementOpponentPoints();

                            destination.setTeam(square.getTeam());
                            destination.setPiece(square.getPiece());
                            destination.setPieceCount(square.getPieceCount() + 1);
                            square.setPieceCount(0);
                            square.setTeam(NONE);
                            square.setPiece(Piece.NONE);
                            moveSuccess = true;
                        }
                    } catch (Exception e) {
                        maxRight--;
                        if (maxRight == 0)
                            rightImpossible = true;
                    }
                    break;
                case BACK: // Tries to move back

                    count = rand.nextInt(maxBack) + 1;

                    try {

                        while (!nothingInWayBack(board, square, count)) {
                            if (enemyInWayBack(board, square, count)) {
                                while (!nothingInWayBack(board, square, count - 1))
                                    count--;
                                break;
                            }
                            count--;

                            if (count == 0) {
                                backImpossible = true;
                                break;
                            }

                        }
                        destination = board[square.getI()][square.getJ() - square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam()) {
                            if (destination.getTeam() == OPPONENT)
                                multiGame.incrementYourPoints();
                            else if (destination.getTeam() == YOU)
                                multiGame.incrementOpponentPoints();

                            destination.setTeam(square.getTeam());
                            destination.setPiece(square.getPiece());
                            destination.setPieceCount(square.getPieceCount() + 1);
                            square.setPieceCount(0);
                            square.setTeam(NONE);
                            square.setPiece(Piece.NONE);
                            moveSuccess = true;
                        }
                    } catch (Exception e) {
                        maxBack--;
                        if (maxBack == 0)
                            backImpossible = true;
                    }
                    break;

            }
        }

        return moveSuccess;
    }

    boolean moveRook(Square[][] board, Square square, MultiGame multiGame, boolean bloodThirsty) {
        List<Direction> options = new ArrayList<>();

        boolean moveSuccess = false;
        boolean forwardImpossible = false;
        boolean leftImpossible = false;
        boolean rightImpossible = false;
        boolean backImpossible = false;
        int maxForward = 8;
        int maxRight = 8;
        int maxLeft = 8;
        int maxBack = 8;
        int count;

        rand.nextInt();

        if (bloodThirsty) {
            ArrayList<Square> enemySquares = getEnemiesOfRook(board, square);
            if (!enemySquares.isEmpty()) {
                destination = enemySquares.get(rand.nextInt(enemySquares.size()));
                if (destination.getTeam() == OPPONENT)
                    multiGame.incrementYourPoints();
                else if (destination.getTeam() == YOU)
                    multiGame.incrementOpponentPoints();
                animateMove(square, destination, multiGame);
                moveSuccess = true;
            }
        }

        while (!moveSuccess && (!forwardImpossible || !leftImpossible || !rightImpossible || !backImpossible)) {
            options.clear();


            if (!forwardImpossible)
                options.add(Direction.UP);
            if (!leftImpossible)
                options.add(Direction.LEFT);
            if (!rightImpossible)
                options.add(Direction.RIGHT);
            if (!backImpossible)
                options.add(Direction.BACK);

            switch (options.get(rand.nextInt(options.size()))) {
                case UP: // Tries to move forward

                    count = rand.nextInt(maxForward) + 1;

                    try {

                        while (!nothingInWayForward(board, square, count)) {

                            if (enemyInWayForward(board, square, count)) {
                                while (!nothingInWayForward(board, square, count - 1))
                                    count--;
                                break;
                            }
                            count--;
                            if (count == 0) {
                                forwardImpossible = true;
                                break;
                            }

                        }
                        destination = board[square.getI()][square.getJ() + square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam()) {
                            if (destination.getTeam() == OPPONENT)
                                multiGame.incrementYourPoints();
                            else if (destination.getTeam() == YOU)
                                multiGame.incrementOpponentPoints();

                            animateMove(square, destination, multiGame);
                            moveSuccess = true;
                        }
                    } catch (Exception e) {
                        maxForward--;
                        if (maxForward == 0)
                            forwardImpossible = true;
                    }
                    break;
                case LEFT: // Tries to move left

                    count = rand.nextInt(maxLeft) + 1;

                    try {

                        while (!nothingInWayLeft(board, square, count)) {
                            if (enemyInWayLeft(board, square, count)) {
                                while (!nothingInWayLeft(board, square, count - 1))
                                    count--;
                                break;
                            }
                            count--;

                            if (count == 0) {
                                leftImpossible = true;
                                break;
                            }

                        }
                        destination = board[square.getI() + square.getTeam() * count][square.getJ()];
                        if (count > 0 && destination.getTeam() != square.getTeam()) {
                            if (destination.getTeam() == OPPONENT)
                                multiGame.incrementYourPoints();
                            else if (destination.getTeam() == YOU)
                                multiGame.incrementOpponentPoints();

                            animateMove(square, destination, multiGame);
                            moveSuccess = true;
                        }
                    } catch (Exception e) {
                        maxLeft--;
                        if (maxLeft == 0)
                            leftImpossible = true;
                    }
                    break;
                case RIGHT: // Tries to move right

                    count = rand.nextInt(maxRight) + 1;

                    try {

                        while (!nothingInWayRight(board, square, count)) {
                            if (enemyInWayRight(board, square, count)) {
                                while (!nothingInWayRight(board, square, count - 1))
                                    count--;
                                break;
                            }
                            count--;

                            if (count == 0) {
                                rightImpossible = true;
                                break;
                            }

                        }
                        destination = board[square.getI() - square.getTeam() * count][square.getJ()];
                        if (count > 0 && destination.getTeam() != square.getTeam()) {
                            if (destination.getTeam() == OPPONENT)
                                multiGame.incrementYourPoints();
                            else if (destination.getTeam() == YOU)
                                multiGame.incrementOpponentPoints();

                            animateMove(square, destination, multiGame);
                            moveSuccess = true;
                        }
                    } catch (Exception e) {
                        maxRight--;
                        if (maxRight == 0)
                            rightImpossible = true;
                    }
                    break;
                case BACK: // Tries to move back

                    count = rand.nextInt(maxBack) + 1;

                    try {

                        while (!nothingInWayBack(board, square, count)) {
                            if (enemyInWayBack(board, square, count)) {
                                while (!nothingInWayBack(board, square, count - 1))
                                    count--;
                                break;
                            }
                            count--;

                            if (count == 0) {
                                backImpossible = true;
                                break;
                            }

                        }
                        destination = board[square.getI()][square.getJ() - square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam()) {
                            if (destination.getTeam() == OPPONENT)
                                multiGame.incrementYourPoints();
                            else if (destination.getTeam() == YOU)
                                multiGame.incrementOpponentPoints();

                            animateMove(square, destination, multiGame);
                            moveSuccess = true;
                        }
                    } catch (Exception e) {
                        maxBack--;
                        if (maxBack == 0)
                            backImpossible = true;
                    }
                    break;

            }
        }


        return moveSuccess;
    }

    boolean moveKnight(Square[][] board, Square square, MultiGame multiGame, boolean bloodThirsty) {
        boolean moveSuccess = false;

        rand.nextInt();
        int enemy = -square.getTeam();

        ArrayList<Square> possibleMoves = getKnightMoves(board, square);

        if (!possibleMoves.isEmpty() && bloodThirsty) {
            ArrayList<Square> enemyMoves = new ArrayList<>();
            for (Square move : possibleMoves) {
                if (move.getTeam() == enemy) {
                    enemyMoves.add(move);
                }
            }

            if (!enemyMoves.isEmpty()) {
                destination = enemyMoves.get(rand.nextInt(enemyMoves.size()));

                if (destination.getTeam() == OPPONENT)
                    multiGame.incrementYourPoints();
                else if (destination.getTeam() == YOU)
                    multiGame.incrementOpponentPoints();

                animateMove(square, destination, multiGame);
                moveSuccess = true;
            }
        }
        if (!possibleMoves.isEmpty() && !moveSuccess) {
            destination = possibleMoves.get(rand.nextInt(possibleMoves.size()));

            if (destination.getTeam() == OPPONENT)
                multiGame.incrementYourPoints();
            else if (destination.getTeam() == YOU)
                multiGame.incrementOpponentPoints();

            animateMove(square, destination, multiGame);
            moveSuccess = true;
        }

        return moveSuccess;
    }

    boolean moveBishop(Square[][] board, Square square, MultiGame multiGame, int max, boolean bloodThirsty) {
        List<Direction> options = new ArrayList<>();

        boolean moveSuccess = false;
        boolean rightUpImpossible = false;
        boolean leftUpImpossible = false;
        boolean leftDownImpossible = false;
        boolean rightDownImpossible = false;
        int maxRightUp = max;
        int maxLeftUp = max;
        int maxLeftDown = max;
        int maxRightDown = max;

        rand.nextInt();

        if (bloodThirsty) {
            ArrayList<Square> enemySquares = getEnemiesOfBishop(board, square);
            if (!enemySquares.isEmpty()) {
                destination = enemySquares.get(rand.nextInt(enemySquares.size()));
                if (destination.getTeam() == OPPONENT)
                    multiGame.incrementYourPoints();
                else if (destination.getTeam() == YOU)
                    multiGame.incrementOpponentPoints();
                animateMove(square, destination, multiGame);
                moveSuccess = true;
            }
        }

        int count;

        while (!moveSuccess && (!rightUpImpossible || !leftUpImpossible || !leftDownImpossible || !rightDownImpossible)) {

            options.clear();

            if (!rightUpImpossible)
                options.add(Direction.RIGHTUP);
            if (!leftUpImpossible)
                options.add(Direction.LEFTUP);
            if (!leftDownImpossible)
                options.add(Direction.LEFTBACK);
            if (!rightDownImpossible)
                options.add(Direction.RIGHTBACK);

            switch (options.get(rand.nextInt(options.size()))) {
                case RIGHTUP: // Tries to move right and up

                    count = rand.nextInt(maxRightUp) + 1;

                    try {
                        while (!nothingInWayRightUp(board, square, count)) {
                            if (enemyInWayRightUp(board, square, count)) {
                                while (!nothingInWayRightUp(board, square, count - 1))
                                    count--;
                                break;
                            }
                            count--;
                            if (count == 0) {
                                rightUpImpossible = true;
                                break;
                            }
                        }
                        destination = board[square.getI() - square.getTeam() * count][square.getJ() + square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam()) {
                            if (destination.getTeam() == OPPONENT)
                                multiGame.incrementYourPoints();
                            else if (destination.getTeam() == YOU)
                                multiGame.incrementOpponentPoints();

                            animateMove(square, destination, multiGame);
                            moveSuccess = true;
                        }
                    } catch (Exception e) {
                        maxRightUp--;
                        if (maxRightUp == 0)
                            rightUpImpossible = true;
                    }
                    break;
                case LEFTUP: // Tries to move left and up

                    count = rand.nextInt(maxLeftUp) + 1;

                    try {

                        while (!nothingInWayLeftUp(board, square, count)) {
                            if (enemyInWayLeftUp(board, square, count)) {
                                while (!nothingInWayLeftUp(board, square, count - 1))
                                    count--;
                                break;
                            }
                            count--;
                            if (count == 0) {
                                leftUpImpossible = true;
                                break;
                            }
                        }
                        destination = board[square.getI() + square.getTeam() * count][square.getJ() + square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam()) {
                            if (destination.getTeam() == OPPONENT)
                                multiGame.incrementYourPoints();
                            else if (destination.getTeam() == YOU)
                                multiGame.incrementOpponentPoints();

                            animateMove(square, destination, multiGame);
                            moveSuccess = true;
                        }
                    } catch (Exception e) {
                        maxLeftUp--;
                        if (maxLeftUp == 0)
                            leftUpImpossible = true;
                    }
                    break;
                case LEFTBACK: // Tries to move left and down


                    count = rand.nextInt(maxLeftDown) + 1;

                    try {

                        while (!nothingInWayLeftDown(board, square, count)) {
                            if (enemyInWayLeftDown(board, square, count)) {
                                while (!nothingInWayLeftDown(board, square, count - 1))
                                    count--;
                                break;
                            }
                            count--;
                            if (count == 0) {
                                leftDownImpossible = true;
                                break;
                            }
                        }
                        destination = board[square.getI() + square.getTeam() * count][square.getJ() - square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam()) {
                            if (destination.getTeam() == OPPONENT)
                                multiGame.incrementYourPoints();
                            else if (destination.getTeam() == YOU)
                                multiGame.incrementOpponentPoints();

                            animateMove(square, destination, multiGame);
                            moveSuccess = true;
                        }
                    } catch (Exception e) {
                        maxLeftDown--;
                        if (maxLeftDown == 0)
                            leftDownImpossible = true;
                    }
                    break;
                case RIGHTBACK: // Tries to move right and down

                    count = rand.nextInt(maxRightDown) + 1;

                    try {

                        while (!nothingInWayRightDown(board, square, count)) {
                            if (enemyInWayRightDown(board, square, count)) {
                                while (!nothingInWayRightDown(board, square, count - 1))
                                    count--;
                                break;
                            }
                            count--;
                            if (count == 0) {
                                rightDownImpossible = true;
                                break;
                            }
                        }
                        destination = board[square.getI() - square.getTeam() * count][square.getJ() - square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam()) {
                            if (destination.getTeam() == OPPONENT)
                                multiGame.incrementYourPoints();
                            else if (destination.getTeam() == YOU)
                                multiGame.incrementOpponentPoints();

                            animateMove(square, destination, multiGame);
                            moveSuccess = true;
                        }
                    } catch (Exception e) {
                        maxRightDown--;
                        if (maxRightDown == 0)
                            rightDownImpossible = true;
                    }
                    break;
            }

        }

        return moveSuccess;
    }

    boolean moveBishop(Square[][] board, Square square, MultiGame multiGame, boolean bloodThirsty) {
        List<Direction> options = new ArrayList<>();

        boolean moveSuccess = false;
        boolean rightUpImpossible = false;
        boolean leftUpImpossible = false;
        boolean leftDownImpossible = false;
        boolean rightDownImpossible = false;
        int maxRightUp = 8;
        int maxLeftUp = 8;
        int maxLeftDown = 8;
        int maxRightDown = 8;
        int count;

        rand.nextInt();

        if (bloodThirsty) {
            ArrayList<Square> enemySquares = getEnemiesOfBishop(board, square);
            if (!enemySquares.isEmpty()) {
                destination = enemySquares.get(rand.nextInt(enemySquares.size()));
                if (destination.getTeam() == OPPONENT)
                    multiGame.incrementYourPoints();
                else if (destination.getTeam() == YOU)
                    multiGame.incrementOpponentPoints();
                animateMove(square, destination, multiGame);
                moveSuccess = true;
            }
        }

        while (!moveSuccess && (!rightUpImpossible || !leftUpImpossible || !leftDownImpossible || !rightDownImpossible)) {
            options.clear();

            if (!rightUpImpossible)
                options.add(Direction.RIGHTUP);
            if (!leftUpImpossible)
                options.add(Direction.LEFTUP);
            if (!leftDownImpossible)
                options.add(Direction.LEFTBACK);
            if (!rightDownImpossible)
                options.add(Direction.RIGHTBACK);

            switch (options.get(rand.nextInt(options.size()))) {
                case RIGHTUP: // Tries to move right and up

                    count = rand.nextInt(maxRightUp) + 1;

                    try {
                        while (!nothingInWayRightUp(board, square, count)) {
                            if (enemyInWayRightUp(board, square, count)) {
                                while (!nothingInWayRightUp(board, square, count - 1))
                                    count--;
                                break;
                            }
                            count--;
                            if (count == 0) {
                                rightUpImpossible = true;
                                break;
                            }
                        }
                        destination = board[square.getI() - square.getTeam() * count][square.getJ() + square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam()) {
                            if (destination.getTeam() == OPPONENT)
                                multiGame.incrementYourPoints();
                            else if (destination.getTeam() == YOU)
                                multiGame.incrementOpponentPoints();

                            animateMove(square, destination, multiGame);
                            moveSuccess = true;
                        }
                    } catch (Exception e) {
                        maxRightUp--;
                        if (maxRightUp == 0)
                            rightUpImpossible = true;
                    }
                    break;
                case LEFTUP: // Tries to move left and up

                    count = rand.nextInt(maxLeftUp) + 1;

                    try {

                        while (!nothingInWayLeftUp(board, square, count)) {
                            if (enemyInWayLeftUp(board, square, count)) {
                                while (!nothingInWayLeftUp(board, square, count - 1))
                                    count--;
                                break;
                            }
                            count--;
                            if (count == 0) {
                                leftUpImpossible = true;
                                break;
                            }
                        }
                        destination = board[square.getI() + square.getTeam() * count][square.getJ() + square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam()) {
                            if (destination.getTeam() == OPPONENT)
                                multiGame.incrementYourPoints();
                            else if (destination.getTeam() == YOU)
                                multiGame.incrementOpponentPoints();

                            animateMove(square, destination, multiGame);
                            moveSuccess = true;
                        }
                    } catch (Exception e) {
                        maxLeftUp--;
                        if (maxLeftUp == 0)
                            leftUpImpossible = true;
                    }
                    break;
                case LEFTBACK: // Tries to move left and down


                    count = rand.nextInt(maxLeftDown) + 1;

                    try {

                        while (!nothingInWayLeftDown(board, square, count)) {
                            if (enemyInWayLeftDown(board, square, count)) {
                                while (!nothingInWayLeftDown(board, square, count - 1))
                                    count--;
                                break;
                            }
                            count--;
                            if (count == 0) {
                                leftDownImpossible = true;
                                break;
                            }
                        }
                        destination = board[square.getI() + square.getTeam() * count][square.getJ() - square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam()) {
                            if (destination.getTeam() == OPPONENT)
                                multiGame.incrementYourPoints();
                            else if (destination.getTeam() == YOU)
                                multiGame.incrementOpponentPoints();

                            animateMove(square, destination, multiGame);
                            moveSuccess = true;
                        }
                    } catch (Exception e) {
                        maxLeftDown--;
                        if (maxLeftDown == 0)
                            leftDownImpossible = true;
                    }
                    break;
                case RIGHTBACK: // Tries to move right and down

                    count = rand.nextInt(maxRightDown) + 1;

                    try {

                        while (!nothingInWayRightDown(board, square, count)) {
                            if (enemyInWayRightDown(board, square, count)) {
                                while (!nothingInWayRightDown(board, square, count - 1))
                                    count--;
                                break;
                            }
                            count--;
                            if (count == 0) {
                                rightDownImpossible = true;
                                break;
                            }
                        }
                        destination = board[square.getI() - square.getTeam() * count][square.getJ() - square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam()) {
                            if (destination.getTeam() == OPPONENT)
                                multiGame.incrementYourPoints();
                            else if (destination.getTeam() == YOU)
                                multiGame.incrementOpponentPoints();

                            animateMove(square, destination, multiGame);
                            moveSuccess = true;
                        }
                    } catch (Exception e) {
                        maxRightDown--;
                        if (maxRightDown == 0)
                            rightDownImpossible = true;
                    }
                    break;
            }

        }

        return moveSuccess;
    }

    boolean moveKing(Square[][] board, Square square, MultiGame multiGame, boolean bloodThirsty) {
        List<KQMODE> options = new ArrayList<>();

        boolean moveSuccess = false;
        boolean bishopImpossible = false;
        boolean rookImpossible = false;

        if (bloodThirsty) {
            ArrayList<Square> enemySquares = getEnemiesOfBishop(board, square, 1);
            enemySquares.addAll(getEnemiesOfRook(board, square, 1));

            if (!enemySquares.isEmpty()) {
                destination = enemySquares.get(rand.nextInt(enemySquares.size()));
                if (destination.getTeam() == OPPONENT)
                    multiGame.incrementYourPoints();
                else if (destination.getTeam() == YOU)
                    multiGame.incrementOpponentPoints();
                animateMove(square, destination, multiGame);
                moveSuccess = true;
            }
        }

        while (!moveSuccess && (!bishopImpossible || !rookImpossible)) {
            options.clear();
            if (!bishopImpossible)
                options.add(KQMODE.BISHOP);
            if (!rookImpossible)
                options.add(KQMODE.ROOK);

            switch (options.get(rand.nextInt(options.size()))) {
                case BISHOP:
                    moveSuccess = moveBishop(board, square, multiGame, 1, false);
                    bishopImpossible = !moveSuccess;
                    break;
                case ROOK:
                    moveSuccess = moveRook(board, square, multiGame, 1, false);
                    rookImpossible = !moveSuccess;
                    break;
            }
        }

        return moveSuccess;
    }

    boolean moveQueen(Square[][] board, Square square, MultiGame multiGame, boolean bloodThirsty) {
        boolean moveSuccess = false;
        boolean bishopImpossible = false;
        boolean rookImpossible = false;

        if (bloodThirsty) {
            ArrayList<Square> enemySquares = getEnemiesOfBishop(board, square);
            enemySquares.addAll(getEnemiesOfRook(board, square));

            if (!enemySquares.isEmpty()) {
                destination = enemySquares.get(rand.nextInt(enemySquares.size()));
                if (destination.getTeam() == OPPONENT)
                    multiGame.incrementYourPoints();
                else if (destination.getTeam() == YOU)
                    multiGame.incrementOpponentPoints();
                animateMove(square, destination, multiGame);
                moveSuccess = true;
            }
        }

        while (!moveSuccess && (!bishopImpossible || !rookImpossible)) {
            if (rand.nextInt(2) == 0 && !bishopImpossible) {
                moveSuccess = moveBishop(board, square, multiGame, false);
                bishopImpossible = !moveSuccess;
            } else if (!rookImpossible) {
                moveSuccess = moveRook(board, square, multiGame, false);
                rookImpossible = !moveSuccess;
            }
        }

        return moveSuccess;
    }

    public synchronized boolean canPieceMove(Square[][] board, Square square, MultiGame multiGame) {
        boolean moveSuccess;
        switch (square.getPiece()) {
            case Piece.PAWN:
                moveSuccess = canPawnMove(board, square, multiGame);
                break;
            case Piece.ROOK:
                moveSuccess = canRookMove(board, square, multiGame);
                break;
            case Piece.KNIGHT:
                moveSuccess = canKnightMove(board, square, multiGame);
                break;
            case Piece.BISHOP:
                moveSuccess = canBishopMove(board, square, multiGame);
                break;
            case Piece.KING:
                moveSuccess = canKingMove(board, square, multiGame);
                break;
            case Piece.QUEEN:
                moveSuccess = canQueenMove(board, square, multiGame);
                break;
            default:
                moveSuccess = false;
                break;
        }

        return moveSuccess;
    }

    boolean canPawnMove(Square[][] board, Square square, MultiGame multiGame) {
        List<Direction> options = new ArrayList<>();

        boolean moveSuccess = false;
        boolean forwardImpossible = false;
        boolean leftImpossible = false;
        boolean rightImpossible = false;
        int count;

        rand.nextInt();

        while (!moveSuccess && (!forwardImpossible || !leftImpossible || !rightImpossible)) {
            options.clear();

            if (!forwardImpossible)
                options.add(Direction.UP);
            if (!leftImpossible)
                options.add(Direction.LEFT);
            if (!rightImpossible)
                options.add(Direction.RIGHT);

            switch (options.get(rand.nextInt(options.size()))) {
                case UP: // tries to move forward


                    if (square.getPieceCount() == 0)
                        count = rand.nextInt(2) + 1;
                    else
                        count = 1;

                    try {

                        while (!nothingInWayForward(board, square, count)) {
                            count--;
                            if (count == 0) {
                                forwardImpossible = true;
                                break;
                            }
                        }
                        if (count > 0) {
                            moveSuccess = true;
                        }
                    } catch (Exception e) {
                        forwardImpossible = true;
                    }
                    break;
                case LEFT: // tries to capture diagonally to left
                    try {
                        destination = board[square.getI() + square.getTeam()][square.getJ() + square.getTeam()];
                        if (destination.getTeam() == -square.getTeam()) {
                            moveSuccess = true;
                        } else {
                            leftImpossible = true;
                        }
                    } catch (Exception e) {
                        leftImpossible = true;
                    }
                    break;
                case RIGHT: // tries to capture diagonally to right
                    try {
                        destination = board[square.getI() - square.getTeam()][square.getJ() + square.getTeam()];
                        if (destination.getTeam() == -square.getTeam()) {
                            moveSuccess = true;
                        } else {
                            rightImpossible = true;
                        }
                    } catch (Exception e) {
                        rightImpossible = true;
                    }
                    break;
            }

        }

        return moveSuccess;
    }

    boolean canRookMove(Square[][] board, Square square, MultiGame multiGame, int max) {
        List<Direction> options = new ArrayList<>();

        boolean moveSuccess = false;
        boolean forwardImpossible = false;
        boolean leftImpossible = false;
        boolean rightImpossible = false;
        boolean backImpossible = false;
        int maxForward = max;
        int maxRight = max;
        int maxLeft = max;
        int maxBack = max;
        int count;


        rand.nextInt();

        while (!moveSuccess && (!forwardImpossible || !leftImpossible || !rightImpossible || !backImpossible)) {
            options.clear();


            if (!forwardImpossible)
                options.add(Direction.UP);
            if (!leftImpossible)
                options.add(Direction.LEFT);
            if (!rightImpossible)
                options.add(Direction.RIGHT);
            if (!backImpossible)
                options.add(Direction.BACK);

            switch (options.get(rand.nextInt(options.size()))) {
                case UP: // Tries to move forward

                    count = rand.nextInt(maxForward) + 1;

                    try {

                        while (!nothingInWayForward(board, square, count)) {

                            if (enemyInWayForward(board, square, count)) {
                                while (!nothingInWayForward(board, square, count - 1))
                                    count--;
                                break;
                            }
                            count--;
                            if (count == 0) {
                                forwardImpossible = true;
                                break;
                            }

                        }
                        destination = board[square.getI()][square.getJ() + square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam()) {
                            moveSuccess = true;
                        }
                    } catch (Exception e) {
                        maxForward--;
                        if (maxForward == 0)
                            forwardImpossible = true;
                    }
                    break;
                case LEFT: // Tries to move left

                    count = rand.nextInt(maxLeft) + 1;

                    try {

                        while (!nothingInWayLeft(board, square, count)) {
                            if (enemyInWayLeft(board, square, count)) {
                                while (!nothingInWayLeft(board, square, count - 1))
                                    count--;
                                break;
                            }
                            count--;

                            if (count == 0) {
                                leftImpossible = true;
                                break;
                            }

                        }
                        destination = board[square.getI() + square.getTeam() * count][square.getJ()];
                        if (count > 0 && destination.getTeam() != square.getTeam()) {
                            moveSuccess = true;
                        }
                    } catch (Exception e) {
                        maxLeft--;
                        if (maxLeft == 0)
                            leftImpossible = true;
                    }
                    break;
                case RIGHT: // Tries to move right

                    count = rand.nextInt(maxRight) + 1;

                    try {

                        while (!nothingInWayRight(board, square, count)) {
                            if (enemyInWayRight(board, square, count)) {
                                while (!nothingInWayRight(board, square, count - 1))
                                    count--;
                                break;
                            }
                            count--;

                            if (count == 0) {
                                rightImpossible = true;
                                break;
                            }

                        }
                        destination = board[square.getI() - square.getTeam() * count][square.getJ()];
                        if (count > 0 && destination.getTeam() != square.getTeam()) {
                            moveSuccess = true;
                        }
                    } catch (Exception e) {
                        maxRight--;
                        if (maxRight == 0)
                            rightImpossible = true;
                    }
                    break;
                case BACK: // Tries to move back

                    count = rand.nextInt(maxBack) + 1;

                    try {

                        while (!nothingInWayBack(board, square, count)) {
                            if (enemyInWayBack(board, square, count)) {
                                while (!nothingInWayBack(board, square, count - 1))
                                    count--;
                                break;
                            }
                            count--;

                            if (count == 0) {
                                backImpossible = true;
                                break;
                            }

                        }
                        destination = board[square.getI()][square.getJ() - square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam()) {
                            moveSuccess = true;
                        }
                    } catch (Exception e) {
                        maxBack--;
                        if (maxBack == 0)
                            backImpossible = true;
                    }
                    break;

            }
        }

        return moveSuccess;
    }

    boolean canRookMove(Square[][] board, Square square, MultiGame multiGame) {
        List<Direction> options = new ArrayList<>();

        boolean moveSuccess = false;
        boolean forwardImpossible = false;
        boolean leftImpossible = false;
        boolean rightImpossible = false;
        boolean backImpossible = false;
        int maxForward = 8;
        int maxRight = 8;
        int maxLeft = 8;
        int maxBack = 8;
        int count;


        rand.nextInt();

        while (!moveSuccess && (!forwardImpossible || !leftImpossible || !rightImpossible || !backImpossible)) {
            options.clear();


            if (!forwardImpossible)
                options.add(Direction.UP);
            if (!leftImpossible)
                options.add(Direction.LEFT);
            if (!rightImpossible)
                options.add(Direction.RIGHT);
            if (!backImpossible)
                options.add(Direction.BACK);

            switch (options.get(rand.nextInt(options.size()))) {
                case UP: // Tries to move forward

                    count = rand.nextInt(maxForward) + 1;

                    try {

                        while (!nothingInWayForward(board, square, count)) {

                            if (enemyInWayForward(board, square, count)) {
                                while (!nothingInWayForward(board, square, count - 1))
                                    count--;
                                break;
                            }
                            count--;
                            if (count == 0) {
                                forwardImpossible = true;
                                break;
                            }

                        }
                        destination = board[square.getI()][square.getJ() + square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam()) {

                            moveSuccess = true;
                        }
                    } catch (Exception e) {
                        maxForward--;
                        if (maxForward == 0)
                            forwardImpossible = true;
                    }
                    break;
                case LEFT: // Tries to move left

                    count = rand.nextInt(maxLeft) + 1;

                    try {

                        while (!nothingInWayLeft(board, square, count)) {
                            if (enemyInWayLeft(board, square, count)) {
                                while (!nothingInWayLeft(board, square, count - 1))
                                    count--;
                                break;
                            }
                            count--;

                            if (count == 0) {
                                leftImpossible = true;
                                break;
                            }

                        }
                        destination = board[square.getI() + square.getTeam() * count][square.getJ()];
                        if (count > 0 && destination.getTeam() != square.getTeam()) {

                            moveSuccess = true;
                        }
                    } catch (Exception e) {
                        maxLeft--;
                        if (maxLeft == 0)
                            leftImpossible = true;
                    }
                    break;
                case RIGHT: // Tries to move right

                    count = rand.nextInt(maxRight) + 1;

                    try {

                        while (!nothingInWayRight(board, square, count)) {
                            if (enemyInWayRight(board, square, count)) {
                                while (!nothingInWayRight(board, square, count - 1))
                                    count--;
                                break;
                            }
                            count--;

                            if (count == 0) {
                                rightImpossible = true;
                                break;
                            }

                        }
                        destination = board[square.getI() - square.getTeam() * count][square.getJ()];
                        if (count > 0 && destination.getTeam() != square.getTeam()) {

                            moveSuccess = true;
                        }
                    } catch (Exception e) {
                        maxRight--;
                        if (maxRight == 0)
                            rightImpossible = true;
                    }
                    break;
                case BACK: // Tries to move back

                    count = rand.nextInt(maxBack) + 1;

                    try {

                        while (!nothingInWayBack(board, square, count)) {
                            if (enemyInWayBack(board, square, count)) {
                                while (!nothingInWayBack(board, square, count - 1))
                                    count--;
                                break;
                            }
                            count--;

                            if (count == 0) {
                                backImpossible = true;
                                break;
                            }

                        }
                        destination = board[square.getI()][square.getJ() - square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam()) {
                            moveSuccess = true;
                        }
                    } catch (Exception e) {
                        maxBack--;
                        if (maxBack == 0)
                            backImpossible = true;
                    }
                    break;

            }
        }


        return moveSuccess;
    }

    boolean canKnightMove(Square[][] board, Square square, MultiGame multiGame) {
        List<Direction> options = new ArrayList<>();

        boolean moveSuccess = false;
        boolean _2R1U, _1R2U, _1L2U, _2L1U, _2L1D, _1L2D, _1R2D, _2R1D; // true means move is impossible
        _2R1U = _1R2U = _1L2U = _2L1U = _2L1D = _1L2D = _1R2D = _2R1D = false;


        rand.nextInt();

        while (!moveSuccess && (!_2R1U || !_1R2U || !_1L2U || !_2L1U || !_2L1D || !_1L2D || !_1R2D || !_2R1D)) {
            options.clear();

            if (!_2R1U)
                options.add(Direction._2R1U);
            if (!_1R2U)
                options.add(Direction._1R2U);
            if (!_1L2U)
                options.add(Direction._1L2U);
            if (!_2L1U)
                options.add(Direction._2L1U);
            if (!_2L1D)
                options.add(Direction._2L1D);
            if (!_1L2D)
                options.add(Direction._1L2D);
            if (!_1R2D)
                options.add(Direction._1R2D);
            if (!_2R1D)
                options.add(Direction._2R1D);


            switch (options.get(rand.nextInt(options.size()))) {
                case _2R1U:
                    try {
                        destination = board[square.getI() - square.getTeam() * 2][square.getJ() + square.getTeam()];
                        if (destination.getTeam() != square.getTeam()) {
                            moveSuccess = true;
                        } else {
                            _2R1U = true;
                        }
                    } catch (Exception e) {
                        _2R1U = true;
                    }
                    break;
                case _1R2U:
                    try {
                        destination = board[square.getI() - square.getTeam()][square.getJ() + square.getTeam() * 2];
                        if (destination.getTeam() != square.getTeam()) {
                            moveSuccess = true;
                        } else {
                            _1R2U = true;
                        }
                    } catch (Exception e) {
                        _1R2U = true;
                    }
                    break;
                case _1L2U:
                    try {
                        destination = board[square.getI() + square.getTeam()][square.getJ() + square.getTeam() * 2];
                        if (destination.getTeam() != square.getTeam()) {
                            moveSuccess = true;
                        } else {
                            _1L2U = true;
                        }
                    } catch (Exception e) {
                        _1L2U = true;
                    }
                    break;
                case _2L1U:
                    try {
                        destination = board[square.getI() + square.getTeam() * 2][square.getJ() + square.getTeam()];
                        if (destination.getTeam() != square.getTeam()) {
                            moveSuccess = true;
                        } else {
                            _2L1U = true;
                        }
                    } catch (Exception e) {
                        _2L1U = true;
                    }
                    break;
                case _2L1D:
                    try {
                        destination = board[square.getI() + square.getTeam() * 2][square.getJ() - square.getTeam()];
                        if (destination.getTeam() != square.getTeam()) {
                            moveSuccess = true;
                        } else {
                            _2L1D = true;
                        }
                    } catch (Exception e) {
                        _2L1D = true;
                    }
                    break;
                case _1L2D:
                    try {
                        destination = board[square.getI() + square.getTeam()][square.getJ() - square.getTeam() * 2];
                        if (destination.getTeam() != square.getTeam()) {
                            moveSuccess = true;
                        } else {
                            _1L2D = true;
                        }
                    } catch (Exception e) {
                        _1L2D = true;
                    }
                    break;
                case _1R2D:
                    try {
                        destination = board[square.getI() - square.getTeam()][square.getJ() - square.getTeam() * 2];
                        if (destination.getTeam() != square.getTeam()) {
                            moveSuccess = true;
                        } else {
                            _1R2D = true;
                        }
                    } catch (Exception e) {
                        _1R2D = true;
                    }
                    break;
                case _2R1D:
                    try {
                        destination = board[square.getI() - square.getTeam() * 2][square.getJ() - square.getTeam()];
                        if (destination.getTeam() != square.getTeam()) {
                            moveSuccess = true;
                        } else {
                            _2R1D = true;
                        }
                    } catch (Exception e) {
                        _2R1D = true;
                    }
                    break;

            }


        }


        return moveSuccess;
    }

    boolean canBishopMove(Square[][] board, Square square, MultiGame multiGame, int max) {
        boolean moveSuccess = false;
        boolean rightUpImpossible = false;
        boolean leftUpImpossible = false;
        boolean leftDownImpossible = false;
        boolean rightDownImpossible = false;
        int maxRightUp = max;
        int maxLeftUp = max;
        int maxLeftDown = max;
        int maxRightDown = max;


        rand.nextInt();

        while (!moveSuccess && (!rightUpImpossible || !leftUpImpossible || !leftDownImpossible || !rightDownImpossible)) {
            List<Direction> options = new ArrayList<>();
            int count;

            if (!rightUpImpossible)
                options.add(Direction.RIGHTUP);
            if (!leftUpImpossible)
                options.add(Direction.LEFTUP);
            if (!leftDownImpossible)
                options.add(Direction.LEFTBACK);
            if (!rightDownImpossible)
                options.add(Direction.RIGHTBACK);

            switch (options.get(rand.nextInt(options.size()))) {
                case RIGHTUP: // Tries to move right and up

                    count = rand.nextInt(maxRightUp) + 1;

                    try {
                        while (!nothingInWayRightUp(board, square, count)) {
                            if (enemyInWayRightUp(board, square, count)) {
                                while (!nothingInWayRightUp(board, square, count - 1))
                                    count--;
                                break;
                            }
                            count--;
                            if (count == 0) {
                                rightUpImpossible = true;
                                break;
                            }
                        }
                        destination = board[square.getI() - square.getTeam() * count][square.getJ() + square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam()) {
                            moveSuccess = true;
                        }
                    } catch (Exception e) {
                        maxRightUp--;
                        if (maxRightUp == 0)
                            rightUpImpossible = true;
                    }
                    break;
                case LEFTUP: // Tries to move left and up

                    count = rand.nextInt(maxLeftUp) + 1;

                    try {

                        while (!nothingInWayLeftUp(board, square, count)) {
                            if (enemyInWayLeftUp(board, square, count)) {
                                while (!nothingInWayLeftUp(board, square, count - 1))
                                    count--;
                                break;
                            }
                            count--;
                            if (count == 0) {
                                leftUpImpossible = true;
                                break;
                            }
                        }
                        destination = board[square.getI() + square.getTeam() * count][square.getJ() + square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam()) {
                            moveSuccess = true;
                        }
                    } catch (Exception e) {
                        maxLeftUp--;
                        if (maxLeftUp == 0)
                            leftUpImpossible = true;
                    }
                    break;
                case LEFTBACK: // Tries to move left and down


                    count = rand.nextInt(maxLeftDown) + 1;

                    try {

                        while (!nothingInWayLeftDown(board, square, count)) {
                            if (enemyInWayLeftDown(board, square, count)) {
                                while (!nothingInWayLeftDown(board, square, count - 1))
                                    count--;
                                break;
                            }
                            count--;
                            if (count == 0) {
                                leftDownImpossible = true;
                                break;
                            }
                        }
                        destination = board[square.getI() + square.getTeam() * count][square.getJ() - square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam()) {
                            moveSuccess = true;
                        }
                    } catch (Exception e) {
                        maxLeftDown--;
                        if (maxLeftDown == 0)
                            leftDownImpossible = true;
                    }
                    break;
                case RIGHTBACK: // Tries to move right and down

                    count = rand.nextInt(maxRightDown) + 1;

                    try {

                        while (!nothingInWayRightDown(board, square, count)) {
                            if (enemyInWayRightDown(board, square, count)) {
                                while (!nothingInWayRightDown(board, square, count - 1))
                                    count--;
                                break;
                            }
                            count--;
                            if (count == 0) {
                                rightDownImpossible = true;
                                break;
                            }
                        }
                        destination = board[square.getI() - square.getTeam() * count][square.getJ() - square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam()) {
                            moveSuccess = true;
                        }
                    } catch (Exception e) {
                        maxRightDown--;
                        if (maxRightDown == 0)
                            rightDownImpossible = true;
                    }
                    break;
            }

        }

        return moveSuccess;
    }

    boolean canBishopMove(Square[][] board, Square square, MultiGame multiGame) {
        List<Direction> options = new ArrayList<>();

        boolean moveSuccess = false;
        boolean rightUpImpossible = false;
        boolean leftUpImpossible = false;
        boolean leftDownImpossible = false;
        boolean rightDownImpossible = false;
        int maxRightUp = 8;
        int maxLeftUp = 8;
        int maxLeftDown = 8;
        int maxRightDown = 8;
        int count;


        rand.nextInt();

        while (!moveSuccess && (!rightUpImpossible || !leftUpImpossible || !leftDownImpossible || !rightDownImpossible)) {
            options.clear();


            if (!rightUpImpossible)
                options.add(Direction.RIGHTUP);
            if (!leftUpImpossible)
                options.add(Direction.LEFTUP);
            if (!leftDownImpossible)
                options.add(Direction.LEFTBACK);
            if (!rightDownImpossible)
                options.add(Direction.RIGHTBACK);

            switch (options.get(rand.nextInt(options.size()))) {
                case RIGHTUP: // Tries to move right and up

                    count = rand.nextInt(maxRightUp) + 1;

                    try {
                        while (!nothingInWayRightUp(board, square, count)) {
                            if (enemyInWayRightUp(board, square, count)) {
                                while (!nothingInWayRightUp(board, square, count - 1))
                                    count--;
                                break;
                            }
                            count--;
                            if (count == 0) {
                                rightUpImpossible = true;
                                break;
                            }
                        }
                        destination = board[square.getI() - square.getTeam() * count][square.getJ() + square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam()) {
                            moveSuccess = true;
                        }
                    } catch (Exception e) {
                        maxRightUp--;
                        if (maxRightUp == 0)
                            rightUpImpossible = true;
                    }
                    break;
                case LEFTUP: // Tries to move left and up

                    count = rand.nextInt(maxLeftUp) + 1;

                    try {

                        while (!nothingInWayLeftUp(board, square, count)) {
                            if (enemyInWayLeftUp(board, square, count)) {
                                while (!nothingInWayLeftUp(board, square, count - 1))
                                    count--;
                                break;
                            }
                            count--;
                            if (count == 0) {
                                leftUpImpossible = true;
                                break;
                            }
                        }
                        destination = board[square.getI() + square.getTeam() * count][square.getJ() + square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam()) {
                            moveSuccess = true;
                        }
                    } catch (Exception e) {
                        maxLeftUp--;
                        if (maxLeftUp == 0)
                            leftUpImpossible = true;
                    }
                    break;
                case LEFTBACK: // Tries to move left and down


                    count = rand.nextInt(maxLeftDown) + 1;

                    try {

                        while (!nothingInWayLeftDown(board, square, count)) {
                            if (enemyInWayLeftDown(board, square, count)) {
                                while (!nothingInWayLeftDown(board, square, count - 1))
                                    count--;
                                break;
                            }
                            count--;
                            if (count == 0) {
                                leftDownImpossible = true;
                                break;
                            }
                        }
                        destination = board[square.getI() + square.getTeam() * count][square.getJ() - square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam()) {
                            moveSuccess = true;
                        }
                    } catch (Exception e) {
                        maxLeftDown--;
                        if (maxLeftDown == 0)
                            leftDownImpossible = true;
                    }
                    break;
                case RIGHTBACK: // Tries to move right and down

                    count = rand.nextInt(maxRightDown) + 1;

                    try {

                        while (!nothingInWayRightDown(board, square, count)) {
                            if (enemyInWayRightDown(board, square, count)) {
                                while (!nothingInWayRightDown(board, square, count - 1))
                                    count--;
                                break;
                            }
                            count--;
                            if (count == 0) {
                                rightDownImpossible = true;
                                break;
                            }
                        }
                        destination = board[square.getI() - square.getTeam() * count][square.getJ() - square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam()) {
                            moveSuccess = true;
                        }
                    } catch (Exception e) {
                        maxRightDown--;
                        if (maxRightDown == 0)
                            rightDownImpossible = true;
                    }
                    break;
            }

        }

        return moveSuccess;
    }

    boolean canKingMove(Square[][] board, Square square, MultiGame multiGame) {
        List<KQMODE> options = new ArrayList<>();

        boolean moveSuccess = false;
        boolean bishopImpossible = false;
        boolean rookImpossible = false;

        while (!moveSuccess && (!bishopImpossible || !rookImpossible)) {
            if (!bishopImpossible)
                options.add(KQMODE.BISHOP);
            if (!rookImpossible)
                options.add(KQMODE.ROOK);

            switch (options.get(rand.nextInt(options.size()))) {
                case BISHOP:
                    moveSuccess = canBishopMove(board, square, multiGame, 1);
                    bishopImpossible = !moveSuccess;
                    break;
                case ROOK:
                    moveSuccess = canRookMove(board, square, multiGame, 1);
                    rookImpossible = !moveSuccess;
                    break;
            }
        }

        return moveSuccess;
    }

    boolean canQueenMove(Square[][] board, Square square, MultiGame multiGame) {
        List<KQMODE> options = new ArrayList<>();

        boolean moveSuccess = false;
        boolean bishopImpossible = false;
        boolean rookImpossible = false;
        while (!moveSuccess && (!bishopImpossible || !rookImpossible)) {
            if (!bishopImpossible)
                options.add(KQMODE.BISHOP);
            if (!rookImpossible)
                options.add(KQMODE.ROOK);

            switch (options.get(rand.nextInt(options.size()))) {
                case BISHOP:
                    moveSuccess = canBishopMove(board, square, multiGame);
                    bishopImpossible = !moveSuccess;
                    break;
                case ROOK:
                    moveSuccess = canRookMove(board, square, multiGame);
                    rookImpossible = !moveSuccess;
                    break;
            }
        }

        return moveSuccess;
    }

    //Multi game functions end

    boolean nothingInWayForward(Square[][] board, Square square, int count) {
        boolean clear = true;

        try {
            for (int j = 1; j <= count; j++) {
                clear = board[square.getI()][square.getJ() + square.getTeam() * j].getPiece() == Piece.NONE;
                if (!clear)
                    break;
            }
        }
        catch (IndexOutOfBoundsException e) {
            clear = false;
        }

        return clear;
    }

    boolean nothingInWayLeft(Square[][] board, Square square, int count) {
        boolean clear = true;

        try {
            for (int j = 1; j <= count; j++) {
                clear = board[square.getI() + square.getTeam() * j][square.getJ()].getPiece() == Piece.NONE;
                if (!clear)
                    break;
            }
        }
        catch (IndexOutOfBoundsException e) {
            clear = false;
        }

        return clear;
    }

    boolean nothingInWayRight(Square[][] board, Square square, int count) {
        boolean clear = true;

        try {
            for (int j = 1; j <= count; j++) {
                clear = board[square.getI() - square.getTeam() * j][square.getJ()].getPiece() == Piece.NONE;
                if (!clear)
                    break;
            }
        }
        catch (IndexOutOfBoundsException e) {
            clear = false;
        }

        return clear;
    }

    boolean nothingInWayBack(Square[][] board, Square square, int count) {
        boolean clear = true;

        try {
            for (int j = 1; j <= count; j++) {
                clear = board[square.getI()][square.getJ() - square.getTeam() * j].getPiece() == Piece.NONE;
                if (!clear)
                    break;
            }
        }
        catch (IndexOutOfBoundsException e) {
            clear = false;
        }

        return clear;
    }

    boolean nothingInWayRightUp(Square[][] board, Square square, int count) {
        boolean clear = true;

        try {
            for (int j = 1; j <= count; j++) {
                destination = board[square.getI() - square.getTeam() * j][square.getJ() + square.getTeam() * j];
                clear = destination.getPiece() == Piece.NONE;
                if (!clear)
                    break;
            }
        }
        catch (IndexOutOfBoundsException e) {
            clear = false;
        }

        return clear;
    }

    boolean nothingInWayLeftUp(Square[][] board, Square square, int count) {
        boolean clear = true;

        try {
            for (int j = 1; j <= count; j++) {
                destination = board[square.getI() + square.getTeam() * j][square.getJ() + square.getTeam() * j];
                clear = destination.getPiece() == Piece.NONE;
                if (!clear)
                    break;
            }
        }
        catch (IndexOutOfBoundsException e) {
            clear = false;
        }

        return clear;
    }

    boolean nothingInWayLeftDown(Square[][] board, Square square, int count) {
        boolean clear = true;

        try {
            for (int j = 1; j <= count; j++) {
                destination = board[square.getI() + square.getTeam() * j][square.getJ() - square.getTeam() * j];
                clear = destination.getPiece() == Piece.NONE;
                if (!clear)
                    break;
            }
        }
        catch (IndexOutOfBoundsException e) {
            clear = false;
        }

        return clear;
    }

    boolean nothingInWayRightDown(Square[][] board, Square square, int count) {
        boolean clear = true;

        try {
            for (int j = 1; j <= count; j++) {
                destination = board[square.getI() - square.getTeam() * j][square.getJ() - square.getTeam() * j];
                clear = destination.getPiece() == Piece.NONE;
                if (!clear)
                    break;
            }
        }
        catch (IndexOutOfBoundsException e) {
            clear = false;
        }

        return clear;
    }

    boolean enemyInWayForward(Square[][] board, Square square, int count) {
        boolean enemy = false;

        try {
            for (int j = 1; j <= count; j++) {
                destination = board[square.getI()][square.getJ() + square.getTeam() * j];
                enemy = destination.getTeam() == -square.getTeam();
                if (enemy)
                    break;
            }
        }
        catch (IndexOutOfBoundsException e) {
        }

        return enemy;
    }

    boolean enemyInWayLeft(Square[][] board, Square square, int count) {
        boolean enemy = false;

        try {
            for (int j = 1; j <= count; j++) {
                destination = board[square.getI() + square.getTeam() * j][square.getJ()];
                enemy = destination.getTeam() == -square.getTeam();
                if (enemy)
                    break;
            }
        }
        catch (IndexOutOfBoundsException e) {
        }

        return enemy;
    }

    boolean enemyInWayRight(Square[][] board, Square square, int count) {
        boolean enemy = false;

        try {
            for (int j = 1; j <= count; j++) {
                destination = board[square.getI() - square.getTeam() * j][square.getJ()];
                enemy = destination.getTeam() == -square.getTeam();
                if (enemy)
                    break;
            }
        }
        catch (IndexOutOfBoundsException e) {
        }

        return enemy;
    }

    boolean enemyInWayBack(Square[][] board, Square square, int count) {
        boolean enemy = false;

        try {
            for (int j = 1; j <= count; j++) {
                destination = board[square.getI()][square.getJ() - square.getTeam() * j];
                enemy = destination.getTeam() == -square.getTeam();
                if (enemy)
                    break;
            }
        }
        catch (IndexOutOfBoundsException e) {
        }


        return enemy;
    }

    boolean enemyInWayRightUp(Square[][] board, Square square, int count) {
        boolean enemy = false;

        try {
            for (int j = 1; j <= count; j++) {
                destination = board[square.getI() - square.getTeam() * j][square.getJ() + square.getTeam() * j];
                enemy = destination.getTeam() == -square.getTeam();
                if (enemy)
                    break;
            }
        }
        catch (IndexOutOfBoundsException e) {
        }

        return enemy;
    }

    boolean enemyInWayLeftUp(Square[][] board, Square square, int count) {
        boolean enemy = false;

        try {
            for (int j = 1; j <= count; j++) {
                destination = board[square.getI() + square.getTeam() * j][square.getJ() + square.getTeam() * j];
                enemy = destination.getTeam() == -square.getTeam();
                if (enemy)
                    break;
            }
        }
        catch (IndexOutOfBoundsException e) {
        }

        return enemy;
    }

    boolean enemyInWayLeftDown(Square[][] board, Square square, int count) {
        boolean enemy = false;

        try {
            for (int j = 1; j <= count; j++) {
                destination = board[square.getI() + square.getTeam() * j][square.getJ() - square.getTeam() * j];
                enemy = destination.getTeam() == -square.getTeam();
                if (enemy)
                    break;
            }
        }
        catch (IndexOutOfBoundsException e) {
        }

        return enemy;
    }

    boolean enemyInWayRightDown(Square[][] board, Square square, int count) {
        boolean enemy = false;

        try {
            for (int j = 1; j <= count; j++) {
                destination = board[square.getI() - square.getTeam() * j][square.getJ() - square.getTeam() * j];
                enemy = destination.getTeam() == -square.getTeam();
                if (enemy)
                    break;
            }
        }
        catch (IndexOutOfBoundsException e) {
        }

        return enemy;
    }

    private int convertDpToPx(int dp) {
        return Math.round(dp * (context.getResources().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

}
    

