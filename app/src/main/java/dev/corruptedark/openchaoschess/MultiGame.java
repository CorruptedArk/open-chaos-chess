package dev.corruptedark.openchaoschess;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by CorruptedArk
 */
public class MultiGame {
    public final int YOU = -1;
    public final int OPPONENT = 1;
    public final int NONE = 0;

    private static final int BOARD_SIZE = 8;

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
        turn = YOU;
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

    public void newGame(){
        yourPoints = 0;
        opponentPoints = 0;
        moveCount = 0;
        turn = YOU;
        gameCount++;
    }

    public int getYourPoints(){
        return yourPoints;
    }

    public void setYourPoints(int points){
        yourPoints = points;
    }

    public void incrementYourPoints(){
        yourPoints++;
    }


    public int getOpponentPoints(){
        return opponentPoints;
    }

    public void setOpponentPoints(int points){
        opponentPoints = points;
    }

    public void incrementOpponentPoints(){
        opponentPoints++;
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
