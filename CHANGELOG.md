# Changelog

All notable changes to this project will be documented in this file. See [standard-version](https://github.com/conventional-changelog/standard-version) for commit guidelines.

### [2.11.3](https://github.com/limaofeng/jfantasy-framework/compare/v2.11.2...v2.11.3) (2024-03-24)


### Features

* graphql 订阅模式认证支持 ([3e36c24](https://github.com/limaofeng/jfantasy-framework/commit/3e36c24e610a20b2d8cb2dfdbe0a35afb420a841))
* jpa 保存对象时，自动注入 tenantId ([6d640fc](https://github.com/limaofeng/jfantasy-framework/commit/6d640fcc7a6bec4c1c86c2fd94fe1f40b909c4c9))
* mybatis 分页查询支持 PropertyFilter ([fbf713d](https://github.com/limaofeng/jfantasy-framework/commit/fbf713d90e5ceb0ccec1c96e053bd30b9a6a16c1))
* 为 LoginUser 添加 tenantId 租户ID ([769fb06](https://github.com/limaofeng/jfantasy-framework/commit/769fb06af8ca39f23a766e2369f5ece80f5238ca))
* 为 PropertyFilterBuilder 添加 getPropertyNames ([e3e1bfc](https://github.com/limaofeng/jfantasy-framework/commit/e3e1bfcc745fa6e35782890b6b716b14c2df7523))
* 为 TaskScheduler 添加事务注解 ([22be082](https://github.com/limaofeng/jfantasy-framework/commit/22be0822702fcf1a600f1e8edc1253eb2652fc43))
* 优化 BatchService ([213910a](https://github.com/limaofeng/jfantasy-framework/commit/213910a5c113a3c3f59ba34dfea13534c9483a38))
* 优化 BatchService, 打印队列大小 ([ee8e731](https://github.com/limaofeng/jfantasy-framework/commit/ee8e7314fb97cf0f851ace95d126b7341e052596))
* 优化 FileUtil 工具类 ([b5aac20](https://github.com/limaofeng/jfantasy-framework/commit/b5aac20433a1bbc361d60fd30461eef875da0a3f))
* 合并内容 ([c59e42b](https://github.com/limaofeng/jfantasy-framework/commit/c59e42bb0c1cc4706256ac7b3e783122b21f901d))
* 支持 Snowflake 主键生成器 ([5a0a135](https://github.com/limaofeng/jfantasy-framework/commit/5a0a135398ccfe47ca855a54d0768ce5ce6949f1))
* 添加 BasePublisher 支持的测试代码 ([824fec1](https://github.com/limaofeng/jfantasy-framework/commit/824fec1f71baa2fb9a45cb1de25bdc3a981eefbc))
* 添加 DataLoaderRegistry Customizer ([202f186](https://github.com/limaofeng/jfantasy-framework/commit/202f1863c323faaf61a53e5540fc08f8f7157685))
* 添加 JpaDefaultPropertyFilter 处理 jpa 过滤器的实现 ([5211c58](https://github.com/limaofeng/jfantasy-framework/commit/5211c5802b48a681afffb480871abad3fd9d5d45))
* 添加 NotAuthenticatedException 异常 ([9871217](https://github.com/limaofeng/jfantasy-framework/commit/9871217eacb6e80931cb31fc25b6eafa255f6c89))
* 添加 Qodana 插件 ([99f5064](https://github.com/limaofeng/jfantasy-framework/commit/99f5064e7b66121592f699e4d7768bdc8cd47521))
* 添加 ShardingJdbc 支持 ([cc1526c](https://github.com/limaofeng/jfantasy-framework/commit/cc1526ce2a0d2e9c2674b1fb2e8dac7bc771d3af))
* 添加 WhereInput 重构 graphql 查询 ([b81388b](https://github.com/limaofeng/jfantasy-framework/commit/b81388b32f5136202b5d4d6d9647f7e3a2e40960))
* 缓存提前初始化 ([625d4a6](https://github.com/limaofeng/jfantasy-framework/commit/625d4a679ed2db838ac42bd81501c5af4633f2d2))
* 重构 TaskScheduler ([8bef171](https://github.com/limaofeng/jfantasy-framework/commit/8bef1719bdabedd2f101a59c40390df4b4efffca))


### Bug Fixes

* Long 转换问题 ([4fc8a39](https://github.com/limaofeng/jfantasy-framework/commit/4fc8a3950409572e98f4655b55e1feaf6b9160b4))
* Long 转换问题 ([bac50d3](https://github.com/limaofeng/jfantasy-framework/commit/bac50d33444a71cbd987f1d700956efb8bc7e25f))
* 优化 qodana 中出现的警告 ([47bddeb](https://github.com/limaofeng/jfantasy-framework/commit/47bddeb4a1d4c8cc47e36c8d164a84b3dcece448))
* 修复 isPropertyFilter 错误 ([13bfb32](https://github.com/limaofeng/jfantasy-framework/commit/13bfb320aaa819ad0a6a37e6760047ebfda96e62))
* 修复 NotAuthenticatedException 对应的 ErrorCde ([23b407b](https://github.com/limaofeng/jfantasy-framework/commit/23b407b5844237525b5bf878223fb80a4be66960))
* 修复 StringToSetDeserializer 导致之后属性解析失败的 Bug ([2ab7a83](https://github.com/limaofeng/jfantasy-framework/commit/2ab7a83b492e5d0c07d95f467a14a58d01baf000))
* 修改 JdbcUtil ([f77c795](https://github.com/limaofeng/jfantasy-framework/commit/f77c795dd839fbaa0c0ac0e6ed8c58cc304fabef))
* 国际序列化BUG ([9b0d8b7](https://github.com/limaofeng/jfantasy-framework/commit/9b0d8b79e9f8345b7af88ef8b5c214af35da1178))
* 抛出认证错误，避免造成二次影响 ([cc6a9c7](https://github.com/limaofeng/jfantasy-framework/commit/cc6a9c737729c176e7107dd2f0872725070af011))
* 解决 BusEntityInterceptor 中设置 tenantId 错误的问题 ([b5eb7fc](https://github.com/limaofeng/jfantasy-framework/commit/b5eb7fc4f48f2886c39abb03e306f985d3485c7e))
* 解决 DateFormatDirective 不支持 Optional 类型的问题 ([65b6abb](https://github.com/limaofeng/jfantasy-framework/commit/65b6abb5930ea508b63bd8233c17e50f24ff0517))
* 解决 graphql jar 冲突的问题 ([69f5f70](https://github.com/limaofeng/jfantasy-framework/commit/69f5f70ac4a9a70071d7c8d12559633913147db6))
* 解决 new WhereInput 时，出现的 空异常 ([08e23d1](https://github.com/limaofeng/jfantasy-framework/commit/08e23d19a2a949c51a950744bb3b986349725959))
* 解决枚举类型查询BUG ([19483ff](https://github.com/limaofeng/jfantasy-framework/commit/19483ff68c95afbcf1dca3f74560734710d43a9e))

### [2.11.2](https://github.com/limaofeng/jfantasy-framework/compare/v2.11.1...v2.11.2) (2024-03-04)


### Bug Fixes

* 修复枚举查询BUG ([e4e62d8](https://github.com/limaofeng/jfantasy-framework/commit/e4e62d85fd9c510e4a6e69da13c5e01a716d83cf))

### [2.11.1](https://github.com/limaofeng/jfantasy-framework/compare/v2.11.0...v2.11.1) (2023-04-13)


### Features

* token 序列化调整 uid 为 null 不序列化 ([a16496c](https://github.com/limaofeng/jfantasy-framework/commit/a16496cac8b74f14dafe40fd974526f604f2bf33))
* 任务调度优化 ([7a090b2](https://github.com/limaofeng/jfantasy-framework/commit/7a090b2bfb9717714556cdaed8e1acbb85dd057d))
* 任务调度初始化调整 ([4e0c7bf](https://github.com/limaofeng/jfantasy-framework/commit/4e0c7bf6fb345c4e60deb6cadb678a09718cee38))
* 任务调度初始化调整，避免命名冲突 ([6d67db7](https://github.com/limaofeng/jfantasy-framework/commit/6d67db7b77a43e06753db4796dc7dc7dfd21ef96))
* 任务调度初始化调整，避免命名冲突 ([372bf04](https://github.com/limaofeng/jfantasy-framework/commit/372bf04addadec7fddb5058f8de0669799fc4b46))
* 优化 DefaultBatchService ([8ca1d71](https://github.com/limaofeng/jfantasy-framework/commit/8ca1d71604876573351bdd1cdf2b0360ff54360d))
* 优化 DefaultBatchService 的线程池 ([6b385e2](https://github.com/limaofeng/jfantasy-framework/commit/6b385e2bb33dd120010e0826224235b470f44d54))
* 优化 error handler ([e9919ee](https://github.com/limaofeng/jfantasy-framework/commit/e9919ee37cb0fc600b34596ae4dfc7b8084a42de))
* 修复 CLIENT_CREDENTIALS 授权逻辑 ([f76b475](https://github.com/limaofeng/jfantasy-framework/commit/f76b475b601dfa53f0f845838ef3c9675e3150b4))
* 升级 springboot 版本 2.7.0 > 2.7.7 ([234be62](https://github.com/limaofeng/jfantasy-framework/commit/234be62a332051922df034f3bbb2769d38adf319))
* 去掉显示的 taskExecutor 改为 spring 配置 ([ad21e5c](https://github.com/limaofeng/jfantasy-framework/commit/ad21e5cd8a2a748321bb0acd628daae7d46cbc51))
* 可以设置 expiresAt ([cd1be81](https://github.com/limaofeng/jfantasy-framework/commit/cd1be812e607ca446bd71386fcec0e4283f00337))
* 批量处理服务支持运行时设置工作线程 ([e3e563c](https://github.com/limaofeng/jfantasy-framework/commit/e3e563c79484bfa638c16e0058acb69b9b0d4d57))
* 提供一个 BasePublisher 基类 ([cda7b56](https://github.com/limaofeng/jfantasy-framework/commit/cda7b5616211f86775c5b1f5c1102e599da0722f))
* 添加 defaultContentType APPLICATION_JSON_UTF8 ([4558139](https://github.com/limaofeng/jfantasy-framework/commit/45581396452145562cf1e78296b5062f94e25885))
* 添加 ListConverter 简化 hibernate convert 过程 ([e148c47](https://github.com/limaofeng/jfantasy-framework/commit/e148c479fa9f2d8ea0f2d23af6f32d9b057ef34e))


### Bug Fixes

* 修复批量处理 setWorkerNumber bug ([c7a97f4](https://github.com/limaofeng/jfantasy-framework/commit/c7a97f49340d7bea670cd0c2c5d527a94e9906a4))
* 解决 incrementRequest 冲突问题 ([45629ef](https://github.com/limaofeng/jfantasy-framework/commit/45629efa84a25616ddaa336de13e68236721498e))
* 解决 setWorkerNumber 错误 ([f6aa197](https://github.com/limaofeng/jfantasy-framework/commit/f6aa1972e2af800bf84d516bc0ce0ada365221a3))

## [2.11.0](https://github.com/limaofeng/jfantasy-framework/compare/v2.10.3...v2.11.0) (2022-12-21)


### Features

* graphql websocket 支持 ([f6ccc18](https://github.com/limaofeng/jfantasy-framework/commit/f6ccc18e0ac58d475104366b038ddf5e8a72578f))
* 自定义序列添加删除序列方法 ([3f0771f](https://github.com/limaofeng/jfantasy-framework/commit/3f0771fa07f22c0ba42cb83a200bfb46b790584d))

### [2.10.3](https://github.com/limaofeng/jfantasy-framework/compare/v2.10.2...v2.10.3) (2022-10-13)


### Features

* 工具类 ObjectUtil 新增 merge 函数 ([e96d126](https://github.com/limaofeng/jfantasy-framework/commit/e96d1269b32c4cd51f737ed5a242e8b804f46b7c))


### Bug Fixes

* asm 函数 makeEnum 入参数优化 ([ec08285](https://github.com/limaofeng/jfantasy-framework/commit/ec082856453823a3d204a2f88fa292c7ccac0cde))
* asm 函数引用类型 signature Bug 修复 ([a1d3611](https://github.com/limaofeng/jfantasy-framework/commit/a1d361102419e8af4af5d9c518a47a66063e9227))
* asm 函数支持自引用类型 ([72a4114](https://github.com/limaofeng/jfantasy-framework/commit/72a411480ad75cccb55d91269d3137e8e766c70d))
* asm 函数支持自引用类型 ([5378cd0](https://github.com/limaofeng/jfantasy-framework/commit/5378cd0d0535a1be54984227e587c034813fc465))
* asm 工具类支持类 signature ([a27a65f](https://github.com/limaofeng/jfantasy-framework/commit/a27a65fc2d3b9152c3c80c29f6644355d6ecba4f))
* asm 工具类自定义方法支持自定义 symbolTable ([9c92539](https://github.com/limaofeng/jfantasy-framework/commit/9c92539f4c39a937e9a9e3439c749d0f5e149a80))
* asm 辅助对象重构 ([962c94a](https://github.com/limaofeng/jfantasy-framework/commit/962c94ae852414e5a496e50a561877092df1c37c))
* 优化 GraphQL 事务方式 ([7b85057](https://github.com/limaofeng/jfantasy-framework/commit/7b85057e1551b18de0d65dc00dc3b7a4d9bdf494))
* 修复 ClassUtil.newInstance 对于动态 class 存在 Bug ([a626bc6](https://github.com/limaofeng/jfantasy-framework/commit/a626bc688b1d110125ceee06a6a10f4ffee6ef6f))
* 修复工具类 ObjectUtil.merge 错误 ([a34ab81](https://github.com/limaofeng/jfantasy-framework/commit/a34ab819d0da2a163fa052ff26e3a1e082841299))
* 修复自定义 OrderBy 对象的 toString 方法错误 ([2a289b6](https://github.com/limaofeng/jfantasy-framework/commit/2a289b6884ebd6889db652c8928af2e9910b1f42))
* 支持自定义 Instrumentation ([8fa1668](https://github.com/limaofeng/jfantasy-framework/commit/8fa166816b590299525a18d2975564dc601db446))
* 暴露 AsmUtil 的 loadClass 函数 ([631ec5c](https://github.com/limaofeng/jfantasy-framework/commit/631ec5c7ab812e109ff6b10fce4066a29915c84d))
* 解决 asm makeEnum 枚举个数大于 5 时的BUG ([54e96db](https://github.com/limaofeng/jfantasy-framework/commit/54e96db0bb4f3cf5e0c333f28ae9963b53eb83d0))
* 解决 ClassUtil forName 函数Bug ([c012f2e](https://github.com/limaofeng/jfantasy-framework/commit/c012f2e1b82af4ecbd26ae5abc15c1ab46913727))

### [2.10.2](https://github.com/limaofeng/jfantasy-framework/compare/v2.10.1...v2.10.2) (2022-10-01)


### Features

* 为 JPA 添加支持 offset / limit 的 findAll 方法 ([dd64fb0](https://github.com/limaofeng/jfantasy-framework/commit/dd64fb04dd2e95285bae22b985bdcb9bbeb9e925))
* 优化GraphQL授权 ([61a6f86](https://github.com/limaofeng/jfantasy-framework/commit/61a6f867936505f35bb76c1666e5a4ce4dfc7941))
* 添加 asm 工具函数 makeEnum ([00c5200](https://github.com/limaofeng/jfantasy-framework/commit/00c52003504d168277ba3bc9d770f5f9bf0023f7))


### Bug Fixes

* mybatis.mapper-locations 配置多个 ([d75978c](https://github.com/limaofeng/jfantasy-framework/commit/d75978c1fe2010943df9f08df8e17760f5ecb4ca))
* 优化 mybatis 集成 ([5ee7b6a](https://github.com/limaofeng/jfantasy-framework/commit/5ee7b6a49359e6081f341d0a4d0b4f0ba0bc2f48))
* 优化GraphQL授权 ([d7403d6](https://github.com/limaofeng/jfantasy-framework/commit/d7403d6beadcce98addbd7020af23f779444e388))
* 优化GraphQL授权 ([04c4255](https://github.com/limaofeng/jfantasy-framework/commit/04c42550332914d5bb1504f3d0cc0c4ad9d05c5b))
* 修复一些过时的写法 ([08e01ed](https://github.com/limaofeng/jfantasy-framework/commit/08e01ed4d5e606d9f54d9ea7c86c7e94e1d1c8be))
* 修复获取授权方法 ([b771fe3](https://github.com/limaofeng/jfantasy-framework/commit/b771fe3c3d6786a3a5f260f8ee7ea27e597e585d))
* 添加工具函数 WebUtil.getFullUrl ([3212ea6](https://github.com/limaofeng/jfantasy-framework/commit/3212ea6a5a8d8d0889d42d4fbee1fb9292066fd7))

### [2.10.1](https://github.com/limaofeng/jfantasy-framework/compare/v2.10.0...v2.10.1) (2022-07-25)


### Features

* 将 SecurityAutoConfiguration 改为 OAuth2SecurityAutoConfiguration ([527b733](https://github.com/limaofeng/jfantasy-framework/commit/527b733cef519cf06551c3712f1f606ee6c7935c))
* 添加 ObjectUtil.resort 函数 ([74c65f3](https://github.com/limaofeng/jfantasy-framework/commit/74c65f3f52200054af7758d3deea44093d5fed34))
* 认证密钥添加 ClientSecretType 字段，区分不同场景下的密钥 ([f10d7ff](https://github.com/limaofeng/jfantasy-framework/commit/f10d7ffda92165bcbe1dfc6e94ec40580b606012))
* 记录 client_secret 方便存储到 access_token ([77cdb23](https://github.com/limaofeng/jfantasy-framework/commit/77cdb23606d9c12e85c39eba421fc14b99394109))

## [2.10.0](https://github.com/limaofeng/jfantasy-framework/compare/v2.9.10...v2.10.0) (2022-05-27)


### Features

* ES集成, 实现排序与分页及关键词高亮 ([e97dc13](https://github.com/limaofeng/jfantasy-framework/commit/e97dc136d64b35157de10b41bd174aac1843d7f6))
* ES集成优化。更新时，如果文档不存在，则去创建文档 ([b245cee](https://github.com/limaofeng/jfantasy-framework/commit/b245cee460bc1cc1c9f8371a93c672fe84b0933b))
* graphql 空排序实现 ([e5cbece](https://github.com/limaofeng/jfantasy-framework/commit/e5cbece7652a79547e229e5bbc3b8a7a5e5b1c4a))
* jpa 分页查询使用默认的分页对象，不在使用自定义的 Pager 分页对象 ([08dc9f3](https://github.com/limaofeng/jfantasy-framework/commit/08dc9f34cfbaea0822c5b4e92d6bcd88f0e3fe4a))
* search 重新索引功能 ([b44f36b](https://github.com/limaofeng/jfantasy-framework/commit/b44f36bf95b3e140415d30f036b6722f47ec8f80))
* 为 ES 配置添加开关 ([52c42ad](https://github.com/limaofeng/jfantasy-framework/commit/52c42ad4cb2a8102678c930b48e7446a846513fe))
* 使用 hikari 取代 druid 连接池 ([39cd5f2](https://github.com/limaofeng/jfantasy-framework/commit/39cd5f2205ba6b675717127505560f00b2632aa0))
* 全文检索模块 Query 简单封装 ([cbb1aa4](https://github.com/limaofeng/jfantasy-framework/commit/cbb1aa421bd82389d40bef88a36e494ff3c53636))
* 全文检索模块添加查询接口 ([f87810d](https://github.com/limaofeng/jfantasy-framework/commit/f87810dd8ca1e8c3ffc85ba384b80133378bcc54))
* 分页对象重命名 ([3221f3a](https://github.com/limaofeng/jfantasy-framework/commit/3221f3aa865723b9474cd8bbacb83324d412e737))
* 升级 SpringBoot 2.7.0 ([28cce8e](https://github.com/limaofeng/jfantasy-framework/commit/28cce8eb64f23700607a3512886c7329afe99be1))
* 去掉 graphql 默认的 version 接口 ([6ffe29c](https://github.com/limaofeng/jfantasy-framework/commit/6ffe29c99a6ba449beb1f0a8289eae8617d462eb))
* 完成查询模块的文档同步新增/修改/删除功能 ([5c920bb](https://github.com/limaofeng/jfantasy-framework/commit/5c920bbb1537d4a820bed259ffcd4b469f4756b1))
* 将之前的 lucene 改为 search 模块 ([78dacca](https://github.com/limaofeng/jfantasy-framework/commit/78dacca5d225630ac12d0866b3ea5ea7fe187c8e))
* 将注解还原成默认名称 ([4b453bd](https://github.com/limaofeng/jfantasy-framework/commit/4b453bd0d31d2eb4d5102d65f5969adf5308212b))
* 查询模块写入 ES 逻辑优化 ([de5e29e](https://github.com/limaofeng/jfantasy-framework/commit/de5e29e5aae430c4e488bbc3bd548f332eea2acf))
* 添加 Datasource Proxy 用于开发时查看数据源信息 ([e1aa265](https://github.com/limaofeng/jfantasy-framework/commit/e1aa265bb81c27240347752e73d9c1710ff8159b))
* 添加批量提交实现 ([8bc3b53](https://github.com/limaofeng/jfantasy-framework/commit/8bc3b538e93a361630e56aaf9402110340434bab))
* 禁用 SpringBoot 内置 Tomcat 的 Session ([88285fc](https://github.com/limaofeng/jfantasy-framework/commit/88285fc9c8c8f3ea6e95e92df901030067b15d79))


### Bug Fixes

* graphql orderby 排序字段直接解析到 Sort 对象 ([5ccc0f0](https://github.com/limaofeng/jfantasy-framework/commit/5ccc0f07e8914903c472bb30d3e72c9b02b4b0f1))
* graphql 使用空对象表示 scalar 默认值的问题 ([e999ae9](https://github.com/limaofeng/jfantasy-framework/commit/e999ae90def553cb3d2b14a7c77c67849cb2f902))
* 为 connection 对象设置分页信息 ([1ffd292](https://github.com/limaofeng/jfantasy-framework/commit/1ffd292b6121001b4f6423e1a554f2aadd8fd7f0))
* 优化自定义序列缓存池大小的配置 ([c0c977f](https://github.com/limaofeng/jfantasy-framework/commit/c0c977f7d5da5275c6c1756052c21bacbd71791c))
* 修复 search 模块的额 rebuild task ([fbb391a](https://github.com/limaofeng/jfantasy-framework/commit/fbb391aa7c15b8bc0c063f8a271e882f83c6dc2e))
* 修复游标显示 ([ad04023](https://github.com/limaofeng/jfantasy-framework/commit/ad04023f188341bd4e530f9f2ab662d9768e5012))
* 分页 limit 模式时 size 判断错误 ([8f700c3](https://github.com/limaofeng/jfantasy-framework/commit/8f700c3ee678979b59d2d85b448f5e6f8c7143ff))
* 实现 copyProperties 逻辑 ([44db332](https://github.com/limaofeng/jfantasy-framework/commit/44db33270fa9ccc3c07165a79665aafd9ebb7b61))
* 将注解改成与 spring-boot-data-elasticsearch 相似 ([4a56920](https://github.com/limaofeng/jfantasy-framework/commit/4a56920de037a0a86acc0d6025202ea2c145c9a5))
* 由于 jpa 的分页是从 0 开始，返回时 +1 使其更容易理解 ([9bc2bca](https://github.com/limaofeng/jfantasy-framework/commit/9bc2bca167570bd20c7c908848af25264bdbf52d))
* 解决 AbstractChangedListener 注入问题 ([415d177](https://github.com/limaofeng/jfantasy-framework/commit/415d17770bc208f87711792da4d5463f8f03e8f7))
* 解决 Graphql 版本不匹配的问题 ([c821a5c](https://github.com/limaofeng/jfantasy-framework/commit/c821a5c20dbe5a75b6923a5cd6d294376fae56a9))
* 解决 PropertyFilter toString 优化 ([f5d3957](https://github.com/limaofeng/jfantasy-framework/commit/f5d395705f7972484955802b90dcee3dcd6bb36f))
* 解决编译问题 ([3601677](https://github.com/limaofeng/jfantasy-framework/commit/3601677a92da6ec79490e01a424f072ba062b33b))
* 解决运行测试时的包冲突 ([d198036](https://github.com/limaofeng/jfantasy-framework/commit/d1980360782251268de391891640849c4013c3bc))

### [2.9.10](https://github.com/limaofeng/jfantasy-framework/compare/v2.9.9...v2.9.10) (2022-05-08)


### Features

* 添加FFmpeg工具类，从视频中提取帧图片 ([90bbb42](https://github.com/limaofeng/jfantasy-framework/commit/90bbb426a5ac428afafc50dd1d277bc99906992c))
* 添加图片处理工具类，提供 resize 与 identify 函数 ([97b1d65](https://github.com/limaofeng/jfantasy-framework/commit/97b1d65d7d4cdf23b4690f0d0a20f86b7c4fe4a6))
* 添加图片处理工具类，提供 resize 与 identify 函数 ([f5f6f62](https://github.com/limaofeng/jfantasy-framework/commit/f5f6f628957be09cbfdd0816cb9cb0cbfec9e78f))


### Bug Fixes

* graphql date 类型支持 long 转换 ([f5fddb3](https://github.com/limaofeng/jfantasy-framework/commit/f5fddb3c1883b237c753d6e76bfe97ca82ac49c3))
* graphql 指令 argument 配置优化 ([7a9f1db](https://github.com/limaofeng/jfantasy-framework/commit/7a9f1dbbf7c2584cc522c3e143acf97a984355f9))
* TokenStore 支持传递 Details 信息 ([7c13d2b](https://github.com/limaofeng/jfantasy-framework/commit/7c13d2b8e72d13099373c669b22c7c2b0038ce81))
* 优化 UserAgent 获取设备信息逻辑 ([46fb163](https://github.com/limaofeng/jfantasy-framework/commit/46fb16339a3d77301f77c983c018375bc27349ac))
* 修复时间序列号的BUG ([92ccaeb](https://github.com/limaofeng/jfantasy-framework/commit/92ccaeb33f863df1a72a5ec4889c692411b55c3a))
* 工具类 SpringSecurityUtils 添加返回授权对象方法 ([02e307e](https://github.com/limaofeng/jfantasy-framework/commit/02e307e23a9c3c723d0b02ac2ff0bcb63e579fbb))
* 获取 IP 的逻辑 ([194a9a6](https://github.com/limaofeng/jfantasy-framework/commit/194a9a6243edbb34abaacb1eff6effc58d2817f3))
* 解决 ObjectUtil recursive 如果返回 NULL 会自动删除改字段 ([6cfc65c](https://github.com/limaofeng/jfantasy-framework/commit/6cfc65c06958157c7f581fe3e20bc9774857566e))
* 解决 recursive 函数 "cannot be cast to " 的错误 ([5ab8949](https://github.com/limaofeng/jfantasy-framework/commit/5ab894939ee91effe325d411bafeb539fe7738d9))
* 解决 SequenceService 注入问题 ([3d31426](https://github.com/limaofeng/jfantasy-framework/commit/3d314268b9d42bf09271e91fe398855947b5eccb))
* 读取 DateFormat 时的错误 ([ef0c7f7](https://github.com/limaofeng/jfantasy-framework/commit/ef0c7f76011322916295f2d9febfeca8fd7c614c))

### [2.9.9](https://github.com/limaofeng/jfantasy-framework/compare/v2.9.8...v2.9.9) (2022-04-06)

### [2.9.8](https://github.com/limaofeng/jfantasy-framework/compare/v2.9.7...v2.9.8) (2022-04-06)


### Bug Fixes

* cors allowed headers 修改 ([fde7500](https://github.com/limaofeng/jfantasy-framework/commit/fde75008942bf0588c7b2bccd3a06807305aad5e))
* cors 暴露 Content-Range 信息 ([7bf1703](https://github.com/limaofeng/jfantasy-framework/commit/7bf17031c8ba7aef3296d61f25f3ebb7d602f696))
* 优化 getExtension 逻辑 ([b803a85](https://github.com/limaofeng/jfantasy-framework/commit/b803a853f2893aa318cd5d76571ca6471ce7e707))
* 优化 ServletUtils 中的方法 ([ed99310](https://github.com/limaofeng/jfantasy-framework/commit/ed99310a007acadf40015b26fb404848f9840a15))
* 修复 FileUtil getMimeType 方法 ([3b4f6ff](https://github.com/limaofeng/jfantasy-framework/commit/3b4f6ffdaf8c884fdd0e0449099381b5f31d9931))
* 修复 HibernateUtils 中，联合主键配置时, 获取 IdClass 的 BUG ([d0caa98](https://github.com/limaofeng/jfantasy-framework/commit/d0caa989286e660ea760295cef0a06e1b25db9bc))
* 修复 MatchType 匹配 BUG ([284d353](https://github.com/limaofeng/jfantasy-framework/commit/284d353ae7acba8a045d144c4ca19ea3faf7ab72))
* 修复筛选 Not in BUG ([02ee204](https://github.com/limaofeng/jfantasy-framework/commit/02ee204b45f3e91951e1d6fdcce3668501a28e36))
* 修正对范围请求的错误理解 ([8f7cab5](https://github.com/limaofeng/jfantasy-framework/commit/8f7cab56ac84aaaf28f2d438e08570a37110d003))
* 全局异常处理只处理部分异常 ([7cd2056](https://github.com/limaofeng/jfantasy-framework/commit/7cd2056c7919a9b58f680e81125a0efab2b89bf8))
* 添加浏览器缓存检查方法 ([8d6b094](https://github.com/limaofeng/jfantasy-framework/commit/8d6b094eeec1cb06918adc94a606688ee75d6a24))
* 解决 DataBaseKeyGenerator 注入 Bug ([400842d](https://github.com/limaofeng/jfantasy-framework/commit/400842d632688886e46d13308c8c53092af1a381))
* 长连接判断添加 Range 设置 ([51078e0](https://github.com/limaofeng/jfantasy-framework/commit/51078e04e4018683d7e3dad1c82f3553542b9eb8))

### [2.9.7](https://github.com/limaofeng/jfantasy-framework/compare/v2.9.6...v2.9.7) (2022-03-06)


### Features

* 分页对象优化 ([b754218](https://github.com/limaofeng/jfantasy-framework/commit/b7542183be04a61ec829d584023ba47ab4cc7e90))
* 可以通过 AuthorizationGraphQLServletContext 共享数据 ([dabfb74](https://github.com/limaofeng/jfantasy-framework/commit/dabfb74d181b72fa6d4c28df07636bc1a27f983a))
* 日期格式处理 DateConverter ([c7b4f58](https://github.com/limaofeng/jfantasy-framework/commit/c7b4f58472c4c6ac0935ed1676d2035f31212564))
* 日期格式处理 DateConverter ([647d2fb](https://github.com/limaofeng/jfantasy-framework/commit/647d2fb9b87dbde6feeb2f938396cd6f3d418aa6))
* 逻辑删除优化 ([f8bdf32](https://github.com/limaofeng/jfantasy-framework/commit/f8bdf3277ea8d47e5c2fd67a7a7412b1781efb8e))


### Bug Fixes

* getPropertyValue 转换 cast 会有问题 ([b0d770d](https://github.com/limaofeng/jfantasy-framework/commit/b0d770d4740304038361c8fd474cf92f062bad14))
* 使用默认的 MultipartConfigElement 配置上传逻辑 ([762e7ff](https://github.com/limaofeng/jfantasy-framework/commit/762e7ff3cd2ea76ff5a2511ff1381882a77235f6))
* 修复 findAll size <= 0 的问题 ([f2bce89](https://github.com/limaofeng/jfantasy-framework/commit/f2bce898f86703b4e04e3a439e4d525323d0dc28))
* 修复上个版本启动出错的问题 ([aeadf5c](https://github.com/limaofeng/jfantasy-framework/commit/aeadf5c01e43e6f93e754fd332af1c3cc85b252f))
* 修复多层关联 ([63c4075](https://github.com/limaofeng/jfantasy-framework/commit/63c40759662e6cce7926ef47b92910a4d8be37e7))
* 日期工具类，添加 betweenDates, diff 方法 ([79eb41d](https://github.com/limaofeng/jfantasy-framework/commit/79eb41d758d310370eb1384aa973b8bcb7e3cf74))
* 日期转换工具类，添加新格式 ([5edfbf2](https://github.com/limaofeng/jfantasy-framework/commit/5edfbf2f50a176c4ee74f028bde023e3950ccc8a))
* 日期转换工具类，添加新格式 ([10ad075](https://github.com/limaofeng/jfantasy-framework/commit/10ad0753bed58cbf24e1101e5118f6711515c72d))
* 添加 Pager.newPager(size, orderBy, first) 方法 ([24455da](https://github.com/limaofeng/jfantasy-framework/commit/24455da80ebae088dca641382732c5414ed64421))

### [2.9.6](https://github.com/limaofeng/jfantasy-framework/compare/v2.9.5...v2.9.6) (2021-12-31)


### Features

* 扩展 JpaRepository 方法 ([c84587e](https://github.com/limaofeng/jfantasy-framework/commit/c84587e177d0842ea94c6b805688ebb18635cd81))
* 添加 FileSize 指令 ([a2fd666](https://github.com/limaofeng/jfantasy-framework/commit/a2fd6661f6586d56b43bab8659a70c69bd520202))
* 添加 NumberFormat 指令 ([1a74bfd](https://github.com/limaofeng/jfantasy-framework/commit/1a74bfd6496e5c06c24c3d691f10de007990b694))


### Bug Fixes

* 解决 FileSize 指令 format 为 NULL 的问题 ([5e7ba11](https://github.com/limaofeng/jfantasy-framework/commit/5e7ba11e0395b9eb27bf0ee6a9cb243032d89113))
* 解决 ObjectUtil.compare 比较 BUG ([659a491](https://github.com/limaofeng/jfantasy-framework/commit/659a4919891d1616cee5ff94b5cbfc42f413084b))

### [2.9.5](https://github.com/limaofeng/jfantasy-framework/compare/v2.9.4...v2.9.5) (2021-10-13)


### Features

* recursive 优化 ([0d5deeb](https://github.com/limaofeng/jfantasy-framework/commit/0d5deebe1d53375c57dcc2adbc49956b342ab0a4))
* 工具类修改 ([2cd0560](https://github.com/limaofeng/jfantasy-framework/commit/2cd0560b8c39b5ddfc15093441164906870ed729))
* 添加工具方法 recursive ([9bb5b2c](https://github.com/limaofeng/jfantasy-framework/commit/9bb5b2ce24d650d17b574fb30b75640321197e9f))
* 添加集合比较方法 compare 实现差异比较 ([3b85f75](https://github.com/limaofeng/jfantasy-framework/commit/3b85f7516c400011cda61d272890af4cf6b3ed58))


### Bug Fixes

* 修复 ObjectUtil.copy 方法泛型错误 ([8288186](https://github.com/limaofeng/jfantasy-framework/commit/82881861b1497884d56aaa6ab436412c9c34e1e4))
* 如果 token 验证出错, 直接抛出异常 ([f666e50](https://github.com/limaofeng/jfantasy-framework/commit/f666e5059a8183f405040c8d7e8a5917067c70fa))
* 实体公共属性，创建人与修改人不在默认为 0L ([d972996](https://github.com/limaofeng/jfantasy-framework/commit/d9729966ca2adb58abc8a580efc14f663f7b455b))
* 解决 token 失效时，请求失败问题 ([e4b03ba](https://github.com/limaofeng/jfantasy-framework/commit/e4b03ba891ba15f22fe58ae578d39cfc31473573))
* 解决排序设置BUG ([5fdca75](https://github.com/limaofeng/jfantasy-framework/commit/5fdca755e3d43e9402219c79952207f3a9d35e53))

### [2.9.4](https://github.com/limaofeng/jfantasy-framework/compare/v2.9.3...v2.9.4) (2021-09-13)


### Bug Fixes

* 解决 GraphQL 异步情况下，获取授权错误 ([dd739a2](https://github.com/limaofeng/jfantasy-framework/commit/dd739a2ff8801ff259ace3459b2235c9debcc7e5))

### [2.9.3](https://github.com/limaofeng/jfantasy-framework/compare/v2.9.2...v2.9.3) (2021-09-13)


### Bug Fixes

* 解决 javadoc: InterceptorBinding not found ([141f035](https://github.com/limaofeng/jfantasy-framework/commit/141f035321547de220e9ac1fa07da7fcd19d2f08))

### [2.9.2](https://github.com/limaofeng/jfantasy-framework/compare/v2.9.1...v2.9.2) (2021-09-13)


### Bug Fixes

* 实现类型 OpenSessionInView 的效果 ([fbd0ee6](https://github.com/limaofeng/jfantasy-framework/commit/fbd0ee6e96be1cda61a05c3ec29394a0a97bc2dd))
* 解决 GraphQLContext 构建时的 no session 问题 ([b365adb](https://github.com/limaofeng/jfantasy-framework/commit/b365adbdabf6267f4863a83accded716765d507b))

### [2.9.1](https://github.com/limaofeng/jfantasy-framework/compare/v2.9.0...v2.9.1) (2021-09-11)


### Bug Fixes

* Operation 由 class 改为 interface ([c7a0842](https://github.com/limaofeng/jfantasy-framework/commit/c7a08422b83238d7831a1d5c1c9dba076ce7b268))

## [2.9.0](https://github.com/limaofeng/jfantasy-framework/compare/v2.8.9...v2.9.0) (2021-09-11)


### Features

* 集成 lombok-mapstruct-binding 插件 ([0418cbb](https://github.com/limaofeng/jfantasy-framework/commit/0418cbb58dfc05c3fbd7a2314879cfbe593810f8))

### [2.8.9](https://github.com/limaofeng/jfantasy-framework/compare/v2.8.8...v2.8.9) (2021-09-08)


### Features

* 优化验证 ([98f4802](https://github.com/limaofeng/jfantasy-framework/commit/98f4802502066957f6f7953c2dae5cd1f47fe9c5))

### [2.8.8](https://github.com/limaofeng/jfantasy-framework/compare/v2.8.7...v2.8.8) (2021-09-06)

### [2.8.7](https://github.com/limaofeng/jfantasy-framework/compare/v2.8.6...v2.8.7) (2021-08-15)

### [2.8.6](https://github.com/limaofeng/jfantasy-framework/compare/v2.8.5...v2.8.6) (2021-08-15)

### [2.8.5](https://github.com/limaofeng/jfantasy-framework/compare/v2.8.4...v2.8.5) (2021-08-14)

### [2.8.4](https://github.com/limaofeng/jfantasy-framework/compare/v2.8.3...v2.8.4) (2021-08-14)

### [2.8.3](https://github.com/limaofeng/jfantasy-framework/compare/v2.8.2...v2.8.3) (2021-08-14)

### [2.8.1](https://github.com/limaofeng/jfantasy-framework/compare/v2.7.2...v2.8.2) (2021-08-14)

### Bug Fixes

* 集成 standard-version ([5583433](https://github.com/limaofeng/jfantasy-framework/commit/5583433c068329f3699b7bf174bec7ae1c9df262))