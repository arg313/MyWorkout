package com.rocasoftware.myworkout.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Repetition implements Parcelable {

    int repNumber;
    int kilos;
    int order;

    public Repetition(int repNumber, int kilos, int order) {
        this.repNumber = repNumber;
        this.kilos = kilos;
        this.order = order;
    }


    protected Repetition(Parcel in) {
        repNumber = in.readInt();
        kilos = in.readInt();
        order = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(repNumber);
        dest.writeInt(kilos);
        dest.writeInt(order);
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

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
