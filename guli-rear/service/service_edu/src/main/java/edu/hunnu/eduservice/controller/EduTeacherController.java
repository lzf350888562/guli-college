package edu.hunnu.eduservice.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import edu.hunnu.commonutils.R;
import edu.hunnu.eduservice.entity.EduTeacher;
import edu.hunnu.eduservice.entity.vo.TeacherQuery;
import edu.hunnu.eduservice.exceptionhandler.GuliException;
import edu.hunnu.eduservice.service.EduTeacherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 讲师 前端控制器
 * </p>
 * 讲师
 * @author testjava
 * @since 2021-05-31
 */
@Api(description = "讲师管理")
@RestController
@RequestMapping("/eduservice/teacher")
@CrossOrigin //解决跨域 只能和springcloud中的gateway中的跨域只能存在一个
public class EduTeacherController {
	@Autowired
	private EduTeacherService teacherService;
	/**
	 * 1、查询讲师表所有信息
	 * http://localhost:8001/edu/teacher/findAll
	 */
	@ApiOperation(value = "所有讲师列表")
	@GetMapping("findAll")
	public R findAllTeacher() {
//		int a=10/0;
//		try {
//			int a=10/0;
//		} catch (Exception e) {
//			throw new GuliException(444,"自定义错误");   //转换为自定义异常
//		}
		List<EduTeacher> list = teacherService.list(null);
		return R.ok().data("items",list);
	}
	/**
	 * 	2、逻辑删除
	 */
	@ApiOperation(value = "根据ID逻辑删除讲师")
	@DeleteMapping("{id}")
	public R removeTeacher(@ApiParam(name = "id", value = "讲师ID", required = true) @PathVariable String id) {
		boolean result = teacherService.removeById(id);
		if(result){
			return R.ok();
		}else {
			return R.error();
		}
	}
	/**
	 * 	3、分页查询
	 * http://localhost:8001/edu/teacher/1/2
	 */
	@ApiOperation(value = "分页讲师列表")
	@GetMapping("{page}/{limit}")
	public R pageList(@ApiParam(name = "page", value = "当前页码", required = true)
					  @PathVariable Long page,

					  @ApiParam(name = "limit", value = "每页记录数", required = true)
					  @PathVariable Long limit) {
		Page<EduTeacher> pageParam = new Page<>(page, limit);

		teacherService.page(pageParam, null);
		List<EduTeacher> records = pageParam.getRecords();  //每一页数据的list集合
		long total = pageParam.getTotal();   //总记录条数

		return R.ok().data("total", total).data("rows", records);
	}

	//4、条件查询
	@ApiOperation(value = "分页讲师列表")
	@PostMapping("pageTeacherCondition/{page}/{limit}")
	public R pageQuery(
			@ApiParam(name = "page", value = "当前页码", required = true)
			@PathVariable Long page,
			@ApiParam(name = "limit", value = "每页记录数", required = true)
			@PathVariable Long limit,
			@ApiParam(name = "teacherQuery", value = "查询对象", required = false)
			//此注解如果加了在swagger中要输入完整json对象且必须post方式提交,不加可直接输入对象属性
			//因为RequestBody表示使用json传递数据,把json数据封装到对应的对象里面
			@RequestBody(required = false)
					TeacherQuery teacherQuery) {
		//创建page对象
		Page<EduTeacher> pageTeacher = new Page<>(page,limit);
		//构建条件
		QueryWrapper<EduTeacher> wrapper = new QueryWrapper<>();
		// 多条件组合查询

		// mybatis学过 动态sql
		String name = teacherQuery.getName();
		Integer level = teacherQuery.getLevel();
		String begin = teacherQuery.getBegin();
		String end = teacherQuery.getEnd();
		//判断条件值是否为空，如果不为空拼接条件
		if(!StringUtils.isEmpty(name)) {
			//构建条件
			wrapper.like("name",name);
		}
		if(!StringUtils.isEmpty(level)) {
			wrapper.eq("level",level);  //等于
		}
		if(!StringUtils.isEmpty(begin)) {
			wrapper.ge("gmt_create",begin);  //大于等于
		}
		if(!StringUtils.isEmpty(end)) {
			wrapper.le("gmt_create",end);	//小于等于
		}

		//排序
		wrapper.orderByDesc("gmt_create");

		//调用方法实现条件查询分页
		teacherService.page(pageTeacher,wrapper);

		long total = pageTeacher.getTotal();//总记录数
		List<EduTeacher> records = pageTeacher.getRecords(); //数据list集合
		return R.ok().data("total",total).data("rows",records);
	}

	//5、添加讲师
	@ApiOperation(value = "新增讲师")
	@PostMapping("addTeacher")
	public R addTeacher(@ApiParam(name = "teacher", value = "讲师对象", required = true)
						@RequestBody EduTeacher eduTeacher) {

		boolean save = teacherService.save(eduTeacher);
		if (save) {
			return R.ok();
		} else {
			return R.error();
		}
	}

	/**
	 * 更新方式1.采用查询+修改
	 * @param id
	 * @return
	 */
	//6、根据id进行查询
	@ApiOperation(value = "根据ID查询讲师")
	@GetMapping("getTeacher/{id}")
	public R getTeacher(@ApiParam(name = "id", value = "讲师ID", required = true)
						@PathVariable String id) {

		EduTeacher eduTeacher = teacherService.getById(id);
		return R.ok().data("teacher", eduTeacher);
	}

	//7、id修改
	@ApiOperation(value = "修改讲师")
	@PostMapping("updateTeacher")
	public R updateTeacher(@RequestBody EduTeacher eduTeacher){
		boolean flag = teacherService.updateById(eduTeacher);
		if (flag){
			return R.ok();
		}else {
			return R.error();
		}
	}

	/**
	 * 更新方式2.使用Put
	 */
	@ApiOperation(value = "根据ID修改讲师")
	@PutMapping("{id}")
	public R updateById(@ApiParam(name="id",value="讲师ID",required = true)
							@PathVariable String id,
						@ApiParam(name="teacher",value="讲师对象",required = true)
						@RequestBody EduTeacher teacher){
		teacher.setId(id);
		teacherService.updateById(teacher);
		return R.ok();
	}
}

