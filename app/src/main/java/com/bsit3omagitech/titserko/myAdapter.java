package com.bsit3omagitech.titserko;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class myAdapter extends RecyclerView.Adapter<myAdapter.MyViewHolder> {

    int index = 0;
    List<String> data1, lessonIds;
    List<Integer> stars, background;
    Context context;
    OnIndividualScreen listenerIndividual;
    GlobalFunctions gf;
    public myAdapter(Context ct, List<String> s1, List<Integer> star, List<String> lessonId){
        context = ct;
        data1 = s1;
        lessonIds = lessonId;
        stars = star;
        gf = new GlobalFunctions(context);
        background = new ArrayList<>();
        background.add(R.drawable.green_gradient);
        background.add(R.drawable.purple_gradient);
        background.add(R.drawable.red_gradient);
        background.add(R.drawable.blue_gradient);
        background.add(R.drawable.yellow_gradient);
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
        String path = "lesson" + lessonIds.get(position) + "/"+gf.getLessonImgPath(lessonIds.get(position)) + ".png";
        gf.setImage(holder.iv_lesson_img, path );
        // holder.iv_lesson_img.setImage
        int _star = stars.get(position);
        String image_path = "";
        switch(_star){
            case 0:
                image_path = "general_img/star0.png";
                gf.setImage(holder.iv_star, image_path);
                break;
            case 1:
                image_path = "general_img/star1.png";
                gf.setImage(holder.iv_star, image_path);
                break;
            case 2:
                image_path = "general_img/star2.png";
                gf.setImage(holder.iv_star, image_path);
                break;
            case 3:
                image_path = "general_img/star3.png";
                gf.setImage(holder.iv_star, image_path);
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

        //set the background
        holder.ll_bg.setBackgroundResource(background.get(index));
        index++;
        if(index > (background.size()-1)) index = 0;


    }

    @Override
    public int getItemCount() {
        return data1.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {


        TextView myText1, myText2;
        ImageView iv_star, iv_lesson_img;
        LinearLayout ll_bg;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            myText1 = itemView.findViewById(R.id.row_lesson_name);
            iv_star = itemView.findViewById(R.id.iv_star);
            ll_bg = itemView.findViewById(R.id.ll_row_bg);
            iv_lesson_img= itemView.findViewById(R.id.iv_lesson_img);

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
