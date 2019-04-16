package com.gimus.permus.api.common;

import java.io.InputStream;

public class ApiCommand {
   public String url;
   public ApiObject data;
   public String type;
   public String command;
   public ApiParameterList parameters;
   public long duration;
   public String error;
   public byte stato;
   public ApiCommand() {
      init("","",null);
    }
   public String result;
   public iAsyncDataReceiver receiver;
   public InputStream inputStream;

    public ApiCommand(String _type, String _url, ApiObject _data) {
        init(_type, _url, _data);
    }

    public ApiCommand(String _type, String _url, iAsyncDataReceiver _receiver) {
        init(_type, _url, null);
        this.receiver=_receiver;
    }

    public ApiCommand(String _type, String _url ) {
        init(_type, _url, null);
    }

    public void init(String _type, String _url, ApiObject _data )
    {
        stato=0;
        parameters=new ApiParameterList();
        if (_url !="" && _type !="" ) {

            this.url = _url;
            this.type = _type;
            this.data = _data;
            String[] s=url.split("\\?");
            String[] s0=s[0].split("/");

            this.command = s[0].split("/")[s0.length - 1];

            this.parameters = new ApiParameterList();
            String[] sx = s[1].split("&");

            for (String x : sx) {
                this.parameters.Add(x);
            }
        }
    }




}
