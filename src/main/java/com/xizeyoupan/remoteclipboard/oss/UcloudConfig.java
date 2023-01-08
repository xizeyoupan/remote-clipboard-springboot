package com.xizeyoupan.remoteclipboard.oss;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "oss")
@Component
@Data
public class UcloudConfig {
    private String name;
    private String ucloudBucketName;
    private String ucloudBucketUrl;
    private String ucloudpublicKey;
    private String ucloudPrivateKey;
    private String ucloudPrefix;


}
