package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.QuestionDao;
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

/**
 * this class is used to fetch all the questions posed by a specific user. Any user can access this endpoint.
 */

@Service
public class GetAllQuestionByUserSerivce {

    @Autowired
    private UserDao userDao;


    @Autowired
    private AnswerDao answerDao;

    @Autowired
    private QuestionDao questionDao;


    @Transactional(propagation = Propagation.REQUIRED)

    /**
     * this method return 'uuid' and 'content' of all the questions posed by the corresponding user

     */
    public List<QuestionEntity> getAllQuestionByUser(final String userId, final String accessToken) throws AuthenticationFailedException, AuthorizationFailedException, UserNotFoundException {


        /**
         * If the access token provided by the user does not exist in the database throw 'AuthorizationFailedException'
         */
        if (userDao.getUserByAuthtoken(accessToken) == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserByAuthtoken(accessToken);

        /**
         * If the user has signed out, throw 'AuthorizationFailedException'
         */
        if (userAuthTokenEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions posted by a specific user");
        }

        /**
         * If the user with uuid whose questions are to be retrieved from the database does not exist in the database, throw 'UserNotFoundException'
         */

        if (userDao.getUserFromUuid(userId) == null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid whose question details are to be seen does not exist");
        }

        UserEntity userEntity = userDao.getUserFromUuid(userId); //userId = UUID

        List<QuestionEntity> allQuestionFromUserId = questionDao.getQuestionFromId(userEntity); //userEntity = user

        return allQuestionFromUserId;
    }

}
