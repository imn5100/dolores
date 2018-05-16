package com.shaw.dolores.dao;

import com.shaw.dolores.bo.Task;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import javax.transaction.Transactional;
import java.util.List;

public interface TaskRepository extends PagingAndSortingRepository<Task, Integer> {
    List<Task> findAllByOwner(int owner);

    int countByOwnerAndStatus(int owner, int status);

    @Modifying
    @Transactional
    @Query("update Task set status=?2 where id =  ?1")
    int updateTaskStatusById(Integer id, int status);
}
