package com.barisd.service;

import com.barisd.dto.request.GetProfileFromTokenRequestDto;
import com.barisd.dto.request.UserProfileSaveRequestDto;
import com.barisd.dto.request.UserProfileUpdateRequestDto;
import com.barisd.dto.response.UserProfileResponseDto;
import com.barisd.exception.ErrorType;
import com.barisd.exception.UserProfileServiceException;
import com.barisd.mapper.IUserProfileMapper;
import com.barisd.repository.entity.UserProfile;
import com.barisd.repository.view.IUserProfileRepository;
import com.barisd.utility.JwtTokenManager;
import com.barisd.utility.ServiceManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserProfileService extends ServiceManager<UserProfile,String> {
    //Constructor Injection:
    private final IUserProfileRepository repository;
    private final JwtTokenManager jwtTokenManager;
    public UserProfileService(IUserProfileRepository repository, JwtTokenManager jwtTokenManager) {
        super(repository);
        this.repository = repository;
        this.jwtTokenManager = jwtTokenManager;
    }

    public Optional<UserProfile> findByAuthid(Long authid){
        return repository.findOptionalByAuthid(authid);

    }

    public Boolean saveDto(UserProfileSaveRequestDto dto) {
        save(IUserProfileMapper.INSTANCE.dtoToUserProfile(dto));
        return true;
    }

    public UserProfileResponseDto getProfileFromToken(GetProfileFromTokenRequestDto dto) {
        /**
         * Kullanıcı token bilgisini gönderecek. jwtTokenManager ile token bilgisini doğrulayıp,
         * içinden authid bilgisini alacağız.
         */

        Optional<Long> authid = jwtTokenManager.decodeToken(dto.getToken());
        if (authid.isEmpty())
            throw new UserProfileServiceException(ErrorType.INVALID_TOKEN);

        Optional<UserProfile> userProfile=  repository.findOptionalByAuthid(authid.get());
        if(userProfile.isEmpty())
            throw new UserProfileServiceException(ErrorType.USER_NOT_FOUND);

        return IUserProfileMapper.INSTANCE.fromUserProfile(userProfile.get());
    }


    public Boolean updateProfile(UserProfileUpdateRequestDto dto) {
        Optional<Long> authid = jwtTokenManager.decodeToken(dto.getToken());
        if (authid.isEmpty())
            throw new UserProfileServiceException(ErrorType.INVALID_TOKEN);

        Optional<UserProfile> userProfile=  repository.findOptionalByAuthid(authid.get());
        if(userProfile.isEmpty())
            throw new UserProfileServiceException(ErrorType.USER_NOT_FOUND);

        UserProfile updatedProfile=userProfile.get();
        updatedProfile.setEmail(dto.getEmail());
        updatedProfile.setPhoto(dto.getPhoto());
        updatedProfile.setPhone(dto.getPhone());
        updatedProfile.setWebsite(dto.getWebsite());

        update(updatedProfile);
        return true;


    }
    /*
    burada uzun sürecek bir işlem thread.sleep ile simule ediliyor.
    buradaki metod girdiye göre hep aynı sonucu üretecek.
     */
@Cacheable(value="GetUpperName", condition = "#name.s")
    public String getUpper(String name){
        try {
            Thread.sleep(3000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return name.toUpperCase();

    }
    @CacheEvict(value = "getUpperName",allEntries = true)
    public void clearCache(){
        System.out.println("Cache temizlendi...");
    }

    @CacheEvict(cacheNames = "getUpperName", key = "#name",beforeInvocation = true)
    public void removeName(String name){
        System.out.println(name+" cacheden silindi");
    }



}