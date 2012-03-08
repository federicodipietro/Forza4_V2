package pdm.namespace;

import android.app.Activity;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class TwoPlayers extends Activity{

	LinearLayout ll;
	FrameLayout fl;
	RelativeLayout rl;
	LayoutParams lpFl,lpRl;//layout params
	
	private int heigthRelative,
	heigthDisplay,widthDisplay,
	heigthTabella,widthTabella;
	
	private int step, col, touchx;
	int matr[][]=new int [6][7];
	
	private Griglia tabella;
	private Indicatore ind;
	
	float divx, divy, diam, offsetX, offsetY;
	
	boolean gio=true;
	boolean win=false;
	
	//per suoni
	MediaPlayer Tock,lancio,vittoria;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forza4);
		
		 fl = (FrameLayout)findViewById(R.id.frLayLocal);
		 fl.setBackgroundColor(Color.DKGRAY);
	     rl = (RelativeLayout)findViewById(R.id.relLayLocal);
	     rl.setBackgroundColor(Color.WHITE);
	     ll = (LinearLayout)findViewById(R.id.linaerLayLocal);
	     
	     heigthDisplay = getWindowManager().getDefaultDisplay().getHeight();
	     widthDisplay = getWindowManager().getDefaultDisplay().getWidth();
	        
	     Log.d("dimensioni display (xy)", Integer.toString(widthDisplay)+"  "+Integer.toString(heigthDisplay));
	     
	     if(widthDisplay/7>heigthDisplay/6)
	    	 step=(int)heigthDisplay/6;
	     else
	    	 step=(int)widthDisplay/7;
	        
	     widthTabella=step*7;
	     heigthTabella=step*6;
	    
	     tabella = new Griglia(TwoPlayers.this, widthTabella, heigthTabella,0,step/2);
	     
	     RedimLayouts(fl,widthTabella,(int)(heigthTabella+1.5*step), rl);
	     
	     ll.removeAllViewsInLayout();
		 ll.addView(fl);
		 ll.addView(rl);
		 fl.addView(tabella);
		 
		 //***********souni****************
	     Tock=MediaPlayer.create(TwoPlayers.this, R.raw.tok);
	     vittoria=MediaPlayer.create(TwoPlayers.this, R.raw.win);
	     // lancio=MediaPlayer.create(Forza4Activity.this, R.raw.lancio);
	     //*********************************
	     
	     //*****************per centrare pedine**************
	     	//rapporto tra largh pedina e larghezza griglia
	     Double temp =0.79277108433733*step;
	     diam=temp.floatValue();
	        
	      	//rapporto distanza bordo e primo buco e largh griglia
	     temp=0.320448192771084*step;
	     offsetX=temp.floatValue();
	        
	        //rapporto distanza bordo e primo buco e altezza griglia
	     temp=0.30167597765363*step;
	     offsetY=temp.floatValue();
	        
	        //rapporto tra distanza tra 2 buchi e largh griglia
	     temp=0.13493975903615*step;
	     divx = temp.floatValue();
	        
	        //rapporto tra distanza tra 2 buchi e altezza griglia
	     temp=0.13128491620112*step;
	     divy = temp.floatValue();
	     
	     fl.setOnTouchListener(new OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				int eventaction = event.getAction();
        		switch (eventaction)
        		{
        		case MotionEvent.ACTION_UP:
        		{
        			if(!win)
        			{
        				touchx=(int)event.getX();
            			col=Functions.getCol(touchx, step);
            			
            			if(matr[0][col]==0)
            			{
            				matr=Functions.inputMatr(matr,col,gio);
                			Tock.start();
            				gio=!gio;
            				fl.removeAllViews();
            				ind = new Indicatore(TwoPlayers.this,event.getX()-step/2,0,step,step/2,gio);
            				Functions.printG(matr, offsetX, offsetY+step/2, diam, divx, divy, TwoPlayers.this, fl);
            				fl.addView(tabella);
            				fl.addView(ind);
            				win=Functions.checkWin(matr, TwoPlayers.this,win);
            			}
               			
               			if(win)vittoria.start();
        			}
        			else
        				Toast.makeText(TwoPlayers.this, "Partita conclusa", Toast.LENGTH_SHORT).show();
        		}
        			break;
        		case MotionEvent.ACTION_MOVE:
        		{	if(!win){
        			ind = new Indicatore(TwoPlayers.this,event.getX()-step/2,0,step,step/2,gio);
        			fl.removeAllViews();
    				Functions.printG(matr, offsetX, offsetY+step/2, diam, divx, divy, TwoPlayers.this, fl);
    				fl.addView(tabella);
        			fl.addView(ind);
        		}}break;
        		default:
        		{	if (!win){
        			ind = new Indicatore(TwoPlayers.this,event.getX()-step/2,0,step,step/2,gio);
        			fl.removeAllViews();
    				Functions.printG(matr, offsetX, offsetY+step/2, diam, divx, divy, TwoPlayers.this, fl);
    				fl.addView(tabella);
        			fl.addView(ind);
        		}}break;
        		}
        		return true;
			}
		});
	     
	    Button btnNew = (Button)findViewById(R.id.btnNew);
	    btnNew.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				matr = new int [6][7];
				gio=true;
				win=false;
				fl.removeAllViews();
				fl.addView(tabella);
				Toast.makeText(TwoPlayers.this,"Nuova Partita", Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	
	//ridimensiona relative layuot
	 private void RedimLayouts(FrameLayout fl,int newWidth,int newHeigth,View rl){
	    	lpFl = new LayoutParams(widthDisplay,
					newHeigth);
			
			heigthRelative=heigthDisplay-newHeigth;
			
			lpRl = new LayoutParams(widthDisplay,
					heigthRelative);
			
			fl.setLayoutParams(lpFl);
			rl.setLayoutParams(lpRl);
			
	    }
	 
	 @Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
