package com.weienlee.set;


import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class CardAdapter extends BaseAdapter {
	private Context context;
	private List<Card> currentCards;
	private String[] colorMap = {"green", "purple", "red"};
	private String[] shapeMap = {"diamond", "round", "squiggle"};
	private String[] shadingMap = {"empty", "filled", "stripe"};
	
	//private HashMap<String, Integer> images = new HashMap<String, Integer>();
	
	public CardAdapter(Context context, List<Card> currentCards) {
		this.context = context;
		this.currentCards = currentCards;
	}

	@Override
	public int getCount() {
		return this.currentCards.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View cardView;
		cardView = inflater.inflate(R.layout.card, null);
		LinearLayout innerContainer = (LinearLayout) cardView
				.findViewById(R.id.card_inner_container);

		Card card = currentCards.get(position);
		String imageName = colorMap[card.getColor()] + "_" + 
				shapeMap[card.getShape()] + "_" +
				shadingMap[card.getShading()];

		for (int i = 1; i <= card.getNumber()+1; i++) {
			int resID = context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());
			ImageView image = (ImageView) (inflater.inflate(R.layout.card_item, innerContainer, false));
			image.setImageResource(resID);
			innerContainer.addView(image);
		}

		cardView.setContentDescription(currentCards.get(position).toString());
		return cardView;
	}
}
