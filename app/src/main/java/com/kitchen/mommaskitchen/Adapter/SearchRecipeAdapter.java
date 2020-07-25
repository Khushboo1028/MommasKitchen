package com.kitchen.mommaskitchen.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kitchen.mommaskitchen.Activity.RecipeViewActivity;
import com.kitchen.mommaskitchen.R;
import com.kitchen.mommaskitchen.Utility.ContentsRecipe;
import com.kitchen.mommaskitchen.Utility.CustomImageView;

import java.util.ArrayList;

public class SearchRecipeAdapter extends RecyclerView.Adapter<SearchRecipeAdapter.ViewHolder>{

    public static final String TAG = "SearchAdapter";
    ArrayList<ContentsRecipe> recipeArrayList;
    Activity mActivity;

    public SearchRecipeAdapter(ArrayList<ContentsRecipe> recipeArrayList, Activity mActivity) {
        this.recipeArrayList = recipeArrayList;
        this.mActivity = mActivity;
    }

    @NonNull
    @Override
    public SearchRecipeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mActivity.getApplicationContext());
        View view = layoutInflater.inflate(R.layout.row_search_recipe, null);
        SearchRecipeAdapter.ViewHolder holder = new SearchRecipeAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final SearchRecipeAdapter.ViewHolder holder, final int i) {

        final ContentsRecipe contentsRecipe = recipeArrayList.get(i);
        Glide.with(mActivity).load(contentsRecipe.getImage_url()).placeholder(R.drawable.ic_placeholder).into(holder.recipe_image);

        holder.tv_recipe_name.setText(contentsRecipe.getRecipe_name());
        holder.tv_prep_time.setText("Prep Time: " + contentsRecipe.getPrep_time());
        holder.tv_ingredients.setText(contentsRecipe.getIngredients().size() + " ingredients");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, RecipeViewActivity.class);
                intent.putExtra("position", i);
                intent.putExtra("recipeArrayList", recipeArrayList);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mActivity.startActivity(intent);
            }
        });

    }




    @Override
    public int getItemCount() {
        return recipeArrayList.size();
    }

    public void updateList(ArrayList<ContentsRecipe> list) {
        recipeArrayList = list;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CustomImageView recipe_image;
        TextView tv_recipe_name, tv_prep_time,tv_ingredients;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            recipe_image = (CustomImageView) itemView.findViewById(R.id.recipe_image);
            tv_recipe_name = (TextView) itemView.findViewById(R.id.tv_recipe_name);
            tv_ingredients = (TextView) itemView.findViewById(R.id.tv_ingredients);
            tv_prep_time = (TextView) itemView.findViewById(R.id.tv_prep_time);
        }
    }
}
