package tests;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class CreateBookingBody {
    private String firstname;
    private String lastname;
    private int totalprice;
    private Boolean depositpaid;
    private BookingDateBody bookingdates;
    private String additionalneeds;
}
