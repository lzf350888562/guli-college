package edu.hunnu.oss.controller;

import edu.hunnu.commonutils.R;
import edu.hunnu.oss.service.OssService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author hunnu/lzf
 * @Date 2021/6/4
 */
@Api(description = "文件上传")
@RestController
@RequestMapping("/eduoss/fileoss")
@CrossOrigin
public class OssController {
	@Autowired
	private OssService ossService;
	//上传头像的方法
	@PostMapping
	public R uploadOssFile(MultipartFile file){
		//获取上传文件 MultipartFile
		//返回上传到oss的路径
		String url = ossService.uploadFileAvatar(file);
		System.out.println(url);
		return R.ok().data("url",url);
	}
}
