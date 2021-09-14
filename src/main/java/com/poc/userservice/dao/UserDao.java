package com.poc.userservice.dao;

import com.poc.userservice.model.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDao extends CrudRepository<User,String> {

    @Query(value = "select * from cuser where surname=:surname order by date_of_birth,joining_date ",nativeQuery = true)
    List<User> findBySurname(@Param("surname") String surname);

    @Query(value = "select * from cuser where name=:name order by date_of_birth,joining_date ",nativeQuery = true)
    List<User> findByName(@Param("name") String name);

    @Query(value = "select * from cuser where pin_code=:pinCode order by date_of_birth,joining_date ",nativeQuery = true)
    List<User> findByPinCode(@Param("pinCode") Integer pinCode);

    @Modifying
    @Query(value = "update cuser set is_deleted=1 where id=:id ",nativeQuery = true)
    void softDelete(@Param("id")String id);
}
