package com.bsit3omagitech.titserko;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class myAdapter extends RecyclerView.Adapter<myAdapter.MyViewHolder> {

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
        background.add(R.drawable.vector_myrow1);
        background.add(R.drawable.vector_myrow2);
        background.add(R.drawable.vector_myrow3);
        background.add(R.drawable.vector_myrow4);

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

        else{
            holder.itemView.setClickable(true);
            holder.itemView.setAlpha(1f);

        }

        holder.ll_bg.setBackgroundResource(background.get(position % 4));


    }

    @Override
    public int getItemCount() {
        return data1.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {


        TextView myText1;
        ImageView iv_star;
        ConstraintLayout ll_bg;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            myText1 = itemView.findViewById(R.id.row_lesson_name);

            //auto size textSize if version is equal or greater than api 26 (autoSize already defined in xml) here manually add textSize based on screen width
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O){

                WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                Display display = wm.getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int width = size.x;
                int height = size.y;

                if(1080 <= width) {
                    Log.d("size", "went here 1080");
                    myText1.setTextSize(getSizeInDp(8));
                }
                else if (720 <= width){
                    Log.d("size", "went here 720");
                    myText1.setTextSize(getSizeInDp(8));

                }
            }

            iv_star = itemView.findViewById(R.id.iv_star);
            ll_bg = itemView.findViewById(R.id.ll_row_bg);

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

    private int getSizeInDp(int sizeInPx){
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, sizeInPx, context.getResources()
                        .getDisplayMetrics());
    }


}
