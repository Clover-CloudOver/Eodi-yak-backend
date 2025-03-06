package com.eodi.yak.eodi_yak.domain.reservation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class MedicineId implements Serializable {
    @Column(name = "me_name")
    private String meName;

    @Column(name = "pa_code")
    private String paCode;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        com.eodi.yak.eodi_yak.domain.reservation.entity.MedicineId that = (com.eodi.yak.eodi_yak.domain.reservation.entity.MedicineId) o;
        return meName.equals(that.meName) && paCode.equals(that.paCode);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(meName, paCode);
    }

}
