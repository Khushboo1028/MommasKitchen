package com.kitchen.mommaskitchen.Fragment;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.kitchen.mommaskitchen.R;
import com.kitchen.mommaskitchen.Adapter.RecipeAdapter;
import com.kitchen.mommaskitchen.Utility.ContentsRecipe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;

public class SavedFragment extends Fragment {

    public static final String TAG = "SavedFragment";
    private RecyclerView recyclerView;
    private RecipeAdapter recipeAdapter;
    private ArrayList<ContentsRecipe> recipeArrayList;
    private ArrayList<ContentsRecipe> savedRecipesList;
    private ArrayList<String> savedRecipeIDArrayList;
    EditText et_search;
    TextView top_heading;

    ListenerRegistration listenerRegistration;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore db;
    View view;

    String recipe_name;
    Map <String, Map<String,Object>> ingredients;
    ArrayList<String> directions;
    Timestamp date_created;
    Map<String,Object> portion;
    ArrayList<ContentsRecipe> latestRecipes;
    String prep_time;

    String date_viewFormat,recipe_image_url,category_id,video_url;

    LottieAnimationView lottieAnimationView;
    TextView tv_saved_recipe;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_saved, container, false);
        init();





        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString().toLowerCase());

            }
        });

        getSavedRecipes(getActivity());


        return view;
    }

    private void init(){

        savedRecipesList = new ArrayList<>();
        recipeArrayList = new ArrayList<>();
        savedRecipeIDArrayList = new ArrayList<>();

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_vertical);
        recyclerView.setHasFixedSize(true); //so it doesn't matter if element size increases or decreases


        et_search = (EditText) view.findViewById(R.id.et_search);
        tv_saved_recipe = (TextView) view.findViewById(R.id.tv_saved_recipe);
        top_heading = (TextView) view.findViewById(R.id.top_heading);
        top_heading.setVisibility(View.GONE);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        lottieAnimationView = view.findViewById(R.id.lottieAnimationView);


    }

    private void filter(String text) {
        ArrayList<ContentsRecipe> temp = new ArrayList();
        for (ContentsRecipe d : savedRecipesList) {
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches

            if (d.getRecipe_name().toLowerCase().contains(text.toLowerCase()) ){
                temp.add(d);
            }
        }
        //update recyclerview
        recipeAdapter.updateList(temp);

    }

    public void getSavedRecipes(final Activity mActivity){
        savedRecipeIDArrayList.clear();
        if(firebaseUser!=null){
            db.collection(mActivity.getString(R.string.users)).document(firebaseUser.getUid())
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                            if(e!=null){
                                Log.i(TAG,"An error occurred in recovering saved ID'S " +e.getMessage());
                            }else{

                                if (snapshot != null && snapshot.exists()) {
                                    Log.d(TAG, "Current data: " + snapshot.getData());

                                    savedRecipeIDArrayList = (ArrayList<String>) snapshot.get(mActivity.getString(R.string.saved_recipe_id));

                                    final CollectionReference docRef = db.collection(mActivity.getString(R.string.recipes));
                                    listenerRegistration = docRef.orderBy(mActivity.getString(R.string.date_created), Query.Direction.DESCENDING)
                                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                                @Override
                                                public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                                                    if (e != null) {
                                                        Log.d(TAG, "Error:" + e.getMessage());
                                                    }else{

                                                        if (snapshots.getDocuments().isEmpty()) {
                                                            Log.i(TAG, "No Recipes");
                                                        }else{
                                                            recipeArrayList.clear();

                                                            for (final QueryDocumentSnapshot doc : snapshots) {


                                                                if(doc.getTimestamp(mActivity.getString(R.string.date_created))!=null){
                                                                    date_created = doc.getTimestamp(mActivity.getString(R.string.date_created));
                                                                    SimpleDateFormat sfd_viewFormat = new SimpleDateFormat("MMMM d, yyyy");
                                                                    date_viewFormat = sfd_viewFormat.format(date_created.toDate());
                                                                }

                                                                if(doc.get(mActivity.getString(R.string.ingredients)) != null){
                                                                    ingredients = (Map<String, Map<String, Object>>) doc.get(mActivity.getString(R.string.ingredients));

                                                                }

                                                                if(doc.get(mActivity.getString(R.string.image_url)) != null){
                                                                    recipe_image_url = doc.getString(mActivity.getString(R.string.image_url));

                                                                }

                                                                if(doc.get(mActivity.getString(R.string.recipe_name)) != null){
                                                                    recipe_name = doc.getString(mActivity.getString(R.string.recipe_name));
                                                                }

                                                                if(doc.get(mActivity.getString(R.string.prep_time)) != null){
                                                                    prep_time = doc.getString(mActivity.getString(R.string.prep_time));
                                                                }

                                                                if(doc.get(mActivity.getString(R.string.directions)) !=null ){
                                                                    directions = (ArrayList<String>) doc.get(mActivity.getString(R.string.directions));
                                                                }

                                                                if(doc.get(mActivity.getString(R.string.portion)) != null){
                                                                    portion = (Map<String, Object>) doc.get(mActivity.getString(R.string.portion));
                                                                }


                                                                if(doc.get(mActivity.getString(R.string.category_id)) != null){
                                                                    category_id = doc.getString(mActivity.getString(R.string.category_id));
                                                                }

                                                                if(doc.get(mActivity.getString(R.string.video_url)) != null){
                                                                    video_url = doc.getString(mActivity.getString(R.string.video_url));
                                                                }




                                                                recipeArrayList.add(new ContentsRecipe(
                                                                        recipe_image_url,
                                                                        recipe_name,
                                                                        prep_time,
                                                                        ingredients,
                                                                        directions,
                                                                        date_viewFormat,
                                                                        portion,
                                                                        category_id,
                                                                        doc.getId(),
                                                                        video_url

                                                                ));





                                                            }

                                                            for(int i = 0;i<recipeArrayList.size();i++){

                                                                if(savedRecipeIDArrayList!=null && savedRecipeIDArrayList.contains(recipeArrayList.get(i).getDocument_id())){
                                                                    savedRecipesList.add(recipeArrayList.get(i));
                                                                }
                                                            }

                                                            recipeAdapter = new RecipeAdapter(savedRecipesList,getActivity());
                                                            recyclerView.setAdapter(recipeAdapter);

                                                            if(savedRecipesList.isEmpty()){
                                                                lottieAnimationView.setVisibility(View.VISIBLE);
                                                                tv_saved_recipe.setText("Oops! No Saved Recipes!");
                                                            }

                                                        }
                                                    }


                                                }
                                            });
                                } else {
                                    Log.d(TAG, "Current data: null");
                                }



                            }

                        }
                    });

        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if(listenerRegistration != null){
            listenerRegistration = null;
        }
    }

}
