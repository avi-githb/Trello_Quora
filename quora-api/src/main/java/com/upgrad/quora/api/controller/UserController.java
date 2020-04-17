package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.SigninResponse;
import com.upgrad.quora.api.model.SignoutResponse;
import com.upgrad.quora.api.model.SignupUserRequest;
import com.upgrad.quora.api.model.SignupUserResponse;
import com.upgrad.quora.service.business.AuthenticationService;
import com.upgrad.quora.service.business.SignOutService;
import com.upgrad.quora.service.business.SignupBusinessService;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.UUID;

/**
 * All the features of User Controller are implemented in this class, SignUp,SignIn and SignOut
 */

@RestController
@RequestMapping("/")
public class UserController {

    /**
     * Method used for a new user registration, by default any user created is a nonadmin user.
     */

    @Autowired
    private SignupBusinessService signupBusinessService;

    @RequestMapping(method = RequestMethod.POST, path = "/user/signup", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupUserResponse> signup(final SignupUserRequest signupUserRequest) throws SignUpRestrictedException {

        final UserEntity userEntity = new UserEntity();
        userEntity.setUuid(UUID.randomUUID().toString());
        userEntity.setFirstName(signupUserRequest.getFirstName());
        userEntity.setLastName(signupUserRequest.getLastName());
        userEntity.setUserName(signupUserRequest.getUserName());
        userEntity.setEmailAddress(signupUserRequest.getEmailAddress());
        userEntity.setPassword(signupUserRequest.getPassword());
        userEntity.setCountry(signupUserRequest.getCountry());
        userEntity.setAboutMe(signupUserRequest.getAboutMe());
        userEntity.setDob(signupUserRequest.getDob());
        userEntity.setContactNumber(signupUserRequest.getContactNumber());
        userEntity.setRole("nonadmin");
        userEntity.setSalt("1234abc");

        final UserEntity createdUserEntity = signupBusinessService.signup(userEntity);
        SignupUserResponse userResponse = new SignupUserResponse().id(createdUserEntity.getUuid()).status("USER SUCCESSFULLY REGISTERED");
        return new ResponseEntity<SignupUserResponse>(userResponse, HttpStatus.CREATED);
    }

    /**
     * Method used to SignIn, user have to provide the Username and Password in Base64 in the following format : Basic Username:Password
     */

    @Autowired
    private AuthenticationService authenticationService;

    @RequestMapping(method = RequestMethod.POST, path = "/user/signin", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SigninResponse> signin(@RequestHeader("authorization") final String authorization) throws AuthenticationFailedException {
        byte[] decode = Base64.getDecoder().decode(authorization);
        String decodedText = new String(decode);
        String[] decodeLevel1 = decodedText.split("Basic ");

        String decodeLevel2 = new String(decodeLevel1[1]);
        String[] decodedTextArray = decodeLevel2.split(":");

        /**
         * decodedTextArray[0] = UserName
         * decodedTextArray[1] = Password
         */
        UserAuthTokenEntity userAuthToken = authenticationService.auth(decodedTextArray[0], decodedTextArray[1]);

        UserEntity user = userAuthToken.getUser();

        SigninResponse signinResponse = new SigninResponse().id(user.getUuid()).message("SIGNED IN SUCCESSFULLY");

        HttpHeaders headers = new HttpHeaders();
        headers.add("access-token", userAuthToken.getAccessToken());
        return new ResponseEntity<SigninResponse>(signinResponse, headers, HttpStatus.OK);
    }

    /**
     * Method used to SignOut a user, User have to provide AccessToken in order to SignOut.
     */

    @Autowired
    private SignOutService signOutService;

    @RequestMapping(method = RequestMethod.POST, path = "/user/signout", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignoutResponse> signout(@RequestHeader("authorization") final String authorization) throws SignOutRestrictedException {

        UserAuthTokenEntity userAuthTokenEntity = signOutService.verifyAuth(authorization);

        SignoutResponse signoutResponse = new SignoutResponse().id(userAuthTokenEntity.getUuid()).message("SIGNED OUT SUCCESSFULLY");
        return new ResponseEntity<SignoutResponse>(signoutResponse, HttpStatus.OK);
    }
}