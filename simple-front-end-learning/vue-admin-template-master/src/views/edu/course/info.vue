<template>
  <div class="app-container">

    <h2 style="text-align: center;">发布新课程</h2>

    <el-steps :active="1" process-status="wait" align-center style="margin-bottom: 40px;">
      <el-step title="填写课程基本信息"/>
      <el-step title="创建课程大纲"/>
      <el-step title="最终发布"/>
    </el-steps>

    <el-form label-width="120px">

      <el-form-item label="课程标题">
        <el-input v-if="courseInfo" v-model="courseInfo.title" placeholder=" 示例：机器学习项目课：从基础到搭建项目视频课程。专业名称注意大小写"/>
      </el-form-item>
	
      <!-- 所属分类 TODO -->
     <el-form-item label="课程分类">
        <el-select
          v-if="subjectOneList"
		  v-model="courseInfo.subjectParentId"
          placeholder="一级分类"
          @change="subjectLevelOneChanged">

          <el-option
            v-for="subject in subjectOneList"
            :key="subject.id"
            :label="subject.title"
            :value="subject.id"/>

        </el-select>

        <!-- 二级分类 -->
      <el-select  v-model="courseInfo.subjectId" placeholder="二级分类">
          <el-option
            v-for="subject in subjectTwoList"
            :key="subject.id"
            :label="subject.title"
            :value="subject.id"/>
        </el-select>
      </el-form-item>

      <!-- 课程讲师 TODO -->
      <!-- 课程讲师 -->
     <el-form-item label="课程讲师" v-if="teacherList && teacherList.length">
        <el-select v-model="courseInfo.teacherId" placeholder="请选择">
          <el-option v-for="teacher in teacherList" :key="teacher.id" :label="teacher.name" :value="teacher.id"/>
        </el-select>
      </el-form-item>

      <el-form-item label="总课时">
        <el-input-number :min="0" v-model="courseInfo.lessonNum" controls-position="right" placeholder="请填写课程的总课时数"/>
      </el-form-item>

      <!-- 课程简介 TODO -->
      <!-- 课程简介-->
     <el-form-item label="课程简介">
       <tinymce :height="300" v-model="courseInfo.description"/>
      </el-form-item>

      <!-- 课程封面 TODO -->
      <!-- 课程封面-->
     <el-form-item label="课程封面">

        <el-upload
          :show-file-list="false"
          :on-success="handleAvatarSuccess"
          :before-upload="beforeAvatarUpload"
          :action="BASE_API+'/eduoss/fileoss'"
          class="avatar-uploader">
          <img :src="imgurl">
        </el-upload>

      </el-form-item>

      <el-form-item label="课程价格">
        <el-input-number :min="0" v-model="courseInfo.price" controls-position="right" placeholder="免费课程请设置为0元"/> 元
      </el-form-item>

      <el-form-item>
        <el-button :disabled="saveBtnDisabled" type="primary" @click="saveOrUpdate">保存并下一步</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import course from "@/api/edu/course";
import subject from "@/api/edu/subject";
import Tinymce from "@/components/Tinymce"; //引入组件
export default {
  //声明组件
  components: { Tinymce },
  data() {
    return {
      saveBtnDisabled: false, // 保存按钮是否禁用
	  imgurl:'/static/guangtouqiang.jpg',
      courseInfo: {
        //提交给后端的课程属性  添加无id  修改有id
        title: "",
        subjectId: "", //二级分类id
        subjectParentId: "", //一级分类id
        teacherId: "",
        lessonNum: 0,
        description: "",
		//这里属性渲染出现bug 初始化失败 所以用中间变量imgurl
        cover: '/static/guangtouqiang.jpg', //默认封面图片
        price: 0
      },
      courseId: "",
      BASE_API: process.env.BASE_API, //接口地址
      teacherList: [], //封装所有的讲师信息
      subjectOneList: [], //一级分类列表
      subjectTwoList: [], //二级分类列表
    };
  },
  created() {
    this.init();
  },
  watch: {
    //监听路由
    $route(to, from) {
      //路由变化的方式，路由发生变化这个方法就会执行
      this.init();
    },
  },

  methods: {
    init() {
      //页面一加载就会执行这个方法
      //获取到路由的id值
      if (this.$route.params && this.$route.params.id) {
        this.courseId = this.$route.params.id;
        //调用根据课程id查询课程信息的方法
        this.getCourseInfo();
      } else {
        //清空数据
        this.courseInfo = {};
        //初始化所有讲师
        this.getListTeacher();
        //初始化一级分类
        this.getOneSubject();
      }
    },

    //根据课程id查询课程信息
    //为了让带id的访问此界面时能数据回显并且当前一级分类和二级分类列表有内容
    getCourseInfo() {
      //在courseInfo课程基本信息中。包含一级分类id和二级分类id
      course.getCourseInfo(this.courseId).then((response) => {
        //数据回显 因为进行了双向绑定
        this.courseInfo = response.data.courseInfoVo;
        this.imgurl = response.data.courseInfoVo.cover;
        //1 先查询所有的分类
        subject.getSubjectList().then((response) => {
          //2 获取所有一级分类
          this.subjectOneList = response.data.list;
          //3 把所有的一级分类数组进行遍历
          for (var i = 0; i < this.subjectOneList.length; i++) {
            //获取每个一级分类
            var oneSubject = this.subjectOneList[i];
            //比较当前 courseInfo里面一级分类id和所有的一级分类id
            if (this.courseInfo.subjectParentId == oneSubject.id) {
              //获取一级分类中的二级分类
              this.subjectTwoList = oneSubject.children;
            }
          }
        });
        //初始化所有讲师
        this.getListTeacher();
      });
    },

    //上传封面成功调用的方法
    handleAvatarSuccess(res, file) {
	    this.imgurl = res.data.url;
      this.courseInfo.cover = res.data.url;
	    console.log('上传封面成功'+this.courseInfo.cover)	
	    this.$message({
                    type:'success',
                    message:'上传成功'
                });
    },
    //上传封面之前调用的方法
    beforeAvatarUpload(file) {
		console.log('上传封面前'+this.courseInfo.cover)	
      const isJPG = file.type === "image/jpeg";
      const isLt2M = file.size / 1024 / 1024 < 2;

      if (!isJPG) {
        this.$message.error("上传头像图片只能是 JPG 格式!");
      }
      if (!isLt2M) {
        this.$message.error("上传头像图片大小不能超过 2MB!");
      }
      return isJPG && isLt2M;
      //   return isLt2M;
    },

    //点击某个一级分类，触发change，显示对应的二级分类
    subjectLevelOneChanged(value) {
      //value就是一级分类的id值
      //遍历所有的分类，包括一级和二级
      for (var i = 0; i < this.subjectOneList.length; i++) {
        //每个一级分类
        var oneSubject = this.subjectOneList[i];
        //判断：所有的一级分类id和点击的一级分类id是否一致
        if (value === oneSubject.id) {
          //从一级分类获取里面的所有二级分类
          this.subjectTwoList = oneSubject.children;
		  break
        }
          //把二级分类id值清空  否则在更换一级分类后二级分类内容不变化 应该在for循环外面
          this.courseInfo.subjectId = "";
      }
    },

    //查询所有的一级分类
    getOneSubject() {
      subject.getSubjectList().then((response) => {
        this.subjectOneList = response.data.list;
      });
    },

    //查询所有的讲师
    getListTeacher() {
      course.getListTeacher().then((response) => {
        this.teacherList = response.data.items;
      });
    },

    // 添加课程的方法
    addCourse() {
      course.addCourseInfo(this.courseInfo).then((response) => {
        //提示信息
        this.$message({
          type: "success",
          message: "添加课程信息成功!",
        });
        //跳转到第二步
        this.$router.push({
          path: "/course/chapter/" + response.data.courseId,
        });
      });
    },

    //修改课程的方法
    updateCourse() {
      course.updateCourseInfo(this.courseInfo).then((response) => {
        //提示信息
        this.$message({
          type: "success",
          message: "修改课程信息成功!",
        });
        //跳转到第二步
        this.$router.push({ path: "/course/chapter/" + this.courseId });
      });
    },
    //判断是修改还是新增
    saveOrUpdate() {
      //获取到路由的id值
      if (this.courseInfo.id) {
        this.updateCourse();
      } else {
        this.addCourse();
      }
    },
  },
};
</script>

<style scoped>
.tinymce-container {
  line-height: 29px;
}

</style>