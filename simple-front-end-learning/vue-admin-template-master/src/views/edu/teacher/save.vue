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
        <!-- 讲师头像 固定格式-->
        <el-form-item label="讲师头像" label-heigh>
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
                :width="100" 
                :height="100"
                :key="imagecropperKey"
                :url="BASE_API+'/eduoss/fileoss'"
                field="file"
                @close="close"
                @crop-upload-success="cropSuccess">
            </image-cropper>
        </el-form-item>

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
import teacherApi from "@/api/edu/teacher"
import ImageCropper from '@/components/ImageCropper'
import PanThumb from '@/components/PanThumb'

export default {
  components: {
      ImageCropper,PanThumb
  },
  data() {
    return {
        teacher:{
            // 如list.vue一样 这些属性都可以省略
            name:'',
            sort:0,
            level:1,
            carrer:'',
            intro:'',
            avatar:''  //可添加默认头像url
        },
        imagecropperShow:false, //上传弹窗组件是否显示
        imagecropperKey:0,      //上传组件唯一标识
        BASE_API: process.env.BASE_API, //获取配置文件接口api地址 固定写法
        saveBtnDisabled:false   //是否禁用重复提交
    };
  },
  created() { //判断是否为修改
     this.init();
  },
  watch:{ //vue监听
    $route(to,from){ //路由变化方式  当路由发生变化时执行
        this.init()
   }
  },
  methods: {
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
      saveTeacher(teacher){
          teacherApi.addTeacher(this.teacher)
            .then(response =>{//添加成功
                //提示信息
                this.$message({
                    type:'success',
                    message:'添加成功'
                });
                //回到列表页面  路由跳转!!!  固定格式
                this.$router.push({path:'/teacher/table'})
            })
      }
  },
};
</script>