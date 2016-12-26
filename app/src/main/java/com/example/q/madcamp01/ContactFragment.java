package com.example.q.madcamp01;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by q on 2016-12-26.
 */

public class ContactFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    ListView lv;
    ListViewAdapter mAdapter;

    private static final String ARG_SECTION_NUMBER = "section_number";

    public ContactFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ContactFragment newInstance(int sectionNumber) {
        ContactFragment fragment = new ContactFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        lv = (ListView) rootView.findViewById(R.id.listView);


        //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));//


        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        mAdapter = new ListViewAdapter(getActivity());
        lv.setAdapter(mAdapter);


        /*for(int a=0; a<mListData.length;a++)
        {
            mAdapter.addItem(mListData.getString(a));
        }


        mAdapter.addItem("김재성", "010-2908-8041");
        Log.d("onactivity created", "hey");*/


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getActivity().checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 0);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            GetUserContactsList();
        }





        lv.setOnItemClickListener
            (
                    new AdapterView.OnItemClickListener()
                    {
                        public void onItemClick(AdapterView parent, View view, int position, long id)
                        {
                            String str = mAdapter.mListData.get(position).name;
                            String a = str + " 선택";
                            Toast.makeText(getActivity(), a, Toast.LENGTH_SHORT).show();
                        }
                    }
            );
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                GetUserContactsList();
            } else {
                Toast.makeText(getActivity(), "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private class ViewHolder
    {
        public TextView name;

        public TextView number;
    }
    private class ListViewAdapter extends BaseAdapter {
        private Context mContext = null;
        private ArrayList<ListData> mListData = new ArrayList<ListData>();

        public ListViewAdapter(Context mContext) {
            super();
            this.mContext = mContext;
        }

        public void addItem(String name, String number) {
            ListData addInfo = null;
            addInfo = new ListData();
            addInfo.name = name;
            addInfo.number = number;

            mListData.add(addInfo);
        }

        @Override
        public int getCount() {
            return mListData.size();
        }

        @Override
        public Object getItem(int position) {
            return mListData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();

                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.format, null);

                holder.name = (TextView) convertView.findViewById(R.id.textView1);
                holder.number = (TextView) convertView.findViewById(R.id.textView2);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            ListData mData = mListData.get(position);


            holder.name.setText(mData.name);
            holder.number.setText(mData.number);

            return convertView;
        }
    }


    public void GetUserContactsList() {

        String[] arrProjection = {
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
        };
        String[] arrPhoneProjection = {
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };
        String[] arrEmailProjection = {
                ContactsContract.CommonDataKinds.Email.DATA
        };
        // get user list
        Cursor clsCursor = getActivity().getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI, arrProjection,
                ContactsContract.Contacts.HAS_PHONE_NUMBER + "=1",
                null, null
        );

        while (clsCursor.moveToNext()) {
            String strContactId = clsCursor.getString(0);
            Log.d("Unity", "이름 : " + clsCursor.getString(1));



            // phone number
            Cursor clsPhoneCursor = getActivity().getContentResolver().query (
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                arrPhoneProjection,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + strContactId,
                null, null
            );
            
            while( clsPhoneCursor.moveToNext() )
            {
            Log.d("Unity", "폰번호 : " + clsPhoneCursor.getString( 0 ));
                mAdapter.addItem(clsCursor.getString(1), clsPhoneCursor.getString(0));

            }
            clsPhoneCursor.close();

        }
        clsCursor.close();
    }
}

