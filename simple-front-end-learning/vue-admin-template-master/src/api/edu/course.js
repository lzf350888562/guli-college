import request from '@/utils/request'  //固定  request里面封装了axios的方法

export default{
    //添加课程信息
    addCourseInfo(courseInfo){
        return request({
            url:'/eduservice/course/addCourseInfo',
            method:'post',
            data:courseInfo
        })
    },
    //2 查询所有讲师
	getListTeacher() {
        return request({
            url: `/eduservice/teacher/findAll`,
            method: 'GET'
          })
	},
	
	//3 根据课程id查询课程基本信息
	getCourseInfo(id) {
	    return request({
	        url: `/eduservice/course/getCourseInfo/${id}`,
	        method: 'GET'
	      })
	},
	
	//4 修改课程信息
	updateCourseInfo(courseInfoVo) {
	    return request({
	        url: `/eduservice/course/updateCourseInfo`,
	        method: 'post',
			data: courseInfoVo
	      })
	},
	
	//课程确认信息显示
	getPublishCourseInfo(id) {
		return request({
		    url: `/eduservice/course/getPublishCourseInfo/${id}`,
		    method: 'GET'
		  })
	},
	
	//课程信息最终发布
	publishCourse(id) {
		return request({
		    url: `/eduservice/course/publishCourse/${id}`,
		    method: 'POST'
		  })
	},
	
	//查询所有的课程列表
	getCourseListPage(page,limit,eduCourse) {
		return request({
		    url: `/eduservice/course/${page}/${limit}`,
		    method: 'POST',
			data: eduCourse
		  })
	},
	
	//根据课程id删除课程信息
	removeCourse(id){
		return request({
		    url: `/eduservice/course/${id}`,
		    method: 'DELETE'
		  })
	},
}