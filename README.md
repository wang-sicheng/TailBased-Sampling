<font  class=center  size=5>**首届云原生编程挑战赛1：
实现一个分布式统计和过滤的链路追踪**</font>
## 引言

云原生环境是错综复杂，而观察性就是云原生的眼睛。云原生的可观察性面临比较多的挑战，比如高性能，低资源，高质量数据。本赛题主要是考察链路追踪中的Tail-based Sampling。

## 问题背景
为了应对各种复杂的业务，系统架构也从单机大型软件演化成微服务架构。微服务构建在不同的软件集上，这些软件模块可能是由不同团队开发的，可能使用不同的编程语言来实现，还可能发布在多台服务器上。因此，如果一个服务出现问题，可能导致几十个服务都出现异常。

分布式追踪系统可以记录请求范围内的信息，包括用户在页面的一次点击发送请求，这个请求的所有处理过程，比如经过多少个服务，在哪些机器上执行，每个服务的耗时和异常情况。可参考 [链路追踪的概念](https://help.aliyun.com/document_detail/90498.html)

  采集链路数据过程中，采集的数据越多，消耗的成本就越多。为了降低成本，目前普遍的做法是对数据进行采样。请求是否采样都是从的头节点决定并通过跟踪上下文透传到所有节点(head-based sampling)。目前业界普遍的采样都是按照这个方式，比如固定比例采样(Probabilistic Sampling)，蓄水池采样(Rate Limiting Sampling)，混合采样(Adaptive Sample)。这样的采样可以保证整个调用链的完整性。但是这样采样也带来两个问题，一 有些异常和慢请求因为采样的原因没有被采集到，而这些链路数据对业务很重要。二 99%的请求都是正常的，而这些数据对问题排查并不重要，也就是说大量的成本花在并不重要的数据上。 
  
   本题目是另外一种采样方式(tail-based Sampling)，只要请求的链路追踪数据中任何节点出现重要数据特征（错慢请求），这个请求的所有链路数据都采集。目前开源的链路追踪产品都没有实现tail-based Sampling，主要的挑战是：任何节点出现符合采样条件的链路数据，那就需要把这个请求的所有链路数据采集。即使其他链路数据在这条链路节点数据之前还是之后产生，即使其他链路数据在分布式系统的成百上千台机器上产生。

 ## 一 整体流程

用户需要实现两个程序，一个是数量流（橙色标记）的处理程序，该机器可以获取数据源的http地址，拉取数据后进行处理，一个是后端程序（蓝色标记），和客户端数据流处理程序通信，将最终数据结果在后端引擎机器上落盘。

![enter image description here](https://tianchi-public.oss-cn-hangzhou.aliyuncs.com/public/files/forum/158797974420048391587979744067.png)
                           <font class=center> <b>流程图</b></font>
### 1.1、数据源


数据来源：采集自分布式系统中的多个节点上的调用链数据，每个节点一份数据文件。数据格式进行了简化，每行数据(即一个span)包含如下信息：

traceId | startTime | spanId | parentSpanId | duration | serviceName | spanName | host | tags

 
具体各字段的：

-   traceId：全局唯一的Id，用作整个链路的唯一标识与组装
-   startTime：调用的开始时间
-   spanId: 调用链中某条数据(span)的id
-   parentSpanId: 调用链中某条数据(span)的父亲id，头节点的span的parantSpanId为0
-  duration：调用耗时
-   serviceName：调用的服务名
-   spanName：调用的埋点名
-   host：机器标识，比如ip，机器名
-   tags: 链路信息中tag信息，存在多个tag的key和value信息。格式为key1=val1&key2=val2&key3=val3 比如 http.status_code=200&error=1

数据总量要足够大，暂定 4G。

文件里面有很个调用链路（很多个traceId）。每个调用链路内有很多个span（相同的traceId，不同的spanId）

例如文件1有

d614959183521b4b|1587457762873000|d614959183521b4b|0|311601|order|getOrder|192.168.1.3|http.status_code=200

d614959183521b4b|1587457762877000|69a3876d5c352191|d614959183521b4b|305265|Item|getItem|192.168.1.3|http.status_code=200

  

文件2有

d614959183521b4b|1587457763183000|dffcd4177c315535|d614959183521b4b|980|Loginc|getLogisticy|192.168.1.2|http.status_code=200

d614959183521b4b|1587457762878000|52637ab771da6ae6|d614959183521b4b|304284|Loginc|getAddress|192.168.1.2|http.status_code=200

 ### 1.2、数据流

  

官方提供两个程序（Docker 容器），以流的形式输出数据，参赛选手无法直接读取到数据文件。这样做的目的是让该比赛是一个面向流处理的场景，而不是批处理的场景。

  

在实际情况中，每个数据文件在时间上是顺序的，但是一个调用链的一个环节出现在哪个节点的文件中是不确定的。为了模拟这种跨节点的数据乱序的特性，我们生成的数据文件其实是一个大文件，该文件穿插了节点里面的若干个文件。因为提供的数据流的特性是：局部有序，全局无序。

### 1.3、后端引擎

上述数据流的两个程序可以和服务端通信，数据流处理程序不可以全量上报到后端数据引擎。只可以上报符合条件的数据和traceId。 在实际业务场景中，全量上报数据会导致网络的负载过大，把网卡打满，需要在数据流处理时进行过滤后上报存储。

  

  

  

## 二 、要求

  

找到所有tags中存在 http.status_code 不为 200，或者 error 等于1的调用链路。输出数据按照 traceId 分组，并按照startTime升序排序。最终输出若干个文件，每个文件中包含一个 traceId 分组下的全部数据。

注意：一条链路的数据，可能会出现两个数据流中，例如span在数据流1中，parentSpan在数据流2中。 当数据流1中某个span出现符合要求时，那对应的调用链(相同的traceId)的其他所有span都需要保存，即使其他span出现在数据流2中。

## 三、验证


官方提供一个验证程序，用来将选手生成的结果同已知结果进行对比，计算 F1 Score；同时记录整个跑分从开始到输出结果的时间 time，最后的比赛得分: F1/time。即 F1 值越大越好，耗时越小越好。

  

## 四、评测方式

用户提供一个docker image，评测程序会启动三个docker 容器，其中两个2核4G的容器会配置环境变量"SERVER_PORT"为8000和8001端口，对应的是流程图中的数据流程序(橙色), 另外一个1核2G的容器会配置环境变量"SERVER_PORT"为8002，对应的是流程图中的后端引擎（蓝色）。评测将分为三个阶段。

### 4.1 开始阶段

评测程序会调用用户docker容器的接口1，检查到某个docker的http接口已经返回状态为200后开始计时。当用户三个docker都返回200后，将会调用接口2

* 接口1：
  HTTP 1.1 HEAD localhost:8000/ready：一个状态接口，启动时评分程序会对探测，返回 HTTP 200 即表示选手的程序已经就绪，可以执行后续操作

* 接口2：
 HTTP 1.1 POST localhost:8000/setParamter：一个接收数据源的接口，格式为 {"dataport":"$port"} ，例如 {"dataport":"8080"}  返回 HTTP 200

  

### 4.2 运行计算阶段

用户程序收到数据源的端口后，可以调用http接口拉取数据。可以拉取到两份数据， http接口的格式为：HTTP 1.1  localhost:\$port/trace1.data, localhost:\$port/trace2.data

  

### 4.3 运行完成阶段

用户程序计算完数据后，需要将运行结果进行上报，该接口的具体url会在接口2中，用户运行完成后一次性返回所有traceID和对应的checksum（同一个traceId下的所有span按照startTime升序排列后生成的md5值），上报的接口格式为：

HTTP 1.1 POST "http://localhost:\${port}/api/finished"：参数为  result={"\$traceId1":"\$checksum1","\$traceId2":"$checksum2"}. curl 模拟请求为： curl -d 'result={"d614959183521b4b":"d8de6a65bd9f0b80f935b44dc6dff8d5"}' "http://localhost:8080/api/finished"

## 五、本地调试
   为了方便选手进行开发，提供了测试数据和本地运行方式
 ###  5.1 程序方式
   测试数据分两份一份是链路数据，分两个文件，[trace1.data](https://tianchi-competition.oss-cn-hangzhou.aliyuncs.com/231790/trace1_data.tar.gz?OSSAccessKeyId=LTAI4G7mrxYb7QrcXkTr3zzt&Expires=1593247540&Signature=YZLVJiJiJOga/KY4Z8dKVPyGJAQ=)和[trace2.data](https://tianchi-competition.oss-cn-hangzhou.aliyuncs.com/231790/trace2_data.tar.gz?OSSAccessKeyId=LTAI4G7mrxYb7QrcXkTr3zzt&Expires=1593247613&Signature=Z7PZrVG1UXnXdbO08bPv3hBCfyA=). 一份是[checksum](https://tianchi-competition.oss-cn-hangzhou.aliyuncs.com/231790/checkSum.data?OSSAccessKeyId=LTAI4G7mrxYb7QrcXkTr3zzt&Expires=1593244605&Signature=dCeICVXT9RrnjCMNEq4Gpduw1XE=)数据，用来校验结果。用户本地调试时可以运行程序分别访问链路数据文件（可以放本地文件，或者文件服务器中，比如nginx）。程序对链路数据进行聚合和过滤，生成符合要求的数据。


