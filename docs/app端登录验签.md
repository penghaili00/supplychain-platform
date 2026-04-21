# App 端登录与风控说明

## 设计说明

- 后台用户使用 `sys_user` 表，角色关系使用 `sys_user_role`
- App 端用户使用 `app_user` 表，角色关系使用 `app_user_role`
- 风控阈值等配置放在 Nacos 的 `supplychain-service-provider.yml` 中
- 登录失败计数、锁定状态、防重放 nonce 放在 Redis 中

这样拆分的原因是：

- 用户主数据需要强一致和可审计，适合放 MySQL
- 风控阈值是运维配置，适合放 Nacos 动态调整
- 登录失败计数和临时封禁是高频、带 TTL 的运行时状态，适合放 Redis

## App 登录验签

App 登录接口仍然是 `POST /api/auth/login`，但请求体新增以下字段：

- `username`
- `password`
- `timestamp`
- `nonce`
- `signature`

签名串拼接规则：

```text
username + "\n" + password + "\n" + timestamp + "\n" + nonce
```

签名算法：

- `HmacSHA256`
- 密钥来自 `supplychain.security.app-auth.sign-secret`
- 输出十六进制小写字符串

## 密码存储

- App 用户密码不再与后台共用一套表
- `app_user.password_salt` 存储 Base64 盐值
- `app_user.password_hash` 存储 `PBKDF2WithHmacSHA256` 结果
- 默认参数：
  - 迭代次数 `120000`
  - 输出长度 `32` 字节

## 默认风控规则

账号维度：

- 30 分钟内失败 5 次，锁定 10 分钟
- 2 小时内失败 15 次，锁定 1 小时
- 1 天内失败 20 次，锁定 1 天

IP 维度：

- 1 天内失败 30 次，锁定 1 天

说明：

- 你原始需求里“2 小时内失败 15 次封号 1”缺少单位，我这里按“锁定 1 小时”实现
- 这些阈值都已经做成可配置项，你可以直接改 Nacos

## Nacos 配置项

```yaml
supplychain:
  security:
    app-auth:
      sign-secret: SupplyChain-App-Sign-Secret-Please-Change-Now-2026
      allowed-timestamp-skew: 5m
      nonce-ttl: 5m
      password:
        iterations: 120000
        hash-bytes: 32
      account-failure-rules:
        - window: 30m
          threshold: 5
          lock-duration: 10m
        - window: 2h
          threshold: 15
          lock-duration: 1h
        - window: 1d
          threshold: 20
          lock-duration: 1d
      ip-failure-rules:
        - window: 1d
          threshold: 30
          lock-duration: 1d
```

## 初始化账号

初始化 SQL 已提供：

- 后台账号：`admin / Admin@123`
- App 账号：`demo / Demo@123`
