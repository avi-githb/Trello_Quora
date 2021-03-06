package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

/**
 * This class is used to sign out from the Quora Application. The user cannot access any other endpoint once he is signed out of the application.
 */

@Service
public class SignOutService {
    @Autowired
    private UserDao userDao;

    @Transactional(propagation = Propagation.REQUIRED)

    /**
     * If the access token provided by the user is valid, update the LogoutAt time of the user in the database
     */
    public UserAuthTokenEntity verifyAuth(final String authorization) throws SignOutRestrictedException {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserByAuthtoken(authorization);

        /**
         * If the access token provided by the user does not exist in the database, throw 'SignOutRestrictedException'
         */
        if (userAuthTokenEntity == null) {
            throw new SignOutRestrictedException("SGR-001", "User is not Signed in");
        }

        userAuthTokenEntity.setLogoutAt(ZonedDateTime.now());
        userDao.createAuthToken(userAuthTokenEntity);
        return userAuthTokenEntity;
    }
}

