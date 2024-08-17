package com.example.nazanin.sheryadetnare;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by nazanin-sarrafzadeh on 5/24/2018.
 */
public class AnswerGrid extends BaseAdapter {
    private ArrayList<TextView> m = null;
    public AnswerGrid(ArrayList<TextView> b)
    {
        m = b;
    }
    @Override
    public int getCount() {
        return m.size();
    }

    @Override
    public Object getItem(int position) {
        return m.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView;
        if (convertView == null) {
            textView = m.get(position);
        } else {
            textView = (TextView) convertView;
        }
        return textView;
    }
}
