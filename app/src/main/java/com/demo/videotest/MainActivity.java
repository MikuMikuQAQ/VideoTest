package com.demo.videotest;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import com.demo.videotest.widget.MediaController;
import com.pili.pldroid.player.widget.PLVideoView;

public class MainActivity extends AppCompatActivity {

    private PLVideoView player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化播放界面
        player = (PLVideoView) findViewById(R.id.PLVideoView);
        //初始化播放控制器
        MediaController mediaController = new MediaController(this);
        player.setMediaController(mediaController);
        //设置播放地址
        player.setVideoURI(Uri.parse("http://183.251.61.207/PLTV/88888888/224/3221225829/index.m3u8"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //程序关闭后自动停止播放
        if (player.isPlaying()) {
            player.stopPlayback();
        }
    }
}
