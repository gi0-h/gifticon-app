package com.example.gifticon_management;

        import androidx.annotation.RequiresApi;
        import androidx.appcompat.app.AppCompatActivity;

        import android.graphics.Color;
        import android.os.Bundle;
        import android.view.View;
        import android.widget.Button;

        import java.io.File;
        import java.io.FileInputStream;
        import java.util.ArrayList;
        import java.util.Arrays;
        import java.util.Collection;
        import java.util.List;

        import androidx.core.app.NotificationCompat;
        import androidx.core.app.NotificationManagerCompat;

        import android.app.NotificationChannel;
        import android.app.NotificationManager;
        import android.app.PendingIntent;
        import android.content.Context;
        import android.content.Intent;
        import android.os.Build;

        import android.widget.Toast;
        import java.util.Calendar;
        import java.util.Collections;
public class settingActivity extends AppCompatActivity {
    // 알림생성버튼
    private Button create;
    // 알림제거버튼
    private Button remove;
    Button setting_back_button, alarmButton_on;
    List<MainData> dataList = new ArrayList<>();
    RoomDB database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_setting);

        database = RoomDB.getInstance(this);
        dataList = database.mainDao().getAll();
        setting_back_button = (Button) findViewById(R.id.setting_back_button);

        create = findViewById(R.id.create);
        remove = findViewById(R.id.remove);

        create.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View view) {

                createNotification();
                Toast.makeText(settingActivity.this,"알림이 설정되었습니다.",Toast.LENGTH_SHORT).show();
            }
        });

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                removeNotification();
                Toast.makeText(settingActivity.this,"알림이 해제되었습니다.",Toast.LENGTH_SHORT).show();
            }
        });

        setting_back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        dataList.addAll(database.mainDao().getAll());
    }

    private void createNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");

        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("만료알림");

        List a = Arrays.asList(fileList());
        String[] b ;
        long k = 0;
        ArrayList mill_list = new ArrayList();
        String[] list = fileList();
        int count = 0;
        long p = 0;
        long t = 10000;
        long c = 0;
        int index = 0;
        for (int i = 0; i <list.length; i++) {
            String str = a.get(i).toString();
            if (str.contains(".txt")) {
            }
            else{
                String spl[] = str.split("-");
                count ++;
                int y = Integer.parseInt(spl[0]);
                int m = Integer.parseInt(spl[1]);
                int d = Integer.parseInt(spl[2]);
                Calendar calendar = Calendar.getInstance(); //현재 날짜 불러옴
                int yy = calendar.get(Calendar.YEAR);
                int mm = calendar.get(Calendar.MONTH)+1;
                int dd = calendar.get(Calendar.DAY_OF_MONTH);
                calendar.set(yy, mm, dd);
                Calendar kCalendar = Calendar.getInstance(); //지금
                kCalendar.set(y, m, d);
                k = kCalendar.getTimeInMillis();
                c = calendar.getTimeInMillis();
                p = (k-c)/(24*60*60*1000);
                if(p<=t){
                    t=p;
                    index = i;
                }
            }
        }

        if(count >0){
            builder.setContentText("다음 기프티콘이 만료 예정입니다 - " + readfile(list[index]));
        }
        else builder.setContentText("기프티콘이 존재하지 않습니다");



        builder.setContentIntent(notificationPendingIntent);
        builder.setColor(Color.RED);

        // 사용자가 탭을 클릭하면 자동 제거
        builder.setAutoCancel(true);

        // 알림 표시
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel("default", "기본 채널", NotificationManager.IMPORTANCE_DEFAULT));
        }

        // id값은
        // 정의해야하는 각 알림의 고유한 int값
        notificationManager.notify(1, builder.build());
    }

    private void removeNotification() {
        // Notification 제거
        NotificationManagerCompat.from(this).cancel(1);
    }

    public String readfile(String readtext)
    {
        String strtemp = "";
        try
        {
            FileInputStream fis = openFileInput(readtext);
            StringBuffer sb = new StringBuffer();
            byte dataBuffer[] = new byte[1024];
            int n = 0;

            while((n=fis.read(dataBuffer))!=-1){
                sb.append(new String(dataBuffer));
            }
            strtemp = sb.toString();

            fis.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return strtemp;
    }



}