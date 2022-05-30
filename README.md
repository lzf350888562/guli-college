# 谷粒商城

B2C模式

后台管理系统 和 前台用户系统

前后端分离

后端技术:springboot springcloud mybatisplus springsecurity redis maven easyExcel jwt oauth2

https://gitee.com/li-zhuofan-350562/guli-rear.git

前端技术:vue element-ui axios nodejs .....

https://gitee.com/li-zhuofan-350562/simple-front-end-learning.git

其他技术:阿里云oss 阿里云视频点播服务 阿里云短信服务 微信支付和登录 docker git jenkins....

# mybatisplus

BaseMapper<T> ,记得在配置类或主启动类扫包,

mybatisplus打印日志:

```
mybatis-plus.configuration.log-impl=org.apache.ibatis.logging.stdout.StdOutImpl
```

## 主键策略

https://www.cnblogs.com/haoxinyue/p/5208136.html

1.自动增长(常见)

2.UUID

3.redis生成ID

4.snowflack算法(mybatisplus自带)

在实体类要想主键自增的字段添加@TableId(type="ID_WORKER"),具体属性见官方注解解释.

最新的版本ID_WORKER已经过期,使用ASSIGN_ID.

## 自动填充

- 实现元对象处理器接口：com.baomidou.mybatisplus.core.handlers.MetaObjectHandler
- 注解填充字段 `@TableField(.. fill = FieldFill.INSERT)` 生成器策略部分也可以配置！
- 自定义实现类 MyMetaObjectHandler

举例见官网

## 乐观锁

主要解决丢失更新问题(并发情况):多人同时修改同一记录,最后提交的把之前提交的数据覆盖.

悲观锁也可以解决:在一个人操作一条记录的时候别人都不能操作,即串行操作,效率低,一般不使用悲观锁.

乐观锁实现方式:

取出记录时,先获取当前version;更新时,带上这个version;执行更新时,set version =  newVersion where version = oldVersion;如果version不对,就更新失败.(先查询,再修改)

mybatisplus实现:实体类添加version字段并添加@Version注解,和@TableField注解并指名fill属性为FieldFill.INSERT或INSERT_UPDATE实现自动填充,在自动填充处理类中设置默认值;

添加乐观锁插件

```
@Configuration
@MapperScan("按需修改")
public class MybatisPlusConfig {
    /**
     * 旧版
     */
    @Bean
    public OptimisticLockerInterceptor optimisticLockerInterceptor() {
        return new OptimisticLockerInterceptor();
    }
    
    /**
     * 新版
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        mybatisPlusInterceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        return mybatisPlusInterceptor;
    }
}
```

## 分页查询

旧版方式

```
@Bean
	public PaginationInterceptor paginationIntercepter(){
		return new PaginationInterceptor;
	}
```

new Page(pagenum,count)

mapper.selectPage(page,......)

## 逻辑删除

假删除,实际为更新.通过deleted字段标注.

```
update user set deleted=1 where id = 1 and deleted=0
```

配置

```
mybatis-plus:
  global-config:
    db-config:
      logic-delete-field: flag  # 全局逻辑删除的实体字段名(since 3.3.0,配置后可以忽略不配置步骤2)
      logic-delete-value: 1 # 逻辑已删除值(默认为 1)
      logic-not-delete-value: 0 # 逻辑未删除值(默认为 0)
```

 实体类字段上加上`@TableLogic`注解.

查询的时候也会忽略deleted=1的字段

旧版本还需要配置逻辑删除插件:

```
@Bean
public ISqlInjector sqlInjector(){
	return new LogicSqlInjector();
}
```

## 性能分析

```
 /**
     * SQL执行效率插件 性能分析插件
     */
    @Bean
    @Profile({"dev","test"})// 设置 dev test 环境开启
    public PerformanceInterceptor performanceInterceptor() {
        PerformanceInterceptor performanceInterceptor =  new PerformanceInterceptor();
        performanceInterceptor.setFormat(true);//格式化语句
        //performanceInterceptor.setMaxTime(500);//ms 执行时间超过多少秒会抛出异常
        return  performanceInterceptor;
```

spring-profiles.active设置当前环境.

## 条件构造器

Wrapper为顶端父类,一般用QueryWrapper<T>.

```
new QueryWrapper<User>
使用其条件方法,见官方文档.
wrapper.ge("age",30)   大于30
mapper.selectList(wrapper)
```

# 模块

guli-parent:父工程:

​	canal-client:canal数据库表同步模块,统计同步数据;

​	common:公共模块父节点;

​		common-util:工具类模块,所有模块都依赖

​		service-base:service服务的base包,包含service服务的公共配置类,所有的service模块都依赖

​		spring-security:认证与授权模块,需要认证授权的service服务都依赖

​	infranstructure:基础服务模块父节点

​		api-gateway:api网关服务

​	service:api接口服务父节点

​		service-acl:用户权限管理api服务

​		service-cms:cms api接口服务

​		service-edu:教学相关api接口服务

​		service-msm:短信api接口服务

​		service-order:订单api接口服务

​		service-oss:阿里云oss api接口服务

​		service-statistics:统计报表api接口服务

​		service-ucenter:会员api接口服务

​		service-vod:视频点播api接口服务

# 讲师管理模块后端

模块service-edu            8001

数据表edu_teacher

注意 数据库url为:

```
url: jdbc:mysql://localhost:3306/guli?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false
```

mybatisplus代码生成器

```
<!-- velocity 模板引擎, Mybatis Plus 代码生成器需要 -->
        <dependency>
            <groupId>org.apache.velocity</groupId>
            <artifactId>velocity-engine-core</artifactId>
        </dependency>
```

复制CodeGenerator.java到test下,修改生成.

json返回时间格式问题

```
#默认情况下json实际格式带有时区并且时世界标准实际,和我们的时间查了8个消失.  设置返回json全局时间格式
spring.jackson.date-format=yyyy-MM-dd HH:mm:ss
spring.jackson.time-zone=GMT+8
```

逻辑删除问题:

```
public static void main(String[] args) {
		SpringApplication.run(EduApplication.class, args);
	}
字段添加
@TableLogic
```

get请求以外的请求测试方法除了postman以外,这里使用swagger2:

## swagger

swagger功能:生成在线接口文档;方便接口测试.

在公共模块的service_base中整个swagger,为了所有模块都能使用

```
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger2</artifactId>
    <scope>provided </scope>
</dependency>
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger-ui</artifactId>
    <scope>provided </scope>
</dependency>
```

```
@Configuration
@EnableSwagger2  //swagger注解
public class SwaggerConfig {
   @Bean
   public Docket webApiConfig(){
      return new Docket(DocumentationType.SWAGGER_2)
            .groupName("webApi")
            .apiInfo(webApiInfo())
            .select()
            .paths(Predicates.not(PathSelectors.regex("/admin/.*")))
            .paths(Predicates.not(PathSelectors.regex("/error.*")))
            .build();
   }

   private ApiInfo webApiInfo() {
      return new ApiInfoBuilder()
            .title("网站-课程中心API文档")
            .description("本文档描述了课程中心微服务接口的定义")
            .version("1.0")
            .contact(new Contact("helen","http://baidu.com","xxx@qq.com"))
            .build();
   }
}
```

讲师管理模块所在的模块要获取到service-base中的配置,需要添加

```
@ComponentScan(basePackages = {"edu.hunnu"})  //扫描common的service_base中的内容
```

访问swagger

http://localhost:8001/swagger-ui.html

在里面可以测试包括get在内的所有请求,尝试删除一个再看数据库deleted字段.

swagger具体见官网.

坑:remove方法实现在版本3.3.0之前remove方法调用的是在IService的ServiceImpl中的delBool方法,这个方法的逻辑是只要程序运行没有报错remove方法就会一直返回true.



## 讲师分页功能

```
@Bean
	public PaginationInterceptor paginationIntercepter(){
		return new PaginationInterceptor();
	}
```

```
@GetMapping("{page}/{limit}")
public R pageList(@ApiParam(name = "page", value = "当前页码", required = true)
              @PathVariable Long page,

              @ApiParam(name = "limit", value = "每页记录数", required = true)
              @PathVariable Long limit) {
   Page<EduTeacher> pageParam = new Page<>(page, limit);

   teacherService.page(pageParam, null);
   List<EduTeacher> records = pageParam.getRecords();  //每一页数据的list集合
   long total = pageParam.getTotal();   //总记录条数

   return R.ok().data("total", total).data("rows", records);
}
```



## 多条件分页功能

条件值通过封装到vo对象里面传给接口.

```
@ApiModel(value = "Teacher查询对象", description = "讲师查询对象封装")
@Data
public class TeacherQuery implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "教师名称,模糊查询")
    private String name;
    @ApiModelProperty(value = "头衔 1高级讲师 2首席讲师")
    private Integer level;
    @ApiModelProperty(value = "查询开始时间", example = "2019-01-01 10:10:10")
    private String begin;//注意，这里使用的是String类型，前端传过来的数据无需进行类型转换
    @ApiModelProperty(value = "查询结束时间", example = "2019-12-01 10:10:10")
    private String end;
}
```

```
@ApiOperation(value = "分页讲师列表")
@PostMapping("pageTeacherCondition/{page}/{limit}")
public R pageQuery(
      @ApiParam(name = "page", value = "当前页码", required = true)
      @PathVariable Long page,
      @ApiParam(name = "limit", value = "每页记录数", required = true)
      @PathVariable Long limit,
      @ApiParam(name = "teacherQuery", value = "查询对象", required = false)
      //此注解如果加了在swagger中要输入完整json对象且必须post方式提交,不加可直接输入对象属性
      //因为RequestBody表示使用json传递数据,把json数据封装到对应的对象里面
      @RequestBody(required = false)
            TeacherQuery teacherQuery) {
   //创建page对象
   Page<EduTeacher> pageTeacher = new Page<>(page,limit);
   //构建条件
   QueryWrapper<EduTeacher> wrapper = new QueryWrapper<>();
   // 多条件组合查询

   // mybatis学过 动态sql
   String name = teacherQuery.getName();
   Integer level = teacherQuery.getLevel();
   String begin = teacherQuery.getBegin();
   String end = teacherQuery.getEnd();
   //判断条件值是否为空，如果不为空拼接条件
   if(!StringUtils.isEmpty(name)) {
      //构建条件
      wrapper.like("name",name);
   }
   if(!StringUtils.isEmpty(level)) {
      wrapper.eq("level",level);  //等于
   }
   if(!StringUtils.isEmpty(begin)) {
      wrapper.ge("gmt_create",begin);  //大于等于
   }
   if(!StringUtils.isEmpty(end)) {
      wrapper.le("gmt_create",end);  //小于等于
   }
   //排序
   wrapper.orderByDesc("gmt_create");
   //调用方法实现条件查询分页
   teacherService.page(pageTeacher,wrapper);
   long total = pageTeacher.getTotal();//总记录数
   List<EduTeacher> records = pageTeacher.getRecords(); //数据list集合
   return R.ok().data("total",total).data("rows",records);
}
```

## 添加和更新

前提:在实体类的两个属性上添加自动填充注解

```
@ApiModelProperty(value = "创建时间")
@TableField(fill = FieldFill.INSERT)  //插入时填充
private Date gmtCreate;

@ApiModelProperty(value = "更新时间")
@TableField(fill = FieldFill.INSERT_UPDATE)  //插入和更新时填充
private Date gmtModified;
```

commom 的service_base添加handler,注意 这里的fieldName为实体类的字段名称 而不是数据库字段名称  类似jpa.

```
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
	@Override
	public void insertFill(MetaObject metaObject) {
		this.setFieldValByName("gmtCreate",new Date(),metaObject);
		this.setFieldValByName("gmtModified",new Date(),metaObject);
	}
	@Override
	public void updateFill(MetaObject metaObject) {
		this.setFieldValByName("gmtModified",new Date(),metaObject);
	}
}
```

因为前面主启动类已经加了注解@ComponentScan(basePackages = {"edu.hunnu"}),所以可以获取该组件.

1.添加

```
//5、添加讲师
@ApiOperation(value = "新增讲师")
@PostMapping("addTeacher")
public R addTeacher(@ApiParam(name = "teacher", value = "讲师对象", required = true)
               @RequestBody EduTeacher eduTeacher) {

   boolean save = teacherService.save(eduTeacher);
   if (save) {
      return R.ok();
   } else {
      return R.error();
   }
}
```

添加完后测试数据(不需要id gmtCreate gmtModified )

```
{
  "avatar": "string",
  "career": "string",
  "intro": "string",
  "isDeleted": false,
  "level": 0,
  "name": "string",
  "sort": 0
}
```

2.更新两种方式

一

```
@ApiOperation(value = "根据ID查询讲师")
@GetMapping("getTeacher/{id}")
public R getTeacher(@ApiParam(name = "id", value = "讲师ID", required = true)
               @PathVariable String id) {

   EduTeacher eduTeacher = teacherService.getById(id);
   return R.ok().data("teacher", eduTeacher);
}

//7、id修改
@PostMapping("updateTeacher")
public R updateTeacher(@RequestBody EduTeacher eduTeacher){
   boolean flag = teacherService.updateById(eduTeacher);
   if (flag){
      return R.ok();
   }else {
      return R.error();
   }
}
```

二

```
@ApiOperation(value = "根据ID修改讲师")
@PutMapping("{id}")
public R updateById(@ApiParam(name="id",value="讲师ID",required = true)
                  @PathVariable String id,
               @ApiParam(name="teacher",value="讲师对象",required = true)
               @RequestBody EduTeacher teacher){
   teacher.setId(id);
   teacherService.updateById(teacher);
   return R.ok();
}
```

## 统一异常处理

有全局异常处理,特定异常处理,自定义异常处理.

在service_base的exceptionhandler中创建同一异常处理类.

```
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    //指定捕获的异常
    @ExceptionHandler(Exception.class)
    @ResponseBody  //返回json而不是错误页面
    public R error(Exception e){
        e.printStackTrace();
        return R.error().message("执行了全局异常处理");
    }

    //特定异常
    @ExceptionHandler(ArithmeticException.class)
    @ResponseBody
    public R error(ArithmeticException e){
        e.printStackTrace();
        return R.error().message("执行了ArithmeticException异常处理");
    }
    //自定义异常
    @ExceptionHandler(GuliException.class)
    @ResponseBody
    public R error(GuliException e){
        log.error(e.getMessage());
        e.printStackTrace();
        return R.error().message(e.getMsg()).code(e.getCode());
    }
}
```

机制:如果先找精确指定的异常方法,如果没有则执行全局异常.

##  统一日志处理(重要)

配置日志级别

OFF FATAL ERROR WARN INFO DEBUG ALL 

springboot默认INFO以上.

```
logging.level.root=warn
```

输出到文件的工具:log4j logback  .这里使用logback

去掉配置文件中的日志级别和mybatis-plus日志相关配置(冲突).

创建logback-spring.xml

```
<?xml version="1.0" encoding="UTF-8"?>
<configuration  scan="true" scanPeriod="10 seconds">
    <!-- 日志级别从低到高分为TRACE < DEBUG < INFO < WARN < ERROR < FATAL，如果设置为WARN，则低于WARN的信息都不会输出 -->
    <!-- scan:当此属性设置为true时，配置文件如果发生改变，将会被重新加载，默认值为true -->
    <!-- scanPeriod:设置监测配置文件是否有修改的时间间隔，如果没有给出时间单位，默认单位是毫秒。当scan为true时，此属性生效。默认的时间间隔为1分钟。 -->
    <!-- debug:当此属性设置为true时，将打印出logback内部日志信息，实时查看logback运行状态。默认值为false。 -->

    <contextName>logback</contextName>
    <!-- name的值是变量的名称，value的值时变量定义的值。通过定义的值会被插入到logger上下文中。定义变量后，可以使“${}”来使用变量。 -->
    <property name="log.path" value="D:/log/guli" />

    <!-- 彩色日志 -->
    <!-- 配置格式变量：CONSOLE_LOG_PATTERN 彩色日志格式 -->
    <!-- magenta:洋红 -->
    <!-- boldMagenta:粗红-->
    <!-- cyan:青色 -->
    <!-- white:白色 -->
    <!-- magenta:洋红 -->
    <property name="CONSOLE_LOG_PATTERN"
              value="%yellow(%date{yyyy-MM-dd HH:mm:ss}) |%highlight(%-5level) |%blue(%thread) |%blue(%file:%line) |%green(%logger) |%cyan(%msg%n)"/>

    <!--输出到控制台-->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <!--此日志appender是为开发使用，只配置最底级别，控制台输出的日志级别是大于或等于此级别的日志信息-->
        <!-- 例如：如果此处配置了INFO级别，则后面其他位置即使配置了DEBUG级别的日志，也不会被输出 -->
        <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
            <level>INFO</level>
        </filter>
        <encoder>
            <Pattern>${CONSOLE_LOG_PATTERN}</Pattern>
            <!-- 设置字符集 -->
            <charset>UTF-8</charset>
        </encoder>
    </appender>

    <!--输出到文件-->

    <!-- 时间滚动输出 level为 INFO 日志 -->
    <appender name="INFO_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <!-- 正在记录的日志文件的路径及文件名 -->
        <file>${log.path}/log_info.log</file>
        <!--日志文件输出格式-->
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} - %msg%n</pattern>
            <charset>UTF-8</charset>
        </encoder>
        <!-- 日志记录器的滚动策略，按日期，按大小记录 -->
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <!-- 每天日志归档路径以及格式 -->
            <fileNamePattern>${log.path}/info/log-info-%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                <maxFileSize>100MB</maxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
            <!--日志文件保留天数-->
            <maxHistory>15</maxHistory>
        </rollingPolicy>
        <!-- 此日志文件只记录info级别的 -->
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>INFO</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
    </appender>

    <!-- 时间滚动输出 level为 WARN 日志 -->
    <appender name="WARN_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <!-- 正在记录的日志文件的路径及文件名 -->
        <file>${log.path}/log_warn.log</file>
        <!--日志文件输出格式-->
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} - %msg%n</pattern>
            <charset>UTF-8</charset> <!-- 此处设置字符集 -->
        </encoder>
        <!-- 日志记录器的滚动策略，按日期，按大小记录 -->
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>${log.path}/warn/log-warn-%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                <maxFileSize>100MB</maxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
            <!--日志文件保留天数-->
            <maxHistory>15</maxHistory>
        </rollingPolicy>
        <!-- 此日志文件只记录warn级别的 -->
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>warn</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
    </appender>

    <!-- 时间滚动输出 level为 ERROR 日志 -->
    <appender name="ERROR_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <!-- 正在记录的日志文件的路径及文件名 -->
        <file>${log.path}/log_error.log</file>
        <!--日志文件输出格式-->
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} - %msg%n</pattern>
            <charset>UTF-8</charset> <!-- 此处设置字符集 -->
        </encoder>
        <!-- 日志记录器的滚动策略，按日期，按大小记录 -->
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>${log.path}/error/log-error-%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                <maxFileSize>100MB</maxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
            <!--日志文件保留天数-->
            <maxHistory>15</maxHistory>
        </rollingPolicy>
        <!-- 此日志文件只记录ERROR级别的 -->
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>ERROR</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
    </appender>

    <!--
        <logger>用来设置某一个包或者具体的某一个类的日志打印级别、以及指定<appender>。
        <logger>仅有一个name属性，
        一个可选的level和一个可选的addtivity属性。
        name:用来指定受此logger约束的某一个包或者具体的某一个类。
        level:用来设置打印级别，大小写无关：TRACE, DEBUG, INFO, WARN, ERROR, ALL 和 OFF，
              如果未设置此属性，那么当前logger将会继承上级的级别。
    -->
    <!--
        使用mybatis的时候，sql语句是debug下才会打印，而这里我们只配置了info，所以想要查看sql语句的话，有以下两种操作：
        第一种把<root level="INFO">改成<root level="DEBUG">这样就会打印sql，不过这样日志那边会出现很多其他消息
        第二种就是单独给mapper下目录配置DEBUG模式，代码如下，这样配置sql语句会打印，其他还是正常DEBUG级别：
     -->

<!--    开发环境对应与application.properties文件-->
    <!--开发环境:打印控制台-->
    <springProfile name="dev">
        <!--可以输出项目中的debug日志，包括mybatis的sql日志-->
        <logger name="com.guli" level="INFO" />

        <!--
            root节点是必选节点，用来指定最基础的日志输出级别，只有一个level属性
            level:用来设置打印级别，大小写无关：TRACE, DEBUG, INFO, WARN, ERROR, ALL 和 OFF，默认是DEBUG
            可以包含零个或多个appender元素。
        -->
        <root level="INFO">
            <appender-ref ref="CONSOLE" />
            <appender-ref ref="INFO_FILE" />
            <appender-ref ref="WARN_FILE" />
            <appender-ref ref="ERROR_FILE" />
        </root>
    </springProfile>


    <!--生产环境:输出到文件-->
    <springProfile name="pro">

        <root level="INFO">
            <appender-ref ref="CONSOLE" />
            <appender-ref ref="DEBUG_FILE" />
            <appender-ref ref="INFO_FILE" />
            <appender-ref ref="ERROR_FILE" />
            <appender-ref ref="WARN_FILE" />
        </root>
    </springProfile>

</configuration>
```

如果想把异常信息输出到文件中去,则在统一异常处理类上添加注解@Slf4j并通过log.error(e.getMessage());输出.

如果需要输出详细的异常栈信息,则可以在common的common_utils中的包utils下新建类

```
package edu.hunnu.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 用于返回异常栈的详细信息
 */
public class ExceptionUtil {

    public static String getMessage(Exception e) {
        StringWriter sw = null;
        PrintWriter pw = null;
        try {
            sw = new StringWriter();
            pw = new PrintWriter(sw);
            // 将出错的栈信息输出到printWriter中
            e.printStackTrace(pw);
            pw.flush();
            sw.flush();
        } finally {
            if (sw != null) {
                try {
                    sw.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (pw != null) {
                pw.close();
            }
        }
        return sw.toString();
    }
}
```

并通过log.error(ExceptionUtil.getMessage(e));来输出

# 前端vscode

安装插件live server|vetur|vue-helper

创建文件夹vscode打开,将文件夹另存为工作区.    

在工作区新建文件夹,文件.

!快速创建html模板

# ES6

ECMAScript6为javascript标准.

https://es6.ruanyifeng.com/#docs/let

let与const

解构赋值

模板字符串

方法定义简写

```
const f2={
        sayHi(){
            console.log("hi ****")
        }
    }
    f2.sayHi()
```

对象拓展运算符(对象复制,对象合并)

箭头函数

# vue

用于构建用户界面的渐进式框架.

需要引入vue的js文件,类似jquery.

复制vue.min.js到文件夹中

```
<script src = "vue.min.js"></script>
```

快速入门,格式比较固定.

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
</head>
<body>
    <div id = "app">
        <!-- //获取data里面定义的内容   {{}} -->
        {{message}}
    </div>
    <script src = "vue.min.js"></script>
    <script>
        new Vue({
            el: '#app',  //绑定vue作用范围
            data:{
                //定义页面中显示的模型数据
                message: 'hello Vue!!'
            }
        })
    </script>
</body>
</html>
```

vscode创建vue模板

文件首选项-添加用户首选项

```html
{
	"vue htm": {
		"scope": "html",
		"prefix": "vuehtml",
		"body": [
			"<!DOCTYPE html>",
			"<html lang=\"en\">",
			"<head>",
			"	<meta charset=\"UTF-8\">",
			"	<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">",
			"	<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">",
			"	<title>Document</title>",
			"</head>",
			"<body>",
			"	<div id = \"app\">",
			"",
			"	</div>",
			"	<script src = \"vue.min.js\"></script>",
			"	<script>",
			"		new Vue({",
			"			el: '#app',",
			"			data:{",
			"				$1",
			"			}",
			"		})",
			"	</script>",
			"</body>",
			"</html>",	
		],
		"description": "my vue template"
	}
}
```

输出prefix指定的内容即可输出模板

## v-bind单向数据绑定

```html
<body>
    <div id = "app">
        <!-- v-bind指令演示 单项数据绑定  用于标签属性里,获取值-->
        <!-- title表示鼠标停留显示的内容-->
        <h1 v-bind:title="message">
            {{content}}
        </h1>
        <!-- 简写 -->
        <h2 :title="message">
            {{content}}
        </h2>
    </div>
    <script src = "vue.min.js"></script>
    <script>
        new Vue({
            el: '#app',
            data:{
                content: '我是标题',
                message: '页面加载于 '+new Date().toLocaleDateString()
            }
        })
    </script>
</body>
```

## v-model双向数据绑定

```html
<body>
    <div id = "app">
        <!-- 只改变自己 -->
        <input type="text" v-bind:value="searchMap.keyword"></input>
        <!-- 改变所有 -->
        <input type="text" v-model="searchMap.keyword"></input>
        <p>{{searchMap.keyword}}</p>
    </div>
    <script src = "vue.min.js"></script>
    <script>
        new Vue({
            el: '#app',
            data:{
                searchMap:{
                    keyword:'尚硅谷'
                }
            }
        })
    </script>
</body>
```

## v-on事件绑定

```html
<body>
    <div id = "app">
        <button v-on:click="search()">查询</button>
        <!-- 简写  还可以不加括号-->
        <button @click="f1()">查询2</button></button>
    </div>
    <script src = "vue.min.js"></script>
    <script>
        new Vue({
            el: '#app',
            data:{
                searchMap:{
                    keyword:'尚硅谷'
                },
                //查询结果
                result:{}
            },
            methods:{//定义多个方法
                search(){
                    console.log('search......')
                },
                f1(){
                    console.log('f1 ......')
                }
            }
        })
    </script>
</body>
```

## .prevent修饰符

用于指出一个指令应该以特殊方式绑定,,阻止默认行为.

```html
<body>
    <div id = "app">
        <!-- 不会提交表单，而是执行指定方法 -->
        <form action="save" v-on:submit.prevent="onSubmitform">
            <input type="text" id="name" v-model="user.name"></input>
            <button type="submit">保存</button>
        </form>
    </div>
    <script src = "vue.min.js"></script>
    <script>
        new Vue({
            el: '#app',
            data:{
                user:{}
            },
            methods:{
                onSubmitform(){
                    if(this.user.name){
                        console.log('提交表单')
                    }else{
                        alert('请输入用户名')
                    }
                }
            }
        })
    </script>
</body>
```

## v-if v-show条件渲染

```html
<body>
    <div id = "app">
        <!-- v-model绑定checkbox的默认值，这里为false，选择变为true -->
        <input type="checkbox" v-model="ok" />是否同意

        <h1 v-if="ok">111111111</h1>
        <h1 v-else>2222222222</h1>

        <h1 v-show="ok">333333333</h1>
        <h1 v-show="!ok">4444444444</h1>
    </div>
    <script src = "vue.min.js"></script>
    <script>
        new Vue({
            el: '#app',
            data:{
                ok:false
            }
        })
    </script>
</body>
```

`v-if` 是“真正”的条件渲染，因为它会确保在切换过程中条件块内的事件监听器和子组件适当地被销毁和重建。

`v-if` 也是**惰性的**：如果在初始渲染时条件为假，则什么也不做——直到条件第一次变为真时，才会开始渲染条件块。

相比之下，`v-show` 就简单得多——不管初始条件是什么，元素总是会被渲染，并且只是简单地基于 CSS 进行切换。

一般来说，`v-if` 有更高的切换开销，而 `v-show` 有更高的初始渲染开销。因此，如果需要非常频繁地切换，则使用 `v-show` 较好；如果在运行时条件很少改变，则使用 `v-if` 较好。

## v-for列表渲染

```html
<body>
    <div id = "app">
        <ul>  <!-- 简单使用之 从1开始 遍历输出-->
            <li v-for="n in 5">{{n}}</li>
        </ul>

        <ol>   <!-- 使用索引-->
            <li v-for="(n,index) in 5">{{n}} --{{index}}</li>
        </ol>

        <hr/>    
        <table> <!--简单应用-->
            <tr v-for="user in userList">
                <td>{{user.id}}</td>
                <td>{{user.username}}</td>
                <td>{{user.age}}</td>
            </tr>
        </table>
    </div>
    <script src = "vue.min.js"></script>
    <script>
        new Vue({
            el: '#app',
            data:{
                userList:[
                    { id:1, username:'tom', age:11},
                    { id:2, username:'jack', age:22},
                    { id:3, username:'mick', age:33}
                ]
            }
        })
    </script>
</body>
```

## 组件

可扩展html元素,封装可重用的代码.

局部组件

```js
new Vue({
  el: '#app',
  //定义局部组件,这里可以定义多个局部组件
  components: {
  //局部组件名字
    'component-a': {        //组件的内容
    	template: '<ul><li>首页</li><li>附页</li></ul>'
    }
  }
})
```

全局组件

新建文件components/component-a.js

```js
Vue.component('component-a', {
    template: '<ul><li>首页</li><li>附页</li></ul>'
  })
```

引入后可以直接使用

```html
<body>
    <div id = "app">
        <component-a></component-a>
    </div>
    <script src = "vue.min.js"></script>
    <script src="components/component-a.js"></script>
    <script>
        new Vue({
            el: '#app',
            data:{
                
            }
        })
    </script>
</body>
```

## 实例生命周期

https://cn.vuejs.org/images/lifecycle.png

重点为created(在页面渲染之前执行)和mounted(在页面渲染之后执行)方法.

```html
<body>
    <div id = "app">
        hello
    </div>
    <script src = "vue.min.js"></script>
    <script>
        new Vue({
            el: '#app',
            data:{
            },
            //在页面渲染之前执行
            created(){
                debugger
                console.log('created........')
            },
            //在页面渲染之后执行
            mounted(){
                debugger
                console.log('mounted........')
            }
        })
    </script>
</body>
```

debug可以看到两个方法先后被执行.

## 路由

允许我们通过不同的url访问不同的内容

需要载入vue-router库

```html
<body>
    <div id = "app">
        <h1>hello rounter</h1>
        <p>
            <router-link to="/">首页</router-link>
            <router-link to="/student">学生页</router-link>
            <router-link to="/teacher">老师页</router-link>
        </p>
        <!-- 路由出口 -->
        <router-view></router-view>
    </div>
    <script src = "vue.min.js"></script>
    <script src = "vue-router.min.js"></script>
    <script>
        // 定义路由组件
        const Welcome = { template:'<div>欢迎Welcome</div>'}
        const Student = { template:'<div>学生Student</div>'}
        const Teacher = { template:'<div>老师Teacher</div>'}
        //定义路由
        const routes = [
            {path:'/',redirect:'/welcome'},
            {path:'/welcome',component:Welcome},
            {path:'/student',component:Student},
            {path:'/teacher',component:Teacher},
        ]
        //创建router实例，然后传routes配置
        const router = new VueRouter({
            routes   //缩写 相当于routes: routes
        })
        //创建和挂载根实例
        new Vue({
            el: '#app',
            router
        })
    </script>
</body>
```

# axios

在vue中发送ajax请求,但独立于vue.

使用:引入两个js文件:vue.min.js 和 axios.min.js

使用: axios.提交方式("请求接口路径") 这里使用json文件模拟后端接口

data.json

```json
{
    "sucess":true,
    "code":20000,
    "message":"成功",
    "data":{
        "items":[
            {"name":"lucy","age":20},
            {"name":"mary","age":30},
            {"name":"jack","age":40}
        ]
    }
}
```

```html
<body>
    <div id = "app">
        <!--使用列表渲染 显示userList 这里为了简单没有使用table -->
        <div v-for="user in userList">
            {{user.name}}--{{user.age}}
        </div>
    </div>
    <script src = "vue.min.js"></script>
    <script src = "axios.min.js"></script>
    <script>
        new Vue({
            el: '#app',
            data:{  
                userList:[]
            },
            created(){  //调用定义的方法
                this.getUserList()
            },
            methods:{   //编写具体的方法  发送ajax请求
                getUserList(){
                    //axios.提交方式("请求接口路径") 这里使用json文件模拟后端接口
                    axios.get("data.json")
                        .then(response => {
                            // console.log(response)
                            //通过response获取具体数据，赋值给空数组
                            this.userList=response.data.data.items
                            console.log(this.userList)
                        })  //请求成功执行then方法
                        .catch(error =>{
                            console.log(error)
                        })    //请求失败执行catch方法
                }
            }
        })
    </script>
</body>
```



# element-ui

基于vuejs的后台组件库,页面快速布局和搭建.

https://element.eleme.cn/#/zh-CN 具体使用时查看组件详情

# nodejs

运行在服务端的js,可理解为js的运行环境.不需要浏览器直接使用nodejs运行js代码.并模拟服务器效果比如tomcat,基于Chrome的v8引擎.

cmd->node->node js文件.

```js
const http = require('http');
http.createServer(function(request,response){
    response.writeHead(200,{'Content-Type':'text/plain'});
    response.end('Hello server');
}).listen(8888);
console.log('Server running at http://127.0.0.1:8888/');
```

# npm

nodejs包管理工具,类似maven,nodejs自动安装.使用命令npm -v查看

修改npm镜像为淘宝.

npm config set registry https://registry.npm.taobao.org

npm config list 查看

1.nps项目初始化 npm init ,设置信息.生成package.json文件.

npm init -y 一键,设置选择默认.

2.安装依赖包

npm install

如 npm install jquery,发现多了package-lock.json文件(用于锁定版本)和node_modules中有jquery文件夹.

安装指定版本 npm install jquery@2.1.x.

3.根据package.json文件下载依赖

npm install  不带参数!

4.插件安装

npm install --save-dev eslint或npm install -D eslint

全局安装

npm install -g webpack

# babel

转码器,把代码es6转换为es5,因为es6有时兼容性不好.

1.安装babel工具(先npm初始化)

npm install  --global babel-cli

babel --version

2.创建js文件,编写es6代码

```
let input = [1,2,3]
input=input.map(item=>item+1)
console.log(input)
```

3.新建 .babelrc文件

```
{
    "presets":["es2015"],
    "plugins":[]
}
```

4.安装转码器

npm install --save-dev babel-preset-es2015

5.转码

根据文件

babel es6/01.js -o dist/001.js

根据文件夹(不会创建文件夹)

babel es6 -d dist

# 前端模块化

js与js之间的调用 

一.es5实现模块化

先npm init -y

在01.js定义js方法,在02.js中调用01.js里面的方法.

1.创建module文件夹

01.js

```js
//定义js方法给02使用
const sum =  function(a,b){
    return parseInt(a)+parseInt(b)
}
const subtract = function(a,b){
    return parseInt(a)-parseInt(b)
}
//设置哪些方法可以被其他js调用
module.exports = {
    sum,
    subtract
}
```

2.导出模块

02.js

```js
//调用01.js方法
//引入
const m = require('./01.js')
//调用
console.log(m.sum(1,2))
console.log(m.subtract(5,1))
```

3.测试 node 02.js

二.es6实现模块化

es6的模块化无法在nodejs中执行,需要用babel编辑成es5才能执行

第一种方式:

01.js

```js
export function getList(){
    console.log('getList..........')
}
export function save(){
    console.log('save..........')
}
```

02.js

```js
import {getList,save} from './01.js'

getList()
save()
```

直接node 02.js会报错,需要使用babel转换(根据文件夹转换).

```
babel module-es6-1 -d module-es5-1 
```

现在可以node 02.js了

第二种方式(多):

01.js

```js
export default{
    getList(){
        console.log('getList..........')
    },
    update(){
        console.log('update..........')
    }
}
```

02,js

```js
import m from './01.js'

m.getList()
m.update()
```

转换

```
babel module-es6-2 -d module-es5-2
```

# webpack

前端资源加载/打包工具,可将多种静态资源js,css,less转换成一个静态文件,减少页面的请求.

1.安装 两个   (先npm init -y)

npm install -g webpack webpack-cli

webpack -v

2.在src下创建common.js,

```js
exports.info=function(str) {
    console.log()   //终端输出
    document.write(str)  //浏览器输出
  }
```

3.在src下创建utils.js

```js
exports.add=function(a,b) {
    return a+b;
  }
```

4.在src下创建main.js,引入其他两个js文件

```js
const common = require('./common.js')
const utils = require('./utils.js')

common.info('hello common'+utils.add1(1,2))
```

5.在根目录下创建webpack.config.js 固定结构为

```js
const path = require("path");
module.exports={
    entry: './src/main.js',  //配置入口文件
    output: {
        path: path.resolve(__dirname,'./dist'), //输出路径,__dirname:当前文件所在路径
        filename: 'bundle.js'   //输出文件
    }
}
```

6.打包命令

webpack      #有黄色警告

webpack --mode=development   #没有警告  且打包文件格式化显示

7.效果

在跟目录下创建01.html,引入dist/bundle.js

```
<script src="dist/bundle.js"></script>
```

访问此页面

注意,打包前的文件必须保存,



打包css

1.src创建style.css

```css
body{
    background-color: burlywood;
}
```

2.在main.js中引入

```js
const common = require('./common.js')
const utils = require('./utils.js')

require('./style.css')

common.info('hello common'+utils.add(1,2))
```

3.安装style-loader和css-loader,因为webpack本身只能处理js模块,如果要处理其他模块,需要loader转换.

npm install --save-dev style-loader css-loader

4.修改配置文件

```
const path = require("path");
module.exports={
    entry: './src/main.js',  //配置入口文件
    output: {
        path: path.resolve(__dirname,'./dist'), //输出路径,__dirname:当前文件所在路径
        filename: 'bundle.js'   //输出文件
    },
    module:{
        rules:[
            {
                test: /\.css$/,   //打包规则应用到以css结尾的文件上  正则
                use: ['style-loader','css-loader']
            }
        ]
    }
}
```

5.重新打包

webpack --mode=development

注意 引入的时候默认为js文件,所以.js可以省略  ,.css不能省略.

浏览器再访问01.html



打包高级:也可以配置项目的npm运行命令,修改package.json文件

```
"scripts":{
	//...,
	"dev":"webpack --mode=development"
}
```

然后npm run dev打包

# 前端页面

前端页面框架,vue-admin-template(主要基于vue+element-ui实现.)

解压vue-admin-template-master.zip(vue-element-admin-master功能更多)

vscode打开,通过配置文件下载依赖

npm install

启动  npm run dev 当终端显示Your application is running here: http://localhost:9528时候访问浏览器.

## 框架介绍

1.前端框架入口(在package.json->build/webpack.dev.conf.js中指定)

index.html

src/main.js

2.build目录

放项目构建的脚本文件

3.config目录全局配置

<u>index.js</u>  可配置host port.

将useEslint改为false(代码格式检查工具,过严格)

<u>dev.env.js</u> ke :点击页面的login查看请求url以 

BASE_API: '"https://easy-mock.com/mock/5950a2419adc231f356a6636/vue-admin"' 

为前缀.后面需要使用自己的本地接口

prod.env.js:

4.src目录 项目源代码

<u>api</u>:定义调用方法

assets:图片等静态资源

component:公共组件

icons:图标

<u>router</u>:路由

style:样式

utils:公共工具类

<u>view</u>:具体页面 .vue

permission.js:认证入口

App.vue:项目顶层组件

## 讲师管理模块前端

把后台管理系统登录改成本地(临时),后面把登录添加权限框架(spring secutiry)

1.url前缀格式修改:config/dev.env.js

```js
'use strict'
const merge = require('webpack-merge')
const prodEnv = require('./prod.env')

module.exports = merge(prodEnv, {
  NODE_ENV: '"development"',
  // BASE_API: '"https://easy-mock.com/mock/5950a2419adc231f356a6636/vue-admin"',
  BASE_API: '"http://localhost:8001"',  //主要 http 
})
```

进行登录调用login方法和登录之后获取信息的info方法:

login返回token值;

info返回roles name avatar.

2.url后缀访问修改:src/api/login.js

```js
export function login(username, password) {
  return request({
    url: '/eduservice/user/login',
    method: 'post',
    data: {
      username,
      password
    }
  })
}
export function getInfo(token) {
  return request({
    url: '/eduservice/user/info',
    method: 'get',
    params: { token }
  })
}
```

## 后端接口整合

```java
@RestController
@RequestMapping("/eduservice/user")
@CrossOrigin //解决跨域 只能和springcloud中的gateway中的跨域只能存在一个
public class EduLoginController {
   //login
   @PostMapping("login")
   public R login(){
      return R.ok().data("token","admin");
   }
   //info
   @GetMapping("info")
   public R info(){
      return R.ok().data("roles","[admin]").data("name","admin").data("avatar","https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
   }
}
```

注意,如果不加@CrossOrigin会出现Access-Control-Allow-Origin 跨域访问问题.

跨域的标准为:访问协议 ip地址 端口号 ,  任意不一致就不可以访问,这里为端口(一个是前端默认9528一个是8001).

还有一种解决方法是使用网关解决.

启动8001 启动前端npm run dev .浏览器点击登录.

在f12 network中可能看到login和info都多了一条方式为OPTIONS的请求,这是一条测试连通性的请求,测试成功后再发送真实请求.

## 路由与教师列表

点击不同内容显示不同页面,这里添加讲师添加页面

1.在src/router/index.js中添加路由:在左侧添加讲师管理导航(每次修改都要保存,包括其他文件)

```js
//lzf:添加讲师管理路由导航
  {
    path: '/teacher',
    component: Layout,
    redirect: '/teacher/table',
    name: '讲师管理',
    meta: { title: '讲师管理', icon: 'example' }, 
    children: [
      {
        path: 'table',
        name: '讲师列表',
        component: () => import('@/views/edu/teacher/list'),
        meta: { title: '讲师列表', icon: 'table' }
      },
      {
        path: 'save',
        name: '添加讲师',
        component: () => import('@/views/edu/teacher/save'),
        meta: { title: '添加讲师', icon: 'tree' }
      }
    ]
  },
```

2.创建路由对应页面,在views中创建页面修改 xxx/index.vue,这里为edu/teacher/list.vue和edu/teacher/save.vue

list.vue

```vue
<template>
  <div class="app-container">
      讲师表单
  </div>
</template>
```

3.在api创建js文件,定义访问的接口地址..

edu/teacher.js

```js
import request from '@/utils/request'  //固定  request里面封装了axios的方法

export default{
    //讲师列表 (条件查询分页)  匹配后端接口
    //current当前页 limit每页记录数 teacherQuery条件对象
    getTeacherListPage(current,limit,teacherQuery){
        return request({
            // url: '/eduservice/teacher/pageTeacherCondition/'+current+'/'+limit,
            url: `/eduservice/teacher/pageTeacherCondition/${current}/${limit}`,
            method: 'post',
            //teacherQuery条件对象,后端使用RequestBody获取
            //data表示把对象转换成json进行传递到接口
            data: teacherQuery
          })
    }
}
```

4.在路由对应界面里面调用定义的接口方法,得到返回的数据.

这里修改list.vue

```vue
<template>                                  <!--template固定-->
  <div class="app-container">
      讲师表单
  </div>
</template>
<script>
//引入teacher.js文件
import teacher from '@/api/edu/teacher'
export default {
    //核心代码位置 不需要new Vue了 在main中已经完成.
    // data:{
    //},
    data(){  //定义变量和初始值
        return {
            list:null,  //查询之后接口返回的集合
            page:1, //当前页
            limit:10,//每页记录数
            total:0,//总记录数
            teacherQuery:{}  //查询条件 默认没条件
        }
    },
    created(){  //页面渲染之前执行,一般调用methods定义的方法
        this.getList()
    },
    methods:{  //创建具体的方法,调用teacher.js定义的方法
        //讲师列表的方法
        getList(){
            // axios.post("")已被封装
            teacher.getTeacherListPage(this.page,this.limit,this.teacherQuery)
                .then(response =>{
                    // console.log(response) 可用来获取数据路径
                    this.list=response.data.rows
                    this.total=response.data.total
                    console.log(this.list)
                    console.log(this.total)
                })  //请求成功
                .catch(error =>{
                    console.log(error)
                })    //请求失败
        }
    }
}
</script>
```

5.把请求接受到的数据在页面中进行显示

在element-ui的组件中选择table 表格,参考代码,修改list.vue

```vue
<template>                                  <!--template固定-->
  <div class="app-container">
      讲师列表
      <!-- 单向绑定 -->
      <el-table
      v-loading="listLoading"
      
         :data="list"       
         element-loading-text="数据加载中"
         border
         fit
         highlight-current-row>
        <el-table-column
            label="序号"
            width="70"
            align="center">
            <!-- scope表示整个表格 -->
            <template slot-scope="scope">
                {{ (page-1) * limit + scope.$index + 1 }}
            </template>
        </el-table-column>
        <el-table-column prop="name" label="名称" width="80"></el-table-column>
        <el-table-column label="头衔" width="80">
            <template slot-scope="scope">
                <!-- 三个==判断值与类型 -->
                {{  scope.row.level===1?'高级讲师':'首席讲师'}}
            </template>
        </el-table-column>
        <el-table-column prop="intro" label="资历"></el-table-column>
        <el-table-column prop="gmtCreate" label="添加时间" width="160"></el-table-column>
        <el-table-column prop="sort" label="排序" width="60"></el-table-column>
        <el-table-column label="操作" width="200" align="center">
            <template slot-scope="scope">
                <router-link :to="'/edu/teacher/edit/'+scope.row.id">
                    <el-button type="primary" size="mini" icon="el-icon-edit">修改</el-button>
                </router-link>
                <el-button type="danger" size="mini" icon="el-icon-delete" @click="removeDataById(scope.row.id)">
                    删除
                </el-button>
            </template>
        </el-table-column> 
    </el-table>
  </div>
</template>
```

## 分页

在element-ui的组件中选择Pagination 分页,参考代码,修改list.vue

在<el-table>下面加入

```vue
<!--分页  @为v-on的简写表示绑定事件 -->
   <el-pagination 
        :current-page="page" 
        :page-size="limit" 
        :total="total" 
        @current-change="getList" 
        layout="total, prev, pager, next, jumper"
        style="padding: 30px 0;text-align: center">
    </el-pagination>
```

因为分页事件触发会传参数页数,所以修改以下同文件的getList方法,让他能做到页面跳转.

```js
getList(page =1){
            this.page=page
            // axios.post("")已被封装
            teacher.getTeacherListPage(this.page,this.limit,this.teacherQuery)
                .then(response =>{
                    // console.log(response) 可用来获取数据路径
                    this.list=response.data.rows
                    this.total=response.data.total
                    console.log(this.list)
                    console.log(this.total)
                })  //请求成功
                .catch(error =>{
                    console.log(error)
                })    //请求失败
        }
```

## 条件查询

在element-ui的组件中选择Form 表单 之 行内表单,参考代码,修改list.vue

在<el-table>上面加入

```html
 <el-form :inline="true" class="demo-form-inline">
      <el-form-item>
        <el-input v-model="teacherQuery.name" placeholder="讲师名"></el-input>
      </el-form-item>
      <el-form-item>
        <el-select v-model="teacherQuery.level" clearable placeholder="讲师头衔">
            <!-- 数据类型要与取出的json一致,所以动态绑定数字 -->
          <el-option label="高级讲师" :value="1"></el-option>
          <el-option label="首席讲师" :value="2"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="添加时间">
          <!-- 时间选择框 -->
        <el-date-picker
          v-model="teacherQuery.begin"
          type="datetime"
          placeholder="选择开始时间"
          value-format="yyyy-MM-dd HH:mm:ss"
          default-time="00:00:00"
        ></el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-date-picker
          v-model="teacherQuery.end"
          type="datetime"
          placeholder="选择截止时间"
          value-format="yyyy-MM-dd HH:mm:ss"
          default-time="00:00:00"
        ></el-date-picker>
      </el-form-item>
      <el-button 
            type="primary" 
            icon="el-icon-search" 
            @click="getList()"
      >查询</el-button>
        <el-button type="default" @click="resetData()"
      >清空</el-button>
    </el-form>
```

清空方法

```js
resetData(){//清空的方法
        //清空输入项数据
        this.teacherQuery={}
        //查询所有讲师数据
        this.getList()
    }
```

## 删除

前面已经在每条记录后面添加了删除按钮并绑定了事件,参数为讲师id.

在api下的edu/teacher.js定义删除接口

```js
deleteTeacherId(id){//删除讲师
        return request({
            url: `/eduservice/teacher/${id}`,
            method: 'delete'
          })
    },
```

views下edu/teacher/list.vue调用teacher.js并添加删除确认的步骤.

在element-ui的组件中选择MessageBox 弹窗,参考代码之确认消息,修改list.vue

添加方法

```js
removeDataById(id) {
      //删除讲师
      // alert(id)
      this.$confirm("此操作将永久删除讲师记录,是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }).then(() => {
        //点击确认 执行then
        teacher.deleteTeacherById(id)
           .then((response) => {        //删除成功 提示信息
          this.$message({
            type: "success",
            message: "删除成功!",
          });
          this.getList(); //刷新页面
        });//确认取消 不catch
      });//删除失败 不catch
    },
```

## 添加和修改

前面已经配置save路由,修改跳转到添加页面并显示数据.

删除

在src/api/edu/teacher.js中添加方法

```js
addTeacher(teacher){//添加讲师
        return request({
            url: `/eduservice/teacher/addTeacher`,
            method: 'post',
            data: teacher
          })
    }
```

在views新建edu/teacher/save.vue

```vue
<template>
  <!--template固定-->
  <div class="app-container">
    讲师添加
    <el-form label-width="120px">
        <el-form-item label="讲师名称">
            <el-input v-model="teacher.name"></el-input>
        </el-form-item>
        <el-form-item label="讲师排序">
            <el-input-number v-model="teacher.sort" controls-position="right" :min="0">
            </el-input-number>
        </el-form-item>
        <el-form-item label="讲师头衔">
            <el-select v-model="teacher.level" clearable placeholder="请选择">
                <el-option label="高级讲师" :value="1"></el-option>
                <el-option label="首席讲师" :value="2"></el-option>
            </el-select>
        </el-form-item>
        <el-form-item label="讲师资历">
            <el-input v-model="teacher.career"></el-input>
        </el-form-item>
        <el-form-item label="讲师简历">
            <el-input v-model="teacher.intro" :rows="10" type="textarea"></el-input>
        </el-form-item>
        <!-- 讲师头像 :TODO-->

        <el-form-item>
            <!-- :disabled是否关闭重复提交 -->
            <el-button :disabled="saveBtnDisabled" type="primary" @click="saveOrUpdate">
                保存
            </el-button>
        </el-form-item>
    </el-form>
  </div>
</template>
<script>
import teacherApi from "@/api/edu/teacher";
export default {
  data() {
    return {
        teacher:{
            // 如list.vue一样 这些属性都可以省略
            name:'',
            sort:0,
            level:1,
            carrer:'',
            intro:'',
            avatar:''
        },
        saveBtnDisabled:false   //是否禁用重复提交
    };
  },
  created() {

  },
  methods: {
      saveOrUpdate(){
          this.saveTeacher()
      },
      saveTeacher(teacher){
          teacherApi.addTeacher(this.teacher)
            .then(response =>{//添加成功
                //提示信息
                this.$message({
                    type:'success',
                    message:'删除成功'
                });
                //回到列表页面  路由跳转!!!  固定格式
                this.$router.push({path:'/teacher/table'})
            })
      }
  },
};
</script>
```

修改部分:因为在前面设置了修改按钮

```html
<router-link :to="'/teacher/edit/'+scope.row.id">
    <el-button type="primary" size="mini" icon="el-icon-edit">修改</el-button>
</router-link>
```

还需要在路由index讲师管理下添加路由

```js
{
        path: 'edit/:id',  //:id相当为占位符
        name: '添加讲师',
        component: () => import('@/views/edu/teacher/save'),
        meta: { title: '编辑讲师', noCache:true },
        hidden: true   //
      }
```

数据回显接口和修改接口,在src/api/edu/teacher.js中添加方法

```js
getTeacherInfo(id){//查询讲师
        return request({
            url: `/eduservice/teacher/getTeacher/${id}`,
            method: 'get'
          })
    },
updateTeacherInfo(teacher){//修改讲师
        return request({
            url: `/eduservice/teacher/updateTeacher`,
            method: 'post',
            data: teacher
        })
    }
```



因为添加和修改使用一个页面,区别添加和修改,只有修改的时候查询数据回显.判断路径里是否有id.  

save.vue

```vue
created() { //判断是否为修改
      if(this.$route.params&&this.$route.params.id){
          //获取路径中的id
          const id = this.$route.params.id
          this.getInfo(id)
      }
  },
```

相关方法

```js
 getInfo(id){  //根据id查询
        teacherApi.getTeacherInfo(id)
            .then(response=>{
                this.teacher=response.data.teacher
            })
      },
      saveOrUpdate(){
          //根据teacher中是否有id来判断是修改还是添加
          if(!this.teacher.id){  //添加
            this.saveTeacher()
          }else{    //修改
            this.updateTeacher()
          }
      },
      updateTeacher(){
          teacherApi.updateTeacherInfo(this.teacher)
            .then(response =>{
                this.$message({
                    type:'success',
                    message:'修改成功'
                });
                this.$router.push({path:'/teacher/table'})
            })
      },
```

## BUG

从修改页面效果不能直接跳转到添加页面效果

解决办法:点击添加讲师时清空表单

```js
created() { //判断是否为修改
      if(this.$route.params&&this.$route.params.id){
          //获取路径中的id
          const id = this.$route.params.id
          this.getInfo(id)
      }
  },else{  //如果不是  则清空表单
          this.teacher={}
      }
```

但是没生效,<u>因为多次跳转同一页面时候created方法只执行一次.</u>

最终解决办法:使用vue监听:在路由发生变化时执行created中的代码逻辑.整理后的代码如下

在methods中添加方法:

```js
init(){
          //判断路径是否有id值
          if(this.$route.params&&this.$route.params.id){
          //获取路径中的id
          const id = this.$route.params.id
          this.getInfo(id)
      }else{  //如果不是  则清空表单
          this.teacher={}
        }
      },
```

```js
created() { //判断是否为修改
     this.init();
  },
  watch:{ //vue监听
    $route(to,from){ //路由变化方式  当路由发生变化时执行
        this.init()
   }
  },
```

# 头像上传oss

使用阿里云oss存储服务(存储->对象存储oss),开通,管理控制台.

创建bucket 低频访问存储类型  读写权限公共读.

文件管理 上传文件 通过url访问

java实现:

1.创建阿里云oss许可证:控制台  AccessKey  继续使用AccessKey  创建AccessKey

| AccessKeyId              | AccessKeySecret                |
| ------------------------ | ------------------------------ |
| xxx                      | xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx |

2.找到java sdk 文档 .

# 添加课程分类功能

使用EasyExcel读取excel添加到数据库

创建service子模块service_oss

```pom
<dependencies>
    <!-- 阿里云oss依赖 -->
    <dependency>
        <groupId>com.aliyun.oss</groupId>
        <artifactId>aliyun-sdk-oss</artifactId>
    </dependency>

    <!-- 日期工具栏依赖 -->
    <dependency>
        <groupId>joda-time</groupId>
        <artifactId>joda-time</artifactId>
    </dependency>
</dependencies>
```

```properties
#服务端口
server.port=8002
  #服务名
spring.application.name=service-oss

  #环境设置：dev、test、prod
spring.profiles.active=dev

  #阿里云 OSS
  #不同的服务器，地址不同  不能多空格
aliyun.oss.file.endpoint=oss-cn-guangzhou.aliyuncs.com
aliyun.oss.file.keyid=xxx
aliyun.oss.file.keysecret=xxx
  #bucket可以在控制台创建，也可以使用java代码创建
aliyun.oss.file.bucketname=lzf-guli2021

  # nacos服务地址
#spring.cloud.nacos.discovery.server-addr=127.0.0.1:8848
```

创建常量类读取数据内容

```java
@Component      //InitializingBean表示spring加载完成执行接口的方法
public class ConstantPropertiesUtils implements InitializingBean {
   //读取配置文件内容
   @Value("${aliyun.oss.file.endpoint}")
   private String endpoint;
   @Value("${aliyun.oss.file.keyid}")
   private String keyId;
   @Value("${aliyun.oss.file.keysecret}")
   private String keySecret;
   @Value("${aliyun.oss.file.bucketname}")
   private String bucketName;
   //定义公开静态常量
   public static String END_POINT;
   public static String ACCESS_KEY_ID;
   public static String ACCESS_KEY_SECRET;
   public static String BUCKET_NAME;
   @Override
   public void afterPropertiesSet() throws Exception {
      END_POINT = endpoint;
      ACCESS_KEY_ID = keyId;
      ACCESS_KEY_SECRET = keySecret;
      BUCKET_NAME = bucketName;
   }
}
```

controller service

```java
@RestController
@RequestMapping("/eduoss/fileoss")
@CrossOrigin
public class OssController {
   @Autowired
   private OssService ossService;
   //上传头像的方法
   @PostMapping
   public R uploadOssFile(MultipartFile file){
      //获取上传文件 MultipartFile
      //返回上传到oss的路径
      String url = ossService.uploadFileAvatar(file);

      return R.ok().data("url",url);
   }
}
```

```java
@Service
public class OssServiceImpl implements OssService {
   //上传头像到oss  使用的是上传文件流
   @Override
   public String uploadFileAvatar(MultipartFile file) {
      // yourEndpoint填写Bucket所在地域对应的Endpoint。以华东1（杭州）为例，Endpoint填写为https://oss-cn-hangzhou.aliyuncs.com。
      String endpoint = ConstantPropertiesUtils.END_POINT;
      // 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
      String accessKeyId = ConstantPropertiesUtils.ACCESS_KEY_ID;
      String accessKeySecret = ConstantPropertiesUtils.ACCESS_KEY_SECRET;
      String bucketName = ConstantPropertiesUtils.BUCKET_NAME;
      try {
         // 创建OSSClient实例。
         OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
         //获取上传文件输入流
         // 填写本地文件的完整路径。如果未指定本地路径，则默认从示例程序所属项目对应本地路径中上传文件流。
         InputStream inputStream = file.getInputStream();
         //获取文件名称
         String fileName = file.getOriginalFilename();
         // 1、在文件名称里面添加随机唯一的值
         String uuid = UUID.randomUUID().toString().replaceAll("-", "");
         fileName = uuid + fileName;
         //2、把文件按照日期进行分类
         String datapath = new DateTime().toString("yyyy/MM/dd");
         //拼接
         fileName = datapath + "/" + fileName;
         //调用oss方法实现上传
         // 填写Bucket名称和Object完整路径。Object完整路径中不能包含Bucket名称。
         //第一个参数 Bucket名称
         //第二个参数 上传到oss文件路径和文件名称
         //第三个参数 上传文件输入流
         ossClient.putObject(bucketName, fileName, inputStream);
         // 关闭OSSClient。
         ossClient.shutdown();
         //把上传后文件路径返回
         //需要把上传到阿里云oss路径手动拼接出来
         //"https://lzf-guli2021.oss-cn-guangzhou.aliyuncs.com/2021/06/04/34cd35bdf12e4ee7804bd7ff4578e47aBoy-01.png"
         String url = "https://"+bucketName+"."+endpoint+"/"+fileName;
         return url;
      } catch (IOException e) {
         e.printStackTrace();
         return null;
      }
   }
}
```

启动8002 ,使用swagger测试http://localhost:8002/swagger-ui.html

看返回的地址   和 oos文件管理



注意:

1.如果没有给文件名加uuid的上述操作,同名文件后面上传的会覆盖前面上传的

2.使用日期分类

## nginx

见nginx框架文档.这里使用windows版.

下载nginx windows压缩文件 解压 进入文件夹 cmd nginx.exe

windows nginx特点:多个进程(多路复用) | 关闭cmd窗口还在运行.

停止方法:nginx.exe -s stop

坑:如果跨域问题无法解决,尝试任务管理器结束nginx进程重新运行.!!!!!!

### 请求转发

配置conf/nginx.conf

1.将默认的80端口改为81(防止冲突,可不改)

2.配置nginx转发规则:

在http{}中添加

```conf
server {
        listen       9001;  
        server_name  localhost;

        location ~ /eduservice {
            proxy_pass http://localhost:8001;
        }
		location ~ /eduoss {
            proxy_pass http://localhost:8002;
        }
    }
```

~表示正则匹配后面的内容.改配置需要重启nginx

3.修改前端框架config/dev.env.js

```
BASE_API: '"http://localhost:9001"',
```

启动nginx 8001 前端9528 ,访问  需要重新登录.

在network中查看后端请求URL都是9001

## 前端页面

修改添加讲师页面,添加上传组件实现上传.使用element-ui.

1.在vue-element-admin-master中的src/components中复制ImageCropper和PanThumb到前端项目的同名文件夹下,使用其强大的美观效果.

2.在src/views/edu/teacher引入并声明:

```js
import ImageCropper from '@/components/ImageCropper'
import PanThumb from '@components/PanThumb'
export default {
  components: {
      ImageCropper,PanThumb
  },
  .....
 }
```

data中添加变量:

```js
 imagecropperShow:false, //上传弹窗组件是否显示
 imagecropperKey:0,      //上传组件唯一标识
 BASE_API: process.env.BASE_API, //获取配置文件接口api地址 固定写法
```

讲师头像TODO处补上.

```vue
<!-- 讲师头像 固定格式-->
        <el-form-item label="讲师头像">
            <!-- 头像缩略图 -->
            <pan-thumb :image="teacher.avatar"></pan-thumb>
            <!-- 文件上传按钮 -->
            <el-button type="primary" icon="el-icon-upload" @click="imagecropperShow=true">
                更换头像
            </el-button>
            <!-- v-show：是否显示上传组件 为true时显示， 由button控制
            :key：类似id，如果一个页面多个图片上传框架可以用作区分
            :url：后台上传的url地址
			width:最小像素宽
            @close：关闭上传组件
            @crop-upload-success:上传成功后的回调 -->
            <image-cropper v-show="imagecropperShow"
                :width="300"
                :height="300"
                :key="imagecropperKey"
                :url="BASE_API+'/eduoss/fileoss'"
                field="file"
                @close="close"
                @crop-upload-success="cropSuccess">
            </image-cropper>
        </el-form-item>
```

在methods中添加两个方法

```js
close(){ //关闭上传弹窗的方法
        this.imagecropperShow=false
    this.imagecropperKey=this.imagecropperKey+1
      },
      cropSuccess(data){//上传成功的方法 这里直接获取的response里的data属性
        this.imagecropperShow=false
        //上传之后接口返回图片地址
        this.teacher.avatar=data.url
          this.imagecropperKey=this.imagecropperKey+1
      },
```

测试 启动nginx9001 8001 8002 9528

注意:因为组件原因,图片获取后先同一命名file.xxx.

# EasyExcel

https://github.com/alibaba/easyexcel/

```
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>easyexcel</artifactId>
    <version>2.1.1</version>
</dependency>
```

本质是对poi进行封装 所以还需要引入poi

```
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi</artifactId>
</dependency>

<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
</dependency>
```

在test中对其进行测试.

创建实体类,与excel表格数据对应.

```java
@Data
public class DemoData {
	//value设置excel表头名称 用于写 ,  index表示列数索引 用于读
	@ExcelProperty(value="学生编号",index = 0)
	private  Integer sno;
	@ExcelProperty(value="学生姓名",index = 1)
	private  String sname;
}
```

## 写

测试代码

```java
//实现excel写操作
//1、设置写入文件地址和excel文件名称
String filename = "E:/Others Demo/guli_otherfile/excel/01.xlsx";

//2、调用easyexcel里面的方法实现写操作
//write方法两个参数:第一个参数文件路径名，第二个参数实体类class
EasyExcel.write(filename, DemoData.class).sheet("学生列表").doWrite(getData());
```

官方还有第二种更麻烦的写法,不推荐

## 读

创建监听器

```java
public class ExcelListener extends AnalysisEventListener<DemoData> {
    //读取表头内容
    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        System.out.println("表头： " + headMap);
    }
    //一行一行读取excel内容 从表头后开始
    @Override
    public void invoke(DemoData data, AnalysisContext analysisContext) {
        System.out.println("****"+data);
    }
    //读取完成之后
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
    }
}
```

测试代码

```java
//实现excel读操作
String filename = "E:/Others Demo/guli_otherfile/excel/01.xlsx";
EasyExcel.read(filename,DemoData.class,new ExcelListener()).sheet().doRead();
```

# 课程分类

在service_edu模块

edu_subject表  二级分类

parentId:表示上一级分类id 0为一级

title:分类名称

通过读取excel表格的分类

再次使用mybatisplus代码生成器生成代码,给生成的实体类添加@TableField属性自动填充.

entity/excel/

```java
@Data
public class SubjectData {
    @ExcelProperty(index = 0)
    private String oneSubjectName;
    @ExcelProperty(index = 1)
    private String twoSubjectName;
}
```

listener/

```java
public class SubjectExcelListener extends AnalysisEventListener<SubjectData> {
    //因为SubjectExcelListener没有交给spring进行管理，需要自己new，不能注入其他对象
    //不能实现数据库操作
    public EduSubjectService subjectService;

    public SubjectExcelListener() {
    }
    public SubjectExcelListener(EduSubjectService subjectService) {
        this.subjectService = subjectService;
    }
    //读取excel，内容，一行一行读取
    @Override
    public void invoke(SubjectData subjectData, AnalysisContext analysisContext) {
        if (subjectData == null){
            throw new GuliException(20001,"文件数据为空");
        }
        //一行一行读取，每次读取两个值，第一个值一级分类，第二个值二级分类
        //判断一级分类是否重复   如果重复则获取 接着判断二级分类
        EduSubject existOneSubject = this.existOneSubject(subjectService, subjectData.getOneSubjectName());
        if (existOneSubject == null) {// 没有相同一级分类
            existOneSubject = new EduSubject();
            existOneSubject.setParentId("0");
            existOneSubject.setTitle(subjectData.getOneSubjectName());//一级分类名称
            subjectService.save(existOneSubject);
        }
        //获取一级分类id值
        String pid = existOneSubject.getId();
        //判断二级分类是否重复
        EduSubject existTwoSubject = this.existOTwoSubject(subjectService, subjectData.getTwoSubjectName(), pid);
        if (existTwoSubject == null) {// 没有相同二级分类
            existTwoSubject = new EduSubject();
            existTwoSubject.setParentId(pid);
            existTwoSubject.setTitle(subjectData.getTwoSubjectName());//二级分类名称
            subjectService.save(existTwoSubject);
        }
    }
    //判断一级分类不能重复添加  
    private EduSubject existOneSubject(EduSubjectService subjectService, String name){
        QueryWrapper<EduSubject> wrapper = new QueryWrapper<>();
        wrapper.eq("title", name);
        wrapper.eq("parent_id", "0");
        EduSubject oneSubject = subjectService.getOne(wrapper);
        return oneSubject;
    }

    //判断二级分类不能重复添加
    private EduSubject existOTwoSubject(EduSubjectService subjectService, String name,String pid){
        QueryWrapper<EduSubject> wrapper = new QueryWrapper<>();
        wrapper.eq("title", name);
        wrapper.eq("parent_id", pid);
        EduSubject twoSubject = subjectService.getOne(wrapper);
        return twoSubject;
    }
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
    }
}
```

添加课程分类相关路由router/index.js

```js
 {
    path: '/subject',
    component: Layout,
    redirect: '/subject/list',
    name: '课程分类管理',
    meta: { title: '课程分类管理', icon: 'example' }, 
    children: [
      {
        path: 'list',
        name: '课程分类列表',
        component: () => import('@/views/edu/subject/list'),
        meta: { title: '课程分类列表', icon: 'table' }
      },
      {
        path: 'save',
        name: '添加课程分类',
        component: () => import('@/views/edu/subject/save'),
        meta: { title: '添加课程分类', icon: 'tree' }
      }
    ]
  },
```

## 添加课程分类

controller方法

```java
@PostMapping("addSubject")
public R addSubject(MultipartFile file){
   //上传过来excel文件
   subjectService.saveSubject(file,subjectService);
   return R.ok();
}
```

service方法

```java
@Override
public void saveSubject(MultipartFile file, EduSubjectService subjectService) {
   try {
      //文件输入流
      InputStream in = file.getInputStream();
      //调用方法进行读取
      EasyExcel.read(in, SubjectData.class, new SubjectExcelListener(subjectService)).sheet().doRead();
   } catch (Exception e) {
      e.printStackTrace();
   }
}
```

添加路由页面view/edu/subject/save.vue

element-ui  upload上传 手动上传 组件.

```vue
<template>
  <!--template固定-->
  <div class="app-container">
    <el-form label-width="120px">
      <el-form-item label="信息描述">
          <el-tag type="info">excel模板说明</el-tag>
          <el-tag>
              <i class="el-icon-download"></i>
              <a :href="'static/guli_subject.xlsx'">点击下载模板</a>
          </el-tag>
      </el-form-item>
      <el-form-item label="选择Excel">
          <!-- ref 唯一标识   用于提交
          :auto-upload 是否自动上传
          limit 每次上传文件个数限制
          accept="application/vnd.ms-excel" 表示只接受excel文件 -->
          <el-upload 
            ref="upload"
            :auto-upload="false"
            :on-success="fileUploadSuccess"
            :on-error="fileUploadError"
            :disabled="importBtnDisabled"
            :limit="1"
            :action="BASE_API+'/eduservice/subject/addSubject'"
            name="file"
            accept="application/vnd.ms-excel">
            <el-button slot="trigger" size="small" type="primary">选取文件</el-button>
            <el-button
                :loading="loading"
                style="margin-left: 10px;"
                size="small"
                type="success"
                @click="submitUpload">上传到服务器</el-button>
          </el-upload>
      </el-form-item>
    </el-form>
  </div>
</template>
<script>

export default {
  data() {
    return {
        BASE_API: process.env.BASE_API,  //接口API地址
        importBtnDisabled: false,       //按钮禁用
        loading: false
    };
  },
  created() { 

  },
  methods: {
      submitUpload(){  //点击按钮上传文件到接口里
        this.importBtnDisabled = true
        this.loading = true
        // document.getElementById("upload").submit()   js原生提交代码
        this.$refs.upload.submit()  //vue 提交方式 upload就是上传组件的ref标识
      },
      fileUploadSuccess(){  //还可以通过参数接受响应信息输出  这里简略
        //提示信息
        this.loading = false
        this.$message({
          type:'success',
          message:'添加课程分类成功'
        })
        //跳转课程分类列表  路由跳转
        this.$router.push({path:'/subject/list'})
      },
      fileUploadErro(){
        this.loading =false
         this.$message({
          type:'error',
          message:'添加课程分类失败'
        })
      }
      
  },
};
</script>
```

将前面生成课程分类的excel表格放入前端项目static文件夹下,用于用户下载模板.

## 二级分类列表(算法)

创建一级分类与二级分类实体类

```java
@Data
public class OneSubject {
    private String id;
    private String title;
    //一个一级分类有多个二级分类
    private List<TwoSubject> children = new ArrayList<>();
}
@Data
public class TwoSubject {
    private String id;
    private String title;
}
```

controller方法

```java
@GetMapping("getAllSubject")
public R getAllSubject(){
   //list集合泛型是一级分类
   List<OneSubject> list = subjectService.getAllOneTwoSubject();
   return R.ok().data("list",list);
}
```

service方法

```java
@Override
   public List<OneSubject> getAllOneTwoSubject() {
      //1、查询所有一级分类 parent_id=0
      QueryWrapper<EduSubject> wrapperOne = new QueryWrapper<>();
      wrapperOne.eq("parent_id", "0");
      //this.list(wrapperOne);
      //使用继承父类中的baseMapper
      List<EduSubject> OneSubjectList = baseMapper.selectList(wrapperOne);

      //2、查询所有二级分类 parent_id!=0
      QueryWrapper<EduSubject> wrapperTwo = new QueryWrapper<>();
      wrapperTwo.ne("parent_id", "0");
      List<EduSubject> TwoSubjectList = baseMapper.selectList(wrapperTwo);

      //创建list集合，用于存储最终封装数据
      List<OneSubject> finalSubjectList = new ArrayList<>();
      //3、封装一级分类
      //查询出来所有的一级分类list集合遍历，得到每一个一级分类对象，获得每一个一级分类对象值
      //封装到要求的list集合里面List<OneSubject> finalSubjectList
      for (int i = 0; i < OneSubjectList.size(); i++) {//遍历OneSubjectList集合
         //得到OneSubjectList每个eduSubject对象
         EduSubject eduSubject = OneSubjectList.get(i);

         //把eduSubject里面值获取出来，放到OneSubject对象里面
         //多个OneSubject放到finalSubjectList里面
         OneSubject oneSubject = new OneSubject();
//            oneSubject.setId(eduSubject.getId());
//            oneSubject.setTitle(eduSubject.getTitle());
         //简化，把eduSubject复制到oneSubject
         BeanUtils.copyProperties(eduSubject, oneSubject);

         finalSubjectList.add(oneSubject);

         //在一级分类循环遍历查询所有的二级分类
         //创建list集合封装每一个一级分类的二级分类
         List<TwoSubject> twoFinalSubjectList = new ArrayList<>();
         //遍历二级分类list集合
         for (int m = 0; m < TwoSubjectList.size(); m++) {
            //获取每个二级分类
            EduSubject tSubject = TwoSubjectList.get(m);
            //判断二级分类parentid和一级分类id是否一致
            if (tSubject.getParentId().equals(eduSubject.getId())) {
               //把tSubject值复制到TwoSubject里面，放到twoFinalList里面
               TwoSubject twoSubject = new TwoSubject();
               BeanUtils.copyProperties(tSubject, twoSubject);
               twoFinalSubjectList.add(twoSubject);
            }
         }
         //把一级下面所有二级分类放到一级分类里面
         oneSubject.setChildren(twoFinalSubjectList);
      }
      return finalSubjectList;
   }
```

在api/edu下新建subject.js访问后端接口

```js
import request from '@/utils/request'  //固定  request里面封装了axios的方法

export default{
    //课程分类列表
    getSubjectList(){
        return request({
            url='/eduservice/subject/getAllSubject',
            method:'get'
        })
    }
}
```

添加路由页面view/edu/subject/list.vue

```vue
<template>
  <div class="app-container">
    <!-- 检索输入框 -->
    <el-input v-model="filterText" placeholder="Filter keyword" style="margin-bottom:30px;" />
    <!-- 树列表  -->
    <el-tree
      ref="tree2"
      :data="data2"
      :props="defaultProps"
      :filter-node-method="filterNode"
      class="filter-tree"
      default-expand-all
    />
  </div>
</template>
<script>
import subject from '@/api/edu/subject'
export default {
  data() {
    return {
      filterText:'',
      data2:[],
      defaultProps:{
        children:'children',
        label:'title'
      }
    }
  },
  created(){
    this.getAllSubjectList()
  },
  watch: {
    filterText(val) {
      this.$refs.tree2.filter(val)
    }
  },
  methods: {
    getAllSubjectList(){
      subject.getSubjectList()
        .then(response => {
          this.data2=response.data.list
        })
    },
    filterNode(value,data){ //某个节点是否匹配  不区分大小写
      if(!value) return true
      return data.title.toLowerCase().indexOf(value.toLowerCase())!==-1
    }
  }
}
</script>
```

# Tinymce可视化编辑器

可编辑字体格式,插入图片等....

因为其没有采用服务器存储图片,所以它采用对图片进行base64编码

将tinymce4.7.5文件夹复制到static下,把Tinymce文件夹复制到src/components下.

配置html变量:

在build/webpack.dev.config.js中添加配置

```js
new HtmlWebpackPlugin({
     ...,
      templateParameters:{
        BASE_URL: config.dev.assetsPublicPath + config.dev.assetsSubDirectory
      }
    })
```

引入js脚本:

在根目录的index.html 加入,有错 不影响

```html
<script src=<%= BASE_URL %>/tinymce4.7.5/tinymce.min.js></script>
<script src=<%= BASE_URL %>/tinymce4.7.5/langs/zh_CN.js></script>  //中文包 可省
```

引入组件

```js
import Tinymce from '@/components/Tinymce'
export default{
	components:{Tinymce},
	......
}
```

组件使用模板

```html
<el-form-item label="课程简介">
	<tinymce :height="300" v-model="courseInfo.description"/>
</el-form-item>
```

组件样式 可不加

```css
<style scoped>
.tinymce-container{
    line-height: 29px;
}
</style>
```

# 阿里云视频点播(收费)

视频采集、编辑、上传、媒体资源管理、自动化转码处理、视频审核分析、分发加速于一体的一站式音视频点播解决方案。

开通服务后进入点播控制台.

媒资库->音/视频:查看上传到阿里云视频点播视频的文件|上传视频文件(需要先设置存储地址)

配置管理->媒资管理配置->存储管理:上传的点播视频本质还是存在oos中.

配置管理->媒资处理配置->转码模板组:转码(可加水印)|加密等.设置转码组后在添加音/视频时候选择转码组.

播放加密视频需要在 分发加速配置->域名管理中添加域名使用.

快速使用: (前提  阿里云AccessKey)

服务端:后端接口

客户端:浏览器 安装 ios.

API:阿里云提供固定的地址,只需要调用这个固定的地址,向地址传递参数,实现功能.较底层,比较麻烦,官方不推荐.   (httpclient可以不需要浏览器来调用api地址)

SDK:对api方式进行封装,更方便.调用阿里云提供类或者接口里面的方法实现视频功能.



视频加密之后地址不能进行视频播放,所以在数据库存的时视频id而不是地址.

注意: 此包需要手动加入maven本地仓库

```java
<dependency>
    <groupId>com.aliyun</groupId>
    <artifactId>aliyun-sdk-vod-upload</artifactId>
</dependency>
```

```
mvn install:install-file  -DgroupId=com.aliyun  -DartifactId=aliyun-sdk-vod-upload -Dversion=1.4.11 -Dpackaging=jar  -Dfile=aliyun-java-vod-upload-1.4.11.jar
```

## 初始化

```java
public class InitObject {
   public static DefaultAcsClient initVodClient(String accessKeyId, String accessKeySecret) throws ClientException {
      String regionId = "cn-shanghai";  // 点播服务接入区域
      DefaultProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, accessKeySecret);
      DefaultAcsClient client = new DefaultAcsClient(profile);
      return client;
   }
}
```

## 获取视频播放地址

```java
@Test
public void test1() throws ClientException {
   //创建初始化对象
   DefaultAcsClient client = InitObject.initVodClient("xxx", "xxx");
   //创建获取视频地址request和response
   GetPlayInfoRequest request = new GetPlayInfoRequest();
   GetPlayInfoResponse response = new GetPlayInfoResponse();

   // 向request对象里面设置视频id
   request.setVideoId("089d1a38480b4f98bf03631f81274a3b");
   // 调用初始化对象里面的方法，传递request,获取数据
   response = client.getAcsResponse(request);

   //输出请求结果
   List<GetPlayInfoResponse.PlayInfo> playInfoList = response.getPlayInfoList();
   //播放地址
   for (GetPlayInfoResponse.PlayInfo playInfo : playInfoList) {
      System.out.print("PlayInfo.PlayURL = " + playInfo.getPlayURL() + "\n");
   }
   //Base信息
   System.out.print("VideoBase.Title = " + response.getVideoBase().getTitle() + "\n");
}
```

## 获取视频播放凭证

```java
@Test
public void test2() throws ClientException {
   //创建初始化对象
   DefaultAcsClient client = InitObject.initVodClient("xxx", "xxx");
   //创建获取视频地址request和response
   GetVideoPlayAuthRequest request = new GetVideoPlayAuthRequest();
   GetVideoPlayAuthResponse response = new GetVideoPlayAuthResponse();

   // 向request对象里面设置视频id
   request.setVideoId("089d1a38480b4f98bf03631f81274a3b");
   //获取请求响应
   response = client.getAcsResponse(request);

   //输出请求结果
   //播放凭证
   System.out.print("PlayAuth = " + response.getPlayAuth() + "\n");
   //VideoMeta信息
   System.out.print("VideoMeta.Title = " + response.getVideoMeta().getTitle() + "\n");
}
```

## 上传视频到阿里云点播

```java
public static void main(String[] args) {
   //1.音视频上传-本地文件上传
   //视频标题(必选)
   String title = "6 - What If I Want to Move Faster - upload by sdk";
   //本地文件上传和文件流上传时，文件名称为上传文件绝对路径，如:/User/sample/文件名称.mp4 (必选)
   //文件名必须包含扩展名
   String fileName = "E:/Others Demo/guli_otherfile/video/6 - What If I Want to Move Faster.mp4";
   //本地文件上传
   UploadVideoRequest request = new UploadVideoRequest("L
	", "oERmIrTdjQ7xYDlN9vCrJ9B9X6JCTV", title, fileName);
   /* 可指定分片上传时每个分片的大小，默认为1M字节 */
   request.setPartSize(1 * 1024 * 1024L);
   /* 可指定分片上传时的并发线程数，默认为1，(注：该配置会占用服务器CPU资源，需根据服务器情况指定）*/
   request.setTaskNum(1);
   /* 是否开启断点续传, 默认断点续传功能关闭。当网络不稳定或者程序崩溃时，再次发起相同上传请求，可以继续未完成的上传任务，适用于超时3000秒仍不能上传完成的大文件。
       注意: 断点续传开启后，会在上传过程中将上传位置写入本地磁盘文件，影响文件上传速度，请您根据实际情况选择是否开启*/
   request.setEnableCheckpoint(false);
//上传
   UploadVideoImpl uploader = new UploadVideoImpl();
   UploadVideoResponse response = uploader.uploadVideo(request);
   System.out.print("RequestId=" + response.getRequestId() + "\n");  //请求视频点播服务的请求ID
   if (response.isSuccess()) {
      System.out.print("VideoId=" + response.getVideoId() + "\n");
   } else {
      /* 如果设置回调URL无效，不影响视频上传，可以返回VideoId同时会返回错误码。其他情况上传失败时，VideoId为空，此时需要根据返回错误码分析具体错误原因 */
      System.out.print("VideoId=" + response.getVideoId() + "\n");
      System.out.print("ErrorCode=" + response.getCode() + "\n");
      System.out.print("ErrorMessage=" + response.getMessage() + "\n");
   }
}
```

## 视频播放

示例代码网站

https://player.alicdn.com/aliplayer/presentation/index.html?type=pictureAD

引入脚本文件和css文件

```html
 <!-- 阿里云视频播放器样式 -->
    <link rel="stylesheet" href="https://g.alicdn.com/de/prismplayer/2.8.1/skins/default/aliplayer-min.css" >
    <!-- 阿里云视频播放器脚本 -->
    <script charset="utf-8" type="text/javascript" src="https://g.alicdn.com/de/prismplayer/2.8.1/aliplayer-min.js" />
```

```html
初始化视频播放器
<body>
    <!-- 定义播放器dom -->
    <div id="J_prismPlayer" class="prism-player" />
    <script>
        new Aliplayer({
            id: 'J_prismPlayer',
            vid: this.vid, // 视频id
            autoplay:false //自动播放
            playauth: this.playAuth, // 播放凭证
            encryptType: '1', // 如果播放加密视频，则需设置encryptType=1，非加密视频无需设置此项
            width: '100%',
            height: '500px',
            cover: 'http://guli.shop/photo/banner/1525939573202.jpg', // 封面
            qualitySort: 'asc', // 清晰度排序

            mediaType: 'video', // 返回音频还是视频
            autoplay: false, // 自动播放
            isLive: false, // 直播
            rePlay: false, // 循环播放
            preload: true,
            controlBarVisibility: 'hover', // 控制条的显示方式：鼠标悬停
            useH5Prism: true, // 播放器类型：html5
        }, function(player) {
            console.log('播放器创建成功')
        })
    </script>
</body>
```

有两种方式

### 播放地址播放

不能播放加密视频.

在Aliplayer的配置参数中添加属性

source: '视频播放地址'

### 播放凭证播放(推荐)

使用播放凭证换取播放地址进行播放,接入方式更简单安全性更高,凭证默认时效为100秒,最大3000秒.

添加属性

encryptType: '1', // 如果播放加密视频，则需设置encryptType=1，非加密视频无需设置此项

vid: this.vid, // 视频id

playauth: this.playAuth, // 播放凭证

# 课程管理

多表

edu_course:课程基本信息

edu_course_description:课程简介

edu_chapter:课程章节信息

edu_video:课程小杰信息

edu_subject:分类表

edu_teacher:讲师表

模块 service_edu

使用代码生成器生成前面四张表的代码,删掉简介表的controller.

课程添加:采用组件 步骤条 表单 弹出对话框进行添加或修改 Tinymce可视化文本编辑器(传统js插件,

代码见gitee

# 解决方案

## 读取java包下的非java文件

1.将非java文件手动复制到target下,不推荐

2.通过pom文件

```xml
    <build>
        <resources>
            <resource>
                <directory>src/main/java</directory>
                <includes>
                    <!-- 这里指定加载多层目录下的xml文件-->
                    <include>**/*.xml</include>
                </includes>
                <filtering>false</filtering>
            </resource>
        </resources>
    </build>
```

3.通过properties或yml指定配置文件,以mybatisplus为例:

```properties
mybatis-plus.mapper-locations=classpath:edu/hunnu/eduservice/mapper/xml/*.xml
```

## 文件上传大小限制

1.tomcat限制

```properties
# 最大上传单个文件大小：默认1M
spring.servlet.multipart.max-file-size=1024MB
# 最大置总上传的数据大小 ：默认10M
spring.servlet.multipart.max-request-size=1024MB
```

2.nginx限制 413(Request Entity Too Large)

在nginx的nginx.conf中的http{}中加入

```json
client_max_body_size 1024m;
```

## 注意前后端各种请求url路径对于

## 注意各页面的数据回显

## 注意前端页面的异步加载问题

## 注意获取数据库null值的异常处理



# 前台

前台系统环境  NUXT

首页显示banner,热门课程和名师,首页数据用redis存储,

ajax前端渲染,不利于SEO(搜索引擎检索匹配度);

NUXT在服务端渲染后一次返回

# 服务端渲染技术NUXT

服务端渲染又称SSR,是在服务端完成页面的内容.而不是客户端通过AJAX获取数据.

解压starter-template-master.zip,将template文件夹下的内容拷贝到vscode工作区

修改package.json的name description author的属性后,npm install下载npm包.

npm run dev 启动   3000端口  .启动后有error 无影响.

先加载布局再引入(nuxt)页面(iframe),

## 文件结构

.nuxt:编译后生成

assets:静态资源 css js img等

component:组件

layouts/default.vue:定义网页布局

pages/index.vue:项目页面

nuxt.config.js:核心配置文件

## 路由

1.固定路由,不发生变化

在layout/default.vue,如

```vue
 <router-link to="/course" tag="li" active-class="current">
              <a>课程</a>
    </router-link>
```

并在pages里创建course/index.vue

2.动态路由

每次生成的路由地址不一样,比如课程详情页面.

创建方式:NUXT的动态路由是以下划线开头的vue文件.如果以课程id查询课程,在pages下的course/_id.vue

注意,在页面中如果要获取动态路由里的参数值,参数值的名称要与_id.vue的id对于,名称可随意取,

如this.$route.params.id

# 幻灯片swiper插件

## 附指定npm包安装版本

安装npm install vue-awesome-swiper@3.1.3

如果已经安装了高版本 在package.json中设置为3.1.3重新npm install(记得保存文件)

配置插件:

在plugins文件夹下新建文件 nuxt-swiper-plugin.js.

```js
import Vue from 'vue'
import VueAwesomeSwiper from 'vue-awesome-swiper/dist/ssr'
Vue.use(VueAwesomeSwiper)
```

在nuxt.config.js文件中配置插件,将plugins和css节点赋值到module.exports节点下加入:

```js
plugins:[
    {
      src: '~/plugins/nuxt-swiper-plugin.js',
      ssr: false
    }
  ],
  css:[
    'swiper/dist/css/swiper.css'
  ],
```



# Spring Cashe+Redis

基于kv存储 ,支持持久化和事务.

一般将要经常访问不经常修改的不重要数据存放到redis.

需要启动redis,注意,先启动server(如果需要使用修改配置文件需要手动指定参数),再启动cli.

## 使用

在公共模块里引入依赖

```xml
<!-- redis -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>

<!--spring2.X集成redis所需common-pool2-->
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-pool2</artifactId>
    <version>2.6.0</version>
</dependency>
```

创建配置类

```java
@EnableCaching // 开启缓存
@Configuration // 配置类
public class RedisConfig extends CachingConfigurerSupport {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        RedisSerializer<String> redisSerializer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        template.setConnectionFactory(factory);
        //key序列化方式
        template.setKeySerializer(redisSerializer);
        //value序列化
        template.setValueSerializer(jackson2JsonRedisSerializer);
        //value hashmap序列化
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        return template;
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        RedisSerializer<String> redisSerializer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        //解决查询缓存转换异常的问题
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        // 配置序列化（解决乱码的问题）,过期时间600秒
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(600))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer))
                .disableCachingNullValues();
        RedisCacheManager cacheManager = RedisCacheManager.builder(factory)
                .cacheDefaults(config)
                .build();
        return cacheManager;
    }
}
```

服务方配置文件

```properties
spring.redis.host=127.0.0.1
spring.redis.port=6379
spring.redis.database= 0
spring.redis.timeout=1800000
#均为默认值
spring.redis.lettuce.pool.max-active=20
spring.redis.lettuce.pool.max-wait=-1
#最大阻塞等待时间(负数表示没限制)
spring.redis.lettuce.pool.max-idle=5   
spring.redis.lettuce.pool.min-idle=0
```

在接口方法(service controller)上添加redis缓存注解,常用如下

1.@Casheable

根据方法对其返回结果进行缓存,下次请求时,如果缓存还在,直接读取缓存数据返回;如果缓存不存在,则执行方法,并把返回的结果存入缓存中,一般用在<u>查询</u>方法上.

value:缓存名,必填,.

cacheNames:与value类似;

key:可以使用SpEL标签自定义缓存的key.

2.@CashePut

方法每次都会执行,并将结果存入指定的缓存中,其他方法可以直接从响应的缓存中读取缓存数据而不需要再去查询数据库,一般用在<u>新增</u>方法上.

value:缓存名,必填,.

cacheNames:与value类似;

key:可以使用SpEL标签自定义缓存的key.

3.@Casheable清空指定缓存,一般用在更新或者删除的方法上.

value:缓存名,必填,指定了缓存存放在那块命名空间 ,.

cacheNames:与value类似;

key:可以使用SpEL标签自定义缓存的key.

allEntries:是否清空所有缓存,默认为false,如果指定为true,则方法调用后立即清空所有缓存.

beforeInvocation:是否在方法执行前就清空,默认为false.如果指定为true,则在方法执行前就会清空缓存.

## redis连接问题

1.关闭linux防火墙

2.redis配置文件注释掉 # bind 127.0.0.1

3.如果出现redis保活错误,则修改redis配置文件 protected-mode yes



# 前端

在assets中加入相关静态资源.

修改layouts/default.vue  表示固定头部尾部

```js
<template>
  <div class="in-wrap">
    <!-- 公共头引入 -->
    <header id="header">
      <section class="container">
        <h1 id="logo">
          <a href="#" title="谷粒学院">
            <img src="~/assets/img/logo.png" width="100%" alt="谷粒学院">
          </a>
        </h1>
        <div class="h-r-nsl">
          <ul class="nav">
            <router-link to="/" tag="li" active-class="current" exact>
              <a>首页</a>
            </router-link>
            <router-link to="/course" tag="li" active-class="current">
              <a>课程</a>
            </router-link>
            <router-link to="/teacher" tag="li" active-class="current">
              <a>名师</a>
            </router-link>
            <router-link to="/article" tag="li" active-class="current">
              <a>文章</a>
            </router-link>
            <router-link to="/qa" tag="li" active-class="current">
              <a>问答</a>
            </router-link>
          </ul>
          <!-- / nav -->
          <ul class="h-r-login">
            <li id="no-login">
              <a href="javascript:lrFun(1)" title="登录">
                <em class="icon18 login-icon">&nbsp;</em>
                <span class="vam ml5">登录</span>
              </a>
              |
              <a href="javascript:lrFun(2)" title="注册">
                <span class="vam ml5">注册</span>
              </a>
            </li>
            <li class="mr10 undis" id="is-login-one">
              <a href="#" title="消息" id="headerMsgCountId">
                <em class="icon18 news-icon">&nbsp;</em>
              </a>
              <q class="red-point" style="display: none">&nbsp;</q>
            </li>
            <li class="h-r-user undis" id="is-login-two">
              <a href="#" title>
                <img
                  src="~/assets/img/avatar-boy.gif"
                  width="30"
                  height="30"
                  class="vam picImg"
                  alt
                >
                <span class="vam disIb" id="userName"></span>
              </a>
              <a href="javascript:void(0)" title="退出" onclick="exit();" class="ml5">退出</a>
            </li>
            <!-- /未登录显示第1 li；登录后显示第2，3 li -->
          </ul>
          <aside class="h-r-search">
            <form action="#" method="post">
              <label class="h-r-s-box">
                <input type="text" placeholder="输入你想学的课程" name="queryCourse.courseName" value>
                <button type="submit" class="s-btn">
                  <em class="icon18">&nbsp;</em>
                </button>
              </label>
            </form>
          </aside>
        </div>
        <aside class="mw-nav-btn">
          <div class="mw-nav-icon"></div>
        </aside>
        <div class="clear"></div>
      </section>
    </header>
      
    <!-- /公共头引入 -->
    <nuxt/>

    <!-- 公共底引入 -->
    <footer id="footer">
      <section class="container">
        <div class>
          <h4 class="hLh30">
            <span class="fsize18 f-fM c-999">友情链接</span>
          </h4>
          <ul class="of flink-list">
            <li>
              <a href="http://www.atguigu.com/" title="尚硅谷" target="_blank">尚硅谷</a>
            </li>
          </ul>
          <div class="clear"></div>
        </div>
        <div class="b-foot">
          <section class="fl col-7">
            <section class="mr20">
              <section class="b-f-link">
                <a href="#" title="关于我们" target="_blank">关于我们</a>|
                <a href="#" title="联系我们" target="_blank">联系我们</a>|
                <a href="#" title="帮助中心" target="_blank">帮助中心</a>|
                <a href="#" title="资源下载" target="_blank">资源下载</a>|
                <span>服务热线：010-56253825(北京) 0755-85293825(深圳)</span>
                <span>Email：info@atguigu.com</span>
              </section>
              <section class="b-f-link mt10">
                <span>©2018课程版权均归谷粒学院所有 京ICP备17055252号</span>
              </section>
            </section>
          </section>
          <aside class="fl col-3 tac mt15">
            <section class="gf-tx">
              <span>
                <img src="~/assets/img/wx-icon.png" alt>
              </span>
            </section>
            <section class="gf-tx">
              <span>
                <img src="~/assets/img/wb-icon.png" alt>
              </span>
            </section>
          </aside>
          <div class="clear"></div>
        </div>
      </section>
    </footer>
    <!-- /公共底引入 -->
  </div>
</template>
<script>
import "~/assets/css/reset.css";
import "~/assets/css/theme.css";
import "~/assets/css/global.css";
import "~/assets/css/web.css";

export default {};
</script>
```

## 请求封装

新建utils/request.js

1.axios

npm install axios;

模仿后台框架封装axios请求,

```js
import axios from 'axios'
//import {MessageBox,Message} from "element-ui";
//import cookie from 'js-cookie'
// 创建axios实例
const service = axios.create({
	baseURL: 'http://localhost:9001', // api的base_url  nginx
	timeout: 20000 // 请求超时时间
})

//service.interceptors.request.use(
//	config => {
//		if (cookie.get('guli_token')) {
//			config.headers['token'] = cookie.get('guli_token');
//		}
//		return config
//	},
//	error => {
//		return Promise.reject(error)
//	}
//)
//service.interceptors.response.use(
//	response => {
//		if (response.data.code !== 200) {
//			
//			if(response.data.code!==250){
//				Message({
//					message:response.data.message||'error',
//					type:'error',
//					duration:5*1000
//				})
//			}
//		}
//		return response
//	}
)
export default service
```

## 首页数据banner与热门显示

新建api/banner.js和api/index.js

```js
import request from '@/utils/request'

export default {
	//查询前两条banner数据
	getListBanner() {
		return request({
			url: '/educms/bannerfront/getAllBanner',
			method: 'get'
		})
	}
}
```

```js
import request from '@/utils/request'

export default {
    //查询热门课程和名师
  getIndexData() {
    return request({
      url: '/eduservice/indexfront/index',
      method: 'get'
    })
  }
}
```

修改pages/index.vue 里面有加入幻灯片代码

```vue
<template>
  
  <div>
    <!-- 幻灯片 开始 -->
    <div v-swiper:mySwiper="swiperOption">
        <div class="swiper-wrapper">
            <div v-for="banner in bannerList" :key="banner.id" class="swiper-slide" style="background: #040B1B;">
                <a target="_blank" :href="banner.linkUrl">
                    <img :src="banner.imageUrl" :alt="banner.title">
                </a>
            </div>
            <div class="swiper-slide" style="background: #F3260B;">
                <a target="_blank" href="/">
                    <img src="~/assets/photo/banner/153525d0ef15459596.jpg" alt="首页banner">
                </a>
            </div>
        </div>
        <div class="swiper-pagination swiper-pagination-white"></div>
        <div class="swiper-button-prev swiper-button-white" slot="button-prev"></div>
        <div class="swiper-button-next swiper-button-white" slot="button-next"></div>
    </div>
    <!-- 幻灯片 结束 -->
    
     <div id="aCoursesList">
      <!-- 网校课程 开始 -->
      <div>
        <section class="container">
          <header class="comm-title">
            <h2 class="tac">
              <span class="c-333">热门课程</span>
            </h2>
          </header>
          <div>
            <article class="comm-course-list">
              <ul class="of" id="bna">
                <li v-for="course in courseList" :key="course.id">       <!--每个li为一个课程 -->
                  <div class="cc-l-wrap">
                    <section class="course-img">
                      <img
                        :src="course.cover"
                        class="img-responsive"
                        :alt="course.title"
                      >
                      <div class="cc-mask">
                        <a href="#" title="开始学习" class="comm-btn c-btn-1">开始学习</a>
                      </div>
                    </section>
                    <h3 class="hLh30 txtOf mt10">
                      <a href="#" :title="course.title" class="course-title fsize18 c-333">{{course.title}}</a>
                    </h3>
                    <section class="mt10 hLh20 of">
                      <!-- 课程费用为0则免费  这里使用数值判断-->
                      <span class="fr jgTag bg-green" v-if="Number(course.price)===0">
                        <i class="c-fff fsize12 f-fA">免费</i>
                      </span>
                      <!-- 否则显示价格 -->
                      <span class="fr jgTag bg-green" v-else>
                        <i class="c-fff fsize12 f-fA">${{course.price}}</i>
                      </span>
                      <span class="fl jgAttr c-ccc f-fA">
                        <i class="c-999 f-fA">{{course.buyCount}}人购买</i>
                        |
                        <i class="c-999 f-fA">{{course.viewCount}}人浏览</i>
                        |
                        <i class="c-999 f-fA">12312人评论</i>
                      </span>
                    </section>
                  </div>
                </li>
              </ul>
              <div class="clear"></div>
            </article>
            <section class="tac pt20">
              <a href="#" title="全部课程" class="comm-btn c-btn-2">全部课程</a>
            </section>
          </div>
        </section>
      </div>
      <!-- /网校课程 结束 -->
      <!-- 网校名师 开始 -->
      <div>
        <section class="container">
          <header class="comm-title">
            <h2 class="tac">
              <span class="c-333">名师大咖</span>
            </h2>
          </header>
          <div>
            <article class="i-teacher-list">
              <ul class="of">
                <li v-for="teacher in teacherList" :key="teacher.id">
                  <section class="i-teach-wrap">
                    <div class="i-teach-pic">
                      <a href="/teacher/1" :title="teacher.name">
                        <img :alt="teacher.name" :src="teacher.avatar">
                      </a>
                    </div>
                    <div class="mt10 hLh30 txtOf tac">
                      <a href="/teacher/1" :title="teacher.name" class="fsize18 c-666">{{teacher.name}}</a>
                    </div>
                    <div class="hLh30 txtOf tac">
                      <span class="fsize14 c-999">{{teacher.career}}</span>
                    </div>
                    <div class="mt15 i-q-txt">
                      <p
                        class="c-999 f-fA"
                      >{{teacher.intro}}</p>
                    </div>
                  </section>
                </li>
              </ul>
              <div class="clear"></div>
            </article>
            <section class="tac pt20">
              <a href="#" title="全部讲师" class="comm-btn c-btn-2">全部讲师</a>
            </section>
          </div>
        </section>
      </div>
      <!-- /网校名师 结束 -->
    </div>
  </div>
</template>

<script>
import banner from '@/api/banner'
import index from '@/api/index'

export default {
  data () {
    return {
      swiperOption: {
        //配置分页
        pagination: {
          el: '.swiper-pagination'//分页的dom节点
        },
        //配置导航
        navigation: {
          nextEl: '.swiper-button-next',//下一页dom节点
          prevEl: '.swiper-button-prev'//前一页dom节点
        }
      },
      bannerList:[],  //banner数组
      courseList:[],  //热门课程数组
      teacherList:[]  //热门讲师数组
    }
  },
  created(){
    this.getBannerList()    //查询banner
    this.getHotCourseTeacher();   //查询热门课程和讲师
  },
  methods:{
    getHotCourseTeacher(){  //查询热门课程 讲师
      index.getIndexData()
        .then(response => {
          this.courseList=response.data.data.courseList
          this.teacherList=response.data.data.teacherList
        })
    },
    getBannerList() {  //查询banner数据
      banner.getListBanner()
        .then(response =>{
          this.bannerList=response.data.data.list
        })
    }
  }
}
</script>
```

# 阿里云短信服务

开通云通信 短信服务

控制台  国内消息  需要先申请签名和模板(验证码).

目前没有上线项目域名已经无法注册了.

## 使用

msm模块

```xml
<dependencies>
    <dependency>
        <groupId>com.alibaba</groupId>
        <artifactId>fastjson</artifactId>
    </dependency>
    <dependency>
        <groupId>com.aliyun</groupId>
        <artifactId>aliyun-java-sdk-core</artifactId>
    </dependency>
</dependencies>
```

service

```java
@Service
public class MsmServiceImpl implements MsmService {
    //发送短信的方法
    @Override
    public boolean send(Map<String, Object> param, String phone) {
        if(StringUtils.isEmpty(phone)) {return false;}

        DefaultProfile profile =
                DefaultProfile.getProfile("default", "xxx", "xxx");
        IAcsClient client = new DefaultAcsClient(profile);

        //设置相关固定参数
        CommonRequest request = new CommonRequest();
        //request.setProtocol(ProtocolType.HTTPS);
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");

        //设置发送相关的参数
        request.putQueryParameter("PhoneNumbers", phone); //手机号
        request.putQueryParameter("SignName", "我的在线教育学习网站"); // 签名名称
        request.putQueryParameter("TemplateCode", "SMS_213282822"); // 模板code
        request.putQueryParameter("TemplateParam", JSONObject.toJSONString(param)); // 验证码，map转json

        //最终发送

        try {
            CommonResponse response = client.getCommonResponse(request);
            System.out.println(response.getData());
            return response.getHttpResponse().isSuccess();
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return false;
    }
}
```

## 使用redis解决短信有效时间问题

controller

```java
@RestController
@RequestMapping("/edumsm/msm")
@CrossOrigin
public class MsmController {

    @Autowired
    private MsmService msmService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    //发送短信的方法
    @GetMapping("send/{phone}")
    public R sendMsm(@PathVariable String phone){
        // 1、从redis获取验证码,如果获取到直接返回
        String code = redisTemplate.opsForValue().get(phone);
        if (!StringUtils.isEmpty(code)){
            return R.ok();
        }
        // 2、如果获取不到，进行阿里云发送
        // 生成随机值，传递阿里云发送
        code = RandomUtil.getFourBitRandom();
        Map<String, Object> param = new HashMap<>();
        param.put("code", code);
        //调用service发送短信的方法
        boolean isSend = msmService.send(param,phone);
        if (isSend){
            // 发送成功，把发送成功验证码放到redis里面
            // 设置有效时间
            redisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);
            return R.ok();
        }else {
            return R.error().message("短信发送失败");
        }
    }
}
```



# 登录

## 实现方式

单一服务器传统登录模式.

1.使用session对象实现.

登录成功之后,用户数据放到session里面;

session.setAttribute("user",user);

从session获取数据可以判断是否登录.

session.getAttribute("user");



集群部署 分布式

单点登录(SSO):在一个模块进行登录, 其他模块就不需要登录了.

1.session广播机制实现.

session复制.

缺点:如果模块数量多,复制消耗资源.且数据重复

2.cookie+redis实现

cookie:客户端技术,每次请求都带着cookie值进行发送.

在项目中任何一个模块进行登录,登录之后把数据放到两个地方:

①redis,在key生成唯一随机值(规则随机),在value存放用户数据

②cookie,把redis里面生成的key值放到cookie里面.

访问项目中其他模块,发送请求带着cookie进行发送,获取cookie的值,根据key到redis进行查询,如果查询到数据就是登录.

3.使用token实现

token(自包含令牌):按照一定规则生成字符串,字符串可以包含用户信息.

在项目某个模块登录后,按照规则生成字符串,把登录后的用户包含到生成字符串里面,把字符串返回

①可通过cookie返回

②可通过地址栏返回

访问项目其他模块,每次访问在地址栏带着生成的字符串,在访问 模块里面获取地址栏字符串,根据字符串获取用户信息.如果可以获取到,就登录.



注意:第一种方式session默认30分钟过期时间,第二种方式没有使用session,可以设置redis过期时间.

## JWT令牌

token方式通用规则的解决方法代表,

生成的字符串包含三部分

1.JWT头:描述JWT元数据的json对象,

```
{
	"alg":"HS256",   #表示签名使用的算法,默认为HMAC SHA256
	"typ":"JWT" 	#表示令牌的类型,JWT令牌同一写为JWT
}
```

2.有效载荷:包含主体信息,也json对象,

3.哈希签名:防伪标志,对上面两部分数据签名,确保数据不会被篡改

使用Base64 URL算法将上述json对象转换为字符串保存

### 使用

ucenter模块 ucenter_member表

在common引入依赖 复制jwt工具类

登录的时候获取token,访问的时候请求头携带token.

```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt</artifactId>
</dependency>
```

```java
public class JwtUtils {
    //常量
    public static final long EXPIRE = 1000 * 60 * 60 * 24; //token过期时间
    public static final String APP_SECRET = "ukc8BDbRigUDaY6pZFfWus2jZWLPHO"; //秘钥
    //生成token字符串的方法
    public static String getJwtToken(String id, String nickname) {
        String JwtToken = Jwts.builder()
                .setHeaderParam("typ", "JWT")  //固定
                .setHeaderParam("alg", "HS256")

                .setSubject("guli-user")
                .setIssuedAt(new Date())     //当前时间
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE)) //过期时间

                .claim("id", id)  //设置token主体部分 ，存储用户信息
                .claim("nickname", nickname)

                .signWith(SignatureAlgorithm.HS256, APP_SECRET)  //
                .compact();
        return JwtToken;
    }
    /**
     * 判断token是否存在与有效
     */
    public static boolean checkToken(String jwtToken) {
        if (StringUtils.isEmpty(jwtToken)) {
            return false;
        }
        try {
            Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(jwtToken);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    /**
     * 判断token是否存在与有效
     */
    public static boolean checkToken(HttpServletRequest request) {
        try {
            String jwtToken = request.getHeader("guli_token");
            if (StringUtils.isEmpty(jwtToken)) {
                return false;
            }
            Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(jwtToken);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 根据token字符串获取会员id
     */
    public static String getMemberIdByJwtToken(HttpServletRequest request) {
        String jwtToken = request.getHeader("token");
        if (StringUtils.isEmpty(jwtToken)) {
            return "";
        }
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(jwtToken);
        Claims claims = claimsJws.getBody();
        return (String) claims.get("id");
    }
}
```

## 实现

```java
@Service
public class UcenterMemberServiceImpl extends ServiceImpl<UcenterMemberMapper, UcenterMember> implements UcenterMemberService {

    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    //登录的方法 返回token
    @Override
    public String login(UcenterMember member) {
        //获取登录手机号和密码
        String mobile = member.getMobile();
        String password = member.getPassword();

        //手机号和密码非空判断
        if(StringUtils.isEmpty(mobile) || StringUtils.isEmpty(password)) {
            throw new GuliException(20001,"登录失败");
        }

        //判断手机号是否正确
        QueryWrapper<UcenterMember> wrapper = new QueryWrapper<>();
        wrapper.eq("mobile",mobile);
        UcenterMember mobileMember = baseMapper.selectOne(wrapper);
        //判断查询对象是否为空
        if(mobileMember == null) {//没有这个手机号
            throw new GuliException(20001,"登录失败");
        }

        //判断密码
        //因为存储到数据库密码肯定加密的
        //把输入的密码进行加密， 再和数据库密码进行比较
        //加密方式 MD5  不能解密
        if(!MD5.encrypt(password).equals(mobileMember.getPassword())) {
            throw new GuliException(20001,"登录失败");
        }

        //判断用户是否禁用
        if(mobileMember.getIsDisabled()) {
            throw new GuliException(20001,"登录失败");
        }

        //登录成功
        //生成token字符串，使用jwt工具类
        String jwtToken = JwtUtils.getJwtToken(mobileMember.getId(), mobileMember.getNickname());
        return jwtToken;
    }

    //注册的方法
    @Override
    public void register(RegisterVo registerVo) {
        //获取注册的数据
        String code = registerVo.getCode(); //验证码
        String mobile = registerVo.getMobile(); //手机号
        String nickname = registerVo.getNickname(); //昵称
        String password = registerVo.getPassword(); //密码

        //非空判断
        if(StringUtils.isEmpty(mobile) || StringUtils.isEmpty(password)
                || StringUtils.isEmpty(code) || StringUtils.isEmpty(nickname)) {
            throw new GuliException(20001,"注册失败");
        }
        //判断验证码
        //获取redis验证码
        String redisCode = redisTemplate.opsForValue().get(mobile);
        if(!code.equals(redisCode)) {
            throw new GuliException(20001,"注册失败");
        }

        //判断手机号是否重复，表里面存在相同手机号不进行添加
        QueryWrapper<UcenterMember> wrapper = new QueryWrapper<>();
        wrapper.eq("mobile",mobile);
        Integer count = baseMapper.selectCount(wrapper);
        if(count > 0) {
            throw new GuliException(20001,"注册失败");
        }

        //数据添加数据库中
        UcenterMember member = new UcenterMember();
        member.setMobile(mobile);
        member.setNickname(nickname);
        member.setPassword(MD5.encrypt(password));//密码需要加密的
        member.setIsDisabled(false);//用户不禁用
        //默认头像地址
        member.setAvatar("http://thirdwx.qlogo.cn/mmopen/vi_32/DYAIOgq83eoj0hHXhgJNOTSOFsS4uZs8x1ConecaVOB8eIl115xmJZcT4oCicvia7wMEufibKtTLqiaJeanU2Lpg3w/132");
        baseMapper.insert(member);
    }

    //根据openid判断
    @Override
    public UcenterMember getOpenIdMember(String openid) {
        QueryWrapper<UcenterMember> wrapper = new QueryWrapper<>();
        wrapper.eq("openid",openid);
        UcenterMember member = baseMapper.selectOne(wrapper);
        return member;
    }

    //查询某一天注册人数
    @Override
    public Integer countRegisterDay(String day) {
        return baseMapper.countRegisterDay(day);
    }
}
```

```java
@RestController
@RequestMapping("/educenter/member")
@CrossOrigin
public class UcenterMemberController {

    @Autowired
    private UcenterMemberService memberService;

    //登录
    @PostMapping("login")
    public R loginUser(@RequestBody UcenterMember member) {
        //member对象封装手机号和密码
        //调用service方法实现登录
        //返回token值，使用jwt生成
        String token = memberService.login(member);
        return R.ok().data("token",token);
    }

    //注册
    @PostMapping("register")
    public R registerUser(@RequestBody RegisterVo registerVo) {
        memberService.register(registerVo);
        return R.ok();
    }

    //根据token获取用户信息
    @GetMapping("getMemberInfo")
    public R getMemberInfo(HttpServletRequest request) {
        //调用jwt工具类的方法。根据request对象获取头信息，返回用户id
        String memberId = JwtUtils.getMemberIdByJwtToken(request);
        //查询数据库根据用户id获取用户信息
        UcenterMember member = memberService.getById(memberId);
        return R.ok().data("userInfo",member);
    }

    //根据用户id获取用户信息
    @PostMapping("getUserInfoOrder/{id}")
    public UcenterMemberOrder getUserInfoOrder(@PathVariable String id) {
        UcenterMember member = memberService.getById(id);
        //把member对象里面值复制给UcenterMemberOrder对象
        UcenterMemberOrder ucenterMemberOrder = new UcenterMemberOrder();
        BeanUtils.copyProperties(member,ucenterMemberOrder);
        return ucenterMemberOrder;
    }

    //查询某一天注册人数
    @GetMapping("countRegister/{day}")
    public R countRegister(@PathVariable String day) {
        Integer count = memberService.countRegisterDay(day);
        return R.ok().data("countRegister",count);
    }
}
```

# OAuth2

主要解决 开放系统间授权 和 分布式访问(单点登录)问题

只是一种解决方案,并没有支持HTTP以外的协议,并不是一个认证协议,并没有定义授权处理机制、token格式和加密方法,并不是一个单个协议.

OAuth2仅是授权框架,仅用于授权代理 .

# 微信登录 

基于OAuth2

准备工作:

1.注册开发者资质 https://open.weixin.qq.com/

支持企业类型;注册之后,提供微信id和微信密钥

2.申请网站应用名称

3.需要域名地址

文档

https://developers.weixin.qq.com/doc/oplatform/Website_App/WeChat_Login/Wechat_Login.html

## 使用

在ucenter模块中

新建工具类

```java
@Component
public class ConstantWxUtils implements InitializingBean {
    @Value("${wx.open.app_id}")
    private String appId;

    @Value("${wx.open.app_secret}")
    private String appSecret;

    @Value("${wx.open.redirect_url}")
    private String redirectUrl;

    public static String WX_OPEN_APP_ID;
    public static String WX_OPEN_APP_SECRET;
    public static String WX_OPEN_REDIRECT_URL;

    @Override
    public void afterPropertiesSet() throws Exception {
        WX_OPEN_APP_ID = appId;
        WX_OPEN_APP_SECRET = appSecret;
        WX_OPEN_REDIRECT_URL = redirectUrl;
    }
}
```

生成微信扫描二维码:直接请求微信提供固定的地址,向地址后面拼接参数.

用户扫码允许授权后，将会重定向到redirect_uri的网址(公司域名网址)上

```java
@CrossOrigin
@Controller  //只是请求地址，不需要返回数据
@RequestMapping("/api/ucenter/wx")
public class WxApiController {

    @Autowired
    private UcenterMemberService memberService;

    //2 获取扫描人信息，添加数据
    @GetMapping("callback")
    public String callback(String code, String state) {
        try {
            //1 获取code值，临时票据，类似于验证码
            //2 拿着code请求 微信固定的地址，得到两个值 accsess_token 和 openid
            String baseAccessTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token" +
                    "?appid=%s" +
                    "&secret=%s" +
                    "&code=%s" +
                    "&grant_type=authorization_code";
            //拼接三个参数 ：id  秘钥 和 code值
            String accessTokenUrl = String.format(
                    baseAccessTokenUrl,
                    ConstantWxUtils.WX_OPEN_APP_ID,
                    ConstantWxUtils.WX_OPEN_APP_SECRET,
                    code
            );
            //请求这个拼接好的地址，得到返回两个值 accsess_token 和 openid
            //使用httpclient发送请求，得到返回结果
            String accessTokenInfo = HttpClientUtils.get(accessTokenUrl);

            //从accessTokenInfo字符串获取出来两个值 accsess_token 和 openid
            //把accessTokenInfo字符串转换map集合，根据map里面key获取对应值
            //使用json转换工具 Gson
            Gson gson = new Gson();
            HashMap mapAccessToken = gson.fromJson(accessTokenInfo, HashMap.class);
            String access_token = (String)mapAccessToken.get("access_token");
            String openid = (String)mapAccessToken.get("openid");

            //把扫描人信息添加数据库里面
            //判断数据表里面是否存在相同微信信息，根据openid判断
            UcenterMember member = memberService.getOpenIdMember(openid);
            if(member == null) {//memeber是空，表没有相同微信数据，进行添加

                //3 拿着得到accsess_token 和 openid，再去请求微信提供固定的地址，获取到扫描人信息
                //访问微信的资源服务器，获取用户信息
                String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo" +
                        "?access_token=%s" +
                        "&openid=%s";
                //拼接两个参数
                String userInfoUrl = String.format(
                        baseUserInfoUrl,
                        access_token,
                        openid
                );
                //发送请求
                String userInfo = HttpClientUtils.get(userInfoUrl);
                //获取返回userinfo字符串扫描人信息
                HashMap userInfoMap = gson.fromJson(userInfo, HashMap.class);
                String nickname = (String)userInfoMap.get("nickname");//昵称
                String headimgurl = (String)userInfoMap.get("headimgurl");//头像

                member = new UcenterMember();
                member.setOpenid(openid);
                member.setNickname(nickname);
                member.setAvatar(headimgurl);
                memberService.save(member);
            }


            //使用jwt根据member对象生成token字符串
            String jwtToken = JwtUtils.getJwtToken(member.getId(), member.getNickname());
            //最后：返回首页面，通过路径传递token字符串
            return "redirect:http://localhost:3000?token="+jwtToken;
        }catch(Exception e) {
            throw new GuliException(20001,"登录失败");
        }
    }

    //1 生成微信扫描二维码
    @GetMapping("login")
    public String getWxCode() {
        //固定地址，后面拼接参数
//        String url = "https://open.weixin.qq.com/" +
//                "connect/qrconnect?appid="+ ConstantWxUtils.WX_OPEN_APP_ID+"&response_type=code";

        // 微信开放平台授权baseUrl  %s相当于?代表占位符
        String baseUrl = "https://open.weixin.qq.com/connect/qrconnect" +
                "?appid=%s" +
                "&redirect_uri=%s" +
                "&response_type=code" +
                "&scope=snsapi_login" +
                "&state=%s" +
                "#wechat_redirect";

        //对redirect_url进行URLEncoder编码
        String redirectUrl = ConstantWxUtils.WX_OPEN_REDIRECT_URL;
        try {
            redirectUrl = URLEncoder.encode(redirectUrl, "utf-8");
        }catch(Exception e) {
        }

        //设置%s里面值
        String url = String.format(
                    baseUrl,
                    ConstantWxUtils.WX_OPEN_APP_ID,
                    redirectUrl,
                    "atguigu"
                 );
        //重定向到请求微信地址里面
        return "redirect:"+url;
    }
}
```







# 前端用户登录注册

在nuxt中安装插件

npm install element-ui

npm install vue-qriously  (下载二维码的组件)

修改nuxt-swipper-plugin.js,使用插件

```js
import Vue from 'vue'
import VueAwesomeSwiper from 'vue-awesome-swiper/dist/ssr'
import VueQriously from 'vue-qriously'
import ElementUI from 'element-ui' //element-ui的全部组件
import 'element-ui/lib/theme-chalk/index.css'//element-ui的css
Vue.use(ElementUI) //使用elementUI
Vue.use(VueQriously)
Vue.use(VueAwesomeSwiper)
```

## 页面跳转

因为登录和注册在默认布局里,修改default.vue.

这里表示登录会跳转到pages的login,注册会跳转到pages的register

```html
 <a href="/login" title="登录">
                <em class="icon18 login-icon">&nbsp;</em>
                <span class="vam ml5">登录</span>
              </a>
              |
              <a href="/register" title="注册">
                <span class="vam ml5">注册</span>
              </a>
```

## 更换布局

在layouts中新建sign.vue

```vue
<template>
    <div class="sign">
        <!--标题-->
        <div class="logo">
            <img src="~/assets/img/logo.png" alt="logo">
        </div>
        <!--表单-->
        <nuxt/>
    </div>
</template>
<style scoped>
</style>
```

在pages页面scrpt的export default {}中指定使用的布局:

​    layout: 'sign',

## 微信登录

下载插件 npm install js-cookie





## 登录注册页面

需要引入静态css文件,在pages下新建login.vue  register.vue.

其中获取验证码后需要60s倒计时,登录后需要携带token到header(先放到cookie,再利用前端拦截器放到header)跳转到主页.

```vue
<template>
    <div class="main">
        <div class="title">
            <a class="active" href="/login">登录</a> <!-- 它会去pages目录下找login.vue-->
            <span>·</span>
            <a href="/register">注册</a>
        </div>

        <div class="sign-up-container">
            <el-form ref="userForm" :model="user">

                <el-form-item class="input-prepend restyle" prop="mobile"
                              :rules="[{ required: true, message: '请输入手机号码', trigger: 'blur' },{validator: checkPhone, trigger: 'blur'}]">
                    <div>
                        <el-input type="text" placeholder="手机号" v-model="user.mobile"/>
                        <i class="iconfont icon-phone"/>
                    </div>
                </el-form-item>

                <el-form-item class="input-prepend" prop="password"
                              :rules="[{ required: true, message: '请输入密码', trigger: 'blur' }]">
                    <div>
                        <el-input type="password" placeholder="密码" v-model="user.password"/>
                        <i class="iconfont icon-password"/>
                    </div>
                </el-form-item>

                <div class="btn">
                    <input type="button" class="sign-in-button" value="登录" @click="submitLogin()">
                </div>
            </el-form>
            <!-- 更多登录方式 -->
            <div class="more-sign">
                <h6>社交帐号登录</h6>
                <ul>
                    <li><a id="weixin" class="weixin" target="_blank"
                           href="http://localhost:9001/api/ucenter/wx/login"><i
                        class="iconfont icon-weixin"/></a></li>
                    <li><a id="qq" class="qq" target="_blank" href="#"><i class="iconfont icon-qq"/></a></li>
                </ul>
            </div>
        </div>

    </div>
</template>
<script>
	import '~/assets/css/sign.css'
	import '~/assets/css/iconfont.css'
	import cookie from 'js-cookie'
	import loginApi from "@/api/login";

	export default {
		layout: 'sign',
		data() {
			return {
                //封装登录手机号和密码对象
				user: {
					mobile: '',
					password: ''
				},
				loginInfo: {},

                // token:''
			}
		},
		methods: {
			//登录,接收token
			submitLogin() {
				loginApi.login(this.user).then(response => {
					console.log(response.data)
					if(response.data.code === 20000){
						//得到token,并置入cookie
                        //第一个参数 cookie参数名称  第二个参数为 参数值  第三个参数为 作用范围
						cookie.set('guli_token',response.data.data.token,{
							domain:'localhost' //作用范围 表示在什么样的请求才进行传递
						})

						//根据token获取用户信息 为了首页显示
						loginApi.getLoginUserInfo().then(response=>{
							this.loginInfo = response.data.data.userInfo;
                            //把用户信息也放到cookie中
							cookie.set('guli_ucenter',this.loginInfo,{
								domain:'localhost'
							})
							//提示
							this.$message({
								type:"success",
								message:"登录成功!"
							})
                            //跳转到首页面
							window.location.href = '/'
                            // this.$router.push({path:'/'})
						})
                    }else {
						//提示
						this.$message({
							type:"warning",
							message:`${response.data.message}`
						})
                        this.user = {}
                    }
				})
			},
			checkPhone(rule, value, callback) {
				//debugger
				if (!(/^1[34578]\d{9}$/.test(value))) {
					return callback(new Error('手机号码格式不正确'))
				}
				return callback()
			}
		}
	}
</script>
<style>
    .el-form-item__error {
        z-index: 9999999;
    }
</style>
```

```vue
<template>
    <div class="main">
        <div class="title">
            <a href="/login">登录</a>
            <span>·</span>
            <a class="active" href="/register">注册</a>
        </div>

        <div class="sign-up-container">
            <el-form ref="userForm" :model="params">
                    <!-- :rules指定规则 这里表示如果没有内容在失去焦点时(blur)就提示消息   -->
                <el-form-item class="input-prepend restyle" prop="nickname" :rules="[{ required: true, message: '请输入你的昵称', trigger: 'blur' }]">
                    <div>
                        <el-input type="text" placeholder="你的昵称" v-model="params.nickname"/>
                        <i class="iconfont icon-user"/>
                    </div>
                </el-form-item>
                    <!-- :rules还可以自己指定规则  手机号的规范性 {validator: checkPhone, trigger: 'blur'}-->
                <el-form-item class="input-prepend restyle no-radius" prop="mobile" :rules="[{ required: true, message: '请输入手机号码', trigger: 'blur' },{validator: checkPhone, trigger: 'blur'}]">
                    <div>
                        <el-input type="text" placeholder="手机号" v-model="params.mobile"/>
                        <i class="iconfont icon-phone"/>
                    </div>
                </el-form-item>

                <el-form-item class="input-prepend restyle no-radius" prop="code" :rules="[{ required: true, message: '请输入验证码', trigger: 'blur' }]">
                    <div style="width: 100%;display: block;float: left;position: relative">
                        <el-input type="text" placeholder="验证码" v-model="params.code"/>
                        <i class="iconfont icon-phone"/>
                    </div>
                    <div class="btn" style="position:absolute;right: 0;top: 6px;width: 40%;">
                        <!-- 获取验证码 -->
                        <a href="javascript:" type="button" @click="getCodeFun()" :value="codeTest" style="border: none;background-color: none">{{codeTest}}</a>
                    </div>
                </el-form-item>

                <el-form-item class="input-prepend" prop="password" :rules="[{ required: true, message: '请输入密码', trigger: 'blur' }]">
                    <div>
                        <el-input type="password" placeholder="设置密码" v-model="params.password"/>
                        <i class="iconfont icon-password"/>
                    </div>
                </el-form-item>

                <div class="btn">
                    <input type="button" class="sign-up-button" value="注册" @click="submitRegister()">
                </div>
                <p class="sign-up-msg">
                    点击 “注册” 即表示您同意并愿意遵守简书
                    <br>
                    <a target="_blank" href="http://www.jianshu.com/p/c44d171298ce">用户协议</a>
                    和
                    <a target="_blank" href="http://www.jianshu.com/p/2ov8x3">隐私政策</a> 。
                </p>
            </el-form>
            <!-- 更多注册方式 -->
            <div class="more-sign">
                <h6>社交帐号直接注册</h6>
                <ul>
                    <li><a id="weixin" class="weixin" target="_blank" href="http://huaan.free.idcfengye.com/api/ucenter/wx/login"><i
                        class="iconfont icon-weixin"/></a></li>
                    <li><a id="qq" class="qq" target="_blank" href="#"><i class="iconfont icon-qq"/></a></li>
                </ul>
            </div>
        </div>
    </div>
</template>

<script>
	import '~/assets/css/sign.css'
	import '~/assets/css/iconfont.css'

	import registerApi from '@/api/register'

	export default {
		layout: 'sign',
		data() {
			return {  
				params:{
					mobile:'',
                    code:'',
					nickname:'',
                    password:''
                },
                sending:true,
                second:60,
                codeTest:'获取验证码'
			}
		},
		methods: {
			getCodeFun(){ //通过手机号发送验证码
            console.log('发送验证码')
                registerApi.sendCode(this.params.mobile).then(response=>{
                	if(response.data.code === 20000){
						this.sending = false
                        console.log('开始倒计时')
						this.timeDown();//倒计时
                    }else{
						//提示
						this.$message({
							type:"warning",
							message:`${response.data.message}`
						})
                    }

                })
            },
            //提交注册
			submitRegister(){
                registerApi.register(this.params).then(response=>{
                	//提示
                    this.$message({
                        type:"success",
                        message:"注册成功"
                    })
                    //跳转到login
                    this.$router.push({
                        path:'/login'
                    })

                })
            },
			timeDown() {         //倒计时
				let result = setInterval(() => {       //js定时器方法
					--this.second;
					this.codeTest = this.second
                    console.log('时间计数减一')
					if (this.second < 1) {
						clearInterval(result);
						this.sending = true;
						//this.disabled = false;
						this.second = 60;
						this.codeTest = "获取验证码"
					}
				}, 1000);

			},
			checkPhone (rule, value, callback) {
				//debugger
				if (!(/^1[34578]\d{9}$/.test(value))) {
					return callback(new Error('手机号码格式不正确'))
				}
				return callback()
			}
		}
	}
</script>

```

在api下新建login.js  register.js

```js
import request from '@/utils/request'

export default {
	//登录
	login(user) {
		return request({
			url: `/educenter/member/login`,
			method: 'post',
			data: user
		})
	},
	//根据token获取用户信息
	getLoginUserInfo(){
		return request({
			url: `/educenter/member/getMemberInfo`,
			method: 'get'
		})
	}
}
```

```js
import request from '@/utils/request'

export default {
	//根据手机号发送验证码
	sendCode(phone) {
		return request({
			url: `/edumsm/msm/send/${phone}`,
			method: 'get'
		})
	},
	//注册
	register(formItem) {
		return request({
			url: `/educenter/member/register`,
			method: 'post',
			data:formItem
		})
	}
}
```

## 拦截器传递token

因为login.vue将token存入了cookie,但后端是从header中查看.

利用拦截器先判断cookie里面是否有token字符串,如果有把token字符串放到header中.

好处:比cookie更安全,后面的支付模块也是.

修改utils.request.js

```js
import axios from 'axios'
// import {MessageBox,Message} from "element-ui";
 import cookie from 'js-cookie'
// 创建axios实例
const service = axios.create({
	baseURL: 'http://localhost:9001', // api的base_url
	timeout: 20000 // 请求超时时间
})
//拦截器
service.interceptors.request.use(
	config => {
		if (cookie.get('guli_token')) {
			config.headers['token'] = cookie.get('guli_token');
		}
		return config
	},
	error => {
		return Promise.reject(error)
	}
	
// )
// service.interceptors.response.use(
// 	response => {
// 		if (response.data.code !== 200) {
			
// 			if(response.data.code!==250){
// 				Message({
// 					message:response.data.message||'error',
// 					type:'error',
// 					duration:5*1000
// 				})
// 			}
// 		}
// 		return response
// 	}
 )
export default service
```



## 用户信息显示

在login.vue中根据token值调用接口获取用户信息后,把用户信息放到cookie里面,在首页获取显示.

修改layouts/default.vue

```vue
<template>
  ....
  <ul class="h-r-login">
            <li v-if="!loginInfo.id" id="no-login">
              <a href="/login" title="登录">
                <em class="icon18 login-icon">&nbsp;</em>
                <span class="vam ml5">登录</span>
              </a>
              |
              <a href="/register" title="注册">
                <span class="vam ml5">注册</span>
              </a>
            </li>
            <li v-if="loginInfo.id" id="is-login-one" class="mr10">
              <a id="headerMsgCountId" href="#" title="消息">
                <em class="icon18 news-icon">&nbsp;</em>
              </a>
              <q class="red-point" style="display: none">&nbsp;</q>
            </li>
            <li v-if="loginInfo.id" id="is-login-two" class="h-r-user">
              <a href="/ucenter" title>
                <img
                  :src="loginInfo.avatar"
                  width="30"
                  height="30"
                  class="vam picImg"
                  alt
                />
                <span id="userName" class="vam disIb">{{loginInfo.nickname}}</span>
              </a>
              <a
                href="javascript:void(0);"
                title="退出"
                @click="logout()"
                class="ml5"
                >退出</a
              >
            </li>
            <!-- /未登录显示第1 li；登录后显示第2，3 li -->
          </ul>
</template>
......
import cookie from "js-cookie";
import loginApi from "@/api/login";

export default {
  data() {
    return {
      token: "",
      loginInfo: {
        id: "",
        age: "",
        avatar: "",
        mobile: "",
        nickname: "",
        sex: "",
      },
    };
  },
  created() {
    this.getInfo();
  },
  methods: {
    getInfo() {
      //从cookie中获取信息
      let user = cookie.get("guli_ucenter");
      //把字符串转换为json对象  因为后端传过来的是带引号的字符串
      if (user) {
        this.loginInfo = JSON.parse(user);
      }
    },
  },
};
</script>
```

## 退出

退出登录只需要清空cookie

```js
logout() {
      //清空cookie
      cookie.set("guli_token", "", {
        domain: "localhost",
      });
      cookie.set("guli_uenter", "", {
        domain: "localhost",
      });
      location.href = "/";
    },
```

# 课程评论

数据表edu_comment

```java
@Api(description = "评论")
@RestController
@RequestMapping("/eduservice/comment")
@CrossOrigin
public class EduCommentController {

   @Autowired
   private EduCommentService commentService;

   @Autowired
   private UcenterClient ucenterClient;

   @ApiOperation(value = "分页查询所有评论")
   @GetMapping("getCommentList/{page}/{limit}/{courseId}")
   public R getCommentList(@PathVariable long page, @PathVariable long limit,@PathVariable String courseId) {
      Page<EduComment> pageParam = new Page<>(page, limit);

      QueryWrapper<EduComment> wrapper = new QueryWrapper<>();
      wrapper.eq("course_id", courseId);

      commentService.page(pageParam, wrapper);
      List<EduComment> commentList = pageParam.getRecords();

      Map<String, Object> map = new HashMap<>();
      map.put("items", commentList);
      map.put("current", pageParam.getCurrent());
      map.put("pages", pageParam.getPages());
      map.put("size", pageParam.getSize());
      map.put("total", pageParam.getTotal());
      map.put("hasNext", pageParam.hasNext());
      map.put("hasPrevious", pageParam.hasPrevious());

      return R.ok().data(map);
   }

   @ApiOperation(value = "添加评论")
   @PostMapping("saveComment")
   public R saveComment(@RequestBody EduComment comment, HttpServletRequest request) {
      String memberId = JwtUtils.getMemberIdByJwtToken(request);
      if (StringUtils.isEmpty(memberId)) {
         return R.error().code(28004).message("请登录");
      }

      comment.setMemberId(memberId);
      System.out.println("*****" + comment);

      UcenterMemberOrder ucenterInfo = ucenterClient.getUcenterPay(memberId);
      System.out.println("===++" + ucenterInfo);

      comment.setNickname(ucenterInfo.getNickname());
      comment.setAvatar(ucenterInfo.getAvatar());

      commentService.save(comment);
      return R.ok();
   }
}
```

## 分页查询

```html
<div>
                        <div class="paging">
                            <!-- undisable这个class是否存在，取决于数据属性hasPrevious -->
                            <a
                                :class="{undisable: !data.hasPrevious}"
                                href="#"
                                title="首页"
                                @click.prevent="gotoPage(1)">首页</a>

                            <a
                                v-if="data.hasPrevious"
                                :class="{undisable: !data.hasPrevious}"
                                href="#"
                                title="前一页"
                                @click.prevent="gotoPage(data.current-1)">&lt;</a>

                            <a
                                v-for="page in data.pages"
                                :key="page"
                                :class="{current: data.current == page, undisable: data.current == page}"
                                :title="'第'+page+'页'"
                                href="#"
                                @click.prevent="gotoPage(page)">{{ page }}</a>

                            <a
                                v-if="data.hasNext"
                                :class="{undisable: !data.hasNext}"
                                href="#"
                                title="后一页"
                                @click.prevent="gotoPage(data.current+1)">&gt;</a>

                            <a
                                :class="{undisable: !data.hasNext}"
                                href="#"
                                title="末页"
                                @click.prevent="gotoPage(data.pages)">末页</a>
                            <div class="clear"></div>
                        </div>
                    </div>
```

## 添加评论(较复杂)

先登录

评论用户信息的获取:从header获取token字符串,根据token获取用户id,再查询.

# 订单模块

需要远程调用edu模块和umenber模块

## 订单

```java
@RestController
@RequestMapping("/eduorder/order")
@CrossOrigin
public class OrderController {

    @Autowired
    private OrderService orderService;

    //1 生成订单的方法
    @PostMapping("createOrder/{courseId}")
    public R saveOrder(@PathVariable String courseId, HttpServletRequest request) {
        //创建订单，返回订单号
        String orderNo =
                orderService.createOrders(courseId, JwtUtils.getMemberIdByJwtToken(request));
        return R.ok().data("orderId",orderNo);
    }

    //2 根据订单id查询订单信息
    @GetMapping("getOrderInfo/{orderId}")
    public R getOrderInfo(@PathVariable String orderId) {
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.eq("order_no",orderId);
        Order order = orderService.getOne(wrapper);
        return R.ok().data("item",order);
    }

    //根据课程id和用户id查询订单表中订单状态
    @GetMapping("isBuyCourse/{courseId}/{memberId}")
    public boolean isBuyCourse(@PathVariable String courseId,@PathVariable String memberId) {
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.eq("course_id",courseId);
        wrapper.eq("member_id",memberId);
        wrapper.eq("status",1);//支付状态 1代表已经支付
        int count = orderService.count(wrapper);
        if(count>0) { //已经支付
            return true;
        } else {
            return false;
        }
    }
}
```

```java
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
    @Autowired
    private EduClient eduClient;
    @Autowired
    private UcenterClient ucenterClient;
    //1 生成订单的方法
    @Override
    public String createOrders(String courseId, String memberId) {
        //通过远程调用根据用户id获取用户信息
        UcenterMemberOrder userInfoOrder = ucenterClient.getUserInfoOrder(memberId);
        //通过远程调用根据课程id获取课信息
        CourseWebVoOrder courseInfoOrder = eduClient.getCourseInfoOrder(courseId);
        //创建Order对象，向order对象里面设置需要数据
        Order order = new Order();
        order.setOrderNo(OrderNoUtil.getOrderNo());//订单号
        order.setCourseId(courseId); //课程id
        order.setCourseTitle(courseInfoOrder.getTitle());
        order.setCourseCover(courseInfoOrder.getCover());
        order.setTeacherName(courseInfoOrder.getTeacherName());
        order.setTotalFee(courseInfoOrder.getPrice());
        order.setMemberId(memberId);
        order.setMobile(userInfoOrder.getMobile());
        order.setNickname(userInfoOrder.getNickname());
        order.setStatus(0);  //订单状态（0：未支付 1：已支付）
        order.setPayType(1);  //支付类型 ，微信1
        baseMapper.insert(order);
         //返回订单号
        return order.getOrderNo();
    }
}
```

## 支付

```java
@RestController
@RequestMapping("/eduorder/paylog")
@CrossOrigin
public class PayLogController {

    @Autowired
    private PayLogService payLogService;

    //生成微信支付二维码接口
    //参数是订单号
    @GetMapping("createNative/{orderNo}")
    public R createNative(@PathVariable String orderNo) {
        //返回信息，包含二维码地址，还有其他需要的信息
        Map map = payLogService.createNatvie(orderNo);
        System.out.println("****返回二维码map集合:"+map);
        return R.ok().data(map);
    }

    //查询订单支付状态
    //参数：订单号，根据订单号查询 支付状态
    @GetMapping("queryPayStatus/{orderNo}")
    public R queryPayStatus(@PathVariable String orderNo) {
        Map<String,String> map = payLogService.queryPayStatus(orderNo);
        System.out.println("*****查询订单状态map集合:"+map);
        if(map == null) {
            return R.error().message("支付出错了");
        }
        //如果返回map里面不为空，通过map获取订单状态
        if(map.get("trade_state").equals("SUCCESS")) {//支付成功
            //添加记录到支付表，更新订单表订单状态
            payLogService.updateOrdersStatus(map);
            return R.ok().message("支付成功");
        }
        return R.ok().code(25000).message("支付中");

    }
}
```

```java
@Service
public class PayLogServiceImpl extends ServiceImpl<PayLogMapper, PayLog> implements PayLogService {
    @Autowired
    private OrderService orderService;
    //生成微信支付二维码接口
    @Override
    public Map createNatvie(String orderNo) {
        try {
            //1 根据订单号查询订单信息
            QueryWrapper<Order> wrapper = new QueryWrapper<>();
            wrapper.eq("order_no",orderNo);
            Order order = orderService.getOne(wrapper);
            //2 使用map设置生成二维码需要参数
            Map m = new HashMap();
            m.put("appid","wx74862e0dfcf69954");
            m.put("mch_id", "1558950191");
            m.put("nonce_str", WXPayUtil.generateNonceStr());
            m.put("body", order.getCourseTitle()); //课程标题
            m.put("out_trade_no", orderNo); //订单号
            m.put("total_fee", order.getTotalFee().multiply(new BigDecimal("100")).longValue()+"");
            m.put("spbill_create_ip", "127.0.0.1");
            m.put("notify_url", "http://guli.shop/api/order/weixinPay/weixinNotify\n");
            m.put("trade_type", "NATIVE");
            //3 发送httpclient请求，传递参数xml格式，微信支付提供的固定的地址
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            //设置xml格式的参数
            client.setXmlParam(WXPayUtil.generateSignedXml(m,"T6m9iK73b0kn9g5v426MKfHQH7X8rKwb"));
            client.setHttps(true);
            //执行post请求发送
            client.post();
            //4 得到发送请求返回结果
            //返回内容，是使用xml格式返回
            String xml = client.getContent();
            //把xml格式转换map集合，把map集合返回
            Map<String,String> resultMap = WXPayUtil.xmlToMap(xml);
            //最终返回数据 的封装
            Map map = new HashMap();
            map.put("out_trade_no", orderNo);
            map.put("course_id", order.getCourseId());
            map.put("total_fee", order.getTotalFee());
            map.put("result_code", resultMap.get("result_code"));  //返回二维码操作状态码
            map.put("code_url", resultMap.get("code_url"));        //二维码地址
            return map;
        }catch(Exception e) {
            throw new GuliException(20001,"生成二维码失败");
        }
    }
    //查询订单支付状态
    @Override
    public Map<String, String> queryPayStatus(String orderNo) {
        try {
            //1、封装参数
            Map m = new HashMap<>();
            m.put("appid", "wx74862e0dfcf69954");
            m.put("mch_id", "1558950191");
            m.put("out_trade_no", orderNo);
            m.put("nonce_str", WXPayUtil.generateNonceStr());
            //2 发送httpclient
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
           client.setXmlParam(WXPayUtil.generateSignedXml(m,"T6m9iK73b0kn9g5v426MKfHQH7X8rKwb"));
            client.setHttps(true);
            client.post();
            //3 得到请求返回内容
            String xml = client.getContent();
            Map<String, String> resultMap = WXPayUtil.xmlToMap(xml);
            //6、转成Map再返回
            return resultMap;
        }catch(Exception e) {
            return null;
        }
    }
    //添加支付记录和更新订单状态
    @Override
    public void updateOrdersStatus(Map<String, String> map) {
        //从map获取订单号
        String orderNo = map.get("out_trade_no");
        //根据订单号查询订单信息
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.eq("order_no",orderNo);
        Order order = orderService.getOne(wrapper);
        //更新订单表订单状态
        if(order.getStatus().intValue() == 1) { return; }
        order.setStatus(1);//1代表已经支付
        orderService.updateById(order);
        //向支付表添加支付记录
        PayLog payLog = new PayLog();
        payLog.setOrderNo(orderNo);  //订单号
        payLog.setPayTime(new Date()); //订单完成时间
        payLog.setPayType(1);//支付类型 1微信
        payLog.setTotalFee(order.getTotalFee());//总金额(分)
        payLog.setTradeState(map.get("trade_state"));//支付状态
        payLog.setTransactionId(map.get("transaction_id")); //流水号
        payLog.setAttr(JSONObject.toJSONString(map));
        baseMapper.insert(payLog);
    }
}
```

# router.push与location.href的用法区别

①vue-router使用pushState进行路由更新，静态跳转，页面不会重新加载；location.href会触发浏览器，页面重新加载一次

②vue-router使用diff算法，实现按需加载，减少dom操作

③vue-router是路由跳转或同一个页面跳转；location.href是不同页面间跳转；

④vue-router是异步加载this.$nextTick(()=>{获取url})；location.href是同步加载

# a标签打开新页面

点击打开新页面显示  添加属性  target="_blank"

# 后台定时任务

spring提供

cron设置规则:www.pppet.net   6位 不是7位

主启动类添加@EnableScheduling // 开启定时任务

```java
@Component
public class ScheduledTask {

    @Autowired
    private StatisticsDailyService staService;

    // 0/5 * * * * ?表示每隔5秒执行一次这个方法
    @Scheduled(cron = "0/5 * * * * ?")
    public void task1() {
        //System.out.println("**************task1执行了..");
    }

    //在每天凌晨1点，把前一天数据进行数据查询添加
    @Scheduled(cron = "0 0 1 * * ?")
    public void task2() {
        staService.registerCount(DateUtil.formatDate(DateUtil.addDays(new Date(), -1)));
    }
}
```

# ECharts

用于图标展示 https://echarts.apache.org/zh/index.html

本项目使用的是npm install --save echar ts@4.1.0

示例见官网

```vue
<template>
  <div class="app-container">
    <!--表单-->
    <el-form :inline="true" class="demo-form-inline">
      <el-form-item>
          <el-select v-model="searchObj.type" class="demo-form-inline">
              <el-option label="学员登录数统计" value="login_num"/>
              <el-option label="学员注册数统计" value="register_num"/>
              <el-option label="课程播放数统计" value="video_view_num"/>
              <el-option label="每日课程数统计" value="course_num"/>
          </el-select>
      </el-form-item>
      <el-form-item>
        <el-date-picker
          v-model="searchObj.begin"
          type="date"
          placeholder="选择开始日期"
          value-format="yyyy-MM-dd" />
      </el-form-item>
      <el-form-item>
        <el-date-picker
          v-model="searchObj.end"
          type="date"
          placeholder="选择截止日期"
          value-format="yyyy-MM-dd" />
      </el-form-item>
      <el-button
        :disabled="btnDisabled"
        type="primary"
        icon="el-icon-search"
        @click="getDataSta()">查询</el-button>
    </el-form>
   <!-- 图标 -->
    <div class="class-container">
        <div id="chart" class="chart" style="height:500px;width:100%"></div>
    </div>
  </div>
</template>
<script>
import echarts from 'echarts'
import sta from '@/api/sta'

export default {
  data() {
    return {
      searchObj: {
        type:'',
        begin: '',
        end: ''
      },
    //   chart:'',
      btnDisabled: false,
      xData:[],
      yData:[]
    }
  },

  methods: {
     getDataSta() {
      sta.getDataSta(this.searchObj).then(response => {
        this.xData = response.data.date_calculatedList
        this.yData = response.data.numDataList
        //调用下面生成图表的方法
        this.setChart()
      })
    },

    setChart() {
      // 基于准备好的dom，初始化echarts实例
      this.chart= echarts.init(document.getElementById('chart'))
      // 指定图表的配置项和数据
      var option = {
        xAxis: {
            type:'category',
            data: this.xData
        },
        yAxis: {
            type:'value',
        },
        series: [{
          data: this.yData,
          type: 'line'   //折线图
        }]
      }
      // 使用刚指定的配置项和数据显示图表。
      this.chart.setOption(option)
    }
  }
}
</script>
```

# 数据同步canal

平常都是通过远程过程调用操作不同的数据库.

远程数据库同步到本地数据库.仅支持mysql

基于mysql binlog技术.

https://github.com/alibaba/canal

## 准备

远程服务器

1.:数据库 数据表;

2.开启mysql的binlog写入功能.

```sql
show variables like 'log_bin';  //检查   默认OFF
```

```conf
修改mysql配置文件(linux为my.cnf vi /etc/my.cnf)
log-bin=mysql-bin  #binlog文件名
binlog_format=ROW  #选择row模式
server_id=1		   #mysql示例id,不能与canal的slaveId重复
```

3.重启mysql数据库

4.在mysql里面添加以下的相关用户权限

```SQL
create user 'canal'@'%' identified by 'canal';
grant show view,select,replication slave,replication client on *.* to 'canal'@'%';
FLUSH PRIVILEGES
```

5.安装canal

https://github.com/alibaba/canal/releases

tar zxcf canal.deployer-1.14.tar.gz

修改配置文件(vi conf/example/instance.properties)

```
canal.instance.master.address=192.168.44.131 #自己数据库信息
canal.instance.dbUsername=canal
canal.instance.dbPassword=canal
canal.instance.filter.regex=.*\\..*             #同步规则 表示所有表
canal.instance.filter.regex=guli.ucenter_menber #同步规则 表示库.表
canal.instance.filter.regex=guli\\..*           #同步规则 表示库下所有表
canal.instance.filter.regex=guli\\.canal.*      #同步规则 表示库下以canal开头的表
#多个规则之间以,分开
```

6.启动

./startup.sh



本地服务器:数据库 

1.数据表  名字和结构与远程服务器的一样

2.在新模块中加入

```xml
   <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!--mysql-->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
        </dependency>

        <dependency>
            <groupId>commons-dbutils</groupId>
            <artifactId>commons-dbutils</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-jdbc</artifactId>
        </dependency>

        <dependency>
            <groupId>com.alibaba.otter</groupId>
            <artifactId>canal.client</artifactId>
        </dependency>
    </dependencies>
```

```properties
# 服务端口
server.port=1000
# 服务名
spring.application.name=canal-client

# 环境设置：dev、test、prod
spring.profiles.active=dev

# mysql数据库连接
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/guli?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false
spring.datasource.username=root
spring.datasource.password=350562
```

```java
@Component
public class CanalClient {
    //sql队列
    private Queue<String> SQL_QUEUE = new ConcurrentLinkedQueue<>();
    @Resource
    private DataSource dataSource;

    /**
     * canal入库方法
     */
    public void run() {
        CanalConnector connector = CanalConnectors.newSingleConnector(new InetSocketAddress("192.168.44.131",
                11111), "example", "", "");
        int batchSize = 1000;
        try {
            connector.connect();
            connector.subscribe(".*\\..*");
            connector.rollback();
            try {
                while (true) {
                    //尝试从master那边拉去数据batchSize条记录，有多少取多少
                    Message message = connector.getWithoutAck(batchSize);
                    long batchId = message.getId();
                    int size = message.getEntries().size();
                    if (batchId == -1 || size == 0) {
                        Thread.sleep(1000);
                    } else {
                        dataHandle(message.getEntries());
                    }
                    connector.ack(batchId);

                    //当队列里面堆积的sql大于一定数值的时候就模拟执行
                    if (SQL_QUEUE.size() >= 1) {
                        executeQueueSql();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        } finally {
            connector.disconnect();
        }
    }
    /**
     * 模拟执行队列里面的sql语句
     */
    public void executeQueueSql() {
        int size = SQL_QUEUE.size();
        for (int i = 0; i < size; i++) {
            String sql = SQL_QUEUE.poll();
            System.out.println("[sql]----> " + sql);
            this.execute(sql.toString());
        }
    }
    /**
     * 数据处理
     */
    private void dataHandle(List<Entry> entrys) throws InvalidProtocolBufferException {
        for (Entry entry : entrys) {
            if (EntryType.ROWDATA == entry.getEntryType()) {
                RowChange rowChange = RowChange.parseFrom(entry.getStoreValue());
                EventType eventType = rowChange.getEventType();
                if (eventType == EventType.DELETE) {
                    saveDeleteSql(entry);
                } else if (eventType == EventType.UPDATE) {
                    saveUpdateSql(entry);
                } else if (eventType == EventType.INSERT) {
                    saveInsertSql(entry);
                }
            }
        }
    }
    /**
     * 保存更新语句
     */
    private void saveUpdateSql(Entry entry) {
        try {
            RowChange rowChange = RowChange.parseFrom(entry.getStoreValue());
            List<RowData> rowDatasList = rowChange.getRowDatasList();
            for (RowData rowData : rowDatasList) {
                List<Column> newColumnList = rowData.getAfterColumnsList();
                StringBuffer sql = new StringBuffer("update " + entry.getHeader().getTableName() + " set ");
                for (int i = 0; i < newColumnList.size(); i++) {
                    sql.append(" " + newColumnList.get(i).getName()
                            + " = '" + newColumnList.get(i).getValue() + "'");
                    if (i != newColumnList.size() - 1) {
                        sql.append(",");
                    }
                }
                sql.append(" where ");
                List<Column> oldColumnList = rowData.getBeforeColumnsList();
                for (Column column : oldColumnList) {
                    if (column.getIsKey()) {
                        //暂时只支持单一主键
                        sql.append(column.getName() + "=" + column.getValue());
                        break;
                    }
                }
                SQL_QUEUE.add(sql.toString());
            }
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存删除语句
     */
    private void saveDeleteSql(Entry entry) {
        try {
            RowChange rowChange = RowChange.parseFrom(entry.getStoreValue());
            List<RowData> rowDatasList = rowChange.getRowDatasList();
            for (RowData rowData : rowDatasList) {
                List<Column> columnList = rowData.getBeforeColumnsList();
                StringBuffer sql = new StringBuffer("delete from " + entry.getHeader().getTableName() + " where ");
                for (Column column : columnList) {
                    if (column.getIsKey()) {
                        //暂时只支持单一主键
                        sql.append(column.getName() + "=" + column.getValue());
                        break;
                    }
                }
                SQL_QUEUE.add(sql.toString());
            }
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存插入语句
     */
    private void saveInsertSql(Entry entry) {
        try {
            RowChange rowChange = RowChange.parseFrom(entry.getStoreValue());
            List<RowData> rowDatasList = rowChange.getRowDatasList();
            for (RowData rowData : rowDatasList) {
                List<Column> columnList = rowData.getAfterColumnsList();
                StringBuffer sql = new StringBuffer("insert into " + entry.getHeader().getTableName() + " (");
                for (int i = 0; i < columnList.size(); i++) {
                    sql.append(columnList.get(i).getName());
                    if (i != columnList.size() - 1) {
                        sql.append(",");
                    }
                }
                sql.append(") VALUES (");
                for (int i = 0; i < columnList.size(); i++) {
                    sql.append("'" + columnList.get(i).getValue() + "'");
                    if (i != columnList.size() - 1) {
                        sql.append(",");
                    }
                }
                sql.append(")");
                SQL_QUEUE.add(sql.toString());
            }
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }
    /**
     * 入库
     */
    public void execute(String sql) {
        Connection con = null;
        try {
            if(null == sql) return;
            con = dataSource.getConnection();
            QueryRunner qr = new QueryRunner();
            int row = qr.execute(con, sql);
            System.out.println("update: "+ row);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DbUtils.closeQuietly(con);
        }
    }
}
```

```java
@SpringBootApplication
public class CanalApplication implements CommandLineRunner {
    @Resource
    private CanalClient canalClient;

    public static void main(String[] args) {
        SpringApplication.run(CanalApplication.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {
        //项目启动，执行canal客户端监听
        canalClient.run();
    }
}
```

实现效果:远程数据表添加一条数据,本地服务器也添加一条数据;修改和删除同理.



# Gateway

前面的请求转发使用的是nginx

gateway也能实现nginx相同的请求转发,负载均衡.还能实现权限控制等.

本项目的gateway配置见api_gateway模块.(没使用)

功能有路由转发,解决跨域,权限,异常处理. 不再需要CrossOrigin注解.



# 权限管理

acl表  未移植成功

## Spring Security权限框架

认证 授权

把登录 注册 请求修改为acl模块中的controller



# 打包运行

1.手动

mvn clean package

java -jar 运行

2.Jenkins ( linux )

需要jdk、maven、git、docker环境

镜像源配置:

到/root/.jenkins/updates下执行

```
sed -i ‘s/http://updates.jenkins-ci.org/download/https://mirrors.tuna.tsinghua.edu.cn/jenkins/g’ default.json && sed -i ‘s/http://www.google.com/https://www.baidu.com/g’ default.json
```

下载jenkins.war . 启动(nohup java -jar    ${  .war}>${ .out}&  重定向错误到标准输出) 

 http://id:8080  登录 安装推荐插件

Configure Global Security配置jdk、maven、git路径.



项目中加入Dockerfile文件

```sql
FROM openjdk:8-jdk=alpine
VOLUME /tmp
COPY ./target/demojenkins.jar demojenkins.jar
ENTRYPOINT ["java","-jar","/demojenkins.jar","&"]
```

修改pom打包类型jar,并加入插件spring-boot-maven-plugin



 新建item Freestyle project

设置把代码提交到git远程仓库的url,和git账户和密码

添加脚本docker.sh

```sh
#!/bin/bash
#maven打包
mvn clean package
echo 'package ok!'
echo 'build start!'
cd ./infrastructure/eureka_server
service_name="eureka-server"
service_prot=8761
#查看镜像id
IID=$(docker images | grep "$service_name" | awk '{print $3}')
echo "IID $IID"
if [ -n "$IID" ]
then
    echo "exist $SERVER_NAME image,IID=$IID"
    #删除镜像
    docker rmi -f $service_name
    echo "delete $SERVER_NAME image"
    #构建
    docker build -t $service_name .
    echo "build $SERVER_NAME image"
else
    echo "no exist $SERVER_NAME image,build docker"
    #构建
    docker build -t $service_name .
    echo "build $SERVER_NAME image"
fi
#查看容器id
CID=$(docker ps | grep "$SERVER_NAME" | awk '{print $1}')
echo "CID $CID"
if [ -n "$CID" ]
then
    echo "exist $SERVER_NAME container,CID=$CID"
    #停止
    docker stop $service_name
    #删除容器
    docker rm $service_name
else
    echo "no exist $SERVER_NAME container"
fi
#启动
docker run -d --name $service_name --net=host -p $service_prot:$service_prot $service_name
#查看启动日志
#docker logs -f  $service_name
```



构建作业(需要先启动docker):

buill now  

控制台输出 ->蓝色表示成功





# 总结问题

## 前端路由切换

多次路由跳转到同一个vue页面,页面中created方法只会执行一次,

解决方法:使用vue监听

```
created(){
	this.init();
}
watch:{
	$route(to,from) //路由变化方式 路由发生变化 方法就会执行
	this.init()
}
```



## ES6模块化问题

nodejs不能直接运行ES6模块化代码,需要使用Babel把ES6模块化代码转换ES5执行



# MybatisPlus生成的ID

mp生成的id值为19位,而js处理数字类型值得时候只会处理到16位.

解决方法,将id设置为字符串类型
