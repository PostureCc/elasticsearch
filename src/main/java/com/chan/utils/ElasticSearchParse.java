package com.chan.utils;

import com.alibaba.fastjson.JSON;
import com.google.gson.JsonObject;
import lombok.extern.log4j.Log4j2;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Auther: Chan
 * @Date: 2019/12/27 11:03
 * @Description:
 */
@Log4j2
public class ElasticSearchParse {

    /**
     * 解析ES返回值
     */
    public static JsonObject parse(Response response) throws IOException {
        String json = response.body().string();
//        log.info(json);
        Map map = JSON.parseObject(json, Map.class);
        JsonObject result = new JsonObject();
        List<Map> ll = new ArrayList<>();
        for (Object o : map.keySet()) {
            if ("hits".equals(o)) {
                Map map1 = JSON.parseObject(map.get(o).toString(), Map.class);
                Integer num = (Integer) map1.get("total");
                result.addProperty("total", num);
                for (Object o1 : map1.keySet()) {
                    if ("hits".equals(o1)) {
                        List list = JSON.parseObject(map1.get(o1).toString(), List.class);
//                        log.info("List查询结果为：{}", list);
                        for (Object o2 : list) {
                            Map map2 = JSON.parseObject(o2.toString(), Map.class);
                            result.addProperty("id", map2.get("_id").toString());
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
        result.addProperty("list", GsonUtils.GsonString(ll));
        return result;
    }

}
