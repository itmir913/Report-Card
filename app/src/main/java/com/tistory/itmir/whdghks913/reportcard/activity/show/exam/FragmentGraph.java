package com.tistory.itmir.whdghks913.reportcard.activity.show.exam;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tistory.itmir.whdghks913.reportcard.R;

/**
 * Created by whdghks913 on 2015-12-13.
 */
public class FragmentGraph extends Fragment {
    public static Fragment getInstance(int _id) {
        FragmentGraph mFragment = new FragmentGraph();

        Bundle args = new Bundle();
        args.putInt("_id", _id);
        mFragment.setArguments(args);

        return mFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_detail_graph, container, false);

        return mView;
    }

}
