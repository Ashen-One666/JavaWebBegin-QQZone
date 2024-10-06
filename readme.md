# 项目说明
- 手写类Springboot包，用于JavaWeb入门
- 类似QQ空间，前端使用js和thymeleaf，后端使用手写的类Springboot包
# 项目配置
- jdk 1.8.0
- tomcat 9.0.93
# 一、QQZone业务需求
### 1. 用户登录
（已实现）
### 2. 登录后显示主页面
（已实现）
- 左侧：显示好友列表
- 上端：显示欢迎词，如果不是自己空间，显示超链接——返回自己空间
### 3. 查看日志详情
（已实现）
- 日志本身信息（作者头像、昵称、日志标题、日志内容、日志日期）
- 回复列表（回复者头像、昵称、回复内容、回复日期）
- 主人回复信息
### 4. 删除日志
（已实现）
### 5. 删除特定回复
（已实现）
### 6. 删除特定主人回复
（已实现）
### 7. 添加日志、添加主人回复
（已实现）
### 8. 点击左侧好友链接，进入好友空间
（已实现）

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

# 四、项目错误和解决方案
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
  没有再直接赋值，然后在类中重写set方法(根据id简单地new一个对象然后赋给author属性)，最后在Service层根据topic的author的id给author补充上完整
  的信息(调用BasicService的方法获取对应的author实例)。  
  同理，Reply在在Service层将author和hostReply信息补全，hostReply在Service层将author信息补全。  
  这样的好处是可以不用把代码在底层写死，而是可以修改类中的set方法实现不同类型的赋值。
  (思想类似于现有的数据库工具包，即先调用set，没有再赋值)
### 6. html页面没有样式，同时数据也不展示
- 原因：我们是直接去请求的静态页面资源，并没有执行super.processTemplate()，也就是thymeleaf未生效
- 方法：新增PageController(放到通用代码myspringmvc中)，添加page方法，目的是发送给中央控制器执行processTemplate进行thymeleaf渲染
### 7. thymeleaf语法未生效
- 原因：可能是资源的路径错误(如 session.topic.replyList 中缺少"session."，或者是 frames/left 中缺少"frames/"，或者是 
  th:src="@{|/imgs/${session.topic.author.headImg}|}" 中缺少 "| ... |" (th语法中同时有字符串和变量需要用|连接) )
- 方法：可以在html中写 <span th:text="${...}"></span> 输出发生错误的变量
### 8. 添加回复时数据库未获取到输入框中的内容
- 原因：detail.html中 <textarea ... > 里面没有name属性，controller无法从页面中的输入框中获取输入信息
- 方法：<td>< textarea name="content" rows="3">这里是另一个回复！</textarea></td> （注意：name的值要和controller中方法里的参数名一致）
### 9. 删除带有主人回复的reply失败
- 报错：Cannot delete or update a parent row: a foreign key constraint fails (`qqzonedb2`.`t_host_reply`, CONSTRAINT `FK_host_reply` FOREIGN KEY (`reply`) REFERENCES `t_reply` (`id`))
- 原因：主人回复表t_host_reply中有外键约束（列reply引用了表t_reply中的列id），想要删除主表数据必须保证没有子表引用它
- 方法：先删除子表（t_host_reply）数据，即在ReplyService的delReply方法中进行修改

# 五、实现过程和注意事项
## 主界面
### index.html
- 3个iframe，每个iframe通过thymeleaf渲染出top/left/main.html
### top.html
- 判断是否是自己的空间 th:if="${session.userBasic.id!=session.friend.id}" ，如果不相等则多显示一个"返回自己空间"
- 返回自己空间的逻辑和left.html中打开好友空间的逻辑类似
### left.html
- 通过遍历当前userBasic(登录者)的friendList来显示他的好友
- 通过iframe的 target="_top" (注：_blank在新窗口打开, _parent在父窗口打开, _top在顶层窗口打开)，将点击的好友的日志在顶层窗口中打开
### main.html
- main页面应该显示friend(当前空间的主人)中的topicList，而不是userBasi(登录者)c中的topicList
## 日志详情页
### 需求
- 1） 已知topic的id，需要根据topic的id获取特定topic
- 2） 获取这个topic关联的所有回复
- 3） 如果某个回复有主人回复，需要查询出来
### 实现
- 在topicController中获取指定的topic
- 具体这个topic关联多少个reply由replyService内部实现
- 同理，reply是否有关联的hostReply也由replyService内部实现(调用hostReplyService的方法查询，并赋给当前topic)
## 添加回复
## 添加主人回复
## 删除日志功能
- 删除主人回复：直接删除
- 删除回复：如果回复有关联的主人回复，需要先删除主人回复再删除当前回复
- 删除日志：如果日志有关联的回复，需要先删除回复再删除当前日志
## 数据库连接
- 将数据库连接的配置写在src/jdbc.properties中，然后在ConnUtil中获取
- 更新：修改了BaseDAO（改写了ConnUtil），让其支持properties文件以及druid数据库连接池  
  有两种连接数据库方式：
  - 直接自己配置properties，然后读取，然后加载驱动...
  - 使用druid技术，那么properties中的key取名是有要求的
## 项目URL访问
- tomcat-Server中设置URL： http://localhost:8080/page.do?operate=page&page=login
- tomcat-deployment中设置application context: /
# 六、session中的变量说明
### userBasic
- 用于确认当前登陆者是谁
### friend
- 当前页面显示的qq空间的所有者是谁 (userBasic和friend不一致时，当前的登录者就没有修改他人空间的权限)
# 七、关于该JavaWeb项目底层的一些说明
### 1) 启动时访问网址
- 问：系统启动时，我们访问的页面是 http://localhost:8080/page.do?operate=page&page=login 
  为什么不是 http://localhost:8080/login.html
- 答：后者是直接访问的静态页面，那么页面上的thymeleaf表达式浏览器是不能识别的。
  我们访问前者的目的是执行 ViewBaseServlet中的processTemplate()
### 2) 访问URL时的解析过程
- 问：http://localhost:8080/context_root_name/page.do?operate=page&page=login 访问这个URL的过程是怎么样的？
  （注：本项目中中央控制器DispatcherServlet未考虑context root，因此本项目的context root名称为空，即"/"）
- 答：1.http:// -> 协议  2.localhost -> ServerIP  3.:8080 -> port  4./context_root_name -> context root  5. /page.do -> 
  request.getServletPath()  6. ?operate=page&page=login -> query string
  - DispatcherServlet -> urlPattern : *.do  拦截/page.do
  - request.getServletPath() -> /page.do
  - 解析处理字符串，将/page.do -> page
  - 拿到page字符串，然后去IOC容器(beanFactory)中寻找id=page的那个bean对象 -> PageController.java
  - 获取operate的值 -> page   因此得知，应该执行PageController中的page()方法
  - PageController中的page()方法定义如下：  
    public String page(String page) { return page; }
  - 在query string: "?operate=page&page=login" 中获取请求参数，参数名是page，参数值是login，因此page()方法的page值会被赋上"login"，然后return "login";
  - 因为PageController的page()方法是DispatcherServlet通过反射调用的，即method.invoke(...)，因此上一步的字符串"login"返回给DispatcherServlet
  - DispatcherServlet接受到返回值，然后处理视图。目前处理视图的方式有两种：  
    1） 带前缀redirect:  
    2） 不带前缀  
    当前返回"login"是不带前缀的，那么执行 super.processTemplate("login",request,response);
  - 此时ViewBaseServlet中的processTemplate方法会执行，效果是：  
    在"login"前面拼接"/"（其实就是配置文件中的view-prefix配置的值）  
    在"login"后面拼接".html"（其实就是配置文件中的view-suffix配置的值）  
    最后进行服务器转发
### 3) 目前进行JavaWeb项目开发的“套路”
- 拷贝myssm包
- 新建配置文件application.xml（可以不叫这个名字或者不写该文件并在web.xml文件中指定）
- 在web.xml文件中配置：  
  - 配置前缀和后缀，这样thymeleaf引擎就可以根据我们返回的字符串进行拼接和跳转
  - 配置监听器读取的参数，目的是加载IOC容器的配置文件（即application.xml）
- 开发具体业务模块：
  - 1. 一个具体的业务模块由这几个部分组成：  
    1） html页面  
    2） pojo类  
    3） DAO接口和实现类  
    4） Service接口和实现类  
    5） Controller控制器组件  
  - 2. 如果 html页面有thymeleaf表达式，一定不能直接访问，必须经过pageController
  - 3. 在applicationContext.xml（注意放在src目录下）中配置DAO、Service、Controller，以及三者间依赖关系
  - 4. DAO实现类中，继承BaseDAO，然后实现具体的接口（注意BaseDAO后面的泛型不能写错），例如 public class UserDAOImpl extends BaseDAO<User> implements UserDAO {}
  - 5. Service是业务控制类，只需要保证：  
    1） 业务逻辑都封装在Service层，不要分散在Controller层，也不要出现在DAO层，我们需要保证DAO方法的单精度特性  
    2） 当某一业务需要使用其他模块的业务功能时，尽量调用别人的service，而不要深入到其他模块的DAO细节
  - 6. Controller类的编写规则：  
    1） 在applicationContext.xml中配置Controller：  
        < bean id="user" class="com.fizz.qqzone.controller.UserController" >  
        那么用户在前端发请求时，对应的servletPath就是 /user.do ，其中"user"就是对应此处bean的id值  
    2） 在Controller中设计的方法名需要和operate的值一致：  
        public String login(String loginId, String pwd, HttpSession session) { return "index"; }  
        因此，我们的登录验证的表单如下：  
        < form th:action="@{user.do}" method="post">  
          < input type="hidden" name=operate" value="login"/>  
        < /form>  
    3） 在表单中，组件的name属性和Controller中方法的参数名一致： 
        < input type="text" name="loginId"/>  
        public String login(String loginId, String pwd, HttpSession session) {}  
    4） 另外，需要注意的是，Controller中的方法中的参数不一定都是通过请求参数（request.getParameter("...")）获取的，在DispatchServlet中对其反射时作了判断，如果是request/response/session则直接赋值
  - 7. DispatchServlet中步骤大致分为：  
    0） 从application作用域获取IOC容器（在初始化方法中，仅执行一次）  
    1） 解析ServletPath，在IOC容器中寻找对应的Controller组件  
    2） 准备operate指定的方法所要求的参数  
    3） 调用operate指定的方法  
    4） 接受到operate指定的方法的返回值，对返回值进行处理（视图解析）  
  - 8. 为什么DispatchServlet能从application作用域中获取IOC容器？  
    答：ContextLoaderListener（监听器）在容器启动时会执行初始化任务，而它的操作就是：  
    1） 解析IOC的配置文件，创建一个一个的组件，并完成组件间依赖关系的注入  
    2） 将IOC容器保存到application作用域


