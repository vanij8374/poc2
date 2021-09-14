package com.poc.userservice.service;

import com.poc.userservice.dao.UserDao;
import com.poc.userservice.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    public void createUser(User user){
        userDao.save(user);
    }

    public void updateUser(User user){
        userDao.save(user);
    }

    public List<User> searchUserByName(String name){
        return userDao.findByName(name);
    }

    public List<User> searchUserBySurname(String surname){
        return userDao.findBySurname(surname);
    }

    public List<User> searchUserByPinCode(Integer pincode){
        return userDao.findByPinCode(pincode);
    }

    public void deleteUser(String id){
        userDao.deleteById(id);
    }

    @Transactional
    public void softDeleteUser(String id){
        userDao.softDelete(id);
    }
}
