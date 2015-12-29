package com.tistory.itmir.whdghks913.reportcard.activity.main;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.tistory.itmir.whdghks913.reportcard.R;
import com.tistory.itmir.whdghks913.reportcard.activity.modify.CategoryActivity;
import com.tistory.itmir.whdghks913.reportcard.activity.modify.ExamActivity;
import com.tistory.itmir.whdghks913.reportcard.activity.modify.SubjectActivity;
import com.tistory.itmir.whdghks913.reportcard.activity.settings.SettingsActivity;
import com.tistory.itmir.whdghks913.reportcard.activity.show.category.ShowCategoryActivity;
import com.tistory.itmir.whdghks913.reportcard.activity.show.subject.ShowSubjectActivity;
import com.tistory.itmir.whdghks913.reportcard.tool.ExamDataBaseInfo;
import com.tistory.itmir.whdghks913.reportcard.tool.Preference;
import com.tistory.itmir.whdghks913.reportcard.tool.initDatabase;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    private FragmentManager mFragmentManager;
    private NavigationView navigationView;

    private Fragment examListFragment, subjectAnalyticsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.mToolbar);
        setSupportActionBar(mToolbar);

        FloatingActionButton mExamFab = (FloatingActionButton) findViewById(R.id.mFab);
        mExamFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Menu mMenu = navigationView.getMenu();
                if (mMenu.getItem(0).isChecked()) {
                    startActivity(new Intent(getApplicationContext(), ExamActivity.class).putExtra("type", 0));
                } else {
                    startActivity(new Intent(getApplicationContext(), SubjectActivity.class).putExtra("type", 0));
                }
            }
        });

        if (!ExamDataBaseInfo.isDatabaseExists()) {
            (new initDatabase()).init(getApplicationContext());
        }

        int mDatabaseVersion = (new Preference(getApplicationContext())).getInt(ExamDataBaseInfo.PreferenceVersionName, ExamDataBaseInfo.DatabaseVersion);
        if (mDatabaseVersion == ExamDataBaseInfo.DatabaseVersion) {
            examListFragment = ExamListFragment.newInstance();
            subjectAnalyticsFragment = SubjectAnalyticsFragment.newInstance();

            mFragmentManager = getSupportFragmentManager();
            mDrawerLayout = (DrawerLayout) findViewById(R.id.mDrawerLayout);
            navigationView = (NavigationView) findViewById(R.id.mNavigationView);
            navigationView.setNavigationItemSelectedListener(this);

            mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.app_name, R.string.app_name);
            mDrawerLayout.setDrawerListener(mDrawerToggle);

            mFragmentManager.beginTransaction().replace(R.id.mContainer, examListFragment).commit();
        } else {
            /**
             * 데이터 베이스 업데이트가 필요함
             */
            MaterialDialog.Builder mBuilder = new MaterialDialog.Builder(getApplicationContext());
            mBuilder.title(R.string.database_update_title);
            mBuilder.content(R.string.database_update_message);
            mBuilder.progress(true, 0);
            mBuilder.show();
        }

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

//    public void onResume() {
//        super.onResume();
//
//        if (mFragmentManager != null) {
//            Menu mMenu = navigationView.getMenu();
//            if (mMenu.getItem(0).isChecked())
//                if (examListFragment.isResumed())
//                    mFragmentManager.beginTransaction().replace(R.id.mContainer, ((ExamListFragment) examListFragment).getResumeFragment()).commitAllowingStateLoss();
//                else if (subjectAnalyticsFragment.isResumed())
//                    mFragmentManager.beginTransaction().replace(R.id.mContainer, ((ExamListFragment) examListFragment).getResumeFragment()).commitAllowingStateLoss();
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            mDrawerLayout.openDrawer(GravityCompat.START);
            return true;
        } else if (id == R.id.action_settings) {
            startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            finish();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();

        switch (id) {
            case R.id.action_view_subject:
                startActivity(new Intent(getApplicationContext(), ShowSubjectActivity.class));
                break;
            case R.id.action_view_exam_category:
                startActivity(new Intent(getApplicationContext(), ShowCategoryActivity.class).putExtra("type", 0));
                break;
            case R.id.action_view_subject_category:
                startActivity(new Intent(getApplicationContext(), ShowCategoryActivity.class).putExtra("type", 1));
                break;
            case R.id.action_add_exam:
                startActivity(new Intent(getApplicationContext(), ExamActivity.class).putExtra("type", 0));
                break;
            case R.id.action_add_subject:
                startActivity(new Intent(getApplicationContext(), SubjectActivity.class).putExtra("type", 0));
                break;
            case R.id.action_add_exam_category:
                startActivity(new Intent(getApplicationContext(), CategoryActivity.class).putExtra("type", 0).putExtra("isExam", true));
                break;
            case R.id.action_add_subject_category:
                startActivity(new Intent(getApplicationContext(), CategoryActivity.class).putExtra("type", 0).putExtra("isExam", false));
                break;
            case R.id.action_exam_list:
                menuItem.setChecked(true);
                mFragmentManager.beginTransaction().replace(R.id.mContainer, examListFragment).commit();
                break;
            case R.id.action_statistics:
                menuItem.setChecked(true);
                mFragmentManager.beginTransaction().replace(R.id.mContainer, subjectAnalyticsFragment).commit();
                break;
        }

        mDrawerLayout.closeDrawers();
        return true;
    }
}
