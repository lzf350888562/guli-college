package edu.hunnu.eduservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import edu.hunnu.eduservice.entity.EduCourse;
import edu.hunnu.eduservice.entity.EduCourseDescription;
import edu.hunnu.eduservice.entity.frontvo.CourseFrontVo;
import edu.hunnu.eduservice.entity.frontvo.CourseWebVo;
import edu.hunnu.eduservice.entity.vo.CourseInfoVo;
import edu.hunnu.eduservice.entity.vo.CoursePublishVo;
import edu.hunnu.eduservice.exceptionhandler.GuliException;
import edu.hunnu.eduservice.mapper.EduCourseMapper;
import edu.hunnu.eduservice.service.EduChapterService;
import edu.hunnu.eduservice.service.EduCourseDescriptionService;
import edu.hunnu.eduservice.service.EduCourseService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.hunnu.eduservice.service.EduVideoService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 课程 服务实现类
 * </p>
 *
 * @author lzf
 * @since 2021-06-05
 */
@Service
public class EduCourseServiceImpl extends ServiceImpl<EduCourseMapper, EduCourse> implements EduCourseService {

	// 注入小节和章节service
	@Autowired
	private EduVideoService eduVideoService;
	@Autowired
	private EduChapterService eduChapterService;
	//课程描述的注入
	@Autowired
	private EduCourseDescriptionService courseDescriptionService;
	//添加课程基本信息的方法  不需要传id
	@Override
	public String saveCourseInfo(CourseInfoVo courseInfoVo) {
		//1 向课程表添加课程基本信息
		//CourseInfoVo对象转换eduCourse对象
		EduCourse eduCourse = new EduCourse();
		BeanUtils.copyProperties(courseInfoVo,eduCourse);
		//debug可以看到 在执行了insert后 eduCourse对象的id属性才从null变为mybatisplus生成的值
		//		this.save(eduCourse);
		int insert = baseMapper.insert(eduCourse);
		if(insert == 0) {
			//添加失败
			throw new GuliException(20001,"添加课程信息失败");
		}
		//获取添加之后课程id
		String cid = eduCourse.getId();

		//2 向课程简介表添加课程简介
		//edu_course_description
		EduCourseDescription courseDescription = new EduCourseDescription();
		courseDescription.setDescription(courseInfoVo.getDescription());
		//设置描述id就是课程id 完成一对一关系
		//注意:还需要修改实体类主键生成策略为手动输入
		courseDescription.setId(cid);
		courseDescriptionService.save(courseDescription);
		return cid;
	}

	// 根据课程id查询课程基本信息
	@Override
	public CourseInfoVo getCourseInfo(String courseId) {

		// 1、查询课程表
		EduCourse eduCourse = baseMapper.selectById(courseId);
		CourseInfoVo courseInfoVo = new CourseInfoVo();
		BeanUtils.copyProperties(eduCourse,courseInfoVo);
		// 2、查询描述表
		EduCourseDescription courseDescription = courseDescriptionService.getById(courseId);
		courseInfoVo.setDescription(courseDescription.getDescription());

		return courseInfoVo;
	}

	// 修改课程信息
	@Override
	public void updateCourseInfo(CourseInfoVo courseInfoVo) {
		// 修改课程表
		EduCourse eduCourse = new EduCourse();
		BeanUtils.copyProperties(courseInfoVo, eduCourse);
		int update = baseMapper.updateById(eduCourse);
		if (update == 0){
			throw new GuliException(20001, "修改课程信息失败");
		}
		// 修改描述表
		EduCourseDescription description = new EduCourseDescription();
		description.setId(courseInfoVo.getId());
		description.setDescription(courseInfoVo.getDescription());
		courseDescriptionService.updateById(description);
	}

	// 根据课程id查询课程确认信息
	@Override
	public CoursePublishVo publishCourseInfo(String id) {
		// 调用maaper
		CoursePublishVo publishCouseInfo = baseMapper.getPublishCouseInfo(id);
		return publishCouseInfo;
	}

	// 删除课程
	@Override
	public void removeCourse(String courseId) {
		// 1、根据课程ID删除小节 在里面调用微服务批量删除云视频
		eduVideoService.removeVideoByCourseId(courseId);

		// 2、根据课程ID删除章节
		eduChapterService.removeChapterByCourseId(courseId);

		// 3、根据课程ID删除描述
		courseDescriptionService.removeById(courseId);

		// 4、根据课程ID删除课程本身
		int result = baseMapper.deleteById(courseId);
		if (result == 0){
			throw new GuliException(20001, "删除失败");
		}
	}

	// 1 条件查询带分页查询课程
	@Override
	public Map<String, Object> getCourseFrontList(Page<EduCourse> pageParam, CourseFrontVo courseFrontVo) {
		// 2 根据讲师id查询所讲课程
		QueryWrapper<EduCourse> wrapper = new QueryWrapper<>();
		// 判断条件是否为空，不为空拼接
		if (!StringUtils.isEmpty(courseFrontVo.getSubjectParentId())) { // 一级分类
			wrapper.eq("subject_parent_id", courseFrontVo.getSubjectParentId());
		}

		if (!StringUtils.isEmpty(courseFrontVo.getSubjectId())) { // 二级分类
			wrapper.eq("subject_id", courseFrontVo.getSubjectId());
		}

		if (!StringUtils.isEmpty(courseFrontVo.getBuyCountSort())) { // 关注度
			wrapper.orderByDesc("buy_count");
		}

		if (!StringUtils.isEmpty(courseFrontVo.getGmtCreateSort())) {
			wrapper.orderByDesc("gmt_create");
		}

		if (!StringUtils.isEmpty(courseFrontVo.getPriceSort())) {
			wrapper.orderByDesc("price");
		}


		baseMapper.selectPage(pageParam, wrapper);

		List<EduCourse> records = pageParam.getRecords();
		long current = pageParam.getCurrent();
		long pages = pageParam.getPages();
		long size = pageParam.getSize();
		long total = pageParam.getTotal();
		boolean hasNext = pageParam.hasNext();
		boolean hasPrevious = pageParam.hasPrevious();

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("items", records);
		map.put("current", current);
		map.put("pages", pages);
		map.put("size", size);
		map.put("total", total);
		map.put("hasNext", hasNext);
		map.put("hasPrevious", hasPrevious);

		return map;
	}

	//2 课程详情的方法
	@Override
	public CourseWebVo getBaseCourseInfo(String courseId) {
		return baseMapper.getBaseCourseInfo(courseId);
	}
}
