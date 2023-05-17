# [Tintin Search](tintin.center)

Tintin Search是一个基于Elasticsearch的搜索引擎。

## 特性

- 搜索引擎基于Elasticsearch,强大的搜索和分析功能
- 提供RESTful API,易于集成
- 支持中文分词,精确匹配查询
- 扩展性强,可方便扩展其他数据源

## 快速开始

1. 安装Elasticsearch 6.x
2. 克隆本项目

```bash
git clone https://github.com/Tintin-c/tintin-search.git
```

3. 修改数据库、Elasticsearch配置

4. 启动项目

5. 在浏览器访问 http://localhost:8081 , 查看API文档

6. 向Elasticsearch中导入数据,然后就可以使用搜索API进行搜索了

## 聚合了文章、图片、用户搜索

![image-20230517160428911](/Users/tintin/Library/Application%20Support/typora-user-images/image-20230517160428911.png)

## 使用Elasticsearch & ik分词器 支持中文搜索、chatgpt完成Elasticsearch部分代码

![image-20230517160910102](/Users/tintin/Library/Application%20Support/typora-user-images/image-20230517160910102.png)




## 未来计划

- 增加更多数据源(MySQL,MongoDB等)
- 提供管理后台
- 优化搜索精度
