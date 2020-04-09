package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.QuestionDetailsResponse;
import com.upgrad.quora.api.model.QuestionRequest;
import com.upgrad.quora.api.model.QuestionResponse;
import com.upgrad.quora.service.business.CreateQuestionService;
import com.upgrad.quora.service.business.GetAllQuestionsService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
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

    @RequestMapping(method = RequestMethod.GET, path = "/question/all",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestions(@RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {

        List<QuestionEntity> allQuestion = getAllQuestionsService.getAllQuestion(authorization);
        QuestionEntity temp = new QuestionEntity();
        List<QuestionDetailsResponse> displayAllQuestions = new ArrayList<>();
        for (int i = 0; i < allQuestion.size(); i++){
            temp = allQuestion.get(i);
        QuestionDetailsResponse questionDetailsResponse = new QuestionDetailsResponse().id(temp.getUuid()).content(temp.getContent());
        displayAllQuestions.add(questionDetailsResponse);
    }
            return new ResponseEntity<List<QuestionDetailsResponse>>(displayAllQuestions, HttpStatus.OK);

    }
}
