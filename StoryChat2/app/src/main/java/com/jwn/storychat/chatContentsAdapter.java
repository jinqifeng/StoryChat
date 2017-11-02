package com.jwn.storychat;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jwn.storychat.emoji.EmojiconEditText;

import java.util.List;

/**
 * Created by JongWN-D on 7/28/2017.
 */

public class chatContentsAdapter extends RecyclerView.Adapter<chatContentsAdapter.ViewHolder> {


    List<chatContents> nStory;
    Context context;
    // Provide a suitable constructor (depends on the kind of dataset)

    public chatContentsAdapter(Context context, List<chatContents> nStory) {
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
        public MyCustomEditTextListener myCustomEditTextListener;

        public ViewHolder(View itemview, MyCustomEditTextListener myCustomEditTextListener) {
            super(itemview);
            person = (TextView)itemview.findViewById(R.id.UserName);
            contents = (EmojiconEditText)itemview.findViewById(R.id.EditText);
            img = (ImageView)itemview.findViewById(R.id.ivImgt);
            this.myCustomEditTextListener = myCustomEditTextListener;
            this.contents.addTextChangedListener(myCustomEditTextListener);
        }
    }

    // Create new views (invoked by the layout manager)
    @Override

    public chatContentsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        // create a new view
        View v= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.story_content_item, parent, false);

        chatContentsAdapter.ViewHolder vh = new chatContentsAdapter.ViewHolder(v,new MyCustomEditTextListener());
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(chatContentsAdapter.ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        // holder.tvEmail.setText(nStory.get(position).getTitle());
        chatContents item = nStory.get(position);
        if((!item.getUrl().isEmpty()) && (!item.getUrl().equals(" ")) ) {
            Glide.with(context)
                    .load(item.getUrl())
                    .asBitmap()
                    .thumbnail(0.5f)
                    .centerCrop()
                    .placeholder(R.drawable.ic_loading_thumb)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.img);
        }
        holder.myCustomEditTextListener.updatePosition(holder.getAdapterPosition());
        holder.person.setText(nStory.get(position).getPerson());
        if((position%2)==0) {
            holder.person.setTextColor(Color.parseColor("#33bcfc"));
        }else{
            holder.person.setTextColor(Color.parseColor("#ef6b6b"));
        }
        holder.contents.setText(nStory.get(holder.getAdapterPosition()).getConv());

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return nStory.size();
    }

    // we make TextWatcher to be aware of the position it currently works with
    // this way, once a new item is attached in onBindViewHolder, it will
    // update current position MyCustomEditTextListener, reference to which is kept by ViewHolder
    private class MyCustomEditTextListener implements TextWatcher {
        private int position;

        public void updatePosition(int position) {
            switch (this.position = position) {
            }
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            // no op
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            nStory.get(position).setConv(charSequence.toString());
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // no op
        }
    }
}
