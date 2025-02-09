package com.eodi.yak.eodi_yak.domain.medicine.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class MedicineId implements Serializable {

    @Column(name = "me_name")
    private String meName;

    @Column(name = "pa_code")
    private String paCode;

    public MedicineId() {}

    public MedicineId(String meName, String paCode) {
        this.meName = meName;
        this.paCode = paCode;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MedicineId that = (MedicineId) o;
        return meName.equals(that.meName) && paCode.equals(that.paCode);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(meName, paCode);
    }

}
