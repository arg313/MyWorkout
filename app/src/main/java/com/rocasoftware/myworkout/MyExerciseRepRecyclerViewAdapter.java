package com.rocasoftware.myworkout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.rocasoftware.myworkout.models.Repetition;
import com.rocasoftware.myworkout.databinding.FragmentExerciseRepBinding;
import java.util.ArrayList;

public class MyExerciseRepRecyclerViewAdapter extends RecyclerView.Adapter<MyExerciseRepRecyclerViewAdapter.ViewHolder> {

    private ArrayList<Repetition> mValues;

    public MyExerciseRepRecyclerViewAdapter(ArrayList<Repetition> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(FragmentExerciseRepBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.repsTf.setText(mValues.get(position).getRepNumber()+"");
        holder.weightTf.setText(mValues.get(position).getKilos()+"");
        holder.deleteBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mValues.remove(position);
                ExerciseRepFragment exerciseRepFragment = ExerciseRepFragment.newInstance(1,mValues);
                AppCompatActivity activity = unwrap(view.getContext());
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.exerciseDone, exerciseRepFragment).addToBackStack(null).commit();
            }
        });

        holder.repsTf.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().isEmpty()) {
                    mValues.get(position).setRepNumber(Integer.parseInt(editable.toString()));
                }
            }
        });

        holder.weightTf.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().isEmpty()) {
                    mValues.get(position).setKilos(Integer.parseInt(editable.toString()));
                }
            }
        });

        holder.acceptBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mValues.get(position).setKilos(Integer.parseInt(holder.weightTf.getText().toString()));
                mValues.get(position).setRepNumber(Integer.parseInt(holder.repsTf.getText().toString()));
                ExerciseRepFragment exerciseRepFragment = ExerciseRepFragment.newInstance(1,mValues);
                AppCompatActivity activity = unwrap(view.getContext());
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.exerciseDone, exerciseRepFragment).addToBackStack(null).commit();
            }
        });
    }

    private static AppCompatActivity unwrap(Context context) {
        while (!(context instanceof AppCompatActivity) && context instanceof ContextWrapper) {
            context = ((ContextWrapper) context).getBaseContext();
        }

        return (AppCompatActivity) context;
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public Repetition mItem;
        public Button deleteBt;
        public Button acceptBt;
        public EditText repsTf;
        public EditText weightTf;

        public ViewHolder(@NonNull FragmentExerciseRepBinding binding) {
            super(binding.getRoot());
            deleteBt = binding.deleteBt;
            acceptBt = binding.acceptBt;
            repsTf = binding.repsTextField;
            weightTf = binding.weightTextField;
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }
}