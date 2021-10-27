import request from '@/utils/request'

export default {
	//分页查询
	getCourseList(current,size,searchObj) {
		return request({
			url: `/eduservice/coursefront/getFrontCourseList/${current}/${size}`,
			method: 'post',
			data:searchObj
		})
	},
	//查询所有分类
	getAllSubject(){
		return request({
			url: `/eduservice/subject/getAllSubject`,
			method: 'get',
		})
	},
	//分页查询
	getCourseInfo(id) {
		return request({
			url: `/eduservice/coursefront/getFrontCourseInfo/${id}`,
			method: 'get'
		})
	}
}