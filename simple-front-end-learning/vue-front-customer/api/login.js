import request from '@/utils/request'

export default {
	//登录
	login(user) {
		return request({
			url: `/educenter/member/login`,
			method: 'post',
			data: user
		})
	},
	//根据token获取用户信息
	getLoginUserInfo(){
		return request({
			url: `/educenter/member/getMemberInfo`,
			method: 'get'
		})
	}
}