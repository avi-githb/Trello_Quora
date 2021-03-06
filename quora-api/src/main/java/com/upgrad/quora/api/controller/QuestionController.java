package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.*;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * All the Features of Question Controller are implemented in this class
 */
@RestController
@RequestMapping("/")
public class QuestionController {

    /**
     * Method to create a Question
     */

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

    /**
     * Method to get All Questions provided the authtoken is valid
     */

    @Autowired
    private GetAllQuestionsService getAllQuestionsService;

    @RequestMapping(method = RequestMethod.GET, path = "/question/all", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestions(@RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {

        List<QuestionEntity> allQuestion = getAllQuestionsService.getAllQuestion(authorization);
        return getListResponseEntity(allQuestion);

    }

    private ResponseEntity<List<QuestionDetailsResponse>> getListResponseEntity(List<QuestionEntity> allQuestion) {
        QuestionEntity questionEntity;
        List<QuestionDetailsResponse> displayAllQuestions = new ArrayList<>();
        for (int i = 0; i < allQuestion.size(); i++) {
            questionEntity = allQuestion.get(i);
            QuestionDetailsResponse questionDetailsResponse = new QuestionDetailsResponse().id(questionEntity.getUuid()).content(questionEntity.getContent());
            displayAllQuestions.add(questionDetailsResponse);
        }
        return new ResponseEntity<List<QuestionDetailsResponse>>(displayAllQuestions, HttpStatus.OK);
    }

    /**
     * Method to edit a question, only question owner can edit the question
     */

    @Autowired
    private EditQuestionContentService editQuestionContentService;

    @RequestMapping(method = RequestMethod.PUT, path = "/question/edit/{questionId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionEditResponse> editQuestionContent(@RequestHeader("authorization") final String authorization, @PathVariable("questionId") final String questionId, final QuestionEditRequest questionEditRequest) throws AuthorizationFailedException, InvalidQuestionException {
        QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setContent(questionEditRequest.getContent());


        QuestionEntity editedQuestion = editQuestionContentService.editQuestion(authorization, questionEntity, questionId);

        QuestionEditResponse questionEditResponse = new QuestionEditResponse().id(editedQuestion.getUuid()).status("QUESTION EDITED");
        return new ResponseEntity<QuestionEditResponse>(questionEditResponse, HttpStatus.OK);
    }

    /**
     * method to get all question created by a specific user
     */

    @Autowired
    private GetAllQuestionByUserSerivce getAllQuestionByUserSerivce;

    @RequestMapping(method = RequestMethod.GET, path = "/question/all/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestionByUser(@RequestHeader("authorization") final String authorization, @PathVariable("userId") final String userId) throws AuthenticationFailedException, AuthorizationFailedException, UserNotFoundException {

        List<QuestionEntity> allQuestionByUser = getAllQuestionByUserSerivce.getAllQuestionByUser(userId, authorization);

        return getListResponseEntity(allQuestionByUser);

    }

    /**
     * method to delete a question, only question owner or admin user can delete the question
     */

    @Autowired
    private DeleteQuestionService deleteQuestionService;

    @RequestMapping(method = RequestMethod.DELETE, path = "question/delete/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionDeleteResponse> deleteQuestion(@RequestHeader("authorization") final String authorization, @PathVariable("questionId") final String questionId) throws AuthorizationFailedException, InvalidQuestionException {

        String deletedQuestionUuid = deleteQuestionService.deleteQuestion(authorization, questionId);

        QuestionDeleteResponse questionDeleteResponse = new QuestionDeleteResponse().id(deletedQuestionUuid).status("QUESTION DELETED");
        return new ResponseEntity<QuestionDeleteResponse>(questionDeleteResponse, HttpStatus.OK);
    }


}


