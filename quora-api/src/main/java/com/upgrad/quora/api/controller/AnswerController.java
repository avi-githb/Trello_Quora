package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.AnswerRequest;
import com.upgrad.quora.api.model.AnswerResponse;
import com.upgrad.quora.service.business.CreateAnswerService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")

public class AnswerController {
    @Autowired
    private CreateAnswerService createAnswerService;

    @RequestMapping(method = RequestMethod.POST, path = "/question/{questionId}/answer/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> createAnswer(@RequestHeader("authorization") final String authorization, final AnswerRequest answerRequest, @PathVariable("questionId") final String questionId) throws AuthorizationFailedException, InvalidQuestionException {

       AnswerEntity answerEntity = new AnswerEntity();
       answerEntity.setAnswer(answerRequest.getAnswer());
       answerEntity=createAnswerService.createAnswer(authorization,answerEntity,questionId);
       AnswerResponse answerResponse = new AnswerResponse().id(answerEntity.getUuid()).status("ANSWER CREATED");
       return new ResponseEntity<AnswerResponse>(answerResponse, HttpStatus.CREATED);
    }
}
