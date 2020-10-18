package com.kitchen.mommaskitchen.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kitchen.mommaskitchen.Adapter.IngredientsAdapter;
import com.kitchen.mommaskitchen.Adapter.LatestReleaseAdapter;
import com.kitchen.mommaskitchen.R;
import com.kitchen.mommaskitchen.Utility.ContentsIngredients;
import com.kitchen.mommaskitchen.Utility.ContentsRecipe;
import com.kitchen.mommaskitchen.Utility.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class IngredientsFragment extends Fragment {

    public static final String TAG = "IngredientsFragment";
    private RecyclerView recyclerView;
    private ArrayList<ContentsIngredients> ingredientsArrayList;
    private IngredientsAdapter ingredientsAdapter;
    int portions = 1;
    View view;
    Utils utils;

    Map <String, Map<String,Object>> ingredients;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_ingredients, container, false);
        init();
        portions = getArguments().getInt("portions");
        Log.i(TAG,"Portions is "+portions);





        if(!ingredients.isEmpty()){
            for ( String key : ingredients.keySet() ) {

                String recipe_name = key;
                float recipe_qty = Float.parseFloat(ingredients.get(key).get("qty").toString());


                String units;
                if(ingredients.get(key).get("units")!=null){
                    units = ingredients.get(key).get("units").toString();

                }else{
                    units = "";
                }

                ingredientsArrayList.add(new ContentsIngredients(utils.capitalize(recipe_name),recipe_qty,units));

            }
        }

        for(int i = 0; i<ingredientsArrayList.size();i++){
            ingredientsArrayList.get(i).setIngredient_quantity(ingredientsArrayList.get(i).getIngredient_quantity() * portions);
        }
        Log.i(TAG,ingredientsArrayList.toString());

        ingredientsAdapter.notifyDataSetChanged();


        return view;
    }

    private void init(){

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        ingredientsArrayList = new ArrayList<>();
        utils = new Utils();
        recyclerView.setHasFixedSize(true); //so it doesn't matter if element size increases or decreases
        ingredientsAdapter = new IngredientsAdapter(ingredientsArrayList,getActivity());
        recyclerView.setAdapter(ingredientsAdapter);

        ingredients = new HashMap<>();
        Bundle bundle = this.getArguments();

        if(bundle.getSerializable("ingredients") != null){
            ingredients = (Map<String, Map<String, Object>>) bundle.getSerializable("ingredients");
            Log.i(TAG,"Ingredients from activity is "+ingredients.toString());
        }




    }
}
