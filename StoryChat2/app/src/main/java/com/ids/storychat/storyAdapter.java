package com.ids.storychat;

/**
 * Created by JongWN-D on 7/24/2017.
 */

import android.content.Context;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import java.util.List;
import com.squareup.picasso.Picasso;


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
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case

        public ImageView story1;
        public ImageView story2;


        public ViewHolder(View itemview) {
            super(itemview);
            story1 = (ImageView)itemview.findViewById(R.id.story1);
            story2 = (ImageView)itemview.findViewById(R.id.story2);
        }
    }


    // Create new views (invoked by the layout manager)
    @Override
    public storyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
        // create a new view
        View v= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.story_item, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
       // holder.tvEmail.setText(nStory.get(position).getTitle());


        if (!nStory.get(position).getTitle().isEmpty()) {
            Uri uri =  nStory.get(position).getPhoto();
            if( (position % 2)==0) {
                Picasso.with(context)
                        .load(R.drawable.story1)
                        .fit()
                        .centerInside()
                        .into(holder.story1);
            }else{
                Picasso.with(context)
                        .load(R.drawable.story2)
                        .fit()
                        .centerInside()
                        .into(holder.story2);
            }
        }
        else
        {

            holder.story1.setImageResource(R.drawable.story1);
            holder.story2.setImageResource(R.drawable.story2);

        }
        // holder.btnChat.setOnClickListener(new View.OnClickListener(){
        //   @Override
        //     public void onClick(View view){
        // Intent in=new Intent(context,ChatViewActivity.class);
        // context.startActivity(in);

        //     }

        //  });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return nStory.size();
    }
}
