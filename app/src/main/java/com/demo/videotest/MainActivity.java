package com.demo.videotest;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;
import com.demo.videotest.widget.MediaController;
import com.demo.videotest.widget.PlayConfigView;
import com.pili.pldroid.player.PLOnInfoListener;
import com.pili.pldroid.player.widget.PLVideoTextureView;

import static com.demo.videotest.utils.Config.LIVE_TEST_URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private FrameLayout frameLayout;

    private PlayConfigView configView;

    private PLVideoTextureView player;

    private ImageButton fullScreenImage;

    private MediaController mediaController;

    private ImageView coverImage;

    private ImageButton stopPlayImage;

    private View loading;

    private View fullView;

    private boolean status = false;

    private boolean fullScreenStatus = false;

    private DisplayMetrics dm;

    private RelativeLayout.LayoutParams params;

    private int width;

    private int height;

    private LinearLayout landscape_ll;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fullView = getWindow().getDecorView();
        //初始化播放界面
        player = (PLVideoTextureView) findViewById(R.id.video_texture_view);
        //初始化播放控制器
        mediaController = findViewById(R.id.media_controller);
        player.setMediaController(mediaController );
        //设置播放地址
        player.setVideoURI(Uri.parse(LIVE_TEST_URL));
        //设置视频缓冲动画
        loading = findViewById(R.id.loading_view);
        player.setBufferingIndicator(loading);
        //铺满全屏
        player.setDisplayAspectRatio(PLVideoTextureView.ASPECT_RATIO_PAVED_PARENT);
        //设置视频播放监听
        player.setOnInfoListener(new PLOnInfoListener() {
            @Override
            public void onInfo(int i, int i1) {
                if (i == PLOnInfoListener.MEDIA_INFO_VIDEO_RENDERING_START) {
                    coverImage.setVisibility(View.GONE);
                    stopPlayImage.setVisibility(View.GONE);
                    mediaController.hide();
                }
            }
        });

        frameLayout = (FrameLayout) findViewById(R.id.video_player_frameLayout);

        fullScreenImage = findViewById(R.id.full_screen_image);
        fullScreenImage.setOnClickListener(this);

        coverImage = findViewById(R.id.cover_image);
        coverImage.setOnClickListener(this);

        stopPlayImage = findViewById(R.id.cover_stop_play);

        landscape_ll = findViewById(R.id.landscape_ll);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //程序关闭后自动停止播放
        player.stopPlayback();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.full_screen_image:
                Log.e("TAG", "onClick: ");
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
                if (status == false) {
                    startCurVideoView();
                } else {
                    stopCurVideoView();
                }
                break;
            default:
                break;
        }
    }

    private void resetConfig() {
        player.setRotation(0);
        player.setMirror(false);
        player.setDisplayAspectRatio(PLVideoTextureView.ASPECT_RATIO_PAVED_PARENT);
    }

    public void stopCurVideoView() {
        resetConfig();
        player.stopPlayback();
        loading.setVisibility(View.GONE);
        coverImage.setVisibility(View.VISIBLE);
        stopPlayImage.setVisibility(View.VISIBLE);
        status = false;
    }

    public void startCurVideoView() {
        player.start();
        loading.setVisibility(View.VISIBLE);
        stopPlayImage.setVisibility(View.GONE);
        status = true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        switch (newConfig.orientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                landscape_ll.setVisibility(View.GONE);
                fullView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                getSupportActionBar().show();
                dm = getResources().getDisplayMetrics();
                width = dm.widthPixels;
                height = dm.heightPixels;
                params = (RelativeLayout.LayoutParams) frameLayout.getLayoutParams();
                params.width = width;
                params.height = (int) (300 * dm.density + 0.5f);
                frameLayout.setLayoutParams(params);
                break;
            case Configuration.ORIENTATION_LANDSCAPE:
                landscape_ll.setVisibility(View.VISIBLE);
                getSupportActionBar().hide();
                fullView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
                dm = getResources().getDisplayMetrics();
                width = dm.widthPixels;
                height = dm.heightPixels;
                params = (RelativeLayout.LayoutParams) frameLayout.getLayoutParams();
                params.width = width;
                params.height = height;
                frameLayout.setLayoutParams(params);
                break;
            default:
                break;
        }
    }
}
