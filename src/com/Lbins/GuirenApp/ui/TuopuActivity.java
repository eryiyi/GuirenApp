package com.Lbins.GuirenApp.ui;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;
import com.Lbins.GuirenApp.R;
import com.Lbins.GuirenApp.base.BaseActivity;
import com.Lbins.GuirenApp.fragment.TuopuFragmentOne;
import com.Lbins.GuirenApp.fragment.TuopuFragmentThree;
import com.Lbins.GuirenApp.fragment.TuopuFragmentTwo;

/**
 * Created by zhl on 2016/6/13.
 */
public class TuopuActivity extends BaseActivity implements View.OnClickListener {
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fm;

    public static String mm_emp_id;

    private TuopuFragmentOne oneFragment;
    private TuopuFragmentTwo twoFragment;
    private TuopuFragmentThree threeFragment;

    private TextView foot_one;
    private TextView foot_two;
    private TextView foot_three;

    //设置底部图标
    Resources res;

    public TuopuActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tuopu_activity);
        mm_emp_id = getIntent().getExtras().getString("mm_emp_id");//会员id  要查询的
        res = getResources();

        fm = getSupportFragmentManager();

        foot_one = (TextView) this.findViewById(R.id.foot_one);
        foot_two = (TextView) this.findViewById(R.id.foot_two);
        foot_three = (TextView) this.findViewById(R.id.foot_three);
        this.findViewById(R.id.foot_liner_one).setOnClickListener(this);
        this.findViewById(R.id.foot_liner_two).setOnClickListener(this);
        this.findViewById(R.id.foot_liner_three).setOnClickListener(this);

        switchFragment(R.id.foot_liner_one);
    }

    public void switchFragment(int id) {
        fragmentTransaction = fm.beginTransaction();
        hideFragments(fragmentTransaction);
        switch (id) {
            case R.id.foot_liner_one:
                if (oneFragment == null) {
                    oneFragment = new TuopuFragmentOne();
                    fragmentTransaction.add(R.id.content_frame, oneFragment);
                } else {
                    fragmentTransaction.show(oneFragment);
                }
                foot_one.setTextColor(res.getColor(R.color.red));
                foot_two.setTextColor(res.getColor(R.color.textColortwo));
                foot_three.setTextColor(res.getColor(R.color.textColortwo));
                break;
            case R.id.foot_liner_two:
                if (twoFragment == null) {
                    twoFragment = new TuopuFragmentTwo();
                    fragmentTransaction.add(R.id.content_frame, twoFragment);
                } else {
                    fragmentTransaction.show(twoFragment);
                }
                foot_one.setTextColor(res.getColor(R.color.textColortwo));
                foot_two.setTextColor(res.getColor(R.color.red));
                foot_three.setTextColor(res.getColor(R.color.textColortwo));
                break;
            case R.id.foot_liner_three:
                if (threeFragment == null) {
                    threeFragment = new TuopuFragmentThree();
                    fragmentTransaction.add(R.id.content_frame, threeFragment);
                } else {
                    fragmentTransaction.show(threeFragment);
                }
                foot_one.setTextColor(res.getColor(R.color.textColortwo));
                foot_two.setTextColor(res.getColor(R.color.textColortwo));
                foot_three.setTextColor(res.getColor(R.color.red));
                break;


        }
        fragmentTransaction.commit();
    }

    private void hideFragments(FragmentTransaction ft) {
        if (oneFragment != null) {
            ft.hide(oneFragment);
        }
        if (twoFragment != null) {
            ft.hide(twoFragment);
        }
        if (threeFragment != null) {
            ft.hide(threeFragment);
        }

    }

    boolean isMobileNet, isWifiNet;
    @Override
    public void onClick(View view) {

        switchFragment(view.getId());
    }


    public void back(View view){
        finish();
    }


}
