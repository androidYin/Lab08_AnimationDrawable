package com.example.android.lab08_animationdrawable;

import android.content.res.TypedArray;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private ImageView m_img_duke;
    private AnimationDrawable m_frame_animation;
    private TextView m_tv_message;

    private View m_view_logo;
    private TextView m_logo_name;
    private TextView m_view_message;
    private Button m_btn_go;
    private SeekBar m_skb_duration;
    private TextView m_tv_duration;

    private TypedArray mNbaLogos;   // 資源檔 陣列
    private int mNbaLogosCount;     // 一共有多少張圖

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initFrameAnimation();
        initNbaLogos();
        initSeekBar();
    }

    private void initNbaLogos() {
        mNbaLogos = getNbaLogos();
        mNbaLogosCount = getNbaLogos().length();
        m_view_logo.setBackground(mNbaLogos.getDrawable(0));
    }

    private TypedArray getNbaLogos() {

        // 取得 nba logo drawables
        TypedArray logos = getResources().obtainTypedArray(R.array.nba_logos);
        return logos;
    }

    private void initView() {
        m_tv_message = (TextView) findViewById(R.id.tv_message);

        m_view_logo = findViewById(R.id.view_logo);
        m_logo_name = (TextView)findViewById(R.id.tv_logo_name);
        m_view_message = (TextView) findViewById(R.id.view_message);

        m_btn_go = (Button)findViewById(R.id.btn_go);

        m_skb_duration = (SeekBar)findViewById(R.id.skb_duration);
        m_tv_duration = (TextView)findViewById(R.id.tv_duration);
    }

    private void initFrameAnimation() {
        m_img_duke = (ImageView) findViewById(R.id.img_duke);
        m_img_duke.setBackgroundResource(R.drawable.frame_animation);
        m_frame_animation = (AnimationDrawable) m_img_duke.getBackground();
    }

    private int mDuration; // 隨機換圖間隔時間

    private void initSeekBar() {
        m_tv_duration.setText(String.valueOf(mDuration)); // 顯示目前所設定的間隔時間
        m_skb_duration.setMax(20);        // SeekBar 可拖曳的最大值，最小值固定為 0 (不可改)

        // 設定 當 SeekBar被操作時要執行什麼
        m_skb_duration.setOnSeekBarChangeListener(new 屠龍刀());
    }


    private class 屠龍刀 implements SeekBar.OnSeekBarChangeListener {

            // 拖曳 SeekBar
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // progress 代表目前拖曳中所代表的值
                int duration = progress*50; // max = 20 , 20 * 50 = 1000
                m_tv_duration.setText(String.valueOf(duration)); // 更新目前顯示的間隔時間
                mDuration = duration;

            }

            // 按下 SeekBar
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
//                m_tv_duration.setText("start");
            }

            // 放開 SeekBar
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
//                m_tv_duration.setText("end");
            }
        }



    // 按下 Button
    public void click(View view) {
        switch(view.getId()) {
            case R.id.btn_start:
                m_frame_animation.start();
                break;
            case R.id.btn_stop:
                m_frame_animation.stop();
                break;
            case R.id.btn_5_secs:
                animation5secs();
                break;
        }
    }

    // Handler 處理代辦事項
    private Handler m_Handler = new Handler();

    private void animation5secs() {
        int delayMillis = 5 * 1000; // 5秒
        // 建立工作
        Runnable task = new Task();
        // 交付工作，並在指定時間過後才執行
        boolean result = m_Handler.postDelayed(task, delayMillis);
        //
        m_tv_message.setText(result ? "交付成功" : "交付失敗");
        m_frame_animation.start(); // 開始動畫
    }



    // 將來被執行的工作 (內部類別，存在於外部類別實體當中，像 引擎 存在於 車子 實體)
    private class Task implements Runnable {
        @Override
        public void run() { // 工作內容
            // 可直接存取 外部類別實體的成員 (包含 private 成員)
            m_frame_animation.stop();           // 結束動畫
            m_tv_message.setText("時間到");

        }
    }

    // 按下 go
    public void go(View view) {
        m_Handler.post(mStartRandomTask);               // 立即執行任務 隨機換圖
        m_Handler.postDelayed(mStopRandomTask, 20_000);   //  20 秒後執行任務 停止隨機換圖
        m_btn_go.setEnabled(false);                     // go 按鈕不可按
    }

    // 建立任務物件
    private StartRandomTask mStartRandomTask = new StartRandomTask();
    private StopRandomTask mStopRandomTask = new StopRandomTask();

    // 任務 開始 隨機換圖
    private class StartRandomTask implements Runnable {
        @Override
        public void run() {
            // 隨機產生 0 ~ 圖數量-1
            int index = (int)(Math.random() * mNbaLogosCount);
            // 換圖
            m_view_logo.setBackground(mNbaLogos.getDrawable(index));
            // mDuration 秒後再執行一次本任務
            m_Handler.postDelayed(this, mDuration);
        }
    }

    // 任務 停止 隨機宦途
    private class StopRandomTask implements Runnable {
        @Override
        public void run() {
            // 取消任務 StartRandomTask
            m_Handler.removeCallbacks(mStartRandomTask);
            // go 按鈕恢復可按
            m_btn_go.setEnabled(true);
        }
    }

}
