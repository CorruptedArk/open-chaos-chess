package dev.corruptedark.openchaoschess;

import android.content.Context;
import android.graphics.Color;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class ColorManager {

    public static final int BACKGROUND_COLOR = 0;
    public static final int BAR_COLOR = 1;
    public static final int SECONDARY_COLOR = 2;
    public static final int BOARD_COLOR_1 = 3;
    public static final int BOARD_COLOR_2 = 4;
    public static final int PIECE_COLOR = 5;
    public static final int SELECTION_COLOR = 6;
    public static final int TEXT_COLOR = 7;

    private static ColorManager instance;
    private File settingsFile;
    private InputStream fileReader;
    private OutputStream fileWriter;
    private byte[] bytes;
    private String[] contentArray;

    private ColorManager(Context context) {

        settingsFile = new File(context.getApplicationContext().getFilesDir(),context.getString(R.string.settings_file));
        if(settingsFile.exists()) {
            try{
                fileReader = new FileInputStream(settingsFile);
                bytes = new byte[(int)settingsFile.length()];
                fileReader.read(bytes);
                fileReader.close();
                String contents = new String(bytes);
                contentArray = contents.split(" ");
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        else {
            try {
                fileWriter = new FileOutputStream(settingsFile,false);
                String contents = "FF303030 FF454545 FF696969 FF800000 FF000000 FFFFFFFF FF888888 FFFFFFFF";
                contentArray = contents.split(" ");
                fileWriter.write(contents.getBytes());
                fileWriter.close();
            }
            catch (Exception e){
                e.printStackTrace();
            }

        }

    }

    public static ColorManager getInstance(Context context)
    {
        if(instance == null)
        {
            instance = new ColorManager(context);
        }

        return instance;
    }

    public int getColorFromFile(int colorId)
    {
        return Color.parseColor("#"+contentArray[colorId]);
    }

    public void updateColor(int colorId, int colorValue)
    {
        contentArray[colorId] = Integer.toHexString(colorValue);

    }

    public boolean saveChangesToFile()
    {
        boolean successful;
        StringBuilder contents = new StringBuilder();

        for(int i = 0; i < contentArray.length; i++)
        {
            contents.append(contentArray[i]).append(" ");
        }

        contents.deleteCharAt(contents.length()-1);

        try{
            fileWriter = new FileOutputStream(settingsFile,false);
            fileWriter.write(contents.toString().getBytes());
            fileWriter.close();
            successful = true;
        }
        catch (Exception e)
        {
            successful = false;
        }

        return successful;
    }

}
