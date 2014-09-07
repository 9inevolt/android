package gg.destiny.app.service;

import gg.destiny.app.chat.PlayerActivity;
import gg.destiny.app.chat.R;
import gg.destiny.app.model.Channel;
import gg.destiny.app.util.StreamEventListener;
import gg.destiny.app.util.StreamWatcher;
import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.*;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class StreamWatcherService extends Service implements StreamEventListener
{
    public static final String TAG = "StreamWatcherService";
    public static final String EXTRA_CHANNEL_NAME = "gg.destiny.intent.channel.NAME";
    private static final int NOTIFICATION_ONLINE = 1;

    private StreamWatcher watcher;
    private String name;

    @Override
    public void onCreate()
    {
        super.onCreate();
        Log.d(TAG, "started");
    }

    @Override
    public void onDestroy()
    {
        Log.d(TAG, "stopped");
        watcher.stop();
        getNotificationManager().cancel(NOTIFICATION_ONLINE);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        name = intent.getStringExtra(EXTRA_CHANNEL_NAME);
        Channel c = new Channel(name.toLowerCase(), name);
        watcher = new StreamWatcher(c, true);
        watcher.start(this);
        return Service.START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        // No binding
        return null;
    }

    @Override
    public void online()
    {
        Notification n = build()
            .setContentTitle(name)
            .setContentText("is now live")
            .setSmallIcon(R.drawable.defaulticon)
            .setAutoCancel(true)
            .setContentIntent(createPendingIntent())
            .build();

        getNotificationManager().notify(NOTIFICATION_ONLINE, n);
    }

    @Override
    public void offline()
    {
        getNotificationManager().cancel(NOTIFICATION_ONLINE);
    }

    @Override
    public void offlineImage(Bitmap bm)
    {
        // no-op
    }

    @Override
    public void channel(Channel channel)
    {
        // no-op
    }

    private NotificationCompat.Builder build()
    {
        return new NotificationCompat.Builder(this);
    }

    private NotificationManager getNotificationManager()
    {
        return (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private PendingIntent createPendingIntent()
    {
        Intent intent = new Intent(this, PlayerActivity.class);
        intent.setAction(Intent.ACTION_SEARCH);
        intent.putExtra(SearchManager.QUERY, name);

        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}