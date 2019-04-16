package com.gimus.permus.api.model;
import com.gimus.permus.api.common.J;
import com.gimus.permus.utility.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Factory {

    public static UtenteLega creaUtenteLega(JSONObject j) {
        UtenteLega o = new UtenteLega();
        o.utenteId =J.getInt(j,"utenteId");
        o.userName=J.getString(j, "userName");
        o.email=J.getString(j,"email");
        o.legaId=J.getString(j,"legaId");
        o.isAdmin=J.getBoolean(j, "isAdmin");
        o.stagione=J.getInt(j,"stagione");
        o.sommaPunteggi=J.getDouble(j,"sommaPunteggi");
        o.bonus=J.getInt(j,"bonus");
        o.punti=J.getInt(j,"punti");
        o.fantaPoints=J.getInt(j,"fantaPoints");
        String img=J.getString(j,"profileImage");
        if (img !="") {
            o.profileImage=Utility.getBitmapFromBase64String(img);
        }
        return o;
    }


    public static ListaUtentiLega creaListaUtentiLega(JSONArray a) {
        ListaUtentiLega l=new ListaUtentiLega();
        if (a != null) {
            for (int i=0; i< a.length();i++){
                try {
                    UtenteLega o= Factory.creaUtenteLega( a.getJSONObject(i));
                    l.add(o);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return l;
    }

    public static Lega creaLega(JSONObject j) {
        Lega o = new Lega();
        o.legaId=J.getInt(j,"legaId");
        o.lega=J.getString(j, "lega");
        o.isUtenteLegaAdmin=J.getBoolean(j, "isUtenteLegaAdmin");
        return o;
    }

    public static ListaLeghe creaListaLeghe(JSONArray a) {
        ListaLeghe l=new ListaLeghe();
        if (a != null) {
            for (int i=0; i< a.length();i++){
                try {
                    Lega o= Factory.creaLega( a.getJSONObject(i));
                    l.add(o);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return l;
    }

    public static Subject creaSubject(JSONObject j) {
        Subject o = new Subject();
        o.id =J.getString(j,"id");
        o.name=J.getString(j, "name");
        o.email=J.getString(j,"email");
        return o;
    }

    public static SystemInfo creaSystemInfo(JSONObject j) {
        SystemInfo o = new SystemInfo();
        o.blockMasterReady=J.getBoolean(j,"blockMasterReady");
        o.blockChainVersion=J.getString(j,"blockChainVersion");
        o.currentBlockSerial= Long.parseLong( J.getString(j,"currentBlockSerial"));
        o.currentTransactionSerial=J.getInt(j,"currentTransactionSerial");
        o.maxTransactionsPerBlock=J.getInt(j,"maxTransactionsPerBlock");
        o.certificationAuthorities=J.getString(j,"certificationAuthorities");
        o.currentTimeStamp= Long.parseLong( J.getString(j,"currentTimeStamp"));
        o.requesterInfo=  creaUserInfo(J.deserialize(J.getString(j,"requesterInfo")));
        String s= J.getString(j,"otherUserInfo");

        if (s != "null")
            o.otherUserInfo=  creaUserInfo(J.deserialize(s));
        else
            o.otherUserInfo=null;

        return o;
    }

    public static UserInfo creaUserInfo(JSONObject j) {
        UserInfo o = new UserInfo();
        o.userId=J.getString(j,"userId");
        o.isOnline=J.getBoolean(j,"isOnline");
        o.userOutgoingPendingTransfersCount=J.getInt(j,"userOutgoingPendingTransfersCount");
        o.userIncomingPendingTransfersCount=J.getInt(j,"userIncomingPendingTransfersCount");
        o.token=J.getString(j,"token");
        o.coinBalance = J.getDouble(j,"coinBalance");
        return o;
    }

}
