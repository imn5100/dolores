package com.shaw.dolores.controller;

import com.alibaba.fastjson.JSON;
import com.shaw.dolores.bo.Device;
import com.shaw.dolores.bo.Meta;
import com.shaw.dolores.bo.User;
import com.shaw.dolores.dao.DeviceRepository;
import com.shaw.dolores.dao.MetaRepository;
import com.shaw.dolores.utils.*;
import com.shaw.dolores.vo.SessionData;
import com.shaw.dolores.websocket.SessionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user/devices")
public class DeviceController {
    @Value("${dolores.subscribe.prefix}")
    private String subPrefix;
    @Autowired
    private MetaRepository metaRepository;
    @Autowired
    private DeviceRepository deviceRepository;
    @Autowired
    private SessionHandler sessionHandler;

    @RequestMapping(value = "/saveOrUpdate", method = RequestMethod.POST)
    public ResponseDataholder devicesAdd(@SessionAttribute(name = Constants.HTTP_SESSION_USER) User user, String id, String deviceName) throws Exception {
        Device device;
        if (Utils.isNotEmpty(id)) {
            device = deviceRepository.findByIdAndUserId(id, user.getId());
            if (device == null) {
                return ResponseDataholder.fail(ResponseCode.ID_WRONG);
            }
        } else {
            device = new Device();
            device.setId(DesUtils.getDefaultInstance().encrypt(String.valueOf(GidUtil.next())));
            device.setCreateTime(System.currentTimeMillis());
            device.setUserId(user.getId());
        }
        device.setName(deviceName);
        device.setUpdateTime(System.currentTimeMillis());
        deviceRepository.save(device);
        return ResponseDataholder.success();
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public ResponseDataholder devicesDelete(@SessionAttribute(name = Constants.HTTP_SESSION_USER) User user, String id) {
        if (Utils.isNotEmpty(id)) {
            Device device = deviceRepository.findByIdAndUserId(id, user.getId());
            if (device == null) {
                return ResponseDataholder.fail(ResponseCode.ID_WRONG);
            } else {
                deviceRepository.delete(device);
                return ResponseDataholder.success();
            }
        } else {
            return ResponseDataholder.fail(ResponseCode.ID_WRONG);
        }
    }

    @RequestMapping(value = "/token", method = RequestMethod.POST)
    public ResponseDataholder getConnectToken(@SessionAttribute(name = Constants.HTTP_SESSION_USER) User user, String driverId) {
        if (user != null && user.getId() != null && Utils.isNotEmpty(driverId)) {
            if (metaRepository.countByOwnerAndExpireTimeAfter(user.getId(), System.currentTimeMillis()) > Constants.MAX_TOKEN) {
                return ResponseDataholder.fail(ResponseCode.TRY_LATER);
            }
            Device device = deviceRepository.findByIdAndUserId(driverId, user.getId());
            if (device == null) {
                return ResponseDataholder.fail(ResponseCode.ID_WRONG);
            }
            SessionData sessionData = new SessionData();
            String sessionId = Utils.generateSessionId();
            String token = Utils.generateToken();
            sessionData.setToken(token);
            sessionData.setTopicList(Collections.singletonList(Utils.buildSubscribeUrl(subPrefix, driverId, user.getId())));
            sessionData.setSessionId(sessionId);
            sessionData.setUserId(user.getId());
            sessionData.setDeviceId(device.getId());
            sessionData.setDeviceName(device.getName());
            Meta meta = Meta.of(token, JSON.toJSONString(sessionData), user.getId(), 5, TimeUnit.MINUTES);
            metaRepository.save(meta);
            return ResponseDataholder.success(sessionData);
        }
        return ResponseDataholder.fail(ResponseCode.NOT_LOGIN);
    }

    @RequestMapping(value = "/disconnect", method = RequestMethod.POST)
    public ResponseDataholder disconnect(@SessionAttribute(name = Constants.HTTP_SESSION_USER) User user, String sessionId) {
        if (user != null && user.getId() != null && Utils.isNotEmpty(sessionId)) {
            SessionData sessionData = sessionHandler.getSessionData(sessionId);
            if (sessionData != null && sessionData.getUserId() == user.getId()) {
                sessionHandler.disconnect(sessionId);
                return ResponseDataholder.success();
            } else {
                return ResponseDataholder.fail("未找到相关连接信息");
            }
        }
        return ResponseDataholder.fail(ResponseCode.NOT_LOGIN);
    }

}
