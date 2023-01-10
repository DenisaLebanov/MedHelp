package com.example.medhelp;

public class PPersonalData {

        private String firstName;
        private String secondName;
        private String cnp;
        private String age;
        private String phone;
        private String details;
        private String gender;

        public PPersonalData(){ }

        public PPersonalData(String firstName, String secondName, String cnp, String age, String phone, String details, String gender){
            this.firstName = firstName;
            this.secondName = secondName;
            this.cnp = cnp;
            this.age = age;
            this.phone = phone;
            this.details = details;
            this.gender = gender;
        }

        public String getFirstName(){
            return this.firstName;
        }

        public String getSecondName(){
            return this.secondName;
        }

        public String getCNP(){
            return this.cnp;
        }

        public String getAge(){
            return this.age;
        }

        public String getPhone(){ return this.phone; }

        public String getDetails(){ return this.details; }

        public String getGender(){ return this.gender; }

        public void setFirstName(String firstName){
            this.firstName = firstName;
        }

        public void setSecondName(String secondName){ this.secondName = secondName; }

        public void setCNP(String cnp){
            this.cnp = cnp;
        }

        public void setAge(String age){ this.age = age; }

        public void setPhone(String phone){ this.phone = phone; }

        public void setDetails(String details) { this.details = details; }

        public void setGender(String gender) { this.gender = gender; }
}

