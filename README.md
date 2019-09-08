# SORM
***This is a framework based on object-relational-mapping by Java.***
#### 主要分为四个部分
- src：框架源码
- SORM0.1_jar:框架形成的jar包
- SORM0.1_api:框架使用的api帮助文件
- Demo：测试框架的示例Java工程
#### 使用该框架的注意事项
1、在src下建立db.properties配置文件。<br>
2、每张表只能有一个主键，不能处理多个主键的情况。<br>
3、po尽量使用包装类，不要使用基本数据类型。<br>
4、目前，只能处理数据库来维护自增主键的方式。<br>
