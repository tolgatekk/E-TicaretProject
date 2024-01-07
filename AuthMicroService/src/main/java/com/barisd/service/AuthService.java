package com.barisd.service;

import com.barisd.dto.request.DoLoginRequestDto;
import com.barisd.dto.request.RegisterRequestDto;
import com.barisd.exception.AuthServiceException;
import com.barisd.exception.ErrorType;
import com.barisd.manager.IUserProfileManager;
import com.barisd.mapper.IAuthMapper;
import com.barisd.rabbitmq.model.SaveAuthModel;
import com.barisd.rabbitmq.producer.CreateUserProducer;
import com.barisd.repository.IAuthRepository;
import com.barisd.repository.entity.Auth;
import com.barisd.utility.JwtTokenManager;
import com.barisd.utility.ServiceManager;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AuthService extends ServiceManager<Auth,Long> {
    private final IAuthRepository repository;
    private final JwtTokenManager jwtTokenManager;
    private final IUserProfileManager iUserProfileManager;
    private final CreateUserProducer createUserProducer;
    public AuthService(IAuthRepository repository, JwtTokenManager jwtTokenManager, IUserProfileManager iUserProfileManager, CreateUserProducer createUserProducer) {
        super(repository);
        this.repository = repository;
        this.jwtTokenManager = jwtTokenManager;
        this.iUserProfileManager = iUserProfileManager;
        this.createUserProducer = createUserProducer;
    }

    public Optional<Auth> findOptionalByEmailAndPassword(String email, String password){
        return repository.findOptionalByEmailAndPassword(email,password);
    }

    public Auth register(RegisterRequestDto dto) {
        if(repository.existsByEmail(dto.getEmail()))
            throw new AuthServiceException(ErrorType.REGISTER_EMAIL_ALREADY_EXISTS);
        Auth auth = IAuthMapper.INSTANCE.registerRequestDtoToAuth(dto);
        save(auth);
        //userProfileManager.save(IAuthMapper.INSTANCE.authToUserProfileSaveRequestDto(auth)); //UserProfileService uygulaması ayaktayken buradaki userProfileManager(OpenFeign) üzerinden kullanıcı kaydetmek için gönderebiliyoruz.

        //Artık RabbitMQ kullanarak UserProfileService ayakta değilken de gönderdiğimiz kullanıcıyı ayağa kalkınca kaydetmesini sağlayacağız.
        createUserProducer.convertAndSend(SaveAuthModel.builder()
                .authid(auth.getId())
                .email(auth.getEmail())
                .username(auth.getUsername())
                .build()); //CreateUserProducer üzerinden convertAndSend metodunu çağırıyoruz ve içerisine SaveAuthModel istediği için builder ile kaydedilecek olan auth içinden get metotlarıyla SaveAuthModel sınfına yazdığımız değişkenleri çekiyoruz.
        //Artık register olunduğunda direct-exchange-auth isminde bir exchange oluşacak ve mesaj olarakta SaveAuthModel'in serileştirilmiş hali base64 ile encode edilmiş halini gönderecek.

        return auth;
    }

    public String login(DoLoginRequestDto dto) { //Email ve şifre ile login olunacak ve başarılı olunca kullanıcıya özel bir kimlik vereceğiz.(TOKEN)
        Optional<Auth> auth = repository.findOptionalByEmailAndPassword(dto.getEmail(),dto.getPassword());
        if(auth.isEmpty()) throw new AuthServiceException(ErrorType.DOLOGIN_EMAILORPASSWORD_NOT_EXISTS);
        return jwtTokenManager.createToken(auth.get().getId()).get(); //Kullanıcı başarılı bir şekilde login olduysa DB tarafında verilen Id ile birlikte geriye String Token dönüyoruz.
    }

    public List<Auth> findAll(String token) {
        Optional<Long> idFromToken;
        try {
            idFromToken = jwtTokenManager.decodeToken(token);
        } catch (Exception e) {
            throw new AuthServiceException(ErrorType.INVALID_TOKEN_FORMAT);
        }
        if (!repository.existsById(idFromToken.get())) //Token içinde Id var mı yok mu onun sorgulamasını yapıyoruz.
            throw new AuthServiceException(ErrorType.INVALID_TOKEN);
        return findAll();
    }

}