package com.kitchen.mommaskitchen.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.kitchen.mommaskitchen.Adapter.SearchRecipeAdapter;
import com.kitchen.mommaskitchen.R;
import com.kitchen.mommaskitchen.Utility.ContentsRecipe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {

    public static final String TAG = "SearchActivity";
    private EditText et_search;

    private RecyclerView recyclerView;
    private SearchRecipeAdapter searchRecipeAdapter;

    FirebaseFirestore db;
    ListenerRegistration listenerRegistration;
    ArrayList<ContentsRecipe> recipeArrayList;

    String recipe_name, category_id, prep_time, date_viewFormat, recipe_image_url, video_url;
    ImageView back;

    Map<String, Map<String, Object>> ingredients;
    ArrayList<String> directions;
    Timestamp date_created;
    Map<String, Object> portion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

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

        getRecipes(SearchActivity.this);
    }

    private void init() {
        et_search = (EditText) findViewById(R.id.et_search);
        et_search.requestFocus();
        back = (ImageView) findViewById(R.id.back);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_search);
        recyclerView.setHasFixedSize(true);
        recipeArrayList = new ArrayList<>();

        searchRecipeAdapter = new SearchRecipeAdapter(recipeArrayList,SearchActivity.this);
        recyclerView.setAdapter(searchRecipeAdapter);

        db = FirebaseFirestore.getInstance();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


    }

    private void filter(String text) {
        ArrayList<ContentsRecipe> temp = new ArrayList();
        for (ContentsRecipe d : recipeArrayList) {

            if (d.getRecipe_name().toLowerCase().contains(text.toLowerCase())) {
                temp.add(d);
            }
        }
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
                                            video_url,
                                            false

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}