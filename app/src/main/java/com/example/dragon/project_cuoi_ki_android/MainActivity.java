package com.example.dragon.project_cuoi_ki_android;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.dragon.project_cuoi_ki_android.Controller.AboutUsActivity;
import com.example.dragon.project_cuoi_ki_android.Controller.FragmentBroadcast;
import com.example.dragon.project_cuoi_ki_android.Controller.ClientReceiver;
import com.example.dragon.project_cuoi_ki_android.Controller.NotificationMaker;
import com.example.dragon.project_cuoi_ki_android.Controller.ServiceReceiver;
import com.example.dragon.project_cuoi_ki_android.Controller.PlayerService;
import com.example.dragon.project_cuoi_ki_android.Utils.Utils;
import com.example.dragon.project_cuoi_ki_android.player.PlayerPlaylistTabFragment;
import com.example.dragon.project_cuoi_ki_android.player.PlayerTabFragment;
import com.example.dragon.project_cuoi_ki_android.player.PlayerViewPagerAdapter;
import com.example.dragon.project_cuoi_ki_android.history.HistoryTabFragment;
import com.example.dragon.project_cuoi_ki_android.model.Song;
import com.example.dragon.project_cuoi_ki_android.offlineMusic.ViewPagerAdapter;
import com.example.dragon.project_cuoi_ki_android.offlineMusic.music.MusicTabFragment;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
        , SharedPreferences.OnSharedPreferenceChangeListener
        , SlidingUpPanelLayout.PanelSlideListener
        , MusicTabFragment.dataTransaction {

    //    variable
    private int numMusicTab;//số lượng tab mục music
    //view
    private ViewPager viewPager;
    private ViewPager playerViewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private PlayerViewPagerAdapter playerViewPagerAdapter;
    private TabLayout tabLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;
    //saved data
    private SharedPreferences pref;
    //key + instance for saved data
    private String headphoneOut;
    private String headphoneIn;
    private String language;

    private boolean isHeadphoneOut;
    private boolean isHeadphoneIn;
    private String languageValue;
    //===player.View
    private ArrayList<Song> listSong = new ArrayList<>();
    private ArrayList<Song> listSongDB = new ArrayList<>();
    private RelativeLayout playerLayout;
    private TextView tvTitleSong;
    private TextView tvTitleArtist;
    private TextView tvCurDuration;
    private TextView tvDuration;
    private SeekBar sbSeeker;
    private ImageView player_icon_center;
    //player state
    private boolean isSeekBarSeeking = false;
    private boolean isPlayState = false;
    private int loopMode = 0;
    private boolean isShuffle = false;
    //
    //parent place holder
    private ViewStub musictabViewStub;
    private HistoryTabFragment historyTabFragment = null;
    private SlidingUpPanelLayout slidingUpPanelLayout;
    //requestCode permission
    private final int permissionCode = 1;
    //dialog
    private AlertDialog permissionAlertDialog;
    //service
    private PlayerService mService;
    boolean binded = false;
    private ClientReceiver clientReceiver;
    private Intent playerService;
    //notification
    private NotificationMaker notification = new NotificationMaker();

    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //chuyern hết hàm cần khởi tạo vào onPermission result
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(this.getResources().getStringArray(R.array.music_tab_title)[0]);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.offmusic);
        navigationView.setNavigationItemSelectedListener(this);
        ///set color cho title navigation
        changeTitleColor(R.id.Drawer_title_offline, navigationView, R.style.DrawTitleColor);
        changeTitleColor(R.id.Drawer_title_info, navigationView, R.style.DrawTitleColor);

        //broadcast receiver from service
        clientReceiver = new ClientReceiver(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ServiceReceiver.CURRENT_POSITION);
        filter.addAction(ServiceReceiver.PLAY);
        filter.addAction(ServiceReceiver.PAUSE);
        filter.addAction(ServiceReceiver.STOP);
        filter.addAction(ServiceReceiver.SHUFFLE);
        filter.addAction(ServiceReceiver.NEXT);
        filter.addAction(ServiceReceiver.RESUME);
        filter.addAction(ServiceReceiver.PREV);
        filter.addAction(ServiceReceiver.LOOPING);
        filter.addAction(ServiceReceiver.APEEND_LIST_SONG);
        filter.addAction(ServiceReceiver.DELETE_ONE_FROM_LIST_SONG);
        filter.addAction(ServiceReceiver.DELETE_ALL_FROM_LIST_SONG);
        // from fragment
        filter.addAction(FragmentBroadcast.ADD_SONG_NOW_PLAYING);
        filter.addAction(FragmentBroadcast.ADD_ALL_SONG_NOW_PLAYING);
        filter.addAction(FragmentBroadcast.DELETE_SONG_IN_DB);
        //from system
        filter.addAction(FragmentBroadcast.PLAYLIST_CHANGED);
        registerReceiver(clientReceiver, filter);

        playerService = new Intent(this, PlayerService.class);
        bindService(playerService, serviceConnection, Context.BIND_AUTO_CREATE);

        //lưu setting vào trong bộ nhớ

        headphoneOut = getString(R.string.pref_key_Headset_Out);
        headphoneIn = getString(R.string.pref_key_Headset_In);
        language = getString(R.string.pref_key_language_select);

        pref = getSharedPreferences("appPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        //lấy  lại setting trong bộ nhớ
        isHeadphoneOut = pref.getBoolean(headphoneOut, false);
        isHeadphoneIn = pref.getBoolean(headphoneIn, false);
        languageValue = pref.getString(language, getResources().getStringArray(R.array.pref_language_select_values)[0]);
        //regist setting change trigger
        pref.registerOnSharedPreferenceChangeListener(this);
        //view stub 4 music tab
        numMusicTab = this.getResources().getStringArray(R.array.music_tab_title).length;
        musictabViewStub = (ViewStub) findViewById(R.id.musicTabViewStub);
        if (musictabViewStub != null) {
            musictabViewStub.setLayoutResource(R.layout.tab_container);
            musictabViewStub.inflate();
        }
        //player slider
        slidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        slidingUpPanelLayout.addPanelSlideListener(this);
        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        slidingUpPanelLayout.buildLayer();
        slidingUpPanelLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                setPlayerButtonListenter();
            }
        });
        //setPlayerButtonListenter();
        // player 3 view page
        if (playerViewPager == null) {
            playerViewPager = findViewById(R.id.player_pager_center);
            playerViewPager.setOffscreenPageLimit(3);
            playerViewPagerAdapter = new PlayerViewPagerAdapter(getSupportFragmentManager(), 3, this);
            playerViewPager.setAdapter(playerViewPagerAdapter);
            playerViewPager.setCurrentItem(1);
            tabLayout = findViewById(R.id.player_tab_layout);
            tabLayout.setupWithViewPager(playerViewPager);
        }

        listSong = new ArrayList<>();
        playerLayout = (RelativeLayout) findViewById(R.id.player);
        tvTitleSong = (TextView) findViewById(R.id.player_title_song);
        tvTitleArtist = (TextView) findViewById(R.id.player_title_artist);
        tvCurDuration = (TextView) findViewById(R.id.player_current_duration);
        tvDuration = (TextView) findViewById(R.id.player_max_duration);
        sbSeeker = (SeekBar) findViewById(R.id.player_seeker);
        player_icon_center = (ImageView) playerViewPager.findViewById(R.id.player_icon_center);
        checkPermission();
    }

    //bind service
    public ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PlayerService.binderClass binder = (PlayerService.binderClass) service;
            mService = binder.getService();
            binded = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onStop() {
//        Toast.makeText(this, "onStop", Toast.LENGTH_SHORT).show();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
//        Toast.makeText(this, "onDestroy", Toast.LENGTH_SHORT).show();
        notification.turnOffNoti();
        try {
            unbindService(serviceConnection);
            unregisterReceiver(clientReceiver);
        } catch (Exception e) {
        }
        if (permissionAlertDialog != null) permissionAlertDialog.dismiss();
        super.onDestroy();
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
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (slidingUpPanelLayout.hasWindowFocus() && slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }

    //preference setting changed event
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
//        Toast.makeText(this, "Setting changed", Toast.LENGTH_SHORT).show();
        //key == key trong xml
        if (key.equalsIgnoreCase(headphoneOut)) {
            this.isHeadphoneOut = sharedPreferences.getBoolean(headphoneOut, false);
        }
        if (key.equalsIgnoreCase(headphoneIn)) {
            this.isHeadphoneIn = sharedPreferences.getBoolean(headphoneIn, false);
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
        if (id == R.id.share) {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = getString(R.string.share_body);
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Download");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
        }
        if (id == R.id.update) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Update");
            builder.setMessage("App is up-to-date");
            builder.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .create()
                    .show();
        }
        if(id == R.id.aboutus){
            Intent i = new Intent(this, AboutUsActivity.class);
            startActivity(i);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    //ham hiển thị phần offline music
    private void onOffMusicSelected() {
        toolbar.setTitle("Music");
        if (musictabViewStub != null) {
            musictabViewStub.setVisibility(View.VISIBLE);
        }
        //add view pager vao content_main
        if (viewPager == null) {
            tabLayout = findViewById(R.id.tabLayout);
            viewPager = findViewById(R.id.pager);
            viewPager.setOffscreenPageLimit(numMusicTab - 1);
            viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), numMusicTab, this);
            viewPager.setAdapter(viewPagerAdapter);
            tabLayout.setupWithViewPager(viewPager);
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
//        Toast.makeText(this, "Sliding", Toast.LENGTH_SHORT).show();
//        setPlayerButtonListenter();
    }

    public void setPlayerButtonListenter() {
        //close button
        ImageView btnClose = (ImageView) findViewById(R.id.player_title_close_button);
        ImageView btnPlay = (ImageView) findViewById(R.id.player_btn_play);
        ImageView btnNext = (ImageView) findViewById(R.id.player_btn_next);
        ImageView btnPrev = (ImageView) findViewById(R.id.player_btn_prev);
        ImageView btnShuffle = (ImageView) findViewById(R.id.player_btn_shuffle);
        ImageView btnLoop = (ImageView) findViewById(R.id.player_btn_repeat);
        //set scrollable
        slidingUpPanelLayout.setNestedScrollingEnabled(true);
//        slidingUpPanelLayout.setScrollableView(v2);
        if (btnClose != null)
            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestStop();
                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                }
            });
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlayState) {
                    requestPause();
                } else {
                    requestResume();
                }
            }
        });
        btnLoop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ++loopMode;
                if (loopMode > PlayerService.loopMode_ALL) {
                    loopMode = PlayerService.loopMode_NO;
                }
                requestLooping(loopMode);
            }
        });
        btnShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isShuffle = !isShuffle;
                requestShuffe(isShuffle);
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestNext();
            }
        });
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPrev();
            }
        });
        sbSeeker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    String currentPositionStr = Utils.millisecondsToString(progress);
                    tvCurDuration.setText(currentPositionStr);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isSeekBarSeeking = true;
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                requestSeek(seekBar.getProgress());
                isSeekBarSeeking = false;
            }
        });
    }

    @Override
    public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
        if (newState == SlidingUpPanelLayout.PanelState.HIDDEN) {
            return;
        }
        if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
            ImageView btnClose = (ImageView) findViewById(R.id.player_title_close_button);
            ImageView icon =(ImageView)findViewById(R.id.player_icon_center);
            btnClose.setImageDrawable(icon.getDrawable());
            btnClose.setVisibility(View.VISIBLE);
        }
        if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
            ImageView btnClose = (ImageView) findViewById(R.id.player_title_close_button);
            btnClose.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
            btnClose.setImageDrawable(getDrawable(android.R.drawable.ic_menu_close_clear_cancel));
            btnClose.setVisibility(View.VISIBLE);
        }
    }
//change color player controller button
    private void changeCtrlBtnColor(ImageView btn,int colorId){
        PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(getResources().getColor(colorId),
                PorterDuff.Mode.SRC_ATOP);
        btn.setColorFilter(porterDuffColorFilter);
    }
    // nhan du lieu tu MusicTabFragment
    @Override
    public void playThisSong(Song song) {
        requestSongDeleteAll();
        requestAppendList(song);
        requestPlay(song.getId());
    }

    @Override
    public void updateListSongDB(Song newSong) {
        this.listSongDB.add(newSong);
    }


//        double durationMinute = duration / 60000.0;
//        int _durationSecond = ((int) ((durationMinute - (int) durationMinute) * 60));
//        String durationSecond = String.valueOf(_durationSecond);
//        durationSecond = durationSecond.length() == 1 ? "0" + durationSecond : durationSecond;
//        return (int) durationMinute + ":" + durationSecond;

    public ArrayList<Song> getListSong() {
        return listSong;
    }

//====================================== Function quản lí trình chơi nhạc====================================//

    //=======ham nhan tu service=========//
    public void responsePlay(Song song) {
        isPlayState = true;
        tvTitleSong.setText(song.getTitle());
        tvTitleArtist.setText(song.getArtist());
        tvCurDuration.setText("0:00");
        tvDuration.setText(Utils.millisecondsToString(song.getDuration()));
        sbSeeker.setMax(song.getDuration());
        sbSeeker.setProgress(0);
        ImageView btnPlay = (ImageView) findViewById(R.id.player_btn_play);
        btnPlay.setImageResource(R.drawable.ic_pause);
        System.out.println(song.getPicture() == null);
        //update UI player
        playerViewPagerAdapter.updateFirstFragment(song, ServiceReceiver.PLAY);
        playerViewPagerAdapter.updateSecondFragment(song);
        playerViewPagerAdapter.updateThirdFragment(song);
        //update UI notification
        notification.updateNotification(this, song, true);
        //update small icon player

        slidingUpPanelLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int width = slidingUpPanelLayout.getWidth();
                int height = slidingUpPanelLayout.getHeight();
                if (width > 0 && height > 0) {
                    slidingUpPanelLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    if (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                        ImageView btnClose = (ImageView) findViewById(R.id.player_title_close_button);
                        ImageView icon =(ImageView)findViewById(R.id.player_icon_center);
                        btnClose.setImageDrawable(icon.getDrawable());
                        btnClose.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        System.gc();
    }

    public void updateProgress(int currentPosition) {
        if (!isSeekBarSeeking && isPlayState) {
            String currentPositionStr = Utils.millisecondsToString(currentPosition);
            tvCurDuration.setText(currentPositionStr);
            sbSeeker.setProgress(currentPosition);
        }
    }

    public void responseStop() {
        isPlayState = false;
        notification.turnOffNoti();
        try {
            ((PlayerTabFragment) playerViewPagerAdapter.getTabFragment()[1]).setData(null, true);
            ImageView btnPlay = (ImageView) findViewById(R.id.player_btn_play);
            btnPlay.setImageResource(R.drawable.ic_play_button);
            sbSeeker.setProgress(0);
        } catch (NullPointerException e) {

        }
    }

    public void responseNext() {
        isPlayState = true;
    }

    public void responseLoop() {
        ImageView btnRepeat = (ImageView) findViewById(R.id.player_btn_repeat);
        switch (loopMode) {
            case PlayerService.loopMode_NO: {
                btnRepeat.setImageResource(R.drawable.ic_repeat);
                changeCtrlBtnColor(btnRepeat,R.color.white);
                break;
            }
            case PlayerService.loopMode_SINGLE: {
                btnRepeat.setImageResource(R.drawable.ic_repeat_single);
                changeCtrlBtnColor(btnRepeat,R.color.colorAccent);
                break;
            }
            case PlayerService.loopMode_ALL: {
                btnRepeat.setImageResource(R.drawable.ic_repeat);
                changeCtrlBtnColor(btnRepeat,R.color.colorAccent);
                break;
            }
        }
    }

    public void responsePrev() {
        isPlayState = true;
    }

    public void responsePause() {
        isPlayState = false;
        notification.updateNotification(this, null, false);
        try {
            ((PlayerTabFragment) playerViewPagerAdapter.getTabFragment()[1]).setData(null, true);
            ImageView btnPlay = (ImageView) findViewById(R.id.player_btn_play);
            btnPlay.setImageResource(R.drawable.ic_play_button);
        } catch (NullPointerException e) {

        }
    }

    public void responseAppendList(int songId) {
        new AsyncTask<Integer, Song, Song>() {
            @Override
            protected Song doInBackground(Integer ... params) {
                for (Song s : listSongDB) {
                    if (s.getId() == params[0]) {
                        listSong.add(s);
                        publishProgress(s);
                        break;
                    }
                }
                return null;
            }
            @Override
            protected void onProgressUpdate(Song... values) {
                try {
                    ((PlayerPlaylistTabFragment) playerViewPagerAdapter.getTabFragment()[0]).setData(values[0], ServiceReceiver.APEEND_LIST_SONG);
                }catch (Exception e){
                    e.printStackTrace();
                }
                super.onProgressUpdate(values);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,songId);

    }

    public void responseShuffle(boolean isShuffle) {
        ImageView btnShuffle = (ImageView) findViewById(R.id.player_btn_shuffle);
        if (isShuffle) {
            changeCtrlBtnColor(btnShuffle,R.color.colorAccent);
        } else {
            changeCtrlBtnColor(btnShuffle,R.color.white);
        }
    }

    public void responseDeleteOneFromList(Song song) {
        try {
            ((PlayerPlaylistTabFragment) playerViewPagerAdapter.getTabFragment()[0]).setData(song, ServiceReceiver.DELETE_ONE_FROM_LIST_SONG);
        } catch (NullPointerException e) {
        }
        listSong.remove(song);
    }

    public void responseUpdatePlaylistTab() {
        try {
            viewPagerAdapter.updateSingleFragment(3);
        } catch (NullPointerException e) {
        }
    }

    public void responseDeleteAllFromList() {

    }

    public void responseResume() {
        isPlayState = true;
        ImageView btnPlay = (ImageView) findViewById(R.id.player_btn_play);
        btnPlay.setImageResource(R.drawable.ic_pause);
        notification.updateNotification(this, null, true);
        try {
            ((PlayerTabFragment) playerViewPagerAdapter.getTabFragment()[1]).setData(null, false);
        } catch (NullPointerException e) {

        }
    }


    //++++++ham gui den service ++++++++//
    public void requestPlay(int songId) {
        if (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.HIDDEN) {
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            notification.showNotification(this);
        }
        clientReceiver.send(ClientReceiver.PLAY, songId);
    }

    public void requestStop() {
        clientReceiver.send(ClientReceiver.STOP);
    }

    public void requestShuffe(boolean isShuffle) {
        clientReceiver.send(ClientReceiver.SHUFFLE, isShuffle);
    }

    public void requestPause() {
        clientReceiver.send(ClientReceiver.PAUSE);
    }

    public void requestResume() {
        clientReceiver.send(ClientReceiver.RESUME);
    }

    public void requestNext() {
        clientReceiver.send(ClientReceiver.NEXT);
    }

    public void requestLooping(int loopMode) {
        clientReceiver.send(ClientReceiver.LOOPING, loopMode);
    }

    public void requestPrev() {
        clientReceiver.send(ClientReceiver.PREV);
    }

    public void requestAppendList(Song song) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(ClientReceiver.APEEND_LIST_SONG, song);
        clientReceiver.send(ClientReceiver.APEEND_LIST_SONG, bundle);
    }

    public void requestAppendListAndPlay(ArrayList<Song> listSong) {
        new AsyncTask<ArrayList<Song>, Song, Song>() {
            @Override
            protected Song doInBackground(ArrayList<Song> ... params) {
                for (int i = 0;i< params[0].size();i++) {
                    Song s =params[0].get(i);
                    requestAppendList(s);
                    if(i==0){
                        publishProgress(s);
                    }
                }

                return null;
            }
            @Override
            protected void onProgressUpdate(Song... values) {
                requestPlay(values[0].getId());
                super.onProgressUpdate(values);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,listSong);
    }
    public void requestSeek(int position) {
        isPlayState = true;
        Bundle bundle = new Bundle();
        bundle.putInt(ClientReceiver.SEEK, position);
        clientReceiver.send(ClientReceiver.SEEK, bundle);
    }

    public void requestSongDelete(Song song) {
        clientReceiver.send(ClientReceiver.DELETE_ONE_FROM_LIST_SONG, song.getId());
    }

    public void requestSongDeleteAll() {
        listSong.clear();
        try {
            ((PlayerPlaylistTabFragment) playerViewPagerAdapter.getTabFragment()[0]).setData(null, ServiceReceiver.DELETE_ALL_FROM_LIST_SONG);
        } catch (NullPointerException e) {
        }
        clientReceiver.send(ClientReceiver.DELETE_ALL_FROM_LIST_SONG);
        //restore UI
        tvTitleSong.setText("");
        tvTitleArtist.setText("");
        tvCurDuration.setText("0:00");
        tvDuration.setText("0:00");
        sbSeeker.setMax(0);
        sbSeeker.setProgress(0);
        ImageView btnPlay = (ImageView) findViewById(R.id.player_btn_play);
        btnPlay.setImageResource(R.drawable.ic_play_button);
    }

    //============ han nhan tu fragment ===========//
    public void songDeletedInDB(Song song) {
        requestSongDelete(song);
        listSongDB.remove(song);
        viewPagerAdapter.updateFragmentExcept(viewPager.getCurrentItem());
    }
}
