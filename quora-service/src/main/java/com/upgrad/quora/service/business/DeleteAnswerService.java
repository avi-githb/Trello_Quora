package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class is used to delete a answer that has been posted by a user. Note, only the answer owner of the answer or admin can delete a answer.
 */

@Service
public class DeleteAnswerService {
    @Autowired
    private UserDao userDao;


    @Autowired
    private AnswerDao answerDao;

    @Autowired
    private QuestionDao questionDao;

    @Transactional(propagation = Propagation.REQUIRED)

    /**
     * This method delete the answer from the database and return "uuid" of the deleted answer.
     */

    public String deleteAnswer(final String authorization, final String answerId) throws AnswerNotFoundException, AuthorizationFailedException {

        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserByAuthtoken(authorization); //all user details

        /**
         * If the access token provided by the user does not exist in the database throw 'AuthorizationFailedException'
         */

        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        /**
         * If the user has signed out, throw 'AuthorizationFailedException'
         */

        if (userAuthTokenEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to delete an answer");
        }


        AnswerEntity answerEntity = answerDao.getAnswerFromUuid(answerId); //all attributes of answer

        /**
         * If the answer with uuid which is to be deleted does not exist in the database, throw "AnswerNotFoundException"
         */

        if (answerEntity == null) {
            throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
        }

        UserEntity userEntity = userDao.getUserFromUuid(userAuthTokenEntity.getUuid());
        UserEntity userEntityFromAnswer = answerEntity.getUser();

        /**
         * if the user who is not the owner of the answer or the role of the user is ‘nonadmin’ and
         tries to delete the answer throw "AuthorizationFailedException"
         */

        if (!(userEntity.getUuid().equals(userEntityFromAnswer.getUuid())) && !(userEntity.getRole().equals("admin"))) {

            throw new AuthorizationFailedException("ATHR-003", "Only the answer owner or admin can delete the answer");
        }

        String deletedAnswerUuid = answerEntity.getUuid();

        answerDao.deleteAnswerFromUuid(deletedAnswerUuid);
        return deletedAnswerUuid; //return deleted Answer's UUid
    }
}
