package edu.hunnu.eduservice.client;

import edu.hunnu.commonutils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author hunnu/lzf
 * @Date 2021/6/6
 */
@Component
@FeignClient(name="service-vod",fallback = VodFileDegradeFeignClient.class)
public interface VodClient {
	// 根据视频id 删除阿里云视频 完整路径
	@DeleteMapping("/eduvod/video/removeVideo/{id}")
	public R removeAlyVideo(@PathVariable("id") String id);

	// 删除多个阿里云视频的方法
	@DeleteMapping("/eduvod/video/delete-batch")
	public R deleteBatch(@RequestParam("videoList") List<String> videoList);
}
