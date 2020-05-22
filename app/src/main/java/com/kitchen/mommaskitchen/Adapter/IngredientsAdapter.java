package com.kitchen.mommaskitchen.Adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kitchen.mommaskitchen.R;
import com.kitchen.mommaskitchen.Utility.ContentsIngredients;
import com.kitchen.mommaskitchen.Utility.ContentsRecipe;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.ViewHolder> {

    public static final String TAG = "IngredientsAdapter";
    ArrayList<ContentsIngredients> recipeArrayList;
    Activity mActivity;


    public IngredientsAdapter(ArrayList<ContentsIngredients> recipeArrayList, Activity mActivity) {
        this.recipeArrayList = recipeArrayList;
        this.mActivity = mActivity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater= LayoutInflater.from(mActivity.getApplicationContext());
        View view=layoutInflater.inflate(R.layout.row_ingredients,null);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {

        ContentsIngredients contentsIngredients = recipeArrayList.get(i);

        float ingredient_quant = contentsIngredients.getIngredient_quantity();
        String q = new DecimalFormat("#.##").format(ingredient_quant);
        holder.tv_ingredient_quantity.setText( q+ " "+ contentsIngredients.getUnit());

        holder.tv_ingredient_name.setText(contentsIngredients.getIngredient_name());



    }

    @Override
    public int getItemCount() {
        return recipeArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_ingredient_quantity,tv_ingredient_name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_ingredient_name = (TextView) itemView.findViewById(R.id.tv_ingredient_name);
            tv_ingredient_quantity = (TextView) itemView.findViewById(R.id.tv_ingredient_quantity);
        }
    }
}
