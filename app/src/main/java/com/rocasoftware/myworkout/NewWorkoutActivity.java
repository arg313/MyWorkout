package com.rocasoftware.myworkout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.rocasoftware.myworkout.models.Exercise;
import com.rocasoftware.myworkout.models.ExerciseToDo;
import com.rocasoftware.myworkout.models.Muscle;
import com.rocasoftware.myworkout.models.MyTimer;
import com.rocasoftware.myworkout.models.Workout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class NewWorkoutActivity extends AppCompatActivity {

    //Elementos graficos
    Button addExerciseBt, cancelBt, stopButton;
    TextView timerText;
    ScrollView scrollView;
    RelativeLayout emptyLy;
    WorkoutExerciseFragment workoutExerciseFragment;

    //Firebase
    FirebaseAuth localAuth = FirebaseAuth.getInstance();
    FirebaseFirestore store = FirebaseFirestore.getInstance();

    //Informacion de la aplicacion
    Workout actualWorkout;
    ArrayList<ExerciseToDo> exerciseArray = new ArrayList<>();
    ArrayList<Workout> workouts = new ArrayList<>();
    ArrayList<Muscle> muscleGroups = new ArrayList<>();

    MyTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_workout);

        scrollView = findViewById(R.id.horizontalScrollView);
        emptyLy = findViewById(R.id.emptyLy);
        scrollView.setVisibility(View.GONE);
        emptyLy.setVisibility(View.GONE);
        timerText = findViewById(R.id.timerText);
        Toolbar myToolBar = findViewById(R.id.toolbar);
        myToolBar.setTitle("Entrenamiento en curso...");

        if (getIntent().getParcelableExtra("workout") != null) {
            actualWorkout = getIntent().getParcelableExtra("workout");
        } else {
            actualWorkout = new Workout();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
            Date date = new Date(System.currentTimeMillis());
            actualWorkout.setStartDate(formatter.format(date));
        }

        //Gestionamos el temporizador
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
            Date currentDate = new Date(System.currentTimeMillis());
            Date startDate = formatter.parse(actualWorkout.getStartDate());

            long diff = currentDate.getTime() - startDate.getTime();
            int rounded = (int) Math.round(diff);
            int duration = ((rounded / 1000));
            timer = new MyTimer(duration);
            timer.startTimer(this, timerText);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (actualWorkout.getExercises().isEmpty()) emptyLy.setVisibility(View.VISIBLE);
        else scrollView.setVisibility(View.VISIBLE);

        workouts = getIntent().getParcelableArrayListExtra("workouts");
        muscleGroups = getIntent().getParcelableArrayListExtra("muscles");
        exerciseArray = getIntent().getParcelableArrayListExtra("exercisesToDo");

        workoutExerciseFragment = WorkoutExerciseFragment.newInstance(1, actualWorkout.getExercises(), actualWorkout, exerciseArray, workouts, muscleGroups,"");
        getSupportFragmentManager().beginTransaction().replace(R.id.exerciseDone, workoutExerciseFragment).commit();
        if (exerciseArray.isEmpty()) {
            emptyLy.setVisibility(View.VISIBLE);
        }
        else scrollView.setVisibility(View.VISIBLE);

        addExerciseBt = findViewById(R.id.addButton);
        addExerciseBt.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), NewExerciseActivity.class);
            putExtras(intent);
            timer.getTimer().cancel();
            startActivity(intent);
        });

        cancelBt = findViewById(R.id.cancelButton);
        cancelBt.setOnClickListener(view -> new MaterialAlertDialogBuilder(NewWorkoutActivity.this)
                .setTitle("Cancelar entrenamiento")
                .setMessage("Los datos introducidos para este entrenamiento no se guardarán. ¿Desea confirmar?")
                .setPositiveButton("Confirmar", (dialogInterface, i) -> {
                    goToMainActivity(view.getContext());
                })
                .setNegativeButton("Cancelar", (dialogInterface, i) -> {
                })
                .show());

        stopButton = findViewById(R.id.stopButton);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (actualWorkout.getExercises().isEmpty()) {
                    new MaterialAlertDialogBuilder(NewWorkoutActivity.this)
                            .setTitle("Terminar entrenamiento")
                            .setMessage("Este entrenamiento no tiene datos, ¿Deseas salir?")
                            .setPositiveButton("Confirmar", (dialogInterface, i) -> {
                                goToMainActivity(view.getContext());
                            })
                            .setNegativeButton("Cancelar", (dialogInterface, i) -> {
                            })
                            .show();
                } else {
                    new MaterialAlertDialogBuilder(NewWorkoutActivity.this)
                            .setTitle("Terminar entrenamiento")
                            .setMessage("¿Deseas acabar el entrenamiento y guardar los datos?")
                            .setPositiveButton("Confirmar", (dialogInterface, i) -> {
                                saveWorkout(view.getContext());
                            })
                            .setNegativeButton("Cancelar", (dialogInterface, i) -> {
                            })
                            .show();
                }
            }
        });
    }

    public void saveWorkout(Context context) {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());
        String finishDate = formatter.format(date);
        actualWorkout.setFinishDate(finishDate);

        FirebaseUser user = localAuth.getCurrentUser();
        DocumentReference dr = store.collection("Trainings").document();
        Map<String, Object> trainingInfo = new HashMap<>();
        trainingInfo.put("User", user.getEmail());
        trainingInfo.put("Start", actualWorkout.getStartDate());
        trainingInfo.put("Finish", finishDate);
        dr.set(trainingInfo);

        for (int i = 0; i < actualWorkout.getExercises().size(); i++) {
            Exercise ex = actualWorkout.getExercises().get(i);
            DocumentReference exerciseRef = store.collection("Exercises").document();
            Map<String, Object> exerciseInfo = new HashMap<>();
            exerciseInfo.put("TrainingId", dr.getId());
            exerciseInfo.put("Name", ex.getName());
            exerciseInfo.put("MuscleId", ex.getMuscle().getMuscleId());
            exerciseInfo.put("nextWorkout", ex.getNextWorkout()+"");
            exerciseRef.set(exerciseInfo);

            for (int j = 0; j < ex.getRepetitions().size(); j++) {
                DocumentReference repetitionRef = store.collection("Repetitions").document();
                Map<String, Object> repetitionInfo = new HashMap<>();
                repetitionInfo.put("ExerciseId", exerciseRef.getId());
                repetitionInfo.put("Number", ex.getRepetitions().get(j).getRepNumber() + "");
                repetitionInfo.put("Kilos", ex.getRepetitions().get(j).getKilos() + "");
                repetitionRef.set(repetitionInfo);
            }
        }
        workouts.add(0, actualWorkout);
        goToMainActivity(getApplicationContext());
    }

    public void goToMainActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        //Se usan workouts y muscleGroups
        putExtras(intent);
        startActivity(intent);
    }

    public void putExtras(Intent intent) {
        intent.putExtra("workouts", workouts);
        intent.putExtra("muscles", muscleGroups);
        intent.putExtra("title", "Nuevo ejercicio");
        intent.putExtra("workout", actualWorkout);
        intent.putExtra("exercisesToDo", exerciseArray);
        intent.putExtra("timer", timer.getTime());

    }
}