package com.barisd.rabbitmq.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
/*
!!!!!!!!!!!!!!!!!!!!!!!! Önemli !!!!!!!!!!!!!!!!!!!!!!!!!
Mutlaka tüm modeller serileştirilmelidir.
Ayrıca paket ismi dahil bu sınıfın aynısı iletilen servite de olmalıdır.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class SaveAuthModel implements Serializable {
    Long authid;
    String username;
    String email;
}