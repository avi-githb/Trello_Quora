package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EditQuestionContentService {

    @Autowired
    private UserDao userDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity editQuestion(final String authorization,final QuestionEntity questionEntity,final String questionId) throws AuthorizationFailedException, InvalidQuestionException {

        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserByAuthtoken(authorization);

        if(userAuthTokenEntity == null){
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }
        if(userAuthTokenEntity.getLogoutAt()!=null){
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to edit the question");
        }

        QuestionEntity  questionWhichNeedsToBeEdited = userDao.getQuestionFromUuid(questionId);


        if(questionWhichNeedsToBeEdited ==null){
            throw new InvalidQuestionException("QUES-001","Entered question uuid does not exist");
        }

        long id1 = questionWhichNeedsToBeEdited.getUser().getId();
        long id2 = userAuthTokenEntity.getUser().getId();

        if(id1!=id2){
            throw new AuthorizationFailedException("ATHR-003","Only the question owner can edit the question");
        }

        questionWhichNeedsToBeEdited.setContent(null);
        questionWhichNeedsToBeEdited.setContent(questionEntity.getContent());

        return questionWhichNeedsToBeEdited;
    }

}
