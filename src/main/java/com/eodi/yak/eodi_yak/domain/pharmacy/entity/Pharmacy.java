package com.eodi.yak.eodi_yak.domain.pharmacy.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalTime;

@Entity
@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="pharmacy")
public class Pharmacy {
        @Id
        @Column(name = "pa_code")
        private String paCode;

        @Column(name = "pa_name")
        private String paName;

        @Column(name = "latitude")
        private BigDecimal latitude;

        @Column(name = "longitude")
        private BigDecimal longitude;

        @Column(name = "phone_number")
        private String phoneNumber;

        @Column(name = "email")
        private String email;

        @Column(name = "open_time")
        private LocalTime openTime;

        @Column(name = "close_time")
        private LocalTime closeTime;

        @Builder
        public Pharmacy(String paCode, String paName, BigDecimal latitude,
                        BigDecimal longitude, String phoneNumber,
                        String email, LocalTime openTime, LocalTime closeTime){
                this.paCode = paCode;
                this.paName = paName;
                this.latitude = latitude;
                this.longitude = longitude;
                this.phoneNumber = phoneNumber;
                this.email = email;
                this.openTime = openTime;
                this.closeTime = closeTime;
        }
}
