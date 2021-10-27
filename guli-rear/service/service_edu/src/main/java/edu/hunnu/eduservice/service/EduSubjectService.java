package edu.hunnu.eduservice.service;

import edu.hunnu.eduservice.entity.EduSubject;
import com.baomidou.mybatisplus.extension.service.IService;
import edu.hunnu.eduservice.entity.subject.OneSubject;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 课程科目 服务类
 * </p>
 *
 * @author lzf
 * @since 2021-06-04
 */
public interface EduSubjectService extends IService<EduSubject> {

	void saveSubject(MultipartFile file, EduSubjectService subjectService);

	List<OneSubject> getAllOneTwoSubject();
}
