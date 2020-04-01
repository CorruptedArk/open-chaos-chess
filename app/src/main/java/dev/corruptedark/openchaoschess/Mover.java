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

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Mover {
    public final int YOU = -1;
    public final int OPPONENT = 1;
    public final int NONE = 0;

    private enum Direction {UP, BACK, LEFT, RIGHT, LEFTUP, RIGHTUP, LEFTBACK, RIGHTBACK, _2R1U, _1R2U, _1L2U, _2L1U, _2L1D, _1L2D, _1R2D, _2R1D}
    private enum KQMODE {ROOK, BISHOP}

    Random rand = new Random();
    Context context;

    private Square destination;

    Mover(Context context) {
        this.context = context;
    }

    public synchronized Square getLastDestination()
    {
        return destination;
    }

    //Single game functions start
    public synchronized boolean movePiece(Square[][] board, Square square, SingleGame singleGame)
    {
        boolean moveSuccess;
        switch(square.getPiece())
        {
            case "P":
                moveSuccess = movePawn(board, square, singleGame);
                break;
            case "R":
                moveSuccess = moveRook(board, square, singleGame);
                break;
            case "Kn":
                moveSuccess = moveKnight( board,  square, singleGame);
                break;
            case "B":
                moveSuccess = moveBishop( board,  square, singleGame);
                break;
            case "Ki":
                moveSuccess = moveKing( board,  square, singleGame);
                break;
            case "Q":
                moveSuccess = moveQueen( board,  square, singleGame);
                break;
            default:
                moveSuccess = false;
                break;
        }

        return moveSuccess;
    }

    boolean movePawn( Square[][] board,  Square square,  SingleGame singleGame)
    {
        List<Direction> options = new ArrayList<>();


        boolean moveSuccess = false;
        boolean forwardImpossible = false;
        boolean leftImpossible = false;
        boolean rightImpossible = false;
        destination = new Square(context,0xffffffff);

        rand.nextInt();
        int count;

        while (!moveSuccess && (!forwardImpossible || !leftImpossible || !rightImpossible))
        {
            options.clear();

            if (!forwardImpossible)
                options.add(Direction.UP);
            if (!leftImpossible)
                options.add(Direction.LEFT);
            if (!rightImpossible)
                options.add(Direction.RIGHT);


            switch(options.get(rand.nextInt(options.size())))
            {
                case UP: // tries to move forward

                    if (square.getPieceCount() == 0)
                        count = rand.nextInt(2)+1;
                    else
                        count = 1;

                    try
                    {

                        while(!nothingInWayForward( board,  square, count))
                        {
                            count--;
                            if(count == 0)
                            {
                                forwardImpossible = true;
                                break;
                            }
                        }
                        if(count > 0)
                        {
                            destination = board[square.getI()][square.getJ() + square.getTeam() * count];
                            destination.setTeam(square.getTeam());
                            destination.setPiece(square.getPiece());
                            destination.setPieceCount(square.getPieceCount() + 1);
                            square.setPieceCount(0);
                            square.setPiece(" ");
                            square.setTeam(NONE);
                            moveSuccess = true;
                        }
                    }
                    catch (Exception e)
                {
                    forwardImpossible = true;
                }
                break;
                case LEFT: // tries to capture diagonally to left
                    try
                    {
                        destination = board[square.getI() + square.getTeam()][ square.getJ() + square.getTeam()];
                        if (destination.getTeam() == -square.getTeam())
                        {
                            if (destination.getTeam() == OPPONENT)
                                singleGame.incrementPlayerPoints();
                            else if (destination.getTeam() == YOU)
                                singleGame.incrementComputerPoints();

                            destination.setTeam(square.getTeam());
                            destination.setPiece(square.getPiece());
                            destination.setPieceCount(square.getPieceCount()+ 1);
                            square.setPieceCount(0);
                            square.setTeam(NONE);
                            square.setPiece(" ");
                            moveSuccess = true;
                        }
                        else
                        {
                            leftImpossible = true;
                        }
                    }
                    catch (Exception e)
                {
                    leftImpossible = true;
                }
                break;
                case RIGHT: // tries to capture diagonally to right
                    try
                    {
                        destination = board[square.getI() - square.getTeam()][ square.getJ() + square.getTeam()];
                        if (destination.getTeam() == -square.getTeam())
                        {
                            if (destination.getTeam() == OPPONENT)
                                singleGame.incrementPlayerPoints();
                            else if (destination.getTeam() == YOU)
                                singleGame.incrementComputerPoints();

                            destination.setTeam(square.getTeam());
                            destination.setPiece(square.getPiece());
                            destination.setPieceCount(square.getPieceCount()+ 1);
                            square.setPieceCount(0);
                            square.setTeam(NONE);
                            square.setPiece(" ");
                            moveSuccess = true;
                        }
                        else
                        {
                            rightImpossible = true;
                        }
                    }
                    catch (Exception e)
                {
                    rightImpossible = true;
                }
                break;
            }

            if(destination.getJ() == 7 && destination.getTeam() == OPPONENT && moveSuccess)
            {
                destination.setPiece("Q");
            }
            else if(destination.getJ() == 0 && destination.getTeam() == YOU && moveSuccess)
            {
                destination.setPiece("Q");
            }
        }

        return moveSuccess;
    }

    boolean moveRook(Square[][] board, Square square, SingleGame singleGame, int max)
    {
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
                            if (destination.getTeam() == OPPONENT)
                                singleGame.incrementPlayerPoints();
                            else if (destination.getTeam() == YOU)
                                singleGame.incrementComputerPoints();

                            destination.setTeam(square.getTeam());
                            destination.setPiece(square.getPiece());
                            destination.setPieceCount(square.getPieceCount() + 1);
                            square.setPieceCount(0);
                            square.setTeam(NONE);
                            square.setPiece(" ");
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

                            destination.setTeam(square.getTeam());
                            destination.setPiece(square.getPiece());
                            destination.setPieceCount(square.getPieceCount() + 1);
                            square.setPieceCount(0);
                            square.setTeam(NONE);
                            square.setPiece(" ");
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

                            destination.setTeam(square.getTeam());
                            destination.setPiece(square.getPiece());
                            destination.setPieceCount(square.getPieceCount() + 1);
                            square.setPieceCount(0);
                            square.setTeam(NONE);
                            square.setPiece(" ");
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

                            destination.setTeam(square.getTeam());
                            destination.setPiece(square.getPiece());
                            destination.setPieceCount(square.getPieceCount() + 1);
                            square.setPieceCount(0);
                            square.setTeam(NONE);
                            square.setPiece(" ");
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

    boolean moveRook( Square[][] board,  Square square,  SingleGame singleGame)
    {
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

        while(!moveSuccess && (!forwardImpossible || !leftImpossible || !rightImpossible || !backImpossible))
        {
            options.clear();


            if (!forwardImpossible)
                options.add(Direction.UP);
            if (!leftImpossible)
                options.add(Direction.LEFT);
            if (!rightImpossible)
                options.add(Direction.RIGHT);
            if (!backImpossible)
                options.add(Direction.BACK);

            switch (options.get(rand.nextInt(options.size())))
            {
                case UP: // Tries to move forward

                    count = rand.nextInt(maxForward)+1;

                    try
                    {

                        while (!nothingInWayForward( board,  square, count))
                        {

                            if (enemyInWayForward( board,  square, count))
                            {
                                while (!nothingInWayForward( board,  square, count-1))
                                    count--;
                                break;
                            }
                            count--;
                            if (count == 0)
                            {
                                forwardImpossible = true;
                                break;
                            }

                        }
                        destination = board[square.getI()][ square.getJ() + square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam())
                        {
                            if (destination.getTeam() == OPPONENT)
                                singleGame.incrementPlayerPoints();
                            else if (destination.getTeam() == YOU)
                                singleGame.incrementComputerPoints();

                            destination.setTeam(square.getTeam());
                            destination.setPiece(square.getPiece());
                            destination.setPieceCount(square.getPieceCount()+ 1);
                            square.setPieceCount(0);
                            square.setTeam(NONE);
                            square.setPiece(" ");
                            moveSuccess = true;
                        }
                    }
                    catch (Exception e)
                {
                    maxForward--;
                    if (maxForward == 0)
                        forwardImpossible = true;
                }
                break;
                case LEFT: // Tries to move left

                    count = rand.nextInt(maxLeft)+1;

                    try
                    {

                        while (!nothingInWayLeft( board,  square, count))
                        {
                            if (enemyInWayLeft( board,  square, count))
                            {
                                while (!nothingInWayLeft( board,  square, count - 1))
                                    count--;
                                break;
                            }
                            count--;

                            if (count == 0)
                            {
                                leftImpossible = true;
                                break;
                            }

                        }
                        destination = board[square.getI() + square.getTeam() * count][ square.getJ()];
                        if (count > 0 && destination.getTeam() != square.getTeam())
                        {
                            if (destination.getTeam() == OPPONENT)
                                singleGame.incrementPlayerPoints();
                            else if (destination.getTeam() == YOU)
                                singleGame.incrementComputerPoints();

                            destination.setTeam(square.getTeam());
                            destination.setPiece(square.getPiece());
                            destination.setPieceCount(square.getPieceCount()+ 1);
                            square.setPieceCount(0);
                            square.setTeam(NONE);
                            square.setPiece(" ");
                            moveSuccess = true;
                        }
                    }
                    catch(Exception e)
                {
                    maxLeft--;
                    if (maxLeft == 0)
                        leftImpossible = true;
                }
                break;
                case RIGHT: // Tries to move right

                    count = rand.nextInt(maxRight)+1;

                    try
                    {

                        while (!nothingInWayRight( board,  square, count))
                        {
                            if (enemyInWayRight( board,  square, count))
                            {
                                while (!nothingInWayRight( board,  square, count - 1))
                                    count--;
                                break;
                            }
                            count--;

                            if (count == 0)
                            {
                                rightImpossible = true;
                                break;
                            }

                        }
                        destination = board[square.getI() - square.getTeam() * count][ square.getJ()];
                        if (count > 0 && destination.getTeam() != square.getTeam())
                        {
                            if (destination.getTeam() == OPPONENT)
                                singleGame.incrementPlayerPoints();
                            else if (destination.getTeam() == YOU)
                                singleGame.incrementComputerPoints();

                            destination.setTeam(square.getTeam());
                            destination.setPiece(square.getPiece());
                            destination.setPieceCount(square.getPieceCount()+ 1);
                            square.setPieceCount(0);
                            square.setTeam(NONE);
                            square.setPiece(" ");
                            moveSuccess = true;
                        }
                    }
                    catch(Exception e)
                {
                    maxRight--;
                    if (maxRight == 0)
                        rightImpossible = true;
                }
                break;
                case BACK: // Tries to move back

                    count = rand.nextInt(maxBack) + 1;

                    try
                    {

                        while (!nothingInWayBack( board,  square, count))
                        {
                            if (enemyInWayBack( board,  square, count))
                            {
                                while (!nothingInWayBack( board,  square, count - 1))
                                    count--;
                                break;
                            }
                            count--;

                            if (count == 0)
                            {
                                backImpossible = true;
                                break;
                            }

                        }
                        destination = board[square.getI()][ square.getJ() - square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam())
                        {
                            if (destination.getTeam() == OPPONENT)
                                singleGame.incrementPlayerPoints();
                            else if (destination.getTeam() == YOU)
                                singleGame.incrementComputerPoints();

                            destination.setTeam(square.getTeam());
                            destination.setPiece(square.getPiece());
                            destination.setPieceCount(square.getPieceCount()+ 1);
                            square.setPieceCount(0);
                            square.setTeam(NONE);
                            square.setPiece(" ");
                            moveSuccess = true;
                        }
                    }
                    catch(Exception e)
                {
                    maxBack--;
                    if (maxBack == 0)
                        backImpossible = true;
                }
                break;

            }
        }



        return moveSuccess;
    }

    boolean moveKnight( Square[][] board,  Square square,  SingleGame singleGame)
    {
        List<Direction> options = new ArrayList<>();

        boolean moveSuccess = false;
        boolean _2R1U, _1R2U, _1L2U, _2L1U, _2L1D, _1L2D, _1R2D, _2R1D; // true means move is impossible
        _2R1U = _1R2U = _1L2U = _2L1U = _2L1D = _1L2D = _1R2D = _2R1D = false;


        rand.nextInt();

        while (!moveSuccess && (!_2R1U || !_1R2U || !_1L2U || !_2L1U || !_2L1D || !_1L2D || !_1R2D || !_2R1D))
        {
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


            switch (options.get(rand.nextInt(options.size())))
            {
                case _2R1U:
                    try
                    {
                        destination = board[square.getI() - square.getTeam() * 2][ square.getJ() + square.getTeam()];
                        if(destination.getTeam() != square.getTeam())
                        {
                            if (destination.getTeam() == OPPONENT)
                                singleGame.incrementPlayerPoints();
                            else if (destination.getTeam() == YOU)
                                singleGame.incrementComputerPoints();

                            destination.setTeam(square.getTeam());
                            destination.setPiece(square.getPiece());
                            destination.setPieceCount(square.getPieceCount()+ 1);
                            square.setPieceCount(0);
                            square.setTeam(NONE);
                            square.setPiece(" ");
                            moveSuccess = true;
                        }
                        else
                        {
                            _2R1U = true;
                        }
                    }
                    catch (Exception e)
                {
                    _2R1U = true;
                }
                break;
                case _1R2U:
                    try
                    {
                        destination = board[square.getI() - square.getTeam()][ square.getJ() + square.getTeam() * 2];
                        if (destination.getTeam() != square.getTeam())
                        {
                            if (destination.getTeam() == OPPONENT)
                                singleGame.incrementPlayerPoints();
                            else if (destination.getTeam() == YOU)
                                singleGame.incrementComputerPoints();

                            destination.setTeam(square.getTeam());
                            destination.setPiece(square.getPiece());
                            destination.setPieceCount(square.getPieceCount()+ 1);
                            square.setPieceCount(0);
                            square.setTeam(NONE);
                            square.setPiece(" ");
                            moveSuccess = true;
                        }
                        else
                        {
                            _1R2U = true;
                        }
                    }
                    catch (Exception e)
                {
                    _1R2U = true;
                }
                break;
                case _1L2U:
                    try
                    {
                        destination = board[square.getI() + square.getTeam()][ square.getJ() + square.getTeam() * 2];
                        if (destination.getTeam() != square.getTeam())
                        {
                            if (destination.getTeam() == OPPONENT)
                                singleGame.incrementPlayerPoints();
                            else if (destination.getTeam() == YOU)
                                singleGame.incrementComputerPoints();

                            destination.setTeam(square.getTeam());
                            destination.setPiece(square.getPiece());
                            destination.setPieceCount(square.getPieceCount()+ 1);
                            square.setPieceCount(0);
                            square.setTeam(NONE);
                            square.setPiece(" ");
                            moveSuccess = true;
                        }
                        else
                        {
                            _1L2U = true;
                        }
                    }
                    catch (Exception e)
                {
                    _1L2U = true;
                }
                break;
                case _2L1U:
                    try
                    {
                        destination = board[square.getI() + square.getTeam() * 2][ square.getJ() + square.getTeam()];
                        if (destination.getTeam() != square.getTeam())
                        {
                            if (destination.getTeam() == OPPONENT)
                                singleGame.incrementPlayerPoints();
                            else if (destination.getTeam() == YOU)
                                singleGame.incrementComputerPoints();

                            destination.setTeam(square.getTeam());
                            destination.setPiece(square.getPiece());
                            destination.setPieceCount(square.getPieceCount()+ 1);
                            square.setPieceCount(0);
                            square.setTeam(NONE);
                            square.setPiece(" ");
                            moveSuccess = true;
                        }
                        else
                        {
                            _2L1U = true;
                        }
                    }
                    catch (Exception e)
                {
                    _2L1U = true;
                }
                break;
                case _2L1D:
                    try
                    {
                        destination = board[square.getI() + square.getTeam() * 2][ square.getJ() - square.getTeam()];
                        if (destination.getTeam() != square.getTeam())
                        {
                            if (destination.getTeam() == OPPONENT)
                                singleGame.incrementPlayerPoints();
                            else if (destination.getTeam() == YOU)
                                singleGame.incrementComputerPoints();

                            destination.setTeam(square.getTeam());
                            destination.setPiece(square.getPiece());
                            destination.setPieceCount(square.getPieceCount()+ 1);
                            square.setPieceCount(0);
                            square.setTeam(NONE);
                            square.setPiece(" ");
                            moveSuccess = true;
                        }
                        else
                        {
                            _2L1D = true;
                        }
                    }
                    catch (Exception e)
                {
                    _2L1D = true;
                }
                break;
                case _1L2D:
                    try
                    {
                        destination = board[square.getI() + square.getTeam()][ square.getJ() - square.getTeam() * 2];
                        if (destination.getTeam() != square.getTeam())
                        {
                            if (destination.getTeam() == OPPONENT)
                                singleGame.incrementPlayerPoints();
                            else if (destination.getTeam() == YOU)
                                singleGame.incrementComputerPoints();

                            destination.setTeam(square.getTeam());
                            destination.setPiece(square.getPiece());
                            destination.setPieceCount(square.getPieceCount()+ 1);
                            square.setPieceCount(0);
                            square.setTeam(NONE);
                            square.setPiece(" ");
                            moveSuccess = true;
                        }
                        else
                        {
                            _1L2D = true;
                        }
                    }
                    catch (Exception e)
                {
                    _1L2D = true;
                }
                break;
                case _1R2D:
                    try
                    {
                        destination = board[square.getI() - square.getTeam()][ square.getJ() - square.getTeam() * 2];
                        if (destination.getTeam() != square.getTeam())
                        {
                            if (destination.getTeam() == OPPONENT)
                                singleGame.incrementPlayerPoints();
                            else if (destination.getTeam() == YOU)
                                singleGame.incrementComputerPoints();

                            destination.setTeam(square.getTeam());
                            destination.setPiece(square.getPiece());
                            destination.setPieceCount(square.getPieceCount()+ 1);
                            square.setPieceCount(0);
                            square.setTeam(NONE);
                            square.setPiece(" ");
                            moveSuccess = true;
                        }
                        else
                        {
                            _1R2D = true;
                        }
                    }
                    catch (Exception e)
                {
                    _1R2D = true;
                }
                break;
                case _2R1D:
                    try
                    {
                        destination = board[square.getI() - square.getTeam() * 2][ square.getJ() - square.getTeam()];
                        if (destination.getTeam() != square.getTeam())
                        {
                            if (destination.getTeam() == OPPONENT)
                                singleGame.incrementPlayerPoints();
                            else if (destination.getTeam() == YOU)
                                singleGame.incrementComputerPoints();

                            destination.setTeam(square.getTeam());
                            destination.setPiece(square.getPiece());
                            destination.setPieceCount(square.getPieceCount()+ 1);
                            square.setPieceCount(0);
                            square.setTeam(NONE);
                            square.setPiece(" ");
                            moveSuccess = true;
                        }
                        else
                        {
                            _2R1D = true;
                        }
                    }
                    catch (Exception e)
                {
                    _2R1D = true;
                }
                break;

            }


        }


        return moveSuccess;
    }

    boolean moveBishop(Square[][] board, Square square, SingleGame singleGame, int max)
    {
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

        int count;

        while (!moveSuccess && (!rightUpImpossible || !leftUpImpossible || !leftDownImpossible || !rightDownImpossible))
        {

            options.clear();

            if (!rightUpImpossible)
                options.add(Direction.RIGHTUP);
            if (!leftUpImpossible)
                options.add(Direction.LEFTUP);
            if (!leftDownImpossible)
                options.add(Direction.LEFTBACK);
            if (!rightDownImpossible)
                options.add(Direction.RIGHTBACK);

            switch (options.get(rand.nextInt(options.size())))
            {
                case RIGHTUP: // Tries to move right and up

                    count = rand.nextInt(maxRightUp)+1;

                    try
                    {
                        while (!nothingInWayRightUp( board,  square, count))
                        {
                            if (enemyInWayRightUp( board,  square, count))
                            {
                                while (!nothingInWayRightUp( board,  square, count - 1))
                                    count--;
                                break;
                            }
                            count--;
                            if (count == 0)
                            {
                                rightUpImpossible = true;
                                break;
                            }
                        }
                        destination = board[square.getI() - square.getTeam() * count][ square.getJ() + square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam())
                        {
                            if (destination.getTeam() == OPPONENT)
                                singleGame.incrementPlayerPoints();
                            else if (destination.getTeam() == YOU)
                                singleGame.incrementComputerPoints();

                            destination.setTeam(square.getTeam());
                            destination.setPiece(square.getPiece());
                            destination.setPieceCount(square.getPieceCount()+ 1);
                            square.setPieceCount(0);
                            square.setTeam(NONE);
                            square.setPiece(" ");
                            moveSuccess = true;
                        }
                    }
                    catch (Exception e)
                    {
                        maxRightUp--;
                        if (maxRightUp == 0)
                            rightUpImpossible = true;
                    }
                    break;
                case LEFTUP: // Tries to move left and up

                    count = rand.nextInt(maxLeftUp)+1;

                    try
                    {

                        while (!nothingInWayLeftUp( board,  square, count))
                        {
                            if (enemyInWayLeftUp( board,  square, count))
                            {
                                while (!nothingInWayLeftUp( board,  square, count - 1))
                                    count--;
                                break;
                            }
                            count--;
                            if (count == 0)
                            {
                                leftUpImpossible = true;
                                break;
                            }
                        }
                        destination = board[square.getI() + square.getTeam() * count][ square.getJ() + square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam())
                        {
                            if (destination.getTeam() == OPPONENT)
                                singleGame.incrementPlayerPoints();
                            else if (destination.getTeam() == YOU)
                                singleGame.incrementComputerPoints();

                            destination.setTeam(square.getTeam());
                            destination.setPiece(square.getPiece());
                            destination.setPieceCount(square.getPieceCount()+ 1);
                            square.setPieceCount(0);
                            square.setTeam(NONE);
                            square.setPiece(" ");
                            moveSuccess = true;
                        }
                    }
                    catch (Exception e)
                    {
                        maxLeftUp--;
                        if (maxLeftUp == 0)
                            leftUpImpossible = true;
                    }
                    break;
                case LEFTBACK: // Tries to move left and down


                    count = rand.nextInt(maxLeftDown)+1;

                    try
                    {

                        while (!nothingInWayLeftDown( board,  square, count))
                        {
                            if (enemyInWayLeftDown( board,  square, count))
                            {
                                while (!nothingInWayLeftDown( board,  square, count - 1))
                                    count--;
                                break;
                            }
                            count--;
                            if (count == 0)
                            {
                                leftDownImpossible = true;
                                break;
                            }
                        }
                        destination = board[square.getI() + square.getTeam() * count][ square.getJ() - square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam())
                        {
                            if (destination.getTeam() == OPPONENT)
                                singleGame.incrementPlayerPoints();
                            else if (destination.getTeam() == YOU)
                                singleGame.incrementComputerPoints();

                            destination.setTeam(square.getTeam());
                            destination.setPiece(square.getPiece());
                            destination.setPieceCount(square.getPieceCount()+ 1);
                            square.setPieceCount(0);
                            square.setTeam(NONE);
                            square.setPiece(" ");
                            moveSuccess = true;
                        }
                    }
                    catch (Exception e)
                    {
                        maxLeftDown--;
                        if (maxLeftDown == 0)
                            leftDownImpossible = true;
                    }
                    break;
                case RIGHTBACK: // Tries to move right and down

                    count = rand.nextInt(maxRightDown)+1;

                    try
                    {

                        while (!nothingInWayRightDown( board,  square, count))
                        {
                            if (enemyInWayRightDown( board,  square, count))
                            {
                                while (!nothingInWayRightDown( board,  square, count - 1))
                                    count--;
                                break;
                            }
                            count--;
                            if (count == 0)
                            {
                                rightDownImpossible = true;
                                break;
                            }
                        }
                        destination = board[square.getI() - square.getTeam() * count][ square.getJ() - square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam())
                        {
                            if (destination.getTeam() == OPPONENT)
                                singleGame.incrementPlayerPoints();
                            else if (destination.getTeam() == YOU)
                                singleGame.incrementComputerPoints();

                            destination.setTeam(square.getTeam());
                            destination.setPiece(square.getPiece());
                            destination.setPieceCount(square.getPieceCount()+ 1);
                            square.setPieceCount(0);
                            square.setTeam(NONE);
                            square.setPiece(" ");
                            moveSuccess = true;
                        }
                    }
                    catch (Exception e)
                    {
                        maxRightDown--;
                        if (maxRightDown == 0)
                            rightDownImpossible = true;
                    }
                    break;
            }

        }

        return moveSuccess;
    }

    boolean moveBishop( Square[][] board,  Square square,  SingleGame singleGame)
    {
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

        while (!moveSuccess && (!rightUpImpossible || !leftUpImpossible || !leftDownImpossible || !rightDownImpossible))
        {
            options.clear();

            if (!rightUpImpossible)
                options.add(Direction.RIGHTUP);
            if (!leftUpImpossible)
                options.add(Direction.LEFTUP);
            if (!leftDownImpossible)
                options.add(Direction.LEFTBACK);
            if (!rightDownImpossible)
                options.add(Direction.RIGHTBACK);

            switch (options.get(rand.nextInt(options.size())))
            {
                case RIGHTUP: // Tries to move right and up

                    count = rand.nextInt(maxRightUp)+1;

                    try
                    {
                        while (!nothingInWayRightUp( board,  square, count))
                        {
                            if (enemyInWayRightUp( board,  square, count))
                            {
                                while (!nothingInWayRightUp( board,  square, count - 1))
                                    count--;
                                break;
                            }
                            count--;
                            if (count == 0)
                            {
                                rightUpImpossible = true;
                                break;
                            }
                        }
                        destination = board[square.getI() - square.getTeam() * count][ square.getJ() + square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam())
                        {
                            if (destination.getTeam() == OPPONENT)
                                singleGame.incrementPlayerPoints();
                            else if (destination.getTeam() == YOU)
                                singleGame.incrementComputerPoints();

                            destination.setTeam(square.getTeam());
                            destination.setPiece(square.getPiece());
                            destination.setPieceCount(square.getPieceCount()+ 1);
                            square.setPieceCount(0);
                            square.setTeam(NONE);
                            square.setPiece(" ");
                            moveSuccess = true;
                        }
                    }
                    catch (Exception e)
                {
                    maxRightUp--;
                    if (maxRightUp == 0)
                        rightUpImpossible = true;
                }
                break;
                case LEFTUP: // Tries to move left and up

                    count = rand.nextInt(maxLeftUp)+1;

                    try
                    {

                        while (!nothingInWayLeftUp( board,  square, count))
                        {
                            if (enemyInWayLeftUp( board,  square, count))
                            {
                                while (!nothingInWayLeftUp( board,  square, count - 1))
                                    count--;
                                break;
                            }
                            count--;
                            if (count == 0)
                            {
                                leftUpImpossible = true;
                                break;
                            }
                        }
                        destination = board[square.getI() + square.getTeam() * count][ square.getJ() + square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam())
                        {
                            if (destination.getTeam() == OPPONENT)
                                singleGame.incrementPlayerPoints();
                            else if (destination.getTeam() == YOU)
                                singleGame.incrementComputerPoints();

                            destination.setTeam(square.getTeam());
                            destination.setPiece(square.getPiece());
                            destination.setPieceCount(square.getPieceCount()+ 1);
                            square.setPieceCount(0);
                            square.setTeam(NONE);
                            square.setPiece(" ");
                            moveSuccess = true;
                        }
                    }
                    catch (Exception e)
                {
                    maxLeftUp--;
                    if (maxLeftUp == 0)
                        leftUpImpossible = true;
                }
                break;
                case LEFTBACK: // Tries to move left and down


                    count = rand.nextInt(maxLeftDown)+1;

                    try
                    {

                        while (!nothingInWayLeftDown( board,  square, count))
                        {
                            if (enemyInWayLeftDown( board,  square, count))
                            {
                                while (!nothingInWayLeftDown( board,  square, count - 1))
                                    count--;
                                break;
                            }
                            count--;
                            if (count == 0)
                            {
                                leftDownImpossible = true;
                                break;
                            }
                        }
                        destination = board[square.getI() + square.getTeam() * count][ square.getJ() - square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam())
                        {
                            if (destination.getTeam() == OPPONENT)
                                singleGame.incrementPlayerPoints();
                            else if (destination.getTeam() == YOU)
                                singleGame.incrementComputerPoints();

                            destination.setTeam(square.getTeam());
                            destination.setPiece(square.getPiece());
                            destination.setPieceCount(square.getPieceCount()+ 1);
                            square.setPieceCount(0);
                            square.setTeam(NONE);
                            square.setPiece(" ");
                            moveSuccess = true;
                        }
                    }
                    catch (Exception e)
                {
                    maxLeftDown--;
                    if (maxLeftDown == 0)
                        leftDownImpossible = true;
                }
                break;
                case RIGHTBACK: // Tries to move right and down

                    count = rand.nextInt(maxRightDown)+1;

                    try
                    {

                        while (!nothingInWayRightDown( board,  square, count))
                        {
                            if (enemyInWayRightDown( board,  square, count))
                            {
                                while (!nothingInWayRightDown( board,  square, count - 1))
                                    count--;
                                break;
                            }
                            count--;
                            if (count == 0)
                            {
                                rightDownImpossible = true;
                                break;
                            }
                        }
                        destination = board[square.getI() - square.getTeam() * count][ square.getJ() - square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam())
                        {
                            if (destination.getTeam() == OPPONENT)
                                singleGame.incrementPlayerPoints();
                            else if (destination.getTeam() == YOU)
                                singleGame.incrementComputerPoints();

                            destination.setTeam(square.getTeam());
                            destination.setPiece(square.getPiece());
                            destination.setPieceCount(square.getPieceCount()+ 1);
                            square.setPieceCount(0);
                            square.setTeam(NONE);
                            square.setPiece(" ");
                            moveSuccess = true;
                        }
                    }
                    catch (Exception e)
                {
                    maxRightDown--;
                    if (maxRightDown == 0)
                        rightDownImpossible = true;
                }
                break;
            }

        }

        return moveSuccess;
    }

    boolean moveKing( Square[][] board,  Square square,  SingleGame singleGame)
    {
        List<KQMODE> options = new ArrayList<>();

        boolean moveSuccess = false;
        boolean bishopImpossible = false;
        boolean rookImpossible = false;
        while (!moveSuccess && (!bishopImpossible || !rookImpossible))
        {
            options.clear();
            if (!bishopImpossible)
                options.add(KQMODE.BISHOP);
            if (!rookImpossible)
                options.add(KQMODE.ROOK);

            switch (options.get(rand.nextInt(options.size())))
            {
                case BISHOP:
                    moveSuccess = moveBishop( board,  square, singleGame,1);
                    bishopImpossible = !moveSuccess;
                    break;
                case ROOK:
                    moveSuccess = moveRook( board,  square, singleGame, 1);
                    rookImpossible = !moveSuccess;
                    break;
            }
        }

        return moveSuccess;
    }

    boolean moveQueen( Square[][] board,  Square square,  SingleGame singleGame)
    {
        boolean moveSuccess = false;
        boolean bishopImpossible = false;
        boolean rookImpossible = false;
        while (!moveSuccess && (!bishopImpossible || !rookImpossible))
        {
            if (rand.nextInt(2) == 0 && !bishopImpossible)
            {
                moveSuccess = moveBishop( board,  square, singleGame);
                bishopImpossible = !moveSuccess;
            }
            else if (!rookImpossible)
            {
                moveSuccess = moveRook( board,  square, singleGame);
                rookImpossible = !moveSuccess;
            }
        }

        return moveSuccess;
    }

    public synchronized boolean canPieceMove(Square[][] board, Square square, SingleGame singleGame)
    {
        boolean moveSuccess;
        switch(square.getPiece())
        {
            case "P":
                moveSuccess = canPawnMove(board, square, singleGame);
                break;
            case "R":
                moveSuccess = canRookMove(board, square, singleGame);
                break;
            case "Kn":
                moveSuccess = canKnightMove( board,  square, singleGame);
                break;
            case "B":
                moveSuccess = canBishopMove( board,  square, singleGame);
                break;
            case "Ki":
                moveSuccess = canKingMove( board,  square, singleGame);
                break;
            case "Q":
                moveSuccess = canQueenMove( board,  square, singleGame);
                break;
            default:
                moveSuccess = false;
                break;
        }

        return moveSuccess;
    }

    boolean canPawnMove( Square[][] board,  Square square,  SingleGame singleGame)
    {
        List<Direction> options = new ArrayList<>();

        boolean moveSuccess = false;
        boolean forwardImpossible = false;
        boolean leftImpossible = false;
        boolean rightImpossible = false;
        int count;

        rand.nextInt();

        while (!moveSuccess && (!forwardImpossible || !leftImpossible || !rightImpossible))
        {
            options.clear();

            if (!forwardImpossible)
                options.add(Direction.UP);
            if (!leftImpossible)
                options.add(Direction.LEFT);
            if (!rightImpossible)
                options.add(Direction.RIGHT);

            switch(options.get(rand.nextInt(options.size())))
            {
                case UP: // tries to move forward


                    if (square.getPieceCount() == 0)
                        count = rand.nextInt(2)+1;
                    else
                        count = 1;

                    try
                    {

                        while(!nothingInWayForward( board,  square, count))
                        {
                            count--;
                            if(count == 0)
                            {
                                forwardImpossible = true;
                                break;
                            }
                        }
                        if(count > 0)
                        {
                            moveSuccess = true;
                        }
                    }
                    catch (Exception e)
                    {
                        forwardImpossible = true;
                    }
                    break;
                case LEFT: // tries to capture diagonally to left
                    try
                    {
                        destination = board[square.getI() + square.getTeam()][ square.getJ() + square.getTeam()];
                        if (destination.getTeam() == -square.getTeam())
                        {
                            moveSuccess = true;
                        }
                        else
                        {
                            leftImpossible = true;
                        }
                    }
                    catch (Exception e)
                    {
                        leftImpossible = true;
                    }
                    break;
                case RIGHT: // tries to capture diagonally to right
                    try
                    {
                        destination = board[square.getI() - square.getTeam()][ square.getJ() + square.getTeam()];
                        if (destination.getTeam() == -square.getTeam())
                        {
                            moveSuccess = true;
                        }
                        else
                        {
                            rightImpossible = true;
                        }
                    }
                    catch (Exception e)
                    {
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

    boolean canRookMove( Square[][] board,  Square square,  SingleGame singleGame)
    {
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

        while(!moveSuccess && (!forwardImpossible || !leftImpossible || !rightImpossible || !backImpossible))
        {
            options.clear();


            if (!forwardImpossible)
                options.add(Direction.UP);
            if (!leftImpossible)
                options.add(Direction.LEFT);
            if (!rightImpossible)
                options.add(Direction.RIGHT);
            if (!backImpossible)
                options.add(Direction.BACK);

            switch (options.get(rand.nextInt(options.size())))
            {
                case UP: // Tries to move forward

                    count = rand.nextInt(maxForward)+1;

                    try
                    {

                        while (!nothingInWayForward( board,  square, count))
                        {

                            if (enemyInWayForward( board,  square, count))
                            {
                                while (!nothingInWayForward( board,  square, count-1))
                                    count--;
                                break;
                            }
                            count--;
                            if (count == 0)
                            {
                                forwardImpossible = true;
                                break;
                            }

                        }
                        destination = board[square.getI()][ square.getJ() + square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam())
                        {

                            moveSuccess = true;
                        }
                    }
                    catch (Exception e)
                    {
                        maxForward--;
                        if (maxForward == 0)
                            forwardImpossible = true;
                    }
                    break;
                case LEFT: // Tries to move left

                    count = rand.nextInt(maxLeft)+1;

                    try
                    {

                        while (!nothingInWayLeft( board,  square, count))
                        {
                            if (enemyInWayLeft( board,  square, count))
                            {
                                while (!nothingInWayLeft( board,  square, count - 1))
                                    count--;
                                break;
                            }
                            count--;

                            if (count == 0)
                            {
                                leftImpossible = true;
                                break;
                            }

                        }
                        destination = board[square.getI() + square.getTeam() * count][ square.getJ()];
                        if (count > 0 && destination.getTeam() != square.getTeam())
                        {

                            moveSuccess = true;
                        }
                    }
                    catch(Exception e)
                    {
                        maxLeft--;
                        if (maxLeft == 0)
                            leftImpossible = true;
                    }
                    break;
                case RIGHT: // Tries to move right

                    count = rand.nextInt(maxRight)+1;

                    try
                    {

                        while (!nothingInWayRight( board,  square, count))
                        {
                            if (enemyInWayRight( board,  square, count))
                            {
                                while (!nothingInWayRight( board,  square, count - 1))
                                    count--;
                                break;
                            }
                            count--;

                            if (count == 0)
                            {
                                rightImpossible = true;
                                break;
                            }

                        }
                        destination = board[square.getI() - square.getTeam() * count][ square.getJ()];
                        if (count > 0 && destination.getTeam() != square.getTeam())
                        {

                            moveSuccess = true;
                        }
                    }
                    catch(Exception e)
                    {
                        maxRight--;
                        if (maxRight == 0)
                            rightImpossible = true;
                    }
                    break;
                case BACK: // Tries to move back

                    count = rand.nextInt(maxBack) + 1;

                    try
                    {

                        while (!nothingInWayBack( board,  square, count))
                        {
                            if (enemyInWayBack( board,  square, count))
                            {
                                while (!nothingInWayBack( board,  square, count - 1))
                                    count--;
                                break;
                            }
                            count--;

                            if (count == 0)
                            {
                                backImpossible = true;
                                break;
                            }

                        }
                        destination = board[square.getI()][ square.getJ() - square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam())
                        {
                            moveSuccess = true;
                        }
                    }
                    catch(Exception e)
                    {
                        maxBack--;
                        if (maxBack == 0)
                            backImpossible = true;
                    }
                    break;

            }
        }



        return moveSuccess;
    }

    boolean canKnightMove( Square[][] board,  Square square,  SingleGame singleGame)
    {
        List<Direction> options = new ArrayList<>();

        boolean moveSuccess = false;
        boolean _2R1U, _1R2U, _1L2U, _2L1U, _2L1D, _1L2D, _1R2D, _2R1D; // true means move is impossible
        _2R1U = _1R2U = _1L2U = _2L1U = _2L1D = _1L2D = _1R2D = _2R1D = false;



        rand.nextInt();

        while (!moveSuccess && (!_2R1U || !_1R2U || !_1L2U || !_2L1U || !_2L1D || !_1L2D || !_1R2D || !_2R1D))
        {
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


            switch (options.get(rand.nextInt(options.size())))
            {
                case _2R1U:
                    try
                    {
                        destination = board[square.getI() - square.getTeam() * 2][ square.getJ() + square.getTeam()];
                        if(destination.getTeam() != square.getTeam())
                        {
                            moveSuccess = true;
                        }
                        else
                        {
                            _2R1U = true;
                        }
                    }
                    catch (Exception e)
                    {
                        _2R1U = true;
                    }
                    break;
                case _1R2U:
                    try
                    {
                        destination = board[square.getI() - square.getTeam()][ square.getJ() + square.getTeam() * 2];
                        if (destination.getTeam() != square.getTeam())
                        {
                            moveSuccess = true;
                        }
                        else
                        {
                            _1R2U = true;
                        }
                    }
                    catch (Exception e)
                    {
                        _1R2U = true;
                    }
                    break;
                case _1L2U:
                    try
                    {
                        destination = board[square.getI() + square.getTeam()][ square.getJ() + square.getTeam() * 2];
                        if (destination.getTeam() != square.getTeam())
                        {
                            moveSuccess = true;
                        }
                        else
                        {
                            _1L2U = true;
                        }
                    }
                    catch (Exception e)
                    {
                        _1L2U = true;
                    }
                    break;
                case _2L1U:
                    try
                    {
                        destination = board[square.getI() + square.getTeam() * 2][ square.getJ() + square.getTeam()];
                        if (destination.getTeam() != square.getTeam())
                        {
                            moveSuccess = true;
                        }
                        else
                        {
                            _2L1U = true;
                        }
                    }
                    catch (Exception e)
                    {
                        _2L1U = true;
                    }
                    break;
                case _2L1D:
                    try
                    {
                        destination = board[square.getI() + square.getTeam() * 2][ square.getJ() - square.getTeam()];
                        if (destination.getTeam() != square.getTeam())
                        {
                            moveSuccess = true;
                        }
                        else
                        {
                            _2L1D = true;
                        }
                    }
                    catch (Exception e)
                    {
                        _2L1D = true;
                    }
                    break;
                case _1L2D:
                    try
                    {
                        destination = board[square.getI() + square.getTeam()][ square.getJ() - square.getTeam() * 2];
                        if (destination.getTeam() != square.getTeam())
                        {
                            moveSuccess = true;
                        }
                        else
                        {
                            _1L2D = true;
                        }
                    }
                    catch (Exception e)
                    {
                        _1L2D = true;
                    }
                    break;
                case _1R2D:
                    try
                    {
                        destination = board[square.getI() - square.getTeam()][ square.getJ() - square.getTeam() * 2];
                        if (destination.getTeam() != square.getTeam())
                        {
                            moveSuccess = true;
                        }
                        else
                        {
                            _1R2D = true;
                        }
                    }
                    catch (Exception e)
                    {
                        _1R2D = true;
                    }
                    break;
                case _2R1D:
                    try
                    {
                        destination = board[square.getI() - square.getTeam() * 2][ square.getJ() - square.getTeam()];
                        if (destination.getTeam() != square.getTeam())
                        {
                            moveSuccess = true;
                        }
                        else
                        {
                            _2R1D = true;
                        }
                    }
                    catch (Exception e)
                    {
                        _2R1D = true;
                    }
                    break;

            }


        }


        return moveSuccess;
    }

    boolean canBishopMove(Square[][] board, Square square, SingleGame singleGame, int max)
    {
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

        while (!moveSuccess && (!rightUpImpossible || !leftUpImpossible || !leftDownImpossible || !rightDownImpossible))
        {
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

            switch (options.get(rand.nextInt(options.size())))
            {
                case RIGHTUP: // Tries to move right and up

                    count = rand.nextInt(maxRightUp)+1;

                    try
                    {
                        while (!nothingInWayRightUp( board,  square, count))
                        {
                            if (enemyInWayRightUp( board,  square, count))
                            {
                                while (!nothingInWayRightUp( board,  square, count - 1))
                                    count--;
                                break;
                            }
                            count--;
                            if (count == 0)
                            {
                                rightUpImpossible = true;
                                break;
                            }
                        }
                        destination = board[square.getI() - square.getTeam() * count][ square.getJ() + square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam())
                        {
                            moveSuccess = true;
                        }
                    }
                    catch (Exception e)
                    {
                        maxRightUp--;
                        if (maxRightUp == 0)
                            rightUpImpossible = true;
                    }
                    break;
                case LEFTUP: // Tries to move left and up

                    count = rand.nextInt(maxLeftUp)+1;

                    try
                    {

                        while (!nothingInWayLeftUp( board,  square, count))
                        {
                            if (enemyInWayLeftUp( board,  square, count))
                            {
                                while (!nothingInWayLeftUp( board,  square, count - 1))
                                    count--;
                                break;
                            }
                            count--;
                            if (count == 0)
                            {
                                leftUpImpossible = true;
                                break;
                            }
                        }
                        destination = board[square.getI() + square.getTeam() * count][ square.getJ() + square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam())
                        {
                            moveSuccess = true;
                        }
                    }
                    catch (Exception e)
                    {
                        maxLeftUp--;
                        if (maxLeftUp == 0)
                            leftUpImpossible = true;
                    }
                    break;
                case LEFTBACK: // Tries to move left and down


                    count = rand.nextInt(maxLeftDown)+1;

                    try
                    {

                        while (!nothingInWayLeftDown( board,  square, count))
                        {
                            if (enemyInWayLeftDown( board,  square, count))
                            {
                                while (!nothingInWayLeftDown( board,  square, count - 1))
                                    count--;
                                break;
                            }
                            count--;
                            if (count == 0)
                            {
                                leftDownImpossible = true;
                                break;
                            }
                        }
                        destination = board[square.getI() + square.getTeam() * count][ square.getJ() - square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam())
                        {
                            moveSuccess = true;
                        }
                    }
                    catch (Exception e)
                    {
                        maxLeftDown--;
                        if (maxLeftDown == 0)
                            leftDownImpossible = true;
                    }
                    break;
                case RIGHTBACK: // Tries to move right and down

                    count = rand.nextInt(maxRightDown)+1;

                    try
                    {

                        while (!nothingInWayRightDown( board,  square, count))
                        {
                            if (enemyInWayRightDown( board,  square, count))
                            {
                                while (!nothingInWayRightDown( board,  square, count - 1))
                                    count--;
                                break;
                            }
                            count--;
                            if (count == 0)
                            {
                                rightDownImpossible = true;
                                break;
                            }
                        }
                        destination = board[square.getI() - square.getTeam() * count][ square.getJ() - square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam())
                        {
                            moveSuccess = true;
                        }
                    }
                    catch (Exception e)
                    {
                        maxRightDown--;
                        if (maxRightDown == 0)
                            rightDownImpossible = true;
                    }
                    break;
            }

        }

        return moveSuccess;
    }

    boolean canBishopMove( Square[][] board,  Square square,  SingleGame singleGame)
    {
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

        while (!moveSuccess && (!rightUpImpossible || !leftUpImpossible || !leftDownImpossible || !rightDownImpossible))
        {
            options.clear();


            if (!rightUpImpossible)
                options.add(Direction.RIGHTUP);
            if (!leftUpImpossible)
                options.add(Direction.LEFTUP);
            if (!leftDownImpossible)
                options.add(Direction.LEFTBACK);
            if (!rightDownImpossible)
                options.add(Direction.RIGHTBACK);

            switch (options.get(rand.nextInt(options.size())))
            {
                case RIGHTUP: // Tries to move right and up

                    count = rand.nextInt(maxRightUp)+1;

                    try
                    {
                        while (!nothingInWayRightUp( board,  square, count))
                        {
                            if (enemyInWayRightUp( board,  square, count))
                            {
                                while (!nothingInWayRightUp( board,  square, count - 1))
                                    count--;
                                break;
                            }
                            count--;
                            if (count == 0)
                            {
                                rightUpImpossible = true;
                                break;
                            }
                        }
                        destination = board[square.getI() - square.getTeam() * count][ square.getJ() + square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam())
                        {
                            moveSuccess = true;
                        }
                    }
                    catch (Exception e)
                    {
                        maxRightUp--;
                        if (maxRightUp == 0)
                            rightUpImpossible = true;
                    }
                    break;
                case LEFTUP: // Tries to move left and up

                    count = rand.nextInt(maxLeftUp)+1;

                    try
                    {

                        while (!nothingInWayLeftUp( board,  square, count))
                        {
                            if (enemyInWayLeftUp( board,  square, count))
                            {
                                while (!nothingInWayLeftUp( board,  square, count - 1))
                                    count--;
                                break;
                            }
                            count--;
                            if (count == 0)
                            {
                                leftUpImpossible = true;
                                break;
                            }
                        }
                        destination = board[square.getI() + square.getTeam() * count][ square.getJ() + square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam())
                        {
                            moveSuccess = true;
                        }
                    }
                    catch (Exception e)
                    {
                        maxLeftUp--;
                        if (maxLeftUp == 0)
                            leftUpImpossible = true;
                    }
                    break;
                case LEFTBACK: // Tries to move left and down


                    count = rand.nextInt(maxLeftDown)+1;

                    try
                    {

                        while (!nothingInWayLeftDown( board,  square, count))
                        {
                            if (enemyInWayLeftDown( board,  square, count))
                            {
                                while (!nothingInWayLeftDown( board,  square, count - 1))
                                    count--;
                                break;
                            }
                            count--;
                            if (count == 0)
                            {
                                leftDownImpossible = true;
                                break;
                            }
                        }
                        destination = board[square.getI() + square.getTeam() * count][ square.getJ() - square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam())
                        {
                            moveSuccess = true;
                        }
                    }
                    catch (Exception e)
                    {
                        maxLeftDown--;
                        if (maxLeftDown == 0)
                            leftDownImpossible = true;
                    }
                    break;
                case RIGHTBACK: // Tries to move right and down

                    count = rand.nextInt(maxRightDown)+1;

                    try
                    {

                        while (!nothingInWayRightDown( board,  square, count))
                        {
                            if (enemyInWayRightDown( board,  square, count))
                            {
                                while (!nothingInWayRightDown( board,  square, count - 1))
                                    count--;
                                break;
                            }
                            count--;
                            if (count == 0)
                            {
                                rightDownImpossible = true;
                                break;
                            }
                        }
                        destination = board[square.getI() - square.getTeam() * count][ square.getJ() - square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam())
                        {
                            moveSuccess = true;
                        }
                    }
                    catch (Exception e)
                    {
                        maxRightDown--;
                        if (maxRightDown == 0)
                            rightDownImpossible = true;
                    }
                    break;
            }

        }

        return moveSuccess;
    }

    boolean canKingMove( Square[][] board,  Square square,  SingleGame singleGame)
    {
        List<KQMODE> options = new ArrayList<>();

        boolean moveSuccess = false;
        boolean bishopImpossible = false;
        boolean rookImpossible = false;

        while (!moveSuccess && (!bishopImpossible || !rookImpossible))
        {
            if (!bishopImpossible)
                options.add(KQMODE.BISHOP);
            if (!rookImpossible)
                options.add(KQMODE.ROOK);

            switch (options.get(rand.nextInt(options.size())))
            {
                case BISHOP:
                    moveSuccess = canBishopMove( board,  square, singleGame,1);
                    bishopImpossible = !moveSuccess;
                    break;
                case ROOK:
                    moveSuccess = canRookMove( board,  square, singleGame, 1);
                    rookImpossible = !moveSuccess;
                    break;
            }
        }

        return moveSuccess;
    }

    boolean canQueenMove( Square[][] board,  Square square,  SingleGame singleGame)
    {
        List<KQMODE> options = new ArrayList<>();

        boolean moveSuccess = false;
        boolean bishopImpossible = false;
        boolean rookImpossible = false;
        while (!moveSuccess && (!bishopImpossible || !rookImpossible))
        {
            if (!bishopImpossible)
                options.add(KQMODE.BISHOP);
            if (!rookImpossible)
                options.add(KQMODE.ROOK);

            switch (options.get(rand.nextInt(options.size())))
            {
                case BISHOP:
                    moveSuccess = canBishopMove( board,  square, singleGame);
                    bishopImpossible = !moveSuccess;
                    break;
                case ROOK:
                    moveSuccess = canRookMove( board,  square, singleGame);
                    rookImpossible = !moveSuccess;
                    break;
            }
        }

        return moveSuccess;
    }
    //Single game functions end

    //Multi game functions start
    
    public synchronized boolean movePiece(Square[][] board, Square square, MultiGame multiGame)
    {
        boolean moveSuccess;
        switch(square.getPiece())
        {
            case "P":
                moveSuccess = movePawn(board, square, multiGame);
                break;
            case "R":
                moveSuccess = moveRook(board, square, multiGame);
                break;
            case "Kn":
                moveSuccess = moveKnight( board,  square, multiGame);
                break;
            case "B":
                moveSuccess = moveBishop( board,  square, multiGame);
                break;
            case "Ki":
                moveSuccess = moveKing( board,  square, multiGame);
                break;
            case "Q":
                moveSuccess = moveQueen( board,  square, multiGame);
                break;
            default:
                moveSuccess = false;
                break;
        }

        return moveSuccess;
    }

    boolean movePawn( Square[][] board,  Square square,  MultiGame multiGame)
    {
        List<Direction> options = new ArrayList<>();


        boolean moveSuccess = false;
        boolean forwardImpossible = false;
        boolean leftImpossible = false;
        boolean rightImpossible = false;
        destination = new Square(context,0xffffffff);

        rand.nextInt();
        int count;

        while (!moveSuccess && (!forwardImpossible || !leftImpossible || !rightImpossible))
        {
            options.clear();

            if (!forwardImpossible)
                options.add(Direction.UP);
            if (!leftImpossible)
                options.add(Direction.LEFT);
            if (!rightImpossible)
                options.add(Direction.RIGHT);


            switch(options.get(rand.nextInt(options.size())))
            {
                case UP: // tries to move forward

                    if (square.getPieceCount() == 0)
                        count = rand.nextInt(2)+1;
                    else
                        count = 1;

                    try
                    {

                        while(!nothingInWayForward( board,  square, count))
                        {
                            count--;
                            if(count == 0)
                            {
                                forwardImpossible = true;
                                break;
                            }
                        }
                        if(count > 0)
                        {
                            destination = board[square.getI()][square.getJ() + square.getTeam() * count];
                            destination.setTeam(square.getTeam());
                            destination.setPiece(square.getPiece());
                            destination.setPieceCount(square.getPieceCount() + 1);
                            square.setPieceCount(0);
                            square.setPiece(" ");
                            square.setTeam(NONE);
                            moveSuccess = true;
                        }
                    }
                    catch (Exception e)
                    {
                        forwardImpossible = true;
                    }
                    break;
                case LEFT: // tries to capture diagonally to left
                    try
                    {
                        destination = board[square.getI() + square.getTeam()][ square.getJ() + square.getTeam()];
                        if (destination.getTeam() == -square.getTeam())
                        {
                            if (destination.getTeam() == OPPONENT)
                                multiGame.incrementYourPoints();
                            else if (destination.getTeam() == YOU)
                                multiGame.incrementOpponentPoints();

                            destination.setTeam(square.getTeam());
                            destination.setPiece(square.getPiece());
                            destination.setPieceCount(square.getPieceCount()+ 1);
                            square.setPieceCount(0);
                            square.setTeam(NONE);
                            square.setPiece(" ");
                            moveSuccess = true;
                        }
                        else
                        {
                            leftImpossible = true;
                        }
                    }
                    catch (Exception e)
                    {
                        leftImpossible = true;
                    }
                    break;
                case RIGHT: // tries to capture diagonally to right
                    try
                    {
                        destination = board[square.getI() - square.getTeam()][ square.getJ() + square.getTeam()];
                        if (destination.getTeam() == -square.getTeam())
                        {
                            if (destination.getTeam() == OPPONENT)
                                multiGame.incrementYourPoints();
                            else if (destination.getTeam() == YOU)
                                multiGame.incrementOpponentPoints();

                            destination.setTeam(square.getTeam());
                            destination.setPiece(square.getPiece());
                            destination.setPieceCount(square.getPieceCount()+ 1);
                            square.setPieceCount(0);
                            square.setTeam(NONE);
                            square.setPiece(" ");
                            moveSuccess = true;
                        }
                        else
                        {
                            rightImpossible = true;
                        }
                    }
                    catch (Exception e)
                    {
                        rightImpossible = true;
                    }
                    break;
            }

            if(destination.getJ() == 7 && destination.getTeam() == OPPONENT && moveSuccess)
            {
                destination.setPiece("Q");
            }
            else if(destination.getJ() == 0 && destination.getTeam() == YOU && moveSuccess)
            {
                destination.setPiece("Q");
            }
        }

        return moveSuccess;
    }

    boolean moveRook(Square[][] board, Square square, MultiGame multiGame, int max)
    {
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
                            if (destination.getTeam() == OPPONENT)
                                multiGame.incrementYourPoints();
                            else if (destination.getTeam() == YOU)
                                multiGame.incrementOpponentPoints();

                            destination.setTeam(square.getTeam());
                            destination.setPiece(square.getPiece());
                            destination.setPieceCount(square.getPieceCount() + 1);
                            square.setPieceCount(0);
                            square.setTeam(NONE);
                            square.setPiece(" ");
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
                            square.setPiece(" ");
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
                            square.setPiece(" ");
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
                            square.setPiece(" ");
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

    boolean moveRook( Square[][] board,  Square square,  MultiGame multiGame)
    {
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

        while(!moveSuccess && (!forwardImpossible || !leftImpossible || !rightImpossible || !backImpossible))
        {
            options.clear();


            if (!forwardImpossible)
                options.add(Direction.UP);
            if (!leftImpossible)
                options.add(Direction.LEFT);
            if (!rightImpossible)
                options.add(Direction.RIGHT);
            if (!backImpossible)
                options.add(Direction.BACK);

            switch (options.get(rand.nextInt(options.size())))
            {
                case UP: // Tries to move forward

                    count = rand.nextInt(maxForward)+1;

                    try
                    {

                        while (!nothingInWayForward( board,  square, count))
                        {

                            if (enemyInWayForward( board,  square, count))
                            {
                                while (!nothingInWayForward( board,  square, count-1))
                                    count--;
                                break;
                            }
                            count--;
                            if (count == 0)
                            {
                                forwardImpossible = true;
                                break;
                            }

                        }
                        destination = board[square.getI()][ square.getJ() + square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam())
                        {
                            if (destination.getTeam() == OPPONENT)
                                multiGame.incrementYourPoints();
                            else if (destination.getTeam() == YOU)
                                multiGame.incrementOpponentPoints();

                            destination.setTeam(square.getTeam());
                            destination.setPiece(square.getPiece());
                            destination.setPieceCount(square.getPieceCount()+ 1);
                            square.setPieceCount(0);
                            square.setTeam(NONE);
                            square.setPiece(" ");
                            moveSuccess = true;
                        }
                    }
                    catch (Exception e)
                    {
                        maxForward--;
                        if (maxForward == 0)
                            forwardImpossible = true;
                    }
                    break;
                case LEFT: // Tries to move left

                    count = rand.nextInt(maxLeft)+1;

                    try
                    {

                        while (!nothingInWayLeft( board,  square, count))
                        {
                            if (enemyInWayLeft( board,  square, count))
                            {
                                while (!nothingInWayLeft( board,  square, count - 1))
                                    count--;
                                break;
                            }
                            count--;

                            if (count == 0)
                            {
                                leftImpossible = true;
                                break;
                            }

                        }
                        destination = board[square.getI() + square.getTeam() * count][ square.getJ()];
                        if (count > 0 && destination.getTeam() != square.getTeam())
                        {
                            if (destination.getTeam() == OPPONENT)
                                multiGame.incrementYourPoints();
                            else if (destination.getTeam() == YOU)
                                multiGame.incrementOpponentPoints();

                            destination.setTeam(square.getTeam());
                            destination.setPiece(square.getPiece());
                            destination.setPieceCount(square.getPieceCount()+ 1);
                            square.setPieceCount(0);
                            square.setTeam(NONE);
                            square.setPiece(" ");
                            moveSuccess = true;
                        }
                    }
                    catch(Exception e)
                    {
                        maxLeft--;
                        if (maxLeft == 0)
                            leftImpossible = true;
                    }
                    break;
                case RIGHT: // Tries to move right

                    count = rand.nextInt(maxRight)+1;

                    try
                    {

                        while (!nothingInWayRight( board,  square, count))
                        {
                            if (enemyInWayRight( board,  square, count))
                            {
                                while (!nothingInWayRight( board,  square, count - 1))
                                    count--;
                                break;
                            }
                            count--;

                            if (count == 0)
                            {
                                rightImpossible = true;
                                break;
                            }

                        }
                        destination = board[square.getI() - square.getTeam() * count][ square.getJ()];
                        if (count > 0 && destination.getTeam() != square.getTeam())
                        {
                            if (destination.getTeam() == OPPONENT)
                                multiGame.incrementYourPoints();
                            else if (destination.getTeam() == YOU)
                                multiGame.incrementOpponentPoints();

                            destination.setTeam(square.getTeam());
                            destination.setPiece(square.getPiece());
                            destination.setPieceCount(square.getPieceCount()+ 1);
                            square.setPieceCount(0);
                            square.setTeam(NONE);
                            square.setPiece(" ");
                            moveSuccess = true;
                        }
                    }
                    catch(Exception e)
                    {
                        maxRight--;
                        if (maxRight == 0)
                            rightImpossible = true;
                    }
                    break;
                case BACK: // Tries to move back

                    count = rand.nextInt(maxBack) + 1;

                    try
                    {

                        while (!nothingInWayBack( board,  square, count))
                        {
                            if (enemyInWayBack( board,  square, count))
                            {
                                while (!nothingInWayBack( board,  square, count - 1))
                                    count--;
                                break;
                            }
                            count--;

                            if (count == 0)
                            {
                                backImpossible = true;
                                break;
                            }

                        }
                        destination = board[square.getI()][ square.getJ() - square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam())
                        {
                            if (destination.getTeam() == OPPONENT)
                                multiGame.incrementYourPoints();
                            else if (destination.getTeam() == YOU)
                                multiGame.incrementOpponentPoints();

                            destination.setTeam(square.getTeam());
                            destination.setPiece(square.getPiece());
                            destination.setPieceCount(square.getPieceCount()+ 1);
                            square.setPieceCount(0);
                            square.setTeam(NONE);
                            square.setPiece(" ");
                            moveSuccess = true;
                        }
                    }
                    catch(Exception e)
                    {
                        maxBack--;
                        if (maxBack == 0)
                            backImpossible = true;
                    }
                    break;

            }
        }



        return moveSuccess;
    }

    boolean moveKnight( Square[][] board,  Square square,  MultiGame multiGame)
    {
        List<Direction> options = new ArrayList<>();

        boolean moveSuccess = false;
        boolean _2R1U, _1R2U, _1L2U, _2L1U, _2L1D, _1L2D, _1R2D, _2R1D; // true means move is impossible
        _2R1U = _1R2U = _1L2U = _2L1U = _2L1D = _1L2D = _1R2D = _2R1D = false;


        rand.nextInt();

        while (!moveSuccess && (!_2R1U || !_1R2U || !_1L2U || !_2L1U || !_2L1D || !_1L2D || !_1R2D || !_2R1D))
        {
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


            switch (options.get(rand.nextInt(options.size())))
            {
                case _2R1U:
                    try
                    {
                        destination = board[square.getI() - square.getTeam() * 2][ square.getJ() + square.getTeam()];
                        if(destination.getTeam() != square.getTeam())
                        {
                            if (destination.getTeam() == OPPONENT)
                                multiGame.incrementYourPoints();
                            else if (destination.getTeam() == YOU)
                                multiGame.incrementOpponentPoints();

                            destination.setTeam(square.getTeam());
                            destination.setPiece(square.getPiece());
                            destination.setPieceCount(square.getPieceCount()+ 1);
                            square.setPieceCount(0);
                            square.setTeam(NONE);
                            square.setPiece(" ");
                            moveSuccess = true;
                        }
                        else
                        {
                            _2R1U = true;
                        }
                    }
                    catch (Exception e)
                    {
                        _2R1U = true;
                    }
                    break;
                case _1R2U:
                    try
                    {
                        destination = board[square.getI() - square.getTeam()][ square.getJ() + square.getTeam() * 2];
                        if (destination.getTeam() != square.getTeam())
                        {
                            if (destination.getTeam() == OPPONENT)
                                multiGame.incrementYourPoints();
                            else if (destination.getTeam() == YOU)
                                multiGame.incrementOpponentPoints();

                            destination.setTeam(square.getTeam());
                            destination.setPiece(square.getPiece());
                            destination.setPieceCount(square.getPieceCount()+ 1);
                            square.setPieceCount(0);
                            square.setTeam(NONE);
                            square.setPiece(" ");
                            moveSuccess = true;
                        }
                        else
                        {
                            _1R2U = true;
                        }
                    }
                    catch (Exception e)
                    {
                        _1R2U = true;
                    }
                    break;
                case _1L2U:
                    try
                    {
                        destination = board[square.getI() + square.getTeam()][ square.getJ() + square.getTeam() * 2];
                        if (destination.getTeam() != square.getTeam())
                        {
                            if (destination.getTeam() == OPPONENT)
                                multiGame.incrementYourPoints();
                            else if (destination.getTeam() == YOU)
                                multiGame.incrementOpponentPoints();

                            destination.setTeam(square.getTeam());
                            destination.setPiece(square.getPiece());
                            destination.setPieceCount(square.getPieceCount()+ 1);
                            square.setPieceCount(0);
                            square.setTeam(NONE);
                            square.setPiece(" ");
                            moveSuccess = true;
                        }
                        else
                        {
                            _1L2U = true;
                        }
                    }
                    catch (Exception e)
                    {
                        _1L2U = true;
                    }
                    break;
                case _2L1U:
                    try
                    {
                        destination = board[square.getI() + square.getTeam() * 2][ square.getJ() + square.getTeam()];
                        if (destination.getTeam() != square.getTeam())
                        {
                            if (destination.getTeam() == OPPONENT)
                                multiGame.incrementYourPoints();
                            else if (destination.getTeam() == YOU)
                                multiGame.incrementOpponentPoints();

                            destination.setTeam(square.getTeam());
                            destination.setPiece(square.getPiece());
                            destination.setPieceCount(square.getPieceCount()+ 1);
                            square.setPieceCount(0);
                            square.setTeam(NONE);
                            square.setPiece(" ");
                            moveSuccess = true;
                        }
                        else
                        {
                            _2L1U = true;
                        }
                    }
                    catch (Exception e)
                    {
                        _2L1U = true;
                    }
                    break;
                case _2L1D:
                    try
                    {
                        destination = board[square.getI() + square.getTeam() * 2][ square.getJ() - square.getTeam()];
                        if (destination.getTeam() != square.getTeam())
                        {
                            if (destination.getTeam() == OPPONENT)
                                multiGame.incrementYourPoints();
                            else if (destination.getTeam() == YOU)
                                multiGame.incrementOpponentPoints();

                            destination.setTeam(square.getTeam());
                            destination.setPiece(square.getPiece());
                            destination.setPieceCount(square.getPieceCount()+ 1);
                            square.setPieceCount(0);
                            square.setTeam(NONE);
                            square.setPiece(" ");
                            moveSuccess = true;
                        }
                        else
                        {
                            _2L1D = true;
                        }
                    }
                    catch (Exception e)
                    {
                        _2L1D = true;
                    }
                    break;
                case _1L2D:
                    try
                    {
                        destination = board[square.getI() + square.getTeam()][ square.getJ() - square.getTeam() * 2];
                        if (destination.getTeam() != square.getTeam())
                        {
                            if (destination.getTeam() == OPPONENT)
                                multiGame.incrementYourPoints();
                            else if (destination.getTeam() == YOU)
                                multiGame.incrementOpponentPoints();

                            destination.setTeam(square.getTeam());
                            destination.setPiece(square.getPiece());
                            destination.setPieceCount(square.getPieceCount()+ 1);
                            square.setPieceCount(0);
                            square.setTeam(NONE);
                            square.setPiece(" ");
                            moveSuccess = true;
                        }
                        else
                        {
                            _1L2D = true;
                        }
                    }
                    catch (Exception e)
                    {
                        _1L2D = true;
                    }
                    break;
                case _1R2D:
                    try
                    {
                        destination = board[square.getI() - square.getTeam()][ square.getJ() - square.getTeam() * 2];
                        if (destination.getTeam() != square.getTeam())
                        {
                            if (destination.getTeam() == OPPONENT)
                                multiGame.incrementYourPoints();
                            else if (destination.getTeam() == YOU)
                                multiGame.incrementOpponentPoints();

                            destination.setTeam(square.getTeam());
                            destination.setPiece(square.getPiece());
                            destination.setPieceCount(square.getPieceCount()+ 1);
                            square.setPieceCount(0);
                            square.setTeam(NONE);
                            square.setPiece(" ");
                            moveSuccess = true;
                        }
                        else
                        {
                            _1R2D = true;
                        }
                    }
                    catch (Exception e)
                    {
                        _1R2D = true;
                    }
                    break;
                case _2R1D:
                    try
                    {
                        destination = board[square.getI() - square.getTeam() * 2][ square.getJ() - square.getTeam()];
                        if (destination.getTeam() != square.getTeam())
                        {
                            if (destination.getTeam() == OPPONENT)
                                multiGame.incrementYourPoints();
                            else if (destination.getTeam() == YOU)
                                multiGame.incrementOpponentPoints();

                            destination.setTeam(square.getTeam());
                            destination.setPiece(square.getPiece());
                            destination.setPieceCount(square.getPieceCount()+ 1);
                            square.setPieceCount(0);
                            square.setTeam(NONE);
                            square.setPiece(" ");
                            moveSuccess = true;
                        }
                        else
                        {
                            _2R1D = true;
                        }
                    }
                    catch (Exception e)
                    {
                        _2R1D = true;
                    }
                    break;

            }


        }


        return moveSuccess;
    }

    boolean moveBishop(Square[][] board, Square square, MultiGame multiGame, int max)
    {
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

        int count;

        while (!moveSuccess && (!rightUpImpossible || !leftUpImpossible || !leftDownImpossible || !rightDownImpossible))
        {

            options.clear();

            if (!rightUpImpossible)
                options.add(Direction.RIGHTUP);
            if (!leftUpImpossible)
                options.add(Direction.LEFTUP);
            if (!leftDownImpossible)
                options.add(Direction.LEFTBACK);
            if (!rightDownImpossible)
                options.add(Direction.RIGHTBACK);

            switch (options.get(rand.nextInt(options.size())))
            {
                case RIGHTUP: // Tries to move right and up

                    count = rand.nextInt(maxRightUp)+1;

                    try
                    {
                        while (!nothingInWayRightUp( board,  square, count))
                        {
                            if (enemyInWayRightUp( board,  square, count))
                            {
                                while (!nothingInWayRightUp( board,  square, count - 1))
                                    count--;
                                break;
                            }
                            count--;
                            if (count == 0)
                            {
                                rightUpImpossible = true;
                                break;
                            }
                        }
                        destination = board[square.getI() - square.getTeam() * count][ square.getJ() + square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam())
                        {
                            if (destination.getTeam() == OPPONENT)
                                multiGame.incrementYourPoints();
                            else if (destination.getTeam() == YOU)
                                multiGame.incrementOpponentPoints();

                            destination.setTeam(square.getTeam());
                            destination.setPiece(square.getPiece());
                            destination.setPieceCount(square.getPieceCount()+ 1);
                            square.setPieceCount(0);
                            square.setTeam(NONE);
                            square.setPiece(" ");
                            moveSuccess = true;
                        }
                    }
                    catch (Exception e)
                    {
                        maxRightUp--;
                        if (maxRightUp == 0)
                            rightUpImpossible = true;
                    }
                    break;
                case LEFTUP: // Tries to move left and up

                    count = rand.nextInt(maxLeftUp)+1;

                    try
                    {

                        while (!nothingInWayLeftUp( board,  square, count))
                        {
                            if (enemyInWayLeftUp( board,  square, count))
                            {
                                while (!nothingInWayLeftUp( board,  square, count - 1))
                                    count--;
                                break;
                            }
                            count--;
                            if (count == 0)
                            {
                                leftUpImpossible = true;
                                break;
                            }
                        }
                        destination = board[square.getI() + square.getTeam() * count][ square.getJ() + square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam())
                        {
                            if (destination.getTeam() == OPPONENT)
                                multiGame.incrementYourPoints();
                            else if (destination.getTeam() == YOU)
                                multiGame.incrementOpponentPoints();

                            destination.setTeam(square.getTeam());
                            destination.setPiece(square.getPiece());
                            destination.setPieceCount(square.getPieceCount()+ 1);
                            square.setPieceCount(0);
                            square.setTeam(NONE);
                            square.setPiece(" ");
                            moveSuccess = true;
                        }
                    }
                    catch (Exception e)
                    {
                        maxLeftUp--;
                        if (maxLeftUp == 0)
                            leftUpImpossible = true;
                    }
                    break;
                case LEFTBACK: // Tries to move left and down


                    count = rand.nextInt(maxLeftDown)+1;

                    try
                    {

                        while (!nothingInWayLeftDown( board,  square, count))
                        {
                            if (enemyInWayLeftDown( board,  square, count))
                            {
                                while (!nothingInWayLeftDown( board,  square, count - 1))
                                    count--;
                                break;
                            }
                            count--;
                            if (count == 0)
                            {
                                leftDownImpossible = true;
                                break;
                            }
                        }
                        destination = board[square.getI() + square.getTeam() * count][ square.getJ() - square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam())
                        {
                            if (destination.getTeam() == OPPONENT)
                                multiGame.incrementYourPoints();
                            else if (destination.getTeam() == YOU)
                                multiGame.incrementOpponentPoints();

                            destination.setTeam(square.getTeam());
                            destination.setPiece(square.getPiece());
                            destination.setPieceCount(square.getPieceCount()+ 1);
                            square.setPieceCount(0);
                            square.setTeam(NONE);
                            square.setPiece(" ");
                            moveSuccess = true;
                        }
                    }
                    catch (Exception e)
                    {
                        maxLeftDown--;
                        if (maxLeftDown == 0)
                            leftDownImpossible = true;
                    }
                    break;
                case RIGHTBACK: // Tries to move right and down

                    count = rand.nextInt(maxRightDown)+1;

                    try
                    {

                        while (!nothingInWayRightDown( board,  square, count))
                        {
                            if (enemyInWayRightDown( board,  square, count))
                            {
                                while (!nothingInWayRightDown( board,  square, count - 1))
                                    count--;
                                break;
                            }
                            count--;
                            if (count == 0)
                            {
                                rightDownImpossible = true;
                                break;
                            }
                        }
                        destination = board[square.getI() - square.getTeam() * count][ square.getJ() - square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam())
                        {
                            if (destination.getTeam() == OPPONENT)
                                multiGame.incrementYourPoints();
                            else if (destination.getTeam() == YOU)
                                multiGame.incrementOpponentPoints();

                            destination.setTeam(square.getTeam());
                            destination.setPiece(square.getPiece());
                            destination.setPieceCount(square.getPieceCount()+ 1);
                            square.setPieceCount(0);
                            square.setTeam(NONE);
                            square.setPiece(" ");
                            moveSuccess = true;
                        }
                    }
                    catch (Exception e)
                    {
                        maxRightDown--;
                        if (maxRightDown == 0)
                            rightDownImpossible = true;
                    }
                    break;
            }

        }

        return moveSuccess;
    }

    boolean moveBishop( Square[][] board,  Square square,  MultiGame multiGame)
    {
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

        while (!moveSuccess && (!rightUpImpossible || !leftUpImpossible || !leftDownImpossible || !rightDownImpossible))
        {
            options.clear();

            if (!rightUpImpossible)
                options.add(Direction.RIGHTUP);
            if (!leftUpImpossible)
                options.add(Direction.LEFTUP);
            if (!leftDownImpossible)
                options.add(Direction.LEFTBACK);
            if (!rightDownImpossible)
                options.add(Direction.RIGHTBACK);

            switch (options.get(rand.nextInt(options.size())))
            {
                case RIGHTUP: // Tries to move right and up

                    count = rand.nextInt(maxRightUp)+1;

                    try
                    {
                        while (!nothingInWayRightUp( board,  square, count))
                        {
                            if (enemyInWayRightUp( board,  square, count))
                            {
                                while (!nothingInWayRightUp( board,  square, count - 1))
                                    count--;
                                break;
                            }
                            count--;
                            if (count == 0)
                            {
                                rightUpImpossible = true;
                                break;
                            }
                        }
                        destination = board[square.getI() - square.getTeam() * count][ square.getJ() + square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam())
                        {
                            if (destination.getTeam() == OPPONENT)
                                multiGame.incrementYourPoints();
                            else if (destination.getTeam() == YOU)
                                multiGame.incrementOpponentPoints();

                            destination.setTeam(square.getTeam());
                            destination.setPiece(square.getPiece());
                            destination.setPieceCount(square.getPieceCount()+ 1);
                            square.setPieceCount(0);
                            square.setTeam(NONE);
                            square.setPiece(" ");
                            moveSuccess = true;
                        }
                    }
                    catch (Exception e)
                    {
                        maxRightUp--;
                        if (maxRightUp == 0)
                            rightUpImpossible = true;
                    }
                    break;
                case LEFTUP: // Tries to move left and up

                    count = rand.nextInt(maxLeftUp)+1;

                    try
                    {

                        while (!nothingInWayLeftUp( board,  square, count))
                        {
                            if (enemyInWayLeftUp( board,  square, count))
                            {
                                while (!nothingInWayLeftUp( board,  square, count - 1))
                                    count--;
                                break;
                            }
                            count--;
                            if (count == 0)
                            {
                                leftUpImpossible = true;
                                break;
                            }
                        }
                        destination = board[square.getI() + square.getTeam() * count][ square.getJ() + square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam())
                        {
                            if (destination.getTeam() == OPPONENT)
                                multiGame.incrementYourPoints();
                            else if (destination.getTeam() == YOU)
                                multiGame.incrementOpponentPoints();

                            destination.setTeam(square.getTeam());
                            destination.setPiece(square.getPiece());
                            destination.setPieceCount(square.getPieceCount()+ 1);
                            square.setPieceCount(0);
                            square.setTeam(NONE);
                            square.setPiece(" ");
                            moveSuccess = true;
                        }
                    }
                    catch (Exception e)
                    {
                        maxLeftUp--;
                        if (maxLeftUp == 0)
                            leftUpImpossible = true;
                    }
                    break;
                case LEFTBACK: // Tries to move left and down


                    count = rand.nextInt(maxLeftDown)+1;

                    try
                    {

                        while (!nothingInWayLeftDown( board,  square, count))
                        {
                            if (enemyInWayLeftDown( board,  square, count))
                            {
                                while (!nothingInWayLeftDown( board,  square, count - 1))
                                    count--;
                                break;
                            }
                            count--;
                            if (count == 0)
                            {
                                leftDownImpossible = true;
                                break;
                            }
                        }
                        destination = board[square.getI() + square.getTeam() * count][ square.getJ() - square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam())
                        {
                            if (destination.getTeam() == OPPONENT)
                                multiGame.incrementYourPoints();
                            else if (destination.getTeam() == YOU)
                                multiGame.incrementOpponentPoints();

                            destination.setTeam(square.getTeam());
                            destination.setPiece(square.getPiece());
                            destination.setPieceCount(square.getPieceCount()+ 1);
                            square.setPieceCount(0);
                            square.setTeam(NONE);
                            square.setPiece(" ");
                            moveSuccess = true;
                        }
                    }
                    catch (Exception e)
                    {
                        maxLeftDown--;
                        if (maxLeftDown == 0)
                            leftDownImpossible = true;
                    }
                    break;
                case RIGHTBACK: // Tries to move right and down

                    count = rand.nextInt(maxRightDown)+1;

                    try
                    {

                        while (!nothingInWayRightDown( board,  square, count))
                        {
                            if (enemyInWayRightDown( board,  square, count))
                            {
                                while (!nothingInWayRightDown( board,  square, count - 1))
                                    count--;
                                break;
                            }
                            count--;
                            if (count == 0)
                            {
                                rightDownImpossible = true;
                                break;
                            }
                        }
                        destination = board[square.getI() - square.getTeam() * count][ square.getJ() - square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam())
                        {
                            if (destination.getTeam() == OPPONENT)
                                multiGame.incrementYourPoints();
                            else if (destination.getTeam() == YOU)
                                multiGame.incrementOpponentPoints();

                            destination.setTeam(square.getTeam());
                            destination.setPiece(square.getPiece());
                            destination.setPieceCount(square.getPieceCount()+ 1);
                            square.setPieceCount(0);
                            square.setTeam(NONE);
                            square.setPiece(" ");
                            moveSuccess = true;
                        }
                    }
                    catch (Exception e)
                    {
                        maxRightDown--;
                        if (maxRightDown == 0)
                            rightDownImpossible = true;
                    }
                    break;
            }

        }

        return moveSuccess;
    }

    boolean moveKing( Square[][] board,  Square square,  MultiGame multiGame)
    {
        List<KQMODE> options = new ArrayList<>();

        boolean moveSuccess = false;
        boolean bishopImpossible = false;
        boolean rookImpossible = false;
        while (!moveSuccess && (!bishopImpossible || !rookImpossible))
        {
            options.clear();
            if (!bishopImpossible)
                options.add(KQMODE.BISHOP);
            if (!rookImpossible)
                options.add(KQMODE.ROOK);

            switch (options.get(rand.nextInt(options.size())))
            {
                case BISHOP:
                    moveSuccess = moveBishop( board,  square, multiGame,1);
                    bishopImpossible = !moveSuccess;
                    break;
                case ROOK:
                    moveSuccess = moveRook( board,  square, multiGame, 1);
                    rookImpossible = !moveSuccess;
                    break;
            }
        }

        return moveSuccess;
    }

    boolean moveQueen( Square[][] board,  Square square,  MultiGame multiGame)
    {
        boolean moveSuccess = false;
        boolean bishopImpossible = false;
        boolean rookImpossible = false;
        while (!moveSuccess && (!bishopImpossible || !rookImpossible))
        {
            if (rand.nextInt(2) == 0 && !bishopImpossible)
            {
                moveSuccess = moveBishop( board,  square, multiGame);
                bishopImpossible = !moveSuccess;
            }
            else if (!rookImpossible)
            {
                moveSuccess = moveRook( board,  square, multiGame);
                rookImpossible = !moveSuccess;
            }
        }

        return moveSuccess;
    }

    public synchronized boolean canPieceMove(Square[][] board, Square square, MultiGame multiGame)
    {
        boolean moveSuccess;
        switch(square.getPiece())
        {
            case "P":
                moveSuccess = canPawnMove(board, square, multiGame);
                break;
            case "R":
                moveSuccess = canRookMove(board, square, multiGame);
                break;
            case "Kn":
                moveSuccess = canKnightMove( board,  square, multiGame);
                break;
            case "B":
                moveSuccess = canBishopMove( board,  square, multiGame);
                break;
            case "Ki":
                moveSuccess = canKingMove( board,  square, multiGame);
                break;
            case "Q":
                moveSuccess = canQueenMove( board,  square, multiGame);
                break;
            default:
                moveSuccess = false;
                break;
        }

        return moveSuccess;
    }

    boolean canPawnMove( Square[][] board,  Square square,  MultiGame multiGame)
    {
        List<Direction> options = new ArrayList<>();

        boolean moveSuccess = false;
        boolean forwardImpossible = false;
        boolean leftImpossible = false;
        boolean rightImpossible = false;
        int count;

        rand.nextInt();

        while (!moveSuccess && (!forwardImpossible || !leftImpossible || !rightImpossible))
        {
            options.clear();

            if (!forwardImpossible)
                options.add(Direction.UP);
            if (!leftImpossible)
                options.add(Direction.LEFT);
            if (!rightImpossible)
                options.add(Direction.RIGHT);

            switch(options.get(rand.nextInt(options.size())))
            {
                case UP: // tries to move forward


                    if (square.getPieceCount() == 0)
                        count = rand.nextInt(2)+1;
                    else
                        count = 1;

                    try
                    {

                        while(!nothingInWayForward( board,  square, count))
                        {
                            count--;
                            if(count == 0)
                            {
                                forwardImpossible = true;
                                break;
                            }
                        }
                        if(count > 0)
                        {
                            moveSuccess = true;
                        }
                    }
                    catch (Exception e)
                    {
                        forwardImpossible = true;
                    }
                    break;
                case LEFT: // tries to capture diagonally to left
                    try
                    {
                        destination = board[square.getI() + square.getTeam()][ square.getJ() + square.getTeam()];
                        if (destination.getTeam() == -square.getTeam())
                        {
                            moveSuccess = true;
                        }
                        else
                        {
                            leftImpossible = true;
                        }
                    }
                    catch (Exception e)
                    {
                        leftImpossible = true;
                    }
                    break;
                case RIGHT: // tries to capture diagonally to right
                    try
                    {
                        destination = board[square.getI() - square.getTeam()][ square.getJ() + square.getTeam()];
                        if (destination.getTeam() == -square.getTeam())
                        {
                            moveSuccess = true;
                        }
                        else
                        {
                            rightImpossible = true;
                        }
                    }
                    catch (Exception e)
                    {
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

    boolean canRookMove( Square[][] board,  Square square,  MultiGame multiGame)
    {
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

        while(!moveSuccess && (!forwardImpossible || !leftImpossible || !rightImpossible || !backImpossible))
        {
            options.clear();


            if (!forwardImpossible)
                options.add(Direction.UP);
            if (!leftImpossible)
                options.add(Direction.LEFT);
            if (!rightImpossible)
                options.add(Direction.RIGHT);
            if (!backImpossible)
                options.add(Direction.BACK);

            switch (options.get(rand.nextInt(options.size())))
            {
                case UP: // Tries to move forward

                    count = rand.nextInt(maxForward)+1;

                    try
                    {

                        while (!nothingInWayForward( board,  square, count))
                        {

                            if (enemyInWayForward( board,  square, count))
                            {
                                while (!nothingInWayForward( board,  square, count-1))
                                    count--;
                                break;
                            }
                            count--;
                            if (count == 0)
                            {
                                forwardImpossible = true;
                                break;
                            }

                        }
                        destination = board[square.getI()][ square.getJ() + square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam())
                        {

                            moveSuccess = true;
                        }
                    }
                    catch (Exception e)
                    {
                        maxForward--;
                        if (maxForward == 0)
                            forwardImpossible = true;
                    }
                    break;
                case LEFT: // Tries to move left

                    count = rand.nextInt(maxLeft)+1;

                    try
                    {

                        while (!nothingInWayLeft( board,  square, count))
                        {
                            if (enemyInWayLeft( board,  square, count))
                            {
                                while (!nothingInWayLeft( board,  square, count - 1))
                                    count--;
                                break;
                            }
                            count--;

                            if (count == 0)
                            {
                                leftImpossible = true;
                                break;
                            }

                        }
                        destination = board[square.getI() + square.getTeam() * count][ square.getJ()];
                        if (count > 0 && destination.getTeam() != square.getTeam())
                        {

                            moveSuccess = true;
                        }
                    }
                    catch(Exception e)
                    {
                        maxLeft--;
                        if (maxLeft == 0)
                            leftImpossible = true;
                    }
                    break;
                case RIGHT: // Tries to move right

                    count = rand.nextInt(maxRight)+1;

                    try
                    {

                        while (!nothingInWayRight( board,  square, count))
                        {
                            if (enemyInWayRight( board,  square, count))
                            {
                                while (!nothingInWayRight( board,  square, count - 1))
                                    count--;
                                break;
                            }
                            count--;

                            if (count == 0)
                            {
                                rightImpossible = true;
                                break;
                            }

                        }
                        destination = board[square.getI() - square.getTeam() * count][ square.getJ()];
                        if (count > 0 && destination.getTeam() != square.getTeam())
                        {

                            moveSuccess = true;
                        }
                    }
                    catch(Exception e)
                    {
                        maxRight--;
                        if (maxRight == 0)
                            rightImpossible = true;
                    }
                    break;
                case BACK: // Tries to move back

                    count = rand.nextInt(maxBack) + 1;

                    try
                    {

                        while (!nothingInWayBack( board,  square, count))
                        {
                            if (enemyInWayBack( board,  square, count))
                            {
                                while (!nothingInWayBack( board,  square, count - 1))
                                    count--;
                                break;
                            }
                            count--;

                            if (count == 0)
                            {
                                backImpossible = true;
                                break;
                            }

                        }
                        destination = board[square.getI()][ square.getJ() - square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam())
                        {
                            moveSuccess = true;
                        }
                    }
                    catch(Exception e)
                    {
                        maxBack--;
                        if (maxBack == 0)
                            backImpossible = true;
                    }
                    break;

            }
        }



        return moveSuccess;
    }

    boolean canKnightMove( Square[][] board,  Square square,  MultiGame multiGame)
    {
        List<Direction> options = new ArrayList<>();

        boolean moveSuccess = false;
        boolean _2R1U, _1R2U, _1L2U, _2L1U, _2L1D, _1L2D, _1R2D, _2R1D; // true means move is impossible
        _2R1U = _1R2U = _1L2U = _2L1U = _2L1D = _1L2D = _1R2D = _2R1D = false;



        rand.nextInt();

        while (!moveSuccess && (!_2R1U || !_1R2U || !_1L2U || !_2L1U || !_2L1D || !_1L2D || !_1R2D || !_2R1D))
        {
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


            switch (options.get(rand.nextInt(options.size())))
            {
                case _2R1U:
                    try
                    {
                        destination = board[square.getI() - square.getTeam() * 2][ square.getJ() + square.getTeam()];
                        if(destination.getTeam() != square.getTeam())
                        {
                            moveSuccess = true;
                        }
                        else
                        {
                            _2R1U = true;
                        }
                    }
                    catch (Exception e)
                    {
                        _2R1U = true;
                    }
                    break;
                case _1R2U:
                    try
                    {
                        destination = board[square.getI() - square.getTeam()][ square.getJ() + square.getTeam() * 2];
                        if (destination.getTeam() != square.getTeam())
                        {
                            moveSuccess = true;
                        }
                        else
                        {
                            _1R2U = true;
                        }
                    }
                    catch (Exception e)
                    {
                        _1R2U = true;
                    }
                    break;
                case _1L2U:
                    try
                    {
                        destination = board[square.getI() + square.getTeam()][ square.getJ() + square.getTeam() * 2];
                        if (destination.getTeam() != square.getTeam())
                        {
                            moveSuccess = true;
                        }
                        else
                        {
                            _1L2U = true;
                        }
                    }
                    catch (Exception e)
                    {
                        _1L2U = true;
                    }
                    break;
                case _2L1U:
                    try
                    {
                        destination = board[square.getI() + square.getTeam() * 2][ square.getJ() + square.getTeam()];
                        if (destination.getTeam() != square.getTeam())
                        {
                            moveSuccess = true;
                        }
                        else
                        {
                            _2L1U = true;
                        }
                    }
                    catch (Exception e)
                    {
                        _2L1U = true;
                    }
                    break;
                case _2L1D:
                    try
                    {
                        destination = board[square.getI() + square.getTeam() * 2][ square.getJ() - square.getTeam()];
                        if (destination.getTeam() != square.getTeam())
                        {
                            moveSuccess = true;
                        }
                        else
                        {
                            _2L1D = true;
                        }
                    }
                    catch (Exception e)
                    {
                        _2L1D = true;
                    }
                    break;
                case _1L2D:
                    try
                    {
                        destination = board[square.getI() + square.getTeam()][ square.getJ() - square.getTeam() * 2];
                        if (destination.getTeam() != square.getTeam())
                        {
                            moveSuccess = true;
                        }
                        else
                        {
                            _1L2D = true;
                        }
                    }
                    catch (Exception e)
                    {
                        _1L2D = true;
                    }
                    break;
                case _1R2D:
                    try
                    {
                        destination = board[square.getI() - square.getTeam()][ square.getJ() - square.getTeam() * 2];
                        if (destination.getTeam() != square.getTeam())
                        {
                            moveSuccess = true;
                        }
                        else
                        {
                            _1R2D = true;
                        }
                    }
                    catch (Exception e)
                    {
                        _1R2D = true;
                    }
                    break;
                case _2R1D:
                    try
                    {
                        destination = board[square.getI() - square.getTeam() * 2][ square.getJ() - square.getTeam()];
                        if (destination.getTeam() != square.getTeam())
                        {
                            moveSuccess = true;
                        }
                        else
                        {
                            _2R1D = true;
                        }
                    }
                    catch (Exception e)
                    {
                        _2R1D = true;
                    }
                    break;

            }


        }


        return moveSuccess;
    }

    boolean canBishopMove(Square[][] board, Square square, MultiGame multiGame, int max)
    {
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

        while (!moveSuccess && (!rightUpImpossible || !leftUpImpossible || !leftDownImpossible || !rightDownImpossible))
        {
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

            switch (options.get(rand.nextInt(options.size())))
            {
                case RIGHTUP: // Tries to move right and up

                    count = rand.nextInt(maxRightUp)+1;

                    try
                    {
                        while (!nothingInWayRightUp( board,  square, count))
                        {
                            if (enemyInWayRightUp( board,  square, count))
                            {
                                while (!nothingInWayRightUp( board,  square, count - 1))
                                    count--;
                                break;
                            }
                            count--;
                            if (count == 0)
                            {
                                rightUpImpossible = true;
                                break;
                            }
                        }
                        destination = board[square.getI() - square.getTeam() * count][ square.getJ() + square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam())
                        {
                            moveSuccess = true;
                        }
                    }
                    catch (Exception e)
                    {
                        maxRightUp--;
                        if (maxRightUp == 0)
                            rightUpImpossible = true;
                    }
                    break;
                case LEFTUP: // Tries to move left and up

                    count = rand.nextInt(maxLeftUp)+1;

                    try
                    {

                        while (!nothingInWayLeftUp( board,  square, count))
                        {
                            if (enemyInWayLeftUp( board,  square, count))
                            {
                                while (!nothingInWayLeftUp( board,  square, count - 1))
                                    count--;
                                break;
                            }
                            count--;
                            if (count == 0)
                            {
                                leftUpImpossible = true;
                                break;
                            }
                        }
                        destination = board[square.getI() + square.getTeam() * count][ square.getJ() + square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam())
                        {
                            moveSuccess = true;
                        }
                    }
                    catch (Exception e)
                    {
                        maxLeftUp--;
                        if (maxLeftUp == 0)
                            leftUpImpossible = true;
                    }
                    break;
                case LEFTBACK: // Tries to move left and down


                    count = rand.nextInt(maxLeftDown)+1;

                    try
                    {

                        while (!nothingInWayLeftDown( board,  square, count))
                        {
                            if (enemyInWayLeftDown( board,  square, count))
                            {
                                while (!nothingInWayLeftDown( board,  square, count - 1))
                                    count--;
                                break;
                            }
                            count--;
                            if (count == 0)
                            {
                                leftDownImpossible = true;
                                break;
                            }
                        }
                        destination = board[square.getI() + square.getTeam() * count][ square.getJ() - square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam())
                        {
                            moveSuccess = true;
                        }
                    }
                    catch (Exception e)
                    {
                        maxLeftDown--;
                        if (maxLeftDown == 0)
                            leftDownImpossible = true;
                    }
                    break;
                case RIGHTBACK: // Tries to move right and down

                    count = rand.nextInt(maxRightDown)+1;

                    try
                    {

                        while (!nothingInWayRightDown( board,  square, count))
                        {
                            if (enemyInWayRightDown( board,  square, count))
                            {
                                while (!nothingInWayRightDown( board,  square, count - 1))
                                    count--;
                                break;
                            }
                            count--;
                            if (count == 0)
                            {
                                rightDownImpossible = true;
                                break;
                            }
                        }
                        destination = board[square.getI() - square.getTeam() * count][ square.getJ() - square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam())
                        {
                            moveSuccess = true;
                        }
                    }
                    catch (Exception e)
                    {
                        maxRightDown--;
                        if (maxRightDown == 0)
                            rightDownImpossible = true;
                    }
                    break;
            }

        }

        return moveSuccess;
    }

    boolean canBishopMove( Square[][] board,  Square square,  MultiGame multiGame)
    {
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

        while (!moveSuccess && (!rightUpImpossible || !leftUpImpossible || !leftDownImpossible || !rightDownImpossible))
        {
            options.clear();


            if (!rightUpImpossible)
                options.add(Direction.RIGHTUP);
            if (!leftUpImpossible)
                options.add(Direction.LEFTUP);
            if (!leftDownImpossible)
                options.add(Direction.LEFTBACK);
            if (!rightDownImpossible)
                options.add(Direction.RIGHTBACK);

            switch (options.get(rand.nextInt(options.size())))
            {
                case RIGHTUP: // Tries to move right and up

                    count = rand.nextInt(maxRightUp)+1;

                    try
                    {
                        while (!nothingInWayRightUp( board,  square, count))
                        {
                            if (enemyInWayRightUp( board,  square, count))
                            {
                                while (!nothingInWayRightUp( board,  square, count - 1))
                                    count--;
                                break;
                            }
                            count--;
                            if (count == 0)
                            {
                                rightUpImpossible = true;
                                break;
                            }
                        }
                        destination = board[square.getI() - square.getTeam() * count][ square.getJ() + square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam())
                        {
                            moveSuccess = true;
                        }
                    }
                    catch (Exception e)
                    {
                        maxRightUp--;
                        if (maxRightUp == 0)
                            rightUpImpossible = true;
                    }
                    break;
                case LEFTUP: // Tries to move left and up

                    count = rand.nextInt(maxLeftUp)+1;

                    try
                    {

                        while (!nothingInWayLeftUp( board,  square, count))
                        {
                            if (enemyInWayLeftUp( board,  square, count))
                            {
                                while (!nothingInWayLeftUp( board,  square, count - 1))
                                    count--;
                                break;
                            }
                            count--;
                            if (count == 0)
                            {
                                leftUpImpossible = true;
                                break;
                            }
                        }
                        destination = board[square.getI() + square.getTeam() * count][ square.getJ() + square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam())
                        {
                            moveSuccess = true;
                        }
                    }
                    catch (Exception e)
                    {
                        maxLeftUp--;
                        if (maxLeftUp == 0)
                            leftUpImpossible = true;
                    }
                    break;
                case LEFTBACK: // Tries to move left and down


                    count = rand.nextInt(maxLeftDown)+1;

                    try
                    {

                        while (!nothingInWayLeftDown( board,  square, count))
                        {
                            if (enemyInWayLeftDown( board,  square, count))
                            {
                                while (!nothingInWayLeftDown( board,  square, count - 1))
                                    count--;
                                break;
                            }
                            count--;
                            if (count == 0)
                            {
                                leftDownImpossible = true;
                                break;
                            }
                        }
                        destination = board[square.getI() + square.getTeam() * count][ square.getJ() - square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam())
                        {
                            moveSuccess = true;
                        }
                    }
                    catch (Exception e)
                    {
                        maxLeftDown--;
                        if (maxLeftDown == 0)
                            leftDownImpossible = true;
                    }
                    break;
                case RIGHTBACK: // Tries to move right and down

                    count = rand.nextInt(maxRightDown)+1;

                    try
                    {

                        while (!nothingInWayRightDown( board,  square, count))
                        {
                            if (enemyInWayRightDown( board,  square, count))
                            {
                                while (!nothingInWayRightDown( board,  square, count - 1))
                                    count--;
                                break;
                            }
                            count--;
                            if (count == 0)
                            {
                                rightDownImpossible = true;
                                break;
                            }
                        }
                        destination = board[square.getI() - square.getTeam() * count][ square.getJ() - square.getTeam() * count];
                        if (count > 0 && destination.getTeam() != square.getTeam())
                        {
                            moveSuccess = true;
                        }
                    }
                    catch (Exception e)
                    {
                        maxRightDown--;
                        if (maxRightDown == 0)
                            rightDownImpossible = true;
                    }
                    break;
            }

        }

        return moveSuccess;
    }

    boolean canKingMove( Square[][] board,  Square square,  MultiGame multiGame)
    {
        List<KQMODE> options = new ArrayList<>();

        boolean moveSuccess = false;
        boolean bishopImpossible = false;
        boolean rookImpossible = false;

        while (!moveSuccess && (!bishopImpossible || !rookImpossible))
        {
            if (!bishopImpossible)
                options.add(KQMODE.BISHOP);
            if (!rookImpossible)
                options.add(KQMODE.ROOK);

            switch (options.get(rand.nextInt(options.size())))
            {
                case BISHOP:
                    moveSuccess = canBishopMove( board,  square, multiGame,1);
                    bishopImpossible = !moveSuccess;
                    break;
                case ROOK:
                    moveSuccess = canRookMove( board,  square, multiGame, 1);
                    rookImpossible = !moveSuccess;
                    break;
            }
        }

        return moveSuccess;
    }

    boolean canQueenMove( Square[][] board,  Square square,  MultiGame multiGame)
    {
        List<KQMODE> options = new ArrayList<>();

        boolean moveSuccess = false;
        boolean bishopImpossible = false;
        boolean rookImpossible = false;
        while (!moveSuccess && (!bishopImpossible || !rookImpossible))
        {
            if (!bishopImpossible)
                options.add(KQMODE.BISHOP);
            if (!rookImpossible)
                options.add(KQMODE.ROOK);

            switch (options.get(rand.nextInt(options.size())))
            {
                case BISHOP:
                    moveSuccess = canBishopMove( board,  square, multiGame);
                    bishopImpossible = !moveSuccess;
                    break;
                case ROOK:
                    moveSuccess = canRookMove( board,  square, multiGame);
                    rookImpossible = !moveSuccess;
                    break;
            }
        }

        return moveSuccess;
    }
    
    //Multi game functions end

    boolean nothingInWayForward( Square[][] board,  Square square, int count)
    {
        boolean clear = true;

        for(int j = 1; j <= count; j++)
        {
            clear = board[square.getI()][ square.getJ() + square.getTeam()*j].getPiece() == " ";
            if (!clear)
                break;
        }

        return clear;
    }
    boolean nothingInWayLeft( Square[][] board,  Square square, int count)
    {
        boolean clear = true;

        for (int j = 1; j <= count; j++)
        {
            clear = board[square.getI() + square.getTeam() * j][ square.getJ()].getPiece() == " ";
            if (!clear)
                break;
        }

        return clear;
    }
    boolean nothingInWayRight( Square[][] board,  Square square, int count)
    {
        boolean clear = true;

        for (int j = 1; j <= count; j++)
        {
            clear = board[square.getI() - square.getTeam() * j][ square.getJ()].getPiece() == " ";
            if (!clear)
                break;
        }

        return clear;
    }
    boolean nothingInWayBack( Square[][] board,  Square square, int count)
    {
        boolean clear = true;

        for (int j = 1; j <= count; j++)
        {
            clear = board[square.getI()][ square.getJ() - square.getTeam() * j].getPiece() == " ";
            if (!clear)
                break;
        }

        return clear;
    }

    boolean nothingInWayRightUp( Square[][] board,  Square square, int count)
    {
        boolean clear = true;


        for (int j = 1; j <= count; j++)
        {
            destination = board[square.getI() - square.getTeam() * j][ square.getJ() + square.getTeam() * j];
            clear = destination.getPiece() == " ";
            if (!clear)
                break;
        }

        return clear;
    }

    boolean nothingInWayLeftUp( Square[][] board,  Square square, int count)
    {
        boolean clear = true;


        for (int j = 1; j <= count; j++)
        {
            destination = board[square.getI() + square.getTeam() * j][ square.getJ() + square.getTeam() * j];
            clear = destination.getPiece() == " ";
            if (!clear)
                break;
        }

        return clear;
    }

    boolean nothingInWayLeftDown( Square[][] board,  Square square, int count)
    {
        boolean clear = true;


        for (int j = 1; j <= count; j++)
        {
            destination = board[square.getI() + square.getTeam() * j][ square.getJ() - square.getTeam() * j];
            clear = destination.getPiece() == " ";
            if (!clear)
                break;
        }

        return clear;
    }

    boolean nothingInWayRightDown( Square[][] board,  Square square, int count)
    {
        boolean clear = true;


        for (int j = 1; j <= count; j++)
        {
            destination = board[square.getI() - square.getTeam() * j][ square.getJ() - square.getTeam() * j];
            clear = destination.getPiece() == " ";
            if (!clear)
                break;
        }

        return clear;
    }

    boolean enemyInWayForward( Square[][] board,  Square square, int count)
    {
        boolean enemy = false;


        for (int j = 1; j <= count; j++)
        {
            destination = board[square.getI()][ square.getJ() + square.getTeam() * j];
            enemy = destination.getTeam() == -square.getTeam();
            if (enemy)
                break;
        }

        return enemy;
    }

    boolean enemyInWayLeft( Square[][] board,  Square square, int count)
    {
        boolean enemy = false;


        for (int j = 1; j <= count; j++)
        {
            destination = board[square.getI() + square.getTeam() * j][ square.getJ()];
            enemy = destination.getTeam() == -square.getTeam();
            if (enemy)
                break;
        }

        return enemy;
    }

    boolean enemyInWayRight( Square[][] board,  Square square, int count)
    {
        boolean enemy = false;


        for (int j = 1; j <= count; j++)
        {
            destination = board[square.getI() - square.getTeam() * j][ square.getJ()];
            enemy = destination.getTeam() == -square.getTeam();
            if (enemy)
                break;
        }

        return enemy;
    }

    boolean enemyInWayBack( Square[][] board,  Square square, int count)
    {
        boolean enemy = false;


        for (int j = 1; j <= count; j++)
        {
            destination = board[square.getI()][ square.getJ() - square.getTeam() * j];
            enemy = destination.getTeam() == -square.getTeam();
            if (enemy)
                break;
        }

        return enemy;
    }

    boolean enemyInWayRightUp( Square[][] board,  Square square, int count)
    {
        boolean enemy = false;


        for (int j = 1; j <= count; j++)
        {
            destination = board[square.getI() - square.getTeam() * j][ square.getJ() + square.getTeam() * j];
            enemy = destination.getTeam() == -square.getTeam();
            if (enemy)
                break;
        }

        return enemy;
    }

    boolean enemyInWayLeftUp( Square[][] board,  Square square, int count)
    {
        boolean enemy = false;


        for (int j = 1; j <= count; j++)
        {
            destination = board[square.getI() + square.getTeam() * j][ square.getJ() + square.getTeam() * j];
            enemy = destination.getTeam() == -square.getTeam();
            if (enemy)
                break;
        }

        return enemy;
    }

    boolean enemyInWayLeftDown( Square[][] board,  Square square, int count)
    {
        boolean enemy = false;


        for (int j = 1; j <= count; j++)
        {
            destination = board[square.getI() + square.getTeam() * j][ square.getJ() - square.getTeam() * j];
            enemy = destination.getTeam() == -square.getTeam();
            if (enemy)
                break;
        }

        return enemy;
    }

    boolean enemyInWayRightDown( Square[][] board,  Square square, int count)
    {
        boolean enemy = false;


        for (int j = 1; j <= count; j++)
        {
            destination = board[square.getI() - square.getTeam() * j][ square.getJ() - square.getTeam() * j];
            enemy = destination.getTeam() == -square.getTeam();
            if (enemy)
                break;
        }

        return enemy;
    }

}
    

