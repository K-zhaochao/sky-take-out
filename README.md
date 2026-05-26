# 苍穹外卖 (Sky Take-Out)

基于 **Spring Boot 2.7.3 + JDK 17 + MyBatis + MySQL + Redis** 的外卖点餐管理系统，包含管理端（商家后台）和用户端（小程序/H5）的双端支持。

---

## 🚀 技术栈

| 技术                 | 版本        | 说明                   |
| -------------------- | ----------- | ---------------------- |
| Spring Boot          | 2.7.3       | 核心框架               |
| JDK                  | 17          | 运行环境               |
| MyBatis              | 2.2.0       | ORM 持久层框架         |
| MySQL                | 8.x         | 关系型数据库           |
| Redis                | —           | 缓存与分布式数据存储   |
| Druid                | 1.2.1       | 数据库连接池           |
| Knife4j              | 3.0.2       | API 文档与 Swagger UI  |
| JWT (jjwt)           | 0.9.1       | 令牌鉴权               |
| PageHelper           | 1.3.0       | 分页插件               |
| Fastjson             | 1.2.76      | JSON 处理              |
| Lombok               | 1.18.30     | 简化 Java 代码         |
| Apache POI           | 3.16        | Excel 报表导出         |
| 阿里云 OSS           | 3.10.2      | 云存储（图片上传）     |
| 微信支付 APIv3       | 0.4.8       | 微信支付集成           |
| WebSocket            | —           | 实时消息推送           |
| Spring AOP           | 1.9.4       | 切面编程（日志/权限）  |
| Spring Cache         | —           | 缓存注解支持           |
| Spring Scheduling    | —           | 定时任务调度           |

---

## 📁 项目结构

```
sky-take-out/
├── sky-common/        公共模块 — 常量、枚举、异常、工具类、JSON、返回结果封装
├── sky-pojo/          实体模块 — Entity、DTO、VO
└── sky-server/        服务模块 — 控制器、业务层、数据层、配置、切面、拦截器
    └── src/main/java/com/sky/
        ├── annotation/      自定义注解
        ├── aspect/          AOP 切面
        ├── config/          配置类
        ├── controller/
        │   ├── admin/       管理端 API
        │   ├── user/        用户端 API
        │   └── notify/      第三方回调（微信支付等）
        ├── handler/         处理器
        ├── interceptor/     拦截器
        ├── mapper/          MyBatis 映射接口
        ├── service/         业务逻辑层
        ├── task/            定时任务
        └── websocket/       WebSocket 服务
```

---

## ✨ 功能模块

### 🔧 管理端（Admin）

| 模块         | 功能描述                         |
| ------------ | -------------------------------- |
| 员工管理     | 登录/登出、CRUD、权限控制        |
| 分类管理     | 菜品/套餐分类的增删改查           |
| 菜品管理     | 菜品 CRUD、图片上传（OSS）、口味管理 |
| 套餐管理     | 套餐 CRUD、关联菜品管理          |
| 订单管理     | 订单查询、状态流转、取消/接单/派送 |
| 报表统计     | 营业额、用户、订单等数据统计与导出 |
| 店铺管理     | 店铺信息设置、营业状态管理       |

### 📱 用户端（User）

| 模块         | 功能描述                         |
| ------------ | -------------------------------- |
| 微信登录     | 微信授权登录获取 JWT 令牌        |
| 菜品浏览     | 分类浏览菜品、查看详情           |
| 套餐浏览     | 套餐列表查看                     |
| 购物车       | 菜品/套餐加入购物车、数量管理    |
| 下单支付     | 创建订单、微信支付               |
| 地址管理     | 收货地址增删改查                 |
| 订单查询     | 历史订单查看、订单状态跟踪       |

### 📢 通知与回调

| 模块         | 功能描述                         |
| ------------ | -------------------------------- |
| 微信支付回调 | 接收支付结果通知，更新订单状态   |
| WebSocket    | 客户端实时消息推送（订单提醒等） |

---

## 🛠️ 快速开始

### 环境要求

- **JDK 17+**
- **Maven 3.6+**
- **MySQL 8.0+**
- **Redis**（Windows 可用 Memurai 或 WSL 运行）

### 1. 克隆项目

```bash
git clone <repository-url>
cd sky-take-out
```

### 2. 初始化数据库

在 MySQL 中创建数据库并导入初始脚本：

```sql
CREATE DATABASE sky_take_out DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. 修改配置

编辑 `sky-server/src/main/resources/application-dev.yml`，配置数据库、Redis、JWT、阿里云 OSS、微信支付等参数：

```yaml
sky:
  datasource:
    host: localhost          # 数据库地址
    port: 3306               # 数据库端口
    database: sky_take_out   # 数据库名
    username: your_username  # 用户名
    password: your_password  # 密码
  redis:
    host: localhost
    port: 6379
    password: your_password
  alioss:
    endpoint: oss-cn-xxx.aliyuncs.com
    access-key-id: your-key
    access-key-secret: your-secret
```

### 4. 编译运行

```bash
# 在项目根目录执行
mvn clean install -DskipTests

# 启动服务
cd sky-server
mvn spring-boot:run
```

服务启动后访问：`http://localhost:8080`

### 5. API 文档

启动后访问 Knife4j/Swagger 文档：

- **管理端接口文档**：`http://localhost:8080/doc.html`

---

## 🏗️ 打包部署

```bash
mvn clean package -DskipTests
```

生成的 JAR 包位于 `sky-server/target/sky-server-1.0-SNAPSHOT.jar`，可直接运行：

```bash
java -jar sky-server/target/sky-server-1.0-SNAPSHOT.jar
```

---

## 📌 注意事项

- 项目使用 **JWT** 进行双端认证，管理端与用户端使用不同的密钥和令牌名
- 图片上传依赖 **阿里云 OSS**，请确保 OSS 相关配置正确
- 微信支付需要有效的商户号、APIv3 密钥等配置
- 生产环境请将 `application-dev.yml` 中的敏感配置移至环境变量或外部配置中心

---

## 📄 License

仅供学习交流使用。
