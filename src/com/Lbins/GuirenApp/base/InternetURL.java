package com.Lbins.GuirenApp.base;

public class InternetURL {
    public static final String INTERNAL = "http://www.shandongguiren.com/";
//    public static final String INTERNAL =  "http://192.168.0.225:8080/";

    public static final String QINIU_URL =  "http://7xt74j.com1.z0.glb.clouddn.com/";

    public static final String WEIXIN_APPID = "wx4da8b73a07135cd1";
    public static final String WEIXIN_SECRET = "a393cc92c26041c3cdad965a931ba537";
    public static final  String WX_API_KEY="a393cc92c26041c3cdad965a931ba537";

    public static final String QINIU_SPACE = "paopao-pic";
    //多媒体文件上传接口
    public static final String UPLOAD_FILE = INTERNAL + "uploadImage.do";
    public static final String UPLOAD_TOKEN = INTERNAL + "token.json";
    //1登陆
    public static final String LOGIN__URL = INTERNAL + "memberLogin.do";
    //2根据用户id获得用户信息
    public static final String GET_MEMBER_URL = INTERNAL + "getMemberInfoById.do";
    //3获得所有省份
    public static final String GET_PROVINCE_URL = INTERNAL + "getProvince.do";
    //4获得城市
    public static final String GET_CITY_URL = INTERNAL + "getCity.do";
    //5获得地区
    public static final String GET_COUNTRY_URL = INTERNAL + "getCountry.do";
    //6注册
    public static final String REG_URL = INTERNAL + "memberRegister.do";
    //修改密码
    public static final String UPDATE_PWR_URL = INTERNAL + "updatePwrApp.do";
    //添加举报
    public static final String ADD_REPORT_URL = INTERNAL + "saveReport.do";
    //查詢廣告
    public static final String GET_AD_LOGIN_URL = INTERNAL + "getLoginAd.do";
    //查询公告
    public static final String GET_NOTICE_URL = INTERNAL + "getNotices.do";
    //根据手机号查询用户是否存在
    public static final String GET_EMP_MOBILE = INTERNAL + "getMemberByMobile.do";
    //百度推送
    public static final String UPDATE_PUSH_ID = INTERNAL + "updatePushId.do";
    //上传个人头像
    public static final String UPDATE_COVER = INTERNAL + "updateCover.do";

    //mob
    public static final String APP_MOB_KEY = "120b58af9e028";
    public static final String APP_MOB_SCRECT = "4bb67a8acb1f268400909074df4aa4c6";

    //查询动态评论
    public static final String GET_DETAIL_PL_URL = INTERNAL + "getCommentsByRecord.do";
    //添加说说
    public static final String PUBLIC_MOOD_URL = INTERNAL + "sendRecord.do";
    //添加评论
    public static final String PUBLISH_COMMENT_RECORD = INTERNAL + "saveComment.do";
    //查询赞
    public static final String GET_FAVOUR_URL = INTERNAL + "listZan.do";
    //动态
    public static final String RECORD_URL = INTERNAL + "recordList.do";

    //赞动态
    public static final String CLICK_LIKE_URL = INTERNAL + "zanRecord.do";
    //根据动态Id删除动态
    public static final String DELETE_RECORDS_URL = INTERNAL + "deleteRecordById.do";

    //行业
    public static final String GET_HY_URL = INTERNAL + "getHyTypes.do";

    //获得个人邀请码列表
    public static final String GET_YAOQING_CARD_URL = INTERNAL + "getYaoqingCardEmp.do";

    //查询人脉
    public static final String GET_RENMAI_URL = INTERNAL + "getEmps.do";

    //查询喜讯
    public static final String GET_XIXUN_URL = INTERNAL + "xixunList.do";

    //查询拓扑结构 两个人之间的关系
    public static final String GET_TUOPU_URL = INTERNAL + "listRenmaidu.do";

    //更换手机号
    public static final String UPDATE_MOBILE_URL = INTERNAL + "updateMobile.do";

    //修改个人信息
    public static final String EDIT_EMP_BY_ID = INTERNAL + "editEmpById.do";

    //根据贵人id查询用户资料
    public static final String GET_EMP_BY_HXUSERNAME = INTERNAL + "getHxByName.do";
    //查询好友资料
    public static final String GET_INVITE_CONTACT_URL = INTERNAL + "listInviteMemberInfo.do";


    //获得用户关系列表
    public static final String GET_MINE_CONTACTS_URL = INTERNAL + "getEmpRelates.do";
    //保存用户关系
    public static final String SAVE_MINE_CONTACTS_URL = INTERNAL + "saveEmpRelate.do";
    //更新用户关系列表
    public static final String UPDATE_MINE_CONTACTS_URL = INTERNAL + "updateEmpRelate.do";


    //根据动态uuid查询动态详情
    public static final String GET_RECORD_DETAIL_BYUUID_URL = INTERNAL + "getRecordById.do";
    //与我相关
    public static final String ANDME_URL = INTERNAL + "listRelate.do";

    //审核  同意 拒绝
    public static final String UPDATE_MINE_CHECK_URL = INTERNAL + "updateEmpCheck.do";


    //传订单给服务端--生成订单
    public static final String SEND_ORDER_TOSERVER = INTERNAL + "orderSave.do";
    //商城详细页
    public static final String DETAIL_GOODS_URL = INTERNAL + "paopaogoods/detail.do";
    //查询店铺信息liebiao
    public static final String GET_DIANPU_MSG_URL = INTERNAL + "appGetDianPu.do";
    //查询店铺信息详情
    public static final String GET_DIANPU_MSG_DETAIL_URL = INTERNAL + "appGetProfileMsg.do";

    public static final String OPEN_ZIMEITI_URL = INTERNAL + "saveDianpu.do";

    //删除商品
    public static final String DELETE_GOODS_URL = INTERNAL + "paopaogoods/delete.do";

    //更新背景图
    public static final String UPDATE_BG_URL = INTERNAL + "updateMineBg.do";
    //分享邀请码页面
    public static final String SHARE_YAOQING_CARD_URL = INTERNAL + "html/download.html";

    //更新订单状态
    public static final String UPDATE_ORDER_TOSERVER = INTERNAL + "orderUpdate.do";

    //获得价格表单
    public static final String GET_MOENY_JIAGE_URL = INTERNAL + "getMoneyJiage.do";

    public static final String SEND_ORDER_TOSERVER_WX = INTERNAL + "orderSaveWx.do";


    //判断版本号
    public static final String CHECK_VERSION_CODE_URL = INTERNAL + "getVersionCode.do";

    //动态页面
    public static final String SHARE_RECORD_URL = INTERNAL + "viewRecord.do";

    //电影列表
    public static final String GET_VIDEOS_URL = INTERNAL +  "listVideosApp.do";
    //TV列表
    public static final String GET_VIDEOS_TV_URL = INTERNAL +  "listVideosAppTv.do";

    //视频分享
    public static final String SHARE_VIDEOS = INTERNAL +  "viewVideos.do";

    //查询videos评论
    public static final String GET_VIDEOS_PL_URL = INTERNAL +  "listVideosComment.do";
    //查询videos赞
    public static final String GET_VIDEOS_FAVOUR_URL =  INTERNAL + "appVideosListZan.do";

    //添加评论
    public static final String PUBLISH_VIDEO_COMMENT_RECORD =  INTERNAL +  "appVideosSaveComment.do";
    //添加赞
    public static final String PUBLISH_VIDEO_FAVOUR_RECORD =  INTERNAL +  "appVideoZanSave.do";

    //查询会员相册头三条
    public static final String RECORD_PICS_URL = INTERNAL + "recordPics.do";

    //跟新与我相关  已读
    public static final String UPDATE_RELATE_URL = INTERNAL + "updateRelateById.do";

    //获得视频类别
    public static final String GET_VIDEO_TYPES_URL = INTERNAL + "getVideoTypes.do";

    //查询电影类别
    public static final String GET_DIANYING_TYPES_URL = INTERNAL + "getDianyingTypes.do";


    //添加评论TV
    public static final String PUBLISH_VIDEO_COMMENT_RECORD_TV =  INTERNAL +  "appVideosSaveCommentTv.do";
    //添加赞TV
    public static final String PUBLISH_VIDEO_FAVOUR_RECORD_TV =  INTERNAL +  "appVideoZanSaveTV.do";

    //查询TV评论
    public static final String GET_VIDEOS_PL_URL_TV = INTERNAL +  "listVideosCommentTv.do";
    //查询TV赞
    public static final String GET_VIDEOS_FAVOUR_URL_TV =  INTERNAL + "appVideosListZanTV.do";

    //视频分享
    public static final String SHARE_VIDEOS_TV = INTERNAL +  "viewVideosTv.do";

    //查询所有自媒体类别
    public static final String GET_GD_TYPE_LISTS_URL = INTERNAL + "getGdTypes.do";
    //帮助服务列表
    public static final String GET_HELP_LISTS = INTERNAL + "appGetHelps.do";
    //保存帮助服务
    public static final String SAVE_HELP_URL = INTERNAL + "appSaveHelp.do";
    //获得帮忙的类型列表
    public static final String getHelpTypes = INTERNAL + "getHelpTypes.do";
    //单位
    public static final String getHelpDanwei = INTERNAL + "getHelpDanwei.do";
}
