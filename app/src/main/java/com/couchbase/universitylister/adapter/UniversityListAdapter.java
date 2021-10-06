package com.couchbase.universitylister.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.couchbase.universitylister.R;
import com.couchbase.universitylister.model.University;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by priya.rajagopal on 8/3/17.
 */

public class UniversityListAdapter extends RecyclerView.Adapter<UniversityListAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView name;
        public TextView country;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView)itemView.findViewById(R.id.name);
            country = (TextView)itemView.findViewById(R.id.country);
        }
    }

    private List<University> mUniversities = new ArrayList<>();

    private Context mContext;

    public UniversityListAdapter(Context context) {
        mContext = context;

    }

    private Context getContext() {
        return mContext;
    }

    public void addUniversities(List<University> universities) {
        mUniversities.addAll(universities);
    }
    public void setUniversities(List<University> universities) {
        mUniversities = universities;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.university_row,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        University university = mUniversities.get(position);
        holder.name.setText(university.getName());
        holder.country.setText( university.getCountry());

    }

    @Override
    public int getItemCount() {
        return  mUniversities.size();
    }



}
