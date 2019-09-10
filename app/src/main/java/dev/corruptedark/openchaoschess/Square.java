package dev.corruptedark.openchaoschess;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;


import androidx.appcompat.widget.AppCompatImageView;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;


/**
 * Created by CorruptedArk
 */
public class Square extends AppCompatImageView implements Serializable {

    private int pieceColor;
    private boolean color;
    private int team;
    private String piece;
    private int pieceCount;
    private int i;
    private int j;

    public byte[] toStream()
    {
        byte[] stream = null;

        try
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            stream = baos.toByteArray();

            oos.close();
            baos.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return stream;
    }

    public Square(Context context,int pieceColor) {
        super(context, null);
        team = 0;
        piece = " ";
        pieceCount = 0;
        i = -1;
        j = -1;
        this.pieceColor = pieceColor;
    }

    public synchronized void setPieceColor(int pieceColor){
        this.pieceColor = pieceColor;
    }

    public synchronized boolean getColor(){
        return color;
    }

    public synchronized void setColor(boolean value){
        color = value;
    }

    public synchronized int getTeam(){
        return team;
    }

    public synchronized void setTeam(int team){
        this.team = team;
    }

    public synchronized String getPiece(){
        return piece;
    }

    public synchronized void setPiece(String piece){
        this.piece = piece;
    }

    public synchronized int getPieceCount(){
        return pieceCount;
    }

    public synchronized void setPieceCount(int count){
        pieceCount = count;
    }

    public synchronized void incrementPieceCount(){
        pieceCount++;
    }

    public synchronized int getI(){
        return i;
    }

    public synchronized void setI(int i){
        this.i = i;
    }

    public synchronized int getJ(){
        return j;
    }

    public synchronized void setJ(int j){
        this.j = j;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int image;

        if (getTeam() == -1)
        {
            switch (getPiece())
            {
                case "P":
                    image = R.drawable.ic_filledpawn;
                    break;
                case "R":
                    image = R.drawable.ic_filledrook;
                    break;
                case "Kn":
                    image = R.drawable.ic_filledknight;
                    break;
                case "B":
                    image = R.drawable.ic_filledbishop;
                    break;
                case "Ki":
                    image = R.drawable.ic_filledking;
                    break;
                case "Q":
                    image = R.drawable.ic_filledqueen;
                    break;
                default:
                    image = R.drawable.empty;
                    break;
            }
        }
        else if (getTeam() == 1)
        {
            switch (getPiece())
            {
                case "P":
                    image = R.drawable.ic_hollowpawn;
                    break;
                case "R":
                    image = R.drawable.ic_hollowrook;
                    break;
                case "Kn":
                    image = R.drawable.ic_hollowknight;
                    break;
                case "B":
                    image = R.drawable.ic_hollowbishop;
                    break;
                case "Ki":
                    image = R.drawable.ic_hollowking;
                    break;
                case "Q":
                    image = R.drawable.ic_hollowqueen;
                    break;
                default:
                    image = R.drawable.empty;
                    break;
            }
        }
        else
            image = R.drawable.empty;


        setImageResource(image);
        setColorFilter(pieceColor, PorterDuff.Mode.MULTIPLY);
    }
}
