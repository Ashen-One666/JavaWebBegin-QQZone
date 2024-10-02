# 一、QQZone业务需求
### 1. 用户登录
### 2. 登录后显示主页面
左侧：显示好友列表
上端：显示欢迎词，如果不是自己空间，显示超链接——返回自己空间
### 3. 查看日志详情
- 日志本身信息（作者头像、昵称、日志标题、日志内容、日志日期）
- 回复列表（回复者头像、昵称、回复内容、回复日期）
- 主人回复信息
### 4. 删除日志
### 5. 删除特定回复
### 6. 删除特定主人回复
### 7. 添加日志、添加主人回复
### 8. 点击左侧好友链接，进入好友空间

# 二、数据库设计
### 1. 抽取实体
用户（用户登录信息、用户详细信息）、日志、回复、回帖。主人回复
### 2. 分析其中属性
- 用户登录信息(t_user_basic)：账号、密码、头像、昵称
- 用户详细信息(t_user_detail)：真实姓名、性别、星座、血型、邮箱、手机号
- 日志(t_topic)：标题、内容、日期、作者
- 回复(t_reply)：内容、日期、作者、日志（回复的哪篇日志）
- 主人回复(t_host_reply)：内容、日期、作者、日志、回复（对于谁的回复）
### 3. 分析实体间关系
- 用户登录信息：用户详细信息 1：1 （PK）
- 用户：日志 1：N
- 日志：回复 1：N
- 回复：主人回复 1：1 （UK 唯一外键）
- 用户：好友 M：N （出现多对多时会产生一张中间表 t_friend）
# 三、数据库的范式
- 1） 第一范式：  
&ensp; 列不可再分
- 2） 第二范式：  
&ensp; 一张表只表达一层含义（只描述一件事），即每个非主属性完全依赖于表的主键
- 3） 第三范式：  
&ensp; 表中的每列和主键都是直接依赖而非间接依赖，即非主属性不能依赖于其他非主属性  
&ensp; 不过实际开发中，如果遵守第三范式（如主人回复中的“作者”属性可删除，因为“作者”这一属性可以
通过“主人回复->回复->日志”并在日志表中找到作者属性），则需要多表联查，这样会增加查询时间，所以通常
需要在数据库规范度（空间冗余）和查询性能（时间）间取舍
- 总结：  
&ensp; 数据库的设计范式和数据库查询性能很多时候是相悖的，我们需要根据业务设计情况做出选择：  
&ensp; - 查询频次不高的情况下，则要提高数据库的设计范式，从而提升存储效率  
&ensp; - 查询批次较高的情况下，则要降低数据库的设计范式，允许特定的冗余，从而提高查询性能
&ensp; (另外，注意数据库设计时主键尽量不要和业务逻辑产生关系，即主键应该是无意义的，如自增id)


# 项目错误和解决方案
### 1. IDEA收到浏览器返回的参数值全部为null
- 原因：在 Java 8 之前，编译后的 .class 文件不包含参数名称， 导致通过反射获取方法参数名时只能得到默认的
  arg0, arg1 等名字。Java 8 提供了一个新的编译选项 -parameters， 可以在编译时将参数名保留到 .class 文件中，
  这样反射获取到的就是真实的参数名称。
- 方法：在 IntelliJ IDEA 中设置 -parameters 编译选项  
  进入settings，选择左侧的 Build, Execution, Deployment > Compiler > Java Compiler。
  在 Additional command line parameters 输入框中，输入 -parameters。
  最后清除out文件夹，重新build项目和artifacts
### 2. URL未修改，用的还是fruitdb
- 方法：在ConnUtil中修改数据库URL中的数据库名
### 3. sql语句想获取列别名失败
- 原因：执行excuteQuery("select fid as id from user")时想获取别名id，但得到的列名仍然是fid，是因为
  excuteQuery()函数中使用的是rsmd.getColumnName()而不是rsmd.getColumnLabel()，前者获取列原名，后者
  获取列别名
- 方法：将excuteQuery()中的rsmd.getColumnName()改为rsmd.getColumnLabel()
### 4. 编译器报错：java.lang.IllegalArgumentException: Can not set java.util.Date field com.fizz.qqzone.pojo.Topic.topicDate to java.time.LocalDateTime
- 方法：在pojo.Topic类中将topicDate类型改为LocalDateTime
### 5. 编译器报错：java.lang.IllegalArgumentException: Can not set com.fizz.qqzone.pojo.UserBasic field com.fizz.qqzone.pojo.Topic.author to java.lang.Integer
- 原因：数据库中Topic的author属性存储的是Integer，而Topic类中其属性为UserBasic，需要把其包装成UserBasic类才能放到Topic类中
- 方法1：一个解决方案是直接只修改BaseDAO中的setValue方法，使得当类中属性的类型为自定义类型(如UserBasic)时，根据参数创造该自定义类型实例并传入
- 方法2：我的想法是同样重写setValue方法，但我的setValue是优先调用类中的set方法(这里set方法的参数属性必须和setValue传入的参数属性一致)，如果
  没有再直接赋值，然后在类中重写set方法。这样的好处是可以不用把代码在底层写死，而是可以修改类中的set方法实现不同类型的赋值
  (思想类似于现有的数据库工具包，即先调用set，没有再赋值)
### 6. html页面没有样式，同时数据也不展示
- 原因：我们是直接去请求的静态页面资源，并没有执行super.processTemplate()，也就是thymeleaf未生效
- 方法：新增PageController(放到通用代码myspringmvc中)，添加page方法，目的是发送给中央控制器执行processTemplate进行thymeleaf渲染