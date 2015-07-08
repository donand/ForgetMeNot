package com.forgetmenot.forgetmenot;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * helper methods.
 */
public class NotificationService extends IntentService {
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String ACTION_NOTIFICA_ACQUA = "com.forgetmenot.forgetmenot.action.NOTIFICA_ACQUA";
    public static final String ACTION_NOTIFICA_FERTILIZZANTE = "com.forgetmenot.forgetmenot.action.NOTIFICA_FERTILIZZANTE";

    private static final String TAG = "NotificationService";

    private String nomeAssegnato, nomeGenerale, immagine;
    private int livelloAcqua, livelloConcimazione, idPossesso;


    public NotificationService() {
        super("NotificationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Log.v(TAG, "NotificationService partito");
            final String action = intent.getAction();
            idPossesso = intent.getIntExtra(DettagliPiantaUtente.ID, -1);
            nomeAssegnato = intent.getStringExtra(DettagliPiantaUtente.NOME_ASSEGNATO);
            nomeGenerale = intent.getStringExtra(DettagliPiantaUtente.NOME_GENERALE);
            immagine = intent.getStringExtra(DettagliPiantaUtente.IMMAGINE);
            livelloAcqua = intent.getIntExtra(DettagliPiantaUtente.LIVELLO_ACQUA, 0);
            livelloConcimazione = intent.getIntExtra(DettagliPiantaUtente.LIVELLO_CONCIMAZIONE, 0);

            if (ACTION_NOTIFICA_ACQUA.equals(action)) {
                handleActionNotificaAcqua(intent);
            } else if (ACTION_NOTIFICA_FERTILIZZANTE.equals(action)) {
                handleActionNotificaFertilizzante(intent);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionNotificaAcqua(Intent input) {
        Intent intent = new Intent(this, DettagliPiantaUtente.class).setAction(ACTION_NOTIFICA_ACQUA);
        intent.putExtras(input);
        intent.putExtra("fromNotificationService", true);
        Log.v(TAG, "nomi: " + intent.getStringExtra(DettagliPiantaUtente.NOME_ASSEGNATO) + ", " + intent.getStringExtra(DettagliPiantaUtente.NOME_GENERALE) + ", " +
                "id: " + intent.getIntExtra(DettagliPiantaUtente.ID, -1));
        long[] pattern = {0, 300, 0};
        //PendingIntent pi = PendingIntent.getActivity(this, idPossesso, intent, 0);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack
        stackBuilder.addParentStack(DettagliPiantaUtente.class);
        // Adds the Intent to the top of the stack
        stackBuilder.addNextIntent(intent);
        // Gets a PendingIntent containing the entire back stack
        PendingIntent pi =
                stackBuilder.getPendingIntent(idPossesso, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.fmn_logo)
                .setContentTitle("Innaffia " + nomeAssegnato)
                .setContentText("La tua pianta ha bisogno di essere innaffiata!")
                .setVibrate(pattern)
                .setAutoCancel(true);

        mBuilder.setContentIntent(pi);
        mBuilder.setDefaults(Notification.DEFAULT_SOUND);
        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(idPossesso, mBuilder.build());
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionNotificaFertilizzante(Intent input) {
        Intent intent = new Intent(this, DettagliPiantaUtente.class).setAction(ACTION_NOTIFICA_FERTILIZZANTE);
        intent.putExtras(input);
        intent.putExtra("fromNotificationService", true);
        long[] pattern = {0, 300, 0};
        PendingIntent pi = PendingIntent.getActivity(this, idPossesso, intent, 0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.fmn_logo)
                .setContentTitle("Concima " + nomeAssegnato)
                .setContentText("La tua pianta ha bisogno di essere concimata!")
                .setVibrate(pattern)
                .setAutoCancel(true);

        mBuilder.setContentIntent(pi);
        mBuilder.setDefaults(Notification.DEFAULT_SOUND);
        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(idPossesso, mBuilder.build());
    }
}
