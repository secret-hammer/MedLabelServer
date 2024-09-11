# MedLabel医学平台项目重构

# 重构目标

## 整体目标

- 保证项目代码模块划分清晰，单一职责原则，模块间耦合度低，能够适应进一步的迭代开发
- 库版本使用较新的LTS版本，保证长期可用
- 编写方便查阅的接口文档和设计文档，便于协作和项目交付
- 构建方便的部署脚本，通过docker等工具维持运行环境的统一，方便移植
- 尽量规范开发流程，保留开发历史记录；部署上线后能够保留清晰的日志记录，便于项目维护

## 前端

| 名称           | 内容                                                         |
| -------------- | ------------------------------------------------------------ |
| **提高库版本** | 之前的库版本太老，对很多新工具不提供支持，而且组件有一些bug影响使用体验（Swiper组件点击跳转异常） |
| **模块分组**   | 抽取之前杂糅在一起的页面（尤其是数据集详情页），把一些常量和功能函数做进一步划分（request、redux（store，actions，reducers）），优化一部分函数设定（标注页面有一些功能重叠的函数） |
| **配合后端**   | 后端接口更新，配合后端接口改变更改数据处理部分               |
| **优化样式**   | 优化一些重点不突出的地方，提高使用体验                       |

## 后端

| 名称                               | 内容                                                         |
| ---------------------------------- | ------------------------------------------------------------ |
| **修改底层架构**                   | 修改之前的架构体系使用Springboot，更新模块划分方式           |
| **修改数据库**                     | 删除冗余表和列，严格定义列的数据类型和值限定，适当使用索引   |
| **重新设计接口**                   | 对于之前混杂的API进行模块划分，重新设计API功能               |
| **重新设计静态资源服务器**         | 除原本数据集图片资源外，增加标注文件和系统静态资源等内容     |
| **严格定义日志系统和异常处理系统** | 严格定义自定义异常的错误码和错误提示，做到BUG可追溯，便于维护 |

## 计算服务

| 名称                 | 内容                                                         |
| -------------------- | ------------------------------------------------------------ |
| **删除业务处理部分** | 只保留深度学习模型推理功能，删除所有和数据处理以及业务数据相关的模块 |



# 设计文档

## 架构选型

| 架构名称       | 技术栈                                   |
| -------------- | ---------------------------------------- |
| 后端           | Springboot + JPA                         |
| 关系型数据库   | MySQL（暂时不需要分库分表）              |
| NoSQL数据库    | Redis（缓存），MongoDB（标注数据持久化） |
| 静态资源服务器 | Ngnix                                    |
| 计算服务       | Flask（沿用之前的架构，修改代码）        |
| 前端           | React                                    |

## 架构和软件版本

| 架构       | 版本   |
| ---------- | ------ |
| JDK        | 17     |
| Maven      | 3.9.8  |
| Springboot | 2.7.18 |
| MySQL      | 8.0.37 |
| Redis      | 6.2.14 |
| MongoDB    | 6.0.16 |
| RabbitMQ   | 3.13.6 |



## 接口文档

### 模块划分

- **用户模块**：负责用户的注册、登录、权限控制、用户数据管理（CRUD）；
- **数据集模块**：负责数据集信息管理（数据集创建、数据集编辑、条件查询数据集和数据集删除）
- **组模块**：负责组相关的操作和信息管理（组创建、组信息编辑、组查询、组删除）6
- **图片数据模块**：负责图片数据信息管理
  - 图片上传
  - 图片下载
  - 图片元信息编辑（包括图片所属分组迁移）
  - 图片删除
  - 图片条件查询（按数据集查询、按组查询等等））
- **标注数据模块**：负责图片标注数据信息管理
  - 标注结果保存
  - 智能标注（click推理和框推理）
  - 标注结果下载
  - 标注结果更新
  - 标注结果的查询
  - 标注结果的删除
- **任务模块**：负责所有任务（耗时操作）（**数据集创建任务、模型推理任务**）信息的管理
  - 任务创建
  - 任务执行（调用python计算服务进行）
  - 任务进度更新（通过websocket实现消息推送）
  - 任务查询



### 具体接口信息在Apifox中更新

### 数据库创建命令

- 要求数据库定义过程**不包含任何外键约束**！在业务中通过应用层JPA，或纯业务逻辑来进行判断
- 数据库每一个字段都要设置为**有值**（设置为非空或给定‘N/A’作为默认值，**不允许出现空值！**）

```sql
# 创建数据库
CREATE DATABASE MedLabel_dev
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

# 设置中国时区
SET time_zone = '+8:00';
# 选择数据库
USE MedLabel_dev;

# 用户表
CREATE TABLE User (
    UserId INT AUTO_INCREMENT PRIMARY KEY,                -- 自增长的用户ID
    Username VARCHAR(50) NOT NULL UNIQUE,                 -- 用户名，必须唯一且非空
    Password VARCHAR(255) NOT NULL,                       -- 密码，非空
    Email VARCHAR(100) NOT NULL UNIQUE,                   -- 电子邮件，必须唯一且非空
    Phone VARCHAR(20) DEFAULT 'N/A',                      -- 手机号码，默认为'N/A'
    ProfileLink VARCHAR(100) DEFAULT 'N/A',               -- 个人信息网站链接，默认为'N/A'
    CreatedTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,      -- 记录创建时间，默认为当前时间
    UpdatedTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP -- 记录最后更新时间，默认为当前时间并在更新时自动修改
);

# 图片类型表 (保存网站支持的图片类型（普通图、病理图）)
CREATE TABLE ImageType (
    ImageTypeId INT PRIMARY KEY,
    ImageTypeName VARCHAR(50) NOT NULL,
    ImageExtensions VARCHAR(500) NOT NULL
);

INSERT INTO ImageType (ImageTypeName, ImageExtensions)
VALUES
('自然图', '["png", "jpg", "jpeg"]'),
('数字医学图像', '["dicom"]'),
('病理图', '["mrxs", "tif", "svs"]');

# 数据集表 (现在的数据集表表达一个较大的概念，在编码时将其设定为和ImageGroup表的父目录，形成两层目录来确定一组图片)
CREATE TABLE Project (
    ProjectId INT AUTO_INCREMENT PRIMARY KEY,                -- 自增长的数据集ID
    ProjectName VARCHAR(50) NOT NULL,                        -- 数据集名，非空
    Description VARCHAR(2000) DEFAULT 'N/A',                   -- 数据集描述信息，默认为'N/A'
    UserId INT NOT NULL,                                     -- 关联的用户，外键（不在数据库中设计外键） 
    ImageTypeId INT NOT NULL,                                -- 关联的图片类型，外键（不在数据库中设计外键）
    CreatedTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,         -- 记录创建时间，默认为当前时间
    UpdatedTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 记录最后更新时间，默认为当前时间并在更新时自动修改
  	version INT NOT NULL DEFAULT 0                      -- 版本号，用于实现乐观锁
);

# 图片组表（ImageGroup表，保存图片的最小一级，是Project的子目录）
CREATE TABLE ImageGroup (
    ImageGroupId INT AUTO_INCREMENT PRIMARY KEY,               -- 自增长的图片组ID
    ImageGroupName VARCHAR(50) NOT NULL,                       -- 图片组名，非空
    Description VARCHAR(2000) NOT NULL DEFAULT 'N/A',          -- 图片组描述信息，非空，默认为'N/A'
    ProjectId INT NOT NULL,                                    -- 关联的数据集ID，外键（不在数据库中设计外键）
    CreatedTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,           -- 记录创建时间，默认为当前时间
    UpdatedTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 记录最后更新时间，默认为当前时间并在更新时自动修改
  	version INT NOT NULL DEFAULT 0                      -- 版本号，用于实现乐观锁
);

# 图片表 
CREATE TABLE Image (
    ImageId INT AUTO_INCREMENT PRIMARY KEY,                -- 自增长的图片ID
    ImageUrl VARCHAR(255) NOT NULL,                        -- 图片的URL，非空
    ImageName VARCHAR(255) NOT NULL,                       -- 图片名称
    ImageGroupId INT NOT NULL,                             -- 关联的图片组ID，外键（不在数据库中设计外键）
    ImageTypeId INT NOT NULL,                              -- 关联的图片类型ID，外键（不在数据库中设计外键）
    Status INT NOT NULL DEFAULT 0,                         -- 图片状态，默认为0 （0:未标注，1:已标注，2:标注完成）
    CreatedTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,       -- 记录创建时间，默认为当前时间
    UpdatedTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 记录最后更新时间，默认为当前时间并在更新时自动修改
  	version INT NOT NULL DEFAULT 0                      -- 版本号，用于实现乐观锁
);

# 网络模型表（表示当前平台可用的所有模型）
CREATE TABLE Network(
NetworkId INT AUTO_INCREMENT PRIMARY KEY,
    NetworkName VARCHAR(50) NOT NULL,
    Description VARCHAR(2000) NOT NULL DEFAULT 'N/A',
    CreatedTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,          -- 记录创建时间，默认为当前时间
    UpdatedTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP -- 记录最后更新时间，默认为当前时间并在更新时自动修改
);

```

```Java
// MongoDB 文档类型，这里给出springboot的类定义进行格式限定
@Data
@Document(collection = "annotations")
public class Annotation {
    @Id
    private ObjectId annotationId;

    private String annotationName;

    private String description;

    @NotNull(message = "Image Id is required")
    private Integer imageId;

    private Timestamp createdTime;

    private Timestamp updatedTime;

    private String annotatedBy;

    @NotNull(message = "User Id is required")
    private Integer userId; // 直接关联用户ID，避免多级关联访问

    private String annotationResult; // 标注结果
}
```

