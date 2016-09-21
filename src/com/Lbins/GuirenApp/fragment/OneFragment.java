package com.Lbins.GuirenApp.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.Lbins.GuirenApp.GuirenApplication;
import com.Lbins.GuirenApp.MainActivity;
import com.Lbins.GuirenApp.R;
import com.Lbins.GuirenApp.base.BaseFragment;
import com.Lbins.GuirenApp.module.*;
import com.Lbins.GuirenApp.ui.ProfileActivity;
import com.Lbins.GuirenApp.ui.SearchActivity;
import com.Lbins.GuirenApp.util.Constants;
import com.Lbins.GuirenApp.util.StringUtil;
import com.Lbins.GuirenApp.widget.MyAlertDialog;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.Projection;
import com.amap.api.maps.model.*;
import com.view.wheelcity.OnWheelChangedListener;
import com.view.wheelcity.WheelView;
import com.view.wheelcity.adapters.AbstractWheelTextAdapter;
import com.view.wheelcity.adapters.ArrayWheelAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhl on 2016/5/6.
 */
public class OneFragment extends BaseFragment implements View.OnClickListener ,AMap.OnMarkerClickListener,AMap.OnInfoWindowClickListener, AMap.OnMarkerDragListener, AMap.OnMapLoadedListener,
         AMap.InfoWindowAdapter {
    private View view;
    private Resources res;
    private MapView mapView;
    private AMap aMap;

    private MarkerOptions markerOption;
    private LatLng latlng = new LatLng(36.061, 103.834);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerBoradcastReceiver();
    }

    private TextView btn_one;
    private TextView btn_two;

    private String mm_hangye_id;
    private String mm_hangye_name;
    private String mm_emp_countryId;
    private String mm_emp_countryName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.one_fragment, null);
        res = getActivity().getResources();
        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        mm_hangye_id = "";
        mm_emp_countryId = "";
        mm_hangye_name = "";
        mm_emp_countryName = "";
        init();
        initView();
        return view;
    }

    void initView(){
        ImageView searchButton = (ImageView) view.findViewById(R.id.btn_search);
        searchButton.setOnClickListener(this);
        view.findViewById(R.id.location_btn).setOnClickListener(this);
        if(!StringUtil.isNullOrEmpty(GuirenApplication.latStr) && !StringUtil.isNullOrEmpty(GuirenApplication.lngStr)){
            latlng = new LatLng(Double.valueOf(GuirenApplication.latStr), Double.valueOf(GuirenApplication.lngStr));
        }
        setUpMap();
        btn_one = (TextView) view.findViewById(R.id.btn_one);
        btn_two = (TextView) view.findViewById(R.id.btn_two);
//        btn_one.setText(getGson().fromJson(getSp().getString("mm_hangye_name", ""), String.class) );
//        btn_two.setText(getGson().fromJson(getSp().getString("areaName", ""), String.class) );
        btn_one.setOnClickListener(this);
        btn_two.setOnClickListener(this);
    }

    private void setUpMap() {
        aMap.setOnMarkerDragListener(this);// 设置marker可拖拽事件监听器
        aMap.setOnMapLoadedListener(this);// 设置amap加载成功事件监听器
        aMap.setOnMarkerClickListener(this);// 设置点击marker事件监听器
        aMap.setOnInfoWindowClickListener(this);// 设置点击infoWindow事件监听器
        aMap.setInfoWindowAdapter(this);// 设置自定义InfoWindow样式
        addMarkersToMap();// 往地图上添加marker
    }

    /**
     * 在地图上添加marker
     */
    public void addMarkersToMap() {
        // 文字显示标注，可以设置显示内容，位置，字体大小颜色，背景色旋转角度
        ArrayList<MarkerOptions> markerOptionlst = new ArrayList<MarkerOptions>();
        TextView textView= (TextView) view.findViewById(R.id.location_map);

        if(MainActivity.recordList != null){
            for(Emp emp:MainActivity.recordList){
                markerOption = new MarkerOptions();
                if(!StringUtil.isNullOrEmpty(emp.getLat()) && !StringUtil.isNullOrEmpty(emp.getLng())){
                    //用户有会员定位数据
                    final LatLng latLngTmp = new LatLng(Double.valueOf(emp.getLat()), Double.valueOf(emp.getLng()));
                    markerOption.position(latLngTmp);

                    markerOption.title(emp.getMm_emp_nickname()).snippet(emp.getMm_hangye_name());
                    markerOption.draggable(true);

//                    final ImageView imageView = (ImageView) view.findViewById(R.id.location_map);
//                    imageLoader.displayImage(emp.getMm_emp_cover(), imageView, GuirenApplication.txOptions, new AnimateFirstDisplayListener(){
//                        @Override
//                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                            super.onLoadingComplete(imageUri, view, loadedImage);
//
//                            Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
//                            markerOption.icon(
//                            BitmapDescriptorFactory.fromBitmap(bitmap));
//
//                        }
//                    });
                    // 将Marker设置为贴地显示，可以双指下拉看效果
//                    textView.setText(emp.getMm_emp_nickname());
//                    textView.setTextSize(8.0f);
//                    textView.setPadding(3,3,3,6);
//                    textView.setTextColor(getResources().getColor(R.color.white));
//                    textView.setDrawingCacheEnabled(false);
//                    textView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//                    textView.layout(0, 0, textView.getMeasuredWidth(), textView.getMeasuredHeight()*2);
//                    Bitmap bitmap = textView.getDrawingCache();
//                    markerOption.icon(
//                            BitmapDescriptorFactory.fromBitmap(bitmap));

                    markerOption.setFlat(true);
                    markerOption.icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
                    markerOptionlst.add(markerOption);
                }

            }
            List<Marker> markerlst = aMap.addMarkers(markerOptionlst, true);
        }
    }

    /**
     * 对marker标注点点击响应事件
     */
    @Override
    public boolean onMarkerClick(final Marker marker) {
        if (aMap != null) {
            jumpPoint(marker);
        }
        return false;
    }

    /**
     * 从地上生长效果，实现思路
     * 在较短的时间内，修改marker的图标大小，从而实现动画<br>
     * 1.保存原始的图片；
     * 2.在原始图片上缩放得到新的图片，并设置给marker；
     * 3.回收上一张缩放后的图片资源；
     * 4.重复2，3步骤到时间结束；
     * 5.回收上一张缩放后的图片资源，设置marker的图标为最原始的图片；
     *
     * 其中时间变化由AccelerateInterpolator控制
     * @param marker
     */
    private void growInto(final Marker marker) {
        marker.setVisible(false);
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final long duration = 250;// 动画总时长
        final Bitmap bitMap = marker.getIcons().get(0).getBitmap();// BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher);
        final int width = bitMap.getWidth();
        final int height = bitMap.getHeight();

        final Interpolator interpolator = new AccelerateInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);

                if (t > 1) {
                    t = 1;
                }

                // 计算缩放比例
                int scaleWidth = (int) (t * width);
                int scaleHeight = (int) (t * height);
                if (scaleWidth > 0 && scaleHeight > 0) {

                    // 使用最原始的图片进行大小计算
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(Bitmap
                            .createScaledBitmap(bitMap, scaleWidth,
                                    scaleHeight, true)));
                    marker.setVisible(true);

                    // 因为替换了新的图片，所以把旧的图片销毁掉，注意在设置新的图片之后再销毁
                    if (lastMarkerBitMap != null
                            && !lastMarkerBitMap.isRecycled()) {
                        lastMarkerBitMap.recycle();
                    }

                    //第一次得到的缩放图片，在第二次回收，最后一次的缩放图片，在动画结束时回收
                    ArrayList<BitmapDescriptor> list = marker.getIcons();
                    if (list != null && list.size() > 0) {
                        // 保存旧的图片
                        lastMarkerBitMap = marker.getIcons().get(0).getBitmap();
                    }

                }

                if (t < 1.0 && count < 10) {
                    handler.postDelayed(this, 16);
                } else {
                    // 动画结束回收缩放图片，并还原最原始的图片
                    if (lastMarkerBitMap != null
                            && !lastMarkerBitMap.isRecycled()) {
                        lastMarkerBitMap.recycle();
                    }
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(bitMap));
                    marker.setVisible(true);
                }
            }
        });
    }

    /**
     * marker点击时跳动一下
     */
    public void jumpPoint(final Marker marker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = aMap.getProjection();
        //todo
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        startPoint.offset(0, -100);
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 1500;

        final Interpolator interpolator = new BounceInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * marker.getPosition().longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * marker.getPosition().latitude + (1 - t)
                        * startLatLng.latitude;
//                marker.setPosition(new LatLng(lat, lng));
                if (t < 1.0) {
                    handler.postDelayed(this, 16);
                }
            }
        });
    }

    private int count = 1;
    Bitmap lastMarkerBitMap = null;


    /**
     * 监听点击infowindow窗口事件回调
     */
    @Override
    public void onInfoWindowClick(Marker marker) {
//        Toast.makeText(getActivity(), "你点击了infoWindow窗口"+marker.getTitle() ,Toast.LENGTH_SHORT).show();
        Emp empT = null;
        for(Emp emp:MainActivity.recordList){
            if(emp.getMm_emp_nickname().equals(marker.getTitle())){
                LatLng latLng = marker.getPosition();
                if(String.valueOf(latLng.latitude).equals(emp.getLat())){
                    empT = emp;
                    break;
                }
            }
        }
        if(empT != null){
            Intent detail = new Intent(getActivity(), ProfileActivity.class);
            detail.putExtra("mm_emp_id", empT.getMm_emp_id());
            startActivity(detail);
        }

    }

    /**
     * 监听拖动marker时事件回调
     */
    @Override
    public void onMarkerDrag(Marker marker) {
        String curDes = marker.getTitle() + "拖动时当前位置:(lat,lng)\n("
                + marker.getPosition().latitude + ","
                + marker.getPosition().longitude + ")";
//        markerText.setText(curDes);
    }

    /**
     * 监听拖动marker结束事件回调
     */
    @Override
    public void onMarkerDragEnd(Marker marker) {
//        markerText.setText(marker.getTitle() + "停止拖动");
    }

    /**
     * 监听开始拖动marker事件回调
     */
    @Override
    public void onMarkerDragStart(Marker marker) {
//        markerText.setText(marker.getTitle() + "开始拖动");
    }

    /**
     * 监听amap地图加载成功事件回调
     */
    @Override
    public void onMapLoaded() {
        // 设置所有maker显示在当前可视区域地图中
//        LatLngBounds bounds = new LatLngBounds.Builder()
//                .include(Constants.XIAN).include(Constants.CHENGDU)
//                .include(Constants.BEIJING).include(latlng).build();
//        LatLngBounds bounds = new LatLngBounds.Builder()
//                .include(latlng)
//                .build();
//        aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150));
        LatLng marker1 = new LatLng(45, 108);
        //设置中心点和缩放比例
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(marker1));
        aMap.moveCamera(CameraUpdateFactory.zoomTo(2));
    }

    /**
     * 监听自定义infowindow窗口的infocontents事件回调
     */
    @Override
    public View getInfoContents(Marker marker) {
//        if (radioOption.getCheckedRadioButtonId() != R.id.custom_info_contents) {
//            return null;
//        }
        View infoContent = getActivity().getLayoutInflater().inflate(
                R.layout.custom_info_contents, null);
        render(marker, infoContent);
        return infoContent;
    }

    /**
     * 监听自定义infowindow窗口的infowindow事件回调
     */
    @Override
    public View getInfoWindow(Marker marker) {
//        if (radioOption.getCheckedRadioButtonId() != R.id.custom_info_window) {
//            return null;
//        }
        View infoWindow = getActivity().getLayoutInflater().inflate(
                R.layout.custom_info_window, null);

        render(marker, infoWindow);
        return infoWindow;
    }

    /**
     * 自定义infowinfow窗口
     */
    public void render(Marker marker, View view) {
//        if (radioOption.getCheckedRadioButtonId() == R.id.custom_info_contents) {
//            ((ImageView) view.findViewById(R.id.badge))
//                    .setImageResource(R.drawable.badge_sa);
//        } else if (radioOption.getCheckedRadioButtonId() == R.id.custom_info_window) {
//            ImageView imageView = (ImageView) view.findViewById(R.id.badge);
//            imageView.setImageResource(R.drawable.badge_wa);
//        }
        String title = marker.getTitle();
        TextView titleUi = ((TextView) view.findViewById(R.id.title));
        if (title != null) {
            SpannableString titleText = new SpannableString(title);
            titleText.setSpan(new ForegroundColorSpan(Color.BLACK), 0,
                    titleText.length(), 0);
            titleUi.setTextSize(14);
            titleUi.setText(titleText);

        } else {
            titleUi.setText("");
        }
        String snippet = marker.getSnippet();
        TextView snippetUi = ((TextView) view.findViewById(R.id.snippet));
        if (snippet != null) {
            SpannableString snippetText = new SpannableString(snippet);
            snippetText.setSpan(new ForegroundColorSpan(Color.DKGRAY), 0,
                    snippetText.length(), 0);
            snippetUi.setTextSize(14);
            snippetUi.setText(snippetText);
        } else {
            snippetUi.setText("");
        }
    }


    /**
     * 初始化AMap对象
     */
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }


    @Override
    public void onClick(View view) {
       switch (view.getId()){
           case R.id.btn_search:
                   Intent searchV = new Intent(getActivity(), SearchActivity.class);
                   searchV.putExtra("mm_hangye_id", mm_hangye_id);
                   searchV.putExtra("mm_emp_countryId", mm_emp_countryId);
                   searchV.putExtra("mm_hangye_name", mm_hangye_name);
                   searchV.putExtra("mm_emp_countryName", mm_emp_countryName);
                   startActivity(searchV);
               break;
           case R.id.location_btn:
                if(!StringUtil.isNullOrEmpty(GuirenApplication.latStr) && !StringUtil.isNullOrEmpty(GuirenApplication.lngStr)){
                   LatLng marker1 = new LatLng(Double.valueOf(GuirenApplication.latStr), Double.valueOf(GuirenApplication.lngStr));
                   //设置中心点和缩放比例
                   aMap.moveCamera(CameraUpdateFactory.changeLatLng(marker1));
                   aMap.moveCamera(CameraUpdateFactory.zoomTo(10));
                }else {
                    Toast.makeText(getActivity(), "请先打开GPS，方便我们进行定位",Toast.LENGTH_LONG).show();
                }
               break;
           case R.id.btn_one:
           {
               View viewDialog = dialogmHy();
               final MyAlertDialog dialog1 = new MyAlertDialog(getActivity())
                       .builder()
                       .setTitle("请选择行业")
                       .setView(viewDialog)
                       .setNegativeButton("全部", new View.OnClickListener() {
                           @Override
                           public void onClick(View v) {
                               mm_hangye_id = "";
                               mm_hangye_name = "点击切换行业";
                               btn_one.setText(mm_hangye_name);
                           }
                       });
               dialog1.setPositiveButton("确定", new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       btn_one.setText(hangYeType.getMm_hangye_name());
                       mm_hangye_id = hangYeType.getMm_hangye_id();
                       mm_hangye_name = hangYeType.getMm_hangye_name();
                   }
               });
               dialog1.show();
           }
               break;
           case R.id.btn_two:
           {
               View viewDialog = dialogm();
               final MyAlertDialog dialog1 = new MyAlertDialog(getActivity())
                       .builder()
                       .setTitle("请选择地区")
                       .setView(viewDialog)
                       .setNegativeButton("全部", new View.OnClickListener() {
                           @Override
                           public void onClick(View v) {
                               mm_emp_countryId = "";
                               mm_emp_countryName = "点击切换地区";
                               btn_two.setText(mm_emp_countryName);
                           }
                       });
               dialog1.setPositiveButton("确定", new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       btn_two.setText(countryObj.getArea());
                       mm_emp_countryId = countryObj.getAreaID();
                       mm_emp_countryName = countryObj.getArea();
                   }
               });
               dialog1.show();
           }
           break;
       }
    }


    //广播接收动作
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(Constants.SELECT_HANGYE_TYPE_SUCCESS)){
                HangYeType hangYeType = (HangYeType) intent.getExtras().get("hangYeType");
                mm_hangye_id = hangYeType.getMm_hangye_id();
                mm_hangye_name  = hangYeType.getMm_hangye_name();
                btn_one.setText(hangYeType.getMm_hangye_name());
            }
            if(action.equals(Constants.SEND_SELECT_AREA_SUCCESS)){
                CountryObj countryObj = (CountryObj) intent.getExtras().get("countryObj");
                mm_emp_countryId = countryObj.getAreaID();
                mm_emp_countryName = countryObj.getArea();
                btn_two.setText(countryObj.getArea());
            }

        }
    };

    //注册广播
    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(Constants.SELECT_HANGYE_TYPE_SUCCESS);
        myIntentFilter.addAction(Constants.SEND_SELECT_AREA_SUCCESS);
        //注册广播
        getActivity().registerReceiver(mBroadcastReceiver, myIntentFilter);
    }


    public void onDestroy() {
        super.onPause();
        //注销短信监听广播
        getActivity().unregisterReceiver(mBroadcastReceiver);
        mapView.onDestroy();
    };

    ProvinceObj provinceObj = null;
    CityObj cityObj = null;
    CountryObj countryObj = null;
    private String cityTxt = "";

    private View dialogm() {
        View contentView = LayoutInflater.from(getActivity()).inflate(
                R.layout.wheelcity_cities_layout, null);
        final WheelView country = (WheelView) contentView
                .findViewById(R.id.wheelcity_country);
        country.setVisibleItems(3);
        country.setViewAdapter(new CountryAdapter(getActivity()));

        final List<CityObj> cities = new ArrayList<CityObj>();
        final List<CountryObj> ccities = new ArrayList<CountryObj>();

        final WheelView city = (WheelView) contentView
                .findViewById(R.id.wheelcity_city);
        city.setVisibleItems(0);

        final WheelView ccity = (WheelView) contentView
                .findViewById(R.id.wheelcity_ccity);
        ccity.setVisibleItems(0);

        country.addChangingListener(new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                provinceObj = GuirenApplication.provinces.get(newValue);
                List<String> lists = new ArrayList<String>();
                for(CityObj cityObj : GuirenApplication.cities){
                    if(cityObj.getFather().equals(provinceObj.getProvinceID())){
                        cities.add(cityObj);
                        lists.add(cityObj.getCity());
                    }
                }
                updateCities(city, lists, newValue);
//                cityTxt = AddressData.PROVINCES[country.getCurrentItem()]
//                        + " | "
//                        + AddressData.CITIES[country.getCurrentItem()][city
//                        .getCurrentItem()]
//                        + " | "
//                        + AddressData.COUNTIES[country.getCurrentItem()][city
//                        .getCurrentItem()][ccity.getCurrentItem()];
            }
        });

        city.addChangingListener(new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                List<CityObj> cities = new ArrayList<CityObj>();
                for(CityObj cityObj : GuirenApplication.cities){
                    if(cityObj.getFather().equals(provinceObj.getProvinceID())){
                        cities.add(cityObj);
                    }
                }

                cityObj = cities.get(newValue);
                List<String> lists = new ArrayList<String>();
                for(CountryObj countryObj : GuirenApplication.areas){
                    if(countryObj.getFather().equals(cityObj.getCityID())){
                        ccities.add(countryObj);
                        lists.add(countryObj.getArea());
                    }
                }
                updatecCities(ccity, lists, country.getCurrentItem(),
                        newValue);
//                cityTxt = AddressData.PROVINCES[country.getCurrentItem()]
//                        + " | "
//                        + AddressData.CITIES[country.getCurrentItem()][city
//                        .getCurrentItem()]
//                        + " | "
//                        + AddressData.COUNTIES[country.getCurrentItem()][city
//                        .getCurrentItem()][ccity.getCurrentItem()];
            }
        });

        ccity.addChangingListener(new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                List<String> lists = new ArrayList<String>();
                List<CountryObj> ccities = new ArrayList<CountryObj>();
                for(CountryObj countryObj : GuirenApplication.areas){
                    if(countryObj.getFather().equals(cityObj.getCityID())){
                        ccities.add(countryObj);
                        lists.add(countryObj.getArea());
                    }
                }
                countryObj = ccities.get(newValue);
//                cityTxt = AddressData.PROVINCES[country.getCurrentItem()]
//                        + " | "
//                        + AddressData.CITIES[country.getCurrentItem()][city
//                        .getCurrentItem()]
//                        + " | "
//                        + AddressData.COUNTIES[country.getCurrentItem()][city
//                        .getCurrentItem()][ccity.getCurrentItem()];
            }
        });

        country.setCurrentItem(4);
        city.setCurrentItem(4);
        ccity.setCurrentItem(4);
        return contentView;
    }

    /**
     * Updates the city wheel
     */
    private void updateCities(WheelView city, List<String> cities, int index) {
        ArrayWheelAdapter<String> adapter = new ArrayWheelAdapter<String>(getActivity(),
                cities);
        adapter.setTextSize(18);
        adapter.setTextColor(res.getColor(R.color.text_color));
        city.setViewAdapter(adapter);
        city.setCurrentItem(0);
    }

    /**
     * Updates the ccity wheel
     */
    private void updatecCities(WheelView city, List<String> ccities, int index,
                               int index2) {
        ArrayWheelAdapter<String> adapter = new ArrayWheelAdapter<String>(getActivity(),
                ccities);
        adapter.setTextSize(18);
        adapter.setTextColor(res.getColor(R.color.text_color));
        city.setViewAdapter(adapter);
        city.setCurrentItem(0);
    }

    /**
     * Adapter for countries
     */
    private class CountryAdapter extends AbstractWheelTextAdapter {
        // Countries names
        private List<ProvinceObj> countries = GuirenApplication.provinces;

        /**
         * Constructor
         */
        protected CountryAdapter(Context context) {
            super(context, R.layout.wheelcity_country_layout, NO_RESOURCE);
            setItemTextResource(R.id.wheelcity_country_name);
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            View view = super.getItem(index, cachedView, parent);
            return view;
        }

        @Override
        public int getItemsCount() {
            return countries.size();
        }

        @Override
        protected CharSequence getItemText(int index) {
            return countries.get(index).getProvince();
        }
    }

    HangYeType hangYeType = null;
    private View dialogmHy() {
        View contentView = LayoutInflater.from(getActivity()).inflate(
                R.layout.wheelhangye_hy_layout, null);
        final WheelView country = (WheelView) contentView
                .findViewById(R.id.wheelhy_hy);
        country.setVisibleItems(3);
        country.setViewAdapter(new HangyeAdapter(getActivity()));

        country.addChangingListener(new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                hangYeType = GuirenApplication.listsTypeHy.get(newValue);
            }
        });

        country.setCurrentItem(4);
        return contentView;
    }

    /**
     * Adapter for countries
     */
    private class HangyeAdapter extends AbstractWheelTextAdapter {
        // Countries names
        private List<HangYeType> countries = GuirenApplication.listsTypeHy;

        /**
         * Constructor
         */
        protected HangyeAdapter(Context context) {
            super(context, R.layout.wheelcity_country_layout, NO_RESOURCE);
            setItemTextResource(R.id.wheelcity_country_name);
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            View view = super.getItem(index, cachedView, parent);
            return view;
        }

        @Override
        public int getItemsCount() {
            return countries.size();
        }

        @Override
        protected CharSequence getItemText(int index) {
            return countries.get(index).getMm_hangye_name();
        }
    }

}
