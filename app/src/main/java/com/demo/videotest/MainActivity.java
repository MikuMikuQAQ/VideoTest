package com.demo.videotest;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import com.demo.videotest.widget.MediaController;
import com.pili.pldroid.player.PLOnInfoListener;
import com.pili.pldroid.player.widget.PLVideoTextureView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private PLVideoTextureView player;

    private ImageButton fullScreenImage;

    private OnFullScreenListener listener;

    private MediaController mediaController;

    private ImageView coverImage;

    private ImageButton stopPlayImage;

    private View loading;

    private boolean status = false;

    public interface OnFullScreenListener {
        void onFullScreen(PLVideoTextureView videoView, MediaController mediaController);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化播放界面
        player = (PLVideoTextureView) findViewById(R.id.video_texture_view);
        //初始化播放控制器
        mediaController = findViewById(R.id.media_controller);
        player.setMediaController(mediaController);
        //设置播放地址
        player.setVideoURI(Uri.parse("http://183.251.61.207/PLTV/88888888/224/3221225829/index.m3u8"));
        //设置视频缓冲动画
        loading = findViewById(R.id.loading_view);
        player.setBufferingIndicator(loading);
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

        fullScreenImage = findViewById(R.id.full_screen_image);
        fullScreenImage.setOnClickListener(this);

        coverImage = findViewById(R.id.cover_image);
        coverImage.setOnClickListener(this);

        stopPlayImage = findViewById(R.id.cover_stop_play);
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
                //listener.onFullScreen(player,mediaController);
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
}
