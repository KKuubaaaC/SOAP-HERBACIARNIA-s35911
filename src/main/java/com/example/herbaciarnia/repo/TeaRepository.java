package com.example.herbaciarnia.repo;

import com.example.herbaciarnia.model.Tea;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TeaRepository extends CrudRepository<Tea, Long> {

    @Override
    List<Tea> findAll();
}
