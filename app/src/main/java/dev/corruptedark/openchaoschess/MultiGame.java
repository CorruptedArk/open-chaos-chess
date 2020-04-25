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


import java.util.ArrayList;
import java.util.List;

public class MultiGame {
    public final int YOU = -1;
    public final int OPPONENT = 1;
    public final int NONE = 0;

    private static final int BOARD_SIZE = 8;

    private Square animatedSquare = null;

    private int yourPoints;
    private int opponentPoints;
    private int moveCount;
    private int turn;
    private static int gameCount;
    private boolean canYouMove;
    private boolean canOpponentMove;
    private static boolean hasBoard = false;

    static private MultiGame instance;

    static private Square[][] boardStatic = new Square[BOARD_SIZE][BOARD_SIZE];

    private MultiGame() {
        yourPoints = 0;
        opponentPoints = 0;
        moveCount = 0;
        turn = NONE;
        gameCount = 0;
    }

    static public MultiGame getInstance()
    {
        if(instance == null)
        {
            instance = new MultiGame();
        }

        return instance;
    }

    public void newGame(boolean isHost){
        yourPoints = 0;
        opponentPoints = 0;
        moveCount = 0;

        if(isHost)
        {
            turn = YOU;
        }
        else{
            turn = OPPONENT;
        }

        gameCount++;
    }

    public void resetGames(){
        gameCount = 0;
    }

    public int getYourPoints(){
        return yourPoints;
    }

    public void setYourPoints(int points){
        yourPoints = points;
    }

    public void incrementYourPoints(){
        yourPoints++;
        if(yourPoints >= 16)
        {
            turn = NONE;
        }
    }


    public int getOpponentPoints(){
        return opponentPoints;
    }

    public void setOpponentPoints(int points){
        opponentPoints = points;
    }

    public void incrementOpponentPoints(){
        opponentPoints++;
        if(opponentPoints >= 16)
        {
            turn = NONE;
        }
    }


    public int getYourCount(){
        return 16 - opponentPoints;
    }

    public int getOpponentCount(){
        return 16 - yourPoints;
    }


    public int getMoveCount(){
        return moveCount;
    }

    public void incrementMoveCount(){
        moveCount++;
    }


    public int getTurn(){
        return turn;
    }

    public void setTurn(int turn){
        this.turn = turn;
    }

    public int getGameCount() {
        return gameCount;
    }

    public boolean getCanYouMove(Mover mover, Square[][] board){
        canYouMove = true;
        List<Square> yourPieces = new ArrayList<>();
        Square picked;
        for (int i = 0; i < board.length; i++)
            for (int j = 0; j < board[i].length; j++)
                if (board[i][ j].getTeam() == YOU)
                    yourPieces.add(board[i][ j]);

        if (yourPieces.size() > 0) {
            picked = yourPieces.get(yourPieces.size() - 1);
            while (!mover.canPieceMove(board, picked, this)) {
                yourPieces.remove(picked);
                if (yourPieces.size() == 0) {
                    canYouMove = false;
                    break;
                }
                picked = yourPieces.get(yourPieces.size() - 1);
            }
        }
        else
            canYouMove = false;

        return canYouMove;
    }

    public boolean getCanOpponentMove(Mover mover, Square[][] board){
        canOpponentMove = true;
        List<Square> opponentPieces = new ArrayList<>();
        Square picked;
        for (int i = 0; i < board.length; i++)
            for (int j = 0; j < board[i].length; j++)
                if (board[i][ j].getTeam() == OPPONENT)
                    opponentPieces.add(board[i][ j]);

        if(opponentPieces.size() > 0) {
            picked = opponentPieces.get(opponentPieces.size() - 1);
            while (!mover.canPieceMove(board, picked, this)) {
                opponentPieces.remove(picked);
                if (opponentPieces.size() == 0) {
                    canOpponentMove = false;
                    break;
                }
                picked = opponentPieces.get(opponentPieces.size() - 1);
            }
        }
        else
            canOpponentMove = false;

        return canOpponentMove;
    }

    public List<Square> movableYous(Mover mover, Square[][] board){
        List<Square> yourPieces = new ArrayList<>();
        List<Square> movablePieces = new ArrayList<>();
        Square picked;
        for (int i = 0; i < board.length; i++)
            for (int j = 0; j < board[i].length; j++)
                if (board[i][ j].getTeam() == YOU)
                    yourPieces.add(board[i][ j]);

        for(int i =0; i < yourPieces.size(); i++) {
            picked = yourPieces.get(i);
            if(mover.canPieceMove(board,picked,this))
                movablePieces.add(picked);
        }
        return  movablePieces;
    }

    public List<Square> movableOpponents(Mover mover, Square[][] board){
        List<Square> opponentPieces = new ArrayList<>();
        List<Square> movablePieces = new ArrayList<>();
        Square picked;
        for (int i = 0; i < board.length; i++)
            for (int j = 0; j < board[i].length; j++)
                if (board[i][ j].getTeam() == OPPONENT)
                    opponentPieces.add(board[i][ j]);

        for(int i =0; i < opponentPieces.size(); i++) {
            picked = opponentPieces.get(i);
            if(mover.canPieceMove(board,picked,this))
                movablePieces.add(picked);
        }
        return  movablePieces;
    }

    public void setAnimatedSquare(Square animatedSquare){
        this.animatedSquare = animatedSquare;
    }

    public Square getAnimatedSquare() {
        return animatedSquare;
    }

    /*public void saveBoard(Square[][] board){
        boardStatic = board;
        hasBoard = true;
    }*/

    /*public Square[][] restoreBoard(){
        hasBoard = false;
        return boardStatic;
    }*/

    /*public boolean hasBoard(){
        return hasBoard;
    }*/


}
