<template>
    <div class="main">
        <div class="title">
            <a class="active" href="/login">登录</a> <!-- 它会去pages目录下找login.vue-->
            <span>·</span>
            <a href="/register">注册</a>
        </div>

        <div class="sign-up-container">
            <el-form ref="userForm" :model="user">

                <el-form-item class="input-prepend restyle" prop="mobile"
                              :rules="[{ required: true, message: '请输入手机号码', trigger: 'blur' },{validator: checkPhone, trigger: 'blur'}]">
                    <div>
                        <el-input type="text" placeholder="手机号" v-model="user.mobile"/>
                        <i class="iconfont icon-phone"/>
                    </div>
                </el-form-item>

                <el-form-item class="input-prepend" prop="password"
                              :rules="[{ required: true, message: '请输入密码', trigger: 'blur' }]">
                    <div>
                        <el-input type="password" placeholder="密码" v-model="user.password"/>
                        <i class="iconfont icon-password"/>
                    </div>
                </el-form-item>

                <div class="btn">
                    <input type="button" class="sign-in-button" value="登录" @click="submitLogin()">
                </div>
            </el-form>
            <!-- 更多登录方式 -->
            <div class="more-sign">
                <h6>社交帐号登录</h6>
                <ul>
                    <li><a id="weixin" class="weixin" target="_blank"
                           href="http://localhost:9001/api/ucenter/wx/login"><i
                        class="iconfont icon-weixin"/></a></li>
                    <li><a id="qq" class="qq" target="_blank" href="#"><i class="iconfont icon-qq"/></a></li>
                </ul>
            </div>
        </div>

    </div>
</template>

<script>
	import '~/assets/css/sign.css'
	import '~/assets/css/iconfont.css'
	import cookie from 'js-cookie'
	import loginApi from "@/api/login";

	export default {
		layout: 'sign',

		data() {
			return {
                //封装登录手机号和密码对象
				user: {
					mobile: '',
					password: ''
				},
				loginInfo: {},

                // token:''
			}
		},

		methods: {
			//登录,接收token
			submitLogin() {
				loginApi.login(this.user).then(response => {
					console.log(response.data)
					if(response.data.code === 20000){
						//得到token,并置入cookie
                        //第一个参数 cookie参数名称  第二个参数为 参数值  第三个参数为 作用范围
						cookie.set('guli_token',response.data.data.token,{
							domain:'localhost' //作用范围 表示在什么样的请求才进行传递
						})

						//根据token获取用户信息 为了首页显示
						loginApi.getLoginUserInfo().then(response=>{
							this.loginInfo = response.data.data.userInfo;
                            //把用户信息也放到cookie中
							cookie.set('guli_ucenter',this.loginInfo,{
								domain:'localhost'
							})
							//提示
							this.$message({
								type:"success",
								message:"登录成功!"
							})
                            //跳转到首页面
							window.location.href = '/'
                            // this.$router.push({path:'/'})
						})
                    }else {
						//提示
						this.$message({
							type:"warning",
							message:`${response.data.message}`
						})
                        this.user = {}
                    }
				})
			},

			checkPhone(rule, value, callback) {
				//debugger
				if (!(/^1[34578]\d{9}$/.test(value))) {
					return callback(new Error('手机号码格式不正确'))
				}
				return callback()
			}
		}
	}
</script>
<style>
    .el-form-item__error {
        z-index: 9999999;
    }
</style>