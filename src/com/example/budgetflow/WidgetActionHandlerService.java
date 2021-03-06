package com.example.budgetflow;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

public class WidgetActionHandlerService extends Service {

	public void onStart(Intent intent, int startId) {
		Log.v("BF", "Handler Launched");
		String command = intent.getAction();
		int widgetId = intent.getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);
		AppWidgetManager aw = AppWidgetManager
				.getInstance(getApplicationContext());
		RemoteViews views = new RemoteViews(getApplicationContext()
				.getPackageName(), R.layout.widgetlayout);
		

		PreferenceWrapper pref = new PreferenceWrapper(this);
        
        int tens = pref.getTens();
        int ones = pref.getOnes();
        int dimes = pref.getDimes();
        int cents = pref.getCents();
        
    	float daily_allowance = pref.getDailyAllowance();
    	float payments_today = pref.getPaymentsToday();
        
        if (command == "increase tens") {
        	tens++;
        	if (tens > 9) tens = 0;
        }
        if (command == "increase ones") {
        	ones++;
        	if (ones > 9) ones = 0;
        }
        if (command == "increase dimes") {
        	dimes++;
        	if (dimes > 9) dimes = 0;
        }
        if (command == "increase cents") {
        	cents+=5;
        	if (cents > 9) cents = 0;
        }
        
        if(command == "register payment") {
        	float payment_total = tens*10 + ones + (float)dimes/10 + (float)cents/100;
        	payments_today += payment_total;
        	tens = 0;
        	ones = 0;
        	dimes = 0;
        	cents = 0;
        }

    	pref.setPaymentsToday(payments_today);
    	pref.setTens(tens);
    	pref.setOnes(ones);
    	pref.setDimes(dimes);
    	pref.setCents(cents);
		
		views.setCharSequence(R.id.tens, "setText", "" + tens);
		views.setCharSequence(R.id.ones, "setText", "" + ones);
		views.setCharSequence(R.id.dimes, "setText", "" + dimes);
		views.setCharSequence(R.id.cents, "setText", "" + cents);

		views.setInt(R.id.moneybar, "setMax", (int) (daily_allowance*100));
		views.setInt(R.id.moneybar, "setProgress", (int) ((daily_allowance - payments_today)*100));
		views.setCharSequence(R.id.progressText, "setText", Math.rint(((daily_allowance-payments_today)*100))/100 + "\\" + daily_allowance);
  		aw.updateAppWidget(widgetId, views);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;

	}

	public static PendingIntent makeControlPendingIntent(Context context,
			String command, int appWidgetId) {
		Intent active = new Intent(context, WidgetActionHandlerService.class);
		active.setAction(command);
		active.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		// this Uri data is to make the PendingIntent unique, so it wont be
		// updated by FLAG_UPDATE_CURRENT
		// so if there are multiple widget instances they wont override each
		// other
		Uri data = Uri.withAppendedPath(
				Uri.parse("countdownwidget://widget/id/#" + command
						+ appWidgetId), String.valueOf(appWidgetId));
		active.setData(data);
		return (PendingIntent.getService(context, 0, active,
				PendingIntent.FLAG_UPDATE_CURRENT));
	}

}
