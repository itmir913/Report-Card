package com.tistory.itmir.whdghks913.reportcard.activity.show.exam;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tistory.itmir.whdghks913.reportcard.R;

/**
 * Created by whdghks913 on 2015-12-13.
 */
public class FragmentSubjectList extends Fragment {
    public static Fragment getInstance(Context mContext, int _id) {
        FragmentSubjectList mFragment = new FragmentSubjectList();

        Bundle args = new Bundle();
        args.putInt("_id", _id);
        mFragment.setArguments(args);

        return mFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_detail_subject, container, false);
        RecyclerView recyclerView = (RecyclerView) mView.findViewById(R.id.mRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));

        final AdapterSubject mAdapter = new AdapterSubject(getActivity());
        recyclerView.setAdapter(mAdapter);

        mAdapter.addItem("수학", 80, 10, 1000, 2);
        mAdapter.addItem("과학", 90, 3, 1000, 1);
        mAdapter.addItem("영어", 100, 1, 1000, 1);

        return mView;
    }
}
