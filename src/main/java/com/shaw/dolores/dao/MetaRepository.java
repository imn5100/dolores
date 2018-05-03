package com.shaw.dolores.dao;

import com.shaw.dolores.bo.Meta;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import javax.transaction.Transactional;

public interface MetaRepository extends PagingAndSortingRepository<Meta, Integer> {
    @Query("select m from Meta  m where name=?1 and expireTime>?2 and status=0")
    Meta findOneMetaByNameAndValid(String name, long currentTime);

    int countByOwnerAndExpireTimeAfter(int owner, long currentTime);

    @Modifying
    @Transactional
    @Query("update Meta set status=?2 where name =  ?1")
    int updateMetaStatusByName(String name, int status);
}
