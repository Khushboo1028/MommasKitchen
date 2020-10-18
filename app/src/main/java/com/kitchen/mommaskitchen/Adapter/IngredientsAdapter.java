package com.kitchen.mommaskitchen.Adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kitchen.mommaskitchen.R;
import com.kitchen.mommaskitchen.Utility.ContentsIngredients;
import com.kitchen.mommaskitchen.Utility.ContentsRecipe;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.ViewHolder> {

    public static final String TAG = "IngredientsAdapter";
    ArrayList<ContentsIngredients> recipeArrayList;
    Activity mActivity;


    public IngredientsAdapter(ArrayList<ContentsIngredients> recipeArrayList, Activity mActivity) {
        this.recipeArrayList = recipeArrayList;
        this.mActivity = mActivity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater= LayoutInflater.from(mActivity.getApplicationContext());
        View view=layoutInflater.inflate(R.layout.row_ingredients,null);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {

        ContentsIngredients contentsIngredients = recipeArrayList.get(i);

        float ingredient_quant = contentsIngredients.getIngredient_quantity();
        String q;
        if(isInteger(ingredient_quant)){
            if(ingredient_quant == 0){
                q = "";
            }else{
                q = new DecimalFormat("#.##").format(ingredient_quant);
            }
        }else{

            //Not an integer - convert decimal to fraction
            q = convertDecimalToFraction(ingredient_quant);

        }








        holder.tv_ingredient_quantity.setText( q+ " "+ contentsIngredients.getUnit());

        holder.tv_ingredient_name.setText(contentsIngredients.getIngredient_name());



    }

    @Override
    public int getItemCount() {
        return recipeArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_ingredient_quantity,tv_ingredient_name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_ingredient_name = (TextView) itemView.findViewById(R.id.tv_ingredient_name);
            tv_ingredient_quantity = (TextView) itemView.findViewById(R.id.tv_ingredient_quantity);
        }
    }

    private String convertDecimalToFraction(double x){
        if (x < 0){
            return "-" + convertDecimalToFraction(-x);
        }
        double tolerance = 1.0E-6;
        double h1=1; double h2=0;
        double k1=0; double k2=1;
        double b = x;
        do {
            double a = Math.floor(b);
            double aux = h1; h1 = a*h1+h2; h2 = aux;
            aux = k1; k1 = a*k1+k2; k2 = aux;
            b = 1/(b-a);
        } while (Math.abs(x-h1/k1) > x*tolerance);

        String h1_string = new DecimalFormat("#.##").format(h1);
        String k1_string = new DecimalFormat("#.##").format(k1);


//        return toMixed(Integer.parseInt(h1_string), Integer.parseInt(k1_string));

        if(h1 < k1){
            //do nothing
            return h1_string+"/"+k1_string;
        }else{
            //fraction is improper. Convert to mixed

            return toMixed(Integer.parseInt(h1_string), Integer.parseInt(k1_string));


        }




    }

    private String toMixed(int numerator, int denominator){
       int wholeNum = Math.floorDiv(numerator,denominator);
       String mixedNum = wholeNum + "   " + String.valueOf((numerator - wholeNum*denominator)) + "/" + String.valueOf(denominator);


        return mixedNum;




    }


   private boolean isInteger(double number) {
       return number % 1 == 0;// if the modulus(remainder of the division) of the argument(number) with 1 is 0 then return true otherwise false. }

   }
}
