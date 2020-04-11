package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GetAllQuestionByUserSerivce {

    @Autowired
    private UserDao userDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public List<QuestionEntity> getAllQuestionByUser(final String userId, final String accessToken) throws AuthenticationFailedException, AuthorizationFailedException, UserNotFoundException {

        if(userDao.getUserByAuthtoken(accessToken)==null){
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserByAuthtoken(accessToken);

        if(userAuthTokenEntity.getLogoutAt()!=null){
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to get all questions posted by a specific user");
        }

        if(userDao.getUserFromUuid(userId)==null){
            throw new UserNotFoundException("USR-001","User with entered uuid whose question details are to be seen does not exist");
        }

        UserEntity userEntity = userDao.getUserFromUuid(userId); //userId = UUID

        List<QuestionEntity> allQuestionFromUserId = userDao.getQuestionFromId(userEntity); //getId = Internal UserID

        return allQuestionFromUserId;
    }

}
