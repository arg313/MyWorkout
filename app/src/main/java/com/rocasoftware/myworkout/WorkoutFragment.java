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

import com.rocasoftware.myworkout.models.ExerciseToDo;
import com.rocasoftware.myworkout.models.Muscle;
import com.rocasoftware.myworkout.models.Workout;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class WorkoutFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private ArrayList<Workout> workouts = new ArrayList<>();
    private ArrayList<Muscle> muscleGroups = new ArrayList<>();
    private ArrayList<ExerciseToDo> exerciseArray = new ArrayList<>();

    public WorkoutFragment() {
    }

    public static WorkoutFragment newInstance(int columnCount, ArrayList<Workout> workouts, ArrayList<Muscle> muscleGroups, ArrayList<ExerciseToDo> exerciseArray) {
        WorkoutFragment fragment = new WorkoutFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putParcelableArrayList("workouts", workouts);
        args.putParcelableArrayList("muscles", muscleGroups);
        args.putParcelableArrayList("exercisesToDo", exerciseArray);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            workouts = getArguments().getParcelableArrayList("workouts");
            muscleGroups = getArguments().getParcelableArrayList("muscles");
            exerciseArray = getArguments().getParcelableArrayList("exercisesToDo");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workout_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new MyWorkoutRecyclerViewAdapter(workouts, muscleGroups, exerciseArray));
        }
        return view;
    }
}