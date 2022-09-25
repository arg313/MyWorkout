package com.rocasoftware.myworkout.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Muscle implements Parcelable{

    String muscleId;
    String name;

    public Muscle () {

    }

    public Muscle (String muscleId, String name) {
        this.muscleId = muscleId;
        this.name = name;
    }

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

    protected Muscle(Parcel in) {
        muscleId = in.readString();
        name = in.readString();
    }

    public static final Creator<Muscle> CREATOR = new Creator<Muscle>() {
        @Override
        public Muscle createFromParcel(Parcel in) {
            return new Muscle(in);
        }

        @Override
        public Muscle[] newArray(int size) {
            return new Muscle[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(muscleId);
        parcel.writeString(name);
    }

    public String toString() {
        return this.name;
    }


    public boolean isEquivalent(Muscle ct) {
        if (this.getMuscleId().equals(ct.getMuscleId())) return true;
        else return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Muscle) {
            return this.isEquivalent((Muscle) obj);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
