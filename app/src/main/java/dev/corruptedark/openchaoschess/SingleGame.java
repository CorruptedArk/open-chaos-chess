package dev.corruptedark.openchaoschess;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by CorruptedArk
 */
public class SingleGame {
    public final int YOU = -1;
    public final int OPPONENT = 1;
    public final int NONE = 0;

    private static final int BOARD_SIZE = 8;

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

}
