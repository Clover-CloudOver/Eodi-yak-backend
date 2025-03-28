package com.eodi.yak.eodi_yak.domain.reservation.entity;

import com.eodi.yak.eodi_yak.domain.medicine.entity.Medicine;
import com.eodi.yak.eodi_yak.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="reservation")
public class Reservation {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "reservation_id")
        private Long reservationId;

        @ManyToOne
        @JoinColumn(name = "member_id", referencedColumnName = "member_id", nullable = false)
        private Member member;  // 유저 FK

        @ManyToOne
        @JoinColumns({
                @JoinColumn(name = "me_name", referencedColumnName = "me_name", nullable = false),
                @JoinColumn(name = "pa_code", referencedColumnName = "pa_code", nullable = false)
        })
        private Medicine medicine;  // 약 재고 FK

        @Column(name = "reservation_at", nullable = true)
        private LocalDateTime reservationAt;

        @Column(name = "pickup_deadline", nullable = true)
        private LocalDateTime pickupDeadline;

        @Column(name = "status", nullable = true)
        private String status;

        @Column(name = "quantity", nullable = true)
        private Integer quantity;

        @Builder
        public Reservation(Member member, Medicine medicine, LocalDateTime reservationAt, String status, Integer quantity, LocalDateTime pickupDeadline){
                this.member = member;
                this.medicine = medicine;
                this.reservationAt = reservationAt;
                this.status = status;
                this.quantity = quantity;
                this.pickupDeadline = pickupDeadline;
        }

        //pickupDeadline
}
