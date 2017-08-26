package com.jwn.storychat;

/**
 * Created by JongWN-D on 7/24/2017.
 */

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import java.util.List;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;


/**
 * Created by jmr2 on 5/14/2017.
 */

public class storyAdapter extends RecyclerView.Adapter<storyAdapter.ViewHolder> {

    List<story> nStory;
    String stEmail;
    final int right=1;
    Context context;
    ImageView ivUser;

    // Provide a suitable constructor (depends on the kind of dataset)

    public storyAdapter(Context context, List<story> nStory) {
        this.nStory = nStory;
        this.context=context;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        // each data item is just a string in this case

        public ImageView story1;
        public ImageView story2;
        public TextView Pub_date1;
        public TextView Author1;
        public TextView Title1;
        public TextView Pub_date2;
        public TextView Author2;
        public TextView Title2;
        ItemClickListener itemClickListener;
        private Context context;
        public ViewHolder(Context context, View itemview) {
            super(itemview);
            this.context = context;
            story1 = (ImageView)itemview.findViewById(R.id.story1);
            story2 = (ImageView)itemview.findViewById(R.id.story2);
            Pub_date1 = (TextView)itemview.findViewById(R.id.textDate1);
            Author1 = (TextView)itemview.findViewById(R.id.textAuthor1);
            Title1 = (TextView)itemview.findViewById(R.id.textTitle1);
            Pub_date2 = (TextView)itemview.findViewById(R.id.textDate2);
            Author2 = (TextView)itemview.findViewById(R.id.textAuthor2);
            Title2 = (TextView)itemview.findViewById(R.id.textTitle2);
            itemview.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            this.itemClickListener.onItemClick(v,getLayoutPosition());
            System.out.println("onClick");

           // Toast.makeText(context, "Sorry, Disconnected to Server !. ", Toast.LENGTH_SHORT).show();
            Integer t = getAdapterPosition();  String t2= String.valueOf(getLayoutPosition());
            if( (t % 2)==0) {

                String t3 = Title1.getText().toString();
                Intent it = new Intent(v.getContext(), storyOneViewActivity.class);
                it.putExtra("title",t3);
                v.getContext().startActivity(it);

            }else {
                String t4 = Title2.getText().toString();
                Intent it = new Intent(v.getContext(), storyOneViewActivity.class);
                it.putExtra("title",t4);
                v.getContext().startActivity(it);
            }

        }
        public void setItemClickListener(ItemClickListener ic)
        {
            this.itemClickListener=ic;
        }
    }


    // Create new views (invoked by the layout manager)
    @Override
    public storyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
        // create a new view
        View v= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.story_item, parent, false);
        ViewHolder vh = new ViewHolder(context,v);
        return vh;
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        if((!nStory.get(position).getPhoto().isEmpty()) && (!nStory.get(position).getPhoto().equals("d")) ) {
            if( (position % 2)==0) {
                Glide.with(context)
                        .load(nStory.get(position).getPhoto())
                        .asBitmap()
                        .thumbnail(0.5f)
                        .centerCrop()
                        .placeholder(R.drawable.ic_loading_thumb)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.story1);
                holder.Author1.setText(nStory.get(position).getAuthor());
                holder.Pub_date1.setText(nStory.get(position).getDate());
                holder.Title1.setText(nStory.get(position).getTitle());
            }else{
                Glide.with(context)
                        .load(nStory.get(position).getPhoto())
                        .asBitmap()
                        .thumbnail(0.5f)
                        .centerCrop()
                        .placeholder(R.drawable.ic_loading_thumb)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.story2);
                holder.Author2.setText(nStory.get(position).getAuthor());
                holder.Pub_date2.setText(nStory.get(position).getDate());
                story d = nStory.get(position);
                String t = d.getTitle();
                holder.Title2.setText(t);
            }


           }
        else
        {
            holder.story1.setImageResource(R.drawable.book1);
            holder.story2.setImageResource(R.drawable.story2);
        }
         holder.setItemClickListener(new ItemClickListener(){

             @Override
             public void onItemClick(View v, int pos) {

             }



          });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return nStory.size();
    }
}
