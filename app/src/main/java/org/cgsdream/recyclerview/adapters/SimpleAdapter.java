package org.cgsdream.recyclerview.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.cgsdream.recyclerview.R;
import org.cgsdream.recyclerview.data.Data;

import java.util.List;

/**
 * Created by sm on 2015/6/6.
 */
public class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.ListItemViewHolder> {

	private List<Data.Model> mList;
	private SparseBooleanArray mSelectedItems;

	public SimpleAdapter(List<Data.Model> list){
		if(list == null){
			throw new IllegalArgumentException("model Data must not be null");
		}
		mList = list;
	}

	public void addData(Data.Model data,int posotion){
		mList.add(posotion,data);
		notifyItemInserted(posotion);
	}
	public void removeData(int position){
		mList.remove(position);
		notifyItemRemoved(position);
	}
	@Override
	public ListItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
		View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_list_item,viewGroup,false);
		return new ListItemViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(ListItemViewHolder listItemViewHolder, int i) {
		Data.Model model = mList.get(i);
		listItemViewHolder.title.setText(model.title);
		listItemViewHolder.desc.setText(model.desc);
	}

	@Override
	public int getItemCount() {
		return mList.size();
	}

	public class ListItemViewHolder extends RecyclerView.ViewHolder{
		TextView title;
		TextView desc;
		public ListItemViewHolder(View itemView){
			super(itemView);
			title = (TextView)itemView.findViewById(R.id.txt_title);
			desc = (TextView)itemView.findViewById(R.id.txt_desc);
		}
	}


}
