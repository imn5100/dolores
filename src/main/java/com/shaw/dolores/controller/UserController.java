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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author imn5100
 */
@Controller
@RequestMapping("/user")
public class UserController {
    @Value("${dolores.subscribe.prefix}")
    private String subPrefix;
    @Autowired
    private MetaRepository metaRepository;
    @Autowired
    private DeviceRepository deviceRepository;
    @Autowired
    private SessionHandler sessionHandler;

    @RequestMapping(value = "/home", method = RequestMethod.GET)
    public ModelAndView home(@SessionAttribute(name = Constants.HTTP_SESSION_USER) User user) {
        ModelAndView mav = new ModelAndView("home");
        int deviceCount = deviceRepository.countByUserId(user.getId());
        List<SessionData> sessionDataList = sessionHandler.getSessionDataByUser(user.getId());
        mav.addObject("active", "home");
        mav.addObject("deviceCount", deviceCount);
        mav.addObject("connectCount", sessionDataList.size());
        mav.addObject("deviceList", sessionDataList.stream().map(SessionData::getDeviceName).collect(Collectors.toSet()));
        return mav;
    }

    @RequestMapping(value = "/devices", method = RequestMethod.GET)
    public ModelAndView devices(@SessionAttribute(name = Constants.HTTP_SESSION_USER) User user) {
        ModelAndView mav = new ModelAndView("devices");
        List<Device> devices = deviceRepository.findAllByUserId(user.getId());
        mav.addObject("active", "devices");
        mav.addObject("devices", devices.stream().map(device -> device.convertToVo(user)).collect(Collectors.toList()));
        return mav;
    }


    @RequestMapping(value = "/devices/saveOrUpdate", method = RequestMethod.POST)
    @ResponseBody
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

    @RequestMapping(value = "/devices/delete", method = RequestMethod.POST)
    @ResponseBody
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
    @ResponseBody
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


}
