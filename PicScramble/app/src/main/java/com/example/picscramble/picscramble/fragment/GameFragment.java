package com.example.picscramble.picscramble.fragment;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.picscramble.picscramble.R;
import com.example.picscramble.picscramble.adaptor.GameAdaptor;
import com.example.picscramble.picscramble.flickr.FlickrManager;
import com.example.picscramble.picscramble.model.PicModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The fragment holds the complete rendeing a even game logic, The game login can be seperated however due to time constraints
 * Added in fragment only.
 */

public class GameFragment extends Fragment implements AdapterView.OnItemClickListener {
    // MAX No of images in a Grid
    private static final int MAX_NO_IMAGES = 9;
    // Animator used for flip animation
    AnimatorSet setRightOut, setLeftIn;
    // Recylcer view related managers.
    private RecyclerView mRecyclerView;
    private GridLayoutManager mLayoutManager;
    //Adpator holding the images view.
    private GameAdaptor mAdapter;
    // Model array use for managing the state of tiles.
    private ArrayList<PicModel> mPicModel = new ArrayList<>(MAX_NO_IMAGES);
    // Timer for managing the initial countdown.
    private CountDownTimer timer;
    // Remaining time view for countdowns.
    private TextView mRemainingTime;
    // Random image view container
    private CardView mRandomView;
    // Random Image view
    private ImageView mRandomImageView;
    // Used to calculate the time taken to complete the game.
    private long startTime;
    // Counter used to determine the win condition.
    private byte sucessCounter = MAX_NO_IMAGES;
    // Current random index to show random image.
    private byte currentRandomIndex;

    /**
     * Utility Method used to identify the connectivity.
     *
     * @param ctx
     * @return
     */
    public static boolean isOnline(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setRightOut = (AnimatorSet) AnimatorInflater.loadAnimator(getActivity().getApplicationContext(),
                R.animator.flip_right_out);
        setLeftIn = (AnimatorSet) AnimatorInflater.loadAnimator(getActivity().getApplicationContext(),
                R.animator.flight_left_in);
        return inflater.inflate(R.layout.game_view, container, false);
    }

    /**
     * The method used to create the model object for 9 tiled images.
     */

    private void preparePicModel() {
        LoadImagesFromFlickrTask task = new LoadImagesFromFlickrTask();
        task.execute();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        // Define a layout for RecyclerView
        mLayoutManager = new GridLayoutManager(getActivity(), 3);
        mRecyclerView.setLayoutManager(mLayoutManager);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.tile_padding);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));


        mRemainingTime = (TextView) view.findViewById(R.id.timeView);

        // Initialize a new instance of RecyclerView Adapter instance
        mAdapter = new GameAdaptor(getActivity(), this, mPicModel);

        // Set the adapter for RecyclerView
        mRecyclerView.setAdapter(mAdapter);

        //Random view for the image check
        mRandomView = (CardView) view.findViewById(R.id.random_card_view);
        mRandomImageView = (ImageView) view.findViewById(R.id.random_image_view);
        mRandomView.setVisibility(View.GONE);

        // Time to be added to create the initial countdown.
        timer = new CountDownTimer(15000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (isAdded()) {
                    mRemainingTime.setText(getString(R.string.secondsRemains) + "===== " + millisUntilFinished / 1000);
                }
            }

            @Override
            public void onFinish() {
                startTime = System.currentTimeMillis();
                flipAllViews();
                mRemainingTime.setVisibility(View.GONE);
                showRandomView();
            }
        };

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (isOnline(context)) {
            preparePicModel();
        }
    }

    /**
     * Show Random image view with random numbers.
     */
    private void showRandomView() {
        mRandomView.setVisibility(View.VISIBLE);
        while (true) {
            currentRandomIndex = getRandomIndex();
            if (mPicModel.get(currentRandomIndex).isFlipped() || sucessCounter == MAX_NO_IMAGES) {
                Picasso.with(getActivity()).load(mPicModel.get(currentRandomIndex).getImageURL()).into(mRandomImageView);
                break;
            }
        }
    }

    /**
     * Give Random No between 0 to 9.
     */

    private byte getRandomIndex() {
        Random rand = new Random();
        return (byte) rand.nextInt(MAX_NO_IMAGES);
    }

    /**
     * Function to flip All tiles.
     */

    private void flipAllViews() {
        for (int temp = 0; temp < mRecyclerView.getChildCount(); temp++) {
            flipView(temp);
        }
    }

    /**
     * Flip the tile based on the provided position
     *
     * @param temp Position
     */

    private void flipView(int temp) {
        View child = mRecyclerView.getChildAt(temp);
        if (mPicModel.get(temp).isFlipped()) {
            setRightOut.setTarget(child.findViewById(R.id.item_card_view_flipped));
            setLeftIn.setTarget(child.findViewById(R.id.item_view));
            setRightOut.start();
            setLeftIn.start();
            mPicModel.get(temp).setFlipped(false);
        } else {
            setRightOut.setTarget(child.findViewById(R.id.item_view));
            setLeftIn.setTarget(child.findViewById(R.id.item_card_view_flipped));
            setRightOut.start();
            setLeftIn.start();
            mPicModel.get(temp).setFlipped(true);
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (isSameImage(position)) {
            flipView(position);
            sucessCounter--;

            if (sucessCounter <= 0) {
                showWinDailog();
            } else {
                showRandomView();
            }
        }
    }

    /**
     * Just to check if the image ID is same for random and clicked image.
     *
     * @param position
     * @return
     */

    private boolean isSameImage(int position) {
        return mPicModel.get(currentRandomIndex).getIdimage() == mPicModel.get(position).getIdimage();

    }

    /**
     * Show the winning dialog.
     */

    private void showWinDailog() {
        long timeDifference = System.currentTimeMillis() - startTime;
        int time_in_sec = (int) (timeDifference / 1000);
        showDialog(time_in_sec);

    }

    private void showDialog(int time) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage(String.format(getString(R.string.congrets), time));
        alertDialogBuilder.setNeutralButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getActivity().finishAffinity();
            }
        });


        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    /**
     * Decorator class for seperation between Grid views.
     */

    class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;

            // Add top margin only for the first item to avoid double space between items
            if (parent.getChildLayoutPosition(view) == 0) {
                outRect.top = space;
            } else {
                outRect.top = 0;
            }
        }
    }

    /**
     * Async Task implementation to fetch image URL. This can be optimised using volley.
     */

    class LoadImagesFromFlickrTask extends AsyncTask<Void, Integer, List> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading images from Flickr. Please wait...");
            progressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressDialog.setMessage(String.format("Loading images from Flickr %s/%s. Please wait...", values[0], values[1]));
        }

        @Override
        protected List doInBackground(Void... params) {
            List<String> imageURL = FlickrManager.searchImagesByTag("india");
            return imageURL;
        }

        @Override
        protected void onPostExecute(List listofUrl) {

            if (mPicModel != null && mPicModel.isEmpty()) {
                mPicModel.clear();
            }
            if (!(listofUrl == null && listofUrl.isEmpty())) {
                for (int i = 0; i < listofUrl.size(); i++) {
                    PicModel tempModel = new PicModel();
                    tempModel.setFlipped(false);
                    tempModel.setIdimage(i);
                    tempModel.setImageURL(listofUrl.get(i).toString());
                    mPicModel.add(tempModel);
                }
            }
            mAdapter.notifyDataSetChanged();
            progressDialog.dismiss();
            timer.start();
            super.onPostExecute(listofUrl);
        }
    }

}
