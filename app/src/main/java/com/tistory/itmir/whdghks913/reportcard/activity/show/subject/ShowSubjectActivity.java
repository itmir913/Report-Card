package com.tistory.itmir.whdghks913.reportcard.activity.show.subject;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tistory.itmir.whdghks913.reportcard.R;
import com.tistory.itmir.whdghks913.reportcard.activity.create.CreateSubjectActivity;
import com.tistory.itmir.whdghks913.reportcard.activity.edit.EditSubjectActivity;
import com.tistory.itmir.whdghks913.reportcard.tool.ExamDataBaseInfo;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ShowSubjectActivity extends AppCompatActivity {
    SimpleRecyclerViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_subject);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.mToolbar);
        setSupportActionBar(mToolbar);

        FloatingActionButton mFab = (FloatingActionButton) findViewById(R.id.mFab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), CreateSubjectActivity.class));
            }
        });

        ActionBar mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setHomeButtonEnabled(true);
            mActionBar.setDisplayHomeAsUpEnabled(true);

            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.mRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));

        mAdapter = new SimpleRecyclerViewAdapter(this);
        recyclerView.setAdapter(mAdapter);

        getSubjectList();
    }

    private void getSubjectList() {
        mAdapter.clear();
        mAdapter.notifyDataSetChanged();

        ArrayList<ExamDataBaseInfo.subjectData> mValues = ExamDataBaseInfo.getSubjectList();
        if (mValues == null)
            return;

        for (int i = 0; i < mValues.size(); i++) {
            ExamDataBaseInfo.subjectData mData = mValues.get(i);
            mAdapter.addItem(mData._subjectId, mData.name, mData.color);
        }

        mAdapter.sort();
    }

    public class SimpleRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleRecyclerViewAdapter.ViewHolder> {

        private final int mBackground;
        private ArrayList<ExamData> mValues = new ArrayList<>();

        public SimpleRecyclerViewAdapter(Context mContext) {
            TypedValue mTypedValue = new TypedValue();
            mContext.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final View mColor;
            public final TextView mSubjectName;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mColor = view.findViewById(R.id.mColor);
                mSubjectName = (TextView) view.findViewById(R.id.mSubjectName);
            }
        }

        public void addItem(int _id, String name, int color) {
            ExamData mData = new ExamData();

            mData._id = _id;
            mData.color = color;
            mData.name = name;

            mValues.add(mData);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

//        public ExamData getItemData(int position) {
//            return mValues.get(position);
//        }

        public void clear() {
            mValues.clear();
        }

        public void sort() {
            Collections.sort(mValues, ALPHA_COMPARATOR);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_subject_list_item, parent, false);
            view.setBackgroundResource(mBackground);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            ExamData mData = mValues.get(position);

            GradientDrawable bgShape = (GradientDrawable) holder.mColor.getBackground();
            bgShape.setColor(mData.color);

            holder.mSubjectName.setText(mData.name);
            holder.mView.setTag(mData);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ExamData mData = (ExamData) v.getTag();
                    int _id = mData._id;
                    int color = mData.color;
                    String name = mData.name;

                    Intent mIntent = new Intent(v.getContext(), EditSubjectActivity.class);
                    mIntent.putExtra("_id", _id);
                    mIntent.putExtra("color", color);
                    mIntent.putExtra("name", name);

                    v.getContext().startActivity(mIntent);
                }
            });
//            holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View view) {
            // TODO
//                    if (mDatabase != null) {
//                        boolean isDelete = true;
//                        int _id = ((ExamData) view.getTag())._id;
//
//                        Cursor mExamNameList = mDatabase.getFirstData(ExamDataBaseInfo.examListTableName, "_id");
//                        label:
//                        for (int i = 0; i < mExamNameList.getCount(); i++) {
//                            Cursor mExamDetailList = mDatabase.getFirstData(ExamDataBaseInfo.getExamTable(mExamNameList.getInt(0)), "name");
//                            for (int j = 0; j < mExamDetailList.getCount(); j++) {
//                                if (_id == mExamDetailList.getInt(0)) {
//                                    isDelete = false;
//                                    break label;
//                                }
//                                mExamDetailList.moveToNext();
//                            }
//                            mExamNameList.moveToNext();
//                        }
//
//                        if (isDelete) {
//                            mDatabase.remove(ExamDataBaseInfo.subjectTableName, "_id", _id);
//                            getSubjectList();
//                        } else {
//                            AlertDialog.Builder builder = new AlertDialog.Builder(holder.mView.getContext(), R.style.AppCompatErrorAlertDialogStyle);
//                            builder.setTitle(R.string.not_delete_subject_title);
//                            builder.setMessage(R.string.not_delete_subject_message);
//                            builder.setPositiveButton(android.R.string.ok, null);
//							builder.setCancelable(false);
//                            builder.show();
//                        }
//                    }
//
//                    return true;
//                }
//            });
        }
    }

    public class ExamData {
        public int _id;
        public int color;
        public String name;
    }

    /**
     * 알파벳순으로 정렬
     */
    public static final Comparator<ExamData> ALPHA_COMPARATOR = new Comparator<ExamData>() {
        private final Collator sCollator = Collator.getInstance();

        @Override
        public int compare(ExamData arg1, ExamData arg2) {
            return sCollator.compare(arg1.name, arg2.name);
        }
    };

    @Override
    public void onResume() {
        super.onResume();

        getSubjectList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_subject, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_add_subject) {
            startActivity(new Intent(getApplicationContext(), CreateSubjectActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
