package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service
public class UserBusinessService {

    @Autowired
    private UserDao userDao;

    public UserEntity getUserByUuId(final String userUuid,String authToken) throws UserNotFoundException, AuthorizationFailedException {

        UserEntity userEntity=userDao.getUserByUuId(userUuid);
        UserAuthTokenEntity userAuthTokenEntity = verifyAuth(authToken);

        /* Now we have to check 3 conditions, as below*/

        /* Case 1:- User and Auth Token both not exist in database, return "User has not signed in" with code 'ATHR-001' */

        if((userEntity == null) && (userAuthTokenEntity==null))
        {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");

        }
        /* Case 2:- User exists but Auth token does not exist in database, return "User is signed out.Sign in first to get user details" with code 'ATHR-002' */

        else if((userEntity != null) && (userAuthTokenEntity==null))
        {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get user details");
        }

        /* Case 3:- User does not exist in database, return "User with entered uuid does not exist" with code 'USR-001' */

         else if(userEntity == null){

            throw new UserNotFoundException("USR-001", "User with entered uuid does not exist");
        }
       return userEntity;
    }

    public UserAuthTokenEntity verifyAuth(final String authToken) throws AuthorizationFailedException {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserByAuthtoken(authToken);
        return userAuthTokenEntity;
    }




}
