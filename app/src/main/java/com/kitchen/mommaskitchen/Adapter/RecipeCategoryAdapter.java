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
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.kitchen.mommaskitchen.Activity.CategoryActivity;
import com.kitchen.mommaskitchen.R;
import com.kitchen.mommaskitchen.Utility.ContentsCategories;
import com.kitchen.mommaskitchen.Utility.ContentsRecipe;
import com.kitchen.mommaskitchen.Utility.CustomImageView;
import com.kitchen.mommaskitchen.Utility.Utils;

import java.util.ArrayList;

public class RecipeCategoryAdapter extends RecyclerView.Adapter<RecipeCategoryAdapter.ViewHolder> {

    ArrayList<ContentsCategories> categoriesArrayList;
    ArrayList<ContentsRecipe> recipeArrayList;
    Activity mActivity;

    public RecipeCategoryAdapter(ArrayList<ContentsCategories> categoriesArrayList, ArrayList<ContentsRecipe> recipeArrayList, Activity mActivity) {
        this.categoriesArrayList = categoriesArrayList;
        this.recipeArrayList = recipeArrayList;
        this.mActivity = mActivity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(mActivity.getApplicationContext());
        View view=layoutInflater.inflate(R.layout.row_recipe_wide_image,null);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int i) {

        Utils utils = new Utils();
        final ContentsCategories contentsCategories = categoriesArrayList.get(i);


        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(mActivity);
        circularProgressDrawable.setStrokeWidth(10f);
        circularProgressDrawable.setCenterRadius(60f);
        circularProgressDrawable.start();

        Glide.with(mActivity).load(contentsCategories.getCategory_image_url())
                .placeholder(circularProgressDrawable)
                .into(holder.recipe_image);


        if(contentsCategories.getCategory_name() != null){
            holder.tv_recipe_category.setText(utils.capitalize(contentsCategories.getCategory_name()));
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, CategoryActivity.class);
                intent.putExtra("recipeArrayList",recipeArrayList);
                intent.putExtra("categoryArrayList",categoriesArrayList);
                intent.putExtra("position",i);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mActivity.startActivity(intent);
            }
        });
    }


    public void updateList(ArrayList<ContentsCategories> list){
        categoriesArrayList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return categoriesArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CustomImageView recipe_image;
        TextView tv_recipe_category;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            recipe_image = (CustomImageView)itemView.findViewById(R.id.recipe_image);
            tv_recipe_category = (TextView) itemView.findViewById(R.id.tv_recipe_name);
        }
    }


}
