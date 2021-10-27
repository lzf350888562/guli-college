package edu.hunnu.eduservice.client;


import edu.hunnu.commonutils.ordervo.UcenterMemberOrder;
import org.springframework.stereotype.Component;

/**
 * 熔断处理类
 */
@Component
public class UcenterClientImpl implements UcenterClient {
    @Override
    public UcenterMemberOrder getUcenterPay(String memberId) {
        return null;
    }
}