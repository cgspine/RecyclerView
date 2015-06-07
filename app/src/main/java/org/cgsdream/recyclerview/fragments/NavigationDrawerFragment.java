package org.cgsdream.recyclerview.fragments;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.cgsdream.recyclerview.R;

/**
 * Created by sm on 2015/6/5.
 */
public class NavigationDrawerFragment extends Fragment {
	private static final String STATE_SELECTED_POSITION = "selected_navgation_drawer_position";
	private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

	public interface NavigationDrawerCallbacks{
		void onNavigationDrawerItemSelected(int position);
	}

	private NavigationDrawerCallbacks mCallbacks;

	private ActionBarDrawerToggle mActionBarDrawerToggle;
	private DrawerLayout  mDrawerLayout;
	private ListView mListView;
	private View mFragmentContainerView;

	private int mCurrentSelectedPosition = 0;
	private boolean mFromSavedInstanceState;
	private boolean mUserLeanedDrawer;

	public NavigationDrawerFragment(){}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallbacks = (NavigationDrawerCallbacks)activity;
		}catch (ClassCastException e){
			throw new ClassCastException("Activity must implent interface NavigationCallbacks");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
		mUserLeanedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER,false);

		if(savedInstanceState != null){
			mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
			mFromSavedInstanceState = true;
		}
		selectItem(mCurrentSelectedPosition);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mListView = (ListView)inflater.inflate(R.layout.listview_navigation_drawer,container,false);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				selectItem(position);
			}
		});
		mListView.setAdapter(new ArrayAdapter<String>(
				getActivity(),
				android.R.layout.simple_list_item_1,
				android.R.id.text1,
				new String[]{
						"简单列表",
						"多item类型列表",
						"按页滑动列表"
				}
		));
		mListView.setItemChecked(mCurrentSelectedPosition, true);
		return mListView;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(mActionBarDrawerToggle.onOptionsItemSelected(item)){
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mActionBarDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mCallbacks = null;
	}

	public void init(int fragmentId,DrawerLayout drawerLayout){
		mFragmentContainerView = getActivity().findViewById(fragmentId);
		mDrawerLayout = drawerLayout;
		mActionBarDrawerToggle = new ActionBarDrawerToggle(
				getActivity(),
				mDrawerLayout,
				R.string.navgation_drawer_toggler_open,
				R.string.navgation_drawer_toggler_close
		){
			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				if(isAdded()){
					return;
				}
				if(!mUserLeanedDrawer){
					mUserLeanedDrawer = true;
					SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
					sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER,true);
				}
				getActivity().invalidateOptionsMenu();
			}

			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
				if(isAdded()){
					return;
				}
				getActivity().invalidateOptionsMenu();
			}
		};
		mDrawerLayout.post(new Runnable() {
			@Override
			public void run() {
				mActionBarDrawerToggle.syncState();
			}
		});
		mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);
	}

	public boolean isDrawerOpen(){
		return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
	}

	private void selectItem(int position){
		mCurrentSelectedPosition = position;
		if(mListView != null){
			mListView.setItemChecked(position,true);
		}
		if(mDrawerLayout != null){
			mDrawerLayout.closeDrawer(mFragmentContainerView);
		}
		if(mCallbacks !=null){
			mCallbacks.onNavigationDrawerItemSelected(position);
		}
	}
}
