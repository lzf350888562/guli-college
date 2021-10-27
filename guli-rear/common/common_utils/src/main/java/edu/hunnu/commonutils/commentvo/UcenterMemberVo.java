package edu.hunnu.commonutils.commentvo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author hunnu/lzf
 * @Date 2021/6/11
 */
@ApiModel(value = "评论封装类",description = "评论封装类")
@Data
public class UcenterMemberVo {

	@ApiModelProperty(value = "会员id")
	private String id;

	@ApiModelProperty(value = "昵称")
	private String nickname;

	@ApiModelProperty(value = "用户头像")
	private String avatar;

}
