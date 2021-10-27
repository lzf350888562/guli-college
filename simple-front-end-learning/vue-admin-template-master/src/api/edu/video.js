import request from '@/utils/request'
export default {
	//添加小节信息
	addVideo(video) {
		return request({
			url: '/eduservice/video/addVideo',
			method: 'POST',
			data: video
		})
	},
	//根据id查询小节信息
	getVideoInfo(id) {
		return request({
			url: '/eduservice/video/getVideoInfo/' +id ,
			method: 'GET'
		})
	},
	//修改小节信息
	updateVideo(video) {
		return request({
			url: '/eduservice/video/updateVideo',
			method: 'post',
			data: video
		})
	},
	//根据id删除小节信息
	deleteVideo(id) {
		return request({
			url: '/eduservice/video/'+id,
			method: 'DELETE'
		})
	},
	//根据id删除小节的视频
	deleteAliVideo(id) {
		return request({
			url: '/eduvod/video/removeVideo/'+id,
			method: 'DELETE'
		})
	}
}