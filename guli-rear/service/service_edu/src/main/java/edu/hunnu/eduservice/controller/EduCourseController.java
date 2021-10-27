package edu.hunnu.eduservice.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import edu.hunnu.commonutils.R;
import edu.hunnu.eduservice.entity.EduCourse;
import edu.hunnu.eduservice.entity.EduTeacher;
import edu.hunnu.eduservice.entity.vo.CourseInfoVo;
import edu.hunnu.eduservice.entity.vo.CoursePublishVo;
import edu.hunnu.eduservice.entity.vo.TeacherQuery;
import edu.hunnu.eduservice.service.EduCourseService;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 课程 前端控制器
 * </p>
 *
 * @author lzf
 * @since 2021-06-05
 */
@RestController
@RequestMapping("/eduservice/course")
@CrossOrigin
public class EduCourseController {

	@Autowired
	private EduCourseService courseService;

	// 课程列表基本实现
	@GetMapping
	public R getCourseList(){
		List<EduCourse> list = courseService.list(null);
		return R.ok().data("list", list);

	}
	// TODO 完善条件查询带分页
	@PostMapping("{page}/{limit}")
	public R getCourseListPage(@ApiParam(name = "page", value = "当前页码", required = true)
								   @PathVariable Long page,

							   @ApiParam(name = "limit", value = "每页记录数", required = true)
								   @PathVariable Long limit,
							   @RequestBody(required = false)
										   EduCourse eduCourse) {
		Page<EduCourse> pageParam = new Page<>(page, limit);
		QueryWrapper<EduCourse> wrapper = new QueryWrapper<>();
		String title = eduCourse.getTitle();
		String status = eduCourse.getStatus();
		if(!StringUtils.isEmpty(title)) {
			//构建条件
			wrapper.like("title",title);
		}
		if(!StringUtils.isEmpty(status)) {
			wrapper.eq("status",status);  //等于
		}
		courseService.page(pageParam, wrapper);
		List<EduCourse> records = pageParam.getRecords();  //每一页数据的list集合
		long total = pageParam.getTotal();   //总记录条数

		return R.ok().data("total", total).data("courseList", records);
	}

	//添加课程基本信息的方法
	@PostMapping("addCourseInfo")
	public R addCourseInfo(@RequestBody CourseInfoVo courseInfoVo){
		String id =  courseService.saveCourseInfo(courseInfoVo);
		return R.ok().data("courseId",id); //返回课程id
	}

	// 根据课程id查询课程基本信息
	@GetMapping("getCourseInfo/{courseId}")
	public R getCourseInfo(@PathVariable String courseId){
		CourseInfoVo courseInfoVo = courseService.getCourseInfo(courseId);
		return R.ok().data("courseInfoVo", courseInfoVo);
	}

	// 修改课程信息
	@PostMapping("updateCourseInfo")
	public R updateCourseInfo(@RequestBody CourseInfoVo courseInfoVo){
		courseService.updateCourseInfo(courseInfoVo);

		return R.ok();
	}

	// 课程最终发布
	@GetMapping("getPublishCourseInfo/{id}")
	public R getPublishCourseInfo(@PathVariable String id){
		CoursePublishVo coursePublishVo = courseService.publishCourseInfo(id);
		return R.ok().data("publishVo",coursePublishVo);
	}


	// 修改课程状态
	@PostMapping("publishCourse/{id}")
	public R publishCourse(@PathVariable String id){
		EduCourse eduCourse = new EduCourse();
		eduCourse.setId(id);
		eduCourse.setStatus("Normal"); // 设置课程发布状态
		courseService.updateById(eduCourse);
		return R.ok();
	}

	// 删除课程
	// 删除课程需要删除 其信息 描述 章节  小节 以及 视频文件
	@DeleteMapping("{courseId}")
	public R deleteCourse(@PathVariable String courseId){
		courseService.removeCourse(courseId);
		return R.ok();
	}
}

