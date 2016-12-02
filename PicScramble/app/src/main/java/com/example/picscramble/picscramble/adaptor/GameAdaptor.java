package com.example.picscramble.picscramble.adaptor;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.picscramble.picscramble.R;
import com.example.picscramble.picscramble.model.PicModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abande on 12/2/16.
 */
public class GameAdaptor extends RecyclerView.Adapter<GameAdaptor.ViewHolder> {

    ArrayList<PicModel> mValues;
    Context mContext;

    AdapterView.OnItemClickListener mListener;

    public GameAdaptor(Context context, AdapterView.OnItemClickListener listener, ArrayList<PicModel> values) {
        mValues = values;
        mContext = context;
        mListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView imageView;
        public ViewHolder(View v) {
            super(v);
            imageView = (ImageView) v.findViewById(R.id.item_view);
            v.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            mListener.onItemClick(null,v,getAdapterPosition(), v.getId());
        }
    }

    @Override
    public GameAdaptor.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(mContext).inflate(R.layout.game_item_view, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder Vholder, int position) {
        Picasso.with(mContext)
                .load(mValues.get(position).getImageURL())
                .into(Vholder.imageView);
    }

    @Override
    public int getItemCount() {

        return mValues.size();
    }


}
