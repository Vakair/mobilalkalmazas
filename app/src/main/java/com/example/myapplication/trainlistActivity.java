package com.example.myapplication;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.Manifest;

public class trainlistActivity extends AppCompatActivity {

    private static final String LOG_TAG = trainlistActivity.class.getName();
    private FirebaseUser user;
    private FirebaseAuth mAuth;

    private RecyclerView recyclerView;
    private ExerciseAdapter exerciseAdapter;
    private List<Exercise> exercises;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainlist);

        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            Log.d(LOG_TAG, "Unauthenticated user!");
            finish();
            return;
        }

        recyclerView = findViewById(R.id.exerciseRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        String muscleGroup = getIntent().getStringExtra("muscleGroup");
        if (muscleGroup == null) {
            Log.d(LOG_TAG, "No muscle group provided.");
            finish();
            return;
        }

        exercises = getExercisesForMuscle(muscleGroup);
        exerciseAdapter = new ExerciseAdapter(this, exercises, user.getUid(), muscleGroup);
        recyclerView.setAdapter(exerciseAdapter);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }


        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(view -> {
            exerciseAdapter.savePerformanceData();
            Toast.makeText(trainlistActivity.this, "Mentve!", Toast.LENGTH_SHORT).show();

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "workout_channel")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("Edzés elmentve")
                    .setContentText("Az edzésed sikeresen elmentve.")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(1001, builder.build());
        });


        createNotificationChannel();
        scheduleDailyNotification();
    }

    private void scheduleDailyNotification() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 16);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1); // ha már elmúlt 16:00, akkor holnapra
        }

        Intent intent = new Intent(this, ReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);
    }




    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                    checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                Log.d(LOG_TAG, "Nem jött létre notification channel, nincs engedély.");
                return;
            }

            CharSequence name = "WorkoutReminderChannel";
            String description = "Workout notification channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("workout_channel", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }







    @Override
    protected void onResume() {
        super.onResume();
        if (exerciseAdapter != null) {
            exerciseAdapter.notifyDataSetChanged();
        }
    }






    private List<Exercise> getExercisesForMuscle(String muscleGroup) {
        List<Exercise> list = new ArrayList<>();

        switch (muscleGroup) {
            case "Láb":
                list.add(new Exercise("Guggolás", R.drawable.squatting));
                list.add(new Exercise("Vádli", R.drawable.calfr));


                break;
            case "Bicepsz":
                list.add(new Exercise("Bicepsz hajlítás", R.drawable.bicepsss));
                break;
            case "Tricepsz ":
                list.add(new Exercise("Tricepsz kötéllel", R.drawable.tricepsss));
                break;
            case "Has":
                list.add(new Exercise("Felülés", R.drawable.sit));
                list.add(new Exercise("Plank", R.drawable.plankk));
                break;
            case "Cardio":
                list.add(new Exercise("Futás", R.drawable.runrun));
                list.add(new Exercise("Ugrókötelezés", R.drawable.jump_rope));
                break;
            case "Váll":
                list.add(new Exercise("Vállból nyomás", R.drawable.shoulderpressing));
                break;
            case "Mell":
                list.add(new Exercise("Fekvenyomás", R.drawable.benchpresssss));
                list.add(new Exercise("Tárogatás", R.drawable.chestfly));
                list.add(new Exercise("Fekvőtámasz", R.drawable.pushupp));
                break;
            case "Hát":
                list.add(new Exercise("Húzódzkodás", R.drawable.pulldown));
                list.add(new Exercise("Deadlift", R.drawable.deadliftt));
                break;
            default:
                list.add(new Exercise("Ismeretlen gyakorlat", R.drawable.bela));
                break;
        }

        return list;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(LOG_TAG, "Notification permission granted.");
                createNotificationChannel();
            } else {
                Log.d(LOG_TAG, "Notification permission denied.");
                Toast.makeText(this, "Az értesítések nem engedélyezettek.", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
