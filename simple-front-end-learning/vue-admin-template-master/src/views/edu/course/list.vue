<template>
  <div class="app-container">
    <!--查询表单-->
    <el-form :inline="true" class="demo-form-inline">
      <el-form-item>
        <el-input v-model="eduCourse.title" placeholder="课程名称" />
      </el-form-item>

      <el-form-item>
        <el-select v-model="eduCourse.status" clearable placeholder="课程状态">
          <el-option value="Normal" label="已发布" />
          <el-option value="Draft" label="未发布" />
        </el-select>
      </el-form-item>

      <el-button type="primary" icon="el-icon-search" @click="getAllCourse()">查询</el-button>
      <el-button type="default" @click="resetData()">清空</el-button>
    </el-form>

    <!-- 表格 -->
    <el-table :data="courseList"  border fit highlight-current-row>
      <el-table-column label="序号" width="70" align="center">
        <template slot-scope="scope">{{ (page - 1) * limit + scope.$index + 1 }}</template>
      </el-table-column>

      <el-table-column prop="title" label="课程名称" />

      <el-table-column label="是否已发布" width="100">
        <template slot-scope="scope">{{ scope.row.status==="Normal"?'已发布':'未发布' }}</template>
      </el-table-column>

      <el-table-column prop="viewCount" label="浏览量" width="70" />

      <el-table-column prop="gmtCreate" label="添加时间" />

      <el-table-column prop="lessonNum" label="课时" width="60" />
      <el-table-column label="操作" width="200"  align="center">
        <template v-if="scope.row.id" slot-scope="scope">
          <router-link :to="'/course/info/'+scope.row.id">
            <el-button type="primary" size="mini" icon="el-icon-edit">编辑课程基本信息</el-button>
          </router-link>
          <router-link :to="'/course/chapter/'+scope.row.id">
            <el-button type="primary" size="mini" icon="el-icon-edit">编辑课程大纲息</el-button>
          </router-link>
          <el-button
            type="danger"
            size="mini"
            icon="el-icon-delete"
            @click="removeDataById(scope.row.id)"
          >删除课程信息</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
      :current-page="page"
      :page-size="limit"
      :total="total"
      style="padding: 60px 0; text-align: center;"
      layout="total, prev, pager, next, jumper"
      @current-change="getAllCourse"
    />
  </div>
</template>

<script>
import course from "@/api/edu/course";
export default {
  //核心代码的位置
  data() {
    //定义变量和初始值
    return {
      //这些值名字可以随意取
      courseList: [], //查询之后接口返回的结果集合
      page: 1,
      limit: 4,
      total: 10,
      eduCourse: {} //放查询条件,由查询搜索框内容双向绑定
    };
  },
  created() {
    //页面渲染之前执行，一般调用methods定义的方法
    this.getAllCourse();
  },
  methods: {
    //创建具体的方法，调用teacher.js定义的方法
    getAllCourse(page = 1) {
      this.page = page; //方法已经封装，前端点击page页码，这里能够拿到更新的page页码
      course
        .getCourseListPage(this.page, this.limit, this.eduCourse)
        .then(response => {
          this.courseList = response.data.courseList;
          this.total = response.data.total;
        })
        .catch(error => {
          //请求失败
          console.log(error);
        });
    },
    //清空查询条件
    resetData() {
      this.eduCourse = {};
      this.getAllCourse();
    },
    removeDataById(courseId) {
      //删除课程的方法
      this.$confirm("此操作将永久删除该课程, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        course.removeCourse(courseId).then(response => {
          //删除成功
          //提示信息
          this.$message({
            type: "success",
            message: "删除成功!"
          });
          //回到刷新后的页面
          this.getAllCourse();
        });
      });
    },
  }
};
</script>
<style>
</style>