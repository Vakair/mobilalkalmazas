package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {

    private Context context;
    private List<Exercise> exercises;
    private String userId;
    private String muscleGroup;

    public ExerciseAdapter(Context context, List<Exercise> exercises, String userId, String muscleGroup) {
        this.context = context;
        this.exercises = exercises;
        this.userId = userId;
        this.muscleGroup = muscleGroup;
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_exercise, parent, false);
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        Exercise exercise = exercises.get(position);
        holder.exerciseName.setText(exercise.getName());
        holder.exerciseImage.setImageResource(exercise.getImageResId());

        // Betöltés SharedPreferences-ből
        String keyPrefix = userId + "_" + muscleGroup + "_" + exercise.getName();
        SharedPreferences prefs = context.getSharedPreferences("ExerciseData", Context.MODE_PRIVATE);

        for (int i = 0; i < 4; i++) {
            String reps = prefs.getString(keyPrefix + "_set" + i + "_reps", "");
            String weight = prefs.getString(keyPrefix + "_set" + i + "_weight", "");

            holder.reps[i].setText(reps);
            holder.weights[i].setText(weight);
        }

        // Mentés minden mezőből SharedPreferences-be, ha a fókusz elveszik
        for (int i = 0; i < 4; i++) {
            final int index = i;
            holder.reps[i].setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus) {
                    saveToPrefs(prefs, keyPrefix, holder.reps[index], holder.weights[index], index);
                }
            });
            holder.weights[i].setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus) {
                    saveToPrefs(prefs, keyPrefix, holder.reps[index], holder.weights[index], index);
                }
            });
        }
    }

    private void saveToPrefs(SharedPreferences prefs, String keyPrefix, EditText reps, EditText weight, int index) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(keyPrefix + "_set" + index + "_reps", reps.getText().toString());
        editor.putString(keyPrefix + "_set" + index + "_weight", weight.getText().toString());
        editor.apply();
    }

    public void savePerformanceData() {
        // Ez a metódus csak hívásra kerül, ha a mentés gombra kattintanak
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    static class ExerciseViewHolder extends RecyclerView.ViewHolder {

        TextView exerciseName;
        ImageView exerciseImage;
        EditText[] reps = new EditText[4];
        EditText[] weights = new EditText[4];

        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            exerciseName = itemView.findViewById(R.id.exerciseName);
            exerciseImage = itemView.findViewById(R.id.exerciseImage);

            reps[0] = itemView.findViewById(R.id.reps1);
            reps[1] = itemView.findViewById(R.id.reps2);
            reps[2] = itemView.findViewById(R.id.reps3);
            reps[3] = itemView.findViewById(R.id.reps4);

            weights[0] = itemView.findViewById(R.id.weight1);
            weights[1] = itemView.findViewById(R.id.weight2);
            weights[2] = itemView.findViewById(R.id.weight3);
            weights[3] = itemView.findViewById(R.id.weight4);
        }
    }
}
