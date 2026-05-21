# 酒店管理系统

一个全栈酒店管理系统，包含面向员工的后台管理端和面向客人的移动端。

## 技术栈

| 层级 | 技术 |
|------|------|
| 管理端前端 | Vue 3 + Vite + Element Plus |
| 用户端前端 | Vue 3 + Vite + Vant 4 |
| 后端 | Spring Boot 3.5 + MyBatis-Plus |
| 数据库 | MySQL 8 |

## 项目结构

```
├── hotel/           # 管理端前端（员工后台）
├── hotel_user/      # 用户端前端（客人小程序）
└── mybatisplue/     # 后端 API（Spring Boot）
```

## 功能介绍

### 管理端 (`hotel/`)

- **入住登记** -- 客人信息录入、房间分配、入住办理
- **退房处理** -- 退房结账，房间状态更新为待清洁
- **预订管理** -- 查看、取消预订，支持从预订直接转入住
- **房间管理** -- 添加/删除房间，设置房型、价格、容纳人数
- **物品库存** -- 酒店用品的增删改查
- **会员管理** -- 查看客户信息，发放奖励积分
- **员工管理** -- 创建/编辑/删除员工账号，设置角色权限

### 用户端 (`hotel_user/`)

- **房间浏览** -- 查看可用房间，展示图片、会员价、日期选择
- **在线预订** -- 选择房间并预订，支持积分抵扣和特殊需求备注
- **会员体系** -- 五级会员等级，享受不同折扣和权益
- **订单记录** -- 查看预订、入住、退房等历史记录
- **评价系统** -- 提交和浏览酒店评价，支持星级评分

### 会员等级

| 等级 | 折扣 | 延迟退房 | 积分倍率 |
|------|------|----------|----------|
| 星会员 | 9.5 折 | 13:00 | 1 倍 |
| 2 星会员 | 9.0 折 | 14:00 | 1.2 倍 |
| 3 星会员 | 8.5 折 | 15:00 | 1.5 倍 |
| 4 星会员 | 8.0 折 | 16:00 | 2 倍 |
| 5 星会员 | 7.5 折 | 17:00 | 2.5 倍 |

## 快速开始

### 环境要求

- Java 24 + Maven
- Node.js 18+
- MySQL 8

### 数据库配置

```sql
CREATE DATABASE hotel DEFAULT CHARACTER SET utf8mb4;
```

通过环境变量配置数据库凭据，或直接修改 `mybatisplue/src/main/resources/application.yml`：

```bash
export DB_USERNAME=root
export DB_PASSWORD=你的密码
```

### 启动后端

```bash
cd mybatisplue
mvn spring-boot:run
# API 运行在 http://localhost:8080
```

### 启动管理端

```bash
cd hotel
npm install
npm run dev
# 访问 http://localhost:5173
```

### 启动用户端

```bash
cd hotel_user
npm install
npm run dev
# 访问 http://localhost:5174
```

## API 概览

| 模块 | 主要接口 |
|------|----------|
| 房间 | `GET /Room/list`、`POST /Room/add`、`POST /Room/del` |
| 客户 | `POST /Customer/register`、`POST /Customer/login` |
| 员工 | `POST /Employeeaccount/login`、`POST /Employeeaccount/register` |
| 入住 | `POST /check/in`、`POST /check/out`、`GET /check/list` |
| 预订 | `POST /Reserve/add`、`POST /Reserve/cancel`、`GET /Reserve/list` |
| 库存 | `GET /Inventory/list`、`POST /Inventory/add` |
| 评价 | `GET /Evaluation/list`、`POST /Evaluation/add` |

## 许可

本项目仅供学习使用。
