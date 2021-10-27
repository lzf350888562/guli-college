package edu.hunnu.eduservice.controller;


import edu.hunnu.commonutils.R;
import edu.hunnu.eduservice.client.VodClient;
import edu.hunnu.eduservice.entity.EduVideo;
import edu.hunnu.eduservice.exceptionhandler.GuliException;
import edu.hunnu.eduservice.service.EduVideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 课程视频 前端控制器
 * </p>
 *
 * @author lzf
 * @since 2021-06-05
 */
@RestController
@RequestMapping("/eduservice/video")
@CrossOrigin
public class EduVideoController {
	@Autowired
	EduVideoService videoService;

	// 注入vodClient
	@Autowired
	VodClient vodClient;

	// 添加小节
	@PostMapping("addVideo")
	public R addVideo(@RequestBody EduVideo eduVideo){
		videoService.save(eduVideo);
		return R.ok();
	}

	// 删除小节
	// TODO 删除视频
	@DeleteMapping("{id}")
	public R deleteVideo(@PathVariable String id){
		// 根据小节ID获得视频id，调用方法实现视频删除
		EduVideo eduVideo = videoService.getById(id);
		String videoSourceId = eduVideo.getVideoSourceId();

		 //判断小节里面是否有视频
		if (!StringUtils.isEmpty(videoSourceId)){
			//根据视频id，远程调用实现视频删除
			R result = vodClient.removeAlyVideo(videoSourceId);
			if (result.getCode() == 20001){  //测试熔断
				throw new GuliException(20001,"删除视频失败，熔断器");
			}
		}
		// 删除小节
		videoService.removeById(id);
		return R.ok();
	}
	// 修改小节 TODO
	@PostMapping("updateVideo")
	public R updateVideo(@RequestBody EduVideo eduVideo){
		videoService.updateById(eduVideo);
		return R.ok();
	}

	// 根据小节Id查询
	@GetMapping("getVideoInfo/{videoId}")
	public R getVideoInfo(@PathVariable String videoId){
		EduVideo eduVideo = videoService.getById(videoId);
		return R.ok().data("video",eduVideo);
	}
}

