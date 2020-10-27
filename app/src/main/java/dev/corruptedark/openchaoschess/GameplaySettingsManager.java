package dev.corruptedark.openchaoschess;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class GameplaySettingsManager {

    private static GameplaySettingsManager instance;

    private Context context;
    private File settingsFile;
    private InputStream fileReader;
    private OutputStream fileWriter;
    private byte[] bytes;
    private String[] contentArray;

    private final int BLOODTHIRST_BY_DEFAULT = 0;

    private GameplaySettingsManager(Context context) {

        this.context = context;
        settingsFile = new File(context.getApplicationContext().getFilesDir(),context.getString(R.string.gameplay_settings_file));
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
                String contents = "false";
                contentArray = contents.split(" ");
                fileWriter.write(contents.getBytes());
                fileWriter.close();
            }
            catch (Exception e){
                e.printStackTrace();
            }

        }

    }

    public boolean getBloodThirstByDefault() {
        return Boolean.parseBoolean(contentArray[BLOODTHIRST_BY_DEFAULT]);
    }

    public void setBloodThirstByDefault(boolean bloodThirstByDefault) {
        contentArray[BLOODTHIRST_BY_DEFAULT] = Boolean.toString(bloodThirstByDefault);
        saveChangesToFile();
    }

    private boolean saveChangesToFile()
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

    public static GameplaySettingsManager getInstance(Context context) {
        if (instance == null) {
            instance = new GameplaySettingsManager(context);
        }

        return instance;
    }

}
