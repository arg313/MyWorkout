package com.rocasoftware.myworkout;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.rocasoftware.myworkout.databinding.FragmentWorkoutExerciseBinding;
import com.rocasoftware.myworkout.models.Exercise;
import com.rocasoftware.myworkout.models.ExerciseToDo;
import com.rocasoftware.myworkout.models.Muscle;
import com.rocasoftware.myworkout.models.MyTimer;
import com.rocasoftware.myworkout.models.Repetition;
import com.rocasoftware.myworkout.models.Workout;
import java.util.ArrayList;

public class MyWorkoutExerciseRecyclerViewAdapter extends RecyclerView.Adapter<MyWorkoutExerciseRecyclerViewAdapter.ViewHolder> {

    private final ArrayList<Exercise> mValues;
    private final Workout actualWorkout;
    private final ArrayList<ExerciseToDo> exercisesArray;
    private final ArrayList<Workout> workouts;
    private final ArrayList<Muscle> muscleGroups;
    private final String mode;

    public MyWorkoutExerciseRecyclerViewAdapter(ArrayList<Exercise> items, Workout actualWorkout, ArrayList<ExerciseToDo> exercisesArray, ArrayList<Workout> workouts, ArrayList<Muscle> muscleGroups, String mode) {
        mValues = items;
        this.actualWorkout = actualWorkout;
        this.exercisesArray = exercisesArray;
        this.workouts = workouts;
        this.muscleGroups = muscleGroups;
        this.mode = mode;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(FragmentWorkoutExerciseBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.exerciseName.setText(mValues.get(position).getName());


        if (mode.equals("detail")) {
            holder.editButton.setVisibility(View.GONE);
        } else {
            holder.editButton.setOnClickListener(view -> {
                Intent intent = new Intent(view.getContext(), NewExerciseActivity.class);
                intent.putExtra("title","Editar ejercicio");
                intent.putExtra("workout",actualWorkout);
                intent.putExtra("exercisesToDo", exercisesArray);
                intent.putExtra("exercise",holder.mItem);
                intent.putExtra("index", position);
                intent.putExtra("workouts", workouts);
                intent.putExtra("muscles", muscleGroups);
                view.getContext().startActivity(intent);
            });
        }

        holder.exerciseGroup.setText(holder.mItem.getMuscle().getName()+" ("+holder.mItem.getNextWorkout()+")");

        ArrayList<Repetition> reps = holder.mItem.getRepetitions();
        String repsTxt = "";
        for (int i = 0; i < reps.size(); i++) {
            repsTxt += reps.get(i).getRepNumber()+" reps - "+reps.get(i).getKilos()+" Kg"+" \n";
        }

        holder.repsTextView.setText(repsTxt);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public Exercise mItem;
        public CardView cardView;
        public Button editButton;
        public TextView exerciseName;
        public TextView exerciseGroup;
        public TextView repsTextView;

        public ViewHolder(FragmentWorkoutExerciseBinding binding) {
            super(binding.getRoot());
            cardView = binding.baseCardview;
            editButton = binding.editExercise;
            exerciseName = binding.exerciseName;
            exerciseGroup = binding.exerciseGroup;
            repsTextView = binding.repsTextView;
        }

        @Override
        public String toString() {
            return super.toString() + " '";
        }
    }
}