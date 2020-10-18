package com.kitchen.mommaskitchen.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;


import com.bumptech.glide.Glide;
import com.kitchen.mommaskitchen.Activity.RecipeViewActivity;
import com.kitchen.mommaskitchen.R;
import com.kitchen.mommaskitchen.Utility.ContentsRecipe;
import com.kitchen.mommaskitchen.Utility.CustomImageView;

import java.util.ArrayList;

public class LatestReleaseAdapter extends RecyclerView.Adapter<LatestReleaseAdapter.ViewHolder> {

    ArrayList<ContentsRecipe> recipeArrayList;
    Activity mActivity;

    public LatestReleaseAdapter(ArrayList<ContentsRecipe> recipeArrayList, Activity mActivity) {
        this.recipeArrayList = recipeArrayList;
        this.mActivity = mActivity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(mActivity.getApplicationContext());
        View view=layoutInflater.inflate(R.layout.row_latest_recipe,null);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int i) {

        final ContentsRecipe contentsRecipe = recipeArrayList.get(i);

        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(mActivity);
        circularProgressDrawable.setStrokeWidth(10f);
        circularProgressDrawable.setCenterRadius(60f);
        circularProgressDrawable.setColorSchemeColors(R.color.colorPrimary);
        circularProgressDrawable.start();

        Glide.with(mActivity).load(contentsRecipe.getImage_url()).placeholder(circularProgressDrawable).into(holder.recipe_image);
        holder.tv_recipe_name.setText(contentsRecipe.getRecipe_name());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, RecipeViewActivity.class);
                intent.putExtra("recipeArrayList",recipeArrayList);
                intent.putExtra("position",i);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mActivity.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return recipeArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        CustomImageView recipe_image;
        TextView tv_recipe_name;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            recipe_image = (CustomImageView)itemView.findViewById(R.id.recipe_image);
            tv_recipe_name = (TextView) itemView.findViewById(R.id.tv_recipe_name);
        }
    }
}
