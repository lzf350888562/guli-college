package edu.hunnu.oss.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import edu.hunnu.oss.service.OssService;
import edu.hunnu.oss.utils.ConstantPropertiesUtils;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * 使用的是oos上传文件流
 * @author hunnu/lzf
 * @Date 2021/6/4
 */
@Service
public class OssServiceImpl implements OssService {
	//上传头像到oss
	@Override
	public String uploadFileAvatar(MultipartFile file) {
		// yourEndpoint填写Bucket所在地域对应的Endpoint。以华东1（杭州）为例，Endpoint填写为https://oss-cn-hangzhou.aliyuncs.com。
		String endpoint = ConstantPropertiesUtils.END_POINT;
		// 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
		String accessKeyId = ConstantPropertiesUtils.ACCESS_KEY_ID;
		String accessKeySecret = ConstantPropertiesUtils.ACCESS_KEY_SECRET;
		String bucketName = ConstantPropertiesUtils.BUCKET_NAME;
		try {
			// 创建OSSClient实例。
			OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
			//获取上传文件输入流
			// 填写本地文件的完整路径。如果未指定本地路径，则默认从示例程序所属项目对应本地路径中上传文件流。
			InputStream inputStream = file.getInputStream();
			//获取文件名称
			String fileName = file.getOriginalFilename();
			// 1、在文件名称里面添加随机唯一的值
			String uuid = UUID.randomUUID().toString().replaceAll("-", "");
			fileName = uuid + fileName;
			//2、把文件按照日期进行目录分类
			String datapath = new DateTime().toString("yyyy/MM/dd");
			//拼接
			fileName = datapath + "/" + fileName;
			//调用oss方法实现上传
			// 填写Bucket名称和Object完整路径。Object完整路径中不能包含Bucket名称。
			//第一个参数 Bucket名称
			//第二个参数 上传到oss系统的文件路径和文件名称 年/月/日/xxx.xxx
			//第三个参数 上传文件输入流
			ossClient.putObject(bucketName, fileName, inputStream);
			// 关闭OSSClient。
			ossClient.shutdown();
			//把上传后文件路径返回
			//需要把上传到阿里云oss路径手动拼接出来
			//https://lzf-guli2021.oss-cn-guangzhou.aliyuncs.com/2021/06/04/34cd35bdf12e4ee7804bd7ff4578e47aBoy-01.png
			String url = "https://"+bucketName+"."+endpoint+"/"+fileName;
			return url;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
