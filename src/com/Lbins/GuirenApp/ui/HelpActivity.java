package com.Lbins.GuirenApp.ui;

import android.os.Bundle;
import android.view.View;
import com.Lbins.GuirenApp.R;
import com.Lbins.GuirenApp.base.BaseActivity;

/**
 * Created by zhl on 2016/8/15.
 */
public class HelpActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.help_activity);

        this.findViewById(R.id.back).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
        }
    }
}
