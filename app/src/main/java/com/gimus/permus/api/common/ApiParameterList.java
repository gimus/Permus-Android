package com.gimus.permus.api.common;

import java.util.ArrayList;

/**
 * Created by Pino.Gimondo on 30/11/2016.
 */

public class ApiParameterList extends ArrayList<ApiParameter> {

    public ApiParameter Add(String s) {
        ApiParameter ap = null;

        if (s != "") {
            String[] as = s.split("=");

            if (as.length == 2)
                ap = new ApiParameter(as[0], as[1]);
            else
                ap = new ApiParameter(as[0], "");

            this.add(ap);

        }
        return ap;
    }
}
