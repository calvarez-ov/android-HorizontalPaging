package com.example.android.horizontalpaging;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.horizontalpaging.databinding.FragmentMainDummyBinding;
import com.example.android.horizontalpaging.databinding.SampleMainBinding;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SampleMainBinding binding = DataBindingUtil.setContentView(this, R.layout.sample_main);
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        binding.pager.setAdapter(adapter);
        binding.pager.setOffscreenPageLimit(adapter.getCount() - 1);
        binding.tabs.setupWithViewPager(binding.pager);
        for (int i = 0; i < adapter.getCount(); i++) {
            TabLayout.Tab tab = binding.tabs.getTabAt(i);
            if (tab != null) {
                tab.setText(String.valueOf(i + 1));
            }
        }
        setSupportActionBar(binding.toolbar);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = position == 0 ? new FragmentWithLoader() : new DummySectionFragment();
            Bundle args = new Bundle();
            args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }

    public static class DummySectionFragment extends Fragment {
        private static final String TAG = MainActivity.TAG + "/" + DummySectionFragment.class.getSimpleName();
        private FragmentMainDummyBinding mBinding;
        public static final String ARG_SECTION_NUMBER = "section_number";

        public DummySectionFragment() {
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_main_dummy, container, false);
            mBinding.sectionLabel.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
            return mBinding.getRoot();
        }

        @Override
        public void onResume() {
            super.onResume();
            Log.v(TAG, "onResume " + mBinding.sectionLabel.getText());
        }
    }

    public static class FragmentWithLoader extends DummySectionFragment implements LoaderManager.LoaderCallbacks<Cursor> {
        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            getLoaderManager().restartLoader(1337, null, this);
        }

        @NonNull
        @Override
        public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
            Loader<Cursor> loader = new DummyLoader(getContext());
            loader.forceLoad();
            return loader;
        }

        @Override
        public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        }

        @Override
        public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        }
    }

    static class DummyLoader extends AsyncTaskLoader<Cursor> {

        DummyLoader(@NonNull Context context) {
            super(context);
        }

        @Nullable
        @Override
        public Cursor loadInBackground() {
            return new MatrixCursor(new String[]{BaseColumns._ID}, 0);
        }
    }
}
