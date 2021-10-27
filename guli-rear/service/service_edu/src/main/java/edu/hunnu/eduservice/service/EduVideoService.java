package edu.hunnu.eduservice.service;

import edu.hunnu.eduservice.entity.EduVideo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 课程视频 服务类
 * </p>
 *
 * @author lzf
 * @since 2021-06-05
 */
public interface EduVideoService extends IService<EduVideo> {
	// 1、根据课程ID删除小节
	void removeVideoByCourseId(String courseId);
}
