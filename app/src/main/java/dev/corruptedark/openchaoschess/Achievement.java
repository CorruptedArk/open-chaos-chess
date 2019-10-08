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
