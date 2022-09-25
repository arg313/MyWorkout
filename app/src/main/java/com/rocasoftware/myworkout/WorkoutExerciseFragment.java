package com.rocasoftware.myworkout;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rocasoftware.myworkout.models.Exercise;
import com.rocasoftware.myworkout.models.ExerciseToDo;
import com.rocasoftware.myworkout.models.Muscle;
import com.rocasoftware.myworkout.models.MyTimer;
import com.rocasoftware.myworkout.models.Workout;

import java.util.ArrayList;

public class WorkoutExerciseFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private ArrayList<Exercise> exercises = new ArrayList<>();
    private Workout actualWorkout;
    private ArrayList<ExerciseToDo> exercisesArray = new ArrayList<>();
    private ArrayList<Workout> workouts = new ArrayList<>();
    ArrayList<Muscle> muscleGroups = new ArrayList<>();
    String mode = "";

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public WorkoutExerciseFragment() {
    }

    @SuppressWarnings("unused")
    public static WorkoutExerciseFragment newInstance(int columnCount, ArrayList<Exercise> exercises, Workout actualWorkout, ArrayList<ExerciseToDo> exercisesArray, ArrayList<Workout> workouts, ArrayList<Muscle> muscleGroups, String mode) {
        WorkoutExerciseFragment fragment = new WorkoutExerciseFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putParcelableArrayList("exercises",exercises);
        args.putParcelable("workout",actualWorkout);
        args.putParcelableArrayList("exercisesToDo", exercisesArray);
        args.putParcelableArrayList("workouts", workouts);
        args.putParcelableArrayList("muscles", muscleGroups);
        args.putString("mode",mode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            exercises = getArguments().getParcelableArrayList("exercises");
            actualWorkout = getArguments().getParcelable("workout");
            exercisesArray = getArguments().getParcelableArrayList("exercisesToDo");
            workouts = getArguments().getParcelableArrayList("workouts");
            muscleGroups = getArguments().getParcelableArrayList("muscles");
            mode = getArguments().getString("mode");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workout_exercise_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new MyWorkoutExerciseRecyclerViewAdapter(exercises, actualWorkout, exercisesArray, workouts, muscleGroups, mode));
        }
        return view;
    }
}