package com.Lbins.GuirenApp.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.Lbins.GuirenApp.R;
import com.Lbins.GuirenApp.base.BaseActivity;
import com.Lbins.GuirenApp.module.Notice;
import com.Lbins.GuirenApp.util.Constants;

/**
 * author: ${zhanghailong}
 * Date: 2015/2/6
 * Time: 22:01
 * 类的功能、说明写在此处.
 */
public class NoticeDetailActivity extends BaseActivity implements View.OnClickListener {
    private Notice notice;
    private TextView title;
    private TextView content;
    private TextView dateline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notice_detail);
        initView();
        notice = (Notice) getIntent().getExtras().get(Constants.NOTICE);
        title.setText(notice.getMm_notice_title());
        content.setText(notice.getMm_notice_content());
        dateline.setText(notice.getDateline());
    }



    private void initView() {
        this.findViewById(R.id.back).setOnClickListener(this);
        title = (TextView) this.findViewById(R.id.title);
        content = (TextView) this.findViewById(R.id.content);
        dateline = (TextView) this.findViewById(R.id.dateline);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }
}
