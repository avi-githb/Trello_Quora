package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.AnswerEntity;

import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class is used to edit an answer. Only the owner of the answer can edit the answer.
 */

@Service
public class EditAnswerService {
    @Autowired
    private UserDao userDao;


    @Autowired
    private AnswerDao answerDao;

    @Autowired
    private QuestionDao questionDao;


    @Transactional(propagation = Propagation.REQUIRED)

    /**
     *this method is used to edit an already created answer.
     */
    public AnswerEntity editAnswer(final String authorization, final AnswerEntity answerEntity, final String answerId) throws AuthorizationFailedException, AnswerNotFoundException {

        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserByAuthtoken(authorization);

        /**
         *If the access token provided by the user does not exist in the database throw "AuthorizationFailedException"
         */
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        /**
         *If the user has signed out, throw "AuthorizationFailedException"
         */
        if (userAuthTokenEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to edit an answer");
        }

        AnswerEntity answerToBeEdited = answerDao.getAnswerFromUuid(answerId);

        /**
         *If the answer with uuid which is to be edited does not exist in the database, throw "AnswerNotFoundException"
         */
        if (answerToBeEdited == null) {
            throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
        }

        long id1 = answerToBeEdited.getUser().getId();
        long id2 = userAuthTokenEntity.getUser().getId();

        /**
         *if the user who is not the owner of the answer tries to edit the answer throw "AuthorizationFailedException"
         */
        if (id1 != id2) {
            throw new AuthorizationFailedException("ATHR-003", "Only the answer owner can edit the answer");
        }

        answerToBeEdited.setAnswer(null);
        answerToBeEdited.setAnswer(answerEntity.getAnswer());

        return answerToBeEdited;
    }
}
