 网上立案系统（Filing System）

一个模拟"网上立案"全流程的练习项目，涵盖前端、后端、数据库、浏览器自动化。

## 项目简介

员工在内部系统录入案件、上传材料、创建立案任务，程序自动打开"模拟法院网站"完成填表和提交，复核员复核后确认提交，生成申请编号。

**本项目使用本地模拟法院页面，不对接真实法院系统。**

## 技术栈

| 层级 | 技术 |
|------|------|
| 前端 | Vue 3 + TypeScript + Vite + Element Plus |
| 后端 | Java 17 + Spring Boot 4 + MyBatis + Spring Security |
| Worker | Playwright for Java（内嵌在后端，@Scheduled 轮询） |
| 数据库 | MySQL 8.0 |

## 目录结构
filing/
├── backend/filing/ # Spring Boot 后端（含 Worker）
│ ├── src/main/java/com/example/filing/
│ │ ├── controller/ # REST API
│ │ ├── service/ # 业务逻辑
│ │ ├── mapper/ # 数据库访问
│ │ ├── entity/ # 实体类
│ │ ├── security/ # JWT + 权限
│ │ ├── worker/ # 自动填表机器人
│ │ └── config/ # 配置
│ └── src/main/resources/
│ └── static/mock-court.html # 模拟法院页面
├── frontend/ # Vue 3 前端
│ └── src/
│ ├── views/ # 页面
│ ├── api/ # 接口封装
│ └── router/ # 路由
└── sql/ # 数据库脚本

text

## 环境要求

- JDK 17
- Maven（项目自带 `mvnw`，无需全局安装）
- Node.js 18+
- MySQL 8.0
- （Worker 需要）Playwright 的 Chromium

## 启动步骤

### 1. 初始化数据库

用 MySQL Workbench 执行 `sql/init.sql`（或手动建库）：

```sql
CREATE DATABASE filing_db DEFAULT CHARACTER SET utf8mb4;
然后执行 init.sql 里的建表和初始化数据。

2. 启动前端
bash
cd frontend
npm install
npm run dev
访问 http://localhost:5173

3. 启动后端（含 Worker）
用 IntelliJ 打开 backend/filing，Run FilingApplication。

或者命令行：

bash
cd backend/filing
./mvnw spring-boot:run
访问 http://localhost:8080

4. 下载 Playwright 浏览器（首次）
bash
cd backend/filing
./mvnw compile exec:java "-Dexec.mainClass=com.microsoft.playwright.CLI" "-Dexec.args=install chromium"
测试账号
用户名	密码	角色
admin	123456	管理员
staff01	123456	员工
reviewer01	123456	复核员
演示流程
用 staff01 登录

新建案件（原告、被告、案由、金额）

上传材料（PDF/JPEG/PNG）

点"创建立案任务" → 状态变 QUEUED

Worker 自动打开浏览器，填模拟法院页面，生成申请编号 → 状态变 WAIT_REVIEW

退出，用 reviewer01 登录

打开任务详情 → 点"确认提交" → 状态变 SUBMITTED

退出，用 admin 登录 → 可看全部案件和任务

任务状态机
text
DRAFT → QUEUED → FILLING → WAIT_REVIEW → SUBMITTED
                    ↓
                 FAILED（可修复重排）
权限矩阵
功能	ADMIN	STAFF	REVIEWER
用户管理	✅	❌	❌
查看案件	全部	仅本人	全部
新增/编辑案件	✅	仅本人且未提交	❌
上传材料	✅	仅本人	❌
创建任务	✅	仅本人案件	❌
复核/提交	✅	❌	✅
主要接口
接口	方法	权限
/api/auth/login	POST	公开
/api/cases	GET/POST	登录
/api/cases/{id}	GET/PUT	本人/ADMIN
/api/cases/{id}/materials	GET/POST	本人/ADMIN
/api/tasks	GET/POST	登录
/api/tasks/{id}/submit	POST	REVIEWER/ADMIN
/api/users	GET/DELETE	ADMIN
说明
本项目为练习项目，模拟法院页面为本地页面，不涉及真实司法系统

Worker 内嵌在后端，通过 @Scheduled 每 3 秒轮询任务表

权限控制在后端强制（JWT + RBAC + 数据级越权校验），前端隐藏按钮仅作体验优化

text

---

## 五、保存

`Ctrl + S`。

---

## 六、建 `sql/init.sql`

README 里提到 `sql/init.sql`，但你的数据库脚本可能没放进去。

**如果你之前建过 `init.sql`，找到它，复制到 `E:\IdeaProjects\filing\sql\init.sql`。**

**没有的话，告诉我，我给你完整的 `init.sql`。**

---

## 七、推送到 GitHub

```bash
cd /e/IdeaProjects/filing
git add .
git commit -m "初始提交：网上立案系统（含README）"
git branch -M main
git remote add origin https://github.com/2003sus/filing-system.git
git push -u origin main
八、告诉我
README.md 建好了吗？

sql/init.sql 有吗？（需要的话我给你）

git push 成功了吗？

做一步告诉我一步，我接着带你走。

本回答由 AI 生成，内容仅供参考，请仔细甄别
give me a whole copy and p[ste
完整复制粘贴版：建 README + 推送 GitHub
第一步：建 README.md
在 Bash 里执行：

bash
cd /e/IdeaProjects/filing
cat > README.md << 'ENDOFFILE'
# 网上立案系统（Filing System）

一个模拟"网上立案"全流程的练习项目，涵盖前端、后端、数据库、浏览器自动化。

## 项目简介

员工在内部系统录入案件、上传材料、创建立案任务，程序自动打开"模拟法院网站"完成填表和提交，复核员复核后确认提交，生成申请编号。

**本项目使用本地模拟法院页面，不对接真实法院系统。**

## 技术栈

| 层级 | 技术 |
|------|------|
| 前端 | Vue 3 + TypeScript + Vite + Element Plus |
| 后端 | Java 17 + Spring Boot 4 + MyBatis + Spring Security |
| Worker | Playwright for Java（内嵌在后端，@Scheduled 轮询） |
| 数据库 | MySQL 8.0 |

## 目录结构
filing/
├── backend/filing/ # Spring Boot 后端（含 Worker）
│ ├── src/main/java/com/example/filing/
│ │ ├── controller/ # REST API
│ │ ├── service/ # 业务逻辑
│ │ ├── mapper/ # 数据库访问
│ │ ├── entity/ # 实体类
│ │ ├── security/ # JWT + 权限
│ │ ├── worker/ # 自动填表机器人
│ │ └── config/ # 配置
│ └── src/main/resources/
│ └── static/mock-court.html # 模拟法院页面
├── frontend/ # Vue 3 前端
│ └── src/
│ ├── views/ # 页面
│ ├── api/ # 接口封装
│ └── router/ # 路由
└── sql/ # 数据库脚本

text

## 环境要求

- JDK 17
- Maven（项目自带 `mvnw`，无需全局安装）
- Node.js 18+
- MySQL 8.0
- Playwright 的 Chromium（Worker 需要）

## 启动步骤

### 1. 初始化数据库

用 MySQL Workbench 执行 `sql/init.sql`，创建 `filing_db` 数据库、6 张表，并插入 3 个测试账号。

### 2. 启动前端

```bash
cd frontend
npm install
npm run dev
访问 http://localhost:5173

3. 启动后端（含 Worker）
用 IntelliJ 打开 backend/filing，运行 FilingApplication。

或命令行：

bash
cd backend/filing
./mvnw spring-boot:run
访问 http://localhost:8080

4. 下载 Playwright 浏览器（首次运行）
bash
cd backend/filing
./mvnw compile exec:java "-Dexec.mainClass=com.microsoft.playwright.CLI" "-Dexec.args=install chromium"
测试账号
用户名	密码	角色
admin	123456	管理员
staff01	123456	员工
reviewer01	123456	复核员
演示流程
用 staff01 登录

新建案件（原告、被告、案由、金额）

上传材料（PDF/JPEG/PNG）

点"创建立案任务" → 状态变 QUEUED

Worker 自动打开浏览器，填模拟法院页面，生成申请编号 → 状态变 WAIT_REVIEW

退出，用 reviewer01 登录

打开任务详情 → 点"确认提交" → 状态变 SUBMITTED

退出，用 admin 登录 → 可看全部案件和任务

任务状态机
text
DRAFT → QUEUED → FILLING → WAIT_REVIEW → SUBMITTED
                    ↓
                 FAILED（可修复重排）
权限矩阵
功能	ADMIN	STAFF	REVIEWER
用户管理	是	否	否
查看案件	全部	仅本人	全部
新增/编辑案件	是	仅本人且未提交	否
上传材料	是	仅本人	否
创建任务	是	仅本人案件	否
复核/提交	是	否	是
主要接口
接口	方法	权限
/api/auth/login	POST	公开
/api/cases	GET/POST	登录
/api/cases/{id}	GET/PUT	本人/ADMIN
/api/cases/{id}/materials	GET/POST	本人/ADMIN
/api/tasks	GET/POST	登录
/api/tasks/{id}/submit	POST	REVIEWER/ADMIN
/api/users	GET/DELETE	ADMIN
说明
本项目为练习项目，模拟法院页面为本地页面，不涉及真实司法系统

Worker 内嵌在后端，通过 @Scheduled 每 3 秒轮询任务表

权限控制在后端强制（JWT + RBAC + 数据级越权校验），前端隐藏按钮仅作体验优化
ENDOFFILE

text

**执行完后，敲 `cat README.md` 确认内容写进去了。**

---

## 第二步：建 `sql/init.sql`

**先建目录：**

```bash
cd /e/IdeaProjects/filing
mkdir -p sql
cat > sql/init.sql << 'ENDOFFILE'
-- ============================================================
-- 网上立案系统 数据库初始化脚本
-- ============================================================

DROP DATABASE IF EXISTS filing_db;
CREATE DATABASE filing_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE filing_db;

-- 1. 用户表
CREATE TABLE sys_user (
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    username      VARCHAR(50)  NOT NULL UNIQUE COMMENT '登录名',
    password_hash VARCHAR(100) NOT NULL        COMMENT 'BCrypt哈希',
    real_name     VARCHAR(50)  NOT NULL        COMMENT '姓名',
    role          VARCHAR(20)  NOT NULL        COMMENT 'ADMIN/STAFF/REVIEWER',
    enabled       TINYINT(1)   NOT NULL DEFAULT 1 COMMENT '1启用 0禁用',
    create_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT '用户表';

-- 2. 案件表
CREATE TABLE filing_case (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    case_no     VARCHAR(50)  NOT NULL UNIQUE COMMENT '案件编号',
    title       VARCHAR(200) NOT NULL        COMMENT '案由标题',
    plaintiff   VARCHAR(100) NOT NULL        COMMENT '原告',
    defendant   VARCHAR(100) NOT NULL        COMMENT '被告',
    amount      DECIMAL(15,2)                COMMENT '涉案金额',
    description TEXT                         COMMENT '案件描述',
    status      VARCHAR(20)  NOT NULL DEFAULT 'DRAFT' COMMENT 'DRAFT/SUBMITTED',
    creator_id  BIGINT       NOT NULL        COMMENT '创建人',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_creator (creator_id)
) COMMENT '案件表';

-- 3. 材料表
CREATE TABLE case_material (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    case_id     BIGINT       NOT NULL COMMENT '所属案件',
    file_name   VARCHAR(255) NOT NULL COMMENT '原始文件名',
    file_path   VARCHAR(500) NOT NULL COMMENT '存储路径',
    file_type   VARCHAR(20)  NOT NULL COMMENT 'PDF/JPEG/PNG',
    file_size   BIGINT       NOT NULL COMMENT '字节数',
    uploader_id BIGINT       NOT NULL COMMENT '上传人',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_case (case_id)
) COMMENT '案件材料表';

-- 4. 任务表
CREATE TABLE filing_task (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    case_id     BIGINT       NOT NULL COMMENT '所属案件',
    status      VARCHAR(20)  NOT NULL DEFAULT 'DRAFT'
                COMMENT 'DRAFT/QUEUED/FILLING/WAIT_REVIEW/SUBMITTED/FAILED',
    snapshot    TEXT         COMMENT '任务快照(JSON)',
    fail_reason VARCHAR(500) COMMENT '失败原因',
    apply_no    VARCHAR(50)  COMMENT '模拟法院申请编号',
    reviewer_id BIGINT       COMMENT '复核人',
    submit_time DATETIME     COMMENT '提交时间',
    version     INT          NOT NULL DEFAULT 0 COMMENT '乐观锁版本',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_case (case_id),
    INDEX idx_status (status)
) COMMENT '立案任务表';

-- 5. 操作日志表
CREATE TABLE operation_log (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    operator_id BIGINT       NOT NULL COMMENT '操作人',
    biz_type    VARCHAR(30)  NOT NULL COMMENT '业务类型：CASE/TASK/USER',
    biz_id      BIGINT       COMMENT '业务ID',
    action      VARCHAR(50)  NOT NULL COMMENT '动作：CREATE/UPDATE/SUBMIT等',
    result      VARCHAR(20)  NOT NULL COMMENT 'SUCCESS/FAIL',
    detail      VARCHAR(500) COMMENT '详情',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_operator (operator_id),
    INDEX idx_biz (biz_type, biz_id)
) COMMENT '操作日志表';

-- 6. 演示数据（3个测试账号，密码都是 123456）
INSERT INTO sys_user (username, password_hash, real_name, role) VALUES
('admin',      '$2a$10$h9sXg54DuDIX3Fd86fi15OkBKB4ytPeFgLjQwYQ0mOu4xx1NT7xzS', '管理员',  'ADMIN'),
('staff01',    '$2a$10$h9sXg54DuDIX3Fd86fi15OkBKB4ytPeFgLjQwYQ0mOu4xx1NT7xzS', '张三',    'STAFF'),
('reviewer01', '$2a$10$h9sXg54DuDIX3Fd86fi15OkBKB4ytPeFgLjQwYQ0mOu4xx1NT7xzS', '李四',    'REVIEWER');

SELECT id, username, real_name, role, enabled FROM sys_user;
ENDOFFIL
