package com.lzwap.android.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.List;
import java.util.UUID;

public class CrimePagerActivity extends AppCompatActivity
        implements CrimeFragment.Callbacks{

    private static final String EXTRA_CRIME_ID =
            "com.lzwap.android.criminalintent.crime_id";

    private ViewPager mViewPager;
    private List<Crime> mCrimes;
    private Button mJumpToFirst;
    private Button mJumpToLast;

    public static Intent newIntent(Context packageContext, UUID crimeId) {
        Intent intent = new Intent(packageContext, CrimePagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);
        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);

        mViewPager = (ViewPager) findViewById(R.id.activity_crime_pager_view_pager);

        mCrimes = CrimeLab.get(this).getCrimes();
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int i) {
                Crime crime = mCrimes.get(i);
                //查看当前item的位置
                Log.i("CrimePagerActivity", "当前item的getCurrentItem()位置" +
                    mViewPager.getCurrentItem());
                Log.i("CrimePagerActivity", "当前item的position位置" + i);

                /**
                 * 这里是获取到点击进来的item的位置：在这里特别说明，getItem()方法的形参position
                 * 得到的第当前item前一个和后一个的item的位置(根据往前滑还是往后滑确定)，
                 * 获得位置后预先加载先一个位置的视图，而mAdapter.getCurrentItem()得到的是当前item的位置，
                 * 但是！这里要注意！当item翻到position==1时，已经将position==0位置的视图加载好了，
                 * 当翻到第0个item时，getCurrentItem()不会更新为0，还是1，所以在这里的setButtonView()方法
                 * 的作用只是为了得到从列表点击进来后启动的item，滑动获得的item交给addOnPageChangeListener
                 */
                setButtonView(mViewPager.getCurrentItem());
                return CrimeFragment.newInstance(crime.getId());
            }

            @Override
            public int getCount() {
                return mCrimes.size();
            }
        });

        /**
         * 在此声明，这个方法在这个挑战中很重要！很重要！很重要！当滑动item是就会调用这个方法
         * 这个方法中的onPageSelected()回调方法是用来获取item滑动变化后当前的item，而他的形参
         * 就是当前item的位置
         */
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                setButtonView(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        mJumpToFirst = (Button) findViewById(R.id.jump_first);
        mJumpToFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(0);
            }
        });

        mJumpToLast = (Button) findViewById(R.id.jump_last);
        mJumpToLast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(mCrimes.size() - 1);
            }
        });

        for (int i = 0; i < mCrimes.size(); i++) {
            if (mCrimes.get(i).getId().equals(crimeId)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }

    @Override
    public void onCrimeUpdated(Crime crime) {

    }

    @Override
    public void onCrimeDeleted(Crime crime) {
        finish();
    }

    @Override
    public void onCrimeAllDeleted(Crime crime) {
        finish();
    }

    private void setButtonView(int position) {
        if (position == 0){
            mJumpToFirst.setVisibility(View.INVISIBLE);
            mJumpToLast.setVisibility(View.VISIBLE);
        }
        if (position == mCrimes.size() - 1){
            mJumpToLast.setVisibility(View.INVISIBLE);
            mJumpToFirst.setVisibility(View.VISIBLE);
        }
        if (position != 0 && position != mCrimes.size() - 1) {
            mJumpToFirst.setVisibility(View.VISIBLE);
            mJumpToLast.setVisibility(View.VISIBLE);
        }
    }
}
