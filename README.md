# mp-support
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
