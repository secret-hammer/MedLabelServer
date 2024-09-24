db = connect("mongodb://localhost:27017/admin"); // 使用root用户连接到admin数据库
db = db.getSiblingDB('medlabel'); // 切换到目标数据库
db.createCollection('test'); // 创建一个集合，确保数据库被创建
db.test.insert({ initialized: true }); // 插入数据