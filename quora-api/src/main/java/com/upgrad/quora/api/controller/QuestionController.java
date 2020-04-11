package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.CreateQuestionService;
import com.upgrad.quora.service.business.EditQuestionContentService;
import com.upgrad.quora.service.business.GetAllQuestionByUserSerivce;
import com.upgrad.quora.service.business.GetAllQuestionsService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/")
public class QuestionController {

    @Autowired
    private CreateQuestionService createQuestionService;

    @RequestMapping(method = RequestMethod.POST, path = "/question/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> create(@RequestHeader("authorization") final String authorization, final QuestionRequest questionRequest) throws AuthorizationFailedException {
        QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setContent(questionRequest.getContent());
        questionEntity = createQuestionService.createQuestion(authorization, questionEntity);

        QuestionResponse questionResponse = new QuestionResponse().id(questionEntity.getUuid()).status("QUESTION CREATED");
        return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.CREATED);
    }



    @Autowired
    private GetAllQuestionsService getAllQuestionsService;

    @RequestMapping(method = RequestMethod.GET, path = "/question/all", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestions(@RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {

        List<QuestionEntity> allQuestion = getAllQuestionsService.getAllQuestion(authorization);
        QuestionEntity temp = new QuestionEntity();
        List<QuestionDetailsResponse> displayAllQuestions = new ArrayList<>();
        for (int i = 0; i < allQuestion.size(); i++) {
            temp = allQuestion.get(i);
            QuestionDetailsResponse questionDetailsResponse = new QuestionDetailsResponse().id(temp.getUuid()).content(temp.getContent());
            displayAllQuestions.add(questionDetailsResponse);
        }
        return new ResponseEntity<List<QuestionDetailsResponse>>(displayAllQuestions, HttpStatus.OK);

    }

    @Autowired
    private EditQuestionContentService editQuestionContentService;

    @RequestMapping(method = RequestMethod.PUT, path = "/question/edit/{questionId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionEditResponse> editQuestionContent(@RequestHeader ("authorization") final String authorization, final QuestionEditRequest questionEditRequest, final String questionId) throws AuthorizationFailedException, InvalidQuestionException {
        QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setContent(questionEditRequest.getContent());


        QuestionEntity editedQuestion = editQuestionContentService.editQuestion(authorization, questionEntity, questionId);

        QuestionEditResponse questionEditResponse = new QuestionEditResponse().id(editedQuestion.getUuid()).status("QUESTION EDITED");
        return new ResponseEntity<QuestionEditResponse>(questionEditResponse,HttpStatus.OK);
    }

    @Autowired
    private GetAllQuestionByUserSerivce getAllQuestionByUserSerivce;

    @RequestMapping(method = RequestMethod.GET, path = "/question/all/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestionByUser(@RequestHeader ("authorization") final String authorization,final String userId) throws AuthenticationFailedException, AuthorizationFailedException, UserNotFoundException {

        List<QuestionEntity> allQuestionByUser = getAllQuestionByUserSerivce.getAllQuestionByUser(userId, authorization);

        QuestionEntity temp1 = new QuestionEntity();
        List<QuestionDetailsResponse> displayAllQuestionsByUser = new ArrayList<>();
        for (int i = 0; i < allQuestionByUser.size(); i++) {
            temp1 = allQuestionByUser.get(i);
            QuestionDetailsResponse questionDetailsResponse = new QuestionDetailsResponse().id(temp1.getUuid()).content(temp1.getContent());
            displayAllQuestionsByUser.add(questionDetailsResponse);
        }
        return new ResponseEntity<List<QuestionDetailsResponse>>(displayAllQuestionsByUser, HttpStatus.OK);

    }


}


