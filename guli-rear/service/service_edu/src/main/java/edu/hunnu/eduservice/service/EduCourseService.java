package edu.hunnu.eduservice.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import edu.hunnu.eduservice.entity.EduCourse;
import com.baomidou.mybatisplus.extension.service.IService;
import edu.hunnu.eduservice.entity.frontvo.CourseFrontVo;
import edu.hunnu.eduservice.entity.frontvo.CourseWebVo;
import edu.hunnu.eduservice.entity.vo.CourseInfoVo;
import edu.hunnu.eduservice.entity.vo.CoursePublishVo;

import java.util.Map;

/**
 * <p>
 * 课程 服务类
 * </p>
 *
 * @author lzf
 * @since 2021-06-05
 */
public interface EduCourseService extends IService<EduCourse> {

	//添加课程基本信息的方法
	String saveCourseInfo(CourseInfoVo courseInfoVo);

	// 根据课程id查询课程基本信息
	CourseInfoVo getCourseInfo(String courseId);

	// 修改课程信息
	void updateCourseInfo(CourseInfoVo courseInfoVo);

	// 根据课程id查询课程确认信息
	CoursePublishVo publishCourseInfo(String id);

	// 删除课程
	void removeCourse(String courseId);

	// 1 条件查询带分页查询课程
	Map<String, Object> getCourseFrontList(Page<EduCourse> pageParam, CourseFrontVo courseFrontVo);

	//2 课程详情的方法
	CourseWebVo getBaseCourseInfo(String courseId);
}
