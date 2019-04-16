package com.gimus.permus.api.model;

import android.graphics.Bitmap;

import com.gimus.permus.api.common.ApiObject;

public class UtenteLega extends ApiObject {
    public int utenteId;
    public String userName;
    public String email;
    public String legaId;
    public Integer stagione;
    public double sommaPunteggi;
    public int punti;
    public int bonus;
    public int fantaPoints;
    public boolean isEnabled;
    public boolean isAdmin;
    public Bitmap profileImage;
}
