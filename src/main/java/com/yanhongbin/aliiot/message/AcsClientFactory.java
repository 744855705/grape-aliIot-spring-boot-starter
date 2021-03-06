package com.yanhongbin.aliiot.message;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.yanhongbin.aliiot.config.AliIotProperties;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created with IDEA
 * description:获取AcsClient,单例
 * @author :YanHongBin
 * @date :Created in 2019/5/11 11:38
 */
@Component
@Scope("singleton")
public class AcsClientFactory {

    @Resource(type = AliIotProperties.class)
    private AliIotProperties aliIotProperties;

    private DefaultAcsClient client;

    public DefaultAcsClient getAcsClient(){
        if (client == null) {
            synchronized (AcsClientFactory.class){
                if (client == null) {
                    try {
                        String regionId= aliIotProperties.getRegionId().toString();
                        String accessKeySecret= aliIotProperties.getAccessKeySecret();
                        String accessKeyId= aliIotProperties.getAccessKeyId();
                        String domain= aliIotProperties.getDomain();
                        String productCode= aliIotProperties.getProductCode();
                        IClientProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, accessKeySecret);
                        DefaultProfile.addEndpoint(regionId, regionId, productCode, domain);
                        // 初始化client
                        client = new DefaultAcsClient(profile);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                return client;
            }
        } else {
            return client;
        }
    }

}
