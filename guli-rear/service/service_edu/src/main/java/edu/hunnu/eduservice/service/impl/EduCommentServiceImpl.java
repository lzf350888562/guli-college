package edu.hunnu.eduservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.hunnu.eduservice.entity.EduComment;
import edu.hunnu.eduservice.mapper.EduCommentMapper;
import edu.hunnu.eduservice.service.EduCommentService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 评论 服务实现类
 * </p>
 *
 * @author testjava
 * @since 2021-03-22
 */
@Service
public class EduCommentServiceImpl extends ServiceImpl<EduCommentMapper, EduComment> implements EduCommentService {

}
