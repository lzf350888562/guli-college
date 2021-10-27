package edu.hunnu.vod.service.impl;

import com.aliyun.vod.upload.impl.UploadVideoImpl;
import com.aliyun.vod.upload.req.UploadStreamRequest;
import com.aliyun.vod.upload.resp.UploadStreamResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.vod.model.v20170321.DeleteVideoRequest;
import edu.hunnu.eduservice.exceptionhandler.GuliException;
import edu.hunnu.vod.Utils.ContantVodUtils;
import edu.hunnu.vod.Utils.InitVodCilent;
import edu.hunnu.vod.service.VodService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class VodServiceImpl implements VodService {
    // 上传视频到阿里云
    @Override
    public String uploadVideoAly(MultipartFile file) {
        try {
            InputStream inputStream = file.getInputStream();
            String originalFilename = file.getOriginalFilename();
            String title = originalFilename.substring(0, originalFilename.lastIndexOf("."));
            //title:上传之后显示的名称  这里 去掉后缀
            //fileName:上传文件原始名称
            //inputStream:上传文件输入流
            UploadStreamRequest request = new UploadStreamRequest(
                    ContantVodUtils.ACCESS_KEY_ID, ContantVodUtils.ACCESS_KEY_SECRET,
                    title, originalFilename, inputStream);

            UploadVideoImpl uploader = new UploadVideoImpl();
            UploadStreamResponse response = uploader.uploadStream(request);

            //如果设置回调URL无效，不影响视频上传，可以返回VideoId同时会返回错误码。
            // 其他情况上传失败时，VideoId为空，此时需要根据返回错误码分析具体错误原因
            String videoId = response.getVideoId();
            if (!response.isSuccess()) {
                String errorMessage = "阿里云上传错误：" + "code：" + response.getCode() + ", message：" + response.getMessage();
                if(StringUtils.isEmpty(videoId)){
                    throw new GuliException(20001, errorMessage);
                }
            }
            return videoId;
        } catch (IOException e) {
            throw new GuliException(20001, "guli vod 服务上传失败");
        }
    }

    // 删除多个阿里云视频的方法
    @Override
    public void removeMoreAlyVideo(List videoList) {
        try {
            // 初始化对象
            DefaultAcsClient client = InitVodCilent.initVodClient(ContantVodUtils.ACCESS_KEY_ID, ContantVodUtils.ACCESS_KEY_SECRET);
            // 创建删除视频的request 对象
            DeleteVideoRequest request = new DeleteVideoRequest();

            // videoList 值用，隔开
            String videoIds = StringUtils.join(videoList.toArray(), ",");
            // 向request设置视频id
            request.setVideoIds(videoIds);
            // 调用初始化对象的方法实现删除
            client.getAcsResponse(request);
        }catch (Exception e){
            e.printStackTrace();
            throw new GuliException(20001, "视频删除失败");
        }
    }
}
