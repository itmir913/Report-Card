package com.tistory.itmir.whdghks913.reportcard.activity.show.exam;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.tistory.itmir.whdghks913.reportcard.R;
import com.tistory.itmir.whdghks913.reportcard.activity.modify.ExamActivity;
import com.tistory.itmir.whdghks913.reportcard.activity.modify.ExamScoreActivity;

import java.util.ArrayList;
import java.util.List;

public class ShowExamDetailActivity extends AppCompatActivity {
    private int _id;
    private String title;
    private Adapter mAdapter;

    private FloatingActionButton mFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_exam_detail);

        Intent mIntent = getIntent();
        title = mIntent.getStringExtra("name");

        Toolbar mToolbar = (Toolbar) findViewById(R.id.mToolbar);
        mToolbar.setTitle(title);
//        mToolbar.setBackgroundColor(mIntent.getIntExtra("color", ContextCompat.getColor(this, R.color.colorPrimary)));
        setSupportActionBar(mToolbar);

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

        mFab = (FloatingActionButton) findViewById(R.id.mFab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(getApplicationContext(), ExamScoreActivity.class);
                mIntent.putExtra("type", 0);
                mIntent.putExtra("_id", _id);
                startActivity(mIntent);
            }
        });

        _id = mIntent.getIntExtra("_id", 0);

        ViewPager viewPager = (ViewPager) findViewById(R.id.mViewpager);
        if (viewPager != null) {
            mAdapter = new Adapter(getSupportFragmentManager());
            mAdapter.addFragment(getString(R.string.subject), FragmentSubjectList.getInstance(_id));
            mAdapter.addFragment(getString(R.string.graph), FragmentGraph.getInstance(_id));
            viewPager.setAdapter(mAdapter);

            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    if (position == 1) mFab.hide();
                    else mFab.show();
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.mTabLayout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setSelectedTabIndicatorColor(Color.WHITE);
    }

    class Adapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        public void addFragment(String mTitle, Fragment mFragment) {
            mFragments.add(mFragment);
            mFragmentTitles.add(mTitle);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public int getItemPosition(Object item) {
            return POSITION_NONE;   // notifyDataSetChanged
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_edit) {
            Intent mIntent = new Intent(this, ExamActivity.class);
            mIntent.putExtra("type", 1);
            mIntent.putExtra("_id", _id);
            mIntent.putExtra("name", title);
            startActivityForResult(mIntent, 777);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if ((resultCode == 1234) || (resultCode == 999)) {
            finish();
        }
    }

}
