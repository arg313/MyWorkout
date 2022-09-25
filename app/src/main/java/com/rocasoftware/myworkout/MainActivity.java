package com.rocasoftware.myworkout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.splashscreen.SplashScreen;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.protobuf.Internal;
import com.rocasoftware.myworkout.models.Exercise;
import com.rocasoftware.myworkout.models.ExerciseToDo;
import com.rocasoftware.myworkout.models.Muscle;
import com.rocasoftware.myworkout.models.Repetition;
import com.rocasoftware.myworkout.models.Sortbydate;
import com.rocasoftware.myworkout.models.Workout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    //Elementos graficos
    ScrollView scrollView;
    Button beginWorkoutBt;
    TextView helloText, lastWorkoutText, exerciseListText;

    //Firebase
    FirebaseAuth localAuth = FirebaseAuth.getInstance();
    FirebaseFirestore store = FirebaseFirestore.getInstance();

    //Informacion de la aplicacion
    ArrayList<Workout> workouts = new ArrayList<>();
    ArrayList<Muscle> muscleGroups = new ArrayList<>();
    ArrayList<ExerciseToDo> exerciseArray = new ArrayList<>();

    String userName = "";

    Boolean loadEx = false;
    Boolean loadWorkouts = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Mostramos el splash screen hasta que se carguen todos los datos
        final View content = findViewById(android.R.id.content);
        content.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        if (loadDone()) {
                            content.getViewTreeObserver().removeOnPreDrawListener(this);
                            return true;
                        } else return false;
                    }
                });


        Toolbar myToolBar = findViewById(R.id.toolbar);
        scrollView = findViewById(R.id.horizontalScrollView);
        beginWorkoutBt = findViewById(R.id.beginWorkoutBt);
        helloText = findViewById(R.id.helloText);
        lastWorkoutText = findViewById(R.id.lastWorkoutText);
        exerciseListText = findViewById(R.id.exerciseList);

        setHelloText();

        if (getIntent().getParcelableArrayListExtra("workouts") != null) {
            workouts = getIntent().getParcelableArrayListExtra("workouts");
            muscleGroups = getIntent().getParcelableArrayListExtra("muscles");
            exerciseArray = getIntent().getParcelableArrayListExtra("exercisesToDo");
            loadEx = true;
            loadWorkouts = true;
            setLastWorkoutText();
        } else {
            workouts = new ArrayList<>();
            loadExercisesToDo();
            loadMuscleGroups();
        }

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

        beginWorkoutBt.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), NewWorkoutActivity.class);
            putExtras(intent);
            startActivity(intent);

        });
    }

//    public void getWorkouts() {
//        Toast.makeText(this, "Cargamos workouts", Toast.LENGTH_SHORT).show();
//        runOnUiThread(() -> store.collection("Trainings")
//                .whereEqualTo("User", localAuth.getCurrentUser().getEmail())
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        for (QueryDocumentSnapshot document : task.getResult()) {
//                            String workoutId = document.getId();
//                            String startDate = document.getString("Start");
//                            String finishDate = document.getString("Finish");
//
//                            Workout work = new Workout(new ArrayList<>(), startDate, finishDate, workoutId);
//                            workouts.add(work);
//                        }
//
//                        //Ordenamos los entrenamientos de mas antiguo a mas nuevo
//                        Collections.sort(workouts, new Sortbydate());
//                        //Recorremos los entrenamientos y obtenemos sus ejericios
//                        for (int i = 0; i < workouts.size(); i++) {
//                            int finalI = i;
//                            store.collection("Exercises")
//                                    .whereEqualTo("TrainingId", workouts.get(i).getWorkoutId())
//                                    .get()
//                                    .addOnCompleteListener(task12 -> {
//                                        if (task12.isSuccessful()) {
//                                            for (QueryDocumentSnapshot document12 : task12.getResult()) {
//                                                String muscleId = document12.getString("MuscleId");
//                                                int index = muscleGroups.indexOf(new Muscle(muscleId, ""));
//                                                String exerciseName = document12.getString("Name");
//                                                String exerciseId = document12.getId();
//
//                                                int nextWorkout = 0;
//                                                if (document12.getString("nextWorkout") != null) {
//                                                    nextWorkout = Integer.parseInt(document12.getString("nextWorkout"));
//                                                }
//                                                Exercise exercise = new Exercise(exerciseName, new ArrayList<>(), muscleGroups.get(index), nextWorkout);
//                                                workouts.get(finalI).getExercises().add(exercise);
//                                            }
//
//                                            if (finalI == (workouts.size() - 1)) {
//                                                for (int j = 0; j < workouts.size(); j++) {
//                                                    int finalJ = j;
//                                                    for (int k = 0; k < workouts.get(j).getExercises().size(); k++) {
//                                                        int finalK = k;
//                                                        Exercise exercise = workouts.get(j).getExercises().get(k);
//                                                        store.collection("Repetitions")
//                                                                .whereEqualTo("ExerciseId", exercise.getExerciseId())
//                                                                //.orderBy("order")
//                                                                .get()
//                                                                .addOnCompleteListener(task1 -> {
//                                                                    if (task1.isSuccessful()) {
//                                                                        for (QueryDocumentSnapshot document1 : task1.getResult()) {
//                                                                            String repetitionId = document1.getId();
//                                                                            int kilos = Integer.parseInt(document1.getString("Kilos"));
//                                                                            int numReps = Integer.parseInt(document1.getString("Number"));
//                                                                            String exerciseId = document1.getString("ExerciseId");
//                                                                            int order = 0;
//                                                                            if (document1.getString("order") != null)
//                                                                                order = Integer.parseInt(document1.getString("order"));
//                                                                            exercise.addRepetition(new Repetition(numReps, kilos, repetitionId, exerciseId, order));
//                                                                        }
//
//                                                                        if ((finalJ == (workouts.size() - 1)) && (finalK == workouts.get(finalJ).getExercises().size() - 1)) {
//                                                                            WorkoutFragment workoutFragment = WorkoutFragment.newInstance(1, workouts, muscleGroups, exerciseArray);
//                                                                            getSupportFragmentManager().beginTransaction().replace(R.id.workoutsDone, workoutFragment).addToBackStack(null).commit();
//                                                                            int a = 4;
//                                                                        }
//                                                                    } else
//                                                                        Log.d("TAG", "Error getting documents: ", task1.getException());
//                                                                });
//                                                    }
//                                                }
//                                            }
//                                        } else
//                                            Log.d("TAG", "Error getting documents: ", task12.getException());
//                                    });
//                        }
//                    } else Log.d("TAG", "Error getting documents: ", task.getException());
//                }));
//    }

    public void loadExercisesToDo() {
        if (exerciseArray.isEmpty()) {
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
                                loadEx = true;
                            } else {
                                Log.d("TAG", "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
    }

    public void loadMuscleGroups() {
        if (muscleGroups.isEmpty()) {
            store.collection("Muscles").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        muscleGroups.add(new Muscle(document.getId(), document.getString("Name")));
                    }
                    loadWorkouts();
                }
            });
        }
    }

    public void loadWorkouts() {
        if (workouts.isEmpty()) {
            Toast.makeText(this, "Cargamos entrenamientos", Toast.LENGTH_SHORT).show();
            store.collection("Trainings")
                    .whereEqualTo("User", localAuth.getCurrentUser().getEmail())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String workoutId = document.getId();
                                String startDate = document.getString("Start");
                                String finishDate = document.getString("Finish");
                                HashMap<String, Object> exercises = (HashMap<String, Object>) document.get("Exercises");
                                ArrayList<Exercise> exerciseList = new ArrayList<>();
                                for (Map.Entry exercise : exercises.entrySet()) {
                                    ArrayList<Repetition> repetitionsList = new ArrayList<>();
                                    HashMap<String, Object> exInfo = (HashMap<String, Object>) exercise.getValue();
                                    HashMap<String, Object> repetitions = (HashMap<String, Object>) exInfo.get("Repetitions");
                                    for (Map.Entry repetition : repetitions.entrySet()) {
                                        HashMap<String, Object> repInfo = (HashMap<String, Object>) repetition.getValue();
                                        Repetition rep = new Repetition(Integer.parseInt((String) repInfo.get("Number")), Integer.parseInt((String)repInfo.get("Kilos")), Integer.parseInt((String)repetition.getKey()));
                                        repetitionsList.add(rep);
                                    }
                                    int nextWorkout = Integer.parseInt((String)exInfo.get("nextWorkout"));
                                    int index = muscleGroups.indexOf(new Muscle((String)exInfo.get("MuscleId"), ""));
                                    Exercise ex = new Exercise((String) exercise.getKey(),repetitionsList,muscleGroups.get(index), nextWorkout);
                                    exerciseList.add(ex);
                                }
                                workouts.add(new Workout(exerciseList, startDate, finishDate, workoutId));
                            }
                            Collections.sort(workouts, new Sortbydate());
                            setLastWorkoutText();
                            loadWorkouts = true;
                        }
                    });
        }
    }

//    public void loadLastWorkoutData() {
//        store.collection("Exercises")
//                .whereEqualTo("TrainingId", workouts.get(0).getWorkoutId())
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                String exerciseName = document.getString("Name");
//                                String exerciseId = document.getId();
//                                String muscleId = document.getString("MuscleId");
//                                int nextWorkout = 0;
//                                if (document.getString("nextWorkout") != null) {
//                                    nextWorkout = Integer.parseInt(document.getString("nextWorkout"));
//                                }
//                                int index = muscleGroups.indexOf(new Muscle(muscleId, ""));
//                                Exercise exercise = new Exercise(exerciseName, new ArrayList<>(), muscleGroups.get(index), nextWorkout);
//                                workouts.get(0).getExercises().add(exercise);
//                            }
//                            setLastWorkoutText();
//                        }
//                    }
//                });
//    }

    public void setLastWorkoutText() {
        try {
            Date start = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z").parse(workouts.get(0).getStartDate());
            Date finish = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z").parse(workouts.get(0).getFinishDate());
            long diff = finish.getTime() - start.getTime();

            int rounded = (int) Math.round(diff);

            int minutes = ((rounded / 60000) % 60);
            int hours = ((rounded / 3600000) % 24);
            lastWorkoutText.setText("Último: " + hours + "h " + minutes + "m | " + workouts.get(0).getExercises().size() + " ejercicios");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String exList = "";
        for (int i = 0; i < workouts.get(0).getExercises().size(); i++) {
            exList += workouts.get(0).getExercises().get(i).getName();
            if (i != workouts.get(0).getExercises().size() - 1) exList += ", ";
        }
        exerciseListText.setText(exList);
    }

//    public void loadWorkoutEx() {
//        for (int i = 0; i < workouts.size(); i++) {
//            int finalI = i;
//            store.collection("Exercises")
//                    .whereEqualTo("TrainingId", workouts.get(i).getWorkoutId())
//                    .get()
//                    .addOnCompleteListener(task -> {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document12 : task.getResult()) {
//                                String exerciseName = document12.getString("Name");
//                                String exerciseId = document12.getId();
//                                String muscleId = document12.getString("MuscleId");
//                                int nextWorkout = 0;
//                                if (document12.getString("nextWorkout") != null) {
//                                    nextWorkout = Integer.parseInt(document12.getString("nextWorkout"));
//                                }
//                                int index = muscleGroups.indexOf(new Muscle(muscleId, ""));
//                                Exercise exercise = new Exercise(exerciseName, new ArrayList<>(), muscleGroups.get(index), nextWorkout);
//                                workouts.get(finalI).getExercises().add(exercise);
//                            }
//
//                        } else
//                            Log.d("TAG", "Error getting documents: ", task.getException());
//                    });
//        }
//    }

//    public void loadWorkExRep() {
//        for (int j = 0; j < workouts.size(); j++) {
//            for (int k = 0; k < workouts.get(j).getExercises().size(); k++) {
//                Exercise exercise = workouts.get(j).getExercises().get(k);
//                store.collection("Repetitions")
//                        .whereEqualTo("ExerciseId", exercise.getExerciseId())
//                        .get()
//                        .addOnCompleteListener(task -> {
//                            if (task.isSuccessful()) {
//                                for (QueryDocumentSnapshot document1 : task.getResult()) {
//                                    String repetitionId = document1.getId();
//                                    int kilos = Integer.parseInt(document1.getString("Kilos"));
//                                    int numReps = Integer.parseInt(document1.getString("Number"));
//                                    String exerciseId = document1.getString("ExerciseId");
//                                    int order = 0;
//                                    if (document1.getString("order") != null)
//                                        order = Integer.parseInt(document1.getString("order"));
//                                    exercise.addRepetition(new Repetition(numReps, kilos, order));
//                                }
//                                //Ordenar las repeticiones
//                            } else
//                                Log.d("TAG", "Error getting documents: ", task.getException());
//                        });
//            }
//        }
//    }

    public boolean loadDone() {
        return loadEx && loadWorkouts;
    }

    public void putExtras(Intent intent) {
        intent.putExtra("workouts", workouts);
        intent.putExtra("muscles", muscleGroups);
        intent.putExtra("exercisesToDo", exerciseArray);
    }

    public void setHelloText() {
        if (userName.equals("")) {
            store.collection("Users").document(localAuth.getCurrentUser().getEmail()).get().addOnSuccessListener(documentSnapshot -> {
                userName = documentSnapshot.getString("Name");
                helloText.setText("Hola, " + userName);
            });
        } else helloText.setText("Hola, " + userName);
    }

    public void saveWorkouts() {
        for (int l = 0; l < workouts.size(); l++) {
            Workout actualWorkout = workouts.get(l);

            FirebaseUser user = localAuth.getCurrentUser();
            DocumentReference dr = store.collection("Trainings").document();
            Map<String, Object> trainingInfo = new HashMap<>();
            Map<String, Object> exercises = new HashMap<>();
            trainingInfo.put("User", user.getEmail());
            trainingInfo.put("Start", actualWorkout.getStartDate());
            trainingInfo.put("Finish", actualWorkout.getFinishDate());

            for (int s = 0; s < actualWorkout.getExercises().size(); s++) {
                Exercise ex = actualWorkout.getExercises().get(s);
                Map<String, Object> exerciseInfo = new HashMap<>();
                Map<String, Object> repetitionsInfo = new HashMap<>();
                exerciseInfo.put("MuscleId", ex.getMuscle().getMuscleId());
                exerciseInfo.put("nextWorkout", ex.getNextWorkout() + "");
                for (int d = 0; d < ex.getRepetitions().size(); d++) {
                    Map<String, Object> repInfo = new HashMap<>();
                    repInfo.put("Number", ex.getRepetitions().get(d).getRepNumber() + "");
                    repInfo.put("Kilos", ex.getRepetitions().get(d).getKilos() + "");
                    repetitionsInfo.put((d + 1) + "", repInfo);
                }
                exerciseInfo.put("Repetitions", repetitionsInfo);
                exercises.put(ex.getName(), exerciseInfo);
            }
            trainingInfo.put("Exercises", exercises);
            dr.set(trainingInfo);
        }
    }
}