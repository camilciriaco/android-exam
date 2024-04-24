package com.cam.exam;

import java.io.Serializable;

public class UserData implements Serializable {
    private String firstName;
    private String lastName;
    private String imageUrl;
    private String birthDate;
    private String age;
    private String email;
    private String mobile;
    private String address;
    private String contactPerson;
    private String contactPersonMobile;


    public UserData(
            String firstName,
            String lastName,
            String imageUrl,
            String birthDate,
            String age,
            String email,
            String mobile,
            String address,
            String contactPerson,
            String contactPersonMobile
    ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.imageUrl = imageUrl;
        this.birthDate = birthDate;
        this.age = age;
        this.email = email;
        this.mobile = mobile;
        this.address = address;
        this.contactPerson = contactPerson;
        this.contactPersonMobile = contactPersonMobile;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public String getAge() {
        return age;
    }

    public String getMobile() {
        return mobile;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public String getContactPersonMobile() {
        return contactPersonMobile;
    }




}
