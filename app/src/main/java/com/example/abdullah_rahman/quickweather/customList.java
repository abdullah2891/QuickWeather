package com.example.abdullah_rahman.quickweather;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Abdullah_Rahman on 3/10/2016.
 */
public class customList extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] forecast;
    private final ArrayList<Integer> imageId;

    public customList(Activity context, String[] forecast, ArrayList<Integer> imageId) {
        super(context, R.layout.rowlayout, forecast);
        this.context = context;
        this.forecast = forecast;
        this.imageId = imageId;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.rowlayout, null, true);

        TextView text = (TextView) rowView.findViewById(R.id.txt);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
        text.setText(forecast[position]);
        imageView.setImageResource(imageId.get(position));
        return rowView;
    }
}

