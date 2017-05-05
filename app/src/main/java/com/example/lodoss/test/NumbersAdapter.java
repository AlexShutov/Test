package com.example.lodoss.test;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lodoss.test.databinding.ListItemBinding;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 5/5/2017.
 */

public class NumbersAdapter extends RecyclerView.Adapter<NumbersAdapter.ViewHolder> {

    private List<BigInteger> numbers = new ArrayList<>();
    private LayoutInflater inflater;

    public NumbersAdapter(Context context){
        inflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.list_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BigInteger number = numbers.get(position);
        holder.setNumber(number);
    }

    @Override
    public int getItemCount() {
        return numbers.size();
    }

    public void setNumbers(List<BigInteger> numbers) {
        this.numbers = new ArrayList<>(numbers);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ListItemBinding binding;

        public ViewHolder(View view){
            super(view);
            binding = ListItemBinding.bind(view);
        }

        void setNumber(BigInteger number){
            String n = number.toString();
            binding.number.setText(n);
        }
    }
}
