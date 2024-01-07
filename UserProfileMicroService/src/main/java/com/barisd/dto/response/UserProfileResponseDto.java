package com.barisd.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserProfileResponseDto {
    String id;
    String username;
    String email;
    String photo;
    String phone;
    String website;
}
