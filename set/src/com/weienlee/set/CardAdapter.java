package com.weienlee.set;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class CardAdapter extends BaseAdapter {
	private Context context;
	private List<Card> currentCards;

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
		String imageName = card.getColor() + "_" + card.getShape() + "_" + card.getShading();
		imageName = imageName.toLowerCase();

		for (int i = 1; i <= card.getNumber(); i++) {
			int resID = context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());
			ImageView image = (ImageView) (inflater.inflate(R.layout.card_item, innerContainer, false));
			image.setImageResource(resID);
			innerContainer.addView(image);
		}

		cardView.setContentDescription(currentCards.get(position).toString());
		return cardView;
	}
}
