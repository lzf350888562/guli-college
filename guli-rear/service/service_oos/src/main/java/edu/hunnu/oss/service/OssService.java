package edu.hunnu.oss.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author hunnu/lzf
 * @Date 2021/6/4
 */
public interface OssService {
	//上传头像到oss
	String uploadFileAvatar(MultipartFile file);
}
