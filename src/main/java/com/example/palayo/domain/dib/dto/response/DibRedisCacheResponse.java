package com.example.palayo.domain.dib.dto.response;

import lombok.*;

import java.io.Serializable;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class DibRedisCacheResponse implements Serializable {
    private Long dibId;
    private Long userId;
    private Long auctionId;
}