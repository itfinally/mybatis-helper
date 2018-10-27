# 设计文档

本文主要描述该插件的设计，包括对 Criteria 对象查询 与 Myabtis 的部分设计分析。
 
### SQL 语法分析

首先来分析一个语句的构成，以下列 SQL 为例:

```SQL
select A.field1, B.field1 as fieldFlag 
from A, B join C on B.c_id = C.id 
where A.b_id = B.id 
group by A.field2 
order by B.create_time desc limit 0, 10
```

以上语句可能作为整体时，会觉得比较复杂，但实际上该 SQL 实际上可以分解为下列子句：

1. select 
2. from
3. ( left / inner / right ) join .. on ..
4. where 
5. group by .. ( having .. ) 
6. order by
7. limit

这种是根据谓词( 即动词以及形容词 )进行分解，这里按最重要的动词进行分解。

那么当我们分解语句后，便可以以每一个动词作为子句，继续对该子句进行分析，通常动词所连接的名词都会有多个，比如 " select a as field, b, c "，这个子句就包含了 a as field / b / c 三个名词( 其中 " a as field " 这个子句实际上又包含了一个动词, 但这里不管，将整体当作名词 )。

根据以上分析，那么我们就可以得到一棵抽象语法树，其中节点均为动词，并且根节点就是语句本身，没有任何词性。

以该语句为例，语句及其抽象语法树如下：

##### 语句：

```
select demo.field1 as demoField1, count(*) 
from demo 
left join demo2 on demo.demo2_id = demo2.id 
where demo.name = ? and demo.no like '%abc%' 
group by demo.field1 
order by demo.field1 desc
```

##### 抽象语法树：
![抽象语法树](https://github.com/itfinally/PhotoAlbum/blob/master/mybatis-helper/%E6%8A%BD%E8%B1%A1%E8%AF%AD%E6%B3%95%E6%A0%91.jpg?raw=true)

### Criteria 查询设计

当对整个 SQL 语法分析有一个大概的认知以后，就可以来看 Criteria 对象查询的设计了，这个功能正是按照语法树进行设计。( 当然也有参考 Hibernate5 的 Criteria )

以 Mybatis-JPA 的 API 为例。

首先每个子句都是一种表达式，这是一种基本概念，一切都是表达式( Expression )，而且每个表达式都可以有别名( alias )，剩下所有的子句都是基于表达式进行扩展。

![子查询继承图](https://github.com/itfinally/PhotoAlbum/blob/master/mybatis-helper/Subquery%20%E7%BB%A7%E6%89%BF%E5%9B%BE.png?raw=true)

以上图为例，上图是 Subquery 的继承图，其中注意 `Reference` 接口,  该接口是所有类的顶级接口, 主要负责别名的设置，所有的表达式都是间接实现该接口。

其次是 `Expression` 接口，就像刚才说的，一切都是表达式，因此实际上所有的子句都是继承该接口的实现，而后续的设计就像上章节的 SQL 语法分析所说的一样，是一个树状结构。

最后最重要的是 `Writable` 接口，该接口是用于渲染当前子句的 SQL，并且调用存储在自身的表达式，而且因为整个语法是一个树状结构，那么只要根节点开始渲染 SQL，SQL 的渲染便会自顶而下一步步渲染，当最后一个子句渲染完毕并且返回到根节点后，结果便是一个完整的 SQL 语句。

其中一个 `Writable` 接口的实现如下。

![Writable](https://github.com/itfinally/PhotoAlbum/blob/master/mybatis-helper/Writable%20%E5%86%85%E9%83%A8%E5%AE%9E%E7%8E%B0.png?raw=true)

以上图为例，其中一个实现 `Writable` 接口的类，可以看到是通过调用存放在内部的表达式引发渲染行为，结合这种实现，以及上文描述的树状结构，便很容易实现出一个结构清晰的 SQL 对象查询工具。

也就是说，只要根节点触发 SQL 渲染，该指令便沿着各个子节点层层传递，直至叶节点将直接变量( 即一切不属于表达式的参数，例如 String )渲染成 SQL 子句，然后返回上层，由上层继续完成更高层次的子句渲染，如此反复，直至整棵树遍历完毕，最后返回根节点即代表 SQL 渲染完毕。

### Mybatis 动态 SQL 嵌入设计

有了前两节内容的设计，那么就意味着有一个完整的 SQL，剩下的问题就是如何安全执行 SQL。我们知道，执行单条 SQL 容易，但如果涉及到事务，防 SQL 注入等问题，那问题将会变得更为复杂。

那么如何解决如事务这类棘手的问题？答案是不要想着推翻，而是想办法通过伪装融入到已有的流程，那么就可以借用 Mybatis 已经存在的各种成熟流程和组件解决问题。

那么现在的问题就从解决事务等棘手问题转变为如何成功伪装并融入到 Mybatis，这一步需要对 Mybatis 的执行流程有所了解，所以首先我们来分析 Mybatis 的 SQL 执行链路。

首先来看 `Configuration` 类，该类是贯穿 Mybatis 所有流程的配置类，里面比较多内容，但我们关注的是 Mybatis 是如何获取 Mapper 的，因此通过跟踪，发现了 `MapperRegistry`， `MapperProxyFactory` 等类，其中的关系如下图所示：

<div align="center">
<img src="https://github.com/itfinally/PhotoAlbum/blob/master/mybatis-helper/Mybatis%20MapperProxy%20%E6%B5%81%E7%A8%8B%E5%9B%BE.png?raw=true">
</div>

我们可以看到，最终是通过 MapperProxy 处理接口调用，那如果我们能够在此通过线程本地变量提前为拦截器提供信息，那我们将可以在拦截器内修改 Mybatis 的行为。

所以该插件也重写了 Configuration -> ... -> MapperProxy 整个调用链路所涉及到的所有类，并且通过 spring 强行覆盖 Mybatis 的 Configuration。

而当我们执行 Criteria 对象查询时，实际上也是通过 Mapper 进行查询，不同的是，因为现在我们有了掌控 MapperProxy 的能力，便可以在拦截器内知道当前查询是这个 Mapper 发起的，从而有针对性地拦截，而所有流程对开发者都是透明的。

##### MappedStatement, 仅次于 Configuration 的配置类：

MappedStatement 类是 Mybatis 对 Mapper 内所有方法的一个描述对象，这里包括对应的参数映射，执行的 SQL 语句，包含的关联查询等信息，因此最后一环是控制并合理替换该类，达成这个目的后，我们便可以对 Mybatis 做到控制自如。

首先是解决 SQL 问题，我们知道 Mybatis 拥有动态 SQL 的能力，并且对用户只能识别自身的模版，那么这意味着在上章节生成的 SQL 不能是直接可执行的语句，而是 Mybatis 的 SQL 模版。

这当然不是问题，但是需要在运行时让 Mybatis 解析该 SQL 模版并转换出诸如 ResultMap 等信息以及 SQL 语句，通过大量的源码分析，有如下发现。

- XMLMapperBuilder 可解析模版文本并生成 ResultMap 实体映射信息
- XMLLanguageDriver 可解析动态 SQL ，并返回对应的 SqlSource

上述两个工具类此处不做叙述，感兴趣的读者可自行阅读 Mybatis 源码。
有了上述两个工具，那么替换的组件就准备完毕，后续的工作便是在拦截器内替换正确的 MappedStatement 并且调用 `invocation.proceed()`，那么 Mybatis 就会继续原流程，达到内嵌 SQL 的目的，同时也解决了事务问题。

### 总结

通过上述所有章节，已经将 Mybatis-JPA 的大概设计描述完毕，当然在细节上如实体的分析，ResultMap 的生成等细节部分的流程在此处不作叙述，感兴趣的读者可以自行阅读源码。

总的来说，整个流程如下图所示：

![Mybatis-JPA](https://github.com/itfinally/PhotoAlbum/blob/master/mybatis-helper/Mybatis-JPA.png?raw=true)


