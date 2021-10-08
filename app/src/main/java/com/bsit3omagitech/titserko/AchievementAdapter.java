package com.bsit3omagitech.titserko;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AchievementAdapter extends RecyclerView.Adapter<AchievementAdapter.MyViewHolder> {


    List<String> title, imagePath, achievementId;
    String username;
    Context context;
    List<Boolean> isUnlocked;
    DataBaseHelper db;
    AchievementAdapter.OnIndividualDialog listenerIndividual;

    public AchievementAdapter(Context ct, List<String> titles, List<String> imagePaths, List<String> achievementIds, String name, List<Boolean> isUnlock){

        context = ct;
        title = titles;
        imagePath = imagePaths;
        achievementId = achievementIds;
        username = name;
        isUnlocked = isUnlock;
        db = new DataBaseHelper(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.achieve_row, parent,false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        //set title
        holder.tv_achieve_title.setText(title.get(position));

        //imageResource
        Uri imageUri =Uri.parse("android.resource://com.bsit3omagitech.titserko/raw/" + imagePath.get(position));
        holder.iv_achieve_img.setImageURI(imageUri);



        //if button is toggled
        if(db.isEquipped(username, achievementId.get(position))){

            holder.btn_achieve.setText("OFF");
            holder.btn_achieve.setClickable(false);
            holder.btn_achieve.setAlpha(0.5f);

        }
        else{
            holder.btn_achieve.setText("ON");
            holder.btn_achieve.setAlpha(1);
            holder.btn_achieve.setClickable(true);
            holder.btn_achieve.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder =new AlertDialog.Builder(context);
                    builder.setTitle("Equip badge")
                            .setMessage("Are you sure you want to equip the badge?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //update user table
                                    db.updateBadge(username, achievementId.get(holder.getAdapterPosition()));
                                    Intent intent = new Intent(context, TkDashboardActivity.class);
                                    intent.putExtra("username", username);
                                    context.startActivity(intent);
                                    Toast.makeText(context, "Successfully changed badge.", Toast.LENGTH_LONG).show();
                                }
                            }).setNegativeButton("No", null)
                            .setCancelable(true);
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
            });
        }

        if(!isUnlocked.get(holder.getAdapterPosition())){

            holder.itemView.setAlpha(0.5f);
            holder.btn_achieve.setClickable(false);

        }
        else{
            holder.itemView.setAlpha(1);
            holder.btn_achieve.setClickable(true);
        }

    }

    @Override
    public int getItemCount() {
        return title.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_achieve_title;
        ImageView iv_achieve_img;
        Button btn_achieve;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_achieve_title = itemView.findViewById(R.id.tv_achieveRow);
            iv_achieve_img = itemView.findViewById(R.id.iv_achieveRow);
            btn_achieve = itemView.findViewById(R.id.btn_achieve);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    listenerIndividual.convertViewOnIndividualScreen(position);
                }
            });

        }
    }

    public interface OnIndividualDialog{
        void convertViewOnIndividualScreen(int position);

    }
    public void setIndividualScreenListener(AchievementAdapter.OnIndividualDialog listenerGalingSaIndividualScreen) {
        listenerIndividual = listenerGalingSaIndividualScreen;
    }
}
