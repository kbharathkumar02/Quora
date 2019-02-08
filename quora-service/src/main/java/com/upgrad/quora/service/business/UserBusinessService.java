package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.UUID;

@Service
public class UserBusinessService {
    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;

    @Autowired
    private UserDao userDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity signup(UserEntity userEntity) throws SignUpRestrictedException {
        UserEntity userByUserName =  userDao.userByUserName(userEntity.getUserName());
        UserEntity userByEmail =  userDao.userByEmail(userEntity.getEmail());
        if (userByUserName != null) {
            throw new SignUpRestrictedException("SGR-001", "Try any other Username, this Username has already been taken");
        }
        else if (userByEmail != null) {
            throw new SignUpRestrictedException("SGR-002", "This user has already been registered, try with any other emailId");
        }
        return this.createUser(userEntity);
    }

    public UserEntity createUser(final UserEntity userEntity) {
        String password = userEntity.getPassword();
        if (password == null) {
            password = "quora@123";
        }
        String[] encryptedText = cryptographyProvider.encrypt(password);
        userEntity.setSalt(encryptedText[0]);
        userEntity.setPassword(encryptedText[1]);
        return userDao.createUser(userEntity);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthTokenEntity authenticate(final String userName, final String password) throws AuthenticationFailedException {
        UserEntity userEntity = userDao.userByUserName(userName);
        if (userEntity == null) {
            throw new AuthenticationFailedException("ATH-001", "This username does not exist");
        }

        final String encryptedPassword = cryptographyProvider.encrypt(password, userEntity.getSalt());
        if (encryptedPassword.equals(userEntity.getPassword())) {
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
            UserAuthTokenEntity userAuthTokenEntity = new UserAuthTokenEntity();
            userAuthTokenEntity.setUuid(UUID.randomUUID().toString());
            userAuthTokenEntity.setUser(userEntity);
            final ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime expiresAt = now.plusHours(8);
            userAuthTokenEntity.setAccessToken(jwtTokenProvider.generateToken(userEntity.getUuid(), now, expiresAt));

            userAuthTokenEntity.setLoginAt(now);
            userAuthTokenEntity.setExpiresAt(expiresAt);
            userDao.createAuthToken(userAuthTokenEntity);
            return userAuthTokenEntity;
        } else {
            throw new AuthenticationFailedException("ATH-002", "Password failed");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity signout(final String authorization) throws SignOutRestrictedException {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getAuthToken(authorization);
        if(userAuthTokenEntity != null && userAuthTokenEntity.getUuid() != null){
            UserEntity signoutUser = userAuthTokenEntity.getUser();
            if(signoutUser != null && signoutUser.getUuid() != null) {
                //UserEntity userEntity = userDao.getUser(signoutUser.getUuid());
                //if (userEntity != null) {
                final ZonedDateTime now = ZonedDateTime.now();
                userAuthTokenEntity.setLogoutAt(now);
                userDao.updateUserAuthToken(userAuthTokenEntity);
                //}
                return signoutUser;
            }
            throw new SignOutRestrictedException("SGR-001", "User is not Signed in");
        }
        throw new SignOutRestrictedException("SGR-001", "User is not Signed in");
    }
}
