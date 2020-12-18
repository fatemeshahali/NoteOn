package com.shahali.mnote.model;

public class Note {
    private String title;
    private String note;

    public Long getCreation_date() {
        return creation_date;
    }

    public void setCreation_date(Long creation_date) {
        this.creation_date = creation_date;
    }

    private Long creation_date;
    private String publisher ;

    public Note(Class<Note> noteClass) {
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public Note(){}

    public Note(String title, String note, Long creationDate , String publisher) {
        this.title = title;
        this.note = note;
        this.creation_date = creationDate;
        this.publisher = publisher;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }


}
