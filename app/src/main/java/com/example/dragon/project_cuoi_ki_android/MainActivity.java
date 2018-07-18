package com.example.dragon.project_cuoi_ki_android;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.dragon.project_cuoi_ki_android.history.HistoryTabFragment;
import com.example.dragon.project_cuoi_ki_android.offlineMusic.ViewPagerAdapter;
import com.example.dragon.project_cuoi_ki_android.offlineMusic.music.MusicTabFragment;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
        , SharedPreferences.OnSharedPreferenceChangeListener
        ,SlidingUpPanelLayout.PanelSlideListener{

    //    variable
    int quality;
    Boolean playAllPlaylist;
    Boolean headphonePullOut;
    Boolean headphonePullIn;
    Boolean saveSearchHistory;
    String language;
    private MenuItem navigateCurrentItem;
    private int numMusicTab;//số lượng tab mục music
    String a;
    //view
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private TabLayout tabLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;
    //parent content view
    private ViewStub musictabViewStub;
    private ViewStub historyTabViewStub;
    private HistoryTabFragment historyTabFragment=null;
    //requestCode permission
    private final int permissionCode = 1;
    //dialog
    AlertDialog permissionAlertDialog;
    private View thisView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //chuyern hết hàm cần khởi tạo vào onPermission result
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(this.getResources().getStringArray(R.array.tab_title)[0]);
        setSupportActionBar(toolbar);
        a = getString(R.string.pref_key_quality_select);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.offmusic);
        navigationView.setNavigationItemSelectedListener(this);
        ///set color cho title navigation
        changeTitleColor(R.id.Drawer_title_account, navigationView, R.style.DrawTitleColor);
        changeTitleColor(R.id.Drawer_title_offline, navigationView, R.style.DrawTitleColor);
        changeTitleColor(R.id.Drawer_title_info, navigationView, R.style.DrawTitleColor);

        //lưu setting vào trong bộ nhớ
        SharedPreferences pref = getSharedPreferences("appPreferences", MODE_PRIVATE);
        SharedPreferences.Editor prefEdit = pref.edit();
        pref.registerOnSharedPreferenceChangeListener(this);
        numMusicTab = this.getResources().getStringArray(R.array.tab_title).length;

        //demo
        historyTabViewStub = (ViewStub) findViewById(R.id.historyTabViewStub);
        if (historyTabViewStub != null) {
            historyTabViewStub.setLayoutResource(R.layout.history_tab);
            historyTabViewStub.inflate();
        }
        musictabViewStub = (ViewStub) findViewById(R.id.musicTabViewStub);
        if (musictabViewStub != null) {
            musictabViewStub.setLayoutResource(R.layout.tab_container);
            musictabViewStub.inflate();
        }
        SlidingUpPanelLayout slidingUpPanelLayout= (SlidingUpPanelLayout)findViewById(R.id.sliding_layout);
        slidingUpPanelLayout.addPanelSlideListener(this);
        checkPermission();

    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                + ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //request permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    permissionCode);
//            onOffMusicSelected();
        } else {
            onOffMusicSelected();
        }
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case permissionCode: {
                boolean isAllGranted = true;
                for (int g : grantResults) {
                    if (g == PackageManager.PERMISSION_DENIED) {
                        isAllGranted = false;
                        break;
                    }
                }
                if (grantResults.length > 0 && !isAllGranted) {
                    Log.d("show dialog", "dialog show");
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("This application need permission to run");
                    builder.setPositiveButton("yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finishAndRemoveTask();
                                }
                            });
                    permissionAlertDialog = builder.create();
                    permissionAlertDialog.show();
                } else {
                    checkPermission();
                }
                break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (permissionAlertDialog != null) permissionAlertDialog.dismiss();
        Log.d("destroy", "running");
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    //menu goc tren tay phai

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.setting) {
            //view setting page
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
        }
        if (id == R.id.offmusic) {
            onOffMusicSelected();
        }
        if (id == R.id.history) {
            onHistorySelected();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    //ham hiển thị phần offline music
    private void onOffMusicSelected() {
        toolbar.setTitle("Music");
        if (historyTabViewStub != null) {
            historyTabViewStub.setVisibility(View.GONE);
        }
        if (musictabViewStub != null) {
            musictabViewStub.setVisibility(View.VISIBLE);
        }
        //add view pager vao content_main

            viewPager = findViewById(R.id.pager);
            viewPager.setOffscreenPageLimit(numMusicTab);
            tabLayout = findViewById(R.id.tabLayout);
            viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), numMusicTab, this);
            viewPager.setAdapter(viewPagerAdapter);
            tabLayout.setupWithViewPager(viewPager);

    }

    //ham hiển thị phần offline music
    private void onHistorySelected() {
        toolbar.setTitle("History");
        if (musictabViewStub != null) {
            musictabViewStub.setVisibility(View.GONE);
        }
        if (historyTabViewStub != null) {
            historyTabViewStub.setVisibility(View.VISIBLE);
        }
        if(historyTabFragment==null) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft_add = fm.beginTransaction();
            historyTabFragment = new HistoryTabFragment();
            ft_add.add(R.id.HistoryTabFragment, historyTabFragment);
            ft_add.commit();
        }else{

        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        //key == key trong xml
        if (key.equalsIgnoreCase(a)) {
            String quality_select_setting = sharedPreferences.getString(a, "128");
            Toast.makeText(this, "" + quality_select_setting, Toast.LENGTH_SHORT).show();
        }
    }

    public void changeTitleColor(int idTitle, NavigationView navView, int styleXml) {
        Menu menu = navigationView.getMenu();
        MenuItem tools = menu.findItem(idTitle);
        SpannableString s = new SpannableString(tools.getTitle());
        s.setSpan(new TextAppearanceSpan(this, R.style.DrawTitleColor), 0, s.length(), 0);
        tools.setTitle(s);
    }

    @Override
    public void onPanelSlide(View panel, float slideOffset) {

    }

    @Override
    public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
        if(newState==SlidingUpPanelLayout.PanelState.COLLAPSED){
            ImageView btnClose = (ImageView)findViewById(R.id.player_title_close_button);
            btnClose.setVisibility(View.INVISIBLE);
        }
        if(previousState == SlidingUpPanelLayout.PanelState.EXPANDED ){
            ImageView btnClose = (ImageView)findViewById(R.id.player_title_close_button);
            btnClose.setVisibility(View.VISIBLE);
        }
    }
}
