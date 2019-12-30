package com.chan.service;

import com.chan.utils.ElasticSearchUtils;
import com.chan.utils.GsonUtils;
import com.google.gson.JsonObject;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

/**
 * @Auther: Chan
 * @Date: 2019/12/27 10:46
 * @Description:
 */
public class TestMain {


    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
//        insert();
//        insert2();
        insert3();
//        insertField();
//        update();
//        queryById();
//        queryMatch();

        System.err.println("消耗时间:" + (System.currentTimeMillis() - startTime));
    }

    public static void queryMatch() {
        String curl = "http://114.67.177.190:9200"
                .concat("/megacorp1/employee/")
                .concat("_search");

        String dsl = "{\n" +
                "  \"query\": {\n" +
                "    \"match\": {\n" +
                //不区分大小写查询
                "      \"last_name\": \"test\"\n" +
                "    }\n" +
                "  }\n" +
                "}";

        System.err.println(ElasticSearchUtils.elasticSearchRequest(curl, dsl, true));

    }

    /**
     * 根据ID查询
     */
    public static void queryById() {
        String curl = "http://114.67.177.190:9200/megacorp1/employee/_search?q=_id:T6pdRm8BfYSHcNeMPZO4";

        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(curl, String.class);

        System.err.println(GsonUtils.GsonToBean(result, JsonObject.class));

    }

    public static void update() {
        String curl = "http://114.67.177.190:9200"
                .concat("/megacorp1/employee/")
                //_id的值
                .concat("T6pdRm8BfYSHcNeMPZO4/")
                //类型
                .concat("_update");

        //需要更新的值 doc是关键字 为必须项
        String dsl = "{\"doc\":{\"address\":\"住址\",\"about\":\"about\"}}";

        JsonObject result = ElasticSearchUtils.elasticSearchRequest(curl, dsl, true);
        System.err.println(result);
    }

    /**
     * 在已有的节点中新增字段
     */
    public static void insertField() {
        String curl = "http://114.67.177.190:9200"
                .concat("/megacorp1/employee/")
                //注意这句很重要!!!
                .concat("_update_by_query");

        String dsl = "{\"script\":{\"lang\":\"painless\",\"inline\":\"if (ctx._source.address == null) {ctx._source.address = ''}\"}}\n";

        JsonObject result = ElasticSearchUtils.elasticSearchRequest(curl, dsl, true);
        System.err.println(result);
    }

    /**
     * 普通新增
     */
    public static void insert() {
        /**
         * curl拼接: ip+port+mapping+type
         * ip: http://114.67.177.190
         * port: 9200
         * mapping: megacorp
         * type: employee
         * 如果不存在相应的索引或类型 会自动创建后新增数据
         * */
        String curl = "http://114.67.177.190:9200"
                .concat("/megacorp1/employee/");

        String dsl = "{\"first_name\":\"Test\",\"last_name\":\"Test\",\"age\":1,\"about\":\"Test\",\"interests\":[\"test1\",\"test2\"]}";

        JsonObject result = ElasticSearchUtils.elasticSearchRequest(curl, dsl, true);
        System.err.println(result);

    }

    /**
     * 测试新增 15569ms = 15s左右
     */
    public static void insert2() {
        String curl = "http://114.67.177.190:9200"
                .concat("/megacorp1/employee/");


        for (int i = 0; i < 1000; i++) {
            String dsl = "{\"first_name\":\"Test" + i + "\",\"last_name\":\"Test" + i + "\",\"age\":1,\"about\":\"Test\",\"interests\":[\"test1\",\"test2\"]}";
            JsonObject result = ElasticSearchUtils.elasticSearchRequest(curl, dsl, true);
            System.err.println(result);
        }
    }

    public static void insert3() {
        String curl = "http://114.67.177.190:9200"
                .concat("/megacorp1/employee/_bulk");


        StringBuilder dsl = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            dsl.append("{\"create\": {\"_index\": \"comm\",\"_type\": \"commodity\", \"_id\": \"" + i + "\"}}");
            dsl.append("{\"first_name\":\"Test" + i + "\",\"last_name\":\"Test" + i + "\",\"age\":1,\"about\":\"Test\",\"interests\":[\"test1\",\"test2\"]}\n");
        }

        JsonObject result = ElasticSearchUtils.elasticSearchRequest(curl, dsl.toString(), true);
        System.err.println(result);
    }

}
