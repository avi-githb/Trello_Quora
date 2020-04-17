package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDeleteService {

    @Autowired
    private UserDao userDao;
    @Transactional(propagation = Propagation.REQUIRED)
    public String deleteUser(final String authorization,final String userId) throws AuthorizationFailedException, UserNotFoundException {
        UserAuthTokenEntity userByAuthtoken = userDao.getUserByAuthtoken(authorization);

        if(userByAuthtoken==null){
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }

        if(userByAuthtoken.getLogoutAt()!=null){
            throw new AuthorizationFailedException("ATHR-002","User is signed out");
        }
        if(userByAuthtoken.getUser().getRole().equals("nonadmin")){
            throw new AuthorizationFailedException("ATHR-003","Unauthorized Access, Entered user is not an admin");
        }

        UserEntity userFromGivenUuid = userDao.getUserFromUuid(userId);
        if(userFromGivenUuid==null){
            throw new UserNotFoundException("USR-001","User with entered uuid to be deleted does not exist");
        }

        return userDao.deleteUser(userId);
    }


}
