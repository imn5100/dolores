package com.shaw.dolores.dao;

import com.shaw.dolores.bo.Meta;
import org.springframework.data.repository.PagingAndSortingRepository;

import javax.transaction.Transactional;

public interface MetaRepository extends PagingAndSortingRepository<Meta, Integer> {
    Meta findMetaByNameAndExpireTimeAfter(String name, long currentTime);

    int countByOwnerAndExpireTimeAfter(int owner, long currentTime);

    @Transactional
    int deleteByName(String name);
}
