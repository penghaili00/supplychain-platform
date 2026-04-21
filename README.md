# SupplyChain

SupplyChain 是一个基于 `JDK 21 + Spring Boot 3 + Spring Cloud Alibaba + Dubbo + MyBatis-Plus + Redis + MySQL + RabbitMQ + Nacos` 的微服务脚手架项目，当前已经完成后台端与 App 端基础链路、统一网关、Dubbo 服务拆分、纯 Nacos 配置管理，以及 App 端独立用户体系与登录风控骨架。

## 项目定位

- 面向后台管理端和 App 端的双端微服务基础工程
- 使用 `gateway + admin + api + service-provider + task + mq` 的模块化结构
- 以 Nacos 作为唯一运行时配置中心
- 以 Dubbo 作为内部服务调用协议
- 以 MySQL 持久化业务主数据，以 Redis 承担会话、缓存与风控计数

## 核心能力

- 后台端与 App 端用户彻底分离
- 后台用户使用 `sys_user`
- App 用户使用 `app_user`
- App 端登录支持验签、盐值哈希、失败次数锁定、IP 封禁
- 后台端与 App 端统一使用 JWT 双 Token 模式
- 统一网关鉴权与路由转发
- 角色、菜单、权限、操作日志基础骨架
- 共享配置与模块配置拆分到 Nacos Data ID

## 技术栈

- `JDK 21`
- `Spring Boot 3.2.9`
- `Spring Cloud 2023.0.3`
- `Spring Cloud Alibaba 2023.0.3.3`
- `Apache Dubbo 3.3.6`
- `MyBatis-Plus 3.5.14`
- `Druid 1.2.27`
- `Redis`
- `MySQL 8`
- `RabbitMQ`
- `Nacos`

## 工程结构

```text
SupplyChain
├─ supplychain-common
│  ├─ supplychain-common-core
│  ├─ supplychain-common-mybatis
│  ├─ supplychain-common-security
│  └─ supplychain-common-web
├─ supplychain-service
│  ├─ supplychain-service-api
│  └─ supplychain-service-provider
├─ supplychain-admin
├─ supplychain-api
├─ supplychain-gateway
├─ supplychain-task
├─ supplychain-mq
├─ nacos
├─ docs
└─ sql
```

各模块职责：

- `supplychain-common`：公共领域对象、MyBatis 支撑、安全与 Web 基础能力
- `supplychain-service-api`：Dubbo 接口、DTO、视图对象
- `supplychain-service-provider`：核心业务实现，按功能域拆包
- `supplychain-admin`：后台管理端 HTTP 应用
- `supplychain-api`：App 端 HTTP 应用
- `supplychain-gateway`：网关、鉴权、统一入口
- `supplychain-task`：定时任务
- `supplychain-mq`：消息队列消费者与扩展能力
- `nacos`：发布到 Nacos 的配置源文件
- `docs`：项目文档
- `sql`：数据库初始化脚本

## 当前包结构约定

`supplychain-service-provider` 采用按功能域拆包的方式组织代码，当前主要包含：

- `admin.user`
- `app.user`
- `auth`
- `audit`
- `rbac`
- `demo`

不再推荐继续使用旧的平铺式 `dubbo / entity / mapper / service` 目录承载新增代码。

## 配置策略

项目当前采用纯 Nacos 配置模式：

- 运行时业务配置只从 Nacos 读取
- 本地 `application.yml` 只保留应用名、Nacos 连接参数和 `optional:nacos:` 导入
- 仓库中的 `nacos/*.yml` 是发布源，不是运行时读取源

共享 Data ID：

- `datasource.yml`
- `redis.yml`
- `mybatis-plus.yml`
- `dubbo-provider.yml`
- `dubbo-consumer.yml`
- `jwt.yml`
- `management.yml`

模块 Data ID：

- `supplychain-admin.yml`
- `supplychain-api.yml`
- `supplychain-gateway.yml`
- `supplychain-mq.yml`
- `supplychain-service-provider.yml`
- `supplychain-task.yml`

## 快速开始

### 1. 准备依赖环境

- MySQL
- Redis
- Nacos
- RabbitMQ

### 2. 初始化数据库

执行脚本 [supplychain_mysql.sql](./sql/supplychain_mysql.sql)。

初始化账号：

- 后台账号：`admin / Admin@123`
- App 账号：`demo / Demo@123`

### 3. 配置环境变量

本地默认环境入口已经统一为 `deploy/env/local.env`。

建议先复制模板：

```bash
cp deploy/env/local.env.example deploy/env/local.env
cp deploy/env/dev.env.example deploy/env/dev.env
cp deploy/env/prod.env.example deploy/env/prod.env
```

至少需要在对应环境文件里准备：

- `SUPPLYCHAIN_MYSQL_HOST`
- `SUPPLYCHAIN_MYSQL_PORT`
- `SUPPLYCHAIN_MYSQL_USERNAME`
- `SUPPLYCHAIN_MYSQL_PASSWORD`
- `SUPPLYCHAIN_REDIS_HOST`
- `SUPPLYCHAIN_REDIS_PORT`
- `SUPPLYCHAIN_REDIS_PASSWORD`
- `SUPPLYCHAIN_REDIS_DATABASE`
- `SUPPLYCHAIN_NACOS_ADDR`
- `SUPPLYCHAIN_NACOS_USERNAME`
- `SUPPLYCHAIN_NACOS_PASSWORD`
- `SUPPLYCHAIN_NACOS_NAMESPACE`
- `SUPPLYCHAIN_NACOS_GROUP`
- `SUPPLYCHAIN_JWT_SECRET`

### 4. 导入 Nacos 配置

将 [nacos](./nacos) 目录下的所有 YAML 文件导入同一个 Nacos `namespace` 和 `group`。

### 5. 打包与启动

在项目根目录执行：

```bash
./scripts/linux/bootstrap.sh
./scripts/linux/build.sh
./scripts/linux/start-all.sh
```

推荐启动顺序：

1. `supplychain-service-provider`
2. `supplychain-admin`
3. `supplychain-api`
4. `supplychain-gateway`
5. `supplychain-task`
6. `supplychain-mq`

## 重要设计约束

- 后台用户与 App 用户必须使用不同表，不允许复用
- App 登录必须保留验签、盐值哈希与 Redis 风控链路
- 所有新增实体、DTO、接口模型需要补中文注释
- 日志、异常、提示信息优先使用中文
- 所有文本文件统一使用 `UTF-8`，且不得带 `BOM`

## 文档索引

- App 登录与风控说明：[app-auth.md](docs/app端登录验签.md)
- 本地运行说明：[local-run.md](docs/本地运行说明.md)
- WSL / 远程部署说明：[deploy-guide.md](docs/部署说明.md)
- 开发文档：[development-guide.md](docs/开发文档.md)
- 开发规范：[development-standards.md](docs/开发规范.md)
- Nacos 配置说明：[README.md](./nacos/README.md)
- 设计文档：[B2B 供应链订单履约平台设计方案](./docs/design/B2B供应链订单履约平台设计方案.md)

## 当前已知运行约束

- `supplychain-service-provider` 启动依赖 MySQL 账户可用
- `supplychain-admin`、`supplychain-api`、`supplychain-task` 依赖 `supplychain-service-provider` 提供 Dubbo 服务
- `supplychain-mq` 依赖 RabbitMQ，可按需启用
- 若 Nacos 中缺少共享 Data ID，模块会直接启动失败
