package com.ids.storychat;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ids.storychat.emoji.EmojiconEditText;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.List;

/**
 * Created by JongWN-D on 7/28/2017.
 */

public class storyContentsAdapter extends RecyclerView.Adapter<storyContentsAdapter.ViewHolder> {


    List<storyContents> nStory;
    Context context;
    // Provide a suitable constructor (depends on the kind of dataset)

    public storyContentsAdapter(Context context, List<storyContents> nStory) {
        this.nStory = nStory;
        this.context = context;
    }
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case

        public TextView person;
        public EmojiconEditText contents;
        public ImageView img;

        public ViewHolder(View itemview) {
            super(itemview);
            person = (TextView)itemview.findViewById(R.id.UserName);
            contents = (EmojiconEditText)itemview.findViewById(R.id.EditText);
            img = (ImageView)itemview.findViewById(R.id.ivImgt);
        }
    }

    // Create new views (invoked by the layout manager)
    @Override

    public storyContentsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        // create a new view
        View v= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.story_content_item, parent, false);

        storyContentsAdapter.ViewHolder vh = new storyContentsAdapter.ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(storyContentsAdapter.ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        // holder.tvEmail.setText(nStory.get(position).getTitle());
        storyContents item = nStory.get(position);
        if(!item.getUrl().isEmpty()) {
            Glide.with(context)
                    .load(item.getUrl())
                    .asBitmap()

                    .thumbnail(0.5f)
                    .centerCrop()
                    .placeholder(R.drawable.ic_loading_thumb)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.img);
        }

        holder.person.setText(nStory.get(position).getPerson());
        holder.contents.setText(nStory.get(position).getConv());

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return nStory.size();
    }
}
