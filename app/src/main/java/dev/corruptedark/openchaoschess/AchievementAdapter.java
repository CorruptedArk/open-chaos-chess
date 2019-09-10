package dev.corruptedark.openchaoschess;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class AchievementAdapter extends ArrayAdapter {

    private ArrayList<Achievement> achievementsList;
    private AchievementHandler achievementHandler;
    private int listResourceId;

    private int background;
    private int textColor;

    public AchievementAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Achievement> objects, int background, int textColor) {
        super(context, resource, objects);
        achievementsList = objects;
        achievementHandler = AchievementHandler.getInstance(context);
        listResourceId = resource;
        this.background = background;
        this.textColor = textColor;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(listResourceId, null);
        TextView titleView = (TextView) view.findViewById(R.id.achievement_item_title);
        TextView descriptionView = (TextView) view.findViewById(R.id.achievement_item_description);
        titleView.setText(achievementsList.get(position).getTitle());
        descriptionView.setText(achievementsList.get(position).getDescription());

        view.setBackgroundColor(background);
        titleView.setTextColor(textColor);
        descriptionView.setTextColor(textColor);


        if(achievementHandler.isUnlocked(achievementsList.get(position))) {
            view.setVisibility(View.VISIBLE);
        }
        else
        {
            view.setVisibility(View.GONE);
        }

        return view;
    }

    @Nullable
    @Override
    public Achievement getItem(int position) {
        return achievementsList.get(position);
    }
}
