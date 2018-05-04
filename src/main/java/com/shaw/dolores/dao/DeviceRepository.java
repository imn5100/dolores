package com.shaw.dolores.dao;

import com.shaw.dolores.bo.Device;
import com.shaw.dolores.bo.Meta;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import javax.transaction.Transactional;
import java.util.List;

public interface DeviceRepository extends PagingAndSortingRepository<Device, Integer> {
    List<Device> findAllByUserId(int userId);


    Device findByIdAndUserId(String id, int userId);

    int countByUserId(int userId);

}
