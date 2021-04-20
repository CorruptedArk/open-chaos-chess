package dev.corruptedark.openchaoschess;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.ArrayList;

public class GameplaySettingsManager {

    private static volatile GameplaySettingsManager instance;

    private File settingsFile;
    private InputStream fileReader;
    private OutputStream fileWriter;
    private byte[] bytes;
    private ArrayList<String> contentArray;

    private final int BLOODTHIRST_BY_DEFAULT = 0;
    private final int AGGRESSIVE_COMPUTER = 1;
    private final int SMART_COMPUTER = 5;
    private final int HANDICAP_ENABLED = 2;
    private final int CHESS960 = 3;
    private final int QUEENS_ATTACK = 4;

    private final String DELIMITER = " ";

    private GameplaySettingsManager(Context context) {
        settingsFile = new File(context.getApplicationContext().getFilesDir(),context.getString(R.string.gameplay_settings_file));
        if(settingsFile.exists()) {
            try{
                fileReader = new FileInputStream(settingsFile);
                bytes = new byte[(int)settingsFile.length()];
                fileReader.read(bytes);
                fileReader.close();
                String contents = new String(bytes);
                contentArray = new ArrayList<>(Arrays.asList(contents.split(DELIMITER)));
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        else {
            try {
                fileWriter = new FileOutputStream(settingsFile,false);
                String contents = "false false false";
                contentArray = new ArrayList<>(Arrays.asList(contents.split(DELIMITER)));
                fileWriter.write(contents.getBytes());
                fileWriter.close();
            }
            catch (Exception e){
                e.printStackTrace();
            }

        }

    }

    public boolean getBloodThirstByDefault() {
        return Boolean.parseBoolean(contentArray.get(BLOODTHIRST_BY_DEFAULT));
    }

    public void setBloodThirstByDefault(boolean bloodThirstByDefault) {
        contentArray.set(BLOODTHIRST_BY_DEFAULT, Boolean.toString(bloodThirstByDefault));
        saveChangesToFile();
    }

    public boolean getAggressiveComputers() {
        boolean aggressiveComputer;

        if (contentArray.size() < AGGRESSIVE_COMPUTER + 1) {
            int sizeDiff = AGGRESSIVE_COMPUTER + 1 - contentArray.size();
            for (int i = 0; i < sizeDiff; i++) {
                contentArray.add("false");
            }
            saveChangesToFile();
            aggressiveComputer = false;
        }
        else {
            aggressiveComputer = Boolean.parseBoolean(contentArray.get(AGGRESSIVE_COMPUTER));
        }

        return aggressiveComputer;
    }

    public void setAggressiveComputer(boolean aggressiveComputer) {
        if (contentArray.size() < AGGRESSIVE_COMPUTER + 1) {
            int sizeDiff = AGGRESSIVE_COMPUTER + 1 - contentArray.size();
            for (int i = 0; i < sizeDiff; i++) {
                contentArray.add("false");
            }
        }

        contentArray.set(AGGRESSIVE_COMPUTER, Boolean.toString(aggressiveComputer));
        saveChangesToFile();
    }

    public boolean getHandicapOnlyBishopsKnightsEnabled() {
        boolean handicapEnabled;

        if (contentArray.size() < HANDICAP_ENABLED + 1) {
            int sizeDiff = HANDICAP_ENABLED + 1 - contentArray.size();
            for (int i = 0; i < sizeDiff; i++) {
                contentArray.add("false");
            }
            saveChangesToFile();
            handicapEnabled = false;
        }
        else {
            handicapEnabled = Boolean.parseBoolean(contentArray.get(HANDICAP_ENABLED));
        }

        return handicapEnabled;
    }

    public void setHandicapOnlyBishopsKnightsEnabled(boolean handicapEnabled) {
        if (contentArray.size() < HANDICAP_ENABLED + 1) {
            int sizeDiff = HANDICAP_ENABLED + 1 - contentArray.size();
            for (int i = 0; i < sizeDiff; i++) {
                contentArray.add("false");
            }
        }

        contentArray.set(HANDICAP_ENABLED, Boolean.toString(handicapEnabled));
        saveChangesToFile();
    }

    public boolean getChess960() {
        boolean chess960;

        if (contentArray.size() < CHESS960 + 1) {
            int sizeDiff = CHESS960 + 1 - contentArray.size();
            for (int i = 0; i < sizeDiff; i++) {
                contentArray.add("false");
            }
            saveChangesToFile();
            chess960 = false;
        }
        else {
            chess960 = Boolean.parseBoolean(contentArray.get(CHESS960));
        }

        return chess960;
    }

    public void setChess960(boolean chess960) {
        if (contentArray.size() < CHESS960 + 1) {
            int sizeDiff = CHESS960 + 1 - contentArray.size();
            for (int i = 0; i < sizeDiff; i++) {
                contentArray.add("false");
            }
        }

        contentArray.set(CHESS960, Boolean.toString(chess960));
        saveChangesToFile();
    }

    public boolean getHandicapQueensAttackEnabled() {
        boolean handicapEnabled;

        if (contentArray.size() < QUEENS_ATTACK + 1) {
            int sizeDiff = QUEENS_ATTACK + 1 - contentArray.size();
            for (int i = 0; i < sizeDiff; i++) {
                contentArray.add("false");
            }
            saveChangesToFile();
            handicapEnabled = false;
        }
        else {
            handicapEnabled = Boolean.parseBoolean(contentArray.get(QUEENS_ATTACK));
        }

        return handicapEnabled;
    }

    public void setHandicapQueensAttackEnabled(boolean handicapEnabled) {
        if (contentArray.size() < QUEENS_ATTACK + 1) {
            int sizeDiff = QUEENS_ATTACK + 1 - contentArray.size();
            for (int i = 0; i < sizeDiff; i++) {
                contentArray.add("false");
            }
        }

        contentArray.set(QUEENS_ATTACK, Boolean.toString(handicapEnabled));
        saveChangesToFile();
    }

    public boolean getSmartComputer() {
        boolean improvedAI;

        if (contentArray.size() < SMART_COMPUTER + 1) {
            int sizeDiff = SMART_COMPUTER + 1 - contentArray.size();
            for (int i = 0; i < sizeDiff; i++) {
                contentArray.add("false");
            }
            saveChangesToFile();
            improvedAI = false;
        }
        else {
            improvedAI = Boolean.parseBoolean(contentArray.get(SMART_COMPUTER));
        }

        return improvedAI;
    }

    public void setSmartComputer(boolean smartComputer) {
        if (contentArray.size() < SMART_COMPUTER + 1) {
            int sizeDiff = SMART_COMPUTER + 1 - contentArray.size();
            for (int i = 0; i < sizeDiff; i++) {
                contentArray.add("false");
            }
        }

        contentArray.set(SMART_COMPUTER, Boolean.toString(smartComputer));
        saveChangesToFile();
    }

    /**
     * @return true if changes were saved successfully and false otherwise
     */
    private boolean saveChangesToFile() {
        boolean successful;
        StringBuilder contents = new StringBuilder();

        for (String setting : contentArray) {
            contents.append(setting).append(" ");
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
