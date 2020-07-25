package com.kitchen.mommaskitchen.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.kitchen.mommaskitchen.Activity.SearchActivity;
import com.kitchen.mommaskitchen.Adapter.LatestReleaseAdapter;
import com.kitchen.mommaskitchen.R;
import com.kitchen.mommaskitchen.Adapter.RecipeCategoryAdapter;
import com.kitchen.mommaskitchen.Utility.ContentsCategories;
import com.kitchen.mommaskitchen.Utility.ContentsRecipe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RecipeFragment extends Fragment {

    public static final String TAG = "RecipeFragment";
    private RecyclerView recyclerView;
    private LatestReleaseAdapter latestReleaseAdapter;
    ArrayList<String> savedRecipeArrayList;

    private RecyclerView categoryRecyclerView;
    private RecipeCategoryAdapter recipeCategoryAdapter;
    EditText et_search;
    RelativeLayout relative_layout;
    View view;



    FirebaseFirestore db;
    ListenerRegistration listenerRegistration;
    ArrayList<ContentsRecipe> recipeArrayList;
    String recipe_name;
    Map <String, Map<String,Object>> ingredients;
    ArrayList<String> directions;
    Timestamp date_created;
    Map<String,Object> portion;
    ArrayList<ContentsRecipe> latestRecipes;
    String category_id,category_name,category_image_url;
    String prep_time;

    String date_viewFormat,recipe_image_url, video_url;

    ArrayList<ContentsCategories> categoriesArrayList;
    EditText searchView;

    NestedScrollView nestedScrollView;

    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_recipe, container, false);
        init();



        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                getFragmentManager()
//                        .beginTransaction()
//                        .add(R.id.fragment_container, new SearchFragment())
//                        .commit();

                Intent intent = new Intent(getContext(), SearchActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(0,0);
            }
        });

        getSavedRecipes(getActivity());
        getCategories();





        return view;
    }

    private void init(){

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        recipeArrayList = new ArrayList<>();
        latestRecipes = new ArrayList<>();
        categoriesArrayList = new ArrayList<>();
        savedRecipeArrayList = new ArrayList<>();
        recyclerView.setHasFixedSize(true); //so it doesn't matter if element size increases or decreases
        latestReleaseAdapter = new LatestReleaseAdapter(latestRecipes,getActivity());
        recyclerView.setAdapter(latestReleaseAdapter);
        searchView = view.findViewById(R.id.et_search);
        searchView.setFocusable(false);

        categoryRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_vertical);
        categoryRecyclerView.setHasFixedSize(true); //so it doesn't matter if element size increases or decreases
        recipeCategoryAdapter = new RecipeCategoryAdapter(categoriesArrayList,recipeArrayList,getActivity());
        categoryRecyclerView.setAdapter(recipeCategoryAdapter);

        et_search = (EditText) view.findViewById(R.id.et_search);

        nestedScrollView = (NestedScrollView) view.findViewById(R.id.nestedScrollView);


        relative_layout = (RelativeLayout) view.findViewById(R.id.relative_layout);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

    }


    public void getRecipes(final Activity mActivity){

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


                                    Boolean isSaved;
                                    if(savedRecipeArrayList.contains(doc.getId())){
                                        isSaved = true;
                                    }else{
                                        isSaved = false;
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
                                            video_url,
                                            isSaved

                                    ));


                                    latestRecipes.clear();
                                    for(int i=0; i<recipeArrayList.size();i++){
                                        if(latestRecipes.size()<9){
                                            latestRecipes.add(recipeArrayList.get(i));
                                        }
                                    }

                                    Log.i(TAG,"size: " +latestRecipes.size());
                                    latestReleaseAdapter.notifyDataSetChanged();



                                }

                            }
                        }


                    }
                });

    }

    public void getCategories(){
        CollectionReference categoryRef = db.collection(getString(R.string.categories));

        listenerRegistration = categoryRef.orderBy(getString(R.string.date_created), Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                        if(e!=null){
                            Log.i(TAG,"Error getting categories " + e.getLocalizedMessage());
                        }else{

                            categoriesArrayList.clear();
                            for(QueryDocumentSnapshot doc:snapshots){

                                category_name = doc.getString(getString(R.string.category_name));
                                category_image_url = doc.getString(getString(R.string.category_image_url));
                                categoriesArrayList.add(new ContentsCategories(category_name,category_image_url,doc.getId()));
                            }

                            recipeCategoryAdapter.notifyDataSetChanged();
                        }
                    }
                });

    }

    public void getSavedRecipes( final Activity mActivity) {

        if (firebaseUser != null) {
            listenerRegistration = db.collection(mActivity.getString(R.string.users)).document(firebaseUser.getUid())
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.i(TAG, "An error occurred in recovering saved ID'S " + e.getMessage());
                            } else {

                                if (snapshot != null && snapshot.exists()) {
                                    Log.d(TAG, "Current data: " + snapshot.getData());

                                    savedRecipeArrayList = (ArrayList<String>) snapshot.get(mActivity.getString(R.string.saved_recipe_id));
                                    getRecipes(mActivity);
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
