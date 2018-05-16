package com.shaw.dolores.dao;

import com.shaw.dolores.bo.Task;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface TaskRepository extends PagingAndSortingRepository<Task, Integer> {
    List<Task> findAllByOwner(int owner);

    int countByOwnerAndStatus(int owner, int status);
}
