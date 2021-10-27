<template>
  <div class="app-container">
    <h2 style="text-align: center">发布新课程</h2>
    <el-steps
      :active="2"
      process-status="wait"
      align-center
      style="marginbottom: 40px"
    >
      <el-step title="填写课程基本信息" />
      <el-step title="创建课程大纲" />
      <el-step title="最终发布" />
    </el-steps>
					<!-- 可以直接@click="this.dialogChapterFormVisible = true"  不建议 -->
    <el-button type="text" @click="openChapterDialog()">添加章节</el-button>

    <!-- 章节 -->
    <ul class="chanpterList">
      <li v-for="chapter in chapterVideoList" :key="chapter.id">
        <p>
          {{ chapter.title }}
          <span class="acts">
            <el-button style="" type="text" @click="openVideo(chapter.id)"
              >添加小节</el-button
            >
            <el-button style="" type="text" @click="openEditChatper(chapter.id)"
              >编辑</el-button
            >
            <el-button type="text" @click="removeChapter(chapter.id)"
              >删除</el-button
            >
          </span>
        </p>
        <!-- 小节 -->
        <ul class="chanpterList videoList">
          <li v-for="video in chapter.children" :key="video.id">
            <p>
              {{ video.title }}
              <span class="acts">
                <el-button style="" type="text" @click="openEditVideo(video.id)"
                  >编辑</el-button
                >
                <el-button type="text" @click="removeVideo(video.id)"
                  >删除</el-button
                >
              </span>
            </p>
          </li>
        </ul>
      </li>
    </ul>

    <div>
      <el-button @click="previous">上一步</el-button>
      <el-button :disabled="saveBtnDisabled" type="primary" @click="next"
        >下一步</el-button
      >
    </div>

    <!-- 添加小节 对话框!!!  :visible.sync表示是否显示 由添加和修改小节按钮控制-->
    <el-dialog :visible.sync="dialogVideoFormVisible" title="添加课时">
      <el-form :model="video" label-width="120px">
        <el-form-item label="课时标题">
          <el-input v-model="video.title" />
        </el-form-item>
        <el-form-item label="课时排序">
          <el-input-number
            v-model="video.sort"
            :min="0"
            controls-position="right"
          />
        </el-form-item>
        <el-form-item label="是否免费">
          <el-radio-group v-model="video.free">
            <el-radio :label="true">免费</el-radio>
            <el-radio :label="false">收费</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="上传视频">
          <!-- 默认自动上传 
          on-remove 点击确认删除后
          before-remove 点击删除X-->
          <el-upload
            :on-success="handleVodUploadSuccess"
            :on-remove="handleVodRemove"     
            :before-remove="beforeVodRemove"
            :on-exceed="handleUploadExceed"
            :file-list="fileList"
            :action="BASE_API + '/eduvod/video/uploadVideo'"
            :limit="1"
            class="upload-demo"
          >
            <el-button size="small" type="primary">上传视频</el-button>
            <el-tooltip placement="right-end">
              <div slot="content">
                最大支持1G，<br />
                支持3GP、ASF、AVI、DAT、DV、FLV、F4V、<br />
                GIF、M2T、M4V、MJ2、MJPEG、MKV、MOV、MP4、<br />
                MPE、MPG、MPEG、MTS、OGG、QT、RM、RMVB、<br />
                SWF、TS、VOB、WMV、WEBM 等视频格式上传
              </div>
              <i class="el-icon-question" />
            </el-tooltip>
          </el-upload>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVideoFormVisible = false">取 消</el-button>
        <el-button
          :disabled="saveVideoBtnDisabled"
          type="primary"
          @click="saveOrUpdateVideo"
          >确 定</el-button
        >
      </div>
    </el-dialog>

    <!-- 添加和修改章节表单 对话框!!!  :visible.sync表示是否显示 由添加和修改章节按钮控制  -->
    <el-dialog :visible.sync="dialogChapterFormVisible" title="添加章节">
      <el-form :model="chapter" label-width="120px">
        <el-form-item label="章节标题">
          <el-input v-model="chapter.title" />
        </el-form-item>
        <el-form-item label="章节排序">
          <el-input-number
            v-model="chapter.sort"
            :min="0"
            controls-position="right"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogChapterFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="saveOrUpdate">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import chapter from "@/api/edu/chapter";
import video from "@/api/edu/video";
export default {
  data() {
    return {
      saveBtnDisabled: false,
      courseId: "", //课程id
      chapterVideoList: [],  //封装的章节与小节整体
      chapter: {
        //封装章节的属性 用于添加章节对话框
        title: "",
        courseId: "",
        sort: 0,
      },
      video: {
		  //封装小节的属性 用于添加章节对话框
        title: "",
        sort: 0,
        free: 0,
        videoSourceId: "",
        videoOriginalName: "", //视频名称
      },
      dialogChapterFormVisible: false, //章节弹框
      dialogVideoFormVisible: false, //小节弹框
      fileList: [], //上传文件列表
      BASE_API: process.env.BASE_API, //接口API地址
      saveVideoBtnDisabled: false,
    };
  },
  created() {
    //获取到路由的id值
    if (this.$route.params && this.$route.params.id) {
      this.courseId = this.$route.params.id;
      this.getChapterVideo();
    }
  },
  methods: {
    // ================================小节操作=================================
    //上传视频成功调用的方法
    handleVodUploadSuccess(response, file, fileList) {
      //上传之后的视频id进行赋值
      this.video.videoSourceId = response.data.videoId;
      //上传视频的名称   file表示上传的文件
      alert(file.name);
      this.video.videoOriginalName = file.name;
    },
    handleVodRemove(){  //在确认删除后 删除视频 在点播系统里同步
      video.deleteAliVideo(this.video.videoSourceId)
        .then(response =>{
          this.$message({
            type:'success',
            message:'删除视频成功'
          })
          //因为限制了一个  所以直接清空
          this.fileList=[]
          //删除数据库里的video信息
          this.video.videoSourceId=''
          this.video.videoOriginalName=''
        })
    },
    beforeVodRemove(file,fileList){  //在点击X后处理
      return this.$confirm("确认移除 ${fina.name}?");
    },
    //上传之前调用的方法  因为限制了上传1个文件
    handleUploadExceed(files, fileList) {
      this.$message.warning("想要重新上传视频，请先删除已上传的视频");
    },

    //修改小节的方法
    updateVideo() {
      //设置课程id
      this.video.courseId = this.courseId;
      video.updateVideo(this.video).then((response) => {
        this.dialogVideoFormVisible = false; //关闭弹窗
        //提示信息
        this.$message({
          type: "success",
          message: "修改小节信息成功!",
        });
        //刷新页面
        this.getChapterVideo();
        //清空数据
        this.video={},
        this.fileList=[]
      });
    },

    //修改小节弹框回显的操作
    openEditVideo(id) {
      //显示弹框
      this.openVideo();
      //回显数据
      video.getVideoInfo(id).then((response) => {
        this.video = response.data.video;
        //难点  实现了 文件上传列表的数据回显!!! 并能保证删除!!!
        // this.fileList[0]=response.data.video.videoOriginalName
        this.fileList.push({name:response.data.video.videoOriginalName,url:""}) 
      });
    },

    //删除小节
    removeVideo(id) {
      this.$confirm("此操作将永久删除小节信息, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }).then(() => {
        //调用删除的方法
        video.deleteVideo(id).then((response) => {
          //删除成功
          //提示信息
          this.$message({
            type: "success",
            message: "删除成功!",
          });
          //刷新页面
          this.getChapterVideo();
        });
      });
    },
    //小节弹窗的方法
    openVideo(chapterId) {
      //清空表单的值
      this.video = {};
      //打开弹窗
      this.dialogVideoFormVisible = true;
      //设置章节id
      this.video.chapterId = chapterId;
    },
    //添加小节
    addVideo() {
      //设置课程id
      this.video.courseId = this.courseId;
      video.addVideo(this.video).then((response) => {
        this.dialogVideoFormVisible = false; //关闭弹窗
        //提示信息
        this.$message({
          type: "success",
          message: "添加小节信息成功!",
        });
        //刷新页面
        this.getChapterVideo();
        //清空数据
        this.video={}
        this.fileList=[]
      });
    },
    saveOrUpdateVideo() {
      if (!this.video.id) {
        this.addVideo();
      } else {
        this.updateVideo();
      }
    },
    // ================================章节操作=================================
    //删除章节的方法
    removeChapter(chapterId) {
      this.$confirm("此操作将永久删除章节信息, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }).then(() => {
        //调用删除的方法
        chapter.deleteChapter(chapterId).then((response) => {
          //删除成功
          //提示信息
          this.$message({
            type: "success",
            message: "删除成功!",
          });
          //刷新页面
          this.getChapterVideo();
        });
      });
    },
    //修改章节弹框数据回显
    openEditChatper(chapterId) {
      //显示弹框
    //   this.openChapterDialog();
		this.dialogChapterFormVisible=true
      chapter.getChapter(chapterId).then((response) => {
        this.chapter = response.data.chapter;
      });
    },
    //弹出添加章节页面弹框
    openChapterDialog() {
      this.dialogChapterFormVisible = true;
      //表单数据做清空  防止再次添加章节时数据为以前的  TODO 添加在这里也有问题 应该添加到章节添加成功之后
      this.chapter = {};
    },
    //添加章节的方法
    addChapter() {
      //在添加前设置课程id到chapter对象中 , 因为添加对话框中没有输入绑定课程id
      this.chapter.courseId = this.courseId;
      chapter.addChapter(this.chapter).then((response) => {
        this.dialogChapterFormVisible = false; //关闭弹窗
        //提示信息
        this.$message({
          type: "success",
          message: "添加章节信息成功!",
        });
        //刷新页面
        this.getChapterVideo();
      });
    },

    //修改章节的方法
    updateChapter() {
      //设置课程id到chapter对象中
      this.chapter.courseId = this.courseId;
      chapter.updateChapter(this.chapter).then((response) => {
        this.dialogChapterFormVisible = false; //关闭弹窗
        //提示信息
        this.$message({
          type: "success",
          message: "修改章节信息成功!",
        });
        //刷新页面
        this.getChapterVideo();
      });
    },

    saveOrUpdate() {
      //判断chapter是否有id值没有就做新增反之做修改
      if (!this.chapter.id) {
        this.addChapter();
      } else {
        this.updateChapter();
      }
    },

    //根据课程id查询章节和小节列表
    getChapterVideo() {
      chapter.getAllChapterVidev(this.courseId).then((response) => {
        this.chapterVideoList = response.data.allChapterVideo;
      });
    },

    previous() {
      //上一步
      this.$router.push({
        path: "/course/info/" + this.courseId,
      });
    },
    next() {
      //跳转到第二步
      this.$router.push({
        path: "/course/publish/" + this.courseId,
      });
    },
  },
};
</script>

<style scoped>
.chanpterList {
  position: relative;
  list-style: none;
  margin: 0;
  padding: 0;
}
.chanpterList li {
  position: relative;
}
.chanpterList p {
  float: left;
  font-size: 20px;
  margin: 10px 0;
  padding: 10px;
  height: 70px;
  line-height: 50px;
  width: 100%;
  border: 1px solid #ddd;
}
.chanpterList .acts {
  float: right;
  font-size: 14px;
}

.videoList {
  padding-left: 50px;
}
.videoList p {
  float: left;
  font-size: 14px;
  margin: 10px 0;
  padding: 10px;
  height: 50px;
  line-height: 30px;
  width: 100%;
  border: 1px dotted #ddd;
}
</style>