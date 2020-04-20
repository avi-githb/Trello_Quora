package com.upgrad.quora.service.business;

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

@Service
public class DeleteAnswerService {
    @Autowired
    private UserDao userDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public String deleteAnswer(final String authorization, final String answerId) throws AnswerNotFoundException, AuthorizationFailedException {

        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserByAuthtoken(authorization); //all user details

        /* An answer can be deleted by Admin or Answer Owner */

        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        if (userAuthTokenEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to delete an answer");
        }


        AnswerEntity answerEntity = userDao.getAnswerFromUuid(answerId); //all attributes of answer

        if (answerEntity == null) {
            throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
        }

        UserEntity userEntity = userDao.getUserFromUuid(userAuthTokenEntity.getUuid());
        UserEntity userEntityFromAnswer = answerEntity.getUser();

        if (!(userEntity.getUuid().equals(userEntityFromAnswer.getUuid())) && !(userEntity.getRole().equals("admin"))) {

            throw new AuthorizationFailedException("ATHR-003", "Only the answer owner or admin can delete the answer");
        }

        String deletedAnswerUuid = answerEntity.getUuid();

        userDao.deleteAnswerFromUuid(deletedAnswerUuid);
        return deletedAnswerUuid; //return deleted Answer's UUid
    }
}
