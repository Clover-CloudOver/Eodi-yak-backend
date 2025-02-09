package com.eodi.yak.eodi_yak.domain.medicine.entity;

import com.eodi.yak.eodi_yak.domain.pharmacy.entity.Pharmacy;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="medicine")
public class Medicine {
        @EmbeddedId
        private MedicineId id; // 복합 키

        @ManyToOne
        @MapsId("paCode")
        @JoinColumn(name = "pa_code")
        private Pharmacy pharmacy;

        @Column(name = "stock")
        private Integer stock;

        @Column(name = "updated_at")
        private LocalDateTime updatedAt;

        @Builder
        public Medicine(String meName, Integer stock){
                this.id = new MedicineId(meName, pharmacy.getPaCode());
                this.stock = stock;
        }
}