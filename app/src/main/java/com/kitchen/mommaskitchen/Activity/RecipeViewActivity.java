package com.kitchen.mommaskitchen.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kitchen.mommaskitchen.Fragment.DirectionsFragment;
import com.kitchen.mommaskitchen.Fragment.IngredientsFragment;
import com.kitchen.mommaskitchen.R;
import com.kitchen.mommaskitchen.Utility.ContentsRecipe;
import com.kitchen.mommaskitchen.Utility.DefaultTextConfig;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RecipeViewActivity extends AppCompatActivity {

    public static final String TAG = "RecipeViewActivity";
    ImageView top_image, img_portions,bookmark;
    TabLayout tabLayout;
    TextView tv_minus, tv_plus, tv_portions, tv_recipe_prep_time, tv_portion_size,tv_recipe_name,tv_portion_makes;
    int tab_position;
    int portions = 1;
    ImageView back;
    Fragment fragment;
    boolean is_bookmarked = false;

    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore db;

    ArrayList<ContentsRecipe> recipeDetails;
    int pos;

    String portion_size,portion_unit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), RecipeViewActivity.this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_recipe_view);


        init();

        fragment = new IngredientsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("portions", portions);
        bundle.putSerializable("ingredients", (Serializable) recipeDetails.get(pos).getIngredients());
        fragment.setArguments(bundle);
        loadFragment(fragment);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab_position = tab.getPosition();
                Log.i(TAG, "Current tab position is " +tab_position);

                switch (tab_position) {

                    case 0:
                        fragment = new IngredientsFragment();
                        Bundle bundle = new Bundle();
                        bundle.putInt("portions", portions);
                        bundle.putSerializable("ingredients", (Serializable) recipeDetails.get(pos).getIngredients());
                        fragment.setArguments(bundle);
                        break;

                    case 1:
                        fragment = new DirectionsFragment();
                        Bundle bundle1 = new Bundle();
                        bundle1.putStringArrayList("directions",recipeDetails.get(pos).getDirections());
                        fragment.setArguments(bundle1);
                        break;
                }
                loadFragment(fragment);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        calculatePortion(portions);


        tv_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                portions++;
                calculatePortion(portions);

                selectIngredientTab();
                fragment = new IngredientsFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("portions", portions);
                bundle.putSerializable("ingredients", (Serializable) recipeDetails.get(pos).getIngredients());
                fragment.setArguments(bundle);
                loadFragment(fragment);



            }
        });

        tv_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(portions <= 1){
                    portions = 1;
                    calculatePortion(portions);

                }else{
                     Log.i(TAG, "Portions is "+portions);
                    calculatePortion(portions--);


                }

                selectIngredientTab();
                fragment = new IngredientsFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("portions", portions);
                bundle.putSerializable("ingredients", (Serializable) recipeDetails.get(pos).getIngredients());
                fragment.setArguments(bundle);
                loadFragment(fragment);

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if(is_bookmarked == true){
            bookmark.setImageResource(R.drawable.ic_bookmark_checked);
        }else{
            bookmark.setImageResource(R.drawable.ic_bookmark_unchecked);
        }

        bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG,"Button selected");
                if(is_bookmarked == true){
                    is_bookmarked = false;
                    bookmark.setImageResource(R.drawable.ic_bookmark_unchecked);
                    saveRecipe(false);
                }else{
                    is_bookmarked = true;
                    bookmark.setImageResource(R.drawable.ic_bookmark_checked);
                    saveRecipe(true);
                }
            }
        });

        setData();


    }

    private void init(){
        top_image = (ImageView) findViewById(R.id.top_image);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        img_portions = (ImageView) findViewById(R.id.img_portions);
        tv_portions = (TextView) findViewById(R.id.tv_portions);
        tv_recipe_prep_time = (TextView) findViewById(R.id.tv_recipe_prep_time);
        tv_portion_size = (TextView) findViewById(R.id.tv_portion_makes);
        tv_minus = (TextView) findViewById(R.id.tv_minus);
        tv_plus = (TextView) findViewById(R.id.tv_plus);
        back = (ImageView) findViewById(R.id.back);
        bookmark = (ImageView) findViewById(R.id.bookmark);
        tv_recipe_name = (TextView) findViewById(R.id.tv_recipe_name);
        tv_portion_makes = (TextView) findViewById(R.id.tv_portion_makes);
        recipeDetails = (ArrayList<ContentsRecipe>) getIntent().getSerializableExtra("recipeArrayList");
        pos = getIntent().getIntExtra("position",0);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
    }

    private void calculatePortion(int portion){
        int portion_size_int;
        switch (portion){
            case 1:
                tv_portions.setText("Portions(1)");
                img_portions.setImageResource(R.drawable.ic_bowl_1);
                tv_portion_makes.setText(String.valueOf(portion) +" portion = " + portion_size + " " + portion_unit);
                break;
            case 2:
                tv_portions.setText("Portions(2)");
                img_portions.setImageResource(R.drawable.ic_bowl_2);
                Log.i(TAG,"Portion size is "+portion_size);
                Log.i(TAG,"Portion is "+portion);
                portion_size_int = Integer.parseInt(portion_size) * portion;
                tv_portion_makes.setText(String.valueOf(portion) +" portion = " + String.valueOf(portion_size_int) + " " + portion_unit);
                break;
            case 3:
                tv_portions.setText("Portions(3)");
                img_portions.setImageResource(R.drawable.ic_bowl_3);
                portion_size_int = Integer.parseInt(portion_size) * portion;
                tv_portion_makes.setText(String.valueOf(portion) +" portion = " + String.valueOf(portion_size_int) + " " + portion_unit);
                break;
            case 4:
                tv_portions.setText("Portions(4)");
                img_portions.setImageResource(R.drawable.ic_bowl_4);
                portion_size_int = Integer.parseInt(portion_size) * portion;
                tv_portion_makes.setText(String.valueOf(portion) +" portion = " + String.valueOf(portion_size_int) + " " + portion_unit);
                break;
            case 5:
                tv_portions.setText("Portions(5)");
                img_portions.setImageResource(R.drawable.ic_bowl_5);
                portion_size_int = Integer.parseInt(portion_size) * portion;
                tv_portion_makes.setText(String.valueOf(portion) +" portion = " + String.valueOf(portion_size_int) + " " + portion_unit);
                break;

            default:
                tv_portions.setText("Portions("+String.valueOf(portion)+")");
                img_portions.setImageResource(R.drawable.ic_bowl_more);
                portion_size_int = Integer.parseInt(portion_size) * portion;
                tv_portion_makes.setText(String.valueOf(portion) +" portion = " + String.valueOf(portion_size_int) + " " + portion_unit);
                break;

        }
    }


    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    private void selectIngredientTab(){
        new Handler().postDelayed(
                new Runnable(){
                    @Override
                    public void run() {
                        tabLayout.getTabAt(0).select();
                    }
                }, 100);
    }

    private void setData(){


        if(recipeDetails.get(pos).getRecipe_name() != null){
            tv_recipe_name.setText(recipeDetails.get(pos).getRecipe_name());
        }

        if(recipeDetails.get(pos).getPrep_time() != null){
            tv_recipe_prep_time.setText(recipeDetails.get(pos).getPrep_time());
        }

        if (recipeDetails.get(pos).getPortion() != null){
            Map<String,Object> portion = recipeDetails.get(pos).getPortion();
            portion_size = String.valueOf( portion.get("size"));
            portion_unit = String.valueOf(portion.get("unit"));
            tv_portion_makes.setText(String.valueOf(portions) +" portion = " + portion_size + " " + portion_unit);

        }


        Glide.with(this).load(recipeDetails.get(pos).getImage_url()).placeholder(R.drawable.pumpkin_soup).into(top_image);




    }

    private void saveRecipe(Boolean is_bookmarked){

        Activity mActivity = this;
        if(firebaseUser!=null){

            final Map<String, Object> docData = new HashMap<>();
            if(is_bookmarked){
                docData.put(mActivity.getString(R.string.saved_recipe_id), FieldValue.arrayUnion(recipeDetails.get(pos).getDocument_id()));
            }else{
                docData.put(mActivity.getString(R.string.saved_recipe_id), FieldValue.arrayRemove(recipeDetails.get(pos).getDocument_id()));

            }

            DocumentReference docRef = db.collection(mActivity.getString(R.string.users)).document(firebaseUser.getUid());

            docRef.update(docData).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.i(TAG,"Recipe is updated in users favourites");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i(TAG,"Recipe could not be saved in users favourites "+e.getMessage());
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}