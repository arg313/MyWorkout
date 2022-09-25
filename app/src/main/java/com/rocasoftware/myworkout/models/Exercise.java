package com.rocasoftware.myworkout.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Exercise implements Parcelable {

    String name;
    ArrayList<Repetition> repetitions;
    String exerciseId;
    Muscle muscle;
    int nextWorkout;

    public Exercise() {
        this.name = "";
        this.repetitions = new ArrayList<>();
        this.muscle = new Muscle();
        this.nextWorkout = 0;
    }

    public Exercise (String name, ArrayList<Repetition> repetitions, String exerciseId, Muscle muscle, int nextWorkout) {
        this.name = name;
        this.repetitions = repetitions;
        this.exerciseId = exerciseId;
        this.muscle = muscle;
        this.nextWorkout = nextWorkout;
    }


    protected Exercise(Parcel in) {
        name = in.readString();
        repetitions = in.createTypedArrayList(Repetition.CREATOR);
        exerciseId = in.readString();
        muscle = in.readParcelable(Muscle.class.getClassLoader());
        nextWorkout = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeTypedList(repetitions);
        dest.writeString(exerciseId);
        dest.writeParcelable(muscle, flags);
        dest.writeInt(nextWorkout);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Exercise> CREATOR = new Creator<Exercise>() {
        @Override
        public Exercise createFromParcel(Parcel in) {
            return new Exercise(in);
        }

        @Override
        public Exercise[] newArray(int size) {
            return new Exercise[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Repetition> getRepetitions() {
        return repetitions;
    }

    public void setRepetitions(ArrayList<Repetition> repetitions) {
        this.repetitions = repetitions;
    }

    public String getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(String exerciseId) {
        this.exerciseId = exerciseId;
    }

    public Muscle getMuscle() {
        return muscle;
    }

    public void setMuscle(Muscle muscle) {
        this.muscle = muscle;
    }

    public void setMuscle (String muscleName, ArrayList<Muscle> muscleGroups) {
        for (int i = 0; i<muscleGroups.size();i++) {
            if (muscleName.equals(muscleGroups.get(i).getName())) this.muscle = muscleGroups.get(i);
        }
    }

    public int getNextWorkout() {
        return nextWorkout;
    }

    public void setNextWorkout(int nextWorkout) {
        this.nextWorkout = nextWorkout;
    }

    public void addRepetition (Repetition repetition) {
        repetitions.add(repetition);
    }



}
