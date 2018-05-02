/**
 * CopyRight (C) 2016-2017 HIIRI Inc.All Rights Reserved.
 *
 * FileName  swan
 *
 * Description
 *
 * History:
 * Version     Author                 Date
 * 1.0         fanmx            2018/3/27  15:25
 */
package com.hitrobotgroup.hiiri.swan.express.control;

import com.alibaba.fastjson.JSONObject;
import com.hitrobotgroup.hiiri.swan.express.pojo.JsonResult;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/api/weixin")
@EnableScheduling
public class WeixinApiControl {

    //公众平台接口通用域名
    private static String url="https://api.weixin.qq.com/";
    //微信小程序
    private static String appId="wx5d35120aeca69f75";
    private static String appSecret="775042d74503c4c8bbdc2024320b953b";

    private static Logger logger= LoggerFactory.getLogger(WeixinApiControl.class);
    private static HttpClient client = HttpClientBuilder.create().build();


    private HttpEntity getHttp(String method,String queryCase) throws IOException {
        HttpPost request = new HttpPost(method);
        logger.info(queryCase);
        StringEntity reqEntity = null;
        try {
            reqEntity = new StringEntity(queryCase);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        reqEntity.setContentType("application/x-www-form-urlencoded");
        request.setEntity(reqEntity);
        HttpResponse response = client.execute(request);
        return  response.getEntity();
    }

    /**
     *
     * @param method
     * @param queryCase
     * @return
     */
    private JSONObject invokeService(String method,String queryCase) {
        JSONObject jsonResult=null;
        try {
            String strResult = EntityUtils.toString(getHttp(method,queryCase),"utf-8");
            jsonResult = JSONObject.parseObject(strResult);
            logger.info("result:" + jsonResult);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonResult;
    }

    /**
     * 微信小程序获取openid
     * @param code
     * @return
     */
    @RequestMapping(value = "getLoginMsg", method = {RequestMethod.POST, RequestMethod.GET})
    JsonResult getLoginMsg(String code) {
        try {
            String queryCase = "appid=" + appId + "&secret=" + appSecret + "&grant_type=authorization_code&js_code=" + code;
            JSONObject jsonObject=invokeService(url+"sns/jscode2session",queryCase);
            return new JsonResult(true, jsonObject);
        } catch (Exception ex) {
            return new JsonResult(false, ex.getMessage());
        }
    }
}
