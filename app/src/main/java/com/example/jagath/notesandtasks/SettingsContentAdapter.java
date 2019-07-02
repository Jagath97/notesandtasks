package com.example.jagath.notesandtasks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by jagath on 27/02/2018.
 */

public class SettingsContentAdapter extends BaseAdapter {
    private Context context;
    private List<SettingsContent> contentsList;
    @Override
    public int getCount() {
        return contentsList.size();
    }

    public SettingsContentAdapter(Context context, List<SettingsContent> contentsList) {
        this.context=context;
        this.contentsList=contentsList;
    }

    @Override
    public Object getItem(int position) {
        return contentsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       @SuppressLint("ViewHolder") View v=View.inflate(context,R.layout.settings_content,null);
        TextView title=v.findViewById(R.id.apperance);
        TextView values=v.findViewById(R.id.colorm);
        title.setText(contentsList.get(position).getTitle());
        values.setText(contentsList.get(position).getValue());
        v.setTag(contentsList.get(position));
        return v;
    }
}
