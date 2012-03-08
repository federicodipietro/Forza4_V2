package pdm.namespace;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class Main extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		ImageButton btn2Play = (ImageButton) findViewById(R.id.btn2Player);
		ImageButton btnOnline = (ImageButton) findViewById(R.id.btnOnline);

		// lancia activity per giocare il locale
		btn2Play.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				Intent intent = new Intent(Main.this, TwoPlayers.class);
				startActivity(intent);
			}
		});
		// lancia activity per giocare online
		btnOnline.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				Intent intent = new Intent(Main.this,
						pdm.namespace.online.Login.class);
				startActivity(intent);
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}