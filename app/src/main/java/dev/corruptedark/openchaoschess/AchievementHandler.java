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

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class AchievementHandler {

    private Context context;

    private static int currentIndex = 0;
    public static final Achievement STARTED_GAME = new Achievement(currentIndex++,1,"Started Game","You opened the game! Congratulations!");
    public static final Achievement WON_A_GAME = new Achievement(currentIndex++,1,"Noice Dude!","You won a game!");
    public static final Achievement LOST_A_GAME = new Achievement(currentIndex++, 1,"Bruh.", "You lost a game, I had such high hopes for you.");
    public static final Achievement TIED_A_GAME = new Achievement(currentIndex++, 1,"Breaking Even","Tied a game, I'm honestly not sure how you did it.");
    public static final Achievement WON_10_GAMES = new Achievement(currentIndex++, 10, "Winner", "Won ten games.");
    public static final Achievement LOST_10_GAMES = new Achievement(currentIndex++,10,"Loser","Lost ten games.");
    public static final Achievement TIED_10_GAMES = new Achievement(currentIndex++,10,"Exceptionally Average","Tied ten games. I'm not sure how I feel about you.");
    public static final Achievement PLAYED_10_GAMES = new Achievement(currentIndex++,10,"Hooked","Played ten games.");
    public static final Achievement PLAYED_50_GAMES = new Achievement(currentIndex++,50,"Chaos Junkie","Played 50 games. You're developing an unhealthy habit.");
    public static final Achievement PLAYED_100_GAMES = new Achievement(currentIndex++,100,"Get Help","Played 100 games. Seriously, you have a problem.");
    public static final Achievement WON_50_GAMES = new Achievement(currentIndex++,50,"Master of Chaos","Won 50 games. Impressive.");
    public static final Achievement TIED_50_GAMES = new Achievement(currentIndex++,50,"Master of Balance","Tied 50 games. I'm as confused as you are.");
    public static final Achievement LOST_50_GAMES = new Achievement(currentIndex++,50,"Master of Failure","Lost 50 times. You must've tried to be this bad.");
    public static final Achievement OPENED_ABOUT = new Achievement(currentIndex++,1,"Informed Player","Opened the \"About\" page. Thanks for caring. I hope it was worth the read.");
    public static final Achievement UNTOUCHABLE = new Achievement(currentIndex++,1,"Untouchable","Won a game without losing any pieces. You deserve a round of applause.");
    public static final Achievement SLAUGHTERED = new Achievement(currentIndex++,1,"Slaughtered","Lost without capturing a single piece. Ouch. I hope you don't uninstall the game for this.");
    public static final Achievement SECRET_KNOCK = new Achievement(currentIndex++,1,"Secret Knock","Found a secret by using the secret knock.");
    public static final Achievement HORSING_AROUND = new Achievement(currentIndex++,1,"Horsing Around","Accessed the Knights Only mode. Good luck finishing a match.");

    public static final int ACHIEVEMENT_COUNT = 18;

    private static final String FILENAME = "achievements.txt";

    private static AchievementHandler instance;

    private File achievementsFile;

    private FileInputStream fileReader;

    private FileOutputStream fileWriter;

    byte[] bytes;

    private  Achievement[] achievementList;
    private int[] achievementValueList;

    private AchievementHandler(Context context) {

        this.context = context.getApplicationContext();

        achievementList = new Achievement[ACHIEVEMENT_COUNT];

        achievementList[STARTED_GAME.getAchievementId()] = STARTED_GAME;
        achievementList[WON_A_GAME.getAchievementId()] = WON_A_GAME;
        achievementList[LOST_A_GAME.getAchievementId()] = LOST_A_GAME;
        achievementList[TIED_A_GAME.getAchievementId()] = TIED_A_GAME;
        achievementList[WON_10_GAMES.getAchievementId()] = WON_10_GAMES;
        achievementList[LOST_10_GAMES.getAchievementId()] = LOST_10_GAMES;
        achievementList[TIED_10_GAMES.getAchievementId()] = TIED_10_GAMES;
        achievementList[PLAYED_10_GAMES.getAchievementId()] = PLAYED_10_GAMES;
        achievementList[PLAYED_50_GAMES.getAchievementId()] = PLAYED_50_GAMES;
        achievementList[PLAYED_100_GAMES.getAchievementId()] = PLAYED_100_GAMES;
        achievementList[WON_50_GAMES.getAchievementId()] = WON_50_GAMES;
        achievementList[TIED_50_GAMES.getAchievementId()] = TIED_50_GAMES;
        achievementList[LOST_50_GAMES.getAchievementId()] = LOST_50_GAMES;
        achievementList[OPENED_ABOUT.getAchievementId()] = OPENED_ABOUT;
        achievementList[UNTOUCHABLE.getAchievementId()] = UNTOUCHABLE;
        achievementList[SLAUGHTERED.getAchievementId()] = SLAUGHTERED;
        achievementList[SECRET_KNOCK.getAchievementId()] = SECRET_KNOCK;
        achievementList[HORSING_AROUND.getAchievementId()] = HORSING_AROUND;

        achievementValueList = new int[ACHIEVEMENT_COUNT];

        achievementsFile = new File(this.context.getFilesDir(),FILENAME);

        if(achievementsFile.exists()) {

            try {
                fileReader = new FileInputStream(achievementsFile);
                bytes = new byte[(int) achievementsFile.length()];
                fileReader.read(bytes);
                fileReader.close();
                String contents = new String(bytes);
                String[] contentArray = contents.split("\n");

                for(int i = 0; i < contentArray.length; i++)
                {
                    achievementValueList[i] = Integer.parseInt(contentArray[i]);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }

        }
        else {
            try {

                String content = "";

                for(int i = 0; i < ACHIEVEMENT_COUNT; i++) {
                    achievementValueList[i] = 0;
                    if(i == ACHIEVEMENT_COUNT - 1)
                    {
                        content += "0";
                    }
                    else{
                        content += "0\n";
                    }

                }

                fileWriter = new FileOutputStream(achievementsFile, false);
                fileWriter.write(content.getBytes());
                fileWriter.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

    }

    public static AchievementHandler getInstance(Context context)
    {
        if(instance == null)
            instance = new AchievementHandler(context);

        return instance;
    }

    public void incrementInMemory(Achievement achievement) {
        if(achievementValueList[achievement.getAchievementId()] < achievement.getThreshold())
            achievementValueList[achievement.getAchievementId()]++;
    }

    public void saveValues()
    {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < achievementValueList.length; i++)
        {
            if(i == achievementValueList.length - 1)
                builder.append(achievementValueList[i]);
            else
                builder.append(achievementValueList[i]).append("\n");
        }

        try {
            fileWriter = new FileOutputStream(achievementsFile, false);
            fileWriter.write(builder.toString().getBytes());
            fileWriter.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isUnlocked(Achievement achievement) {
        return achievementValueList[achievement.getAchievementId()] >= achievement.getThreshold();
    }

    public Achievement findAchievementById(int id)
    {
        return achievementList[id];
    }

    public ArrayList<Achievement> getList()
    {
        return new ArrayList<>(Arrays.asList(achievementList));
    }
}
