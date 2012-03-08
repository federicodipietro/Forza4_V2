package pdm.namespace;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.View;

public class Indicatore extends View {

	private Bitmap ind = null;
	private float x, y;

	public Indicatore(Context context, float POSx, float POSy, float width,
			float heigth, boolean gio) {
		super(context);
		Bitmap img = null;
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		x = POSx;
		y = POSy;
		if (gio == true)
			img = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.indicatoreyelw);
		else if (gio == false)
			img = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.indicatorered);
		ind = Pedina.getResizeBitmap(img, heigth, width);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawBitmap(ind, x, y, null);
	}
}
