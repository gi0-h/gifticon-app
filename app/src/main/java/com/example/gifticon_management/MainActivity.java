package com.example.gifticon_management;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import java.io.File;


public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerviewAdapter adapter;
    FloatingActionButton gifticon_add_button;
    //GificonDailog gificonDailog;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle barDrawerToggle;


    List<MainData> dataList = new ArrayList<>();
    RoomDB database;

    ActivityResultLauncher<Intent> activityResultLauncher; // 기프티콘 추가 액티비티 실행할 때 필요

    public String readDay = null;
    public String str = null;
    public CalendarView calendarView;
    public Button cha_Btn, del_Btn, save_Btn;
    public TextView diaryTextView, textView2, textView3;
    public EditText contextEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 네비게이션 뷰
        navigationView = findViewById(R.id.navigationView);
        drawerLayout = findViewById(R.id.layout_drawer);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_setting: // 설정 눌렀을 때
                        Intent intent = new Intent(MainActivity.this, settingActivity.class);
                        startActivity(intent); // 설정 창 실행
                        break;
                    case R.id.menu_setting2:
                        Intent intent2 = new Intent(MainActivity.this, calenderActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.menu_setting3:
                         database.mainDao().reset(dataList);
                         dataList.clear();
                         dataList.addAll(database.mainDao().getAll());
                         adapter.notifyDataSetChanged();
                        String path = "/data/data/com.example.gifticon_management/files";
                        File deleteFolder = new File(path);
                        File[] deleteFolderList = deleteFolder.listFiles();
                        for (int j = 0; j < deleteFolderList.length; j++  ) {
                            if(deleteFolderList[j].toString().contains(".txt"))
                            {}
                            else deleteFolderList[j].delete();
                        }
                        break;



                }
                // Drawer 닫기
                drawerLayout.closeDrawer(navigationView);
                return false;
            }
        });

        // 누르면 Drawer 열리는 삼선 모양 버튼
        barDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.app_name, R.string.app_name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        barDrawerToggle.syncState();
        drawerLayout.addDrawerListener(barDrawerToggle);


        database = RoomDB.getInstance(this);
        dataList = database.mainDao().getAll();

        recyclerView = findViewById(R.id.recyclerView);
        gifticon_add_button = (FloatingActionButton) findViewById(R.id.gifticon_add_button);
        int numColumns = 3;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new RecyclerviewAdapter(MainActivity.this, dataList);

        // 기프티콘 터치
        adapter.setOnItemClickListener(new RecyclerviewAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(int position, String data) {
                //여기에 intent 정보 전달
                Intent intent = new Intent(MainActivity.this, gifticon_touch_activity.class);
                intent.putExtra("name", dataList.get(position).getText());
                intent.putExtra("date", dataList.get(position).getDate_text());
                //날짜 입력
                intent.putExtra("year", dataList.get(position).getYy());
                intent.putExtra("month", dataList.get(position).getMm());
                intent.putExtra("day", dataList.get(position).getDd());
                //이미지 입력
                Bitmap sendBitmap = dataList.get(position).getImage();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                sendBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                intent.putExtra("image_gif", byteArray);

                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);


        gifticon_add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, gifticon_input.class);
                activityResultLauncher.launch(intent); // 기프티콘 추가 액티비티 시작
                //startActivity(intent);
            }
        });

        activityResultLauncher = registerForActivityResult( // 기프티콘 추가 액티비티에서 확인 버튼 누르면 실행됨
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == 9001) {
                        dataList.clear();
                        dataList.addAll(database.mainDao().getAll());
                        adapter.notifyDataSetChanged(); // 새로 추가한 데이터 갱신
                    }
                });


        // 데이터 모두 삭제

//        database.mainDao().reset(dataList);
//        dataList.clear();
//        dataList.addAll(database.mainDao().getAll());
//        adapter.notifyDataSetChanged();


        //데이터 임시 추가

//        for(int i=0; i<3; i++){
//            MainData temp = new MainData();
//            temp.setText("스타벅스"+i);
//            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sample3);
//            temp.setImage(bitmap);
//            database.mainDao().insert(temp);
//
//            dataList.clear();
//            dataList.addAll(database.mainDao().getAll());
//            adapter.notifyDataSetChanged();
//        }

    }

    @Override // 네비게이션 뷰의 메뉴를 터치했을 때 실행
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        barDrawerToggle.onOptionsItemSelected(item);
        return super.onOptionsItemSelected(item);
    }



}