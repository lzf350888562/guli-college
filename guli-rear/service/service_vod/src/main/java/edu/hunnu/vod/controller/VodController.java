package edu.hunnu.vod.controller;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.vod.model.v20170321.DeleteVideoRequest;
import com.aliyuncs.vod.model.v20170321.GetVideoPlayAuthRequest;
import com.aliyuncs.vod.model.v20170321.GetVideoPlayAuthResponse;
import edu.hunnu.commonutils.R;
import edu.hunnu.eduservice.exceptionhandler.GuliException;
import edu.hunnu.vod.Utils.ContantVodUtils;
import edu.hunnu.vod.Utils.InitVodCilent;
import edu.hunnu.vod.service.VodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/eduvod/video")
@CrossOrigin
public class VodController {

    @Autowired
    private VodService vodService;

    // 上传视频到阿里云
    @PostMapping("uploadVideo")
    public R uploadAlyVideo(MultipartFile file){
        // 返回视频Id
        String videoId = vodService.uploadVideoAly(file);
        return R.ok().data("videoId",videoId);
    }

    // 根据视频id 删除阿里云视频
    @DeleteMapping("removeVideo/{id}")
    public R removeAlyVideo(@PathVariable String id){
        try {
            // 初始化对象
            DefaultAcsClient client = InitVodCilent.initVodClient(ContantVodUtils.ACCESS_KEY_ID, ContantVodUtils.ACCESS_KEY_SECRET);
            // 创建删除视频的request 对象
            DeleteVideoRequest request = new DeleteVideoRequest();
            // 向request设置视频id
            request.setVideoIds(id);
            // 调用初始化对象的方法实现删除
            client.getAcsResponse(request);
            System.out.println("删除云视频成功!!!");
            return  R.ok();
        }catch (Exception e){
            e.printStackTrace();
            throw new GuliException(20001, "视频删除失败");
        }
    }

    // 删除多个阿里云视频的方法
    @DeleteMapping("delete-batch")
    public R deleteBatch(@RequestParam("videoList") List<String> videoList){
        vodService.removeMoreAlyVideo(videoList);
        return R.ok();
    }

    // 根据视频id获取视频凭证
    @GetMapping("getPlayAuth/{id}")
    public R getPlayAuth(@PathVariable String id){
        try {
            // 创建初始化对象
            DefaultAcsClient client = InitVodCilent.initVodClient(ContantVodUtils.ACCESS_KEY_ID, ContantVodUtils.ACCESS_KEY_SECRET);
            // 创建获取凭着request和response对象
            GetVideoPlayAuthRequest request = new GetVideoPlayAuthRequest();
            // 向request设置对象id
            request.setVideoId(id);
            // 调用方法得到凭证
            GetVideoPlayAuthResponse response = client.getAcsResponse(request);
            String playAuth = response.getPlayAuth();
            return R.ok().data("playAuth",playAuth);
        }catch (Exception e){
            throw new GuliException(20001, "获取凭证失败");
        }
    }
}
