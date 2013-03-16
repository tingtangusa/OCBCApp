package com.jason.ocbcapp;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class MainActivity extends SherlockFragmentActivity implements
        ActionBar.TabListener, TabHost.OnTabChangeListener {

    // The tag for Android Logger
    public final static String APP_TAG = "OCBCApp";

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
     * will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    TabHost mTabHost;

    private HashMap<String, TabInfo> mapTabInfo = new HashMap<String, MainActivity.TabInfo>();

    // Name of the preference SharedPreferences that we are using for the app
    public static final String PREFS_NAME = "OCBCPrefsFile";

    MainActivity mMainActivity = this;

    private LinkedList<String> abDropdownList;
    private ArrayAdapter<String> abDropdownAdapter;

    private PullToRefreshListView mPullRefreshListView;

    // The request type when we want the SetupActivity to perform
    // setup for the user
    static final int SETUP_REQUEST = 1;

    // token that uniquely identifies the user on the server
    public String userToken = null;

    // state to store whether user has already done the setting up
    private boolean hasSetup = false;

    SharedPreferences settings = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Restore preferences
        settings = getSharedPreferences(PREFS_NAME, 0);

        // start SetupActivity if user has not setup the app
        hasSetup = settings.getBoolean("hasSetup", false);
        Log.i("OCBCApp", "hasSetup = " + hasSetup);
        startSetup();
        initializeUserToken();
        Log.i("OCBCApp", "user token = " + userToken);

        initializeTabHost(savedInstanceState);

        initializeViewPager();

        // initialize actionbar dropdown list
        initializeActionBarDropDownList();

        initializeActionBar();
    }

    /**
     * 
     */
    private void initializeActionBarDropDownList() {
        abDropdownList = new LinkedList<String>();
        abDropdownList.add(getResources().getString(
                R.string.title_list_least_wait));
        abDropdownList.add(getResources().getString(
                R.string.title_list_nearest_branches));
        abDropdownAdapter = new ArrayAdapter<String>(mMainActivity,
                android.R.layout.simple_spinner_dropdown_item, abDropdownList);
    }

    private void initializeUserToken() {
        if (hasSetup)
            userToken = settings.getString("userToken", "");
        else {
            Log.wtf(APP_TAG, "can't init token without setting up");
        }
    }

    private void initializeActionBar() {
        /** Enabling dropdown list navigation for the action bar */
        ActionBar ab = getSupportActionBar();
        ab.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        // Not showing title
        // ab.setDisplayShowTitleEnabled(false);
        /** Defining Navigation listener */
        OnNavigationListener navigationListener = new OnNavigationListener() {

            @Override
            public boolean onNavigationItemSelected(int itemPosition,
                    long itemId) {
                Toast.makeText(getBaseContext(),
                        "You selected : " + abDropdownList.get(itemPosition),
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        };

        ab.setBackgroundDrawable(getResources().getDrawable(
                R.drawable.ab_solid_ocbc));
        /**
         * Setting dropdown items and item navigation listener for the actionbar
         */
        ab.setListNavigationCallbacks(abDropdownAdapter, navigationListener);
    }

    private void initializeViewPager() {
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the app.
        mSectionsPagerAdapter = new SectionsPagerAdapter(
                getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager
                .setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        mTabHost.setCurrentTab(position);
                    }
                });
    }

    // starts the setup activity and waits for the user's token
    private void startSetup() {
        if (!hasSetup) {
            Log.i("OCBCApp", "Starting setup");
            Intent intent = new Intent(this, SetupActivity.class);
            startActivityForResult(intent, SETUP_REQUEST);
        }
    }

    // work on the result that the activities return
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(APP_TAG, "returned from activity");
        // make sure we are responding to the setup request
        if (requestCode == SETUP_REQUEST) {
            // make sure the request was successful
            if (resultCode == RESULT_OK) {
                storeToken(data.getStringExtra("userToken"));
            }
        }
    }

    // stores the token we got from SetupActivity
    private void storeToken(String token) {
        if (TextUtils.isEmpty(token)) {
            Log.wtf(APP_TAG, "token is empty!");
        } else {
            Log.d(APP_TAG, "Storing token: " + token);
            Log.d(APP_TAG, "Storing token, hasSetup : " + hasSetup);
            userToken = token;
            // we got the token, user has performed setup
            hasSetup = true;
            commitTokenToPrefs(userToken);
        }
    }

    private void commitTokenToPrefs(String userToken) {
        settings = getSharedPreferences(MainActivity.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("hasSetup", true);
        editor.putString("userToken", userToken);
        editor.commit();
        Log.d(APP_TAG, "after commiting, hasSetup: " + hasSetup);
    }

    /**
     * Initialise the Tab Host, add fragments to tab host.
     */
    private void initializeTabHost(Bundle args) {
        mTabHost = (TabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup();
        TabInfo tabInfo = null;

        View indicator = getLayoutInflater().inflate(R.layout.tab_indicator,
                null);
        TextView titleTv = (TextView) indicator.findViewById(R.id.title);

        String tabName = this.getString(R.string.title_section_branches)
                .toUpperCase();
        titleTv.setText(tabName);

        MainActivity.AddTab(this, this.mTabHost,
                this.mTabHost.newTabSpec(tabName).setIndicator(indicator),
                (tabInfo = new TabInfo(tabName,
                        LeastWaitingTimeListFragment.class, args)));
        this.mapTabInfo.put(tabInfo.tag, tabInfo);

        indicator = getLayoutInflater().inflate(R.layout.tab_indicator, null);
        titleTv = (TextView) indicator.findViewById(R.id.title);

        tabName = this.getString(R.string.title_section_notifs).toUpperCase();
        titleTv.setText(tabName);
        MainActivity.AddTab(this, this.mTabHost,
                this.mTabHost.newTabSpec(tabName).setIndicator(indicator),
                (tabInfo = new TabInfo(tabName, NotificationsFragment.class,
                        args)));
        this.mapTabInfo.put(tabInfo.tag, tabInfo);

        indicator = getLayoutInflater().inflate(R.layout.tab_indicator, null);
        titleTv = (TextView) indicator.findViewById(R.id.title);
        tabName = this.getString(R.string.title_section_appts).toUpperCase();
        titleTv.setText(tabName);
        MainActivity.AddTab(this, this.mTabHost,
                this.mTabHost.newTabSpec(tabName).setIndicator(indicator),
                (tabInfo = new TabInfo(tabName, AppointmentsFragment.class,
                        args)));
        this.mapTabInfo.put(tabInfo.tag, tabInfo);
        // Default to first tab
        // this.onTabChanged("Tab1");
        //
        mTabHost.setOnTabChangedListener(this);
    }

    /**
     * Add Tab content to the Tabhost
     * 
     * @param activity
     * @param tabHost
     * @param tabSpec
     * @param clss
     * @param args
     */
    private static void AddTab(MainActivity activity, TabHost tabHost,
            TabHost.TabSpec tabSpec, TabInfo tabInfo) {
        // Attach a Tab view factory to the spec
        tabSpec.setContent(activity.new TabFactory(activity));
        tabHost.addTab(tabSpec);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab,
            android.support.v4.app.FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab,
            android.support.v4.app.FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab,
            android.support.v4.app.FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a DummySectionFragment (defined as a static inner class
            // below) with the page number as its lone argument.
            switch (position) {
            case 0:
                return new LeastWaitingTimeListFragment();
            case 1:
                return new NotificationsFragment();
            case 2:
                return new AppointmentsFragment();
            }
            Fragment fragment = new DummySectionFragment();
            Bundle args = new Bundle();
            args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
            case 0:
                return getString(R.string.title_section_branches).toUpperCase();
            case 1:
                return getString(R.string.title_section_notifs).toUpperCase();
            case 2:
                return getString(R.string.title_section_appts).toUpperCase();
            }
            return null;
        }
    }

    /**
     * A dummy fragment representing a section of the app, but that simply
     * displays dummy text.
     */
    public static class DummySectionFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        public static final String ARG_SECTION_NUMBER = "section_number";

        public DummySectionFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            // Create a new TextView and set its text to the fragment's section
            // number argument value.
            TextView textView = new TextView(getActivity());
            textView.setGravity(Gravity.CENTER);
            textView.setText(Integer.toString(getArguments().getInt(
                    ARG_SECTION_NUMBER)));
            return textView;
        }
    }

    /**
     * Maintains extrinsic info of a tab's construct
     * 
     * @author Jason
     */
    private class TabInfo {
        private String tag;
        private Class<?> clss;
        private Bundle args;
        private Fragment fragment;

        TabInfo(String tag, Class<?> clazz, Bundle args) {
            this.tag = tag;
            this.clss = clazz;
            this.args = args;
        }

    }

    /**
     * A simple factory that returns dummy views to the Tabhost
     * 
     * @author Jason
     */
    class TabFactory implements TabContentFactory {

        private final Context mContext;

        /**
         * @param context
         */
        public TabFactory(Context context) {
            mContext = context;
        }

        /**
         * (non-Javadoc)
         * 
         * @see android.widget.TabHost.TabContentFactory#createTabContent(java.lang.String)
         */
        public View createTabContent(String tag) {
            View v = new View(mContext);
            v.setMinimumWidth(0);
            v.setMinimumHeight(0);
            return v;
        }

    }

    @Override
    public void onTabChanged(String arg0) {
        int pos = this.mTabHost.getCurrentTab();
        this.mViewPager.setCurrentItem(pos);
    }

    public void showToast(String string) {
        Toast.makeText(getApplicationContext(), string, Toast.LENGTH_SHORT)
                .show();
    }
}
