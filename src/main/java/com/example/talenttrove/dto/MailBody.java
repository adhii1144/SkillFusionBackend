package com.example.talenttrove.dto;

public class MailBody {
    private String to;
    private String subject;
    private String body;

    public MailBody(String to, String subject, String body) {
        this.to = to;
        this.subject = subject;
        this.body = body;
    }

    // Getters and setters
    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}



//package com.example.talenttrove.dto;
//
//public class MailBody {
//    private String to;
//    private String subject;
//    private String body;
//
//    public MailBody(String to, String subject, String body) {
//        this.to = to;
//        this.subject = subject;
//        this.body = body;
//    }
//
//    public String getTo() { return to; }
//    public void setTo(String to) { this.to = to; }
//    public String getSubject() { return subject; }
//    public void setSubject(String subject) { this.subject = subject; }
//    public String getBody() { return body; }
//    public void setBody(String body) { this.body = body; }
//}
