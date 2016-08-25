package com.gellert.quicksort;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hrules.charter.CharterBar;
import com.hrules.charter.CharterXLabels;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    @BindView(R.id.charter_bar_XLabel) CharterXLabels charterBarLabelX;
    @BindView(R.id.charter_bar_with_XLabel) CharterBar charterBarWithLabel;
    @BindView(R.id.partitionTextView) TextView partitionTextView;
    @BindView(R.id.pivotTextView) TextView pivotTextView;
    @BindView(R.id.leftCursorTextView) TextView leftCursorTextView;
    @BindView(R.id.rightCursorTextView) TextView rightCursorTextView;

    private QuickSort mQuickSort;

    private static int DEFAULT_ITEMS_COUNT = 5;
    private static int DEFAULT_RANDOM_VALUE_MIN = 10;
    private static int DEFAULT_RANDOM_VALUE_MAX = 100;
    private static int DEFAULT_WAIT = 1000;

    private static final String TAG = "MainActivityFragment";

    private float[] values;
    private boolean isSorting;


    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);


        Resources res = getResources();
        int[] barColors = new int[] {
                res.getColor(R.color.lightBlue400), res.getColor(R.color.lightBlue300)
        };


        ButterKnife.bind(this,view);

        fillRandomValues();

        charterBarLabelX.setStickyEdges(false);
        charterBarLabelX.setVisibilityPattern(new boolean[] { true });

        charterBarWithLabel.setColors(barColors);

        charterBarWithLabel.setAnim(false);

        isSorting = false;

        //partitionTextView = (TextView)view.findViewById(R.id.partitionTextView);
        //pivotTextView = (TextView)view.findViewById(R.id.pivotTextView);
        //leftCursorTextView = (TextView)view.findViewById(R.id.leftCursorTextView);
        //rightCursorTextView = (TextView)view.findViewById(R.id.rightCursorTextView);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        fillRandomValues();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopSorting();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());

        DEFAULT_ITEMS_COUNT = Integer.parseInt(sp.getString(getString(R.string.pref_num_columns),getString(R.string.pref_num_columns_default)));
        DEFAULT_RANDOM_VALUE_MIN = Integer.parseInt(sp.getString(getString(R.string.pref_min_value),getString(R.string.pref_min_value_default)));
        DEFAULT_RANDOM_VALUE_MAX = Integer.parseInt(sp.getString(getString(R.string.pref_max_value),getString(R.string.pref_max_value_default)));
        DEFAULT_WAIT = Integer.parseInt(sp.getString(getString(R.string.pref_wait), getString(R.string.pref_wait_default)));
    }

    public void fillRandomValues() {
        if(isSorting){
            stopSorting();
        }
        Random random = new Random();

        float[] newRandomValues = new float[DEFAULT_ITEMS_COUNT];
        for (int i = 0; i < newRandomValues.length; i++) {
            newRandomValues[i] = random.nextInt(DEFAULT_RANDOM_VALUE_MAX - DEFAULT_RANDOM_VALUE_MIN + 1) + DEFAULT_RANDOM_VALUE_MIN;
        }

        values = newRandomValues;
        refreshValues(values);


        if(getActivity().findViewById(R.id.fab) != null)
            ((FloatingActionButton)getActivity().findViewById(R.id.fab)).show();
    }
    
    
    public void stopSorting(){
        if(mQuickSort != null) {
            mQuickSort.cancel(true);
            isSorting = false;
        }
    }

    private void refreshValues(float[] v) {
        charterBarWithLabel.setValues(v);
        charterBarWithLabel.show();

        charterBarLabelX.setValues(v);
    }

    private void refreshTextViews(float piv, int leftC, int rightC, int l, int r) {
        if(getActivity() == null){return;}
        String partitionString = getResources().getString(R.string.partition_formatted,
                l,
                (int)values[l],
                r,
                (int)values[r]
        );
        partitionTextView.setText(partitionString);

        String leftCursorString = getResources().getString(R.string.left_cursor_formatted,
                leftC,
                (int)values[leftC]
        );
        leftCursorTextView.setText(leftCursorString);

        String rightCursorString = getResources().getString(R.string.right_cursor_formatted,
                rightC,
                (int)values[rightC]
        );
        rightCursorTextView.setText(rightCursorString);

        String pivotString = getResources().getString(R.string.pivot_formatted,(int)piv);
        pivotTextView.setText(pivotString);
    }


    public void SortValues() {
        Log.v(TAG,"Sorting values");

        mQuickSort = new QuickSort();
        mQuickSort.execute(values);
        return;
    }

    public class QuickSort extends AsyncTask<float[], Void, float[]> {
        private float[] v;
        private int leftC, rightC, l, r;
        private float piv;

        private int numberOfSwaps, numberOfCompares;


        @Override
        protected void onPreExecute() {
            isSorting = true;

            pivotTextView.setEnabled(true);
            leftCursorTextView.setEnabled(true);
            rightCursorTextView.setEnabled(true);
            partitionTextView.setEnabled(true);

            numberOfCompares = 0;
            numberOfSwaps = 0;
        }

        @Override
        protected float[] doInBackground(float[]... input) {
            v = input[0];
            int left = 0;
            int right = v.length-1;

            quickSort(left, right);
            return v;
        }

        // This method sorts the array v using the quicksort algorithm
        // It is given two cursors, the part of the array it sorts
        private void quickSort(int left,int right){
            // It terminates if the task is cancelled
            if(isCancelled()){
                return;
            }

            // If both the cursor have gone through the array, sorting is done
            if(left >= right)
                return;

            // We pick the right item as the pivot value
            float pivot = v[right];
            int partition = partition(left, right, pivot);

            // Recursively, it calls the method to sort the left and right part of the array
            quickSort(0, partition-1);
            quickSort(partition+1, right);
        }

        // It is used to partition the array and returns the cursor to the sorted pivot
        private int partition(int left, int right, float pivot){
            // It terminates if the task is cancelled
            if(isCancelled()){
                return 0;
            }

            int leftCursor = left;
            int rightCursor = right;

            // Change variables which are displayed in the text views
            l = left;
            r = right;
            piv = pivot;
            leftC = leftCursor;
            rightC = rightCursor;
            onProgressUpdate();
            SystemClock.sleep(DEFAULT_WAIT);
            
            // It searches for elements of the array (in both directions) that are not in the right direction from the pivot, and swaps them
            while(leftCursor < rightCursor){
                while(v[leftCursor] < pivot){
                    leftCursor++;

                    numberOfCompares++;
                    
                    // Update UI 
                    leftC = leftCursor;
                    onProgressUpdate();
                    SystemClock.sleep(DEFAULT_WAIT);

                    // It terminates if the task is cancelled
                    if(isCancelled()){
                        return 0;
                    }

                }
                while(rightCursor > 0 && v[--rightCursor] > pivot){
                    numberOfCompares++;

                    // Update UI 
                    rightC = rightCursor;
                    onProgressUpdate();
                    SystemClock.sleep(DEFAULT_WAIT);

                    // It terminates if the task is cancelled
                    if(isCancelled()){
                        return 0;
                    }

                }
                if(leftCursor >= rightCursor){
                    break;
                } else {
                    swap(leftCursor, rightCursor);
                }
            }
            swap(leftCursor, right);
            return leftCursor;
        }

        // This method swaps two elements of the array by their indexes
        public void swap(int left,int right){
            float temp = v[left];
            v[left] = v[right];
            v[right] = temp;

            numberOfSwaps++;

            onProgressUpdate();
            SystemClock.sleep(DEFAULT_WAIT);
        }

        @Override
        protected void onProgressUpdate(Void... progress) {
            // It terminates if the task is cancelled
            if(isCancelled()){
                return;
            }
            
            
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    refreshValues(v);
                    refreshTextViews(piv, leftC, rightC, l, r);
                }
            });
        }

        @Override
        protected void onPostExecute(float[] floats) {
            values = v;
            isSorting = false;

            pivotTextView.setEnabled(false);
            leftCursorTextView.setEnabled(false);
            rightCursorTextView.setEnabled(false);
            partitionTextView.setEnabled(false);

            AlertDialog.Builder builer = new AlertDialog.Builder(getContext());
            builer.setTitle(getString(R.string.sorting_finished));
            builer.setMessage(getString(R.string.sorting_message_formatted,numberOfCompares,numberOfSwaps));
            builer.setPositiveButton("OK",null);
            builer.create().show();
        }
    }
}
