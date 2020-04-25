package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class is used to edit a question that has been posted by a user. Note, only the owner of the question can edit the question.
 */

@Service
public class EditQuestionContentService {

    @Autowired
    private UserDao userDao;


    @Autowired
    private AnswerDao answerDao;

    @Autowired
    private QuestionDao questionDao;


    @Transactional(propagation = Propagation.REQUIRED)

    /**
     * this method edit the question in the database
     */

    public QuestionEntity editQuestion(final String authorization, final QuestionEntity questionEntity, final String questionId) throws AuthorizationFailedException, InvalidQuestionException {

        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserByAuthtoken(authorization);

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
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to edit the question");
        }

        QuestionEntity questionWhichNeedsToBeEdited = questionDao.getQuestionFromUuid(questionId);

        /**
         * If the question with uuid which is to be edited does not exist in the database, throw 'InvalidQuestionException'
         */
        if (questionWhichNeedsToBeEdited == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }

        long id1 = questionWhichNeedsToBeEdited.getUser().getId();
        long id2 = userAuthTokenEntity.getUser().getId();

        /**
         * if the user who is not the owner of the question tries to edit the question throw "AuthorizationFailedException"
         */
        if (id1 != id2) {
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner can edit the question");
        }

        questionWhichNeedsToBeEdited.setContent(null);
        questionWhichNeedsToBeEdited.setContent(questionEntity.getContent());

        return questionWhichNeedsToBeEdited;
    }

}
