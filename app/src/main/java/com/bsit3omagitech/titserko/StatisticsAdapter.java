package com.bsit3omagitech.titserko;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StatisticsAdapter extends RecyclerView.Adapter<StatisticsAdapter.MyViewHolder> {

    List<String> titles;
    List<Integer> studyProgress, quizHighscore, quizLowscore, quizTaken;
    Context context;
    DataBaseHelper db;

    public StatisticsAdapter(List<String> titles, List<Integer> studyProgress, List<Integer> quizHighscore, List<Integer> quizLowscore, List<Integer> quizTaken, Context context) {
        this.titles = titles;
        this.studyProgress = studyProgress;
        this.quizHighscore = quizHighscore;
        this.quizLowscore = quizLowscore;
        this.quizTaken = quizTaken;
        this.context = context;
        db = new DataBaseHelper(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.stat_row, parent,false);
        return new StatisticsAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        //set values

        holder.stat_row_title.setText(titles.get(position));
        holder.stat_row_study_progress.setText(studyProgress.get(position) + "%");
        holder.stat_row_highscore.setText(quizHighscore.get(position) + "");
        holder.stat_row_lowscore.setText(quizLowscore.get(position) + "");
        holder.stat_row_quiz_taken.setText(quizTaken.get(position) + "");


    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView stat_row_study_progress, stat_row_highscore, stat_row_lowscore, stat_row_quiz_taken, stat_row_title;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            stat_row_title = itemView.findViewById(R.id.stat_row_title);
            stat_row_study_progress = itemView.findViewById(R.id.stat_row_study_progress);
            stat_row_highscore = itemView.findViewById(R.id.stat_row_highscore);
            stat_row_lowscore = itemView.findViewById(R.id.stat_row_lowscore);
            stat_row_quiz_taken = itemView.findViewById(R.id.stat_row_quiz_taken);


        }
    }
}
