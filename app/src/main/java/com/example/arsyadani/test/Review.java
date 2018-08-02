package com.example.arsyadani.test;

/**
 * Created by Arsyadani on 20-Apr-18.
 */

public class Review {
    private String idPengguna;
    private String idLokasi;
    private String review;
    private String waktu;
    private double rating;

    public Review() {
    }

    public Review(String idPengguna, String idLokasi, String review, String waktu, int rating) {
        this.idPengguna = idPengguna;
        this.idLokasi = idLokasi;
        this.review = review;
        this.waktu = waktu;
        this.rating = rating;
    }

    public String getIdPengguna() {
        return idPengguna;
    }

    public void setIdPengguna(String idPengguna) {
        this.idPengguna = idPengguna;
    }

    public String getIdLokasi() {
        return idLokasi;
    }

    public void setIdLokasi(String idLokasi) {
        this.idLokasi = idLokasi;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getWaktu() {
        return waktu;
    }

    public void setWaktu(String waktu) {
        this.waktu = waktu;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
