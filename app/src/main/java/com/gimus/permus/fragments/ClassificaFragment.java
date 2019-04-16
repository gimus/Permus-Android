package com.gimus.permus.fragments;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.gimus.permus.R;
import com.gimus.permus.api.model.ListaUtentiLega;
import com.gimus.permus.api.model.UtenteLega;

import java.util.ArrayList;

public class ClassificaFragment extends Fragment {

    ListView lv;

    public static ClassificaFragment newInstance() {
        ClassificaFragment fragment = new ClassificaFragment();
        Bundle args = new Bundle();
 //        args.putString(ARG_COUNTRY_CODE, countryCode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_classifica, viewGroup, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        this.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        lv = (ListView) view.findViewById(R.id.lvUtenti);
//        updateListView();
    }

    public void updateListView(ListaUtentiLega l) {
        lv.setAdapter(new ClassificaFragment.ListaUtentiLegaArrayAdapter(getActivity(), l) );
    }

    // ---------------------------------------------------------------------------------------------
    //
    //
    // ---------------------------------------------------------------------------------------------

    public static class ListaUtentiLegaArrayAdapter extends ArrayAdapter<UtenteLega> {
        ArrayList<UtenteLega> l;
        Context C;

        public ListaUtentiLegaArrayAdapter(Context _C, ListaUtentiLega l)
        {
            super(_C, R.layout.listitem_utente_lega);
            C=_C;
            this.l=l;
        }

        @Override
        public int getCount() {
            return l.size();
        }

        @Override
        public UtenteLega getItem(int position) {
            return l.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            UtenteLega c = getItem(position);

            convertView = LayoutInflater.from(C).inflate(R.layout.listitem_utente_lega, null);

            TextView tName = (TextView) convertView.findViewById(R.id.lvUserName);
            TextView tInfo = (TextView) convertView.findViewById(R.id.lvFantaPoints);
            ImageView Icon = (ImageView) convertView.findViewById(R.id.ivUserImage);

            tName.setText(c.userName);
            tInfo.setText(String.valueOf(c.fantaPoints));
            Icon.setImageBitmap(c.profileImage);

            return convertView;
        }

    }
}