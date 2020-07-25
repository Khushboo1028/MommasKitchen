package com.kitchen.mommaskitchen.Fragment;

import android.app.Activity;
import android.content.Context;
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

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.kitchen.mommaskitchen.Adapter.LatestReleaseAdapter;
import com.kitchen.mommaskitchen.Adapter.SearchRecipeAdapter;
import com.kitchen.mommaskitchen.R;
import com.kitchen.mommaskitchen.Utility.ContentsCategories;
import com.kitchen.mommaskitchen.Utility.ContentsRecipe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;

public class SearchFragment extends Fragment {

    public static final String TAG = "SearchFragment";
    private View view;


    private EditText et_search;

    private RecyclerView recyclerView;
    private SearchRecipeAdapter searchRecipeAdapter;

    FirebaseFirestore db;
    ListenerRegistration listenerRegistration;
    ArrayList<ContentsRecipe> recipeArrayList;

    String recipe_name, category_id, prep_time, date_viewFormat, recipe_image_url, video_url;

    Map<String, Map<String, Object>> ingredients;
    ArrayList<String> directions;
    Timestamp date_created;
    Map<String, Object> portion;


    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_search, container, false);

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

        getRecipes(getActivity());


        return view;
    }

    private void init() {
        et_search = (EditText) view.findViewById(R.id.et_search);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_search);
        recyclerView.setHasFixedSize(true);
        recipeArrayList = new ArrayList<>();

        searchRecipeAdapter = new SearchRecipeAdapter(recipeArrayList,getActivity());
        recyclerView.setAdapter(searchRecipeAdapter);

        db = FirebaseFirestore.getInstance();


    }

    private void filter(String text) {
        ArrayList<ContentsRecipe> temp = new ArrayList();
        for (ContentsRecipe d : recipeArrayList) {
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches

            if (d.getRecipe_name().toLowerCase().contains(text.toLowerCase())) {
                temp.add(d);
            }
        }
        //update recyclerview
        searchRecipeAdapter.updateList(temp);

    }

    public void getRecipes(final Activity mActivity) {

        final CollectionReference docRef = db.collection(mActivity.getString(R.string.recipes));

        listenerRegistration = docRef.orderBy(mActivity.getString(R.string.date_created), Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.d(TAG, "Error:" + e.getMessage());
                        } else {

                            if (snapshots.getDocuments().isEmpty()) {
                                Log.i(TAG, "No Recipes");
                            } else {
                                recipeArrayList.clear();

                                for (final QueryDocumentSnapshot doc : snapshots) {


                                    if (doc.getTimestamp(mActivity.getString(R.string.date_created)) != null) {
                                        date_created = doc.getTimestamp(mActivity.getString(R.string.date_created));
                                        SimpleDateFormat sfd_viewFormat = new SimpleDateFormat("MMMM d, yyyy");
                                        date_viewFormat = sfd_viewFormat.format(date_created.toDate());
                                    }

                                    if (doc.get(mActivity.getString(R.string.ingredients)) != null) {
                                        ingredients = (Map<String, Map<String, Object>>) doc.get(mActivity.getString(R.string.ingredients));

                                    }

                                    if (doc.get(mActivity.getString(R.string.image_url)) != null) {
                                        recipe_image_url = doc.getString(mActivity.getString(R.string.image_url));

                                    }

                                    if (doc.get(mActivity.getString(R.string.recipe_name)) != null) {
                                        recipe_name = doc.getString(mActivity.getString(R.string.recipe_name));
                                    }

                                    if (doc.get(mActivity.getString(R.string.prep_time)) != null) {
                                        prep_time = doc.getString(mActivity.getString(R.string.prep_time));
                                    }

                                    if (doc.get(mActivity.getString(R.string.directions)) != null) {
                                        directions = (ArrayList<String>) doc.get(mActivity.getString(R.string.directions));
                                    }

                                    if (doc.get(mActivity.getString(R.string.portion)) != null) {
                                        portion = (Map<String, Object>) doc.get(mActivity.getString(R.string.portion));
                                    }


                                    if (doc.get(mActivity.getString(R.string.category_id)) != null) {
                                        category_id = doc.getString(mActivity.getString(R.string.category_id));
                                    }

                                    if (doc.get(mActivity.getString(R.string.video_url)) != null) {
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

                                    //change Adapter
                                    searchRecipeAdapter.notifyDataSetChanged();


                                }

                            }
                        }


                    }
                });

    }

    @Override
    public void onStop() {
        super.onStop();

        if (listenerRegistration != null) {
            listenerRegistration = null;
        }
    }
}