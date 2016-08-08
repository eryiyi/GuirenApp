package com.Lbins.GuirenApp.ui;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.Lbins.GuirenApp.R;
import com.Lbins.GuirenApp.base.BaseActivity;

/**
 * Created by zhl on 2016/7/10.
 */
public class AboutUsActivity  extends BaseActivity implements View.OnClickListener{
    private TextView version;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aboutus_activity);

        this.findViewById(R.id.share).setOnClickListener(this);
        this.findViewById(R.id.back).setOnClickListener(this);
        version = (TextView) this.findViewById(R.id.version);
        version.setText(getVersion());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.share:
                //分享
                break;
            case R.id.back:
                finish();
                break;
        }
    }

    public String getVersion() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            String version = info.versionName;
            return  version;
        } catch (Exception e) {
            e.printStackTrace();
            return "暂无版本信息";
        }
    }
}
