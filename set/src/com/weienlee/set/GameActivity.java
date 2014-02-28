package com.weienlee.set;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import android.os.Bundle;
import android.os.SystemClock;
import android.app.Activity;
import android.app.AlertDialog;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;
import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;

public class GameActivity extends Activity {

	public static final String PREFS_NAME = "MyPrefsFile";
	private GridView gridView;
	private Deck deck;
	private List<Card> deckCards;
	private List<Card> currentCards = new ArrayList<Card>();
	private List<String> selectedCards = new ArrayList<String> ();
	private List<String> currentPointers = new ArrayList<String> ();
	private List<Integer> highScores = new ArrayList<Integer> ();
	private CardAdapter adapter;
	private Chronometer timer;
	private final int mask0 = 85; //0b01010101
	private final int mask1 = 170; //0b10101010
	boolean extraCards = false;
	private int deckSize = 81;
	private long timeWhenStopped = 0;
	private boolean paused = false;
	private boolean gameOver = false;
	
	// score related
	private int errors = 0;
	private int noSetErrors = 0;
	private int numSets = 0;
	private int numNoSets = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
        // remove title
		/* requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN); */
		
		Window win = getWindow();
		win.addFlags(WindowManager.LayoutParams.FLAG_SECURE);
		
		setContentView(R.layout.activity_game);
		
		// Show the Up button in the action bar.
		setupActionBar();
		
		// set up timer
		timer = ((Chronometer) findViewById(R.id.timer));
		
		// Restore preferences
	    SharedPreferences scores = getSharedPreferences(PREFS_NAME, 0);
	    for (int i=0; i<10; i++) {
	    	Integer score = scores.getInt("score"+i, 0); 
	    	highScores.add(score);
	    }

		
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
            		noSetErrors += 1;
            		Toast.makeText(GameActivity.this, "there is a set somewhere", Toast.LENGTH_SHORT).show();
            	} else {
            		numNoSets += 1;
            		dealExtraCards();
    				adapter.notifyDataSetChanged();
            	}
            }
	    });

	    Button pauseButton = (Button) findViewById(R.id.pause_button);
	    pauseButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	pause();
            }
	    });
	    
	    ImageView pauseImage = (ImageView) findViewById(R.id.pause_icon);
	    pauseImage.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v) {
	        	pause();
	        }
	    });
	    
	    ImageView noSetImage = (ImageView) findViewById(R.id.no_set_icon);
	    noSetImage.setOnClickListener(new View.OnClickListener() {
	    	@Override
	    	public void onClick(View v) {
            	if (!noSet()) {
            		Toast toast = Toast.makeText(GameActivity.this, "there is a set somewhere", Toast.LENGTH_SHORT);
            		toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 200);
            		toast.show();
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
			getActionBar().setDisplayHomeAsUpEnabled(false);
			getActionBar().setDisplayShowHomeEnabled(false);
			//getActionBar().setDisplayShowTitleEnabled(false);
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

	public void onStop() {
		super.onStop();
		if (!paused && !gameOver) {
			pause();
		}
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
		deckCards = deck.getCards().subList(12,deckSize);
		deckSize -= 12;
		timer.setBase(SystemClock.elapsedRealtime());
		timer.start();
		getActionBar().setTitle("Cards Remaining: 81");
	}
	
	private void restartGame() {
		
		// reset values
		extraCards = false;
		paused = false;
		gameOver = false;
		timeWhenStopped = 0;
		
		// reset score values
		errors = 0;
		noSetErrors = 0;
		numSets = 0;
		numNoSets = 0;
        
        //clear all lists
		selectedCards.clear();
		currentPointers.clear();
		currentCards.clear();
		deckCards.clear();
		
		// start new game
		startGame();
		adapter.notifyDataSetChanged();
	}
	
	@SuppressLint("NewApi")
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
					/*
					List<ObjectAnimator> objAnimList = new ArrayList<ObjectAnimator>();
					AnimatorSet animSet = new AnimatorSet();
					animSet.addListener(new AnimatorListener() {

						@Override
						public void onAnimationCancel(Animator animation) {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void onAnimationEnd(Animator animation) {
							// TODO Auto-generated method stub
							dealNewSet();
							adapter.notifyDataSetChanged();
							gridView.getChildAt(Integer.parseInt(selectedCards.get(0))).setAlpha(0);
							gridView.getChildAt(Integer.parseInt(selectedCards.get(1))).setAlpha(0);
							gridView.getChildAt(Integer.parseInt(selectedCards.get(2))).setAlpha(0);
							clearSelected();
						}

						@Override
						public void onAnimationRepeat(Animator animation) {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void onAnimationStart(Animator animation) {
							// TODO Auto-generated method stub
							
						}
						
					});
					
					ObjectAnimator anim1 = ObjectAnimator.ofFloat(gridView.getChildAt(Integer.parseInt(selectedCards.get(0))), "alpha", 0);
					ObjectAnimator anim2 = ObjectAnimator.ofFloat(gridView.getChildAt(Integer.parseInt(selectedCards.get(1))), "alpha", 0);
					ObjectAnimator anim3 = ObjectAnimator.ofFloat(gridView.getChildAt(Integer.parseInt(selectedCards.get(2))), "alpha", 0);
					
					objAnimList.add(anim1);
					objAnimList.add(anim2);
					objAnimList.add(anim3);
					
					ObjectAnimator[] objectAnimators = objAnimList.toArray(new ObjectAnimator[objAnimList.size()]);
					animSet.playTogether(objectAnimators);
					animSet.setDuration(250);
					animSet.start();
					*/

					dealNewSet();
					clearSelected();
					adapter.notifyDataSetChanged();
					
					getActionBar().setTitle("Cards Remaining: " + (deckSize+currentCards.size()));
					numSets += 1;
				} else {
					clearSelected();
					errors += 1;
					Toast toast = Toast.makeText(GameActivity.this, "invalid set", Toast.LENGTH_SHORT);
            		toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 200);
            		toast.show();
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
			// get selected cards in descending order
			Integer[] toBeRemoved = new Integer[3];
			for (int i=0; i<3; i++) {
				toBeRemoved[i] = Integer.parseInt(selectedCards.get(i));
			}
			Arrays.sort(toBeRemoved);
			
			// removed selected cards
			for (int i=2; i>=0; i--) {
				currentCards.remove((int)toBeRemoved[i]);	
			}
			
			if (noSet()) {
				win();
			}
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
			getActionBar().setTitle("Cards Remaining: " + (deckSize+currentCards.size()));
		} else {
			win();
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

	
private void pause() {
		gridView.setVisibility(View.INVISIBLE);
		timeWhenStopped = timer.getBase() - SystemClock.elapsedRealtime();
		timer.stop();
		paused = true;
		AlertDialog.Builder pauseBuilder = new AlertDialog.Builder(this);
        pauseBuilder.setMessage("click resume to continue playing!");
        pauseBuilder.setCancelable(true);
        pauseBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {         
        	@Override
        	public void onCancel(DialogInterface dialog) {
        		resume();
            }
        });
        pauseBuilder.setPositiveButton("New Game",
                new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            	restartGame();
                dialog.cancel();
            }
        });
        pauseBuilder.setNegativeButton("Resume", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            	resume();
                dialog.cancel();
            }
        });

        AlertDialog pauseDialog = pauseBuilder.create();
        pauseDialog.show();
}
	
	private void resume() {
		gridView.setVisibility(View.VISIBLE);
		timer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
		timer.start();
		paused = false;
	}
	
	
	private void win() {
		timeWhenStopped = timer.getBase() - SystemClock.elapsedRealtime();
		timer.stop();
		gameOver=true; // to reject pause dialog onStop()
		boolean newHighScore = false;
		
		int finalScore = score(); 
		if (finalScore > highScores.get(9)) {
			
			// Save score if high score
			highScores.add(finalScore);
			Collections.sort(highScores, Collections.reverseOrder());
			SharedPreferences scores = getSharedPreferences(PREFS_NAME, 0);
			SharedPreferences.Editor editor = scores.edit();
			for (int i=0; i<10; i++) {
				editor.putInt("score" + i, highScores.get(i));
			}
		
			// commit changes
			editor.commit();
		
			newHighScore = true;
		}
			
		DecimalFormat formatter = new DecimalFormat("#,###");
		AlertDialog.Builder winBuilder = new AlertDialog.Builder(this);

		if (newHighScore) {
			winBuilder.setTitle("New high score!");
		} else {
			winBuilder.setTitle("Game over!");
		}
		
		String winMessage = "Final score: \t" + formatter.format(finalScore);
        winBuilder.setMessage(winMessage);
        
        winBuilder.setCancelable(false);
        winBuilder.setPositiveButton("New Game",
                new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            	restartGame();
                dialog.cancel();
            }
        });
        winBuilder.setNegativeButton("Main Menu",
        		new DialogInterface.OnClickListener() {
        	public void onClick(DialogInterface dialog, int id) {
        		Intent intent = new Intent(GameActivity.this, MainActivity.class);
        		startActivity(intent);
        	}
        });
        
        AlertDialog winDialog = winBuilder.create();
        winDialog.show();
	}
	
	private int score() {
		int baseScore = numSets * 100 + numNoSets * 500 - 10 * errors;
		long ms = -timeWhenStopped;
		float seconds = ms/1000;
		seconds = seconds + noSetErrors;
		float bonusSeconds = Math.max((600-seconds), 0);
		int bonusScore = (int) Math.round(1.1*Math.pow(2, 16)*(Math.pow(Math.E,(bonusSeconds/(650-bonusSeconds))) - 1));
		int total = baseScore + bonusScore;
		return total;
	}
	
}
