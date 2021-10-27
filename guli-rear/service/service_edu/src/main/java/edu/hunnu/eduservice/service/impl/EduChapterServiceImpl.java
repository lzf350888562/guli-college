package edu.hunnu.eduservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import edu.hunnu.eduservice.entity.EduChapter;
import edu.hunnu.eduservice.entity.EduVideo;
import edu.hunnu.eduservice.entity.chapter.ChapterVo;
import edu.hunnu.eduservice.entity.chapter.VideoVo;
import edu.hunnu.eduservice.exceptionhandler.GuliException;
import edu.hunnu.eduservice.mapper.EduChapterMapper;
import edu.hunnu.eduservice.service.EduChapterService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.hunnu.eduservice.service.EduVideoService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 课程 服务实现类
 * </p>
 *
 * @author lzf
 * @since 2021-06-05
 */
@Service
public class EduChapterServiceImpl extends ServiceImpl<EduChapterMapper, EduChapter> implements EduChapterService {
	@Autowired
	private EduVideoService videoService;

	//课程大纲列表，根据课程id进行查询
	@Override
	public List<ChapterVo> getChapterVideoByCourseId(String courseId) {

		// 1、根据课程id查询课程所有的章节
		QueryWrapper<EduChapter> wrapperChapter = new QueryWrapper<>();
		wrapperChapter.eq("course_id", courseId);
		List<EduChapter> eduChaptersList = baseMapper.selectList(wrapperChapter);

		// 2、根据课程id查询课程里面所有的小节
		QueryWrapper<EduVideo> wrapperVideo = new QueryWrapper<>();
		wrapperVideo.eq("course_id", courseId);
		List<EduVideo> eduVideoList = videoService.list(wrapperVideo);

		//创建list集合，用于最后封装数据
		List<ChapterVo> finalList = new ArrayList<>();

		// 3、遍历查询章节list集合进行封装
		// 遍历查询章节list集合
		for (int i = 0; i < eduChaptersList.size(); i++) {
			// 每个章节
			EduChapter eduChapter = eduChaptersList.get(i);
			// eduChapter对象复制到ChapterVo里面
			ChapterVo chapterVo = new ChapterVo();
			BeanUtils.copyProperties(eduChapter, chapterVo);
			// 把chapterVo放到最终list集合
			finalList.add(chapterVo);

			// 创建集合，用于封装章节的小节
			List<VideoVo> videoList = new ArrayList<>();

			// 4、遍历查询小节list集合，进行封装
			for (int m = 0; m < eduVideoList.size(); m++) {
				// 得到每个小节
				EduVideo eduVideo = eduVideoList.get(m);
				// 判断：小节里面chapterid和章节里面id是否一样
				if (eduVideo.getChapterId().equals(eduChapter.getId())){
					// 进行封装
					VideoVo videoVo = new VideoVo();
					BeanUtils.copyProperties(eduVideo, videoVo);
					// 放到小节封装集合
					videoList.add(videoVo);
				}
			}
			// 把封装之后小节list集合，放到章节对象里面
			chapterVo.setChildren(videoList);
		}
		return finalList;
	}

	// 删除章节
	//如果章节里面有小节 不让进行删除!!!!!!!!
	@Override
	public boolean deleteChapter(String chapterId) {
		// 根据chapterId章节id查询小节表，如果查询出数据，不进行删除
		QueryWrapper<EduVideo> wrapper = new QueryWrapper<>();
		wrapper.eq("chapter_id", chapterId);
		int count = videoService.count(wrapper);
		// 判断
		if (count > 0){ // 查询出来小节，不进行删除
			throw new GuliException(20001,"不能删除");
		} else { // 不能查询出来数据，进行删除
			// 删除章节
			int result = baseMapper.deleteById(chapterId);
			// 成功 1>0
			return result>0;
		}

	}

	// 2、根据课程ID删除章节
	@Override
	public void removeChapterByCourseId(String courseId) {
		QueryWrapper<EduChapter> queryWrapper = new QueryWrapper();
		queryWrapper.eq("course_id", courseId);
		baseMapper.delete(queryWrapper);
	}
}
