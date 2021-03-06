package com.rwan.spellproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {
    private static final int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 1;
    public static final String NOTIFICATION_ID = "1117";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent sintent = new Intent(MainActivity.this, SplashActivity.class);
        startActivity(sintent);

        ImageButton btnSpellChecker = findViewById(R.id.btnSpellChecker);
        btnSpellChecker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SpellCheckActivity.class);
                startActivity(intent);
            }
        });
        ImageButton btnSpellLog = findViewById(R.id.btnSpellLog);
        btnSpellLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SpellLogActivity.class);
                startActivity(intent);
            }
        });
        ImageButton btnChart = findViewById(R.id.btnChart);
        btnChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ChartActivity.class);
                startActivity(intent);
            }
        });
        ImageButton btnSett = findViewById(R.id.btnSett);
        btnSett.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
        Switch swFast = findViewById(R.id.swFast);
        if (isServiceRunning("com.rwan.spellproject.SpellService")) {
            swFast.setChecked(true);
        }
        else
        {
            swFast.setChecked(false);
        }
        swFast.setOnCheckedChangeListener(new visibilitySwitchListener());
    }
    class visibilitySwitchListener implements CompoundButton.OnCheckedChangeListener{
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked)
            {
                checkPermission();
                Intent intent = new Intent(MainActivity.this, SpellService.class);
                startService(intent);
            }
            else
            {
                closeNotification();
            }
        }
    }
    public Boolean isServiceRunning(String serviceName) {

        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo runningServiceInfo : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceName.equals(runningServiceInfo.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    public void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {   // ??????????????? ????????? ??????
            if (!Settings.canDrawOverlays(this)) {              // ??????
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
            } else {
                //startService(new Intent(MainActivity.this, MyService.class));
                popUpNotification();
                //startPopUpApplication();
            }
        } else {
            //startService(new Intent(MainActivity.this, MyService.class));
            popUpNotification();
            //startPopUpApplication();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                // TODO ????????? ?????? ????????? ????????? ??????
                final Snackbar snackbar = Snackbar.make(getWindow().getDecorView().getRootView(), "????????? ???????????? ????????? ???????????????.\n??? ?????? ???????????? ??????????????????.", Snackbar.LENGTH_INDEFINITE);
                //Snackbar.make(getWindow().getDecorView().getRootView(), "?????????", Snackbar.LENGTH_LONG).show();
                snackbar.setAction("??????", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        snackbar.dismiss();
                    }
                });
                snackbar.show();
            } else {
                popUpNotification();
                //startPopUpApplication();
                //startService(new Intent(MainActivity.this, MyService.class));
            }
        }
    }

    public void popUpNotification(){
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(this, PopActivity.class);
        notificationIntent.putExtra("notificationId",0); //????????? ???
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK) ;
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,  PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_ID)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_foreground)) //BitMap ????????? ??????
                .setContentTitle("????????? ?????????")
                .setContentText("???????????? ??????")
                // ??? ?????? ??????????????? ????????? ???????????? ?????? ?????? ?????? ????????? ???????????? setContentText??? ?????? ????????? ?????? ?????? ???????????? ?????????
                .setStyle(new NotificationCompat.BigTextStyle().bigText("????????? ???????????? ?????? ????????????.\n???????????? ???????????? ??????????????????."))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent) // ???????????? ????????????????????? ?????? ResultActivity??? ??????????????? ??????

                .setAutoCancel(false);

        //OREO API 26 ??????????????? ?????? ??????
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            builder.setSmallIcon(R.drawable.ic_launcher_foreground); //mipmap ????????? Oreo ???????????? ????????? UI ?????????
            CharSequence channelName  = "????????? ?????????";
            String description = "???????????? ??????";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(NOTIFICATION_ID, channelName , importance);
            channel.setDescription(description);

            // ?????????????????? ????????? ???????????? ??????
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);

        }else builder.setSmallIcon(R.mipmap.ic_launcher); // Oreo ???????????? mipmap ???????????? ????????? Couldn't create icon: StatusBarIcon ?????????

        assert notificationManager != null;
        notificationManager.notify(1234, builder.build()); // ??????????????? ?????????????????? ????????????
    }

    public void closeNotification()
    {
        NotificationManager notificationManager =(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(1234);
    }
    void startPopUpApplication()
    {
        Intent i = new Intent(this, PopSpellActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT|Intent.FLAG_ACTIVITY_MULTIPLE_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        Rect rect = new Rect(100, 800, 900, 700);
        ActivityOptions options = ActivityOptions.makeBasic();
        ActivityOptions bounds = options.setLaunchBounds(rect);
        startActivity(i, bounds.toBundle());
    }
}
