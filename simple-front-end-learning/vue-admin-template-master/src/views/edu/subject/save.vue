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
      fileUploadError(){
        this.loading =false
         this.$message({
          type:'error',
          message:'添加课程分类失败'
        })
      }
      
  },
};
</script>