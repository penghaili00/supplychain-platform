# SupplyChain Platform

SupplyChain Platform 是一个面向企业采购场景的 `B2B 供应链订单履约平台`。项目以 `客户下单 -> 订单审核 -> 锁库存 -> 分仓发货 -> 确认收货 -> 对账结算 -> 售后处理 -> 采购补货` 为长期业务主链，当前阶段聚焦一期建设，优先打通真实可演示的订单履约闭环。

系统基于 `JDK 21 + Spring Boot 3 + Spring Cloud Alibaba + Dubbo + MyBatis-Plus + Redis + MySQL + RabbitMQ + Nacos` 构建，采用 `gateway + admin + api + service-provider + task + mq` 的微服务结构，服务采购客户、运营、仓库、财务和采购等多角色协同场景。

## 项目定位

- 面向企业采购交易与履约协同，而不是普通零售商城
- 聚焦订单履约主链，逐步扩展至售后、结算、采购和分析能力
- 同时提供采购客户 App 端与后台管理端能力
- 以 Nacos 作为统一配置中心，以 Dubbo 作为内部服务调用协议
- 以 MySQL 持久化主数据，以 Redis 承担缓存、会话、锁与幂等控制

## 一期目标

一期目标是交付一个最小但完整的 B2B 履约闭环，让系统能够真实完成一笔采购订单的全链路流转。

一期重点包括：

- 客户管理
- 商品与 SKU 管理
- 基础价格策略
- 客户下单
- 订单审核
- 库存锁定
- 基础分仓
- 出库发货
- 确认收货
- 订单搜索与轨迹
- 审计日志
- 超时取消与自动确认收货任务

## 核心业务主链

```text
客户下单
  -> 订单审核
  -> 锁库存
  -> 分仓分配
  -> 出库发货
  -> 确认收货
  -> 对账结算
  -> 售后处理
  -> 采购补货
```

当前代码与文档建设优先覆盖前半段，也就是订单、库存、仓库履约这条主线。

## 业务角色

- `采购客户`：浏览商品、提交订单、查单、确认收货
- `销售 / 运营`：审核订单、改价、维护客户和价格策略
- `仓库人员`：出库、发货、录入物流单号
- `财务人员`：后续承接对账、回款、发票等能力
- `采购专员`：后续承接采购申请、到货、补货等能力
- `平台管理员`：维护主数据、权限、配置和审计

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
- `Elasticsearch`

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
- `supplychain-service-api`：Dubbo 接口、DTO、命令对象、视图对象
- `supplychain-service-provider`：订单、库存、履约、鉴权、审计等核心业务实现
- `supplychain-admin`：后台管理端 HTTP 应用
- `supplychain-api`：采购客户 App / 客户端 HTTP 应用
- `supplychain-gateway`：统一入口、鉴权、路由、链路透传
- `supplychain-task`：超时取消、自动确认收货等定时任务
- `supplychain-mq`：订单事件、通知事件、索引同步等异步能力承载模块
- `nacos`：发布到 Nacos 的配置源文件
- `docs`：项目设计、开发、部署文档
- `sql`：数据库初始化脚本

## 当前开发阶段

当前项目处于“业务骨架已迁移、一期方案开始细化”的阶段：

- 已完成项目命名与工程结构迁移
- 已建立后台端、App 端、网关、Dubbo、Nacos、Redis、MySQL 基础骨架
- 已沉淀 B2B 供应链订单履约平台总体设计方案
- 正在补齐一期开发草案、数据模型、状态机和履约细节设计

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

- 所有新增能力优先围绕订单履约主链推进，不做无主线扩展
- 后台用户与 App 用户必须使用不同表，不允许复用
- App 登录链路继续保留验签、盐值哈希与 Redis 风控能力
- 订单、库存、履约、售后、结算状态分开建模，避免语义混杂
- 关键动作必须保留审计轨迹，包括审核、改价、锁库、出库、发货、确认收货
- 所有新增实体、DTO、接口模型需要补中文注释
- 日志、异常、提示信息优先使用中文
- 所有文本文件统一使用 `UTF-8`，且不得带 `BOM`

## 文档索引

- 设计文档：[B2B 供应链订单履约平台设计方案](./docs/design/B2B供应链订单履约平台设计方案.md)
- 一期开发草案：[一期开发草案](./docs/design/一期开发草案.md)
- 一期数据模型草案：[一期数据模型草案](./docs/design/一期数据模型草案.md)
- 一期建表 SQL 草案：[一期建表 SQL 草案](./docs/design/一期建表SQL草案.md)
- 一期后台菜单结构草案：[一期后台菜单结构草案](./docs/design/一期后台菜单结构草案.md)
- 一期项目结构层级草案：[一期项目结构层级草案](./docs/design/一期项目结构层级草案.md)
- 订单状态机说明：[订单状态机说明](./docs/design/订单状态机说明.md)
- 库存与分仓设计说明：[库存与分仓设计说明](./docs/design/库存与分仓设计说明.md)
- 出库发货设计说明：[出库发货设计说明](./docs/design/出库发货设计说明.md)
- App 登录与风控说明：[app-auth.md](docs/app端登录验签.md)
- 本地运行说明：[local-run.md](docs/本地运行说明.md)
- WSL / 远程部署说明：[deploy-guide.md](docs/部署说明.md)
- 开发文档：[development-guide.md](docs/开发文档.md)
- 开发规范：[development-standards.md](docs/开发规范.md)
- 工程基线说明：[engineering-baseline.md](docs/工程基线说明.md)
- Nacos 配置说明：[README.md](./nacos/README.md)

## 后续路线

- 继续细化一期数据模型
- 细化库存与分仓规则
- 补充库存与分仓设计说明
- 补充出库发货设计说明
- 继续沉淀接口与建表草案
- 逐步把一期业务骨架落到代码与 SQL

## 当前已知运行约束

- `supplychain-service-provider` 启动依赖 MySQL 账户可用
- `supplychain-admin`、`supplychain-api`、`supplychain-task` 依赖 `supplychain-service-provider` 提供 Dubbo 服务
- `supplychain-mq` 依赖 RabbitMQ，可按需启用
- 若 Nacos 中缺少共享 Data ID，模块会直接启动失败
