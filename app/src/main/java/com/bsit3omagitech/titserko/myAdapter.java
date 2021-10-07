package com.bsit3omagitech.titserko;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class myAdapter extends RecyclerView.Adapter<myAdapter.MyViewHolder> {

    List<String> data1;
    List<Integer> stars;
    Context context;
    OnIndividualScreen listenerIndividual;

    public myAdapter(Context ct, List<String> s1, List<Integer> star){
        context = ct;
        data1 = s1;
        stars = star;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

       //for lesson titles

        holder.myText1.setText(data1.get(position));
        int _star = stars.get(position);

        switch(_star){
            case 0:
                holder.iv_star.setImageResource(R.drawable.star0);
                break;
            case 1:
                holder.iv_star.setImageResource(R.drawable.star1);
                break;
            case 2:
                holder.iv_star.setImageResource(R.drawable.star2);
                break;
            case 3:
                holder.iv_star.setImageResource(R.drawable.star3);
                break;

        }

        if(position!=0){

            if(stars.get(position-1) > 0){
                //lock this
                holder.itemView.setClickable(true);
                holder.itemView.setAlpha(1f);
            }
            else{
                holder.itemView.setClickable(false);
                holder.itemView.setAlpha(0.5f);

            }




        }




    }

    @Override
    public int getItemCount() {
        return data1.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {


        TextView myText1, myText2;
        ImageView iv_star;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            myText1 = itemView.findViewById(R.id.row_lesson_name);
            iv_star = itemView.findViewById(R.id.iv_star);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    listenerIndividual.convertViewOnIndividualScreen(position);
                }
            });

        }
    }

    public interface OnIndividualScreen{
        void convertViewOnIndividualScreen(int position);

    }
    public void setIndividualScreenListener(OnIndividualScreen listenerGalingSaIndividualScreen) {
        listenerIndividual = listenerGalingSaIndividualScreen;
    }



}
