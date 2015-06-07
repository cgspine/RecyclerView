package org.cgsdream.recyclerview.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by sm on 2015/6/6.
 */
public class Data {
	private static Data instance;
	private List<Model> mList;

	private Data(){
		generate();
	}

	public static Data getInstance(){
		if(instance == null){
			instance = new Data();
		}
		return instance;
	}

	public List<Model> getData(){
		return mList;
	}


	private void generate(){
		mList = new ArrayList<Model>();
		for(int i = 0;i<1000;i++){
			Model model =new  Model("title" +i,i+"======"+new Date().toString());
			mList.add(model);
		}
	}

	public static class Model{
		public String title;
		public String desc;
		public Model(String title,String desc){
			this.title = title;
			this.desc = desc;
		}
	}



}
