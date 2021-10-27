<template>
    <div id="aCoursesList" class="bg-fa of">
        <!-- /课程列表 开始 -->
        <section class="container">
            <header class="comm-title">
                <h2 class="fl tac">
                    <span class="c-333">全部课程</span>
                </h2>
            </header>
            <section class="c-sort-box">
                <section class="c-s-dl">
                    <dl>
                        <dt>
                            <span class="c-999 fsize14">课程类别</span>
                        </dt>
                        <dd class="c-s-dl-li">
                            <ul class="clearfix">
                                <li>
                                    <a title="全部" href="#" @click="searchAll()">全部</a>
                                </li>
                                <!-- 一级分类 点击显示二级分类     class表示如果相等就使用active样式-->
                                <li v-for="(item,index) in subjectNestedList" :key="index" :class="{active:oneIndex===index}">
                                    <a :title="item.title" href="#" @click="searchSubjectList(item.id,index)">{{item.title}}</a>
                                </li>
                            </ul>
                        </dd>
                    </dl>
                    <dl>
                        <dt>
                            <span class="c-999 fsize14"></span>
                        </dt>
                        <dd class="c-s-dl-li">
                            <ul class="clearfix">
                                 <!-- 二级分类 -->
                                <li v-for="(item,index) in subSubjectList" :key="index" :class="{active:twoIndex === index}">
                                    <a :title="item.title" href="#" @click="searchTwo(item.id,index)">{{item.title}}</a>
                                </li>
                            </ul>
                        </dd>
                    </dl>
                    <div class="clear"></div>
                </section>
                <div class="js-wrap">
                    <section class="fr">
            <span class="c-ccc">
              <i class="c-master f-fM">1</i>/
              <i class="c-666 f-fM">1</i>
            </span>
                    </section>
                    <section class="fl">
                        <ol class="js-tap clearfix">
                            <li :class="{'current bg-orange':buyCountSort!==''}">
                                <a title="销量" href="javascript:void(0);" @click="searchBuyCount()">销量
                                    <span :class="{hide:buyCountSort===''}">↓</span>
                                </a>
                            </li>
                            <li :class="{'current bg-orange':gmtCreateSort!==''}">
                                <a title="最新" href="javascript:void(0);" @click="searchGmtCreate()">最新
                                    <span :class="{hide:gmtCreateSort===''}">↓</span>
                                </a>
                            </li>
                            <li :class="{'current bg-orange':priceSort!==''}">
                                <a title="价格" href="javascript:void(0);" @click="searchPrice()">价格&nbsp;
                                    <span :class="{hide:priceSort===''}">↓</span>
                                </a>
                            </li>
                        </ol>
                    </section>
                </div>
                <div class="mt40">
                    <!-- /无数据提示 开始-->
                    <section v-if="data.total === 0" class="no-data-wrap">
                        <em class="icon30 no-data-ico">&nbsp;</em>
                        <span class="c-666 fsize14 ml10 vam">没有相关数据，小编正在努力整理中...</span>
                    </section>
                    <!-- /无数据提示 结束-->
                    <article v-if="data.total>0" class="comm-course-list">
                        <ul class="of" id="bna">
                            <li v-for="item in data.items" :key="item.id">
                                <div class="cc-l-wrap">
                                    <section class="course-img">
                                        <!-- 去掉样式class="img-responsive" 添加固定高度防止封面图片大小不一致 -->
                                        <img height="150px" :src="item.cover" :alt="item.title">
                                        <div class="cc-mask">
                                            <a :href="'/course/'+item.id" :title="item.title" class="comm-btn c-btn-1">开始学习</a>
                                        </div>
                                    </section>
                                    <h3 class="hLh30 txtOf mt10">
                                        <a :href="'/course/'+item.id" :title="item.title" class="course-title fsize18 c-333">{{item.title}}</a>
                                    </h3>
                                    <section class="mt10 hLh20 of">
                                        <span class="fr jgTag bg-green" v-if="Number(item.price) === 0">
                                          <i class="c-fff fsize12 f-fA" >免费</i>
                                        </span>
                                                            <span class="fl jgAttr c-ccc f-fA">
                                          <i class="c-999 f-fA">{{item.buyCount}}人学习</i>
                                          |
                                          <i class="c-999 f-fA">{{item.viewCount}}人浏览</i>
                                        </span>
                                    </section>
                                </div>
                            </li>

                        </ul>
                        <div class="clear"></div>
                    </article>
                </div>
                <!-- 公共分页 开始 -->
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
                            :class="{current: data.current === page, undisable: data.current === page}"
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
                <!-- 公共分页 结束 -->
            </section>
        </section>
        <!-- /课程列表 结束 -->
    </div>
</template>
<script>
	import courseApi from "@/api/course";

	export default {
		data(){
			return{
				current:1, //当前页
				data:{},  //课程列表
				subjectNestedList: [], // 一级分类列表
				subSubjectList: [], // 二级分类列表

				searchObj: {}, // 查询表单对象

				oneIndex:-1,
				twoIndex:-1,
				buyCountSort:"",
				gmtCreateSort:"",
				priceSort:""
            }
        },
        created() {
			this.initCourseFirst()   //课程初始化
            this.initOneSubject()   //一级分类初始化
		},
        methods:{
			//初始化课程列表
			initCourseFirst(){
				courseApi.getCourseList(1,8,this.searchObj).then(response=>{
					this.data = response.data.data
                })
            },
            //初始化一级分类列表
            initOneSubject(){
				courseApi.getAllSubject().then(response=>{
					this.subjectNestedList = response.data.data.list
                })
            },
            //分页切换
            gotoPage(current){
				courseApi.getCourseList(current,8,this.searchObj).then(response=>{
					this.data = response.data.data
				})
            },
            //点击一级分类查询对应的二级分类
			searchSubjectList(subjectParentId,index){
				// 添加样式
				this.oneIndex = index;
				this.twoIndex = -1
                this.searchObj.subjectId = ''
                this.subSubjectList = []
				//遍历一级分类列表
				for(let i=0;i<this.subjectNestedList.length;i++){
                    var oneSubject = this.subjectNestedList[i]
                    if(subjectParentId == oneSubject.in){
                        this.subSubjectList = oneSubject.children
                    }
                }
            },
			searchTwo(subjectId,index){
				// 添加样式
				this.twoIndex = index
				this.searchObj.subjectId = subjectId
                this.gotoPage(1)
            },
            //全部
            searchAll(){
				this.searchObj = {}
				this.oneIndex = -1;
				this.twoIndex = -1

				this.subSubjectList = []
				this.gotoPage(1)
            },
			//6 根据销量排序
			searchBuyCount() {
				//设置对应变量值，为了样式生效
				this.buyCountSort = "1"
				this.gmtCreateSort = ""
				this.priceSort = ""

				//把值赋值到searchObj
				this.searchObj.buyCountSort = this.buyCountSort
				this.searchObj.gmtCreateSort = this.gmtCreateSort;
				this.searchObj.priceSort = this.priceSort;

				//调用方法查询
				this.gotoPage(1)
			},

			//7 最新排序
			searchGmtCreate() {
				//设置对应变量值，为了样式生效
				this.buyCountSort = ""
				this.gmtCreateSort = "1"
				this.priceSort = ""

				//把值赋值到searchObj
				this.searchObj.buyCountSort = this.buyCountSort
				this.searchObj.gmtCreateSort = this.gmtCreateSort;
				this.searchObj.priceSort = this.priceSort;

				//调用方法查询
				this.gotoPage(1)
			},

			//8 价格排序
			searchPrice() {
				//设置对应变量值，为了样式生效
				this.buyCountSort = ""
				this.gmtCreateSort = ""
				this.priceSort = "1"

				//把值赋值到searchObj
				this.searchObj.buyCountSort = this.buyCountSort
				this.searchObj.gmtCreateSort = this.gmtCreateSort;
				this.searchObj.priceSort = this.priceSort;

				//调用方法查询
				this.gotoPage(1)
			}

		}


	};
</script>

<style scoped>
    .active {
        background: #bdbdbd;
    }
    .hide {
        display: none;
    }
    .show {
        display: block;
    }
</style>