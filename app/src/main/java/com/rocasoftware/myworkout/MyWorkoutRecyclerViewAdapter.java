package com.rocasoftware.myworkout;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.rocasoftware.myworkout.models.ExerciseToDo;
import com.rocasoftware.myworkout.models.Muscle;
import com.rocasoftware.myworkout.models.Workout;
import com.rocasoftware.myworkout.databinding.FragmentWorkoutBinding;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MyWorkoutRecyclerViewAdapter extends RecyclerView.Adapter<MyWorkoutRecyclerViewAdapter.ViewHolder> {

    private ArrayList<Workout> mValues;
    ArrayList<Muscle> muscleGroups;
    ArrayList<ExerciseToDo> exerciseArray;

    public MyWorkoutRecyclerViewAdapter(ArrayList<Workout> items, ArrayList<Muscle> muscleGroups, ArrayList<ExerciseToDo> exerciseArray) {
        mValues = items;
        this.muscleGroups = muscleGroups;
        this.exerciseArray = exerciseArray;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(FragmentWorkoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);

        SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        SimpleDateFormat myFormat = new SimpleDateFormat("dd MMMM yyyy");

        try {
            String reformattedStr = myFormat.format(fromUser.parse(holder.mItem.getFinishDate()));
            holder.workoutDate.setText(reformattedStr);
        } catch (ParseException e) {
            holder.workoutDate.setText(holder.mItem.getFinishDate());
            e.printStackTrace();
        }

        try {
            Date start = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z").parse(holder.mItem.getStartDate());
            Date finish = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z").parse(holder.mItem.getFinishDate());
            long diff = finish.getTime() - start.getTime();

            int rounded = (int)Math.round(diff);

            int minutes = ((rounded / 60000) % 60);
            int hours = ((rounded / 3600000) % 24);
            holder.durationNumEx.setText(hours+"h "+minutes+"m | "+holder.mItem.getExercises().size()+" ejercicios");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String exList = "";
        for (int i = 0; i < holder.mItem.getExercises().size(); i++) {
            exList += holder.mItem.getExercises().get(i).getName();
            if (i != holder.mItem.getExercises().size()-1) exList += ", ";
        }
        holder.exerciseList.setText(exList);

        holder.seeDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), WorkoutDetailActivity.class);
                intent.putExtra("actual",holder.mItem);
                intent.putExtra("workouts", mValues);
                intent.putExtra("muscles", muscleGroups);
                intent.putExtra("exercisesToDo", exerciseArray);
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public Workout mItem;
        public TextView workoutDate;
        public TextView durationNumEx;
        public TextView exerciseList;
        public Button seeDetails;

        public ViewHolder(FragmentWorkoutBinding binding) {
            super(binding.getRoot());
            workoutDate = binding.workoutDate;
            durationNumEx = binding.durationNumEx;
            exerciseList = binding.exerciseList;
            seeDetails = binding.seeDetails;
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }
}