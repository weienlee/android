package com.weienlee.set;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Chronometer;
import android.widget.GridView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;

public class GameActivity extends Activity {

	private GridView gridView;
	private Deck deck;
	private List<Card> deckCards;
	private List<Card> currentCards;
	private List<String> selectedCards = new ArrayList<String> ();
	private List<String> currentPointers = new ArrayList<String> ();
	private CardAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
        // remove title
		/* requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN); */
		
		setContentView(R.layout.activity_game);
		
		// Show the Up button in the action bar.
		setupActionBar();
		
		startGame();
		gridView = (GridView) findViewById(R.id.gridView);
		adapter = new CardAdapter(this, currentCards);
		gridView.setAdapter(adapter);
		
	    gridView.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event){
				handleTouch(event);
				return true;
			}
		});
	    
	    
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.game, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void clickItemView(int px, int py) {
		int pv = gridView.pointToPosition(px,py);
		long id = gridView.pointToRowId(px,py);
		View lv = gridView.getChildAt(pv);
		if (0 <= pv && pv <= 15) {
			toggleCard(lv,pv);
		}
		//gridView.performItemClick(lv, pv, id);
	}
	
	private void handleTouch(MotionEvent event) {
		int pointerCount = event.getPointerCount();
		
		for (int i = 0; i < Math.min(pointerCount,3); i++) {
			int x = (int) event.getX(i);
			int y = (int) event.getY(i);    		
	    	int id = event.getPointerId(i);
	    	String idString = id+"";
	    	int action = event.getActionMasked();
	    	int actionIndex = event.getActionIndex();
    		int pv = gridView.pointToPosition(x,y);
	    		
	    	switch (action)	{
	    	case MotionEvent.ACTION_DOWN:
	    		if (!currentPointers.contains(idString)) {
	    			clickItemView(x,y);
	    			currentPointers.add(idString);
	    		}
	    		break;
	    	case MotionEvent.ACTION_UP:
	    		currentPointers.remove(idString);
	    		break;	
	    	case MotionEvent.ACTION_POINTER_DOWN:
	    		if (!currentPointers.contains(idString)) {
	    			clickItemView(x,y);
	    			currentPointers.add(idString);
	    		}
	    		break;
	    	case MotionEvent.ACTION_POINTER_UP:
	    			currentPointers.remove(idString);
	    		break;
	    	case MotionEvent.ACTION_MOVE:
	    		break;
	    	default:
	    		break;
	    	}
		}
	}
	
	private void startGame() {
		deck = new Deck();
		currentCards = deck.getCards().subList(0, 12);
		deckCards = deck.getCards().subList(12,81);
		((Chronometer) findViewById(R.id.timer)).start();
		
	}
	
	private void toggleCard(View v, int position) {
		String positionString = position+"";
		if (selectedCards.contains(positionString)) {
			v.findViewById(R.id.card_border).setBackgroundColor(Color.WHITE);
			selectedCards.remove(positionString);
		} else {
			selectedCards.add(positionString);
			v.findViewById(R.id.card_border).setBackgroundColor(Color.BLACK);
			if (selectedCards.size() == 3) {
				if (testSet(selectedCards)) {
					dealNewSet();
					clearSelected();
					adapter.notifyDataSetChanged();
				} else {
					clearSelected();
					Toast.makeText(GameActivity.this, "invalid set", Toast.LENGTH_SHORT).show();
				}
			}
		}
	}
	
	private void dealNewSet() {
		if (deckCards.size()>0) {
			for (int i=0; i<3; i++) {
				int position = Integer.parseInt(selectedCards.get(i));
				currentCards.set(position, deckCards.get(i));
			}
			deckCards = deckCards.subList(3, deckCards.size());
		} else {
			Toast.makeText(GameActivity.this, "you win!", Toast.LENGTH_SHORT).show();
		}
		
	}
	
	private void clearSelected() {
		for (String card : selectedCards) {
			int position = Integer.parseInt(card);
			gridView.getChildAt(position).findViewById(R.id.card_border).setBackgroundColor(Color.WHITE);
		}
		selectedCards.clear();
	}
	
	private boolean testSet(List<String> inputSet) {
		assert (inputSet.size() == 3);
		Card card1 = currentCards.get(Integer.parseInt(inputSet.get(0)));
		Card card2 = currentCards.get(Integer.parseInt(inputSet.get(1)));
		Card card3 = currentCards.get(Integer.parseInt(inputSet.get(2)));
		
		boolean number = (checkCharacteristic(card1.getNumber(), card2.getNumber(), card3.getNumber()));
		boolean color = (checkCharacteristic(card1.getColor(), card2.getColor(), card3.getColor()));
		boolean shape = (checkCharacteristic(card1.getShape(), card2.getShape(), card3.getShape()));
		boolean shading = (checkCharacteristic(card1.getShading(), card2.getShading(), card3.getShading()));
		
		return (number && color && shape && shading);

	}

	private boolean checkCharacteristic(Object c1, Object c2, Object c3) {
		if (c1.equals(c2) && c2.equals(c3)) {
			return true;
		} else if (!c1.equals(c2) && !c2.equals(c3) && !c1.equals(c3)) {
			return true;
		} else {
			return false;
		}
	}
}
