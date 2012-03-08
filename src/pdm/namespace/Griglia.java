package pdm.namespace;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.View;

public class Griglia extends View {

	private Bitmap tabella = null;
	private int x, y;

	public Griglia(Context context, int newWidth, int newHeigth, int POSx,
			int POSy) {
		super(context);
		x = POSx;
		y = POSy;
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		tabella = Pedina.getResizeBitmap(BitmapFactory.decodeResource(
				context.getResources(), R.drawable.grigliav2), newHeigth,
				newWidth);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawBitmap(tabella, x, y, null);
	}
}
