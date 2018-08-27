package com.demo.videotest;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import com.demo.videotest.widget.MediaController;
import com.demo.videotest.widget.PlayConfigView;
import com.pili.pldroid.player.PLOnInfoListener;
import com.pili.pldroid.player.widget.PLVideoTextureView;
import master.flame.danmaku.controller.DrawHandler;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.ui.widget.DanmakuView;

import java.util.HashMap;
import java.util.Random;

import static com.demo.videotest.utils.Config.LIVE_TEST_URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener, PLOnInfoListener, View.OnFocusChangeListener {

    public DanmakuView danmakuView;

    private EditText danmakuEdit;

    private ImageButton danmakuSwitch;

    private DanmakuContext danmakuContext;

    private FrameLayout frameLayout;

    private PlayConfigView configView;

    private PLVideoTextureView player;

    private ImageButton fullScreenImage;

    private ImageButton backButton;

    private MediaController mediaController;

    private ImageView coverImage;

    private ImageButton stopPlayImage;

    private ImageButton moreBtn;

    private View loading;

    private View fullView;

    public boolean status = false;

    private boolean fullScreenStatus = false;

    private DisplayMetrics dm;

    private RelativeLayout.LayoutParams params;

    private int width;

    private int height;

    private final int PX_TO_SP = 0;

    private final int PX_TO_DP = 1;

    private LinearLayout landscape_ll;

    private BaseDanmakuParser parser = new BaseDanmakuParser() {
        @Override
        protected IDanmakus parse() {
            return new Danmakus();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getDisplayInfo();
        fullView = getWindow().getDecorView();

        initCreateView();

        //初始化播放控制器
        player.setMediaController(mediaController);
        //设置播放地址
        player.setVideoURI(Uri.parse(LIVE_TEST_URL));
        //设置视频缓冲动画
        player.setBufferingIndicator(loading);
        //铺满全屏
        player.setDisplayAspectRatio(PLVideoTextureView.ASPECT_RATIO_PAVED_PARENT);

        initCreateListener();

        danmakuView.setCallback(new DrawHandler.Callback() {
            @Override
            public void prepared() {

            }

            @Override
            public void updateTimer(DanmakuTimer timer) {

            }

            @Override
            public void danmakuShown(BaseDanmaku danmaku) {

            }

            @Override
            public void drawingFinished() {

            }
        });
        setDanmakuStyle();
        danmakuView.prepare(parser, danmakuContext);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //程序关闭后自动停止播放
        player.stopPlayback();
        status = false;
        if (danmakuView != null) {
            danmakuView.release();
            danmakuView = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (danmakuView != null && danmakuView.isPrepared()) {
            danmakuView.pause();
        }
        if (player.isPlaying()) {
            player.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (danmakuView != null && danmakuView.isPrepared() && danmakuView.isPaused()) {
            danmakuView.resume();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (player.isPlaying()) {
            player.stopPlayback();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.full_screen_image:
                if (fullScreenStatus == false) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    fullScreenStatus = true;
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    fullScreenStatus = false;
                }
                break;
            case R.id.cover_image:
                status = true;
                startCurVideoView();
                randomDanmakuText();
                break;
            case R.id.back_image_btn:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                fullScreenStatus = false;
                break;
            case R.id.more_image_btn:
                if (configView.getVisibility() == View.GONE) {
                    configView.setVisibility(View.VISIBLE);
                } else {
                    configView.setVisibility(View.GONE);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onInfo(int i, int i1) {
        if (i == PLOnInfoListener.MEDIA_INFO_VIDEO_RENDERING_START) {
            coverImage.setVisibility(View.GONE);
            stopPlayImage.setVisibility(View.GONE);
            mediaController.hide();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.config_view:
                return true;
            default:
                break;
        }
        return false;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        switch (newConfig.orientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                setPortrait();
                break;
            case Configuration.ORIENTATION_LANDSCAPE:
                setLandscape();
                break;
            default:
                break;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.danmaku_edit:
                if (hasFocus) {
                    mediaController.mPlayer.pause();
                } else {
                    mediaController.mPlayer.start();
                }
                break;
            default:
                break;
        }
    }

    private void initCreateView(){
        //初始化播放界面
        player = findViewById(R.id.video_texture_view);
        mediaController = findViewById(R.id.media_controller);
        loading = findViewById(R.id.loading_view);
        fullScreenImage = findViewById(R.id.full_screen_image);
        frameLayout = findViewById(R.id.video_player_frameLayout);
        coverImage = findViewById(R.id.cover_image);
        stopPlayImage = findViewById(R.id.cover_stop_play);
        landscape_ll = findViewById(R.id.landscape_ll);
        backButton = findViewById(R.id.back_image_btn);
        moreBtn = findViewById(R.id.more_image_btn);
        configView = findViewById(R.id.config_view);
        danmakuView = findViewById(R.id.danmaku_view);
        danmakuEdit = findViewById(R.id.danmaku_edit);
        danmakuSwitch = findViewById(R.id.danmaku_switch);
    }

    private void initCreateListener(){
        player.setOnInfoListener(this);
        fullScreenImage.setOnClickListener(this);
        coverImage.setOnClickListener(this);
        backButton.setOnClickListener(this);
        moreBtn.setOnClickListener(this);
        configView.setOnTouchListener(this);
        danmakuView.enableDanmakuDrawingCache(true);
        danmakuEdit.setOnFocusChangeListener(this);
        danmakuSwitch.setOnClickListener(this);
    }

    private void setDanmakuStyle() {
        HashMap<Integer, Integer> maxLines = new HashMap<Integer, Integer>();
        maxLines.put(BaseDanmaku.TYPE_SCROLL_RL, 5);

        HashMap<Integer, Boolean> noOverlap = new HashMap<Integer, Boolean>();
        noOverlap.put(BaseDanmaku.TYPE_SCROLL_RL, true);
        noOverlap.put(BaseDanmaku.TYPE_FIX_BOTTOM, true);
        noOverlap.put(BaseDanmaku.TYPE_FIX_TOP, true);
        noOverlap.put(BaseDanmaku.TYPE_SCROLL_LR, true);

        danmakuContext = DanmakuContext.create();
        danmakuContext.setMaximumLines(maxLines)
                .preventOverlapping(noOverlap);
    }

    /*
     *
     *
     * 设置播放器播放时的UI
     *
     * */
    public void startCurVideoView() {
        player.start();
        danmakuView.start();
        loading.setVisibility(View.VISIBLE);
        stopPlayImage.setVisibility(View.GONE);
    }

    /*
     *
     *
     * 设置横屏播放器参数
     *
     * */
    private void setLandscape() {
        landscape_ll.setVisibility(View.VISIBLE);
        getSupportActionBar().hide();
        fullView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        params = (RelativeLayout.LayoutParams) frameLayout.getLayoutParams();
        params.width = height;
        params.height = width;
        frameLayout.setLayoutParams(params);
        configView.setVideoView(player);
    }

    /*
     *
     *
     * 设置竖屏播放器参数
     *
     * */
    private void setPortrait() {
        landscape_ll.setVisibility(View.GONE);
        fullView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        getSupportActionBar().show();
        params = (RelativeLayout.LayoutParams) frameLayout.getLayoutParams();
        params.width = width;
        params.height = changeNum(210, PX_TO_DP);
        frameLayout.setLayoutParams(params);
    }

    private void showDanmaku(String content, boolean userText) {
        BaseDanmaku baseDanmaku = danmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL, danmakuContext);
        baseDanmaku.text = content;
        baseDanmaku.padding = 5;
        baseDanmaku.textSize = changeNum(18, PX_TO_SP);
        baseDanmaku.textColor = randomColor();
        baseDanmaku.isLive = true;
        baseDanmaku.setTime(danmakuView.getCurrentTime());
        if (userText) {
            baseDanmaku.borderColor = Color.GREEN;
        }
        danmakuView.addDanmaku(baseDanmaku);
    }

    private int randomColor() {
        Random random = new Random();
        int r = random.nextInt(256);
        int g = random.nextInt(256);
        int b = random.nextInt(256);
        return Color.rgb(r, g, b);
    }

    private void getDisplayInfo() {
        dm = getResources().getDisplayMetrics();
        width = dm.widthPixels;
        height = dm.heightPixels;
    }

    private int changeNum(float num, int kind) {
        switch (kind) {
            case PX_TO_SP:
                return (int) (num * dm.scaledDensity + 0.5f);
            case PX_TO_DP:
                return (int) (num * dm.density + 0.5f);
            default:
                return 0;
        }
    }

    public void randomDanmakuText() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (status) {
                    //Log.e("TAG", "run: " + status );
                    int time = new Random().nextInt(300);
                    String content = "" + time + time;
                    showDanmaku(content, false);
                    try {
                        Thread.sleep(time);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
