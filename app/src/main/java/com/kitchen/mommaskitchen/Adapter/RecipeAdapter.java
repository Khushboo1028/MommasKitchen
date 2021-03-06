package com.kitchen.mommaskitchen.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.kitchen.mommaskitchen.Activity.RecipeViewActivity;
import com.kitchen.mommaskitchen.R;
import com.kitchen.mommaskitchen.Utility.ContentsRecipe;
import com.kitchen.mommaskitchen.Utility.CustomImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    public static final String TAG = "RecipeAdapter";
    ArrayList<ContentsRecipe> recipeArrayList;
    Activity mActivity;

    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    ArrayList<String> savedRecipeArrayList;
    ListenerRegistration listenerRegistration;
    Boolean savedFragment;


    public RecipeAdapter(ArrayList<ContentsRecipe> recipeArrayList, Activity mActivity, Boolean savedFragment ) {
        this.recipeArrayList = recipeArrayList;
        this.mActivity = mActivity;
        this.savedFragment = savedFragment;
        savedRecipeArrayList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mActivity.getApplicationContext());
        View view = layoutInflater.inflate(R.layout.row_recipe_saved, null);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int i) {

        final ContentsRecipe contentsRecipe = recipeArrayList.get(i);
        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(mActivity);
        circularProgressDrawable.setStrokeWidth(10f);
        circularProgressDrawable.setCenterRadius(60f);
        circularProgressDrawable.start();

        Glide.with(mActivity).load(contentsRecipe.getImage_url()).placeholder(circularProgressDrawable).into(holder.recipe_image);

        holder.tv_recipe_name.setText(contentsRecipe.getRecipe_name());

        if (contentsRecipe.getSaved()) {
            holder.bookmark.setImageResource(R.drawable.ic_bookmark_checked);

        } else {
            holder.bookmark.setImageResource(R.drawable.ic_bookmark_unchecked);
        }



        holder.bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG,"isSaved: "+contentsRecipe.getSaved());
                if (contentsRecipe.getSaved()) {
                    holder.bookmark.setImageResource(R.drawable.ic_bookmark_unchecked);
                    saveRecipe(contentsRecipe, false);
                    contentsRecipe.setSaved(false);

                    if(savedFragment){
                        removeAt(i);
                    }


                } else {
                    contentsRecipe.setSaved(true);
                    holder.bookmark.setImageResource(R.drawable.ic_bookmark_checked);
                    saveRecipe(contentsRecipe, true);
                }
            }
        });


        holder.recipe_image.setOnClickListener(new View.OnClickListener() {
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

    public void removeAt(int position) {
        recipeArrayList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, recipeArrayList.size());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CustomImageView recipe_image;
        TextView tv_recipe_name;
        ImageView bookmark;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            recipe_image = (CustomImageView) itemView.findViewById(R.id.recipe_image);
            tv_recipe_name = (TextView) itemView.findViewById(R.id.tv_recipe_name);
            bookmark = (ImageView) itemView.findViewById(R.id.bookmark);
        }
    }

    private void saveRecipe(ContentsRecipe contentsRecipe, Boolean is_bookmarked) {

        if (firebaseUser != null) {

            final Map<String, Object> docData = new HashMap<>();
            if (is_bookmarked) {
                docData.put(mActivity.getString(R.string.saved_recipe_id), FieldValue.arrayUnion(contentsRecipe.getDocument_id()));
            } else {
                docData.put(mActivity.getString(R.string.saved_recipe_id), FieldValue.arrayRemove(contentsRecipe.getDocument_id()));

            }

            DocumentReference docRef = db.collection(mActivity.getString(R.string.users)).document(firebaseUser.getUid());

            docRef.update(docData).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.i(TAG, "Recipe is updated in users favourites");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i(TAG, "Recipe could not be saved in users favourites " + e.getMessage());
                }
            });
        }
    }




}
