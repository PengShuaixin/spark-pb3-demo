package com.donews;

import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

/**
 * @author Niclas
 * @title UserInfoDeserializer
 * @projectName spark-pb3-demo
 * @date 2021/2/26 17:39
 * @description
 */
public class UserInfoDeserializer implements Deserializer<UserInfoBuf.UserInfo> {
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public UserInfoBuf.UserInfo deserialize(String topic, byte[] data) {
        if (data == null)
            return null;
        try {
            return UserInfoBuf.UserInfo.parseFrom(data);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void close() {

    }
}
