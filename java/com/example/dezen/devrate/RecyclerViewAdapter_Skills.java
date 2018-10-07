package com.example.dezen.devrate;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class RecyclerViewAdapter_Skills extends RecyclerView.Adapter<RecyclerViewAdapter_Skills.ViewHolder>{

    private ArrayList<String> skills = new ArrayList<>();
    private Context mContext;

    public RecyclerViewAdapter_Skills(ArrayList<String> skills, Context mContext) {
        this.skills = skills;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem_skills,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.skill_name.setText(skills.get(position));

    }

    @Override
    public int getItemCount() {
        return skills.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView skill_name;
        RelativeLayout RelativeLayout_skill;
        public ViewHolder(View itemView) {
            super(itemView);
            skill_name = itemView.findViewById(R.id.Skill_text);
            RelativeLayout_skill = itemView.findViewById(R.id.RelativeLayout_skill);
        }
    }
}
