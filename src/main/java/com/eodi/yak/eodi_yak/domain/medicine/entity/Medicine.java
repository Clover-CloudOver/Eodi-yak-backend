package com.eodi.yak.eodi_yak.domain.medicine.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Table(name="medicine")
public class Medicine {
        @EmbeddedId
        private MedicineId id; // 복합 키

        @ManyToOne
        @MapsId("paCode")
        @JoinColumn(name = "pa_code")
        private Pharmacy pharmacy;
        // TODO: id에 이미 pa code 존재 -> pharmacy 삭제 가능

        @Column(name = "stock")
        private Integer stock;

        @Column(name = "updated_at")
        private LocalDateTime updatedAt;

        @PreUpdate
        public void preUpdate() {
                this.updatedAt = LocalDateTime.now();
        }

        @Builder
        public Medicine(String meName, Pharmacy pharmacy, Integer stock){
                this.id = new MedicineId(meName, pharmacy.getPaCode());
                this.pharmacy = pharmacy;
                this.stock = stock;
                this.updatedAt = LocalDateTime.now();
        }
}