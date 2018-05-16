package com.shaw.dolores.controller;

import com.shaw.dolores.bo.Device;
import com.shaw.dolores.bo.Task;
import com.shaw.dolores.bo.User;
import com.shaw.dolores.dao.DeviceRepository;
import com.shaw.dolores.dao.TaskRepository;
import com.shaw.dolores.utils.Constants;
import com.shaw.dolores.vo.SessionData;
import com.shaw.dolores.websocket.SessionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author imn5100
 */
@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private DeviceRepository deviceRepository;
    @Autowired
    private SessionHandler sessionHandler;
    @Autowired
    private TaskRepository taskRepository;

    @RequestMapping(value = "/home", method = RequestMethod.GET)
    public ModelAndView home(@SessionAttribute(name = Constants.HTTP_SESSION_USER) User user) {
        ModelAndView mav = new ModelAndView("home");
        int deviceCount = deviceRepository.countByUserId(user.getId());
        int taskCount = taskRepository.countByOwnerAndStatus(user.getId(), Task.STATUS_SEND);
        List<SessionData> sessionDataList = sessionHandler.getSessionDataByUser(user.getId());
        mav.addObject("active", "home");
        mav.addObject("deviceCount", deviceCount);
        mav.addObject("connectCount", sessionDataList.size());
        mav.addObject("taskCount", taskCount);
        mav.addObject("deviceList", sessionDataList.stream().map(SessionData::getDeviceName).collect(Collectors.toSet()));
        return mav;
    }

    @RequestMapping(value = "/connectedDevices", method = RequestMethod.GET)
    public ModelAndView connectedDevices(@SessionAttribute(name = Constants.HTTP_SESSION_USER) User user) {
        ModelAndView mav = new ModelAndView("connectedDevices");
        List<SessionData> sessionDataList = sessionHandler.getSessionDataByUser(user.getId());
        mav.addObject("active", "connectedDevices");
        mav.addObject("deviceList", sessionDataList.stream().map(SessionData::convert2Vo).collect(Collectors.toSet()));
        mav.addObject("topicList", Task.TOPIC_SET);
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
}
