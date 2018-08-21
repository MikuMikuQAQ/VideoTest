package com.demo.videotest;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import com.demo.videotest.widget.MediaController;
import com.demo.videotest.widget.PlayConfigView;
import com.google.gson.Gson;
import com.pili.pldroid.player.PLOnInfoListener;
import com.pili.pldroid.player.widget.PLVideoTextureView;
import master.flame.danmaku.controller.DrawHandler;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.ui.widget.DanmakuTextureView;
import master.flame.danmaku.ui.widget.DanmakuView;

import java.util.Random;

import static com.demo.videotest.utils.Config.LIVE_TEST_URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener, PLOnInfoListener {

    private DanmakuView danmakuView;

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

    private boolean status = false;

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
        //初始化播放界面
        player = (PLVideoTextureView) findViewById(R.id.video_texture_view);
        //初始化播放控制器
        mediaController = findViewById(R.id.media_controller);
        player.setMediaController(mediaController);
        //设置播放地址
        player.setVideoURI(Uri.parse(LIVE_TEST_URL));
        //设置视频缓冲动画
        loading = findViewById(R.id.loading_view);
        player.setBufferingIndicator(loading);
        //铺满全屏
        player.setDisplayAspectRatio(PLVideoTextureView.ASPECT_RATIO_PAVED_PARENT);
        //设置视频播放监听
        player.setOnInfoListener(this);

        frameLayout = (FrameLayout) findViewById(R.id.video_player_frameLayout);

        fullScreenImage = findViewById(R.id.full_screen_image);
        fullScreenImage.setOnClickListener(this);

        coverImage = findViewById(R.id.cover_image);
        coverImage.setOnClickListener(this);

        stopPlayImage = findViewById(R.id.cover_stop_play);

        landscape_ll = findViewById(R.id.landscape_ll);

        backButton = findViewById(R.id.back_image_btn);
        backButton.setOnClickListener(this);

        moreBtn = findViewById(R.id.more_image_btn);
        moreBtn.setOnClickListener(this);

        configView = findViewById(R.id.config_view);
        configView.setOnTouchListener(this);

        danmakuView = findViewById(R.id.danmaku_view);
        danmakuView.enableDanmakuDrawingCache(true);
        danmakuView.setCallback(new DrawHandler.Callback() {
            @Override
            public void prepared() {
                /*showDanmakuStatus = true;
                danmakuView.start();
                Log.e("TAG", "prepared: " );
                randomDanmakuText();*/
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
        danmakuContext = DanmakuContext.create();
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
        player.stopPlayback();
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
                //判断视频是否在播放
                if (!status) {
                    status = true;
                    danmakuView.start();
                    startCurVideoView();
                    randomDanmakuText();
                    Log.e("TAG", "onClick: " + status);
                } /*else {
                    status = false;
                    danmakuView.pause();
                    stopCurVideoView();
                    Log.e("TAG", "onClick: " + status);
                }*/

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

    /*
     *
     *
     * 设置播放器参数
     *
     * */
    private void resetConfig() {
        player.setRotation(0);
        player.setMirror(false);
        player.setDisplayAspectRatio(PLVideoTextureView.ASPECT_RATIO_PAVED_PARENT);
    }

    /*
     *
     *
     * 设置播放器暂停时的UI
     *
     * */
    /*public void stopCurVideoView() {
        resetConfig();
        player.stopPlayback();
        loading.setVisibility(View.GONE);
        coverImage.setVisibility(View.VISIBLE);
        stopPlayImage.setVisibility(View.VISIBLE);
        //status = false;
    }*/

    /*
     *
     *
     * 设置播放器播放时的UI
     *
     * */
    public void startCurVideoView() {
        player.start();
        loading.setVisibility(View.VISIBLE);
        stopPlayImage.setVisibility(View.GONE);
        //status = true;
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
        baseDanmaku.textColor = Color.RED;
        baseDanmaku.setTime(danmakuView.getCurrentTime());
        if (userText) {
            baseDanmaku.borderColor = Color.GREEN;
        }
        //Log.e("TAG", "showDanmaku: "+ new Gson().toJson(baseDanmaku));
        danmakuView.addDanmaku(baseDanmaku);
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

    private void randomDanmakuText() {
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
