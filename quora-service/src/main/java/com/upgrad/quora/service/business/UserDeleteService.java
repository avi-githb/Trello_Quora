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


/**
 * This class is used to delete a user from the Quora Application.
 */
@Service
public class UserDeleteService {

    @Autowired
    private UserDao userDao;

    @Transactional(propagation = Propagation.REQUIRED)

    /**
     * this method delete the records from all the tables related to that user and return 'uuid' of the deleted user from 'users' table
     */
    public String deleteUser(final String authorization, final String userId) throws AuthorizationFailedException, UserNotFoundException {
        UserAuthTokenEntity userByAuthtoken = userDao.getUserByAuthtoken(authorization);

        /**
         * If the access token provided by the user does not exist in the database throw 'AuthorizationFailedException'
         */
        if (userByAuthtoken == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        /**
         * If the user has signed out, throw 'AuthorizationFailedException'
         */
        if (userByAuthtoken.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out");
        }

        /**
         * If the role of the user is 'nonadmin',  throw 'AuthorizationFailedException'
         */
        if (userByAuthtoken.getUser().getRole().equals("nonadmin")) {
            throw new AuthorizationFailedException("ATHR-003", "Unauthorized Access, Entered user is not an admin");
        }

        UserEntity userFromGivenUuid = userDao.getUserFromUuid(userId);

        /**
         * If the user with uuid whose profile is to be deleted does not exist in the database, throw 'UserNotFoundException'
         */
        if (userFromGivenUuid == null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid to be deleted does not exist");
        }

        return userDao.deleteUser(userId);
    }


}
