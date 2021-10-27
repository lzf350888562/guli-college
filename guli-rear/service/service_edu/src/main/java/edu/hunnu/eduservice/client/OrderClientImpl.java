package edu.hunnu.eduservice.client;

import org.springframework.stereotype.Component;

/**
 * @author hunnu/lzf
 * @Date 2021/6/12
 */
@Component
public class OrderClientImpl implements OrderClient{
	@Override
	public boolean isBuyCourse(String courseId, String memberId) {
		return false;
	}
}
