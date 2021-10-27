import request from '@/utils/request'

export default {
	//分页查询
	getTeacherList(current,size) {
		return request({
			url: `/eduservice/teacherfront/getTeacherFrontList/${current}/${size}`,
			method: 'post'
		})
	},
	//讲师详情
	getTeacherInfo(id) {
		return request({
			url: `/eduservice/teacherfront/getTeacherFrontInfo/${id}`,
			method: 'get'
		})
	}
}