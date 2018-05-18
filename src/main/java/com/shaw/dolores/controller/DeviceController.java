package com.shaw.dolores.controller;

import com.alibaba.fastjson.JSON;
import com.shaw.dolores.bo.Device;
import com.shaw.dolores.bo.Meta;
import com.shaw.dolores.bo.Task;
import com.shaw.dolores.bo.User;
import com.shaw.dolores.dao.DeviceRepository;
import com.shaw.dolores.dao.MetaRepository;
import com.shaw.dolores.dao.TaskRepository;
import com.shaw.dolores.utils.*;
import com.shaw.dolores.vo.SessionData;
import com.shaw.dolores.websocket.SessionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

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
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private TaskRepository taskRepository;

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
            sessionData.setTopicList(Collections.singletonList(Utils.buildSubscribeUrl(subPrefix, driverId)));
            sessionData.setSessionId(sessionId);
            sessionData.setUserId(user.getId());
            sessionData.setDeviceId(device.getId());
            sessionData.setDeviceName(device.getName());
            Meta meta = Meta.of(token, JSON.toJSONString(sessionData), user.getId(), Constants.TOKEN_EXPIRE_TIME_MIN, TimeUnit.MINUTES);
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

    @RequestMapping(value = "/sendTask", method = RequestMethod.POST)
    public ResponseDataholder sendTask(@SessionAttribute(name = Constants.HTTP_SESSION_USER) User user, String sessionId, String content, String topic, boolean saveInDb) {
        if (user != null && user.getId() != null && Utils.isNotEmpty(sessionId)) {
            SessionData sessionData = sessionHandler.getSessionData(sessionId);
            if (sessionData != null && sessionData.getUserId() == user.getId()) {
                if (Utils.isNotEmpty(content) && Task.TOPIC_SET.contains(topic)) {
                    Task task = new Task();
                    task.setContents(content);
                    task.setTopic(topic);
                    if (saveInDb) {
                        task.setOwner(user.getId());
                        task.setSessionId(sessionId);
                        task.setCreateTime(System.currentTimeMillis());
                        task.setUpdateTime(System.currentTimeMillis());
                        task.setStatus(Task.STATUS_SEND);
                        taskRepository.save(task);
                    }
                    sessionData.getTopicList().forEach(
                            subTopic -> messagingTemplate.convertAndSend(subTopic, JSON.toJSONString(task))
                    );
                    return ResponseDataholder.success();
                } else {
                    return ResponseDataholder.fail(ResponseCode.PARAM_NOT_FORMAT);
                }
            }
            return ResponseDataholder.fail("未找到相关连接信息");
        }
        return ResponseDataholder.fail(ResponseCode.NOT_LOGIN);
    }

}
