# Changelog

All notable changes to this project will be documented in this file. See [standard-version](https://github.com/conventional-changelog/standard-version) for commit guidelines.

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