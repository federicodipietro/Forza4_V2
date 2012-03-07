package pdm.namespace.online;

import java.util.Timer;
import java.util.TimerTask;

import pdm.namespace.Functions;
import pdm.namespace.Griglia;
import pdm.namespace.Indicatore;
import pdm.namespace.Pedina;
import pdm.namespace.R;
import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class Forza4Online extends Activity implements MessageReceiver {

	private static final int SHOW_TOAST = 0;
	private static final int REFRESH_VIEW = 1;

	LinearLayout ll;
	FrameLayout fl;
	RelativeLayout rl;
	LayoutParams lpFl, lpRl;// layout params

	enum Stato {
		WAIT_FOR_START, WAIT_FOR_START_ACK, WAIT_FOR_SELECT, USER_SELECTING
	}

	Stato statoCorrente;
	Handler handler;
	Button b;
	Timer timer;

	// comunica colonna che altro player ha selezionato
	private String selectedCol;

	ConnectionManager connection;

	Pedina ped;
	Griglia tabella;

	float divx, divy, diam, offsetX, offsetY;
	int matr[][] = new int[6][7];

	boolean gio = true;
	boolean win = false;
	// per suoni
	MediaPlayer Tock, lancio, vittoria;

	private int heigthRelative, heigthDisplay, widthDisplay, heigthTabella,
			widthTabella;

	private int step, col, touchx;

	private Indicatore ind;

	private String nomeMio, password, nomeAvversario;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forza4);

		fl = (FrameLayout) findViewById(R.id.frLayLocal);
		rl = (RelativeLayout) findViewById(R.id.relLayLocal);
		ll = (LinearLayout) findViewById(R.id.linaerLayLocal);

		// ***********souni****************
		Tock = MediaPlayer.create(Forza4Online.this, R.raw.tok);
		vittoria = MediaPlayer.create(Forza4Online.this, R.raw.win);
		// *********************************

		heigthDisplay = getWindowManager().getDefaultDisplay().getHeight();
		widthDisplay = getWindowManager().getDefaultDisplay().getWidth();
		Log.d("dimensioni display (xy)", Integer.toString(widthDisplay) + "  "
				+ Integer.toString(heigthDisplay));

		if (widthDisplay / 7 > heigthDisplay / 6)
			step = (int) heigthDisplay / 6;
		else
			step = (int) widthDisplay / 7;

		widthTabella = step * 7;
		heigthTabella = step * 6;

		// ****disegna tabella*********
		tabella = new Griglia(Forza4Online.this, widthTabella, heigthTabella,
				0, step / 2);
		RedimLayouts(fl, widthTabella, (int) (heigthTabella + 1.5 * step), rl);

		ll.removeAllViewsInLayout();
		ll.addView(fl);
		ll.addView(rl);
		fl.addView(tabella);
		// ****************************

		// rapporto tra largh pedina e larghezza griglia*7
		Double temp = 0.79277108433733 * step;
		diam = temp.floatValue();

		// rapporto distanza bordo e primo buco e largh griglia*7
		temp = 0.320448192771084 * step;
		offsetX = temp.floatValue();

		// rapporto distanza bordo e primo buco e altezza griglia*6
		temp = 0.30167597765363 * step;
		offsetY = temp.floatValue();

		// rapporto tra distanza tra 2 buchi e largh griglia
		temp = 0.13493975903615 * step;
		divx = temp.floatValue();

		// rapporto tra distanza tra 2 buchi e altezza griglia
		temp = 0.13128491620112 * step;
		divy = temp.floatValue();

		// ***************************da passare da login*****************

		nomeMio = getIntent().getExtras().getString("user");
		password = getIntent().getExtras().getString("pass");
		nomeAvversario = getIntent().getExtras().getString("Avversario");
		Log.w(nomeMio, nomeAvversario);
		// ^^^^^^^*******************************************************************

		// *******************CONNESSIONE*****************************************************
		connection = new ConnectionManager(nomeMio, password);
		timer = new Timer();
		TimerTask sendStart = new TimerTask() {

			@Override
			public void run() {

				if (statoCorrente == Stato.WAIT_FOR_START_ACK) {
					connection.send(nomeAvversario, "START");
					Log.d("Stato", "inviato START");
				} else {
					Log.d("ATTENZIONE", "Sending START but the state is "
							+ statoCorrente);
				}
			}
		};
		// decido chi comincia
		if (nomeAvversario.hashCode() <= nomeMio.hashCode()) {
			// inizio per primo
			gio = true;
			Toast.makeText(Forza4Online.this, "Sei giocatore 1",
					Toast.LENGTH_SHORT).show();
			timer.schedule(sendStart, 1000L, 5000L);
			statoCorrente = Stato.WAIT_FOR_START_ACK;
			Log.d("Stato", statoCorrente.toString());
		} else {
			// inizia avversario e io aspetto il pacchetto
			gio = false;
			Toast.makeText(Forza4Online.this, "Sei giocatore 2",
					Toast.LENGTH_SHORT).show();
			statoCorrente = Stato.WAIT_FOR_START;
			Log.d("Stato", statoCorrente.toString());
		}
		// creo handler
		handler = new Handler() {
			@Override
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {
				case Forza4Online.SHOW_TOAST:
					Toast.makeText(Forza4Online.this,
							msg.getData().getString("toast"), Toast.LENGTH_LONG)
							.show();
					break;
				case Forza4Online.REFRESH_VIEW: {
					Tock.start();
					fl.removeAllViews();

					ind = new Indicatore(Forza4Online.this, touchx - step / 2,
							0, step, step / 2, gio);

					Functions.printG(matr, offsetX, offsetY + step / 2, diam,
							divx, divy, Forza4Online.this, fl);
					fl.addView(tabella);
					fl.addView(ind);
					win = Functions.checkWin(matr, Forza4Online.this, win);
				}
				default:
					super.handleMessage(msg);
				}
			}
		};
		// ************************************************************************************************

		fl.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (statoCorrente == Stato.WAIT_FOR_START
						|| statoCorrente == Stato.WAIT_FOR_START_ACK) {
					Toast.makeText(Forza4Online.this,
							"Aspetta mossa avversario", Toast.LENGTH_SHORT)
							.show();
					return false;
				} else if (statoCorrente == Stato.USER_SELECTING) {
					int eventaction = event.getAction();
					switch (eventaction) {
					case MotionEvent.ACTION_UP: {
						if (!win) {
							touchx = (int) event.getX();
							col = Functions.getCol(touchx, step);

							if (matr[0][col] == 0) {
								matr = Functions.inputMatr(matr, col, gio);
								Tock.start();
								gio = !gio;

								// invia colonna selezionata*****
								connection.send(nomeAvversario, "SELECTED:"
										+ Integer.toString(col));
								// *******

								fl.removeAllViews();
								ind = new Indicatore(Forza4Online.this, touchx
										- step / 2, 0, step, step / 2, gio);
								Functions.printG(matr, offsetX, offsetY + step
										/ 2, diam, divx, divy,
										Forza4Online.this, fl);
								fl.addView(tabella);
								fl.addView(ind);
								win = Functions.checkWin(matr,
										Forza4Online.this, win);
							}

							if (win)
								vittoria.start();
						} else
							Toast.makeText(Forza4Online.this,
									"Partita conclusa", Toast.LENGTH_SHORT)
									.show();
					}
						break;
					case MotionEvent.ACTION_MOVE: {
						if (!win) {
							ind = new Indicatore(Forza4Online.this, event
									.getX() - step / 2, 0, step, step / 2, gio);
							fl.removeAllViews();
							Functions.printG(matr, offsetX, offsetY + step / 2,
									diam, divx, divy, Forza4Online.this, fl);
							fl.addView(tabella);
							fl.addView(ind);
						}
					}
						break;
					default: {
						if (!win) {
							ind = new Indicatore(Forza4Online.this, touchx
									- step / 2, 0, step, step / 2, gio);
							fl.removeAllViews();
							Functions.printG(matr, offsetX, offsetY + step / 2,
									diam, divx, divy, Forza4Online.this, fl);
							fl.addView(tabella);
							fl.addView(ind);
						}
					}
						break;
					}
					return true;
				} else {
					Toast.makeText(Forza4Online.this, "Attendi il tuo turno",
							Toast.LENGTH_SHORT).show();
					return false;
				}
			}
		});

	}

	// ridimensiona relative layuot
	private void RedimLayouts(FrameLayout fl, int newWidth, int newHeigth,
			View rl) {
		lpFl = new LayoutParams(widthDisplay, newHeigth);

		heigthRelative = heigthDisplay - newHeigth;

		lpRl = new LayoutParams(widthDisplay, heigthRelative);

		fl.setLayoutParams(lpFl);
		rl.setLayoutParams(lpRl);

	}

	public void receiveMessage(String From, String msg) {

		if (msg.equals("START")) {
			if (statoCorrente == Stato.WAIT_FOR_START) {
				// mando ACK
				connection.send(nomeAvversario, "STARTACK");
				Message osmsg = handler.obtainMessage(Forza4Online.SHOW_TOAST);
				Bundle b = new Bundle();
				b.putString("toast", "Aspetta");
				osmsg.setData(b);
				handler.sendMessage(osmsg);
				statoCorrente = Stato.WAIT_FOR_SELECT;
				Log.d("Stato", statoCorrente.toString());
			} else {
				Log.e("ATTENZIONE", "Ricevuto START ma lo stato e' "
						+ statoCorrente);
			}
		}
		// ricevo start_ack e aspetto che avversario gioca
		else if (msg.equals("STARTACK")) {
			if (statoCorrente == Stato.WAIT_FOR_START_ACK) {

				timer.cancel();

				Log.w("ATTENZIONE", "Timer cancel");

				statoCorrente = Stato.USER_SELECTING;
				Log.d("Stato", statoCorrente.toString());
				Toast.makeText(Forza4Online.this, "Tocca a te",
						Toast.LENGTH_SHORT).show();

			} else {
				Log.e("ATTENZIONE", "Ricevuto STARTACK ma lo stato e' "
						+ statoCorrente);
			}
		} else if (msg.startsWith("SELECTED")) {
			if (statoCorrente == Stato.WAIT_FOR_SELECT) {
				selectedCol = msg.split(":")[1];
				Message osmsg = handler.obtainMessage(Forza4Online.SHOW_TOAST);
				Bundle b = new Bundle();

				matr = Functions.inputMatr(matr, Integer.parseInt(selectedCol),
						!gio);

				b.putString("toast", "Tocca a te");
				osmsg.setData(b);
				handler.sendMessage(osmsg);

				statoCorrente = Stato.USER_SELECTING;
				Log.d("Stato", statoCorrente.toString());
			} else {
				Log.e("ATTENZIONE", "Ricevuto SELECTED ma lo stato e' :"
						+ statoCorrente);
			}
		}
	}

}
