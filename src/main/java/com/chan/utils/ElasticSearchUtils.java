package com.chan.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import lombok.extern.log4j.Log4j2;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * @Auther: Chan
 * @Date: 2019/12/26 10:01
 * @Description:
 */
@Log4j2
@Component
public class ElasticSearchUtils {

    //    @Value("${commodityEs.userName}")
    private String userName;

    //    @Value("${commodityEs.password}")
    private String password;

    private static final OkHttpClient client = new OkHttpClient();

    private static MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

    /**
     * 校验用户名密码
     */
    private Request auth(Request.Builder builder) {
        if (StringUtils.isNotBlank(userName)) {
            builder.header("Authorization", String.format("Basic %s", snEncode(String.format("%s:%s", userName, password))));
        }
        return builder.build();
    }

    /**
     * 转码
     */
    public static String snEncode(String sn) {
        String snDecode = null;
        if (StringUtils.isNotEmpty(sn)) {
            try {
                snDecode = Base64.getEncoder().encodeToString(sn.getBytes("utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return snDecode;
    }

    /**
     * ElasticSearch 通用请求
     *
     * @param url
     * @param contentText
     * @return
     */
    public Response elasticSearchRequest(String url, String contentText) {
        log.info("ElasticSearch------请求报文--- {}", "\n" + url + "\n" + contentText);
        RequestBody requestBody = RequestBody.create(mediaType, contentText);
        Request.Builder builder = new Request.Builder()
                .url(url)
                .header("Content-type", "application/json; charset=utf-8")
                .header("Content-Length", String.valueOf(contentText.length()))
                .post(requestBody);

        Response response = null;
        try {
            Request request = this.auth(builder);
            response = client.newCall(request).execute();
            response.close();
            log.info("-------------ElasticSearch--请求返回------------\n{}", response);
        } catch (Exception e) {
            response.close();
            log.error(e);
        }
        return response;
    }

    public static Map parse(Response response) throws IOException {

        String json = response.body().string();
//        System.out.println(json);
        Map map = JSON.parseObject(json, Map.class);
        Map result = new HashMap();
        List<Map> ll = new ArrayList<>();
        for (Object o : map.keySet()) {
            if ("hits".equals(o)) {
                Map map1 = JSON.parseObject(map.get(o).toString(), Map.class);
                Integer num = (Integer) map1.get("total");
                result.put("total", num);
                for (Object o1 : map1.keySet()) {
                    if ("hits".equals(o1)) {
                        List list = JSON.parseObject(map1.get(o1).toString(), List.class);
//                        logger.info("List查询结果为：{}", list);
                        for (Object o2 : list) {
                            Map map2 = JSON.parseObject(o2.toString(), Map.class);
                            for (Object o3 : map2.keySet()) {
                                if ("_source".equals(o3)) {
                                    ll.add((Map) map2.get(o3));
                                }
                            }
                        }
                    }
                }
            }
        }
        result.put("list", ll);
        return result;
    }

    public static void main(String[] args) throws IOException {
        StringBuilder sb = new StringBuilder();
        StringBuilder query = new StringBuilder()
                .append("{\n" +
                        "    \"query\":{\n" +
                        "        \"bool\":{\n" +
                        "            \"must\":[\n" +
                        "                {\n" +
                        "                    \"match\":{\n" +
                        "                        \"last_name\":\"Smith\"\n" +
                        "                    }\n" +
                        "                }\n" +
                        "            ],\n" +
                        "            \"must_not\":[\n" +
                        "                {\n" +
                        "                    \"match\":{\n" +
                        "                        \"first_name\":\"Jane\"\n" +
                        "                    }\n" +
                        "                }\n" +
                        "            ]\n" +
                        "        }\n" +
                        "    },\n" +
                        "    \"sort\":[\n" +
                        "        {\n" +
                        "            \"age\":{\n" +
                        "                \"order\":\"asc\"\n" +
                        "            }\n" +
                        "        }\n" +
                        "    ]\n" +
                        "}");

        String curl = sb.append("http://114.67.177.190:9200")
                .append("/megacorp/employee/_search" + "\n").toString();
        log.info("请求地址为：{}", curl.concat(query.toString()));

        RequestBody requestBody = RequestBody.create(mediaType, query.toString());
        Request.Builder builder = new Request.Builder()
                .url(curl)
                .header("Content-type", "application/json; charset=utf-8")
                .header("Authorization", String.format("Basic %s", snEncode(String.format("%s:%s", null, null))))
                .header("Content-Length", String.valueOf(query.length()))
                .post(requestBody);

        Request request = new ElasticSearchUtils().auth(builder);
        Response response = client.newCall(request).execute();
        Map result = new HashMap();
        if (response.isSuccessful()) {
            result = parse(response);
        }

        System.err.println(GsonUtils.GsonToBean(result.toString(), JsonObject.class));

    }

}
