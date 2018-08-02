package com.example.arsyadani.test;

import java.util.ArrayList;

/**
 * Created by Arsyadani on 12-Apr-18.
 */

public class Tempat {
    private String nama, tipe, alamat, telp, foto, key, lati, longi, order, kapasitas, prosedurSewa, waktuRamai, term;
    private ArrayList<String> fasilitas, jam;
    private ArrayList<MultipleImage> fotoPanorama;

    public Tempat() {

    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getTipe() {
        return tipe;
    }

    public void setTipe(String tipe) {
        this.tipe = tipe;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getTelp() {
        return telp;
    }

    public void setTelp(String telp) {
        this.telp = telp;
    }

    public ArrayList<String> getJam() {
        return jam;
    }

    public void setJam(ArrayList<String> jam) {
        this.jam = jam;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getLati() {
        return lati;
    }

    public void setLati(String lati) {
        this.lati = lati;
    }

    public String getLongi() {
        return longi;
    }

    public void setLongi(String longi) {
        this.longi = longi;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getKapasitas() {
        return kapasitas;
    }

    public void setKapasitas(String kapasitas) {
        this.kapasitas = kapasitas;
    }

    public String getProsedurSewa() {
        return prosedurSewa;
    }

    public void setProsedurSewa(String prosedurSewa) {
        this.prosedurSewa = prosedurSewa;
    }

    public String getWaktuRamai() {
        return waktuRamai;
    }

    public void setWaktuRamai(String waktuRamai) {
        this.waktuRamai = waktuRamai;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public ArrayList<String> getFasilitas() {
        return fasilitas;
    }

    public void setFasilitas(ArrayList<String> fasilitas) {
        this.fasilitas = fasilitas;
    }

    public ArrayList<MultipleImage> getFotoPanorama() {
        return fotoPanorama;
    }

    public void setFotoPanorama(ArrayList<MultipleImage> fotoPanorama) {
        this.fotoPanorama = fotoPanorama;
    }
}
