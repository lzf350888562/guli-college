package edu.hunnu.eduservice.client;

import edu.hunnu.commonutils.R;
import org.springframework.stereotype.Component;

import java.util.List;

@Component //注意:此注解不能丢
public class VodFileDegradeFeignClient implements VodClient{
    // 出错之后执行
    @Override
    public R removeAlyVideo(String id) {
        return R.error().message("熔断处理:删除视频出错了");  //20001
    }

    @Override
    public R deleteBatch(List<String> videoList) {
        return R.error().message("熔断处理:删除多个视频出错了");
    }
}
