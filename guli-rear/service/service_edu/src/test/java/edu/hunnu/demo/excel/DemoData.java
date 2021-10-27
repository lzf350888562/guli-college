package edu.hunnu.demo.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author hunnu/lzf
 * @Date 2021/6/4
 */
@Data
public class DemoData {
	//value设置excel表头名称 用于写 ,  index表示列数索引 用于读
	@ExcelProperty(value="学生编号",index = 0)
	private  Integer sno;
	@ExcelProperty(value="学生姓名",index = 1)
	private  String sname;
}
