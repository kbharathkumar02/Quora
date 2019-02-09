package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserAuthTokenDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class QuestionBusinessService {

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private UserAuthTokenDao userAuthTokenDao;

    @Autowired
    private UserDao userDao;

    /**
     * retrieves the user auth token
     *
     * @param authorizationToken
     * @return
     */
    public UserAuthTokenEntity getUserAuthToken(final String authorizationToken) {
        UserAuthTokenEntity userAuthTokenEntity = null;
        if (authorizationToken != null && !authorizationToken.isEmpty()) {
            String[] bearer = authorizationToken.split("Bearer ");
            if (bearer != null && bearer.length > 1) {
                return userAuthTokenDao.getAuthToken(bearer[1]);
            }
        }
        return userAuthTokenEntity;
    }

    /**
     * validates if the user is signed in
     *
     * @param userAuthTokenEntity
     * @return
     */
    public boolean isUserSignedIn(UserAuthTokenEntity userAuthTokenEntity) {
        boolean isUserSignedIn = false;
        if (userAuthTokenEntity != null && userAuthTokenEntity.getExpiresAt() != null && ZonedDateTime.now().isBefore(userAuthTokenEntity.getExpiresAt())) {
            if ((userAuthTokenEntity.getLogoutAt() == null) ||
                    (userAuthTokenEntity.getLogoutAt() != null && ZonedDateTime.now().isBefore(userAuthTokenEntity.getLogoutAt()))) {
                isUserSignedIn = true;
            }
        }
        return isUserSignedIn;
    }

    /**
     * this method creates question
     *
     * @param questionEntity
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(QuestionEntity questionEntity) {
        return questionDao.createQuestion(questionEntity);
    }

    /**
     * retrieves all the questions
     *
     * @return
     */
    public List<QuestionEntity> getAllQuestions() {
        return questionDao.getAllQuestions();
    }

    /**
     * retrieves user for question id
     *
     * @param uuid
     * @return
     */
    public QuestionEntity getUserForQuestionId(String uuid) {
        return questionDao.getUserForQuestionId(uuid);
    }

    public List<QuestionEntity> getQuestionsForUserId(Integer userId) {
        return questionDao.getQuestionsForUserId(userId);
    }

    /**
     * This method identifies if the user is the owners of the question
     *
     * @param user
     * @param questionOwner
     * @return
     */
    public boolean isUserQuestionOwner(UserEntity user, UserEntity questionOwner) {
        boolean isUserQuestionOwner = false;
        if (user != null && questionOwner != null && user.getUuid() != null && !user.getUuid().isEmpty()
                && questionOwner.getUuid() != null && !questionOwner.getUuid().isEmpty()) {
            if (user.getUuid().equals(questionOwner.getUuid())) {
                isUserQuestionOwner = true;
            }
        }
        return isUserQuestionOwner;
    }

    /**
     * this methoed updates the question for passed in questionEntity object in DB
     *
     * @param questionEntity
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateQuestion(QuestionEntity questionEntity) {
        questionDao.updateQuestion(questionEntity);
    }

    /**
     * checks if the user is an admin
     *
     * @param user
     * @return
     */
    public boolean isUserAdmin(UserEntity user) {
        boolean isUserAdmin = false;
        if (user != null && "admin".equals(user.getRole())) {
            isUserAdmin = true;
        }
        return isUserAdmin;
    }

    /**
     * This method deletes the question for question entity
     * * @param questionEntity
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteQuestion(QuestionEntity questionEntity) {
        questionDao.deleteQuestion(questionEntity);
    }

    /**
     * This method gest the user for user id
     *
     * @param userUuid
     * @return
     */
    public UserEntity getUserForUserId(String userUuid) {
        return userDao.getUser(userUuid);
    }

    public QuestionEntity performCreateQuestion(final String authorizationToken, String QuestionContent) throws AuthorizationFailedException {
        UserAuthTokenEntity userAuthTokenEntity = getUserAuthToken(authorizationToken);
        QuestionEntity questionEntity = new QuestionEntity();
        if (userAuthTokenEntity != null) {
            if (isUserSignedIn(userAuthTokenEntity)) {
                questionEntity.setDate(ZonedDateTime.now());
                questionEntity.setContent(QuestionContent);
                questionEntity.setUuid(UUID.randomUUID().toString());
                questionEntity.setUser(userAuthTokenEntity.getUser());
                questionEntity = createQuestion(questionEntity);
            } else {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post a question");
            }
        } else {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        return questionEntity;
    }

    /**
     * This method performs the business logic reuired to get all questions
     *
     * @param authorizationToken
     * @return
     * @throws AuthorizationFailedException
     */
    public List<QuestionEntity> performGetAllQuestions(final String authorizationToken) throws AuthorizationFailedException {

        UserAuthTokenEntity userAuthTokenEntity = getUserAuthToken(authorizationToken);
        List<QuestionEntity> questionEntityList = new ArrayList<QuestionEntity>();
        if (userAuthTokenEntity != null) {
            if (isUserSignedIn(userAuthTokenEntity)) {
                questionEntityList = getAllQuestions();
            } else {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions");
            }
        } else {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        return questionEntityList;

    }

    /**
     * This method performs the buisness logic required to edit question
     *
     * @param authorizationToken
     * @param questionId
     * @param questionContent
     * @return
     * @throws AuthorizationFailedException
     * @throws InvalidQuestionException
     */
    public QuestionEntity performEditQuestionContent(final String authorizationToken,
                                                     final String questionId, String questionContent)
            throws AuthorizationFailedException, InvalidQuestionException {
        QuestionEntity questionEntity = new QuestionEntity();
        UserAuthTokenEntity userAuthTokenEntity = getUserAuthToken(authorizationToken);
        if (userAuthTokenEntity != null) {
            if (isUserSignedIn(userAuthTokenEntity)) {
                questionEntity = getUserForQuestionId(questionId);
                if (questionEntity != null) {
                    if (isUserQuestionOwner(userAuthTokenEntity.getUser(), questionEntity.getUser())) {
                        questionEntity.setContent(questionContent);
                        updateQuestion(questionEntity);
                    } else {
                        throw new AuthorizationFailedException("ATHR-003", "Only the question owner can edit the question");
                    }
                } else {
                    throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
                }
            } else {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to edit the question");
            }
        } else {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        return questionEntity;
    }

    /**
     * This method performs the business logic required to delete question
     *
     * @param authorizationToken
     * @param questionId
     * @throws AuthorizationFailedException
     * @throws InvalidQuestionException
     */
    public void performDeleteQuestion(final String authorizationToken,
                                      final String questionId)
            throws AuthorizationFailedException, InvalidQuestionException {
        UserAuthTokenEntity userAuthTokenEntity = getUserAuthToken(authorizationToken);
        if (userAuthTokenEntity != null) {
            if (isUserSignedIn(userAuthTokenEntity)) {
                QuestionEntity questionEntity = getUserForQuestionId(questionId);
                if (questionEntity != null) {
                    if (isUserQuestionOwner(userAuthTokenEntity.getUser(), questionEntity.getUser())
                            || isUserAdmin(userAuthTokenEntity.getUser())) {
                        deleteQuestion(questionEntity);
                    } else {
                        throw new AuthorizationFailedException("ATHR-003", "Only the question owner or admin can delete the question");
                    }
                } else {
                    throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
                }
            } else {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to delete a question");
            }
        } else {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
    }

    /**
     * This method performs the business logic required to retrieve questions bya user
     *
     * @param authorizationToken
     * @param userUuId
     * @return
     * @throws AuthorizationFailedException
     * @throws UserNotFoundException
     */
    public List<QuestionEntity> performGetAllQuestionsByUser(final String authorizationToken,
                                                             @PathVariable("userId") final String userUuId) throws AuthorizationFailedException,
            UserNotFoundException {
        List<QuestionEntity> questionEntityList = new ArrayList<QuestionEntity>();
        UserEntity userEntity = getUserForUserId(userUuId);
        if (userEntity != null) {
            UserAuthTokenEntity userAuthTokenEntity = getUserAuthToken(authorizationToken);
            if (userAuthTokenEntity != null) {
                if (isUserSignedIn(userAuthTokenEntity)) {
                    if (userEntity != null) {
                        questionEntityList = getQuestionsForUserId(userEntity.getId());
                    }
                } else {
                    throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions posted by a specific user");
                }
            } else {
                throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
            }

        } else {

            throw new UserNotFoundException("USR-001", "User with entered uuid whose question details are to be seen does not exist");

        }
        return questionEntityList;

    }

}
