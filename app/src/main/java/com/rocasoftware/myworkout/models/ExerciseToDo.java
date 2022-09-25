package com.rocasoftware.myworkout.models;

import android.os.Parcel;
import android.os.Parcelable;

public class ExerciseToDo implements Parcelable {

    String exerciseToDoId;
    String muscleId;
    String name;

    public ExerciseToDo(String exerciseToDoId, String muscleId, String name) {
        this.exerciseToDoId = exerciseToDoId;
        this.muscleId = muscleId;
        this.name = name;
    }

    protected ExerciseToDo(Parcel in) {
        exerciseToDoId = in.readString();
        muscleId = in.readString();
        name = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(exerciseToDoId);
        dest.writeString(muscleId);
        dest.writeString(name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ExerciseToDo> CREATOR = new Creator<ExerciseToDo>() {
        @Override
        public ExerciseToDo createFromParcel(Parcel in) {
            return new ExerciseToDo(in);
        }

        @Override
        public ExerciseToDo[] newArray(int size) {
            return new ExerciseToDo[size];
        }
    };

    public String getMuscleId() {
        return muscleId;
    }

    public void setMuscleId(String muscleId) {
        this.muscleId = muscleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExerciseToDoId() {
        return exerciseToDoId;
    }

    public void setExerciseToDoId(String exerciseToDoId) {
        this.exerciseToDoId = exerciseToDoId;
    }

    public String toString() {
        return this.name;
    }


}
