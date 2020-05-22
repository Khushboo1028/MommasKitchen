package com.kitchen.mommaskitchen.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kitchen.mommaskitchen.R;
import com.kitchen.mommaskitchen.Utility.ContentsIngredients;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class DirectionsAdapter extends RecyclerView.Adapter<DirectionsAdapter.ViewHolder> {

    public static final String TAG = "IngredientsAdapter";
    ArrayList<String> directionsArrayList;
    Activity mActivity;


    public DirectionsAdapter(ArrayList<String> directionsArrayList, Activity mActivity) {
        this.directionsArrayList = directionsArrayList;
        this.mActivity = mActivity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater= LayoutInflater.from(mActivity.getApplicationContext());
        View view=layoutInflater.inflate(R.layout.row_directions,null);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {

        holder.tv_direction.setText(directionsArrayList.get(i));




    }

    @Override
    public int getItemCount() {
        return directionsArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_direction;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_direction = (TextView) itemView.findViewById(R.id.tv_direction);

        }
    }
}
