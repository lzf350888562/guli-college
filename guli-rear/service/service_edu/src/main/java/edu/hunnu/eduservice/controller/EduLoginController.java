package edu.hunnu.eduservice.controller;

import edu.hunnu.commonutils.R;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.*;

/**
 * 模拟整合前端登录 无security
 * @author hunnu/lzf
 * @Date 2021/6/3
 */
@Api(description = "讲师登录")
@RestController
@RequestMapping("/eduservice/user")
@CrossOrigin //解决跨域 只能和springcloud中的gateway中的跨域只能存在一个
public class EduLoginController {
	//login
	@PostMapping("login")
	public R login(){
		return R.ok().data("token","admin");
	}
	//info
	@GetMapping("info")
	public R info(){
		return R.ok().data("roles","[admin]").data("name","admin").data("avatar","https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
	}
}