# mp-support

对MybatisPlus开源框架源码Generator模块进行改造，支持开发者生成文件目录进行定制化输出.

源码改造主要有两点：

1、重写service接口，抽取日常开发中常用的方法且便于自行管理，还能减少应用启动成本。
增加自定义的service接口及实现如下，仅保存常用的增删查改、翻页查询、批量新增方法；

public interface IMPService{

}

public class MPServiceImpl<M extends BaseMapper, T> implements IMPService{

}

2、重写generator模块，达到定制化输出文件目录，实现如下：
mybatisPuls generator生成文件结构为： 
![Image text](https://raw.githubusercontent.com/wlstone119/img/master/20190912122500.jpg)

修改为： 
![Image text](https://raw.githubusercontent.com/wlstone119/img/master/20190912123547.jpg)
包装了mybatisPlus，可以根据自己公司的骨架生成对应目录的文件<br>

1. 在本地打包之后，引用到自己的工程中
2. 复制mpGenerator的文件到自己的工程中
3. 修改Generator中的参数 
    - project_name 项目名称
    - company 公司名称
    - username 数据库登录名
    - password 数据库密码
    - ip 数据库地址
4. 修改XDLGenerator的参数
    - author 作者
    - dir 文件目录
    - project_url 本地项目路径
    - tablePrefix 表前缀，默认会去掉，如果要保留可以修改Generator
    - table_names 要生成的表列表
    - isNeedController 控制要不要生成controller
    - isNeedService 控制要不要生成service  可以避免覆盖
