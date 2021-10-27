package edu.hunnu.eduservice.service;

import edu.hunnu.eduservice.entity.EduChapter;
import com.baomidou.mybatisplus.extension.service.IService;
import edu.hunnu.eduservice.entity.chapter.ChapterVo;

import java.util.List;

/**
 * <p>
 * 课程 服务类
 * </p>
 *
 * @author lzf
 * @since 2021-06-05
 */
public interface EduChapterService extends IService<EduChapter> {
	//课程大纲列表，根据课程id进行查询
	List<ChapterVo> getChapterVideoByCourseId(String courseId);

	// 删除章节
	boolean deleteChapter(String chapterId);

	// 2、根据课程ID删除章节
	void removeChapterByCourseId(String courseId);
}
