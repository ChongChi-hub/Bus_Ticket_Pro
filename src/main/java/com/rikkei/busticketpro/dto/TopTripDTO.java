package com.rikkei.busticketpro.dto;

import com.rikkei.busticketpro.entity.Trip;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TopTripDTO {
    private Trip trip;
    private Long ticketCount;
}
