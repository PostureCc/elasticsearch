package com.chan.utils;

import com.google.gson.JsonObject;
import lombok.extern.log4j.Log4j2;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;

import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * @Auther: Chan
 * @Date: 2019/12/26 10:01
 * @Description:
 */
@Log4j2
//@Component
public class ElasticSearchUtils {

    private static String userName;

    private static String password;

    //    @Value("${commodityEs.userName}")
    public void setUserName(String userName) {
        this.userName = userName;
    }

    //    @Value("${commodityEs.password}")
    public void setPassword(String password) {
        this.password = password;
    }

    private static final OkHttpClient client = new OkHttpClient();

    private static MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

    /**
     * 校验用户名密码
     */
    private static Request auth(Request.Builder builder) {
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
     * @param url     请求地址
     * @param dsl     DSL语句
     * @param isParse 是否解析返回值
     * @return
     */
    public static JsonObject elasticSearchRequest(String url, String dsl, boolean isParse) {
        log.info("ElasticSearch---请求报文--- {} \n{}", url, dsl);
        RequestBody requestBody = RequestBody.create(mediaType, dsl);
        Request.Builder builder = new Request.Builder()
                .url(url)
                .header("Content-type", "application/json; charset=utf-8")
                .header("Content-Length", String.valueOf(dsl.length()))
                .post(requestBody);

        JsonObject result = null;
        Response response = null;
        try {

            Request request = auth(builder);
            response = client.newCall(request).execute();

            if (isParse && response.isSuccessful()) result = ElasticSearchParse.parse(response);

        } catch (Exception e) {
            log.error(e);
        } finally {
            response.close();
            log.info("---ElasticSearch--请求返回---\n{}", response);
        }
        return result;
    }


    public static void main(String[] args) {
        String dsl = "{\n" +
                "    \"_source\":[\n" +
                "        \"first_name\",\n" +
                "        \"last_name\",\n" +
                "        \"age\",\n" +
                "        \"about\",\n" +
                "        \"interests\"\n" +
                "    ],\n" +
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
                "    ],\n" +
                "    \"from\":0,\n" +
                "    \"size\":20,\n" +
                "    \"highlight\":{\n" +
                "        \"fields\":{\n" +
                "            \"last_name\":{\n" +
                "\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "}";

        String curl = "http://114.67.177.190:9200"
                .concat("/megacorp/employee/_search");

        JsonObject response = elasticSearchRequest(curl, dsl, true);

        System.err.println(response);

    }

}
