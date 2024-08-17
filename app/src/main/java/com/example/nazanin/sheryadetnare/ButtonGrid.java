package com.example.nazanin.sheryadetnare;

/**
 * Created by nazanin-sarrafzadeh on 3/26/2018.
 */

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import java.util.ArrayList;

public class ButtonGrid extends BaseAdapter {
    private ArrayList<Button> mButtons = null;
    public ButtonGrid(ArrayList<Button> b)
    {
        mButtons = b;
    }
    @Override
    public int getCount() {
        return mButtons.size();
    }
    @Override
    public Object getItem(int position) {
        return (Object) mButtons.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Button button;
        if (convertView == null) {
            button = mButtons.get(position);
        } else {
            button = (Button) convertView;
        }
        return button;
    }
}
