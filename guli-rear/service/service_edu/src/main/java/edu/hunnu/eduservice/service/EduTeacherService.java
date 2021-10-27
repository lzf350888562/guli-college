package edu.hunnu.eduservice.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import edu.hunnu.eduservice.entity.EduTeacher;
import com.baomidou.mybatisplus.extension.service.IService;
import edu.hunnu.eduservice.entity.vo.TeacherQuery;

import java.util.Map;

/**
 * <p>
 * 讲师 服务类
 * </p>
 *
 * @author testjava
 * @since 2021-05-31
 */
public interface EduTeacherService extends IService<EduTeacher> {
	void pageQuery(Page<EduTeacher> pageParam, TeacherQuery teacherQuery);
	// 1 分页查询讲师的方法
	Map<String, Object> getTeacherFrontList(Page<EduTeacher> pageTeacher);
}
