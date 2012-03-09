package pdm.namespace.online;

import java.util.Timer;
import java.util.TimerTask;

import pdm.namespace.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class SearchPlayer extends Activity implements MessageReceiver {

	ConnectionManager connection;
	Timer timer;

	enum Stato {
		WAIT_FOR_HELLO, SEND_HELLO
	}

	Stato statocorrente;
	String username, password, Avversario;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.saerchplayers);

		username = getIntent().getExtras().getString("user");
		password = getIntent().getExtras().getString("pass");

		connection = new ConnectionManager(username, password, this);
		Log.d("LOGIN", "Connesso");

		// mi metto in ascolto
		statocorrente = Stato.WAIT_FOR_HELLO;
		// setto il timer, aspetta 20 secondi(delay) e poi invia un msg "HELLO"
		// ogni 5 sec(period)
		timer = new Timer();
		TimerTask ascolta = new TimerTask() {

			@Override
			public void run() {
				if (statocorrente == Stato.WAIT_FOR_HELLO) {
					// ho aspettato ora provo a cercare
					statocorrente = Stato.SEND_HELLO;
				} else if (statocorrente == Stato.SEND_HELLO) {
					// invio un messaggio in broadcast al gruppo
					String Gruppo = "rossi@broadcast.ppl.eln.uniroma2.it";
					connection.send(Gruppo, "HELLO");
					Log.d("BROADCAST", "inviato HELLO");
				}
				Log.d("Stato", statocorrente.toString());

			}
		};
		timer.schedule(ascolta, 10000L, 7000L);

	}

	public void receiveMessage(String From, String msg) {
		// parte broadcast***********************
		if (msg.equals("HELLO")) {
			connection.send(From, "HELLOACK");

			Log.d("BROADCAST", "Ack inviato a " + From);

		} else if (msg.equals("HELLOACK")) {
			// aggiungo giocatore che sono online

			connection.send(From, "HELLOACK");
			Log.d("BROADCAST", "Ack inviato a " + From);
			
			timer.cancel();
			connection.close();
			Intent intent = new Intent(SearchPlayer.this, Forza4Online.class);
			intent.putExtra("Avversario", From);
			intent.putExtra("user", username);
			intent.putExtra("pass", password);
			startActivity(intent);

			Log.d("BROADCAST", "Ack ricevuto da " + From);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		timer.cancel();
		connection.close();
		Log.d("BROADCAST", "connessione chiusa");
	}
}
