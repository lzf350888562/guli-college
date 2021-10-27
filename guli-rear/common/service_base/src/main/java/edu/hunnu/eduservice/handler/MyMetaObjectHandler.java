package edu.hunnu.eduservice.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author hunnu/lzf
 * @Date 2021/5/31
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
	//注意 这里的fieldName为实体类的字段名称 而不是数据库字段名称  类似jpa
	@Override
	public void insertFill(MetaObject metaObject) {
		this.setFieldValByName("gmtCreate",new Date(),metaObject);
		this.setFieldValByName("gmtModified",new Date(),metaObject);
	}
	@Override
	public void updateFill(MetaObject metaObject) {
		this.setFieldValByName("gmtModified",new Date(),metaObject);
	}
}
