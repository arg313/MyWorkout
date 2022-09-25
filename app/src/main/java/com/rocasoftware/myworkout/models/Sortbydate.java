package com.rocasoftware.myworkout.models;

import java.util.Comparator;

public class Sortbydate implements Comparator<Workout> {

    @Override
    public int compare(Workout workout, Workout t1) {
        return t1.getStartDate().compareTo(workout.getStartDate());
    }

    @Override
    public Comparator<Workout> reversed() {
        return Comparator.super.reversed();
    }
}
