package dev.corruptedark.openchaoschess;

import androidx.annotation.NonNull;

public class Achievement {
    private int id;
    private int threshold;
    private String title;
    private String description;

    public Achievement(int id, int threshold, @NonNull String title, @NonNull String description)
    {
        this.id = id;
        this.threshold = threshold;
        this.title = title;
        this.description = description;
    }

    public int getAchievementId()
    {
        return id;
    }

    public int getThreshold()
    {
        return threshold;
    }

    @NonNull
    public String getTitle()
    {
        return title;
    }

    @NonNull
    public String getDescription()
    {
        return description;
    }

}
