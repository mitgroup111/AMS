package com.mit.ams.common;

/**
 * Created by Administrator on 17.7.7.
 */

public class Constants {

    /**
     *  阳光钣喷的web地址
     **/
    public final static String ARS_WEB_URL_1 = "http://teach.hems999.com/AMS/";

    /**
     * 登陆地址
     */
    public final static String ARS_LOGIN_URL = "u/androidLogin";

    /**
     * 阳光钣喷客户信息
     */
    public final static String ARS_CUS_URL = "android/customer/list";

    /**
     * 阳光钣喷车辆信息
     */
    public final static String ARS_CAR_URL = "android/customer/listCar";

    /**
     * 阳光钣喷工单信息
     */
    public final static String ARS_ORDER_URL = "android/order/list";

    /**
     * 阳光钣喷看板信息
     */
    public final static String ARS_KANBAN_URL = "android/kanban";

    /**
     * 阳光钣喷看板信息
     */
    public final static String ARS_TASK_URL = "android/tastList";

    /**
     * 阳光钣喷web地址
     */
    public static final String WEB_DOMAIN = "http://teach.hems999.com/AMS";//web

    public static final String APK_VERSION_ADDRESS = "http://teach.hems999.com/AMS/upload/AMS.apk";//web
    /**** old ****/


    /**
     *  奥瑞思保养的web地址
     **/
    public final static String ARS_WEB_URL = "http://www.arsauto.com.cn/";

    /**
     * webservice命名空间
     */
    public static final String NAME_SPACE = "http://services.dsws.mybatis.ds.com/";

    /**
     * 用到服务的主机地址
     */
    //public static final String WEB_SERVICE_DOMAIN = "http://192.168.206.126:7001";//webservice
    public static final String WEB_SERVICE_DOMAIN = "http://51ars.cn";//webservice


    /**
     * webservice地址
     */
//    public static final String BASE_URL = "http://192.168.208.208/dsWebService/service/IDsWS";
  public static final String BASE_URL= WEB_SERVICE_DOMAIN + "/dsWebService/service/IDsWS";
//    public static final String BASE_URL = "http://www.51ars.com/dsWebService/service/IDsWS";


    /**
     * 访问图片路径
     */
    public static final String IMG_URL = WEB_SERVICE_DOMAIN + "/dsWebService/upload";
//	public static final String IMG_URL= "http://192.168.206.126:7001/dsWebService/upload";

    /**
     * 个人中心各项目的web地址
     *
     */
    public static final String YUYUE_URL = WEB_DOMAIN + "/ListWeixiuYuyue.do?user_id=REPLACE_USERID&pageNo=1";
    public static final String DIANPING_URL = WEB_DOMAIN + "/ListDianping.do?user_id=REPLACE_USERID&pageNo=1";
    public static final String DINGSUN_URL = WEB_DOMAIN + "/listDingsun.do?flag=2&user_id=REPLACE_USERID&pageNo=1";
    public static final String WEIXIU_URL = WEB_DOMAIN + "//listWeixiu.do?flag=1&user_id=REPLACE_USERID&pageNo=1";
    public static final String BAOYANG_URL = WEB_DOMAIN + "/listMaint.do?user_id=REPLACE_USERID";
    public static final String CAR_URL = WEB_DOMAIN + "/gotCarList.do?user_id=REPLACE_USERID";
    public static final String MSG_URL = WEB_DOMAIN + "/toUserInfo.do?user_id=REPLACE_USERID";
    /**
     * webservice认证用户名
     */
    public static final String WS_UNAME = "ars";
    /**
     * webservice认证密码
     */
    public static final String WS_PWD = "ars3g";
}
