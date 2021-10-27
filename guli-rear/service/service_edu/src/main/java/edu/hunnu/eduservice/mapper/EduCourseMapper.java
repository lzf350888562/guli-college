package edu.hunnu.eduservice.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.hunnu.eduservice.entity.EduCourse;
import edu.hunnu.eduservice.entity.frontvo.CourseWebVo;
import edu.hunnu.eduservice.entity.vo.CoursePublishVo;

/**
 * <p>
 * 课程 Mapper 接口
 * </p>
 *
 * @author testjava
 * @since 2021-03-10
 */
public interface EduCourseMapper extends BaseMapper<EduCourse> {
    public CoursePublishVo getPublishCouseInfo(String courseId);

    CourseWebVo getBaseCourseInfo(String courseId);
}
