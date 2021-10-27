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
        title:{
           text:'数据统计'
        },
        tooltip:{//x坐标轴触发提示 光标移动高上面 有提示
            trigger:'axis'
        },
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