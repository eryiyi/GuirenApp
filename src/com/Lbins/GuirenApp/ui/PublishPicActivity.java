package com.Lbins.GuirenApp.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.Lbins.GuirenApp.R;
import com.Lbins.GuirenApp.adapter.Publish_mood_GridView_Adapter;
import com.Lbins.GuirenApp.base.BaseActivity;
import com.Lbins.GuirenApp.base.InternetURL;
import com.Lbins.GuirenApp.data.RecordSingleDATA;
import com.Lbins.GuirenApp.face.ChatEmoji;
import com.Lbins.GuirenApp.face.FaceAdapter;
import com.Lbins.GuirenApp.face.FaceConversionUtil;
import com.Lbins.GuirenApp.face.ViewPagerAdapter;
import com.Lbins.GuirenApp.util.*;
import com.Lbins.GuirenApp.widget.NoScrollGridView;
import com.Lbins.GuirenApp.widget.PublishPopWindow;
import com.Lbins.GuirenApp.widget.SelectPhoTwoPopWindow;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author: ${zhanghailong}
 * Date: 2015/2/4
 * Time: 19:42
 * 类的功能、说明写在此处.
 */
public class PublishPicActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private String typeid = "";
    private final static int SELECT_LOCAL_PHOTO = 110;
    private ImageView publis_pic_back;//返回
    private NoScrollGridView publish_moopd_gridview_image;//图片
    private Publish_mood_GridView_Adapter adapter;
    private TextView publish_pic_run;
    private TextView title;
    private ImageView add_pic;//添加图片按钮

    private PublishPopWindow publishPopWindow;

    private ArrayList<String> dataList = new ArrayList<String>();
    private ArrayList<String> tDataList = new ArrayList<String>();
    private List<String> uploadPaths = new ArrayList<String>();

    private static int REQUEST_CODE = 1;

    private Uri uri;
    private String mm_emp_id = "";//当前登陆者UUID


    private SelectPhoTwoPopWindow deleteWindow;
    AsyncHttpClient client = new AsyncHttpClient();


    //表情
    /**
     * 表情页的监听事件
     */
    private OnCorpusSelectedListener mListener;

    /**
     * 显示表情页的viewpager
     */
    private ViewPager vp_face;

    /**
     * 表情页界面集合
     */
    private ArrayList<View> pageViews;

    /**
     * 游标显示布局
     */
    private LinearLayout layout_point;

    /**
     * 游标点集合
     */
    private ArrayList<ImageView> pointViews;

    /**
     * 表情集合
     */
    private List<List<ChatEmoji>> emojis;

    /**
     * 表情区域
     */
    public View view;
    public ImageButton send_btn_face_normal;
    public ImageButton send_btn_face_enable;
    /**
     * 输入框
     */
    public EditText et_sendmessage;
    public TextView et_sendmessage_count;
    /**
     * 表情数据填充器
     */
    private List<FaceAdapter> faceAdapters;

    /**
     * 当前表情页
     */
    private int current = 0;

    public void setOnCorpusSelectedListener(OnCorpusSelectedListener listener) {
        mListener = listener;
    }

    /**
     * 表情选择监听
     *
     * @author naibo-liao
     * @时间： 2013-1-15下午04:32:54
     */
    public interface OnCorpusSelectedListener {
        void onCorpusSelected(ChatEmoji emoji);
        void onCorpusDeleted();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.publish_pic_xml);
        typeid = getIntent().getExtras().getString(Constants.SELECT_PHOTOORPIIC);
        initView();
        mm_emp_id = getGson().fromJson(getSp().getString("mm_emp_id", ""), String.class);
        if ("1".equals(typeid)) {
            openCamera();
        }else{
            title.setText("发说说");
        }

        emojis = FaceConversionUtil.getInstace().emojiLists;
        Init_View();
        Init_viewPager();
        Init_Point();
        Init_Data();
    }

    private void initView() {
        publis_pic_back = (ImageView) this.findViewById(R.id.publis_pic_back);
        publis_pic_back.setOnClickListener(this);
        publish_pic_run = (TextView) this.findViewById(R.id.publish_pic_run);
        publish_pic_run.setOnClickListener(this);
        title = (TextView) this.findViewById(R.id.title);
        publish_moopd_gridview_image = (NoScrollGridView) this.findViewById(R.id.publish_moopd_gridview_image);
        adapter = new Publish_mood_GridView_Adapter(this, dataList);
        publish_moopd_gridview_image.setAdapter(adapter);
        publish_moopd_gridview_image.setOnItemClickListener(new GridView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String path = dataList.get(position);
                if (path.contains("camera_default") && position == dataList.size() - 1 && dataList.size() - 1 != 9) {
                    showSelectImageDialog();
                } else {
                    Intent intent = new Intent(PublishPicActivity.this, ImageDelActivity.class);
                    intent.putExtra("position", position);
                    intent.putExtra("path", dataList.get(position));
                    startActivityForResult(intent, CommonDefine.DELETE_IMAGE);
                }
            }
        });
        add_pic = (ImageView) this.findViewById(R.id.add_pic);
        add_pic.setOnClickListener(this);
    }


    /**
     * 初始化控件
     */
    private void Init_View() {
        vp_face = (ViewPager) findViewById(R.id.vp_contains);

        et_sendmessage = (EditText) findViewById(R.id.face_content);
//        et_sendmessage.addTextChangedListener(textWatcher);
        et_sendmessage_count = (TextView) findViewById(R.id.count);

        layout_point = (LinearLayout) findViewById(R.id.iv_image);

        send_btn_face_normal = (ImageButton) this.findViewById(R.id.send_btn_face_normal);
        send_btn_face_normal.setOnClickListener(this);
        send_btn_face_enable = (ImageButton) this.findViewById(R.id.send_btn_face_enable);
        send_btn_face_enable.setOnClickListener(this);

        view = findViewById(R.id.ll_facechoose);

        et_sendmessage.setOnClickListener(this);
    }
    private TextWatcher textWatcher = new TextWatcher(){
        private CharSequence temp;//监听前的文本
        private int editStart;//光标开始位置
        private int editEnd;//光标结束位置
        private final int charMaxNum = 500;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            temp = s;
        }

        @Override
        public void afterTextChanged(Editable s) {
            editStart = et_sendmessage.getSelectionStart();
            editEnd = et_sendmessage.getSelectionEnd();
            et_sendmessage.removeTextChangedListener(textWatcher);
            if(temp.length() > charMaxNum) {
                s.delete(editStart - 1, editEnd);
                editStart--;
                editEnd--;
            }
            et_sendmessage.setText(s);
            et_sendmessage.setSelection(editStart);
            et_sendmessage.addTextChangedListener(textWatcher);
            et_sendmessage_count.setText(String.valueOf(charMaxNum - temp.length()));
        }
    };

    /**
     * 初始化显示表情的viewpager
     */
    private void Init_viewPager() {
        pageViews = new ArrayList<View>();
        // 左侧添加空页
        View nullView1 = new View(this);
        // 设置透明背景
        nullView1.setBackgroundColor(Color.TRANSPARENT);
        pageViews.add(nullView1);

        // 中间添加表情页

        faceAdapters = new ArrayList<FaceAdapter>();
        for (int i = 0; i < emojis.size(); i++) {
            GridView view = new GridView(this);
            FaceAdapter adapter = new FaceAdapter(this, emojis.get(i));
            view.setAdapter(adapter);
            faceAdapters.add(adapter);
            view.setOnItemClickListener(this);
            view.setNumColumns(7);
            view.setBackgroundColor(Color.TRANSPARENT);
            view.setHorizontalSpacing(1);
            view.setVerticalSpacing(1);
            view.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
            view.setCacheColorHint(0);
            view.setPadding(5, 0, 5, 0);
            view.setSelector(new ColorDrawable(Color.TRANSPARENT));
            view.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT));
            view.setGravity(Gravity.CENTER);
            pageViews.add(view);
        }

        // 右侧添加空页面
        View nullView2 = new View(this);
        // 设置透明背景
        nullView2.setBackgroundColor(Color.TRANSPARENT);
        pageViews.add(nullView2);
    }

    /**
     * 初始化游标
     */
    private void Init_Point() {

        pointViews = new ArrayList<ImageView>();
        ImageView imageView;
        for (int i = 0; i < pageViews.size(); i++) {
            imageView = new ImageView(this);
            imageView.setBackgroundResource(R.drawable.d1);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    new ViewGroup.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT));
            layoutParams.leftMargin = 10;
            layoutParams.rightMargin = 10;
            layoutParams.width = 8;
            layoutParams.height = 8;
            layout_point.addView(imageView, layoutParams);
            if (i == 0 || i == pageViews.size() - 1) {
                imageView.setVisibility(View.GONE);
            }
            if (i == 1) {
                imageView.setBackgroundResource(R.drawable.d2);
            }
            pointViews.add(imageView);

        }
    }

    /**
     * 填充数据
     */
    private void Init_Data() {
        vp_face.setAdapter(new ViewPagerAdapter(pageViews));

        vp_face.setCurrentItem(1);
        current = 0;
        vp_face.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                current = arg0 - 1;
                // 描绘分页点
                draw_Point(arg0);
                // 如果是第一屏或者是最后一屏禁止滑动，其实这里实现的是如果滑动的是第一屏则跳转至第二屏，如果是最后一屏则跳转到倒数第二屏.
                if (arg0 == pointViews.size() - 1 || arg0 == 0) {
                    if (arg0 == 0) {
                        vp_face.setCurrentItem(arg0 + 1);// 第二屏 会再次实现该回调方法实现跳转.
                        pointViews.get(1).setBackgroundResource(R.drawable.d2);
                    } else {
                        vp_face.setCurrentItem(arg0 - 1);// 倒数第二屏
                        pointViews.get(arg0 - 1).setBackgroundResource(
                                R.drawable.d2);
                    }
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });

    }

    /**
     * 绘制游标背景
     */
    public void draw_Point(int index) {
        for (int i = 1; i < pointViews.size(); i++) {
            if (index == i) {
                pointViews.get(i).setBackgroundResource(R.drawable.d2);
            } else {
                pointViews.get(i).setBackgroundResource(R.drawable.d1);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        ChatEmoji emoji = (ChatEmoji) faceAdapters.get(current).getItem(arg2);
        if (emoji.getId() == R.drawable.face_del_icon) {
            int selection = et_sendmessage.getSelectionStart();
            String text = et_sendmessage.getText().toString();
            if (selection > 0) {
                String text2 = text.substring(selection - 1);
                if ("]".equals(text2)) {
                    int start = text.lastIndexOf("[");
                    int end = selection;
                    et_sendmessage.getText().delete(start, end);
                    return;
                }
                et_sendmessage.getText().delete(selection - 1, selection);
            }
        }
        if (!TextUtils.isEmpty(emoji.getCharacter())) {
            if (mListener != null)
                mListener.onCorpusSelected(emoji);
            SpannableString spannableString = FaceConversionUtil.getInstace()
                    .addFace(this, emoji.getId(), emoji.getCharacter());
            et_sendmessage.append(spannableString);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.publis_pic_back:
                if (!TextUtils.isEmpty(et_sendmessage.getText().toString().trim())|| dataList.size()!=0) {   //这里trim()作用是去掉首位空格，防止不必要的错误
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(et_sendmessage.getWindowToken(), 0); //强制隐藏键盘
                    showSelectPublishDialog();
                } else {
                    finish();
                }
                break;
            case R.id.publish_pic_run:
                uploadPaths.clear();
                if (dataList.size() == 0) {
                    if (StringUtil.isNullOrEmpty(et_sendmessage.getText().toString())) {
                        Toast.makeText(this, R.string.commetn_isnull, Toast.LENGTH_SHORT).show();
                        return;
                    }else{
                        if (et_sendmessage.getText().toString().length() > 500){
                            Toast.makeText(this, R.string.publish_video_error, Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                }
                progressDialog = new ProgressDialog(PublishPicActivity.this);
                progressDialog.setCancelable(false);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                //检查有没有选择图片
                if (dataList.size() == 0) {
                    publishMood();
                    return;
                } else {
                    for (int i = 0; i < dataList.size(); i++) {
                        //七牛
                        Bitmap bm = FileUtils.getSmallBitmap(dataList.get(i));
                        final String cameraImagePath = FileUtils.saveBitToSD(bm, System.currentTimeMillis() + ".jpg");
                        Map<String,String> map = new HashMap<String, String>();
                        map.put("space",InternetURL.QINIU_SPACE);
                        RequestParams params = new RequestParams(map);
                        client.get(InternetURL.UPLOAD_TOKEN ,params, new JsonHttpResponseHandler(){
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                super.onSuccess(statusCode, headers, response);
                                try {
                                    String token = response.getString("data");
                                    UploadManager uploadManager = new UploadManager();
                                    uploadManager.put(StringUtil.getBytes(cameraImagePath), StringUtil.getUUID(), token,
                                            new UpCompletionHandler() {
                                                @Override
                                                public void complete(String key, ResponseInfo info, JSONObject response) {
                                                    //key
                                                    uploadPaths.add(key);
                                                    if (uploadPaths.size() == dataList.size()) {
                                                        publishAll();
                                                    }
                                                }
                                            }, null);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                super.onFailure(statusCode, headers, throwable, errorResponse);
                            }
                        });
                    }
                }
                break;
            case R.id.send_btn_face_normal:
                // 显示表情选择框
                send_btn_face_normal.setVisibility(View.INVISIBLE);
                send_btn_face_enable.setVisibility(View.VISIBLE);
                view.setVisibility(View.VISIBLE);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(et_sendmessage.getWindowToken(), 0); //强制隐藏键盘
                break;
            case R.id.send_btn_face_enable:
                // 隐藏表情选择框
                send_btn_face_enable.setVisibility(View.INVISIBLE);
                send_btn_face_normal.setVisibility(View.VISIBLE);
                view.setVisibility(View.GONE);
                break;
            case R.id.add_pic://打开选择框 ，相机还是相册
                showSelectImageDialog();
                break;
            case R.id.face_content:
                if (view.getVisibility() == View.VISIBLE) {
                    send_btn_face_enable.setVisibility(View.INVISIBLE);
                    send_btn_face_normal.setVisibility(View.VISIBLE);
                    view.setVisibility(View.GONE);
                }
                break;
        }
    }

    // 选择是否退出发布
    private void showSelectPublishDialog() {
        publishPopWindow = new PublishPopWindow(PublishPicActivity.this, itemOnClick);
        publishPopWindow.showAtLocation(PublishPicActivity.this.findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    //为弹出窗口实现监听类
    private View.OnClickListener itemOnClick = new View.OnClickListener() {

        public void onClick(View v) {
            publishPopWindow.dismiss();
            switch (v.getId()) {
                case R.id.btn_sure:
                    finish();
                    break;
                default:
                    break;
            }
        }
    };

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) { //监控/拦截/屏蔽返回键
            if (!TextUtils.isEmpty(et_sendmessage.getText().toString().trim())|| dataList.size()!=0) {
                showSelectPublishDialog();
                return true;
            } else {
                finish();
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    //发布
    private void publishMood() {
        final String contentStr = et_sendmessage.getText().toString();
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.PUBLIC_MOOD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            RecordSingleDATA data = getGson().fromJson(s, RecordSingleDATA.class);
                            if (Integer.parseInt(data.getCode()) == 200) {
                                Toast.makeText(PublishPicActivity.this, R.string.publish_success, Toast.LENGTH_SHORT).show();
                                  //调用广播，刷新主页
                                Intent intent1 = new Intent(Constants.SEND_INDEX_SUCCESS);
                                intent1.putExtra("addRecord", data.getData());
                                sendBroadcast(intent1);
                                finish();
                            }
                        }
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(PublishPicActivity.this, R.string.publish_error_one, Toast.LENGTH_SHORT).show();
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mm_msg_type", Constants.MOOD_TYPE);
                params.put("mm_msg_content", contentStr);
                params.put("mm_emp_id", mm_emp_id);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        getRequestQueue().add(request);
    }

    //上传完图片后开始发布
    private void publishAll() {
        final String contentStr = et_sendmessage.getText().toString();
        final StringBuffer filePath = new StringBuffer();
        for (int i = 0; i < uploadPaths.size(); i++) {
            filePath.append(uploadPaths.get(i));
            if (i != uploadPaths.size() - 1) {
                filePath.append(",");
            }
        }
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.PUBLIC_MOOD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            RecordSingleDATA data = getGson().fromJson(s, RecordSingleDATA.class);
                            if (Integer.parseInt(data.getCode()) == 200) {
                                Toast.makeText(PublishPicActivity.this, R.string.publish_success, Toast.LENGTH_SHORT).show();
                                //调用广播，刷新主页
                                Intent intent1 = new Intent(Constants.SEND_INDEX_SUCCESS);
                                intent1.putExtra("addRecord", data.getData());
                                sendBroadcast(intent1);
                                finish();
                            }
                        }
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        Toast.makeText(PublishPicActivity.this, R.string.publish_error_one, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mm_msg_type", Constants.RECORD_TYPE);
                params.put("mm_msg_content", contentStr);
                params.put("mm_msg_picurl", String.valueOf(filePath));
                params.put("mm_emp_id", mm_emp_id);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        getRequestQueue().add(request);
    }

    // 选择相册，相机
    private void showSelectImageDialog() {
        deleteWindow = new SelectPhoTwoPopWindow(PublishPicActivity.this, itemsOnClick);
        //显示窗口
        deleteWindow.showAtLocation(PublishPicActivity.this.findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    private void openCamera() {
        Intent cameraIntent = new Intent();
        cameraIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.addCategory(Intent.CATEGORY_DEFAULT);
        // 根据文件地址创建文件
        File file = new File(CommonDefine.FILE_PATH);
        if (file.exists()) {
            file.delete();
        }
        uri = Uri.fromFile(file);
        // 设置系统相机拍摄照片完成后图片文件的存放地址
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

        // 开启系统拍照的Activity
        startActivityForResult(cameraIntent, CommonDefine.TAKE_PICTURE_FROM_CAMERA);
    }

    private ArrayList<String> getIntentArrayList(ArrayList<String> dataList) {

        ArrayList<String> tDataList = new ArrayList<String>();

        for (String s : dataList) {
//            if (!s.contains("camera_default")) {
            tDataList.add(s);
//            }
        }
        return tDataList;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case SELECT_LOCAL_PHOTO:
                    tDataList = data.getStringArrayListExtra("datalist");
                    if (tDataList != null) {
                        for (int i = 0; i < tDataList.size(); i++) {
                            String string = tDataList.get(i);
                            dataList.add(string);
                        }
//                        if (dataList.size() < 9) {
//                            dataList.add("camera_default");
//                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        finish();
                    }
                    break;
                case CommonDefine.TAKE_PICTURE_FROM_CAMERA:
                    String sdStatus = Environment.getExternalStorageState();
                    if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
                        return;
                    }
                    Bitmap bitmap = ImageUtils.getUriBitmap(this, uri, 400, 400);
                    String cameraImagePath = FileUtils.saveBitToSD(bitmap, System.currentTimeMillis() + ".jpg");

                    dataList.add(cameraImagePath);
                    adapter.notifyDataSetChanged();
                    break;
                case CommonDefine.TAKE_PICTURE_FROM_GALLERY:
                    tDataList = data.getStringArrayListExtra("datalist");
                    if (tDataList != null) {
                        dataList.clear();
                        for (int i = 0; i < tDataList.size(); i++) {
                            String string = tDataList.get(i);
                            dataList.add(string);
                        }
                        adapter.notifyDataSetChanged();
                    }
                    break;
                case CommonDefine.DELETE_IMAGE:
                    int position = data.getIntExtra("position", -1);
                    dataList.remove(position);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    }



    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        public void onClick(View v) {
            deleteWindow.dismiss();
            switch (v.getId()) {
                case R.id.camera: {
                    Intent cameraIntent = new Intent();
                    cameraIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                    cameraIntent.addCategory(Intent.CATEGORY_DEFAULT);
                    // 根据文件地址创建文件
                    File file = new File(CommonDefine.FILE_PATH);
                    if (file.exists()) {
                        file.delete();
                    }
                    uri = Uri.fromFile(file);
                    // 设置系统相机拍摄照片完成后图片文件的存放地址
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                    // 开启系统拍照的Activity
                    startActivityForResult(cameraIntent, CommonDefine.TAKE_PICTURE_FROM_CAMERA);
                }
                break;
                case R.id.mapstorage: {
                    Intent intent = new Intent(PublishPicActivity.this, AlbumActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList("dataList", getIntentArrayList(dataList));
                    bundle.putString("editContent", et_sendmessage.getText().toString());
                    intent.putExtras(bundle);
                    startActivityForResult(intent, CommonDefine.TAKE_PICTURE_FROM_GALLERY);
                }
                break;
                default:
                    break;
            }
        }

    };

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {

            // 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
            View v = getCurrentFocus();

            if (isShouldHideInput(v, ev)) {
                hideSoftInput(v.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
     *
     * @param v
     * @param event
     * @return
     */
    private boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
        return false;
    }

    /**
     * 多种隐藏软件盘方法的其中一种
     *
     * @param token
     */
    private void hideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token,
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
