package com.rocasoftware.myworkout.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Repetition implements Parcelable {

    int repNumber;
    int kilos;
    String repetitionId;
    String exerciseId;

    public Repetition(int repNumber, int kilos, String repetitionId, String exerciseId) {
        this.repNumber = repNumber;
        this.kilos = kilos;
        this.repetitionId = repetitionId;
        this.exerciseId = exerciseId;
    }


    protected Repetition(Parcel in) {
        repNumber = in.readInt();
        kilos = in.readInt();
        repetitionId = in.readString();
        exerciseId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(repNumber);
        dest.writeInt(kilos);
        dest.writeString(repetitionId);
        dest.writeString(exerciseId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Repetition> CREATOR = new Creator<Repetition>() {
        @Override
        public Repetition createFromParcel(Parcel in) {
            return new Repetition(in);
        }

        @Override
        public Repetition[] newArray(int size) {
            return new Repetition[size];
        }
    };

    public int getRepNumber() {
        return repNumber;
    }

    public void setRepNumber(int repNumber) {
        this.repNumber = repNumber;
    }

    public int getKilos() {
        return kilos;
    }

    public void setKilos(int kilos) {
        this.kilos = kilos;
    }

    public String getRepetitionId() {
        return repetitionId;
    }

    public void setRepetitionId(String repetitionId) {
        this.repetitionId = repetitionId;
    }

    public String getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(String exerciseId) {
        this.exerciseId = exerciseId;
    }


}
