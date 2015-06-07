package org.cgsdream.recyclerview.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import org.cgsdream.recyclerview.R;
import org.cgsdream.recyclerview.adapters.SimpleAdapter;
import org.cgsdream.recyclerview.data.Data;
import org.cgsdream.recyclerview.decorator.SpaceItemDecorator;

import java.util.List;

/**
 * Created by sm on 2015/6/6.
 */
public class SimpleRecyclerViewFragment extends Fragment implements View.OnClickListener {
	private RecyclerView mRecyclerView;
	private ImageButton mAddButton;
	private SimpleAdapter mAdapter;
	private List<Data.Model> dataList;
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		RelativeLayout rootView = (RelativeLayout)inflater.inflate(R.layout.fragment_simple_recycler_view,container,false);

		mRecyclerView = (RecyclerView)rootView.findViewById(R.id.recycler_view);
		//adaptor
		dataList = Data.getInstance().getData();
		mAdapter = new SimpleAdapter(dataList);
		mRecyclerView.setAdapter(mAdapter);
		//layout manager
		LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
		layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
		mRecyclerView.setLayoutManager(layoutManager);

		//itemDecorator
		SpaceItemDecorator itemDecorator = new SpaceItemDecorator(getActivity());
		itemDecorator.setInsets(20);
		mRecyclerView.addItemDecoration(itemDecorator);

		mAddButton = (ImageButton)rootView.findViewById(R.id.add_btn);
		mAddButton.setOnClickListener(this);

		return rootView;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu_simple_recycler_view, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		if (item.getItemId() == R.id.action_remove) {
			removeItemFromList();
		}
		return true;
	}

	private void addItemToList() {
		Data.Model model = new Data.Model("inset-item-title","insert-item-desc");

		int position = ((LinearLayoutManager)mRecyclerView.getLayoutManager()).
				findFirstVisibleItemPosition();
		position++;
		dataList.add(position,model);
		mAdapter.addData(model, position);
	}

	private void removeItemFromList() {
		int position = ((LinearLayoutManager)mRecyclerView.getLayoutManager()).
				findFirstCompletelyVisibleItemPosition();
		dataList.remove(position);
		mAdapter.removeData(position);
	}

	@Override
	public void onClick(View v) {
		 if(v.getId() == R.id.add_btn){
			 addItemToList();
		 }
	}

}
