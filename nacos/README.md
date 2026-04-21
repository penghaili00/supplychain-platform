# SupplyChain Nacos Config

These files are prepared for direct import into Nacos.
The application runtime no longer reads these local files directly.

Recommended import settings:

- Group: `DEFAULT_GROUP`
- Namespace: `supplychain_local`
- Type: `YAML`
- Data ID: use the exact file name

Important:

- If your Nacos namespace was created with custom `namespaceId=supplychain_local`, the current app defaults will work directly
- If the namespace display name is `supplychain_local` but the real `namespaceId` is a generated UUID, set `SUPPLYCHAIN_NACOS_NAMESPACE` to that UUID in the app environment

Files included:

- `datasource.yml`
- `elasticsearch.yml`
- `redis.yml`
- `mybatis-plus.yml`
- `dubbo-provider.yml`
- `dubbo-consumer.yml`
- `jwt.yml`
- `management.yml`
- `supplychain-service-provider.yml`
- `supplychain-admin.yml`
- `supplychain-api.yml`
- `supplychain-gateway.yml`
- `supplychain-task.yml`
- `supplychain-mq.yml`

Notes:

- The current project keeps each module `application.yml` only for application name, Nacos connection bootstrap, and `optional:nacos:` imports
- Shared business configuration is split into `datasource.yml`, `elasticsearch.yml`, `redis.yml`, `mybatis-plus.yml`, `dubbo-provider.yml`, `dubbo-consumer.yml`, `jwt.yml`, and `management.yml`
- The current project reads runtime business configuration only from Nacos
- Keep the Data ID exactly the same as the Spring application name plus `.yml`
- `supplychain-service-provider.yml` now keeps only provider-specific settings such as port and app auth rules
- `supplychain-admin.yml`, `supplychain-api.yml`, and `supplychain-gateway.yml` reuse the shared JWT config from `jwt.yml`
- `supplychain-mq.yml` keeps RabbitMQ disabled by default because RabbitMQ was not detected locally

Local values used in this config set:

- MySQL host/port: `127.0.0.1:3306`
- Redis host/port: `127.0.0.1:6379`
- Nacos host/port: `127.0.0.1:8848`
- Nacos namespace: `supplychain_local`
- Dubbo registry: `nacos://127.0.0.1:8848`
- RabbitMQ host/port: `127.0.0.1:5672`

Before running:

- Start MySQL
- Start Redis
- Start Nacos
- Import all shared and module-specific YAML files in this directory into the same Nacos namespace and group
- Import `sql/supplychain_mysql.sql`
- Install RabbitMQ if you want to enable the MQ module
