import request from '@/utils/request'

export default {
	//分页查询评论
	getCommentList(current,size,courseId) {
		return request({
			url: `/eduservice/comment/getCommentList/${current}/${size}/${courseId}`,
			method: 'get'
		})
	},
	//添加评论
	addComment(comment){
		return request({
			url: `/eduservice/comment/saveComment`,
			method: 'post',
			data:comment
		})
	}
}