package pdm.namespace.online;

import pdm.namespace.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class Login extends Activity {

	private String username, password;
	private EditText etexUser, etextPass;

	ConnectionManager connection;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		etexUser = (EditText) findViewById(R.id.etextUser);
		etextPass = (EditText) findViewById(R.id.etextPass);
		Button btnJoin = (Button) findViewById(R.id.btnJoin);
		btnJoin.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// salvo dati accesso utente
				username = etexUser.getText().toString()
						+ "@ppl.eln.uniroma2.it";
				password = etextPass.getText().toString();
				// lancio activity ListPlayer
				Intent intent = new Intent(Login.this, SearchPlayer.class);
				intent.putExtra("user", username);
				intent.putExtra("pass", password);
				startActivity(intent);
			}
		});
	}

}
