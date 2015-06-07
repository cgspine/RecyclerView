package org.cgsdream.recyclerview.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.cgsdream.recyclerview.R;
import org.cgsdream.recyclerview.data.Data;

import java.util.Date;
import java.util.List;

/**
 * Created by sm on 2015/6/7.
 */
public class MutiItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	private List<Data.Model> mList;
	private enum ITEM_TYPE{
		ITEM_TYPE_ONE,
		ITEM_TYPE_TWO
	}

	public MutiItemAdapter(List<Data.Model> list){
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
	public int getItemViewType(int position) {
		return position%2==0?ITEM_TYPE.ITEM_TYPE_ONE.ordinal():ITEM_TYPE.ITEM_TYPE_TWO.ordinal();
	}

	@Override
	public int getItemCount() {
		return mList.size();
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		 if(holder instanceof ItemOneViewHolder){
			 ItemOneViewHolder h = (ItemOneViewHolder)holder;
			 h.title.setText("title" + position);
			 h.desc.setText(new Date().toString());
		 }else if(holder instanceof ItemTwoViewHolder){
			 ItemTwoViewHolder h = (ItemTwoViewHolder)holder;
			 h.title.setText("title" + position);
			 h.img.setImageResource(R.mipmap.ic_launcher);
		 }
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		if(viewType == ITEM_TYPE.ITEM_TYPE_ONE.ordinal()){
			return new ItemOneViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_list_item,parent,false));
		}else if(viewType == ITEM_TYPE.ITEM_TYPE_TWO.ordinal()){
			return new ItemTwoViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_list_item_2,parent,false));
		}
		return null;
	}

	public class ItemOneViewHolder extends RecyclerView.ViewHolder{
		TextView title;
		TextView desc;
		public ItemOneViewHolder(View itemView) {
			super(itemView);
			title = (TextView)itemView.findViewById(R.id.txt_title);
			desc = (TextView)itemView.findViewById(R.id.txt_desc);
		}
	}

	public class ItemTwoViewHolder extends RecyclerView.ViewHolder{
		TextView title;
		ImageView img;
		public ItemTwoViewHolder(View itemView) {
			super(itemView);
			title = (TextView)itemView.findViewById(R.id.item_title);
			img = (ImageView)itemView.findViewById(R.id.item_img);
		}
	}
}
