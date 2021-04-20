package dev.corruptedark.openchaoschess;

import java.util.ArrayList;
import java.util.List;

public class PieceCost {
    public static final int PAWN = 1;
    public static final int ROOK = 4;
    public static final int KNIGHT = 3;
    public static final int BISHOP = 3;
    public static final int KING = 2;
    public static final int QUEEN = 5;
    public static final int NONE = 0;

    public static int getPieceCost(Square square) {
        int cost;
        switch (square.getPiece()) {
            case Piece.PAWN:
                cost = PAWN;
                break;
            case Piece.ROOK:
                cost = ROOK;
                break;
            case Piece.KNIGHT:
                cost = KNIGHT;
                break;
            case Piece.BISHOP:
                cost = BISHOP;
                break;
            case Piece.KING:
                cost = KING;
                break;
            case Piece.QUEEN:
                cost = QUEEN;
                break;
            default:
                cost = NONE;
                break;
        }

        return cost;
    }

    public static int getAttackCost(Mover mover, Square[][] board, Square square) {
        int cost = 0;
        List<Square> enemies = new ArrayList<>();
        switch (square.getPiece()) {
            case Piece.PAWN:
                try {
                    Square left = board[square.getI() + square.getTeam()][square.getJ() + square.getTeam()];
                    if (left.getTeam() == left.YOU) // left capture
                    {
                        enemies.add(left);
                    }
                } catch (Exception e) {
                }

                try {
                    Square right = board[square.getI() - square.getTeam()][square.getJ() + square.getTeam()];
                    if (right.getTeam() == right.YOU) // right capture
                    {
                        enemies.add(right);
                    }
                } catch (Exception e) {
                }
                break;
            case Piece.ROOK:
                enemies.addAll(mover.getEnemiesOfRook(board, square));
                break;
            case Piece.BISHOP:
                enemies.addAll(mover.getEnemiesOfBishop(board, square));
                break;
            case Piece.KNIGHT:
                List<Square> knightEnemies = new ArrayList<>();
                knightEnemies.addAll(mover.getKnightMoves(board, square));
                for (Square piece : knightEnemies) {
                    if(piece.getTeam() == piece.YOU)
                        enemies.add(piece);
                }
            case Piece.QUEEN:
                enemies.addAll(mover.getEnemiesOfBishop(board, square));
                enemies.addAll(mover.getEnemiesOfRook(board, square));
                break;
            case Piece.KING:
                enemies.addAll(mover.getEnemiesOfBishop(board, square, 1));
                enemies.addAll(mover.getEnemiesOfRook(board, square, 1));
                break;
        }

        if(!enemies.isEmpty()) {
            for (Square piece : enemies) {
                cost += getPieceCost(piece);
            }
            cost /= enemies.size();
        }

        return cost;
    }

    public static int getDangerCostOfMove(Mover mover, Square[][] board, Square square, boolean bloodThirsty) {
        int cost = 0;
        List<Square> reachableSquares = mover.getMovesOfPiece(board, square, bloodThirsty);

        if(!reachableSquares.isEmpty()) {
            for (Square piece : reachableSquares) {                                           // Piece can be NONE, but it's not important
                cost += getPieceCost(square) * (mover.pieceInDanger(board, piece) ? 1 : 0);
            }
            cost /= reachableSquares.size();
        }

        return cost;
    }
}
