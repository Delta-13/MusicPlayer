package com.example.shaoyangyang.myplayer;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.shaoyangyang.myplayer.adapter.MusicPagerAdapter;
import com.example.shaoyangyang.myplayer.fragment.LogicFragment;
import com.example.shaoyangyang.myplayer.fragment.OnlineFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

//实现OnClickListener的接口
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    //定义activity_main.xml的控件对象
    private TextView logicTv;
    private TextView onlineTv;
    private ViewPager viewPager;
    private ImageView menuImagv;
    private ImageView seachImagv;
    private Calendar calendar;
    //将Fragment放入List集合中，存放fragment对象
    private List<Fragment> fragmentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //绑定id
        bindingID();
        //设置监听
        listener();
        //创建fragment对象
        LogicFragment logicFragment = new LogicFragment();
        OnlineFragment onlineFragment = new OnlineFragment();
        //将fragment对象添加到fragmentList中
        fragmentList.add(logicFragment);
        fragmentList.add(onlineFragment);
        //通过MusicPagerAdapter类创建musicPagerAdapter的适配器，下面我将添加MusicPagerAdapter类的创建方法
        MusicPagerAdapter musicPagerAdapter = new MusicPagerAdapter(getSupportFragmentManager(), fragmentList);
        //viewPager绑定适配器
        viewPager.setAdapter(musicPagerAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        logicTv.setTextColor(getResources().getColor(R.color.white));
                        onlineTv.setTextColor(getResources().getColor(R.color.white_60P));
                        break;
                    case 1:
                        onlineTv.setTextColor(getResources().getColor(R.color.white));
                        logicTv.setTextColor(getResources().getColor(R.color.white_60P));
                        break;
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }

    private void listener() {
        logicTv.setOnClickListener(this);
        onlineTv.setOnClickListener(this);
        menuImagv.setOnClickListener(this);
        seachImagv.setOnClickListener(this);
    }

    private void bindingID() {
        logicTv = findViewById(R.id.main_logic_tv);
        onlineTv = findViewById(R.id.main_online_tv);
        viewPager = findViewById(R.id.main_vp);
        menuImagv = findViewById(R.id.main_menu_imgv);
        seachImagv = findViewById(R.id.main_search_imgv);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_logic_tv:
                //实现点击TextView切换fragment
                viewPager.setCurrentItem(0);
                break;
            case R.id.main_online_tv:
                viewPager.setCurrentItem(1);
                break;
            default:
                break;
        }

    }


}

