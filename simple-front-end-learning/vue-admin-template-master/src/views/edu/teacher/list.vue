<template>
  <!--template固定-->
  <div class="app-container">
    讲师列表
    <el-form :inline="true" class="demo-form-inline">
      <el-form-item>
        <el-input v-model="teacherQuery.name" placeholder="讲师名"></el-input>
      </el-form-item>
      <el-form-item>
        <el-select
          v-model="teacherQuery.level"
          clearable
          placeholder="讲师头衔"
        >
        <!-- 数据类型要与取出的json一致,所以双向绑定数字 -->
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
      <el-button type="primary" icon="el-icon-search" @click="getList()"
        >查询</el-button
      >
      <el-button type="default" @click="resetData()">清空</el-button>
    </el-form>
    <!-- 显示讲师列表 :data单向绑定 -->
    <el-table
      :data="list"
      element-loading-text="数据加载中"
      border
      fit
      highlight-current-row
    >
      <el-table-column label="序号" width="70" align="center">
        <!-- scope表示整个表格 -->
        <template slot-scope="scope">
          {{ (page - 1) * limit + scope.$index + 1 }}
        </template>
      </el-table-column>
      <el-table-column prop="name" label="名称" width="80"></el-table-column>
      <el-table-column label="头衔" width="80">
        <template slot-scope="scope">
          <!-- 三个==判断值与类型 -->
          {{ scope.row.level === 1 ? "高级讲师" : "首席讲师" }}
        </template>
      </el-table-column>
      <el-table-column prop="intro" label="资历"></el-table-column>
      <el-table-column
        prop="gmtCreate"
        label="添加时间"
        width="160"
      ></el-table-column>
      <el-table-column prop="sort" label="排序" width="60"></el-table-column>
      <el-table-column label="操作" width="200" align="center">
        <template slot-scope="scope">
          <router-link :to="'/teacher/edit/'+scope.row.id">
            <el-button type="primary" size="mini" icon="el-icon-edit"
              >修改</el-button
            >
          </router-link>
          <el-button
            type="danger"
            size="mini"
            icon="el-icon-delete"
            @click="removeDataById(scope.row.id)"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <!--分页  @为v-on的简写表示绑定事件 -->
    <el-pagination
      :current-page="page"
      :page-size="limit"
      :total="total"
      @current-change="getList"
      layout="total, prev, pager, next, jumper"
      style="padding: 30px 0; text-align: center"
    >
    </el-pagination>
  </div>
</template>
<script>
//引入teacher.js文件
import teacher from "@/api/edu/teacher";
export default {
  //核心代码位置 不需要new Vue了 在main中已经完成.
  // data:{
  //},
  data() {
    //定义变量和初始值
    return {
      list: null, //查询之后接口返回的集合
      page: 1, //当前页
      limit: 10, //每页记录数
      total: 0, //总记录数
      teacherQuery: {}, //查询条件 可以不指定属性,条件查询传值时会自动创建属性,默认没条件
    };
  },
  created() {
    //页面渲染之前执行,一般调用methods定义的方法
    this.getList(); 
  },
  methods: {
    //创建具体的方法,调用teacher.js定义的方法
    //讲师列表的方法
    getList(page = 1) {
      this.page = page;
      // axios.post("")已被封装
      teacher
        .getTeacherListPage(this.page, this.limit, this.teacherQuery)
        .then((response) => {
          // console.log(response) 可用来获取数据路径
          this.list = response.data.rows;
          this.total = response.data.total;
          console.log(this.list);
          console.log(this.total);
        }) //请求成功
        .catch((error) => {
          console.log(error);
        }); //请求失败
    },
    resetData() {
      //清空的方法
      //清空输入项数据
      this.teacherQuery = {};
      //查询所有讲师数据
      this.getList();
    },
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
           .then((response) => {        
          //删除成功 提示信息  
          this.$message({
            type: "success",
            message: "删除成功!",
          });
          this.getList(); //刷新页面
        });//确认取消 不catch
      });//删除失败 不catch  并且  request.js封装了对错误响应的处理
    },
  },
};
</script>