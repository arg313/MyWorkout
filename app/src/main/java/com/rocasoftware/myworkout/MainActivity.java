package com.rocasoftware.myworkout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.rocasoftware.myworkout.models.Exercise;
import com.rocasoftware.myworkout.models.ExerciseToDo;
import com.rocasoftware.myworkout.models.Muscle;
import com.rocasoftware.myworkout.models.Repetition;
import com.rocasoftware.myworkout.models.Sortbydate;
import com.rocasoftware.myworkout.models.Workout;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    //Elementos graficos
    //FloatingActionButton fab;
    //RelativeLayout emptyLy, loadLy;
    ScrollView scrollView;
    Button beginWorkoutBt;
    TextView helloText;

    //Firebase
    FirebaseAuth localAuth = FirebaseAuth.getInstance();
    FirebaseFirestore store = FirebaseFirestore.getInstance();

    //Informacion de la aplicacion
    ArrayList<Workout> workouts = new ArrayList<>();
    ArrayList<Muscle> muscleGroups = new ArrayList<>();
    ArrayList<ExerciseToDo> exerciseArray = new ArrayList<>();

    String userName = "";

    boolean loadFinish = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolBar = findViewById(R.id.toolbar);
        //emptyLy = findViewById(R.id.emptyLy);
        //loadLy = findViewById(R.id.loadLy);
        scrollView = findViewById(R.id.horizontalScrollView);
        beginWorkoutBt = findViewById(R.id.beginWorkoutBt);
        helloText = findViewById(R.id.helloText);
        setHelloText();
        //emptyLy.setVisibility(View.GONE);
        //loadLy.setVisibility(View.VISIBLE);
        //scrollView.setVisibility(View.GONE);
        //fab = findViewById(R.id.newWorkoutBt);

        myToolBar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.logout:
                    localAuth.signOut();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    return true;
                default:
                    return false;
            }
        });

        beginWorkoutBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), NewWorkoutActivity.class);
                putExtras(intent);
                startActivity(intent);
            }
        });

//        fab.setEnabled(false);
//        fab.setOnClickListener(view -> {
//            Intent intent = new Intent(view.getContext(), NewWorkoutActivity.class);
//            putExtras(intent);
//            startActivity(intent);
//        });

        if (getIntent().getParcelableArrayListExtra("workouts") != null) {
            workouts = getIntent().getParcelableArrayListExtra("workouts");
            muscleGroups = getIntent().getParcelableArrayListExtra("muscles");
            exerciseArray = getIntent().getParcelableArrayListExtra("exercisesToDo");
            //loadLy.setVisibility(View.GONE);
            if (workouts.isEmpty()) {
                //emptyLy.setVisibility(View.VISIBLE);
            } else {
                WorkoutFragment workoutFragment = WorkoutFragment.newInstance(1, workouts, muscleGroups, exerciseArray);
                getSupportFragmentManager().beginTransaction().replace(R.id.workoutsDone, workoutFragment).addToBackStack(null).commit();
                scrollView.setVisibility(View.VISIBLE);
            }
        } else {
            loadExercisesToDo();
            //getWorkouts();
        }
    }

    public void getWorkouts() {
        Toast.makeText(this, "Cargamos workouts", Toast.LENGTH_SHORT).show();
        runOnUiThread(() -> store.collection("Muscles").get().addOnCompleteListener(task23 -> {
            if (task23.isSuccessful()) {
                for (QueryDocumentSnapshot document : task23.getResult()) {
                    muscleGroups.add(new Muscle(document.getId(), document.getString("Name")));
                }

                //Cargamos los entrenamientos y los almacenamos
                store.collection("Trainings")
                        .whereEqualTo("User", localAuth.getCurrentUser().getEmail())
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
//                                if (task.getResult().isEmpty()) emptyLy.setVisibility(View.VISIBLE);
//                                else scrollView.setVisibility(View.VISIBLE);

                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String workoutId = document.getId();
                                    String startDate = document.getString("Start");
                                    String finishDate = document.getString("Finish");

                                    Workout work = new Workout(new ArrayList<>(), startDate, finishDate, workoutId);
                                    workouts.add(work);
                                }

                                //Ordenamos los entrenamientos de mas antiguo a mas nuevo
                                Collections.sort(workouts, new Sortbydate());
                                //Recorremos los entrenamientos y obtenemos sus ejericios
                                for (int i = 0; i < workouts.size(); i++) {
                                    int finalI = i;
                                    store.collection("Exercises")
                                            .whereEqualTo("TrainingId", workouts.get(i).getWorkoutId())
                                            .get()
                                            .addOnCompleteListener(task12 -> {
                                                if (task12.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document12 : task12.getResult()) {
                                                        String exerciseName = document12.getString("Name");
                                                        String exerciseId = document12.getId();
                                                        String muscleId = document12.getString("MuscleId");
                                                        int nextWorkout = 0;
                                                        if (document12.getString("nextWorkout") != null) {
                                                            nextWorkout = Integer.parseInt(document12.getString("nextWorkout"));
                                                        }
                                                        int index = muscleGroups.indexOf(new Muscle(muscleId, ""));
                                                        Exercise exercise = new Exercise(exerciseName, new ArrayList<>(), exerciseId, muscleGroups.get(index), nextWorkout);
                                                        workouts.get(finalI).getExercises().add(exercise);
                                                    }

                                                    if (finalI == (workouts.size() - 1)) {
                                                        for (int j = 0; j < workouts.size(); j++) {
                                                            int finalJ = j;
                                                            for (int k = 0; k < workouts.get(j).getExercises().size(); k++) {
                                                                int finalK = k;
                                                                Exercise exercise = workouts.get(j).getExercises().get(k);
                                                                store.collection("Repetitions")
                                                                        .whereEqualTo("ExerciseId", exercise.getExerciseId())
                                                                        .get()
                                                                        .addOnCompleteListener(task1 -> {
                                                                            if (task1.isSuccessful()) {
                                                                                for (QueryDocumentSnapshot document1 : task1.getResult()) {
                                                                                    String repetitionId = document1.getId();
                                                                                    int kilos = Integer.parseInt(document1.getString("Kilos"));
                                                                                    int numReps = Integer.parseInt(document1.getString("Number"));
                                                                                    String exerciseId = document1.getString("ExerciseId");

                                                                                    exercise.addRepetition(new Repetition(numReps, kilos, repetitionId, exerciseId));
                                                                                }

                                                                                if ((finalJ == (workouts.size()-1)) && (finalK == workouts.get(finalJ).getExercises().size()-1)) {
                                                                                    //loadLy.setVisibility(View.GONE);
                                                                                    WorkoutFragment workoutFragment = WorkoutFragment.newInstance(1, workouts, muscleGroups, exerciseArray);
                                                                                    getSupportFragmentManager().beginTransaction().replace(R.id.workoutsDone, workoutFragment).addToBackStack(null).commit();
                                                                                    //fab.setEnabled(true);
                                                                                }
                                                                            } else {
                                                                                Log.d("TAG", "Error getting documents: ", task1.getException());
                                                                            }
                                                                        });
                                                            }
                                                        }
                                                    }
                                                } else {
                                                    Log.d("TAG", "Error getting documents: ", task12.getException());
                                                }
                                            });
                                }
                            } else {
                                Log.d("TAG", "Error getting documents: ", task.getException());
                            }
                        });
            } else {
                Log.d("TAG", "Error getting documents: ", task23.getException());
            }
        }));
    }

    public void loadExercisesToDo () {
        Toast.makeText(this, "Cargamos ejercicios disponibles", Toast.LENGTH_SHORT).show();
        store.collection("ExercisesToDo")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String name = document.getString("Name");
                                String muscleId = document.getString("MuscleId");
                                String id = document.getId();
                                exerciseArray.add(new ExerciseToDo(id, muscleId, name));
                            }
                            loadFinish = true;
                        } else {
                            loadFinish = true;
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void putExtras(Intent intent) {
        intent.putExtra("workouts", workouts);
        intent.putExtra("muscles", muscleGroups);
        intent.putExtra("exercisesToDo", exerciseArray);
    }

    public void setHelloText() {
        if (userName.equals("")) {
            store.collection("Users").document(localAuth.getCurrentUser().getEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    userName = documentSnapshot.getString("Name");
                    helloText.setText("Hola, "+userName);
                }
            });
        } else {
            helloText.setText("Hola, " + userName);
        }
    }
//    public void getWorkouts() {
//        runOnUiThread(() -> store.collection("Muscles").get().addOnCompleteListener(task23 -> {
//            if (task23.isSuccessful()) {
//                for (QueryDocumentSnapshot document : task23.getResult()) {
//                    muscleGroups.add(new Muscle(document.getId(), document.getString("Name")));
//                }
//                store.collection("Trainings")
//                        .whereEqualTo("User", localAuth.getCurrentUser().getEmail())
//                        .get()
//                        .addOnCompleteListener(task -> {
//                            if (task.isSuccessful()) {
//                                if (task.getResult().isEmpty()) emptyLy.setVisibility(View.VISIBLE);
//                                else scrollView.setVisibility(View.VISIBLE);
//
//                                for (QueryDocumentSnapshot document : task.getResult()) {
//                                    String workoutId = document.getId();
//                                    String startDate = document.getString("Start");
//                                    String finishDate = document.getString("Finish");
//
//                                    emptyLy.setVisibility(View.GONE);
//
//                                    Workout work = new Workout(new ArrayList<>(), startDate, finishDate, workoutId);
//
//                                    store.collection("Exercises")
//                                            .whereEqualTo("TrainingId", workoutId)
//                                            .get()
//                                            .addOnCompleteListener(task12 -> {
//                                                if (task12.isSuccessful()) {
//                                                    for (QueryDocumentSnapshot document12 : task12.getResult()) {
//                                                        String exerciseId = document12.getId();
//                                                        String exerciseName = document12.getString("Name");
//                                                        String muscleId = document12.getString("MuscleId");
//
//                                                        int index = 0;
//                                                        for (int i = 0; i<muscleGroups.size(); i++) {
//                                                            if (muscleGroups.get(i).getMuscleId().equals(muscleId)) {
//                                                                index = i;
//                                                                break;
//                                                            }
//                                                        }
//                                                        Exercise exercise = new Exercise(exerciseName, new ArrayList<>(), exerciseId,muscleGroups.get(index));
//
//                                                        store.collection("Repetitions")
//                                                                .whereEqualTo("ExerciseId", exerciseId)
//                                                                .get()
//                                                                .addOnCompleteListener(task1 -> {
//                                                                    if (task1.isSuccessful()) {
//                                                                        for (QueryDocumentSnapshot document1 : task1.getResult()) {
//                                                                            String repetitionId = document1.getId();
//                                                                            int kilos = Integer.parseInt(document1.getString("Kilos"));
//                                                                            int numReps = Integer.parseInt(document1.getString("Number"));
//
//                                                                            Repetition rep = new Repetition(numReps, kilos, repetitionId);
//                                                                            exercise.addRepetition(rep);
//                                                                        }
//                                                                        work.addExercise(exercise);
//                                                                        WorkoutFragment workoutFragment = WorkoutFragment.newInstance(1, workouts);
//                                                                        getSupportFragmentManager().beginTransaction().replace(R.id.workoutsDone, workoutFragment).addToBackStack(null).commit();
//                                                                    } else {
//                                                                        Log.d("TAG", "Error getting documents: ", task1.getException());
//                                                                    }
//                                                                });
//                                                    }
//                                                } else {
//                                                    Log.d("TAG", "Error getting documents: ", task12.getException());
//                                                }
//                                            });
//                                    workouts.add(work);
//                                }
//                            } else {
//                                Log.d("TAG", "Error getting documents: ", task.getException());
//                            }
//                        });
//            } else {
//                Log.d("TAG", "Error getting documents: ", task23.getException());
//            }
//        }));
//    }
}