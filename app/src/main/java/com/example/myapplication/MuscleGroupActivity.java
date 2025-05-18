package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MuscleGroupActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MuscleGroupAdapter adapter;
    private List<MuscleGroupItem> muscleGroups;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musclegroup);

        recyclerView = findViewById(R.id.recyclerViewMuscles);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));






        muscleGroups = new ArrayList<>();
        muscleGroups.add(new MuscleGroupItem("Láb", R.drawable.leg));
        muscleGroups.add(new MuscleGroupItem("Bicepsz", R.drawable.bicep));
        muscleGroups.add(new MuscleGroupItem("Tricepsz ", R.drawable.tricep));
        muscleGroups.add(new MuscleGroupItem("Has", R.drawable.abs));
        muscleGroups.add(new MuscleGroupItem("Cardio", R.drawable.cardio));
        muscleGroups.add(new MuscleGroupItem("Váll", R.drawable.shoulder));
        muscleGroups.add(new MuscleGroupItem("Mell", R.drawable.chest));
        muscleGroups.add(new MuscleGroupItem("Hát", R.drawable.back));

        adapter = new MuscleGroupAdapter(this, muscleGroups);
        recyclerView.setAdapter(adapter);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_fade);
        recyclerView.setLayoutAnimation(controller);
        recyclerView.scheduleLayoutAnimation();


    }
}
