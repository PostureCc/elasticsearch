package com.chan.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Map;


/**
 * @Auther: Chan
 * @Date: 2019/9/7 11:18
 * @Description:
 */
public class GsonUtils {

    private static Gson gson = (new GsonBuilder()).disableHtmlEscaping().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    private GsonUtils() {
    }

    /**
     * 转成json
     *
     * @param object
     * @return
     */
    public static String GsonString(Object object) {
        return gson.toJson(object);
    }

    /**
     * 转成bean
     *
     * @param gsonString
     * @param cls
     * @return
     */
    public static <T> T GsonToBean(String gsonString, Class<T> cls) {
        return gson.fromJson(gsonString, cls);
    }

    /**
     * 转成list
     *
     * @param gsonString
     * @param cls
     * @return
     */
    public static <T> List<T> GsonToList(String gsonString, Class<T> cls) {
        return gson.fromJson(gsonString, new TypeToken<List<T>>() {
        }.getType());
    }

    /**
     * 转成list中有map的
     *
     * @param gsonString
     * @return
     */
    public static <T> List<Map<String, T>> GsonToListMaps(String gsonString) {
        return gson.fromJson(gsonString,
                new TypeToken<List<Map<String, T>>>() {
                }.getType());
    }

    /**
     * 转成map的
     *
     * @param gsonString
     * @return
     */
    public static <T> Map<String, T> GsonToMaps(String gsonString) {
        return gson.fromJson(gsonString, new TypeToken<Map<String, T>>() {
        }.getType());
    }

}
