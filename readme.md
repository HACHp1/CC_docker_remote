# CC链远程调试docker环境

根据 [javasec](https://github.com/p1n93r/javasec) 构建的docker镜像。

漏洞环境与 [javasec](https://github.com/p1n93r/javasec) 基本一致，只不过使用spring boot设置了一系列路由，然后使用mvn构建jar包。
由于java版本有限制，版本需要比7u21小，找了一大圈没有现成的镜像，只能自己构建。最后得到的环境能够复现除CC5之外的其他链（CC5需要jdk 1.8）。

详见 [博客](https://hachp1.github.io/posts/Web%E5%AE%89%E5%85%A8/20220407-cc_analysis.html) 。
