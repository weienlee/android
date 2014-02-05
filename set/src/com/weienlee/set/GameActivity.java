package com.weienlee.set;


import java.util.ArrayList;
import java.util.Arrays;
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
import android.widget.Button;
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
	private List<Card> currentCards = new ArrayList<Card>();
	private List<String> selectedCards = new ArrayList<String> ();
	private List<String> currentPointers = new ArrayList<String> ();
	private CardAdapter adapter;
	private final int mask0 = 85; //0b01010101
	private final int mask1 = 170; //0b10101010
	boolean extraCards = false;
	private int deckSize = 81;
	
	
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
	    
	    Button noSetButton = (Button) findViewById(R.id.no_set_button);
	    noSetButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	if (!noSet()) {
            		Toast.makeText(GameActivity.this, "there is a set somewhere", Toast.LENGTH_SHORT).show();
            	} else {
            		dealExtraCards();
    				adapter.notifyDataSetChanged();
            	}
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
		deckSize = deck.getCards().size();
		for (int i=0;i<12;i++) {
			currentCards.add(deck.getCards().get(i));
		}
		//deckCards = deck.getCards().subList(12,81);
		deckCards = deck.getCards().subList(12,deckSize);
		deckSize -= 12;
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
		if (deckSize>0) {
			if (!extraCards) {
				for (int i=0; i<3; i++) {
					int position = Integer.parseInt(selectedCards.get(i));
					currentCards.set(position, deckCards.get(i));
				}
				deckCards = deckCards.subList(3, deckCards.size());
				deckSize -= 3;
			} else if (extraCards) {
				if (selectedCards.contains("12") && selectedCards.contains("13") && selectedCards.contains("14")) {
					dealExtraCards();
				} else {
					List<Card> extraCardsList = currentCards.subList(12, 15);
					List<Integer> toBeReplaced = new ArrayList<Integer>();
					List<Integer> leftOver = new ArrayList<Integer>(Arrays.asList(12, 13, 14));
					for (int i=0; i<3; i++) {
						int position = Integer.parseInt(selectedCards.get(i));
						if (position < 12) {
							toBeReplaced.add(position);
						} else {
							leftOver.remove((Integer)position);
						}
					}
					for (int i=0; i<toBeReplaced.size(); i++) {
						currentCards.set(toBeReplaced.get(i), extraCardsList.get(leftOver.get(i)-12));
					}
					currentCards.remove(14);
					currentCards.remove(13);
					currentCards.remove(12);
					
					extraCards = false;
					
				}
			}
		} else {
			Toast.makeText(GameActivity.this, "you win!", Toast.LENGTH_SHORT).show();
		}
	}
	
	private void dealExtraCards() {
		if (deckSize > 0) {
			if (!extraCards) {
				currentCards.addAll(deckCards.subList(0,3));
				extraCards = true;
			} else if (extraCards) {
				for (int i=0; i<3; i++) {
					currentCards.set(i+12,deckCards.get(i));
				}
			}
			
			deckCards = deckCards.subList(3, deckCards.size());
			deckSize -= 3;
			
		} else {
			Toast.makeText(GameActivity.this, "you win!", Toast.LENGTH_SHORT).show();
			// win();
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
		
		boolean number = (((card1.getNumber() + card2.getNumber() + card3.getNumber()) % 3) == 0);
		boolean color = (((card1.getColor() + card2.getColor() + card3.getColor()) % 3) == 0);
		boolean shape = (((card1.getShape() + card2.getShape() + card3.getShape()) % 3) == 0);
		boolean shading = (((card1.getShading() + card2.getShading() + card3.getShading()) % 3) == 0);
		
		return (number && color && shape && shading);

	}
	
	private int thirdCardBits(Card card1, Card card2) {
		int x = card1.getBits();
		int y = card2.getBits();
		int xor = x^y;
		int swap = ((xor & mask1) >> 1) | ((xor & mask0) << 1);
		return (x&y) | (~(x|y) & swap);
	}
	
	
	// algorithm based on the one described at the following site:
	// http://code.activestate.com/recipes/578508-finding-sets-in-the-card-game-set/
	private boolean noSet() {
		int[] have = new int[256];
		for (Card card : currentCards) {
			have[card.getBits()] = currentCards.indexOf(card);
		}
		for (int i=0; i<currentCards.size(); i++) {
			for (int j=i+1; j<currentCards.size(); j++) {
				int k = have[thirdCardBits(currentCards.get(i), currentCards.get(j))];
				if (k > j) {
					return false;
				}
			}
		}
		return true;
	}
}
