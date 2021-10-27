package edu.hunnu.eduservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import edu.hunnu.eduservice.client.VodClient;
import edu.hunnu.eduservice.entity.EduVideo;
import edu.hunnu.eduservice.mapper.EduVideoMapper;
import edu.hunnu.eduservice.service.EduVideoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 课程视频 服务实现类
 * </p>
 *
 * @author lzf
 * @since 2021-06-05
 */
@Service
public class EduVideoServiceImpl extends ServiceImpl<EduVideoMapper, EduVideo> implements EduVideoService {
	@Autowired
	VodClient vodClient;

	// 1、根据课程ID删除其所有小节的视频   单个删除在service-vod中前端直接调用
	// TODO 删除小节，删除对应视频文件
	@Override
	public void removeVideoByCourseId(String courseId) {
		// 1.根据课程Id查询所有视频Id
		QueryWrapper<EduVideo> wrapperVideo = new QueryWrapper<>();
		wrapperVideo.eq("course_id", courseId);
		//只需要云视频id
		wrapperVideo.select("video_source_id");
		List<EduVideo> eduVideoList = baseMapper.selectList(wrapperVideo);

		// List<EduVideo>变成List<String>
		List<String> videoIds = new ArrayList<>();
		for (int i = 0; i < eduVideoList.size(); i++) {
			EduVideo eduVideo = eduVideoList.get(i);
			//大坑  如果小节没有视频videoSourceId为null而不是空串  尽管前端设置了默认空串
			//所以这里采用了忽略NullPointerException
			//也可以在数据库中设置默认值为空串 ,但是在原先存在数据的话修改麻烦
			String videoSourceId = null;
			try {
				videoSourceId = eduVideo.getVideoSourceId();
			} catch (NullPointerException e) {
				continue;
			}
			if (!StringUtils.isEmpty(videoSourceId)) {
				videoIds.add(videoSourceId);
			}
		}
		if (videoIds.size() > 0) {
			// 根据多个视频Id删除多个视频
			vodClient.deleteBatch(videoIds);

			QueryWrapper<EduVideo> wrapper = new QueryWrapper<>();
			wrapper.eq("course_id", courseId);
			baseMapper.delete(wrapper);
		}
	}
}
