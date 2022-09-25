package com.rocasoftware.myworkout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentContainerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.rocasoftware.myworkout.models.Exercise;
import com.rocasoftware.myworkout.models.ExerciseToDo;
import com.rocasoftware.myworkout.models.Muscle;
import com.rocasoftware.myworkout.models.MyTimer;
import com.rocasoftware.myworkout.models.Repetition;
import com.rocasoftware.myworkout.models.Workout;

import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class NewExerciseActivity extends AppCompatActivity {

    //Elementos graficos
    Button cancelBt, addRepBt, acceptBt;
    AutoCompleteTextView exerciseDropdown;
    FragmentContainerView fragments;
    ExerciseRepFragment exerciseRepFragment;
    MaterialCardView lastWorkoutCardview;
    TextView repsTextView, nextWorkoutTextView;

    //Firebase
    FirebaseFirestore store = FirebaseFirestore.getInstance();

    //Informacion de la aplicacion
    Exercise actualExercise;
    Workout actualWorkout;
    ArrayList<ExerciseToDo> exerciseArray = new ArrayList<>();
    ArrayList<String> exerciseString = new ArrayList<>();
    ArrayList<Workout> workouts = new ArrayList<>();
    ArrayList<Muscle> muscleGroups = new ArrayList<>();
    ExerciseToDo selectedExe;
    MyTimer timer;
    int oldExercise = -1;
    String title = "Nuevo ejercicio";
    int nextWorkout = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_exercise);

        Toolbar myToolBar = findViewById(R.id.toolbar);
        title = getIntent().getStringExtra("title");
        if (title != null) myToolBar.setTitle(title);
        setSupportActionBar(myToolBar);

        actualWorkout = getIntent().getParcelableExtra("workout");
        exerciseArray = getIntent().getParcelableArrayListExtra("exercisesToDo");
        workouts = getIntent().getParcelableArrayListExtra("workouts");
        muscleGroups = getIntent().getParcelableArrayListExtra("muscles");

        exerciseDropdown = findViewById(R.id.exerciseDropdown);
        lastWorkoutCardview = findViewById(R.id.lastWorkoutCardview);
        repsTextView = findViewById(R.id.repsTextView);
        nextWorkoutTextView = findViewById(R.id.nextWorkoutView);
        //lastWorkoutCardview.setVisibility(View.GONE);

        //Cuando se cambia a modo oscuro, guardar la informacion de las series y del ejercicio seleccionado

        ArrayAdapter<ExerciseToDo> exerciseAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, exerciseArray);
        exerciseDropdown.setAdapter(exerciseAdapter);
        exerciseDropdown.setOnItemClickListener((adapterView, view, i, l) -> {
            selectedExe = (ExerciseToDo) adapterView.getAdapter().getItem(i);
            exerciseDropdown.setFocusable(false);
            exerciseDropdown.setEnabled(false);
            exerciseDropdown.setAdapter(null);
            //Buscamos las repeticiones realizadas en el ultimo workouts
            String repsString = "";
            for (int j = 0; j<workouts.size();j++) {
                if (!repsString.equals("")) break;
                Workout work = workouts.get(j);
                ArrayList<Exercise> lastWorkEx = work.getExercises();
                for (int k = 0; k<lastWorkEx.size(); k++) {
                    Exercise lastEx = lastWorkEx.get(k);
                    if (lastEx.getName().equals(selectedExe.getName())) {
                        ArrayList<Repetition> lastReps = lastEx.getRepetitions();
                        for (int a = 0; a < lastReps.size();a++) {
                            repsString += lastReps.get(a).getRepNumber()+"reps - "+lastReps.get(a).getKilos()+" kg\n";
                        }
                        nextWorkoutTextView.setText(lastEx.getNextWorkout()+"");
                        repsTextView.setText(repsString);
                        //lastWorkoutCardview.setVisibility(View.VISIBLE);
                        break;
                    }
                }
            }
            //if (repsString.equals("")) lastWorkoutCardview.setVisibility(View.GONE);
            exerciseDropdown.setFocusable(true);
            exerciseDropdown.setEnabled(true);
            exerciseDropdown.setAdapter(exerciseAdapter);
        });

        timer = new MyTimer(getIntent().getDoubleExtra("timer", 0.0));
        timer.startTimer(this, null);

        if (getIntent().getParcelableExtra("exercise") != null) {
            actualExercise = getIntent().getParcelableExtra("exercise");
            oldExercise = getIntent().getIntExtra("index", -1);
            //Buscamos el ExerciseToDo con el nombre del ejercicio y lo establecemos en el dropdown
            for (int i=0; i<exerciseArray.size(); i++) {
                if(exerciseArray.get(i).getName().equals(actualExercise.getName())) {
                    exerciseDropdown.setListSelection(i);
                    selectedExe = exerciseArray.get(i);
                }
            }
            exerciseDropdown.setText(actualExercise.getName());
        } else actualExercise = new Exercise();

        exerciseRepFragment = ExerciseRepFragment.newInstance(1, actualExercise.getRepetitions());
        getSupportFragmentManager().beginTransaction().replace(R.id.exerciseDone, exerciseRepFragment).commit();

        fragments = findViewById(R.id.exerciseDone);

        cancelBt = findViewById(R.id.cancelBt);
        cancelBt.setOnClickListener(view -> {
            if (actualExercise.getRepetitions().isEmpty() && exerciseDropdown.getText().toString().isEmpty()) {
                goToNewWorkoutActivity(view.getContext());
            } else {
                new MaterialAlertDialogBuilder(NewExerciseActivity.this)
                        .setTitle("Cancelar ejercicio")
                        .setMessage("Los datos introducidos para este ejercicio no se guardarán. ¿Desea confirmar?")
                        .setPositiveButton("Confirmar", (dialogInterface, i) -> {
                            goToNewWorkoutActivity(view.getContext());
                        })
                        .setNegativeButton("Cancelar", (dialogInterface, i) -> {
                        })
                        .show();
            }
        });

        addRepBt = findViewById(R.id.addRep);
        addRepBt.setOnClickListener(view -> {
            Repetition rep = new Repetition(0, 0,  actualExercise.getRepetitions().size()+1);
            actualExercise.getRepetitions().add(rep);
            exerciseRepFragment = ExerciseRepFragment.newInstance(1, actualExercise.getRepetitions());
            getSupportFragmentManager().beginTransaction().replace(R.id.exerciseDone, exerciseRepFragment).commit();
        });

        View rate_layout = getLayoutInflater().inflate(R.layout.rate_popup, null);

        ImageView lessBt = rate_layout.findViewById(R.id.lessBt);
        ImageView equalBt = rate_layout.findViewById(R.id.equalBt);
        ImageView moreBt = rate_layout.findViewById(R.id.moreBt);

        lessBt.setAlpha(0.5F);
        equalBt.setAlpha(0.5F);
        moreBt.setAlpha(0.5F);

        lessBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextWorkout = -1;
                lessBt.setAlpha(1F);
                equalBt.setAlpha(0.5F);
                moreBt.setAlpha(0.5F);
            }
        });

        equalBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextWorkout = 0;
                lessBt.setAlpha(0.5F);
                equalBt.setAlpha(1F);
                moreBt.setAlpha(0.5F);
            }
        });

        moreBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextWorkout = 1;
                lessBt.setAlpha(0.5F);
                equalBt.setAlpha(0.5F);
                moreBt.setAlpha(1F);
            }
        });

        acceptBt = findViewById(R.id.acceptBt);
        acceptBt.setOnClickListener(view -> {
            if (actualExercise.getRepetitions().isEmpty()) {
                new MaterialAlertDialogBuilder(NewExerciseActivity.this)
                        .setTitle("Falta información")
                        .setMessage("Debes introducir por lo menos una repetición para guardar el ejercicio.")
                        .setNegativeButton("Aceptar", (dialogInterface, i) -> {
                        })
                        .show();
            } else if (exerciseDropdown.getText().toString().isEmpty()) {
                new MaterialAlertDialogBuilder(NewExerciseActivity.this)
                        .setTitle("Falta información")
                        .setMessage("Debes indicar el ejercicio que estás realizando para guardar el ejercicio.")
                        .setNegativeButton("Aceptar", (dialogInterface, i) -> {
                        })
                        .show();
            } else {
                new MaterialAlertDialogBuilder(NewExerciseActivity.this)
                        .setTitle("Para el próximo entrenamiento")
                        .setView(rate_layout)
                        .setMessage("En el próximo entrenamiento quieres aumentar el peso, mantenerlo o bajarlo?")
                        .setPositiveButton("Aceptar", (dialogInterface3, k) -> {
                            if (selectedExIsNew()) {
                                ExerciseToDo newExercise = new ExerciseToDo("IdTemporal","IdTemporal",exerciseDropdown.getText().toString());
                                new MaterialAlertDialogBuilder(NewExerciseActivity.this)
                                        .setTitle("Añadir ejercicio")
                                        .setMessage("Primero debes guardar el ejercicio para fuuros entrenamientos")
                                        .setPositiveButton("Sí", (dialogInterface, i) -> {
                                            View muscle_layout = getLayoutInflater().inflate(R.layout.muscle_popup, null);
                                            AutoCompleteTextView spinner = muscle_layout.findViewById(R.id.muscleDropdown);
                                            ArrayAdapter<Muscle> muscleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, muscleGroups);
                                            spinner.setAdapter(muscleAdapter);
                                            spinner.setOnItemClickListener((adapterView, view1, i1, l) -> newExercise.setMuscleId(muscleGroups.get(i1).getMuscleId()));

                                            new MaterialAlertDialogBuilder(NewExerciseActivity.this)
                                                    .setTitle("Grupo muscular")
                                                    .setView(muscle_layout)
                                                    .setMessage("Selecciona el grupo muscular que trabaja el nuevo ejercicio")
                                                    .setPositiveButton("Aceptar", (dialogInterface1, j) -> {
                                                        if (spinner.getText().toString().isEmpty()) {
                                                            Toast.makeText(this, "Debes seleccionar un grupo muscular", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            DocumentReference dr = store.collection("ExercisesToDo").document();
                                                            Map<String, Object> exerciseInfo = new HashMap<>();
                                                            exerciseInfo.put("Name", newExercise.getName());
                                                            exerciseInfo.put("MuscleId", newExercise.getMuscleId());
                                                            dr.set(exerciseInfo);

                                                            //Obtenemos el Id del nuevo ejercicio para guardarlo
                                                            store.collection("ExercisesToDo")
                                                                    .whereEqualTo("Name", newExercise.getName())
                                                                    .get()
                                                                    .addOnCompleteListener(task -> {
                                                                        if (task.isSuccessful()) {
                                                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                                                newExercise.setExerciseToDoId(document.getId());
                                                                                exerciseArray.add(newExercise);
                                                                                Muscle actualMuscle = new Muscle();
                                                                                for (int i12 = 0; i12 <muscleGroups.size(); i12++) {
                                                                                    if (muscleGroups.get(i12).getMuscleId().equals(newExercise.getMuscleId())) {
                                                                                        actualMuscle = muscleGroups.get(i12);
                                                                                    }
                                                                                }
                                                                                actualExercise.setMuscle(actualMuscle);
                                                                                actualExercise.setName(newExercise.getName());
                                                                                actualExercise.setNextWorkout(nextWorkout);
                                                                                if (oldExercise != -1)
                                                                                    actualWorkout.editExercise(oldExercise, actualExercise);
                                                                                else actualWorkout.addExercise(actualExercise);
                                                                                goToNewWorkoutActivity(view.getContext());
                                                                            }
                                                                        } else {
                                                                            Log.d("TAG", "Error getting documents: ", task.getException());
                                                                        }
                                                                    });
                                                        }
                                                    })
                                                    .setNegativeButton("Cancelar", (dialogInterface2, l) -> {
                                                    })
                                                    .show();
                                        })
                                        .setNeutralButton("Cancelar", (dialogInterface, i) -> {
                                        })
                                        .show();
                            } else {
                                actualExercise.setName(selectedExe.getName());
                                int index = muscleGroups.indexOf(new Muscle(selectedExe.getMuscleId(),""));
                                actualExercise.setMuscle(muscleGroups.get(index));
                                actualExercise.setNextWorkout(nextWorkout);

                                if (oldExercise != -1) actualWorkout.editExercise(oldExercise, actualExercise);
                                else actualWorkout.addExercise(actualExercise);
                                goToNewWorkoutActivity(view.getContext());
                            }
                        })
                        .setNegativeButton("Cancelar", (dialogInterface2, k) -> {
                        })
                        .show();


            }
        });
    }

    public void goToNewWorkoutActivity(Context context) {
        Intent intent = new Intent(context, NewWorkoutActivity.class);
        intent.putExtra("workout", actualWorkout);
        intent.putExtra("exercisesToDo", exerciseArray);
        intent.putExtra("workouts", workouts);
        intent.putExtra("timer", timer.getTime());
        intent.putExtra("muscles", muscleGroups);
        timer.getTimer().cancel();
        startActivity(intent);
    }

    public boolean selectedExIsNew() {
        for (int i = 0; i < exerciseArray.size(); i++) {
            String a = exerciseArray.get(i).getName();
            String b = exerciseDropdown.getText().toString();
            if (a.equals(b)) return false;
        }
        return true;
    }
}