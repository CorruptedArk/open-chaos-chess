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

public class SingleGame {
    public final int YOU = -1;
    public final int OPPONENT = 1;
    public final int NONE = 0;

    public static final int BOARD_SIZE = 8;

    private Square animatedSquare = null;

    private int playerPoints;
    private int computerPoints;
    private int moveCount;
    private int turn;
    private static int gameCount;
    private boolean canPlayerMove;
    private boolean canComputerMove;
    private static boolean hasBoard = false;


    static private SingleGame instance;

    static private Square[][] boardStatic = new Square[BOARD_SIZE][BOARD_SIZE];

    private SingleGame() {
        playerPoints = 0;
        computerPoints = 0;
        moveCount = 0;
        turn = YOU;
        gameCount = 0;
    }

    static public SingleGame getInstance()
    {
        if(instance == null)
        {
            instance = new SingleGame();
        }

        return instance;
    }

    public void newGame(){
        playerPoints = 0;
        computerPoints = 0;
        moveCount = 0;
        turn = YOU;
        gameCount++;
    }

    public int getPlayerPoints(){
        return playerPoints;
    }

    public void setPlayerPoints(int points){
        playerPoints = points;
    }

    public void incrementPlayerPoints(){
        playerPoints++;
    }


    public int getComputerPoints(){
        return computerPoints;
    }

    public void setComputerPoints(int points){
        computerPoints = points;
    }

    public void incrementComputerPoints(){
        computerPoints++;
    }


    public int getPlayerCount(){
        return 16 - computerPoints;
    }

    public int getComputerCount(){
        return 16 - playerPoints;
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

    public boolean getCanPlayerMove(Mover mover, Square[][] board){
        canPlayerMove = true;
        List<Square> playerPieces = new ArrayList<>();
        Square picked;
        for (int i = 0; i < board.length; i++)
            for (int j = 0; j < board[i].length; j++)
                if (board[i][ j].getTeam() == YOU)
                    playerPieces.add(board[i][ j]);

        if (playerPieces.size() > 0) {
            picked = playerPieces.get(playerPieces.size() - 1);
            while (!mover.canPieceMove(board, picked, this)) {
                playerPieces.remove(picked);
                if (playerPieces.size() == 0) {
                    canPlayerMove = false;
                    break;
                }
                picked = playerPieces.get(playerPieces.size() - 1);
            }
        }
        else
            canPlayerMove = false;

        return canPlayerMove;
    }

    public boolean getCanComputerMove(Mover mover, Square[][] board){
        canComputerMove = true;
        List<Square> computerPieces = new ArrayList<>();
        Square picked;
        for (int i = 0; i < board.length; i++)
            for (int j = 0; j < board[i].length; j++)
                if (board[i][ j].getTeam() == OPPONENT)
                    computerPieces.add(board[i][ j]);

        if(computerPieces.size() > 0) {
            picked = computerPieces.get(computerPieces.size() - 1);
            while (!mover.canPieceMove(board, picked, this)) {
                computerPieces.remove(picked);
                if (computerPieces.size() == 0) {
                    canComputerMove = false;
                    break;
                }
                picked = computerPieces.get(computerPieces.size() - 1);
            }
        }
        else
            canComputerMove = false;

        return canComputerMove;
    }

    public List<Square> movablePlayers(Mover mover, Square[][] board){
        List<Square> playerPieces = new ArrayList<>();
        List<Square> movablePieces = new ArrayList<>();
        Square picked;
        for (int i = 0; i < board.length; i++)
            for (int j = 0; j < board[i].length; j++)
                if (board[i][ j].getTeam() == YOU)
                    playerPieces.add(board[i][ j]);

        for(int i =0; i < playerPieces.size(); i++) {
            picked = playerPieces.get(i);
            if(mover.canPieceMove(board,picked,this))
                movablePieces.add(picked);
        }
        return  movablePieces;
    }

    public List<Square> movableComputers(Mover mover, Square[][] board){
        List<Square> computerPieces = new ArrayList<>();
        List<Square> movablePieces = new ArrayList<>();
        Square picked;
        for (int i = 0; i < board.length; i++)
            for (int j = 0; j < board[i].length; j++)
                if (board[i][ j].getTeam() == OPPONENT)
                    computerPieces.add(board[i][ j]);

        for(int i =0; i < computerPieces.size(); i++) {
            picked = computerPieces.get(i);
            if(mover.canPieceMove(board,picked,this))
                movablePieces.add(picked);
        }
        return  movablePieces;
    }

    public void saveBoard(Square[][] board){
        boardStatic = board;
        hasBoard = true;
    }

    public Square[][] restoreBoard(){
        hasBoard = false;
        return boardStatic;
    }

    public boolean hasBoard(){
        return hasBoard;
    }

    public void setAnimatedSquare(Square animatedSquare) {
        this.animatedSquare = animatedSquare;
    }

    public Square getAnimatedSquare() {
        return animatedSquare;
    }
}

