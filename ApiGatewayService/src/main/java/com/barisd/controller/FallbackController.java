package com.barisd.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fallback")
public class FallbackController {
    /*
    Hata durumunda düşülen endpoint burası bur durumda yapılması işlemler burada yapılabilir. loglama gibi
     */
    @GetMapping("/auth")
    public ResponseEntity<String>fallbackAuth(){
        return ResponseEntity.ok("Auth Servisi şu an yanıt vermemektedir. Lütfen daha sonra tekrar deneyiniz.");

    }
    @GetMapping("/user")
    public ResponseEntity<String>fallbackUser(){
        return ResponseEntity.ok("UserProfile Servisi şu an yanıt vermemektedir. Lütfen daha sonra tekrar deneyiniz.");

    }




}
