package com.kitchen.mommaskitchen.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.kitchen.mommaskitchen.R;
import com.kitchen.mommaskitchen.Adapter.RecipeAdapter;
import com.kitchen.mommaskitchen.Utility.ContentsCategories;
import com.kitchen.mommaskitchen.Utility.ContentsRecipe;
import com.kitchen.mommaskitchen.Utility.DefaultTextConfig;
import com.kitchen.mommaskitchen.Utility.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CategoryActivity extends AppCompatActivity {

    public static final String TAG = "CategoryActivity";
    private RecyclerView recyclerView;
    private RecipeAdapter recipeAdapter;
    private ArrayList<ContentsRecipe> recipeArrayList;
    private ArrayList<ContentsRecipe> recipes_in_category_list;
    private ArrayList<ContentsCategories> categoriesArrayList;
    TextView tv_saved_recipe;
    private ImageView back;
    Utils utils;
    int pos;
    String category_name;

    EditText et_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), CategoryActivity.this);
        setContentView(R.layout.fragment_saved);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        init();

        recipeArrayList = (ArrayList<ContentsRecipe>) getIntent().getSerializableExtra("recipeArrayList");
        categoriesArrayList = (ArrayList<ContentsCategories>) getIntent().getSerializableExtra("categoryArrayList");
        pos = getIntent().getIntExtra("position",0);

        category_name = categoriesArrayList.get(pos).getCategory_name();
        tv_saved_recipe.setText(utils.capitalize(category_name));

        for(int i = 0; i<recipeArrayList.size() ; i++){
            if(recipeArrayList.get(i).getCategory_id().equals(categoriesArrayList.get(pos).getCategory_id())){
                recipes_in_category_list.add(recipeArrayList.get(i));
            }
        }

        recipeAdapter = new RecipeAdapter(recipes_in_category_list,this, false);
        recyclerView.setAdapter(recipeAdapter);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

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
    }

    private void init(){
        recipeArrayList = new ArrayList<>();
        recipes_in_category_list = new ArrayList<>();
        categoriesArrayList = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_vertical);
        recyclerView.setHasFixedSize(true); //so it doesn't matter if element size increases or decreases
        et_search = (EditText) findViewById(R.id.et_search);

        tv_saved_recipe = (TextView) findViewById(R.id.tv_saved_recipe);
        back = (ImageView) findViewById(R.id.back);
        back.setVisibility(View.VISIBLE);

        utils = new Utils();
    }

    private void filter(String text) {
        ArrayList<ContentsRecipe> temp = new ArrayList();
        for (ContentsRecipe d : recipes_in_category_list) {
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            Log.i(TAG,"categories in search: "+d.getRecipe_name());
            if (d.getRecipe_name().toLowerCase().contains(text.toLowerCase()) ){
                temp.add(d);
            }
        }
        //update recyclerview
        recipeAdapter.updateList(temp);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
