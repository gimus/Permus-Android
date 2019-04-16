package com.gimus.permus.api.client;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.gimus.permus.api.common.ApiClient;
import com.gimus.permus.api.common.ApiCommand;
import com.gimus.permus.api.common.ApiObject;
import com.gimus.permus.api.common.ApiTask;
import com.gimus.permus.api.common.J;
import com.gimus.permus.api.common.iAsyncDataReceiver;
import com.gimus.permus.api.model.Classifica;
import com.gimus.permus.api.model.Factory;
import com.gimus.permus.crypto.AES128;

public class Client extends ApiClient {

    protected String server;
    protected String password;
    protected String password_hash;
    protected int counter;

    public Client( String serverURL, String _password) {
        server=serverURL;
        password=_password;
        password_hash=AES128.SHA128(this.password);
        resetCounter();
    }

    @Override
    public void onTaskExecuted(ApiCommand ac){
        if(ac.result== null ) {
            ac.stato=3;
            ac.receiver.onDataError(ac, ac.error);
        }
        else
        {
            ac.stato=2;

            try {
                switch ( ac.command) {
                    case "lista_utenti_lega":
                        Classifica c= new Classifica();
                        c.utenti=Factory.creaListaUtentiLega( J.deserializeToArray(ac.result));
                        ac.data= c;
                        break;
                    case "device_check_in":
                        ac.data= Factory.creaSubject(J.deserialize(ac.result));
                        break;
                    case "device_info":
                        ac.data= Factory.creaSystemInfo(J.deserialize(ac.result));
                        break;
                    case "ip":
                        ApiObject o=new ApiObject();
                        byte[] b= Base64.decode(ac.result, Base64.DEFAULT);
                        Bitmap myBitmap = BitmapFactory.decodeByteArray(b,0,b.length) ;
                        o.tag=myBitmap;
                        ac.data=o;
                        break;
                }
            }
            catch (Exception e) {
                ac.error = e.getMessage();
                ac.data=null;
            }
            ac.receiver.onDataReceived(ac,ac.result,ac.data);
        }
    }

    public void checkIn(iAsyncDataReceiver adr) {
        resetCounter();

        ApiCommand ac=new ApiCommand("GET", server + "api/device_check_in?token=" + generateToken() + "&device=" + android.os.Build.MANUFACTURER.toUpperCase() + " " + android.os.Build.MODEL , adr);
        new ApiTask().executeCommand(this,ac);
    }

    public void getInfo(iAsyncDataReceiver adr) {
         ApiCommand ac=new ApiCommand("GET", server + "api/device_info?token=" + generateToken(), adr);
        new ApiTask().executeCommand(this,ac);
    }

/*
    public void getImmagineProfiloUtente(Integer utenteId, iAsyncDataReceiver adr) {
        ApiCommand ac=new ApiCommand("GET", server +"f/ip?b64=1&utenteId=" + String.valueOf(utenteId) , adr);
        new ApiTask().executeCommand(this,ac);
    }
*/

    protected  String generateToken(){
        counter++;
        return (password_hash + "|" + AES128.enc( String.valueOf(counter),this.password)).replace("+","*");
    }

    protected  void resetCounter(){
        this.counter=0;
    }

}
