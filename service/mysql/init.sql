# 创建数据库
CREATE DATABASE IF NOT EXISTS medlabel
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

# 设置中国时区
SET time_zone = '+8:00';
# 选择数据库
USE medlabel;
# 设置通讯字符集
SET NAMES utf8mb4;

GRANT ALL PRIVILEGES ON medlabel.* TO 'admin'@'%';
FLUSH PRIVILEGES;

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
    ImageTypeId INT AUTO_INCREMENT PRIMARY KEY,
    ImageTypeName VARCHAR(50) NOT NULL,
    ImageExtensions VARCHAR(500) NOT NULL
);

INSERT INTO ImageType (ImageTypeName, ImageExtensions)
VALUES
('自然图', '["png", "jpg", "jpeg"]'),
('数字医学图像', '["dicom", "dcm"]'),
('病理图', '["mrxs", "tif", "svs"]');

# 数据集表 (现在的数据集表表达一个较大的概念，在编码时将其设定为和ImageGroup表的父目录，形成两层目录来确定一组图片)
CREATE TABLE Project (
    ProjectId INT AUTO_INCREMENT PRIMARY KEY,                -- 自增长的数据集ID
    ProjectName VARCHAR(50) NOT NULL,                        -- 数据集名，非空
    Description VARCHAR(2000) DEFAULT 'N/A',                   -- 数据集描述信息，默认为'N/A'
    UserId INT NOT NULL,                                     -- 关联的用户，外键（不在数据库中设计外键） 
    ImageTypeId INT NOT NULL,                                -- 关联的图片类型，外键（不在数据库中设计外键）
    Categories VARCHAR(1000) NOT NULL DEFAULT '[]',          -- 数据集的标注类别信息（以json数组序列化形式存储）
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

CREATE INDEX idx_project_UserId ON project (UserId);
CREATE INDEX idx_imagegroup_ProjectId ON imagegroup (ProjectId);
CREATE INDEX idx_image_ImageGroupId ON image (ImageGroupId);
CREATE INDEX idx_image_Status ON image (Status);