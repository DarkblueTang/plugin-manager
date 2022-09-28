/**
 * jeecg-boot-parent
 *
 * @author : tzy
 * @Date : 2020-12-21
 * @Description:
 **/

package com.tang.utils;

import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtils {
    private static Object Class;

    public static boolean isMatch(String mapper, String formType) {

        if (mapper == null || formType == null || StringUtils.isEmpty(formType) || StringUtils.isEmpty(mapper))
            return false;

        Pattern pattern = Pattern.compile(mapper);
        Matcher matcher = pattern.matcher(formType);
        if (matcher.find()) return true;
        return false;
    }

    public static <T extends Annotation> T getAnnotation(Object object, Class<T> tClass) {
        T annotation = null;

        if (object instanceof Class) {
            try {
                Class aClass = (Class) object;
                annotation = (T) aClass.getAnnotation(tClass);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (object instanceof Field) {
            try {
                Field field = (Field) object;
                annotation = (T) field.getAnnotation(tClass);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            annotation = object.getClass().getAnnotation(tClass);
        }

        if (annotation == null) {
            try {
                Object target = AopTargetUtils.getTargetDeep(object);
                annotation = target.getClass().getAnnotation(tClass);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return annotation;
    }

    public static boolean isEmpty(List list) {
        return list == null || list.size() == 0;
    }

    public static boolean isEmpty(String str) {
        return StringUtils.isEmpty(str);
    }

    public static boolean isEmpty(Map map) {
        return map == null || map.size() == 0;
    }

    public static boolean isEmpty(Set set) {
        return set == null || set.size() == 0;
    }

    public static boolean isEmpty(Object[] objects) {
        return objects == null || objects.length == 0;
    }

    public static boolean isEmpty(Object object) {
        return object == null;
    }

    public static boolean isContain(Object target, Object[] array) {
        for (Object o : array) {
            if (o.equals(target)) return true;
        }
        return false;
    }

    public static void log(Object... os) {
        for (int i = 0; i < os.length; i++) {
            Object o = os[i];
            System.out.print(o.toString());
            if (i < os.length - 1 && os.length > 1) {
                System.out.print(" ");
            }
        }
        System.out.println();
    }

    public static RuntimeException newException(Exception exception, String message) {
        RuntimeException e = new RuntimeException(message, exception.getCause());
        e.setStackTrace(exception.getStackTrace());

        return e;
    }

    public static String toLog(Object... object) {
        String res = "";
        if (isEmpty(object)) return res;
        StringBuilder stringBuilder = new StringBuilder();
        for (Object o : object) {
            stringBuilder.append(o.toString());
        }
        return stringBuilder.toString();
    }

    public static void isEmptyThrow(String str, RuntimeException exception) {
        if (isEmpty(str)) throw exception;
    }

    public static void isEmptyThrow(Object[] objs, RuntimeException exception) {
        if (isEmpty(objs)) throw exception;
    }

    public static void isEmptyThrow(Object obj, RuntimeException exception) {
        if (isEmpty(obj)) throw exception;
    }

    public static void isEmptyThrow(List obj, RuntimeException exception) {
        if (isEmpty(obj)) throw exception;
    }

    /**
     * 获取类的所有属性，包括父类
     *
     * @param object
     * @return
     */
    public static Field[] getAllFields(Object object) {
        if (object == null) return new Field[0];
        Class<?> clazz = object.getClass();
        List<Field> fieldList = new ArrayList<>();
        while (clazz != null) {
            fieldList.addAll(new ArrayList<>(Arrays.asList(clazz.getDeclaredFields())));
            clazz = clazz.getSuperclass();
        }
        Field[] fields = new Field[fieldList.size()];
        fieldList.toArray(fields);
        return fields;
    }

    /**
     * 获取类的所有属性，包括父类
     *
     * @param object
     * @return
     */
    public static Field[] getAllFields(Class object) {
        Class<?> clazz = object;
        List<Field> fieldList = new ArrayList<>();
        while (clazz != null) {
            fieldList.addAll(new ArrayList<>(Arrays.asList(clazz.getDeclaredFields())));
            clazz = clazz.getSuperclass();
        }
        Field[] fields = new Field[fieldList.size()];
        fieldList.toArray(fields);
        return fields;
    }

    public static Set toSet(Object obj) {
        //集合类型
        if (obj instanceof Collection) {
            return toSetFromCollection((Collection) obj);
        }

        //字符串类型
        if (obj instanceof String) {
            return toSetFromString((String) obj);
        }

        //基本类型
        if (obj instanceof Integer || obj instanceof Short
                || obj instanceof Long || obj instanceof Character
                || obj instanceof Float || obj instanceof Double
                || obj instanceof Boolean) {
            HashSet set = new HashSet();
            set.add(obj);
            return set;
        }

        return null;
    }

    private static Set toSetFromString(String obj) {
        if (CommonUtils.isEmpty(obj)) return new HashSet();
        String[] split = obj.split(",");
        HashSet res = new HashSet();
        for (String s : split)
            res.add(s);

        return res;
    }

    private static Set<String> toSetFromCollection(Collection collection) {
        return new HashSet(collection);
    }

    public static Set<String> toStringSet(Object o) {
        Set set = toSet(o);
        if(CommonUtils.isEmpty(set)) return new HashSet<>();

        Set<String> res = new HashSet<>();
        for (Object o1 : set) {
            if (CommonUtils.isEmpty(o1)) continue;
            String[] split = o1.toString().split(",");
            for (String s : split) {
                if(CommonUtils.isEmpty(s)) continue;
                res.add(s.trim());
            }
        }

        return res;
    }
}
