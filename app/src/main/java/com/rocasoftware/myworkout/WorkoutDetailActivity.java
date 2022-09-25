package com.rocasoftware.myworkout;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.rocasoftware.myworkout.models.ExerciseToDo;
import com.rocasoftware.myworkout.models.Muscle;
import com.rocasoftware.myworkout.models.Workout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WorkoutDetailActivity extends AppCompatActivity {

    //Elementos graficos
    TextView dateText, startText, finishText, exerciseDoneText;
    WorkoutExerciseFragment workoutExerciseFragment;

    //Informacion de la aplicacion
    Workout actualWorkout;
    ArrayList<Workout> workouts = new ArrayList<>();
    ArrayList<Muscle> muscleGroups = new ArrayList<>();
    ArrayList<ExerciseToDo> exerciseArray = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_detail);

        Toolbar myToolBar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolBar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        dateText = findViewById(R.id.dateText);
        startText = findViewById(R.id.startText);
        finishText = findViewById(R.id.finishText);
        exerciseDoneText = findViewById(R.id.exerciseDoneText);

        actualWorkout = getIntent().getParcelableExtra("actual");
        workouts = getIntent().getParcelableArrayListExtra("workouts");
        muscleGroups = getIntent().getParcelableArrayListExtra("muscles");
        exerciseArray = getIntent().getParcelableArrayListExtra("exercisesToDo");

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");

        try {
            Date startDate = formatter.parse(actualWorkout.getStartDate());
            Date finishDate = formatter.parse(actualWorkout.getFinishDate());
            dateText.setText(dateFormat.format(startDate));
            startText.setText(hourFormat.format(startDate));
            finishText.setText(hourFormat.format(finishDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        exerciseDoneText.setText("Ejercicios hechos ("+actualWorkout.getExercises().size()+")");

        workoutExerciseFragment = WorkoutExerciseFragment.newInstance(1, actualWorkout.getExercises(), actualWorkout, exerciseArray, workouts, muscleGroups, "detail");
        getSupportFragmentManager().beginTransaction().replace(R.id.exerciseDone, workoutExerciseFragment).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                Intent intent = new Intent(WorkoutDetailActivity.this, MainActivity.class);
                putExtras(intent);
                startActivity(intent);
                return true;
        }
    }

    public void putExtras(Intent intent) {
        intent.putExtra("workouts", workouts);
        intent.putExtra("muscles", muscleGroups);
        intent.putExtra("exercisesToDo", exerciseArray);
    }
}