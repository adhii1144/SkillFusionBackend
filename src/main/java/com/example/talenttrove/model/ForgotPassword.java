package com.example.talenttrove.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ForgotPassword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer fid;

    @Column(nullable = false)
    private Integer otp;

    @Column(nullable = false)
    private Date otpexpireyTime;

    @OneToOne
    private Users user;

    // Constructor with parameters
    public ForgotPassword(Integer otp, Date otpexpireyTime, Users user) {
        this.otp = otp;
        this.otpexpireyTime = otpexpireyTime;
        this.user = user;
    }

    // Getter method for fid
    public Integer getFid() {
        return fid;
    }

    // Setter method for fid
    public void setFid(Integer fid) {
        this.fid = fid;
    }

    // Getter method for otpexpireyTime
    public Date getOtpexpireyTime() {
        return otpexpireyTime;
    }

    // Setter method for otpexpireyTime
    public void setOtpexpireyTime(Date otpexpireyTime) {
        this.otpexpireyTime = otpexpireyTime;
    }
}