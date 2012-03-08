package pdm.namespace;

import android.content.Context;
import android.widget.FrameLayout;
import android.widget.Toast;

public class Functions {

	public static int[][] inputMatr(int matr[][], int col, boolean gio) {
		for (int i = 5; i >= 0; i--) {
			if (matr[i][col] == 0) {
				if (gio)
					matr[i][col] = 1;
				else
					matr[i][col] = 2;
				break;
			}
		}
		return matr;
	}

	public static int getCol(int touchx, int raggio) {
		int col = 0;
		if (0 <= touchx && touchx < raggio)
			col = 0;
		else if (raggio <= touchx && touchx < 2 * raggio)
			col = 1;
		else if (2 * raggio <= touchx && touchx < 3 * raggio)
			col = 2;
		else if (3 * raggio <= touchx && touchx < 4 * raggio)
			col = 3;
		else if (4 * raggio <= touchx && touchx < 5 * raggio)
			col = 4;
		else if (5 * raggio <= touchx && touchx < 6 * raggio)
			col = 5;
		else if (6 * raggio <= touchx)
			col = 6;
		return col;
	}

	private static Pedina ped;

	public static void printG(int matr[][], float offsetX, float offsetY,
			float diam, float divx, float divy, Context context, FrameLayout fl) {
		for (int i = 0; i < 6; i++) {
			for (int k = 0; k < 7; k++) {
				if (matr[i][k] != 0) {
					ped = new Pedina(context, offsetX + k * (diam + divx),
							offsetY + i * (diam + divy), diam, matr[i][k]);
					fl.addView(ped);
				}
			}
		}
	}

	// conta pedine inserite
	private static int cont;

	public static boolean checkWin(int matr[][], Context context, boolean win) {
		int temp;
		cont = 0;

		for (int j = 0; j < 6; j++)// j=coordy
		{
			if (win)
				break;
			for (int i = 0; i < 7; i++)// i= coordX
			{
				temp = matr[j][i];
				if (temp != 0) {
					cont = cont + 1;
					// orizzontali
					if (i < 4) {
						if (temp == matr[j][i + 1] && temp == matr[j][i + 2]
								&& temp == matr[j][i + 3]) {
							win = ChihaVinto(temp, context, win);
							// ("orizzontale da sx verso dx");
							break;
						}
					} else if (i >= 4) {
						if (temp == matr[j][i - 1] && temp == matr[j][i - 2]
								&& temp == matr[j][i - 3]) {
							win = ChihaVinto(temp, context, win);
							// ("orizzontale da dx verso sx");
							break;
						}
					}
					// verticali
					if (j <= 2) {

						if (temp == matr[j + 1][i] && temp == matr[j + 2][i]
								&& temp == matr[j + 3][i]) {
							win = ChihaVinto(temp, context, win);
							if (win) {
								// ("verticale");
							}
						}
					}

					// diagonale
					// basso sx
					if (j > 2 && i <= 3) {
						if (temp == matr[j - 1][i + 1]
								&& temp == matr[j - 2][i + 2]
								&& temp == matr[j - 3][i + 3]) {
							win = ChihaVinto(temp, context, win);
							if (win)
								break;
						}
					}// alto sx
					else if (j <= 2 && i <= 3) {
						if (temp == matr[j + 1][i + 1]
								&& temp == matr[j + 2][i + 2]
								&& temp == matr[j + 3][i + 3]) {
							win = ChihaVinto(temp, context, win);
							if (win)
								break;
						}
					}// alto dx
					else if (j <= 2 && i >= 3) {
						if (temp == matr[j + 1][i - 1]
								&& temp == matr[j + 2][i - 2]
								&& temp == matr[j + 3][i - 3]) {
							win = ChihaVinto(temp, context, win);
							if (win)
								break;
						}
					}// basso dx
					else if (j > 2 && i >= 3) {
						if (temp == matr[j - 1][i - 1]
								&& temp == matr[j - 2][i - 2]
								&& temp == matr[j - 3][i - 3]) {
							win = ChihaVinto(temp, context, win);
							if (win)
								break;
						}
					}
				}
			}
		}
		if (cont == 42 && win != true) {
			win = ChihaVinto(0, context, win);
		}
		return win;
	}

	public static boolean ChihaVinto(int player, Context context, boolean win) {
		if (player == 1) {
			Toast.makeText(context, "Giocatore 1 Vince", Toast.LENGTH_LONG)
					.show();
			return true;
		} else if (player == 2) {
			Toast.makeText(context, "Giocatore 2 Vince", Toast.LENGTH_LONG)
					.show();
			return true;
		} else if (player == 0) {
			Toast.makeText(context, "Parita'", Toast.LENGTH_LONG).show();
			return true;
		} else
			return false;

	}
}
