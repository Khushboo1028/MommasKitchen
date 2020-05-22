package com.kitchen.mommaskitchen.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.kitchen.mommaskitchen.Adapter.DirectionsAdapter;
import com.kitchen.mommaskitchen.R;

import java.util.ArrayList;


public class DirectionsFragment extends Fragment {


    public static final String TAG = "IngredientsFragment";
    private RecyclerView recyclerView;
    private ArrayList<String> directionsArrayList,stringArrayList;
    private DirectionsAdapter directionsAdapter;

    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_ingredients, container, false);

        init();

        Bundle bundle = this.getArguments();
        if(bundle.getStringArrayList("directions") != null){
            stringArrayList = bundle.getStringArrayList("directions");
            Log.i(TAG,"Directions from activity is "+stringArrayList.toString());


        }

        directionsArrayList.clear();
        for(int i=0;i<stringArrayList.size();i++){
            directionsArrayList.add(stringArrayList.get(i));
        }

        directionsAdapter.notifyDataSetChanged();

        return view;
    }

    private void init(){

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        directionsArrayList = new ArrayList<>();
        stringArrayList = new ArrayList<>();
        recyclerView.setHasFixedSize(true); //so it doesn't matter if element size increases or decreases
        directionsAdapter = new DirectionsAdapter(directionsArrayList,getActivity());
        recyclerView.setAdapter(directionsAdapter);
    }
}
