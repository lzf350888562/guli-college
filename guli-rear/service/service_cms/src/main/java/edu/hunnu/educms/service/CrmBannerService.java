package edu.hunnu.educms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.hunnu.educms.entity.CrmBanner;

import java.util.List;

/**
 * <p>
 * 首页banner表 服务类
 * </p>
 *
 * @author testjava
 * @since 2021-03-18
 */
public interface CrmBannerService extends IService<CrmBanner> {

    // 查询所有banner
    List<CrmBanner> selectAllBanner();
}
