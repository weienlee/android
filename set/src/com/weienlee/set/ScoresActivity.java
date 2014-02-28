package com.weienlee.set;

import java.text.DecimalFormat;

import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

public class ScoresActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scores);
		// Show the Up button in the action bar.
		setupActionBar();
		
		TextView labelView = (TextView)findViewById(R.id.high_scores_label);
		TextView scoreView = (TextView)findViewById(R.id.high_scores_list);
		SharedPreferences scores = getSharedPreferences(GameActivity.PREFS_NAME, 0);
		
		String labelText = "";
		String scoresText = "";
		
		DecimalFormat formatter = new DecimalFormat("#,###");
		for (int i=0;i<10;i++) {
			labelText += (i+1) + ".  \n";
			scoresText += (formatter.format(scores.getInt("score"+i, 0)) + "\n");
		}
		
		labelView.setText(labelText);
		scoreView.setText(scoresText);
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.scores, menu);
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

}
