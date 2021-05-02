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
    private boolean knightsOnly;
    private static int gameCount;
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
        hasBoard = false;
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
        return !movablePlayers(mover, board).isEmpty();
    }

    public boolean getCanComputerMove(Mover mover, Square[][] board){
        return !movableComputers(mover, board).isEmpty();
    }

    public List<Square> movablePlayers(Mover mover, Square[][] board){
        List<Square> playerPieces = new ArrayList<>();
        List<Square> movablePieces = new ArrayList<>();

        for(Square[] line : board)
            for(Square piece : line)
                if (piece.getTeam() == YOU)
                    playerPieces.add(piece);

        for(Square piece : playerPieces)
            if(mover.canPieceMove(board, piece, this))
                movablePieces.add(piece);

        return  movablePieces;
    }

    public List<Square> movableComputers(Mover mover, Square[][] board){
        List<Square> computerPieces = new ArrayList<>();
        List<Square> movablePieces = new ArrayList<>();

        for(Square[] line : board)
            for(Square piece : line)
                if (piece.getTeam() == OPPONENT)
                    computerPieces.add(piece);

        for(Square piece : computerPieces)
            if(mover.canPieceMove(board, piece, this))
                movablePieces.add(piece);

        return  movablePieces;
    }

    public void saveBoard(Square[][] board){
        boardStatic = board;
        hasBoard = true;
    }

    public Square[][] restoreBoard(){
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

    public void setKnightsOnly(boolean knightsOnly) {
        this.knightsOnly = knightsOnly;
    }

    public boolean isKnightsOnly() {
        return knightsOnly;
    }
}

