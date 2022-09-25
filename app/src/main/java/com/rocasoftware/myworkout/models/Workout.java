package com.rocasoftware.myworkout.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

public class Workout implements Parcelable {

    ArrayList<Exercise> exercises;
    String startDate;
    String finishDate;
    String workoutId;

    public Workout () {
        this.exercises = new ArrayList<>();
    }

    public Workout (ArrayList<Exercise> exercises, String startDate, String finishDate, String workoutId) {
        this.exercises = exercises;
        this.startDate = startDate;
        this.finishDate = finishDate;
        this.workoutId = workoutId;
    }

    protected Workout(Parcel in) {
        exercises = in.createTypedArrayList(Exercise.CREATOR);
        startDate = in.readString();
        finishDate = in.readString();
        workoutId = in.readString();
    }

    public static final Creator<Workout> CREATOR = new Creator<Workout>() {
        @Override
        public Workout createFromParcel(Parcel in) {
            return new Workout(in);
        }

        @Override
        public Workout[] newArray(int size) {
            return new Workout[size];
        }
    };

    public ArrayList<Exercise> getExercises() {
        return exercises;
    }

    public void setExercises(ArrayList<Exercise> exercises) {
        this.exercises = exercises;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(String finishDate) {
        this.finishDate = finishDate;
    }

    public String getWorkoutId() {
        return workoutId;
    }

    public void setWorkoutId(String workoutId) {
        this.workoutId = workoutId;
    }

    public void addExercise (Exercise exercise) {
        this.exercises.add(exercise);
    }

    public void editExercise (int old, Exercise edit) {
        this.exercises.remove(old);
        this.exercises.add(old, edit);
    }

    public Date getStartDateObject() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        try {
            return formatter.parse(this.getStartDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Date getFinishDateObject() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        try {
            return formatter.parse(this.getFinishDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedList(exercises);
        parcel.writeString(startDate);
        parcel.writeString(finishDate);
        parcel.writeString(workoutId);
    }
}

