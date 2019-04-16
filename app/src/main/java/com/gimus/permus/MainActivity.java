package com.gimus.permus;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.gimus.permus.api.client.Client;
import com.gimus.permus.api.common.ApiCommand;
import com.gimus.permus.api.common.ApiObject;
import com.gimus.permus.api.common.iAsyncDataReceiver;
import com.gimus.permus.api.model.Lega;
import com.gimus.permus.api.model.Subject;
import com.gimus.permus.api.model.SystemInfo;
import com.gimus.permus.fragments.ClassificaFragment;
import com.gimus.permus.utility.Utility;

import static com.gimus.permus.A.client;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, iAsyncDataReceiver {

    public static final int FR_CLASSIFICA = 201;
    public static final int FR_GIOCATE = 202;

    protected int frammentoSelezionato=0;

    protected ClassificaFragment fr_classifica=null;
    protected TextView tvCoins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        A.a.currentActivity=this;
        A.a.mainActivity=this;


        // hack per inserire un codice di accesso utente bypassando il componente di letture qrcode
        A.a.savePreferenceString("PASSWORD","1c60a5f2-bd97-4d40-bb14-73507a9d8b34");

        A.client =new Client(A.getResourceString(R.string.apiServer), A.a.getPreferenceString("PASSWORD"));

        tvCoins=  (TextView) findViewById(R.id.tvCoins);
        client.checkIn(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.a_lega_classifica:
                selezionaFrammento(FR_CLASSIFICA);
                break;
            case R.id.a_lega_giocate:
                selezionaFrammento(FR_GIOCATE);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.nav_connetti:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    protected  void connetti(){
        QRCScannerActivity.getQRCode(this);
    }

    protected  void selezionaFrammento( int frammentoId) {
        if (frammentoSelezionato != frammentoId) {

            Fragment f=null;

            switch (frammentoId){
                case FR_CLASSIFICA:
                    if (fr_classifica==null)
                        fr_classifica = ClassificaFragment.newInstance();
                    f=fr_classifica;
                    break;
                case FR_GIOCATE:
                    break;
            }

            if (f != null) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_placeholder, f);
                ft.commit();
                frammentoSelezionato=frammentoId;
            }
        }

    }

    public  void selezionaLega(Lega l) {
        A.lega=l;
        getSupportActionBar().setTitle(A.lega.lega);
     //   A.client.getClassificaLega(A.lega.legaId, this);
    }

    @Override
    public void onDataReceived(ApiCommand ac, String JSON, ApiObject o) {
        switch ( ac.command) {

            case "device_check_in":
                if (o != null) {
                    Subject s=(Subject) o;
                    A.setSubject(s);

//                    selezionaLega(u.leghe.get(1));

                    TextView tv= (TextView) findViewById(R.id.tv_userName);
                    tv.setText(A.subject.name);

                    tv= (TextView) findViewById(R.id.tv_email);
                    tv.setText(A.subject.email);

                //    client.getImmagineProfiloUtente(A.utente.utenteId, this);
                    selezionaFrammento( FR_CLASSIFICA);


                }
                else
                    connetti();
                break;

            case "device_info":
                 A.systemInfo=(SystemInfo) o;
                updateInterface();
                break;
            case "ip":
                ImageView iv=(ImageView) findViewById(R.id.imageView);
                Bitmap bm = Utility.clipInCircle( Bitmap.createScaledBitmap((Bitmap) o.tag, 512, 512, false) ,0);
                 iv.setImageBitmap(bm);
                break;
        }
    }

    @Override
    public void onDataError(ApiCommand ac, String error) {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode== Activity.RESULT_OK) {
            switch (requestCode) {
                case QRCScannerActivity.SCAN_QRCODE:
                    String secretCode = data.getStringExtra("QRCode");
                    if (secretCode !=""){
                        A.a.savePreferenceString("PASSWORD",secretCode);
                        newSecretCode(secretCode);
                    }
            }
        }
    }

    protected void newSecretCode(String sc){
        client =new Client(A.getResourceString(R.string.apiServer), sc);
        client.checkIn(this);
    }
    protected void onResume() {
        super.onResume();
        A.a.currentActivity=this;
    }

    public void tick(){
        if (A.subject != null)
            A.client.getInfo(this);
    }

    public void updateInterface(){
       tvCoins.setText( String.valueOf(A.getUserBalance()));

    }

}
