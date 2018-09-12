package com.mit.ams.utils;

import java.io.IOException;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.kxml2.kdom.Element;
import org.kxml2.kdom.Node;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;

import com.mit.ams.common.Constants;

public class WSClient {

    public static String soapGetInfo(String wsUrl, String methodName, String[] key, String[] value) {
        String result = "";
        try {
            SoapObject request = new SoapObject(Constants.NAME_SPACE, methodName);
            //		request.addProperty("userName1", "李四");
            //传参
            for (int i = 0; i < key.length; i++) {
                request.addProperty(key[i], value[i]);
            }
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);
            envelope.bodyOut = request;
            HttpTransportSE ht = new HttpTransportSE(wsUrl);
            ht.call(null, envelope);
            Object soapObject = (Object) envelope.getResponse();
            //		System.out.println(soapObject.toString());
            result = soapObject.toString();
        } catch (Exception ex) {

        }
        return result;
    }

    /**
     * @param methodName
     * @return 传入参数及方法名获取服务端返回值
     * @throws IOException
     * @throws XmlPullParserException
     */
    public static String soapGetInfo(String methodName, String[] key, String[] value) throws IOException, XmlPullParserException {
        Log.i("soap： " + methodName, "==============  WS通讯start  ==============");//开始通讯

        // 返回的查询结果
        String result = null;
        // 将方法名和名称空间绑定在一起
        String SOAP_ACTION = Constants.NAME_SPACE + methodName;

        Log.i("soap： " + methodName, "==============  WS通讯soap_action:" + methodName);//记录访问action

        SoapObject request = new SoapObject(Constants.NAME_SPACE, methodName);
        //传参
        for (int i = 0; i < key.length; i++) {
            request.addProperty(key[i], value[i]);
        }

        //加密信息
        Element[] header = new Element[1];
        header[0] = new Element().createElement("", "AuthenticationToken");
        Element username = new Element().createElement("", "username");
        username.addChild(Node.TEXT, Constants.WS_UNAME);
        header[0].addChild(Node.ELEMENT, username);
        Element pass = new Element().createElement("", "password");
        pass.addChild(Node.TEXT, Constants.WS_PWD);
        header[0].addChild(Node.ELEMENT, pass);

        // 设置soap的版本
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        // 设置是否调用的是dotNet开发的
        envelope.dotNet = true;
        //添加通讯内容的 header、body
        envelope.headerOut = header;
        envelope.bodyOut = request;
        envelope.setOutputSoapObject(request);

        Log.i("soap： " + methodName, "==============  WS通讯soap_content:" + envelope.toString());//记录访问action

        HttpTransportSE hts;
        //获取代理主机
//		String host = android.net.Proxy.getDefaultHost();
        // 获取端口
//		int port = android.net.Proxy.getDefaultPort();

        hts = new HttpTransportSE(Constants.BASE_URL);

        Log.i("soap： " + methodName, "==============  WS通讯soap_url:" + Constants.BASE_URL);
        Log.i("soap： " + methodName, "==============  WS通讯soap_proxy:" + android.net.Proxy.getDefaultHost() + ":" + android.net.Proxy.getDefaultPort());
        try {
            // web service请求
            hts.call(SOAP_ACTION, envelope);
            // 得到返回结果
            //Object o = envelope.getResponse();
            Object o = (Object) envelope.getResponse();
            result = o.toString();
        } catch (Exception ex) {
            String err = ex.getMessage();
            Log.e("soap： " + methodName, "==============  WS通讯错误:" + err);
        }

        Log.i("soap： " + methodName, "==============  WS通讯result:" + result);
        Log.i("soap： " + methodName, "==============  WS通讯end   ==============");

        return result;
    }

}
