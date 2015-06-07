package org.cgsdream.recyclerview;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;

import org.cgsdream.recyclerview.fragments.MutiItemRecyclerViewFragment;
import org.cgsdream.recyclerview.fragments.NavigationDrawerFragment;
import org.cgsdream.recyclerview.fragments.PagerRecyclerViewFragment;
import org.cgsdream.recyclerview.fragments.SimpleRecyclerViewFragment;

/**
 * Created by sm on 2015/6/5.
 */
public class MainActivity extends AppCompatActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

	private NavigationDrawerFragment mNavigationDrawerFragment;
	private CharSequence mTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mNavigationDrawerFragment = (NavigationDrawerFragment)getSupportFragmentManager().findFragmentById(R.id.main_navigation_drawer);
		mTitle = getTitle();
		mNavigationDrawerFragment.init(R.id.main_navigation_drawer,(DrawerLayout)findViewById(R.id.main_drawer));
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		switch (position){
			case 0:
				startFragment(new SimpleRecyclerViewFragment());
				break;
			case 1:
				startFragment(new MutiItemRecyclerViewFragment());
				break;
			case 3:
				startFragment(new PagerRecyclerViewFragment());
				break;
			default:
				break;
		}
	}

	public void startFragment(Fragment fragment){
		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.main_cointer, fragment)
				.commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if(!mNavigationDrawerFragment.isDrawerOpen()){
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	public void restoreActionBar(){
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
	}
}
