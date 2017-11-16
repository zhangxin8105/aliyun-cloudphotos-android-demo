/**
 * Copyright (C) 2017 Alibaba Group Holding Limited
 */
package com.alibaba.sdk.android.photostore.api;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.RpcAcsRequest;
import com.aliyuncs.http.FormatType;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.http.ProtocolType;

public class RenameFaceRequest extends BaseRequest {
    private Long id;
    private String name;

    public RenameFaceRequest(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public RpcAcsRequest build() {
        com.aliyuncs.cloudphoto.model.v20170711.RenameFaceRequest request = new com.aliyuncs.cloudphoto.model.v20170711.RenameFaceRequest();
        request.setProtocol(ProtocolType.HTTPS); //指定访问协议
        request.setAcceptFormat(FormatType.JSON); //指定api返回格式
        request.setMethod(MethodType.POST); //指定请求方法
        request.setRegionId(REGION_CN);//指定要访问的Region,仅对当前请求生效，不改变client的默认设置。
        request.setFaceId(id);
        request.setFaceName(name);
        if (!stsToken.isEmpty())
            request.setSecurityToken(stsToken);
        else if (!libraryId.isEmpty())
            request.setLibraryId(libraryId);
        request.setStoreName(storeName);
        request.setActionName("RenameFace");
        return request;
    }

    @Override
    public BaseResponse parse(String result) {
        return JSON.parseObject(result, RenameFaceResponse.class);
    }
}
